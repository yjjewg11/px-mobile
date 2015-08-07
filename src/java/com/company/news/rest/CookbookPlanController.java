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

import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.CookbookPlan;
import com.company.news.entity.Parent;
import com.company.news.jsonform.ClassRegJsonform;
import com.company.news.jsonform.CookbookPlanJsonform;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.CookbookPlanService;
import com.company.news.service.CountService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/cookbookplan")
public class CookbookPlanController extends AbstractRESTController {

	@Autowired
	private CookbookPlanService cookbookPlanService;



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
			String endDateStr=request.getParameter("endDateStr");
			String groupuuid=request.getParameter("groupuuid");
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
			if(StringUtils.isBlank(groupuuid)){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("参数groupuuid不能为空!");
				return "";
			}
			Parent user = this.getUserInfoBySession(request);
			List<CookbookPlan> list = cookbookPlanService.query(
					begDateStr,
					endDateStr,
					groupuuid,user.getUuid());
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);

			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
	
	}

	@Autowired
	private CountService countService;
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid,ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			Parent user = this.getUserInfoBySession(request);
			CookbookPlan c = cookbookPlanService.get(uuid,user.getUuid());
			//定义接口,返回浏览总数.
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_shipu));
			model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getCookbookPlanByUuid(uuid));

			model.addAttribute(RestConstants.Return_G_entity,c);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		
	}

}
