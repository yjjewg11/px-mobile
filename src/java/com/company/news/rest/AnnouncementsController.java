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
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.AnnouncementsService;
import com.company.news.service.CountService;
import com.company.news.vo.AnnouncementsVo;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/announcements")
public class AnnouncementsController extends AbstractRESTController {
	@Autowired
	private CountService countService;
	@Autowired
	private AnnouncementsService announcementsService;
	/**
	 * 根据分类获取所有，管理员用
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
			List list = announcementsService.query(request.getParameter("type"),request.getParameter("groupuuid"));
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


	
	
	/**
	 * 获取我的通知(无用)
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
//	@Deprecated
//	@RequestMapping(value = "/queryMyAnnouncements", method = RequestMethod.GET)
//	public String queryMyAnnouncements(ModelMap model, HttpServletRequest request) {
//		ResponseMessage responseMessage = RestUtil
//				.addResponseMessageForModelMap(model);
//		List list = announcementsService.queryMyAnnouncements(request.getParameter("type"),request.getParameter("groupuuid"),request.getParameter("classuuid"));
//		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
//		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
//		return "";
//	}
//	

	/**
	 * 获取我的孩子学校相关的公告
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryMy", method = RequestMethod.GET)
	public String queryMy(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
			String groupuuids=request.getParameter("groupuuids");
			if(StringUtils.isBlank(groupuuids)){
				groupuuids=this.getMyChildrenGroupUuidsBySession(request);
			}
			PageQueryResult pageQueryResult=null;
			if(StringUtils.isBlank(groupuuids)){
				pageQueryResult=new PageQueryResult();
			}else{
				
				pageQueryResult = announcementsService.query(groupuuids,pData);
			}
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
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid,ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		AnnouncementsVo a=null;
		try {
			a = announcementsService.get(uuid);
			
			if(a==null){
				responseMessage.setMessage("数据不存在!");
				return "";
			}
			if(SystemConstants.Check_status_disable.equals(a.getStatus())){
				responseMessage.setMessage("数据已被禁止浏览!");
				return "";
			}
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			announcementsService.warpVo(a, user.getUuid());
			//定义接口,返回浏览总数.
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_gonggao));
			model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getAnnByUuid(uuid));
			model.put(RestConstants.Return_ResponseMessage_isFavorites,announcementsService.isFavorites( user.getUuid(),uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,a);
	
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
}
