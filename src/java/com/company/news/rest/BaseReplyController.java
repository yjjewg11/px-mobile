package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.commons.util.MyUbbUtils;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.BaseReplyJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.BaseReplyService;
import com.company.news.vo.ResponseMessage;

/**
 * 基本回复模块.
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/baseReply")
public class BaseReplyController extends AbstractRESTController {
	@Autowired
	private BaseReplyService baseReplyService;

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
		BaseReplyJsonform baseReplyJsonform;
		try {
			
			
			baseReplyJsonform = (BaseReplyJsonform) this.bodyJsonToFormObject(
					bodyJson, BaseReplyJsonform.class);
			
			
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		//转换特定格式.
		baseReplyJsonform.setContent(MyUbbUtils.htmlToMyUbb(baseReplyJsonform.getContent()));

			boolean flag=false;
			if(StringUtils.isEmpty(baseReplyJsonform.getUuid()))
			    flag = baseReplyService.add(user,baseReplyJsonform, responseMessage);
			
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
	 * 获取班级信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryByRel_uuid", method = RequestMethod.GET)
	public String queryByRel_uuid(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String rel_uuid=request.getParameter("rel_uuid");
			if (StringUtils.isBlank(rel_uuid)) {
				responseMessage.setMessage("参数:rel_uuid不能为空！");
				return "";
			}
			if(DBUtil.isSqlInjection(rel_uuid, responseMessage))return "";
			
			String type=request.getParameter("type");
			if(DBUtil.isSqlInjection(type, responseMessage))return "";
			if (StringUtils.isBlank(type)) {
				responseMessage.setMessage("参数:type不能为空！");
				return"";
			}
			
			SessionUserInfoInterface parent=this.getUserInfoBySession(request);
			String cur_user_uuid="";
			if(parent!=null)cur_user_uuid=parent.getUuid();
			
			PaginationData pData=this.getPaginationDataByRequest(request);
			PageQueryResult pageQueryResult = baseReplyService.query(rel_uuid,Integer.valueOf(type), pData,cur_user_uuid);
		
			baseReplyService.warpVoList(pageQueryResult.getData(), cur_user_uuid);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
	
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
			String uuid=request.getParameter("uuid");
			if(DBUtil.isSqlInjection(uuid, responseMessage))return "";
			if (StringUtils.isBlank(uuid)) {
				responseMessage.setMessage("参数:uuid不能为空！");
				return "";
			}
			String type=request.getParameter("type");
			if(DBUtil.isSqlInjection(type, responseMessage))return "";
			if (StringUtils.isBlank(type)) {
				responseMessage.setMessage("参数:type不能为空！");
				return"";
			}
			
			SessionUserInfoInterface parent=this.getUserInfoBySession(request);
			
			boolean flag = baseReplyService.delete(parent,uuid,Integer.valueOf(type)
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


	public BaseReplyService getAbstractReplyService() {
		return baseReplyService;
	}


	public void setAbstractReplyService(BaseReplyService baseReplyService) {
		this.baseReplyService = baseReplyService;
	}
	

}
