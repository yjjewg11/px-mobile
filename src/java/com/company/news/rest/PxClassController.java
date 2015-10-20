package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.entity.PxClass;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.PxClassService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/pxclass")
public class PxClassController extends AbstractRESTController {

	@Autowired
	private PxClassService pxClassService;

	

	/**
	 * 我孩子参加的培训班级列表(正在学习,完成学习)
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listMyChildClassByPage", method = RequestMethod.GET)
	public String listMyChildClassByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
			String isdisable=request.getParameter("isdisable");
			PageQueryResult list = pxClassService.listMyChildClassByPage(this.getUserInfoBySession(request).getUuid(),pData,isdisable);

			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
			.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}


	/**
	 * 查询我孩子参加的班级
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listByStudent", method = RequestMethod.GET)
	public String listByStudent(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		
		String student_uuid=request.getParameter("student_uuid");
		if(StringUtils.isBlank(student_uuid)){//查询全部班级时,只有管理员可以.
				responseMessage.setMessage("student_uuid 不能为空!");
				return "";
		}
		List<PxClass> list = pxClassService.listByStudent(student_uuid);

		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

	

	/**
	 * 查询班级列表,根据uuid
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listByuuids", method = RequestMethod.GET)
	public String listByuuids(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		
		String uuids=request.getParameter("uuids");
		if(StringUtils.isBlank(uuids)){//查询全部班级时,只有管理员可以.
				responseMessage.setMessage("uuids 不能为空!");
				return "";
		}
		List<PxClass> list = pxClassService.listByuuids(uuids);

		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

	
	/**
	 * 获取班级信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryClassByUseruuid", method = RequestMethod.GET)
	public String queryClassByUseruuid(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		List list = pxClassService.queryClassByUseruuid(this.getUserInfoBySession(request).getUuid());

		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid,ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		PxClass c;
		try {
			c = pxClassService.get(uuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,c);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	


	/**
	 * 班级老师列表.
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listclassTeacher", method = RequestMethod.GET)
	public String listclassTeacher(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			String classuuid=request.getParameter("classuuid");
			
			if(StringUtils.isBlank(classuuid)){
				responseMessage.setMessage("classuuid 不能为空!");
				return "";
		}
			List list = pxClassService.listclassTeacher(classuuid);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
			.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

}
