package com.company.news.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.dao.NSimpleHibernateDao;
import com.company.news.entity.Announcements;
import com.company.news.entity.BaseDataList;
import com.company.news.entity.ClassNews;
import com.company.news.entity.Cookbook;
import com.company.news.entity.CookbookPlan;
import com.company.news.entity.Group;
import com.company.news.entity.PClass;
import com.company.news.rest.util.RestUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.service.CountService;
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
				List<BaseDataList> list= (List<BaseDataList>) this.nSimpleHibernateDao
						.getHibernateTemplate().find(
								"from BaseDataList where typeuuid='emot' and enable=1 order by datakey asc");
				this.nSimpleHibernateDao
				.getHibernateTemplate().clear();
				String share_url_getEmot=ProjectProperties.getProperty("share_url_getEmot", "http://120.25.248.31/px-rest/w/xheditor/xheditor_emot/default/");
				for(BaseDataList o:list){
					//o.getDescription()=laugh.gif
					o.setDescription(share_url_getEmot+o.getDescription());
				}
				
				model.addAttribute(RestConstants.Return_ResponseMessage_list,list);
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
				responseMessage.setMessage("数据不存在.");
				return "/404";
			}
			model.put("group",CommonsCache.get(a.getGroupuuid(), Group.class));

			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_gonggao));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "/404";
		}
		model.addAttribute(RestConstants.Return_G_entity,a);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/getAnn";
	}
	

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
				responseMessage.setMessage("数据不存在.");
				return "/404";
			}
			
			PClass pClass=(PClass)CommonsCache.get(a.getClassuuid(), PClass.class);
			model.put("group",CommonsCache.get(pClass.getGroupuuid(), Group.class));
			model.put("pclass",CommonsCache.get(a.getClassuuid(), PClass.class));

			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_hudong));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
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
				responseMessage.setMessage("数据不存在.");
				return "/404";
			}
			
			

			a.setList_time_1(this.getCookbookList(a.getTime_1()));
			a.setList_time_2(this.getCookbookList(a.getTime_2()));
			a.setList_time_3(this.getCookbookList(a.getTime_3()));
			a.setList_time_4(this.getCookbookList(a.getTime_4()));
			a.setList_time_5(this.getCookbookList(a.getTime_5()));
			
			String G_imgPath=ProjectProperties.getProperty("img_down_url_pre", "http://localhost:8080/px-moblie/rest/uploadFile/getImgFile.json?uuid={uuid}");
			model.put("G_imgPath",G_imgPath.replace("{uuid}", ""));
			model.put("group",CommonsCache.get(a.getGroupuuid(), Group.class));
			model.put("plandate",TimeUtils.getDateString(a.getPlandate()));
			model.put(RestConstants.Return_ResponseMessage_count, countService.count(uuid, SystemConstants.common_type_shipu));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "/404";
		}
		model.addAttribute(RestConstants.Return_G_entity,a);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "/getCookbookPlan";
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
}
