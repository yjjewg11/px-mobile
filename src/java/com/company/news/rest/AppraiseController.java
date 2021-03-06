package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.commons.util.PxStringUtil;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.AppraiseJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.AppraiseService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/appraise")
public class AppraiseController extends AbstractRESTController {

	@Autowired
	private AppraiseService appraiseService;

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
		AppraiseJsonform appraiseJsonform;
		try {
			appraiseJsonform = (AppraiseJsonform) this.bodyJsonToFormObject(
					bodyJson, AppraiseJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:" + error_bodyJsonToFormObject);
			return "";
		}

		try {
			// 设置当前用户
			SessionUserInfoInterface user = this.getUserInfoBySession(request);		
			//保密用户信息.增加匿名提交评价.
			if(Integer.valueOf(1).equals(appraiseJsonform.getAnonymous())){
				appraiseJsonform.setCreate_user("匿名");
			}else{
				appraiseJsonform.setCreate_user(PxStringUtil.getSecretCellphone(user.getLoginname()));
			}
			
			appraiseJsonform.setCreate_useruuid(user.getUuid());
			
			boolean flag=false;
			flag = appraiseService.add(appraiseJsonform, responseMessage,request);
			
			
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
			PageQueryResult list = appraiseService.queryByPage(ext_uuid, pData);

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
	
	/**
	 * 根据培训班级uuid查询,我评价内容.
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryMyByPage", method = RequestMethod.GET)
	public String queryMyByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String class_uuid=request.getParameter("class_uuid");
			PaginationData pData = this.getPaginationDataByRequest(request);
			PageQueryResult list = appraiseService.queryMyByPage(class_uuid,this.getUserInfoBySession(request).getUuid(), pData);

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
	
	

	/**
	 *查询我的幼儿园培训内容.
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryMyKDByPage", method = RequestMethod.GET)
	public String queryMyKDByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String groupuuid=request.getParameter("groupuuid");
			PaginationData pData = this.getPaginationDataByRequest(request);
			PageQueryResult list = appraiseService.queryMyKDByPage(groupuuid,this.getUserInfoBySession(request).getUuid(), pData);

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
