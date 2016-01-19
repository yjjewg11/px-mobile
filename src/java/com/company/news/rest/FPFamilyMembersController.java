package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPFamilyMembersJsonform;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.FPFamilyMembersService;
import com.company.news.vo.ResponseMessage;
/**
 * 家庭相册
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/fPFamilyMembers")
public class FPFamilyMembersController extends AbstractRESTController {

	@Autowired
	private FPFamilyMembersService fPFamilyMembersService;


	/**
	 * 修改保存相册基本资料
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
		FPFamilyMembersJsonform favoritesJsonform;
		try {
			favoritesJsonform = (FPFamilyMembersJsonform) this.bodyJsonToFormObject(
					bodyJson, FPFamilyMembersJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}
		
		//设置当前用户
		
		try {
			Object flag;
			    flag = fPFamilyMembersService.add(favoritesJsonform, responseMessage, request);

			if (flag==null)// 请求服务返回失败标示
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
	 * 班级删除
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
			if(DBUtil.isSqlInjection(uuid, responseMessage)){
				return "";
			}
			SessionUserInfoInterface user=this.getUserInfoBySession(request);
			
			boolean flag = fPFamilyMembersService.delete(request,uuid
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
		responseMessage.setMessage("操作成功");
		return "";
	}
	
	

	
	

	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid,ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		Object m;
		try {
			//防止sql注入.
			if(DBUtil.isSqlInjection(uuid,responseMessage))return "";
			
			m = fPFamilyMembersService.get(uuid);
			if(m==null){
				responseMessage.setMessage("查询对象不存在!");
				return "";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,m);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
}
