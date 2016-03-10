package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.UserThirdLoginQQService;
import com.company.news.vo.ResponseMessage;

/**
 * 第三分登录,微信
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/userThirdLoginQQ")
public class UserThirdLoginQQController extends AbstractRESTController {
	@Autowired
	private UserThirdLoginQQService userThirdLoginQQService;


	/**
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/access_token", method = RequestMethod.GET)
	public String access_token(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String appid=request.getParameter("appid");
			if (StringUtils.isBlank(appid)) {
				responseMessage.setMessage("参数:appid不能为空！");
				return "";
			}
			if(DBUtil.isSqlInjection(appid, responseMessage))return "";
			
//			String code=request.getParameter("code");
//			if(DBUtil.isSqlInjection(code, responseMessage))return "";
//			if (StringUtils.isBlank(code)) {
//				responseMessage.setMessage("参数:code不能为空！");
//				return"";
//			}
			String access_token=request.getParameter("access_token");
			if(DBUtil.isSqlInjection(access_token, responseMessage))return "";
			if (StringUtils.isBlank(access_token)) {
				responseMessage.setMessage("参数:access_token不能为空！");
				return"";
			}
			String openid=request.getParameter("openid");
			if(DBUtil.isSqlInjection(openid, responseMessage))return "";
			if (StringUtils.isBlank(openid)) {
				responseMessage.setMessage("参数:openid不能为空！");
				return"";
			}
			boolean flag= userThirdLoginQQService.update_access_token(model, request, responseMessage, appid, access_token, openid);
			if(!flag){
				return "";
			}
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
	
	
	@RequestMapping(value = "/bindTel", method = RequestMethod.GET)
	public String bindTel(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String access_token=request.getParameter("access_token");
			if (StringUtils.isBlank(access_token)) {
				responseMessage.setMessage("参数:access_token不能为空！");
				return "";
			}
			if(DBUtil.isSqlInjection(access_token, responseMessage))return "";
			
			String tel=request.getParameter("tel");
			if(DBUtil.isSqlInjection(tel, responseMessage))return "";
			if (StringUtils.isBlank(tel)) {
				responseMessage.setMessage("参数:电话号码不能为空！");
				return"";
			}
			
			String smsCode=request.getParameter("smsCode");
			if(DBUtil.isSqlInjection(smsCode, responseMessage))return "";
			if (StringUtils.isBlank(smsCode)) {
				responseMessage.setMessage("参数:验证码不能为空！");
				return"";
			}
			
			boolean flag= userThirdLoginQQService.update_bindTel(model, request, responseMessage, access_token, tel, smsCode);
			if(!flag){
				return "";
			}
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

	

}
