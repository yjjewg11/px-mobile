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
import com.company.news.commons.util.DistanceUtil;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Group;
import com.company.news.entity.Group4Q;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.CountService;
import com.company.news.service.GroupService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/group")
public class GroupController extends AbstractRESTController {

	@Autowired
	private GroupService groupService;
	 @Autowired
     private CountService countService ;


	/**
	 * 获取机构信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@Deprecated
	public String list(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			List<Group4Q> list = groupService.query();
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
	 * 查询所有培训机构列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/pxlistByPage", method = RequestMethod.GET)
	public String pxlistByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
//			pData.setPageSize(50);
		
			String type = request.getParameter("type");
			//sort	 否	排序.取值: intelligent(智能排序). appraise(评价最高).distance(距离最近)
			String sort = request.getParameter("sort");
			String mappoint = request.getParameter("map_point");
			PageQueryResult list = groupService.pxlistByPage(type,sort,pData,mappoint);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

	/**
	 * 查询所有培训机构列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/kdlistByPage", method = RequestMethod.GET)
	public String kdlistByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
//			pData.setPageSize(50);
		
			//sort	 否	排序.取值: intelligent(智能排序). appraise(评价最高).distance(距离最近)
			String sort = request.getParameter("sort");
			String mappoint = request.getParameter("map_point");
			PageQueryResult list = groupService.kdlistByPage(sort,pData,mappoint);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}


	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid,ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		Group c;
		try {
			c = groupService.get(uuid);
			if(c ==null){
				responseMessage.setMessage("学校不存在！");
				return "";
			}
			
			
			 countService.count(uuid, SystemConstants.common_type_pxgroup);
			 SessionUserInfoInterface user = this.getUserInfoBySession(request);
			 
			 String mappoint = request.getParameter("map_point");
				//当前坐标点参数不为空时，进行距离计算
				if(StringUtils.isNotBlank(mappoint)){
					 model.addAttribute("distance", DistanceUtil.getDistance(mappoint, c.getMap_point()));
				}else{
					 model.addAttribute("distance", "");
				}
				
				
			 model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getGroupShareURLByUuid(uuid));
			 model.put(RestConstants.Return_ResponseMessage_isFavorites,groupService.isFavorites( user.getUuid(),uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,c);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	
	

	/**
	 * 不返回内容,返回内容的url地址.
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/get2", method = RequestMethod.GET)
	public String get2(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			
			
			String uuid= request.getParameter("uuid");
			
			if(StringUtils.isBlank(uuid)){
				responseMessage.setMessage("uuid 必填");
				return "";
			}
			Group4Q c = groupService.getGroup4Q(uuid);
			if(c ==null){
				responseMessage.setMessage("学校不存在！");
				return "";
			}
			
			String mappoint = request.getParameter("map_point");
			//当前坐标点参数不为空时，进行距离计算
			if(StringUtils.isNotBlank(mappoint)){
				 model.addAttribute("distance", DistanceUtil.getDistance(mappoint, c.getMap_point()));
			}else{
				 model.addAttribute("distance", "");
			}
			 countService.count(uuid, SystemConstants.common_type_pxgroup);
			 SessionUserInfoInterface user = this.getUserInfoBySession(request);
			 model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getGroupShareURLByUuid(uuid));
			 model.put(RestConstants.Return_ResponseMessage_obj_url,PxStringUtil.getGroupShareURLByUuid(uuid));
			
			 model.put(RestConstants.Return_ResponseMessage_isFavorites,groupService.isFavorites( user.getUuid(),uuid));
			 
			 
			 model.addAttribute(RestConstants.Return_G_entity,c);
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
	 * 获取幼儿园详细
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getKD", method = RequestMethod.GET)
	public String getKD(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			
			
			String uuid= request.getParameter("uuid");
			if(StringUtils.isBlank(uuid)){
				responseMessage.setMessage("uuid 必填");
				return "";
			}
			Group4Q c = groupService.getGroup4Q(uuid);
			if(c ==null){
				responseMessage.setMessage("学校不存在！");
				return "";
			}
			 countService.count(uuid, SystemConstants.common_type_pxgroup);
			 SessionUserInfoInterface user = this.getUserInfoBySession(request);
			 model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getGroupShareURLByUuid(uuid));
			 model.put(RestConstants.Return_ResponseMessage_obj_url,PxStringUtil.getGroupShareURLByUuid(uuid));
			 model.put("recruit_url",PxStringUtil.getGroupRecruitURLByUuid(uuid));
			 
			 
			 model.put(RestConstants.Return_ResponseMessage_isFavorites,groupService.isFavorites( user.getUuid(),uuid));
				String mappoint = request.getParameter("map_point");
				//当前坐标点参数不为空时，进行距离计算
				if(StringUtils.isNotBlank(mappoint)){
					 model.addAttribute("distance", DistanceUtil.getDistance(mappoint, c.getMap_point()));
				}else{
					 model.addAttribute("distance", "");
				}
			 model.addAttribute(RestConstants.Return_G_entity,c);
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
}
