package com.company.news.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.BaseDianzanJsonform;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.BaseDianzanService;
import com.company.news.vo.ResponseMessage;

/**
 * 基本回复模块.
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/baseDianzan")
public class BaseDianzanController extends AbstractRESTController {
	@Autowired
	private BaseDianzanService baseDianzanService;

	/**
	 * 组织注册
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		BaseDianzanJsonform baseReplyJsonform;
		try {
			
			
			baseReplyJsonform = (BaseDianzanJsonform) this.bodyJsonToFormObject(
					bodyJson, BaseDianzanJsonform.class);
			
			baseReplyJsonform.setUuid(null);
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		//转换特定格式.
			boolean flag = baseDianzanService.add(user,baseReplyJsonform, responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("操作成功");
		return "";
	}



	/**
	 * 删除
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);

		try {
			String rel_uuid=request.getParameter("rel_uuid");
			if(DBUtil.isSqlInjection(rel_uuid, responseMessage))return "";
			
			String type=request.getParameter("type");
			if(DBUtil.isSqlInjection(type, responseMessage))return "";
			
			SessionUserInfoInterface parent=this.getUserInfoBySession(request);
			
			boolean flag = baseDianzanService.delete(parent,rel_uuid,Integer.valueOf(type)
					,responseMessage);
			if (!flag)
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("删除成功");
		return "";
	}
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public String query(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			String rel_uuid = request.getParameter("rel_uuid");
			String type = request.getParameter("type");

			if(DBUtil.isSqlInjection(rel_uuid,responseMessage))return "";
			if(DBUtil.isSqlInjection(type,responseMessage))return "";
			
			
			
			if (StringUtils.isBlank(type)) {
				responseMessage.setMessage("type不能为空！");
				return "";
			}

			if (StringUtils.isBlank(rel_uuid)) {
				responseMessage.setMessage("rel_uuid不能为空！");
				return "";
			}
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String user_uuid=null;
			if(user!=null){
				user_uuid=user.getUuid();
			}
			Map vo=this.baseDianzanService.query(rel_uuid, Integer.valueOf(type), user_uuid);
			
			model.addAttribute(RestConstants.Return_G_entity,vo);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	

}
