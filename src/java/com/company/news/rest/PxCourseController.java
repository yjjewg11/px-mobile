package com.company.news.rest;

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
import com.company.news.entity.PxCourse;
import com.company.news.entity.PxCourse4Q;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.CountService;
import com.company.news.service.PxCourseService;
import com.company.news.vo.ResponseMessage;

/**
 * 培训机构对外发布课程
 * @author Administrator
 *
 */
@Controller
@RequestMapping(value = "/pxCourse")
public class PxCourseController extends AbstractRESTController {

	@Autowired
	private PxCourseService pxCourseService;

	 @Autowired
     private CountService countService ;

	/**
	 * 获取培训机构对外发布课程列表分页
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryByPage", method = RequestMethod.GET)
	public String queryByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
//			pData.setPageSize(50);
			String groupuuid = request.getParameter("groupuuid");
			String mappoint = request.getParameter("map_point");
			
			
			String type = request.getParameter("type");
			String teacheruuid = request.getParameter("teacheruuid");
			//sort	 否	排序.取值: intelligent(智能排序). appraise(评价最高).distance(距离最近)
			String sort = request.getParameter("sort");
			PageQueryResult list = pxCourseService.queryByPage(groupuuid,type,pData,mappoint,teacheruuid,sort);

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
	 * 
	 * 热门课程
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hotByPage", method = RequestMethod.GET)
	public String hotByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
			String groupuuid = request.getParameter("groupuuid");
			String mappoint = request.getParameter("map_point");
			String type = request.getParameter("type");
			String teacheruuid = request.getParameter("teacheruuid");
			//sort	 否	排序.取值: intelligent(智能排序). appraise(评价最高).distance(距离最近)
			String sort = request.getParameter("sort");
			PageQueryResult list = pxCourseService.queryByPage(groupuuid,type,pData,mappoint,teacheruuid,sort);

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
 * 获取培训机构对外发布课程详细
 * @param uuid
 * @param model
 * @param request
 * @return
 * 新版本ios 1.3以上不只使用.
 */
	@Deprecated
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid, ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			if(DBUtil.isSqlInjection(uuid, responseMessage))return "";
			
			PxCourse t = pxCourseService.get(uuid);
		
			if(t==null){
				responseMessage.setMessage("数据不存在,uuid="+uuid);
				return "";
			}
			pxCourseService.warpVo(t);
			model.addAttribute(RestConstants.Return_G_entity, t);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			 countService.count(uuid, SystemConstants.common_type_pxcourse);
			 SessionUserInfoInterface user = this.getUserInfoBySession(request);
			 model.put(RestConstants.Return_ResponseMessage_isFavorites,pxCourseService.isFavorites( user.getUuid(),uuid));
//			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_pxcourse));
			 model.put(RestConstants.Return_ResponseMessage_link_tel,pxCourseService.getGroupLink_tel( t.getGroupuuid()));
			 
			 model.put(RestConstants.Return_ResponseMessage_obj_url,PxStringUtil.getPxCourseContentByUuid(uuid));
			model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getPxCourseByUuid(uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
			.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		return "";
	}
	
/**
 * 获取培训机构对外发布课程详细
 * @param uuid
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
			PxCourse4Q t = pxCourseService.getPxCourse4Q(uuid);
		
			if(t==null){
				responseMessage.setMessage("数据不存在,uuid="+uuid);
				return "";
			}
			pxCourseService.warpVo(t);
			model.addAttribute(RestConstants.Return_G_entity, t);
			
			String age_min_max=null;
			if(t.getAge_max()!=null||t.getAge_min()!=null){
				age_min_max="";
				if(t.getAge_min()!=null)age_min_max+=t.getAge_min();
				else age_min_max+="不限";
				age_min_max+="-";
				if(t.getAge_max()!=null)age_min_max+=t.getAge_max();
				else age_min_max+="不限";
			}
			model.addAttribute("age_min_max", age_min_max);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			 countService.count(uuid, SystemConstants.common_type_pxcourse);
			 SessionUserInfoInterface user = this.getUserInfoBySession(request);
			 model.put(RestConstants.Return_ResponseMessage_isFavorites,pxCourseService.isFavorites( user.getUuid(),uuid));
		
			 model.put(RestConstants.Return_ResponseMessage_link_tel,pxCourseService.getGroupLink_tel( t.getGroupuuid()));
			 model.put(RestConstants.Return_ResponseMessage_obj_url,PxStringUtil.getPxCourseContentByUuid(uuid));
			model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getPxCourseByUuid(uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
			.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		return "";
	}
}
