package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.SystemConstants;
import com.company.news.entity.Parent;
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
			responseMessage.setMessage(error_bodyJsonToFormObject);
			return "";
		}
		
		//设置当前用户
		Parent user=this.getUserInfoBySession(request);
		jsonform.setUser_uuid(user.getUuid());
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
		
		jsonform.setGroup_uuid(this.getMyChildrenGroupUuidsBySession(request));
		try {
			boolean flag;
			    flag = pushMsgDeviceService.save(jsonform, responseMessage);

			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("修改成功");
		return "";
	}
}
