package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.entity.PxStudent;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.PxClassService;
import com.company.news.service.PxStudentService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/pxstudent")
public class PxStudentController extends AbstractRESTController {
	@Autowired
	private PxStudentService pxStudentService;

	@Autowired
	private PxClassService pxClassService;
	/**
	 * 获取我的孩子列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listByMy", method = RequestMethod.GET)
	public String listByMy(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		SessionUserInfoInterface parent=this.getUserInfoBySession(request);
		
		List<PxStudent> list = pxStudentService.listByMy(parent);
		List list1= pxClassService.listPxStudentPXClassRelationByStudent(parent);
		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		model.addAttribute("class_list", list1);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid, ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		PxStudent s;
		try {
			if(DBUtil.isSqlInjection(uuid, responseMessage))return "";
			
			s = pxStudentService.get(uuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity, s);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

}
