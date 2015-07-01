package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.entity.CookbookPlan;
import com.company.news.entity.Teachingplan;
import com.company.news.jsonform.ClassRegJsonform;
import com.company.news.jsonform.CookbookPlanJsonform;
import com.company.news.jsonform.StudentJsonform;
import com.company.news.jsonform.TeachingPlanJsonform;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.CookbookPlanService;
import com.company.news.service.TeachingPlanService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/teachingplan")
public class TeachingPlanController extends AbstractRESTController {

	@Autowired
	private TeachingPlanService teachingPlanService;



	/**
	 * 获取班级信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);

		List<Teachingplan> list = teachingPlanService.query(
				request.getParameter("begDateStr"),
				request.getParameter("endDateStr"),
				request.getParameter("classuuid"));
		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}


	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid, ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		Teachingplan t = teachingPlanService.get(uuid);

		model.addAttribute(RestConstants.Return_G_entity, t);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

}
