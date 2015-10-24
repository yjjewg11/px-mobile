package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ClassNewsDianzanJsonform;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.ClassNewsDianzanService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/dianzan")
public class DianzanController extends AbstractRESTController {

	@Autowired
	private ClassNewsDianzanService classNewsDianzanService;

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		ClassNewsDianzanJsonform classNewsJsonform;
		try {
			classNewsJsonform = (ClassNewsDianzanJsonform) this
					.bodyJsonToFormObject(bodyJson,
							ClassNewsDianzanJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}

		// 设置当前用户
		SessionUserInfoInterface user = this.getUserInfoBySession(request);
		classNewsJsonform.setCreate_user(user.getName());
		classNewsJsonform.setCreate_useruuid(user.getUuid());
		if(classNewsJsonform.getType()>1000){
			responseMessage.setMessage("type取值超出范围:type="+classNewsJsonform.getType());
			return "";
		}
		try {
			boolean flag;
			flag = classNewsDianzanService.save(classNewsJsonform, responseMessage);
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

	@RequestMapping(value = "/getByNewsuuid", method = RequestMethod.GET)
	public String getByNewsuuid(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String newsuuid = request.getParameter("newsuuid");

		List list;
		try {
			list = classNewsDianzanService.getDianzanByNewsuuid(newsuuid);
			
			Boolean canDianzan=true;
			if(list.size()>0){
				SessionUserInfoInterface user = this.getUserInfoBySession(request);
				canDianzan=classNewsDianzanService.canDianzan(newsuuid,user.getUuid());
			}
			model.addAttribute("names", StringUtils.join(list,","));
			model.addAttribute("canDianzan", canDianzan);
			model.addAttribute(RestConstants.Return_ResponseMessage_count, list.size());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		ClassNewsDianzanJsonform classNewsJsonform;
		try {
			classNewsJsonform = (ClassNewsDianzanJsonform) this
					.bodyJsonToFormObject(bodyJson,
							ClassNewsDianzanJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}

		// 设置当前用户
		SessionUserInfoInterface user = this.getUserInfoBySession(request);
		classNewsJsonform.setCreate_user(user.getName());
		classNewsJsonform.setCreate_useruuid(user.getUuid());

		try {
			boolean flag;
			flag = classNewsDianzanService.delete(classNewsJsonform, responseMessage);
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
		responseMessage.setMessage("修改成功");
		return "";
	}

}
