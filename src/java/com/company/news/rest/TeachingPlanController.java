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

import com.company.news.entity.CookbookPlan;
import com.company.news.entity.Teachingplan;
import com.company.news.jsonform.ClassRegJsonform;
import com.company.news.jsonform.CookbookPlanJsonform;
import com.company.news.jsonform.StudentJsonform;
import com.company.news.jsonform.TeachingPlanJsonform;
import com.company.news.rest.util.DBUtil;
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

		try {
			
			
			String begDateStr=request.getParameter("begDateStr");
			if(DBUtil.isSqlInjection(begDateStr, responseMessage))return "";
			
			String endDateStr=request.getParameter("endDateStr");
			if(DBUtil.isSqlInjection(endDateStr, responseMessage))return "";
			String classuuid=request.getParameter("classuuid");
			if(DBUtil.isSqlInjection(classuuid, responseMessage))return "";
			if(StringUtils.isBlank(begDateStr)){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("参数begDateStr不能为空!");
				return "";
			}
			if(StringUtils.isBlank(endDateStr)){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("参数endDateStr不能为空!");
				return "";
			}
			if(StringUtils.isBlank(classuuid)){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("参数classuuid不能为空!");
				return "";
			}
			List<Teachingplan> list = teachingPlanService.query(
					request.getParameter("begDateStr"),
					request.getParameter("endDateStr"),
					request.getParameter("classuuid"),this.getUserInfoBySession(request));
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);

			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		return "";
	}


	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid, ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		if(DBUtil.isSqlInjection(uuid, responseMessage))return "";
		
		Teachingplan t = teachingPlanService.get(uuid,this.getUserInfoBySession(request));

		model.addAttribute(RestConstants.Return_G_entity, t);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

}
