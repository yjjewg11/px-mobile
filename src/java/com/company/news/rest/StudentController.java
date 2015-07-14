package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.entity.Student;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.StudentService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/student")
public class StudentController extends AbstractRESTController {
	@Autowired
	private StudentService studentService;



	/**
	 * 获取机构信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getStudentByClassuuid", method = RequestMethod.GET)
	public String getStudentByClassuuid(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		List<Student> list = studentService.query(
				request.getParameter("classuuid"),
				request.getParameter("groupuuid"));
		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid, ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		Student s;
		try {
			s = studentService.get(uuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity, s);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}


}
