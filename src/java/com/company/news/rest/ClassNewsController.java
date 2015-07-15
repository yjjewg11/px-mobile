package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.entity.Parent;
import com.company.news.jsonform.ClassNewsJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.ClassNewsService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/classnews")
public class ClassNewsController extends AbstractRESTController {

	@Autowired
	private ClassNewsService classNewsService;

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
		ClassNewsJsonform classNewsJsonform;
		try {
			classNewsJsonform = (ClassNewsJsonform) this.bodyJsonToFormObject(
					bodyJson, ClassNewsJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage(error_bodyJsonToFormObject);
			return "";
		}

		// 设置当前用户
		Parent user = this.getUserInfoBySession(request);
		classNewsJsonform.setCreate_user(user.getName());
		classNewsJsonform.setCreate_useruuid(user.getUuid());

		try {
			boolean flag;
			if (StringUtils.isEmpty(classNewsJsonform.getUuid()))
				flag = classNewsService.add(classNewsJsonform, responseMessage);
			else
				flag = classNewsService.update(classNewsJsonform,
						responseMessage);
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

	/**
	 * 获取班级信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getClassNewsByClassuuid", method = RequestMethod.GET)
	public String getClassNewsByClassuuid(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		PaginationData pData = this.getPaginationDataByRequest(request);

		PageQueryResult pageQueryResult = classNewsService.query(null,null,
				request.getParameter("classuuid"), pData);
		model.addAttribute(RestConstants.Return_ResponseMessage_list,
				pageQueryResult);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	
	/**
	 * 获取我的相关班级信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getClassNewsByMy", method = RequestMethod.GET)
	public String getClassNewsByMy(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		PaginationData pData = this.getPaginationDataByRequest(request);
		Parent user = this.getUserInfoBySession(request);
		PageQueryResult pageQueryResult = classNewsService.query(user,"myByTeacher",
				request.getParameter("classuuid"), pData);
		model.addAttribute(RestConstants.Return_ResponseMessage_list,
				pageQueryResult);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

	/**
	 * 删除
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
			boolean flag = classNewsService.delete(
					request.getParameter("uuid"), responseMessage);
			if (!flag)
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
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
		ClassNewsJsonform c;
		try {
			c = classNewsService.get(uuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity, c);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}


}
