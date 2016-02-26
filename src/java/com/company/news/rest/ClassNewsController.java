package com.company.news.rest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.SystemConstants;
import com.company.news.commons.util.MyUbbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ClassNewsJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.ClassNewsService;
import com.company.news.service.CountService;
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
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}

		// 设置当前用户
		SessionUserInfoInterface user = this.getUserInfoBySession(request);
		//转换特定格式.
		classNewsJsonform.setContent(MyUbbUtils.htmlToMyUbb(classNewsJsonform.getContent()));
		classNewsJsonform.setImgs(PxStringUtil.imgUrlToUuid(classNewsJsonform.getImgs()));
		try {
			boolean flag;
			if (StringUtils.isEmpty(classNewsJsonform.getUuid()))
				flag = classNewsService.add(user,classNewsJsonform, responseMessage);
			else
				flag = classNewsService.update(user,classNewsJsonform,
						responseMessage);
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
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);

			PageQueryResult pageQueryResult = classNewsService.query(null,
					request.getParameter("classuuid"), pData);
			model.addAttribute(RestConstants.Return_ResponseMessage_list,
					pageQueryResult);
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
	
	/**
	 * 获取我的孩子相关班级的互动信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryPxClassNewsBy", method = RequestMethod.GET)
	public String queryPxClassNewsBy(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
			pData.setPageSize(5);
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String courseuuid=request.getParameter("courseuuid");
			String groupuuid=request.getParameter("groupuuid");
			PageQueryResult pageQueryResult = classNewsService.queryPxClassNewsBy(user,
					courseuuid,groupuuid, pData);
			model.addAttribute(RestConstants.Return_ResponseMessage_list,
					pageQueryResult);
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
	
	
	/**
	 * 获取我的孩子相关班级的互动信息
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
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
			pData.setPageSize(5);
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String classuuids=request.getParameter("classuuid");
			if(StringUtils.isBlank(classuuids)){
				classuuids=classNewsService.getMyChildrenClassuuidsBySession(request);
				String pxclassuuids=(String)this.getValueOfSession(request,RestConstants.Session_MyStudentClassUuids);
				classuuids+=","+pxclassuuids;
				classuuids=PxStringUtil.StringDecComma(classuuids);
			}
			PageQueryResult pageQueryResult = classNewsService.query(user,
					classuuids, pData);
			model.addAttribute(RestConstants.Return_ResponseMessage_list,
					pageQueryResult);
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
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("删除成功");
		return "";
	}
	@Autowired
	private CountService countService;
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid, ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		Map c=null;
		try {
			if(DBUtil.isSqlInjection(uuid, responseMessage)){
				return "";
			}
			c = classNewsService.get(this.getUserInfoBySession(request),uuid);
			
			//定义接口,返回浏览总数.
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_hudong));
			model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getClassNewsByUuid(uuid));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity, c);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}


}
