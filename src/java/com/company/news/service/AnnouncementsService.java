package com.company.news.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.DistanceUtil;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Announcements;
import com.company.news.entity.Announcements4Q;
import com.company.news.entity.AnnouncementsTo;
import com.company.news.entity.ClassNews;
import com.company.news.entity.PClass;
import com.company.news.entity.Parent;
import com.company.news.entity.User;
import com.company.news.jsonform.AnnouncementsJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.AnnouncementsVo;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class AnnouncementsService extends AbstractService {
	public static final int announcements_type_class = 2;// 班级
	public static final int announcements_type_general = 0;// 默认公开通知
	public static final int announcements_type_secret = 1;// 机密

	/**
	 * 增加
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(AnnouncementsJsonform announcementsJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(announcementsJsonform.getTitle())
				|| announcementsJsonform.getTitle().length() > 45) {
			responseMessage.setMessage("Title不能为空！，且长度不能超过45位！");
			return false;
		}

		if (StringUtils.isBlank(announcementsJsonform.getMessage())) {
			responseMessage.setMessage("Message不能为空！");
			return false;
		}

		if (StringUtils.isBlank(announcementsJsonform.getGroupuuid())) {
			responseMessage.setMessage("Groupuuid不能为空！");
			return false;
		}

		Announcements announcements = new Announcements();

		BeanUtils.copyProperties(announcements, announcementsJsonform);

		announcements.setCreate_time(TimeUtils.getCurrentTimestamp());

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(announcements);

		// 如果类型是班级通知
		if (announcements.getType().intValue() == this.announcements_type_class) {
			if (StringUtils.isBlank(announcementsJsonform.getClassuuids())) {
				responseMessage.setMessage("Classuuids不能为空！");
				return false;
			}

			String[] classuuid = announcementsJsonform.getClassuuids().split(
					",");
			for (String s : classuuid) {
				// 保存用户机构关联表
				AnnouncementsTo announcementsTo = new AnnouncementsTo();
				announcementsTo.setClassuuid(s);
				announcementsTo.setAnnouncementsuuid(announcements.getUuid());
				// 有事务管理，统一在Controller调用时处理异常
				this.nSimpleHibernateDao.getHibernateTemplate().save(
						announcementsTo);
			}
		}

		return true;
	}

	/**
	 * 增加机构
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean update(AnnouncementsJsonform announcementsJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(announcementsJsonform.getTitle())
				|| announcementsJsonform.getTitle().length() > 45) {
			responseMessage.setMessage("Title不能为空！，且长度不能超过45位！");
			return false;
		}

		if (StringUtils.isBlank(announcementsJsonform.getMessage())) {
			responseMessage.setMessage("Message不能为空！");
			return false;
		}

		if (StringUtils.isBlank(announcementsJsonform.getUuid())) {
			responseMessage.setMessage("uuid不能为空！");
			return false;
		}

		Announcements announcements = (Announcements) this.nSimpleHibernateDao
				.getObjectById(Announcements.class,
						announcementsJsonform.getUuid());

		announcements.setIsimportant(announcementsJsonform.getIsimportant());
		announcements.setMessage(announcementsJsonform.getMessage());
		announcements.setTitle(announcementsJsonform.getTitle());
		announcements.setType(announcementsJsonform.getType());
		announcements.setGroupuuid(announcementsJsonform.getGroupuuid());

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().update(announcements);

		// 删除原来已发通知
		this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"delete from AnnouncementsTo where announcementsuuid=?",
				announcements.getUuid());

		// 如果类型是班级通知
		if (announcements.getType().intValue() == this.announcements_type_class) {
			if (StringUtils.isBlank(announcementsJsonform.getClassuuids())) {
				responseMessage.setMessage("Classuuids不能为空！");
				return false;
			}

			String[] classuuid = announcementsJsonform.getClassuuids().split(
					",");
			for (String s : classuuid) {
				// 保存用户机构关联表
				AnnouncementsTo announcementsTo = new AnnouncementsTo();
				announcementsTo.setClassuuid(s);
				announcementsTo.setAnnouncementsuuid(announcements.getUuid());
				// 有事务管理，统一在Controller调用时处理异常
				this.nSimpleHibernateDao.getHibernateTemplate().save(
						announcementsTo);
			}
		}

		return true;
	}

	/**
	 * 查询所有通知
	 * 
	 * @return
	 */
	public List query(String type, String groupuuid) {
		if (StringUtils.isBlank(groupuuid))
			return null;

		String hql = "from Announcements4Q where status="+SystemConstants.Check_status_fabu+" and groupuuid='" + groupuuid + "'";
		if (StringUtils.isNotBlank(type))
			hql += " and type=" + type;

		hql += " order by create_time";
		return (List) this.nSimpleHibernateDao.getHibernateTemplate().find(hql);
	}

	/**
	 * 查询指定班级的通知列表
	 * 
	 * @return
	 */
	private List<Announcements> getAnnouncementsByClassuuid(String classuuid) {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = "";
		Query q = s
				.createSQLQuery(
						"select {t1.*} from px_announcementsto t0,px_announcements {t1} where t0.announcementsuuid={t1}.uuid and t0.classuuid='"
								+ classuuid + "' order by {t1}.create_time")
				.addEntity("t1", Announcements4Q.class);

		return q.list();
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public List queryMyAnnouncements(String type, String groupuuid,
			String classuuid) {
		if (StringUtils.isBlank(type))
			return null;
		// 查询班级公告
		if (Integer.parseInt(type) == announcements_type_class) {
			if (StringUtils.isBlank(classuuid))
				return null;
			return getAnnouncementsByClassuuid(classuuid);

		} else {// 机构公告
			return query(type, groupuuid);
		}

	}

	
	

	/**
	 * 查询我公告
	 * 
	 * @return
	 */
	public PageQueryResult queryMyChildGroup(String useruuid, PaginationData pData) {
		
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql=" SELECT t1.uuid,t1.create_time,t1.create_user,t1.create_useruuid,t1.groupuuid,t1.title from px_announcements t1 where t1.type=0 and  t1.status="+SystemConstants.Check_status_fabu;
		sql+=" and (t1.groupuuid IN (select DISTINCT  t2.groupuuid from px_studentcontactrealation t2 where t2.parent_uuid='"+useruuid+"')";
		sql+=" or t1.groupuuid IN (select DISTINCT  t3.groupuuid from px_pxclass t3 inner JOIN px_pxstudentpxclassrelation t4 on t4.class_uuid=t3.uuid ";
		sql+=" inner join px_pxstudentcontactrealation t5 on  t5.student_uuid=t4.student_uuid where t5.parent_uuid='"+useruuid+"')";
		sql+=") ORDER BY t1.create_time desc";
		
		Query q = s.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		PageQueryResult pageQueryResult =this.nSimpleHibernateDao.findByPageForSqlNoTotal(q, pData);
		return pageQueryResult;

	}
	/**
	 * 查询我公告
	 * 
	 * @return
	 */
	public PageQueryResult query(String groupuuid, PaginationData pData) {
		String hql = "from Announcements4Q where  status="+SystemConstants.Check_status_fabu+" and type=0";
		if (StringUtils.isNotBlank(groupuuid)){
			hql += " and  groupuuid in("+DBUtil.stringsToWhereInValue(groupuuid)+")";
		}
		pData.setOrderFiled("create_time");
		pData.setOrderType("desc");
		
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHqlNoTotal(hql, pData);
		return pageQueryResult;

	}
	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean delete(String uuid, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(uuid)) {

			responseMessage.setMessage("ID不能为空！");
			return false;
		}

		if (uuid.indexOf(",") != -1)// 多ID
		{
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from Announcements where uuid in(?)", uuid);
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from AnnouncementsTo where classuuid in(?)", uuid);
		} else {
			this.nSimpleHibernateDao
					.deleteObjectById(Announcements.class, uuid);
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from AnnouncementsTo where classuuid =?", uuid);
		}

		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public AnnouncementsVo get(String uuid) throws Exception {
		Announcements announcements = (Announcements) this.nSimpleHibernateDao
				.getObjectById(Announcements.class, uuid);

//		String classuuids = "";
//		String classnames = "";
//		
//		//当类型是通知班级时
//		if (announcements.getType().intValue() == announcements_type_class) {
//			List<AnnouncementsTo> list = (List<AnnouncementsTo>) this.nSimpleHibernateDao
//					.getHibernateTemplate().find(
//							"from AnnouncementsTo where announcementsuuid=?",
//							uuid);
//
//			for (AnnouncementsTo announcementsTo : list) {
//				PClass p = (PClass) CommonsCache
//						.get(announcementsTo.getClassuuid(),PClass.class);
//				if (p != null) {
//					classuuids += (p.getUuid() + ",");
//					classnames += (p.getName() + ",");
//				}
//			}
//		}
		
		if(announcements==null)return null;
		AnnouncementsVo a = new AnnouncementsVo();
		BeanUtils.copyProperties(a, announcements);

//		a.setClassnames(PxStringUtil.StringDecComma(classnames));
//		a.setClassuuids(PxStringUtil.StringDecComma(classuuids));

		
		return a;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public List<Announcements4Q> warpVoList(List<Announcements4Q> list,String cur_user_uuid){
		for(Announcements4Q o:list){
			warpVo(o,cur_user_uuid);
		}
		return list;
	}
	
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public Announcements4Q warpVo(Announcements4Q o,String cur_user_uuid){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		try {
			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(), cur_user_uuid));
			if(StringUtils.isBlank(o.getUrl())){
				o.setUrl(PxStringUtil.getArticleByUuid(o.getUuid()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public AnnouncementsVo warpVo(AnnouncementsVo o,String cur_user_uuid){
		//this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		try {
			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(), cur_user_uuid));
			o.setReplyPage(this.getReplyPageListAndRelyDianzan(o.getUuid(),cur_user_uuid));
			if(StringUtils.isNotBlank(o.getUrl())){
				o.setMessage("请升级问界互动家园最新版本,才可以浏览."+o.getMessage());
			}
			o.setMessage(PxStringUtil.warpHtml5Responsive(o.getMessage()));
			if(StringUtils.isBlank(o.getUrl())){
				o.setUrl(PxStringUtil.getArticleByUuid(o.getUuid()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public AnnouncementsVo warpNoReplyVo(AnnouncementsVo o,String cur_user_uuid){
		//this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		try {
			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(), cur_user_uuid));
			//o.setReplyPage(this.getReplyPageListAndRelyDianzan(o.getUuid(),cur_user_uuid));
//			if(StringUtils.isNotBlank(o.getUrl())&&StringUtils.isBlank(o.getMessage())){
//				o.setMessage("请升级最新版本,才可以浏览.");
//			}
		//	o.setMessage(PxStringUtil.warpHtml5Responsive(o.getMessage()));
//			if(StringUtils.isBlank(o.getUrl())){
//				o.setUrl(PxStringUtil.getArticleByUuid(o.getUuid()));
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}
	 @Autowired
     private CountService countService ;
	public PageQueryResult pxbenefitListByPage(PaginationData pData,
			String mappoint, String sort) throws Exception {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String crrentTime=TimeUtils.getCurrentTime(TimeUtils.YYYY_MM_DD_FORMAT);
		String sql=" SELECT t1.uuid,t2.img as group_img,t1.title,t2.brand_name as group_name,t2.map_point";
		sql+=" FROM px_announcements t1 ";
		sql+=" LEFT JOIN  px_group t2 on t1.groupuuid=t2.uuid ";
		sql+=" where  t1.status=0 and t1.type="+SystemConstants.common_type_pxbenefit;
		sql+=" and start_time<="+DBUtil.stringToDateByDBType(crrentTime);
		sql+=" and end_time>="+DBUtil.stringToDateByDBType(crrentTime);
		sql+=" order by t1.create_time desc";
//		if("distance".equals(sort)){
//			sql+=" order by t1.create_time desc";
//		}else{
//			sql+=" order by t1.ct_stars desc,t1.ct_study_students desc";
//		}
		Query q = s.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		PageQueryResult pageQueryResult =this.nSimpleHibernateDao.findByPageForSqlNoTotal(q, pData);
		List<Map> list=pageQueryResult.getData();
		
		
		String uuids="";
		for(Map o:list){
			uuids+=o.get("uuid")+",";
		}
		
		Map countMap=countService.getCountByExt_uuids(uuids);
		for(Map pxCourse4Q:list)
		{
			
			pxCourse4Q.put("group_img", PxStringUtil.imgSmallUrlByUuid((String)pxCourse4Q.get("group_img")));
			
//			pxCourse4Q.put("count", countService.get((String)pxCourse4Q.get("uuid"), SystemConstants.common_type_pxbenefit));
			pxCourse4Q.put("count", countMap.get(pxCourse4Q.get("uuid")));
			//当前坐标点参数不为空时，进行距离计算
			if(StringUtils.isNotBlank(mappoint)){
				pxCourse4Q.put("distance", DistanceUtil.getDistance(mappoint, (String)pxCourse4Q.get("map_point")));
			}else{
				pxCourse4Q.put("distance", "");
			}
		}
		return pageQueryResult;
	}

}
