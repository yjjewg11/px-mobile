package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.SystemConstants;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.PushMsgDeviceJsonform;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.PushMsgDeviceService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/pushMsgDevice")
public class PushMsgDeviceController extends AbstractRESTController {

	@Autowired
	private PushMsgDeviceService pushMsgDeviceService;

	

	/**
	 * 给老师写信
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveToTeacher(ModelMap model, HttpServletRequest request) {
		
		
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		PushMsgDeviceJsonform jsonform;
		try {
			jsonform = (PushMsgDeviceJsonform) this.bodyJsonToFormObject(
					bodyJson, PushMsgDeviceJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}

		try {
			if(StringUtils.isBlank(jsonform.getDevice_id())){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("参数必填:device_id");
				return "";
			}
			//设置当前用户
			SessionUserInfoInterface user=this.getUserInfoBySession(request);
			if(jsonform.getStatus()==null){
				jsonform.setStatus(0);
			}
			jsonform.setType(SystemConstants.PushMsgDevice_type_0);
			if(!SystemConstants.PushMsgDevice_device_type_android.equals(jsonform.getDevice_type())
					&&!SystemConstants.PushMsgDevice_device_type_ios.equals(jsonform.getDevice_type())){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("参数无效:device_type="+jsonform.getDevice_type());
				return "";
			}
			boolean flag;
			    flag = pushMsgDeviceService.save(jsonform, responseMessage,request);

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
