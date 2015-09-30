package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.entity.PxTeachingplan;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.PxTeachingPlanService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/pxteachingplan")
public class PxTeachingPlanController extends AbstractRESTController {

	@Autowired
	private PxTeachingPlanService pxTeachingPlanService;

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

		
		PaginationData pData = this.getPaginationDataByRequest(request);
		
		PageQueryResult pageQueryResult = pxTeachingPlanService.query(
				request.getParameter("begDateStr"),
				request.getParameter("endDateStr"),
				request.getParameter("classuuid"),pData,this.getUserInfoBySession(request).getUuid());
		model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

	/**
	 * 班级删除
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);

		try {
			boolean flag = pxTeachingPlanService.delete(
					request.getParameter("uuid"), responseMessage);
			if (!flag)
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("删除成功");
		return "";
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid, ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		PxTeachingplan t = pxTeachingPlanService.get(uuid);

		model.addAttribute(RestConstants.Return_G_entity, t);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

}
