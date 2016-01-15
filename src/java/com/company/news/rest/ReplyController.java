package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.commons.util.MyUbbUtils;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ClassNewsReplyJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.ClassNewsReplyService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/reply")
public class ReplyController extends AbstractRESTController {

	@Autowired
	private ClassNewsReplyService classNewsReplyService;

	/**
	 * 组织注册
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
		ClassNewsReplyJsonform classNewsReplyJsonform;
		try {
			
			
			classNewsReplyJsonform = (ClassNewsReplyJsonform) this.bodyJsonToFormObject(
					bodyJson, ClassNewsReplyJsonform.class);
			
			String classuuids=this.getMyChildrenClassuuidsBySession(request);
			if(StringUtils.isBlank(classuuids)){
				responseMessage.setMessage("没有你的孩子信息,不能发布评论!");
				return "";
			}
		if(classNewsReplyJsonform.getType()==null||classNewsReplyJsonform.getType()>1000){
			responseMessage.setMessage("type取值超出范围:type="+classNewsReplyJsonform.getType());
			return "";
		}
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		//转换特定格式.
		classNewsReplyJsonform.setContent(MyUbbUtils.htmlToMyUbb(classNewsReplyJsonform.getContent()));

			boolean flag;
			if(StringUtils.isEmpty(classNewsReplyJsonform.getUuid()))
			    flag = classNewsReplyService.add(user,classNewsReplyJsonform, responseMessage);
			else
				flag = classNewsReplyService.update(user,classNewsReplyJsonform, responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("操作成功");
		return "";
	}


	/**
	 * 获取班级信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getReplyByNewsuuid", method = RequestMethod.GET)
	public String getReplyByNewsuuid(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			
			
			SessionUserInfoInterface parent=this.getUserInfoBySession(request);
			String cur_user_uuid="";
			if(parent!=null)cur_user_uuid=parent.getUuid();
			
			PaginationData pData=this.getPaginationDataByRequest(request);
			PageQueryResult pageQueryResult = classNewsReplyService.query(request.getParameter("newsuuid"), pData,cur_user_uuid);
		
			classNewsReplyService.warpVoList(pageQueryResult.getData(), cur_user_uuid);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
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
			boolean flag = classNewsReplyService.delete(request.getParameter("uuid"),
					responseMessage);
			if (!flag)
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("删除成功");
		return "";
	}
	

}
