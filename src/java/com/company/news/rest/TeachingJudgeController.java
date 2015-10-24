package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.entity.TeacherJudge;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.TeachingJudgeJsonform;
import com.company.news.rest.util.RestUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.service.TeachingJudgeService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/teachingjudge")
public class TeachingJudgeController extends AbstractRESTController {

	@Autowired
	private TeachingJudgeService teachingJudgeService;

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
		TeachingJudgeJsonform teachingJudgeJsonform;
		try {
			teachingJudgeJsonform = (TeachingJudgeJsonform) this
					.bodyJsonToFormObject(bodyJson, TeachingJudgeJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}

		try {
			if(teachingJudgeJsonform.getType()!=null
					&&teachingJudgeJsonform.getType().intValue()>0
					&&teachingJudgeJsonform.getType().intValue()<4)	{
				
			}else{
				responseMessage.setMessage("数据不合法,Type有效取值范围:[1-3],当前为:type="+teachingJudgeJsonform.getType());
				return "";
			}
			// 设置当前用户
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			teachingJudgeJsonform.setCreate_user(user.getName());
			teachingJudgeJsonform.setCreate_useruuid(user.getUuid());
			boolean flag = teachingJudgeService.add(teachingJudgeJsonform,
					responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("修改成功");
		return "";
	}

	@RequestMapping(value = "/getJudgeByDate", method = RequestMethod.GET)
	public String getJudgeByDate(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);

		try {
			// 设置当前用户
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			TeacherJudge t = teachingJudgeService.getJudgeByDate(
					request.getParameter("teacheruuid"),
					TimeUtils.getCurrentTimestamp(), user.getUuid());

			model.addAttribute(RestConstants.Return_G_entity, t);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
			.setStatus(RestConstants.Return_ResponseMessage_failed);
	responseMessage.setMessage("服务器异常:"+e.getMessage());
		}
		return "";
	}
	
	

	/**
	 * 获取我孩子相关老师列表及老师评价列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getTeachersAndJudges", method = RequestMethod.GET)
	public String getTeachersAndJudges(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			SessionUserInfoInterface user=this.getUserInfoBySession(request);	
			String class_uuids=this.getMyChildrenClassuuidsBySession(request);
			
			 teachingJudgeService.getTeachersAndJudges(user.getUuid(),class_uuids,model);
			 
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
	}

}
