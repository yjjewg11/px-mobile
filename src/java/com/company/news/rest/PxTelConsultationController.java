package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.commons.util.PxStringUtil;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.PxTelConsultationJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.PxTelConsultationService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/pxTelConsultation")
public class PxTelConsultationController extends AbstractRESTController {

	@Autowired
	private PxTelConsultationService pxTelConsultationService;

	/**
	 * 
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
		PxTelConsultationJsonform appraiseJsonform;
		try {
			appraiseJsonform = (PxTelConsultationJsonform) this.bodyJsonToFormObject(
					bodyJson, PxTelConsultationJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:" + error_bodyJsonToFormObject);
			return "";
		}

		try {
			// 设置当前用户
			SessionUserInfoInterface user = this.getUserInfoBySession(request);		
			boolean flag = pxTelConsultationService.add(appraiseJsonform,
					responseMessage,user);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:" + e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("修改成功");
		return "";
	}

	@RequestMapping(value = "/queryByPage", method = RequestMethod.GET)
	public String queryByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);

		try {
			String ext_uuid=request.getParameter("ext_uuid");
			PaginationData pData = this.getPaginationDataByRequest(request);
			PageQueryResult list = pxTelConsultationService.queryByPage(ext_uuid, pData);

			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:" + e.getMessage());
		}
		return "";
	}
	
	@RequestMapping(value = "/queryMyByPage", method = RequestMethod.GET)
	public String queryMyByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String class_uuid=request.getParameter("class_uuid");
			PaginationData pData = this.getPaginationDataByRequest(request);
			PageQueryResult list = pxTelConsultationService.queryMyByPage(class_uuid,this.getUserInfoBySession(request).getUuid(), pData);

			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:" + e.getMessage());
		}
		return "";
	}

}
