package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Announcements;
import com.company.news.entity.Announcements4Q;
import com.company.news.entity.AnnouncementsTo;
import com.company.news.entity.PClass;
import com.company.news.entity.User;
import com.company.news.jsonform.AnnouncementsJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.AnnouncementsVo;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class AnnouncementsService extends AbstractServcice {
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

		String hql = "from Announcements4Q where groupuuid='" + groupuuid + "'";
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

		String classuuids = "";
		String classnames = "";

		//当类型是通知班级时
		if (announcements.getType().intValue() == announcements_type_class) {
			List<AnnouncementsTo> list = (List<AnnouncementsTo>) this.nSimpleHibernateDao
					.getHibernateTemplate().find(
							"from AnnouncementsTo where announcementsuuid=?",
							uuid);

			for (AnnouncementsTo announcementsTo : list) {
				PClass p = CommonsCache
						.getClass(announcementsTo.getClassuuid());
				if (p != null) {
					classuuids += (p.getUuid() + ",");
					classnames += (p.getName() + ",");
				}
			}
		}
		AnnouncementsVo a = new AnnouncementsVo();
		BeanUtils.copyProperties(a, announcements);

		a.setClassnames(PxStringUtil.StringDecComma(classnames));
		a.setClassuuids(PxStringUtil.StringDecComma(classuuids));

		return a;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

}
