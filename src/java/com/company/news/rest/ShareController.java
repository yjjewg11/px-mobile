package com.company.news.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.common.HttpRequestUtils;
import com.company.news.ProjectProperties;
import com.company.news.ResponseMessageConstants;
import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.cache.PxConfigCache;
import com.company.news.cache.redis.FPPhotoRedisCache;
import com.company.news.commons.util.FPPhotoUtil;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.dao.NSimpleHibernateDao;
import com.company.news.entity.Announcements;
import com.company.news.entity.Announcements4Q;
import com.company.news.entity.ClassNews;
import com.company.news.entity.Cookbook;
import com.company.news.entity.CookbookPlan;
import com.company.news.entity.FPMovie;
import com.company.news.entity.FPMovieOfPlay;
import com.company.news.entity.Group;
import com.company.news.entity.Group4Q;
import com.company.news.entity.PClass;
import com.company.news.entity.User4Q;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.json.JSONUtils;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.MD5Until;
import com.company.news.rest.util.RestUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.service.AnnouncementsService;
import com.company.news.service.ClassNewsService;
import com.company.news.service.CountService;
import com.company.news.service.FPPhotoItemService;
import com.company.news.service.FavoritesService;
import com.company.news.service.GroupService;
import com.company.news.vo.AnnouncementsVo;
import com.company.news.vo.ResponseMessage;

/**
 * 公开类,用于分享数据显示.
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/share")
public class ShareController extends AbstractRESTController {

	 @Autowired
	  @Qualifier("NSimpleHibernateDao")
	  protected NSimpleHibernateDao nSimpleHibernateDao;
	 @Autowired
     private CountService countService ;
	 
		@Autowired
		private FavoritesService favoritesService;

	 @Autowired
     private AnnouncementsService announcementsService ;
	 
	 @Autowired
     private GroupService groupService;
	
	 
	 
	 
		private static String shareImgUrlIOS=null;
	 /**
		 * 获取老师web登录地址. ios https 
		 * @param model
		 * @param request
		 * @return
		 */
		@RequestMapping(value = "/getConfigOfIOS", method = RequestMethod.GET)
		public String getConfigOfIOS(ModelMap model, HttpServletRequest request) {
			ResponseMessage responseMessage = RestUtil
					.addResponseMessageForModelMap(model);
			try {
				
			
				
				if(shareImgUrlIOS==null){
					List list=this.nSimpleHibernateDao
							.getHibernateTemplate().find(
									"select description from BaseDataList where typeuuid='shareImgUrlIOS' and datakey=1");
						
						if(list!=null&&list.size()>0){
							shareImgUrlIOS=list.get(0)+"";
						}else{
							shareImgUrlIOS="https://www.wenjienet.com/px-rest/i/share_logo.png";
						}
			}
			
				model.put("shareImgUrl",shareImgUrlIOS);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("服务器异常:"+e.getMessage());
				return "";
			}
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		}
		/**
		 * 获取表情列表(url)
		 * @param model
		 * @param request
		 * @return
		 */
		@RequestMapping(value = "/getEmot", method = RequestMethod.GET)
		public String getEmot(ModelMap model, HttpServletRequest request) {
			ResponseMessage responseMessage = RestUtil
					.addResponseMessageForModelMap(model);
			try {

				Session s = this.nSimpleHibernateDao.getHibernateTemplate()
						.getSessionFactory().openSession();
				//description ios 的关键词,不使用.
				String sql=" SELECT datavalue,description";
				sql+=" from px_basedatalist where typeuuid='emot' and enable=1 order by datakey asc ";
				
				Query q = s.createSQLQuery(sql);
				q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
				List<Map> list= q.list();
				
				String share_url_getEmot=ProjectProperties.getProperty("share_url_getEmot", "http://jz.wenjienet.com/px-rest/i/emoji/");
				
				for(Map o:list){
					//o.getDescription()=laugh.gif
					o.put("description", share_url_getEmot+o.get("description"));
				}
				
				
				model.addAttribute(RestConstants.Return_ResponseMessage_list,list);
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
	 * 全校公告
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getAnn", method = RequestMethod.GET)
	public String getAnn(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		Announcements a=null;
		try {
			a = (Announcements)nSimpleHibernateDao.getObject(Announcements.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "/404";
			}
			if(SystemConstants.Check_status_disable.equals(a.getStatus())){
				return "/404";
		}
			model.put("group",CommonsCache.get(a.getGroupuuid(), Group4Q.class));
			model.put("show_time", TimeUtils.getDateString(a.getCreate_time()));
			
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_gonggao));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "/404";
		}
		model.addAttribute(RestConstants.Return_G_entity,a);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/getAnn";
	}
	
	/**
	 * 获取精品文章列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/articleList", method = RequestMethod.GET)
	public String articleList(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
			String type=request.getParameter("type");
			if(!StringUtils.isNumeric(type)){
				type=SystemConstants.common_type_jingpinwenzhang+"";
			}
			String hql = "from Announcements4Q where status=0 and type="+type;
			hql += " order by create_time desc";
			PageQueryResult pageQueryResult = this.nSimpleHibernateDao
					.findByPaginationToHqlNoTotal(hql, pData);
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String cur_user_uuid=null;
			if(user!=null){
				cur_user_uuid=user.getUuid();
			}
			announcementsService.warpVoList(pageQueryResult.getData(),cur_user_uuid);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
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
	 * 获取精品文章详细
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getArticleJSON", method = RequestMethod.GET)
	public String getArticleJSON(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		if(StringUtils.isBlank(uuid)){
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("uuid 不能为空!");
			return "";
		}
		AnnouncementsVo  vo = new AnnouncementsVo();
		try {
			Announcements a = (Announcements)nSimpleHibernateDao.getObject(Announcements.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "";
			}
			
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String cur_user_uuid=null;
			if(user!=null){
				cur_user_uuid=user.getUuid();
			}
				BeanUtils.copyProperties(vo, a);
				announcementsService.warpVo(vo, cur_user_uuid);
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_jingpinwenzhang));
			model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getArticleByUuid(uuid));
			model.put(RestConstants.Return_ResponseMessage_isFavorites,favoritesService.isFavorites( cur_user_uuid,uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,vo);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	
	/**
	 * 获取精品文章详细
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getArticleUrlJSON", method = RequestMethod.GET)
	public String getArticleUrlJSON(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		if(StringUtils.isBlank(uuid)){
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("uuid 不能为空!");
			return "";
		}
		AnnouncementsVo  vo = new AnnouncementsVo();
		try {
			Announcements4Q a = (Announcements4Q)nSimpleHibernateDao.getObject(Announcements4Q.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "";
			}
			
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String cur_user_uuid=null;
			if(user!=null){
				cur_user_uuid=user.getUuid();
			}
				BeanUtils.copyProperties(vo, a);
				announcementsService.warpNoReplyVo(vo, cur_user_uuid);
				if(!PxStringUtil.isUrl(vo.getUrl())){
					vo.setUrl(PxStringUtil.getArticleByUuid(uuid));
				}else{
					model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_jingpinwenzhang));
					
				}
				model.put(RestConstants.Return_ResponseMessage_share_url,vo.getUrl());
		
			model.put(RestConstants.Return_ResponseMessage_isFavorites,favoritesService.isFavorites( cur_user_uuid,uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,vo);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	
	/**
	 * 获取精品文章详细
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getAnnUrlJSON", method = RequestMethod.GET)
	public String getAnnUrlJSON(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		if(StringUtils.isBlank(uuid)){
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("uuid 不能为空!");
			return "";
		}
		AnnouncementsVo  vo = new AnnouncementsVo();
		try {
			Announcements4Q a = (Announcements4Q)nSimpleHibernateDao.getObject(Announcements4Q.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "";
			}
			
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String cur_user_uuid=null;
			if(user!=null){
				cur_user_uuid=user.getUuid();
			}
			BeanUtils.copyProperties(vo, a);
			announcementsService.warpNoReplyVo(vo, cur_user_uuid);
			if(!PxStringUtil.isUrl(vo.getUrl())){
				vo.setUrl(PxStringUtil.getAnnByUuid(uuid));
			}else{
				model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_gonggao));
				
			}
			model.put(RestConstants.Return_ResponseMessage_share_url,vo.getUrl());
	
			model.put(RestConstants.Return_ResponseMessage_isFavorites,favoritesService.isFavorites( cur_user_uuid,uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,vo);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	
	/**
	 * 获取精品文章详细
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getArticle", method = RequestMethod.GET)
	public String getArticle(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		if(StringUtils.isBlank(uuid)){
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("uuid 不能为空!");
			return "/404";
		}
		AnnouncementsVo vo =null;
		try {
			Announcements a = (Announcements)nSimpleHibernateDao.getObject(Announcements.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "/404";
			}
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String cur_user_uuid=null;
			if(user!=null){
				cur_user_uuid=user.getUuid();
			}
			 vo = new AnnouncementsVo();
			BeanUtils.copyProperties(vo, a);
			announcementsService.warpVo(vo, cur_user_uuid);
			model.put("group",CommonsCache.get(a.getGroupuuid(), Group4Q.class));
			model.put("show_time", TimeUtils.getDateString(a.getCreate_time()));
			
			model.put(RestConstants.Return_ResponseMessage_share_url,PxStringUtil.getArticleByUuid(uuid));
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_jingpinwenzhang));
			model.put(RestConstants.Return_ResponseMessage_isFavorites,favoritesService.isFavorites( cur_user_uuid,uuid));
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "/404";
		}
		model.addAttribute(RestConstants.Return_G_entity,vo);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/getArticle";
	}
	
	@Autowired
	private ClassNewsService classNewsService;
	/**
	 * 全校公告
	 * @param model
	 * @param request 
	 * @return
	 */
	@RequestMapping(value = "/getClassNews", method = RequestMethod.GET)
	public String getClassNews(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		ClassNews a;
		try {
			a = (ClassNews)nSimpleHibernateDao.getObject(ClassNews.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "/404";
			}
			PClass pClass=(PClass)CommonsCache.get(a.getClassuuid(), PClass.class);
			model.put("group",CommonsCache.get(pClass.getGroupuuid(), Group4Q	.class));
			model.put("pclass",CommonsCache.get(a.getClassuuid(), PClass.class));

			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_hudong));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "/404";
		}
		model.addAttribute(RestConstants.Return_G_entity,a);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/getClassNews";
	}

	/**
	 * 全校公告
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getCookbookPlan", method = RequestMethod.GET)
	public String getCookbookPlan(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		CookbookPlan a;
		try {
			a = (CookbookPlan)nSimpleHibernateDao.getObject(CookbookPlan.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "/404";
			}
			
			

			a.setList_time_1(this.getCookbookList(a.getTime_1()));
			a.setList_time_2(this.getCookbookList(a.getTime_2()));
			a.setList_time_3(this.getCookbookList(a.getTime_3()));
			a.setList_time_4(this.getCookbookList(a.getTime_4()));
			a.setList_time_5(this.getCookbookList(a.getTime_5()));
			
			String G_imgPath=ProjectProperties.getProperty("img_down_url_pre", "http://localhost:8080/px-moblie/rest/uploadFile/getImgFile.json?uuid={uuid}");
			model.put("G_imgPath",G_imgPath.replace("{uuid}", ""));
			model.put("group",CommonsCache.get(a.getGroupuuid(), Group4Q.class));
			model.put("plandate",TimeUtils.getDateString(a.getPlandate()));
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_shipu));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "/404";
		}
		model.addAttribute(RestConstants.Return_G_entity,a);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/getCookbookPlan";
	}
	
	/**
	 * 获取幼儿园介绍
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getKDInfo", method = RequestMethod.GET)
	public String getKDInfo(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		Group a=null;
		try {
			a = (Group)nSimpleHibernateDao.getObject(Group.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "/404";
			}

			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_Kindergarten_introduction));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "/404";
		}
		model.addAttribute(RestConstants.Return_G_entity,a);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/getKDInfo";
	}

	/**
	 * 获取招生计划,只查询最新的一篇
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getRecruitBygroupuuid", method = RequestMethod.GET)
	public String getRecruit(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		AnnouncementsVo vo=null;
		try {
			String hql = "from Announcements where  status=0 and  type="+SystemConstants.common_type_zhaoshengjihua;
			if (StringUtils.isNotBlank(uuid)){
				hql += " and  groupuuid in("+DBUtil.stringsToWhereInValue(uuid)+")";
			}
			hql+=" order by create_time desc";
			List list= nSimpleHibernateDao.getHibernateTemplate().find(hql);
			if(list==null||list.size()==0){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "/404";
			}
			Announcements a=(Announcements)list.get(0);
			vo=new AnnouncementsVo();
			
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String cur_user_uuid=null;
			if(user!=null){
				cur_user_uuid=user.getUuid();
			}
				BeanUtils.copyProperties(vo, a);
				announcementsService.warpVo(vo, cur_user_uuid);
			model.put("group",CommonsCache.get(a.getGroupuuid(), Group4Q.class));
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(a.getUuid(), SystemConstants.common_type_zhaoshengjihua));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "/404";
		}
		model.addAttribute(RestConstants.Return_G_entity,vo);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/getRecruitBygroupuuid";
	}
	/**
	 * 
	 * @param uuids
	 * @return
	 * 食材uuid$图片uuid$食材名字
	 */
	private List getCookbookList(String uuids) {
		List list=new ArrayList();
		if (StringUtils.isNotBlank(uuids)) {
			String[] uuid = uuids.split(",");
			for (String s : uuid) {
				Cookbook cb = (Cookbook) CommonsCache.get(s,Cookbook.class);
				list.add(cb);

			}
		}
		return list;

	}
	
	
	/**
	 * 老师详细信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getTeacherInfo", method = RequestMethod.GET)
	public String getTeacherInfo(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		if(StringUtils.isBlank(uuid)){
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("uuid 不能为空!");
			return "";
		}
		User4Q a=null;
		try {
			a=(User4Q)CommonsCache.get(uuid, User4Q.class);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "";
			}
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
	
	
	/**
	 * 获取所有幼儿园列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/allKDGroupList", method = RequestMethod.GET)
	public String allKDGroupList(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
//			String uuid_not_in=SystemConstants.Group_uuid_wjd+",group_wj1,group_wj2";
//			String hql = "from Group4Q where type=1 and status=9 and uuid not in("+DBUtil.stringsToWhereInValue(uuid_not_in)+")";
			String hql = "from Group4Q where type="+SystemConstants.Group_type_1+" and status=9";
			
			hql += " order by create_time asc";
			PageQueryResult pageQueryResult = this.nSimpleHibernateDao
					.findByPaginationToHql(hql, pData);
			groupService.warpVoList(pageQueryResult.getData());
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "/404";
		}
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/allKDGroupList";
	}
	
	/**
	 * 获取所有幼儿园列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/allPxGroupList", method = RequestMethod.GET)
	public String allPxGroupList(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
//			String uuid_not_in=SystemConstants.Group_uuid_wjd+",group_wj1,group_wj2";
//			String hql = "from Group4Q where type=1 and status=9 and uuid not in("+DBUtil.stringsToWhereInValue(uuid_not_in)+")";
			String hql = "from Group4Q where type="+SystemConstants.Group_type_2+" and status=9";
			hql += " order by create_time asc";
			PageQueryResult pageQueryResult = this.nSimpleHibernateDao
					.findByPaginationToHql(hql, pData);
			groupService.warpVoList(pageQueryResult.getData());
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "/404";
		}
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/allKDGroupList";
	}
	
	 /**
	 * 获取培训课程分类(用于培训对外发布课程分类）
	 *	/share/getCourseType.json
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getCourseType", method = RequestMethod.GET)
	public String getCourseType(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			
			
			Session s = this.nSimpleHibernateDao.getHibernateTemplate()
					.getSessionFactory().openSession();
			//description ios 的关键词,不使用.
			String sql=" SELECT datakey ,datavalue,description as img";
			sql+=" from px_basedatalist where typeuuid='course_type' and enable=1 order by datakey asc ";
			
			Query q = s.createSQLQuery(sql);
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map> list= q.list();
			
			String share_url_course_type=ProjectProperties.getProperty("share_url_course_type", "http://img.wenjienet.com/i/course_type/");
			for(Map o:list){
				//o.getDescription()=laugh.gif
				o.put("img", share_url_course_type+o.get("img"));
			}
			
			model.addAttribute(RestConstants.Return_ResponseMessage_list,list);
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
	 * 获取优惠活动列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/pxbenefitList", method = RequestMethod.GET)
	public String pxbenefitList(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			PaginationData pData = this.getPaginationDataByRequest(request);
			
				String mappoint = request.getParameter("map_point");
			if(DBUtil.isSqlInjection(mappoint, responseMessage))return "";
			
			//sort	 否	排序.取值: intelligent(智能排序). appraise(评价最高).distance(距离最近)
			String sort = request.getParameter("sort");
			if(DBUtil.isSqlInjection(sort, responseMessage))return "";
			
			PageQueryResult list = announcementsService.pxbenefitListByPage(pData,mappoint,sort);
			
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
	 * 获取精品文章详细
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getPxbenefitJSON", method = RequestMethod.GET)
	public String getPxbenefitJSON(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String uuid=request.getParameter("uuid");
		if(StringUtils.isBlank(uuid)){
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("uuid 不能为空!");
			return "";
		}
		AnnouncementsVo  vo = new AnnouncementsVo();
		try {
			Announcements4Q a = (Announcements4Q)nSimpleHibernateDao.getObject(Announcements4Q.class,uuid);
			if(a==null){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(ResponseMessageConstants.Data_deleted);
				return "";
			}
			
			SessionUserInfoInterface user = this.getUserInfoBySession(request);
			String cur_user_uuid=null;
			if(user!=null){
				cur_user_uuid=user.getUuid();
			}
				BeanUtils.copyProperties(vo, a);
				announcementsService.warpNoReplyVo(vo, cur_user_uuid);
				if(StringUtils.isBlank(vo.getUrl())){
					vo.setUrl(PxStringUtil.getArticleByUuid(uuid));
				}else{
					model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_pxbenefit));
					
				}
				model.put(RestConstants.Return_ResponseMessage_share_url,vo.getUrl());
				//在
//			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_pxbenefit));
			 model.put(RestConstants.Return_ResponseMessage_link_tel,announcementsService.getGroupLink_tel( a.getGroupuuid()));
			model.put(RestConstants.Return_ResponseMessage_isFavorites,favoritesService.isFavorites( cur_user_uuid,uuid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,vo);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
	
	String Md5_getConfig=null;
	/**
	 * 获取系统参数
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getConfig", method = RequestMethod.GET)
	public String getConfig(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			
			String md5=request.getParameter(RestConstants.Return_ResponseMessage_md5);
			if(StringUtils.isNotBlank(md5)){
				if(md5.equals(Md5_getConfig)){
					model.clear();
					 responseMessage = RestUtil
							.addResponseMessageForModelMap(model);
					responseMessage.setStatus(RestConstants.Return_ResponseMessage_unchange);
					return "";
				}
			}
		
			Map map=new HashMap();
			//空字符串表示不启用话题.否则未话题的地址.
			map.put("sns_url",PxConfigCache.getConfig_sns_url());
			JSONObject o =new JSONObject();
			String dd=JSONUtils.getJsonString(map);
			
			Md5_getConfig=MD5Until.getMD5String(dd);
			model.put(RestConstants.Return_ResponseMessage_md5, Md5_getConfig);
			model.addAttribute(RestConstants.Return_G_entity, map);
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
	 * 获取表情列表(url)
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getHtmlTitle", method = RequestMethod.GET)
	public String getHtmlTitle(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		String url=request.getParameter("url");
		try {

			String title=HttpRequestUtils.httpGetHtmlTitle(url);
			if(title==null)title="";
			title="[链接]"+title;
			
			model.addAttribute(RestConstants.Return_G_entity,title);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
	}
	
	
	
	@Autowired
	private FPPhotoItemService fPPhotoItemService;
	
		@RequestMapping("/getFPMovie")  
	    public void getFPMovie(ModelMap model, HttpServletRequest request,HttpServletResponse response,PaginationData pData) {  
	       try {  
	    	   model.clear();
	    	   ResponseMessage responseMessage = RestUtil
	   				.addResponseMessageForModelMap(model);
	    	   
	    	  // String jsonpCallback = request.getParameter("jsonpCallback");//客户端请求参数  
	    	   String movie_uuid = request.getParameter("movie_uuid");//客户端请求参数  
		    	  
	    	   
	    	   FPMovie fPMovie= (FPMovie)this.nSimpleHibernateDao.getObject(FPMovie.class, movie_uuid);
	    	   if(fPMovie==null){
	    		   responseMessage.setMessage("动态相册不存在！");
	    		   HttpRequestUtils.responseJSONP(model, response, "getFPMovie");
	    		   return;
	    	   }
	    	   
	    	   
	    	   this.nSimpleHibernateDao.getHibernateTemplate().evict(fPMovie);
	    	   pData.setPageSize(999);
	    	   PageQueryResult pageQueryResult=  fPPhotoItemService.queryForMoviePhoto_uuids(fPMovie.getPhoto_uuids(), pData, model);
	    	   fPMovie.setPhoto_uuids(null);
	    	   fPMovie.setHerald(FPPhotoUtil.imgUrlByUuid_sub(fPMovie.getHerald()));
	    	   model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
	    	   model.addAttribute(RestConstants.Return_G_entity,fPMovie);
	    	   
	    	   
	    	   responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
	    	 HttpRequestUtils.responseJSONP(model, response, "getFPMovie");
	      } catch (IOException e) {  
	       e.printStackTrace();  
	      }  
	    }  
}
