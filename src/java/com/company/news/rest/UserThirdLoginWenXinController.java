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
import com.company.news.service.UserThirdLoginWenXinService;
import com.company.news.vo.ResponseMessage;

/**
 * 第三分登录,微信
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/userThirdLoginWenXin")
public class UserThirdLoginWenXinController extends AbstractRESTController {
	@Autowired
	private UserThirdLoginWenXinService userThirdLoginWenXinService;


	/**
	 https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
参数说明
参数	是否必须	说明
appid	是	应用唯一标识，在微信开放平台提交应用审核通过后获得
secret	是	应用密钥AppSecret，在微信开放平台提交应用审核通过后获得
code	是	填写第一步获取的code参数
grant_type	是	填authorization_code



return:
{ 
"access_token":"ACCESS_TOKEN", 
"expires_in":7200, 
"refresh_token":"REFRESH_TOKEN",
"openid":"OPENID", 
"scope":"SCOPE",
"unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"
}
	 * 
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
			
			String code=request.getParameter("code");
			if(DBUtil.isSqlInjection(code, responseMessage))return "";
			if (StringUtils.isBlank(code)) {
				responseMessage.setMessage("参数:code不能为空！");
				return"";
			}

			
			boolean flag= userThirdLoginWenXinService.access_token(model,request,responseMessage,appid, code);
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
			
			boolean flag= userThirdLoginWenXinService.update_bindTel(model, request, responseMessage, access_token, tel, smsCode);
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

	
	/**
	 * 微信票据登录
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/loginByaccess_token", method = RequestMethod.GET)
	public String loginByaccess_token(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String access_token=request.getParameter("access_token");
			if (StringUtils.isBlank(access_token)) {
				responseMessage.setMessage("参数:access_token不能为空！");
				return "";
			}
		
			
			boolean flag= userThirdLoginWenXinService.loginByaccess_token(model, request, responseMessage, access_token);
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

	/**
	 * 注销
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String logout(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String access_token=request.getParameter("access_token");
			if (StringUtils.isBlank(access_token)) {
				responseMessage.setMessage("参数:access_token不能为空！");
				return "";
			}
			boolean flag= userThirdLoginWenXinService.update_logoutByaccess_token(model, request, responseMessage, access_token);
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
