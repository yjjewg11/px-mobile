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
import com.company.news.entity.Message;
import com.company.news.entity.PClass;
import com.company.news.entity.User;
import com.company.news.jsonform.AnnouncementsJsonform;
import com.company.news.jsonform.MessageJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.AnnouncementsVo;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class MessageService extends AbstractServcice {
	public static final int announcements_isread_yes = 1;// 已读
	public static final int announcements_isread_no = 0;// 未读
	public static final int announcements_isdelete_yes = 1;// 已读
	public static final int announcements_isdelete_no = 0;// 未读

	/**
	 * 增加
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(MessageJsonform messageJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(messageJsonform.getTitle())
				|| messageJsonform.getTitle().length() > 45) {
			responseMessage.setMessage("Title不能为空！，且长度不能超过45位！");
			return false;
		}

		if (StringUtils.isBlank(messageJsonform.getRevice_useruuid())) {
			responseMessage.setMessage("Revice_useruuid不能为空！");
			return false;
		}

		User user = (User) CommonsCache.get(messageJsonform.getRevice_useruuid(),User.class);
		if (user == null) {
			responseMessage.setMessage("user 不存在！");
			return false;
		}
		messageJsonform.setRevice_user(user.getName());

		Message message = new Message();

		BeanUtils.copyProperties(message, messageJsonform);

		message.setCreate_time(TimeUtils.getCurrentTimestamp());
		message.setIsread(announcements_isread_no);
		message.setIsdelete(announcements_isdelete_no);

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(message);

		return true;
	}

	/**
	 * 查询所有通知
	 * 
	 * @return
	 */
	public List query(String type, String useruuid) {

		String hql = "from Message where isdelete=" + announcements_isdelete_no;
		if (StringUtils.isNotBlank(type))
			hql += " and type=" + type;
		if (StringUtils.isNotBlank(useruuid))
			hql += " and revice_useruuid='" + useruuid + "'";
		hql += " order by create_time desc";
		return (List) this.nSimpleHibernateDao.getHibernateTemplate().find(hql);
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
					"update Message set isdelete=? where uuid in(?)",
					announcements_isdelete_yes, uuid);
		} else {
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"update Message set isdelete=? where uuid =?",
					announcements_isdelete_yes, uuid);
		}

		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public Message get(String uuid) throws Exception {
		Message message = (Message) this.nSimpleHibernateDao.getObjectById(
				Message.class, uuid);

		return message;
	}

	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean read(String uuid, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(uuid)) {

			responseMessage.setMessage("ID不能为空！");
			return false;
		}

		if (uuid.indexOf(",") != -1)// 多ID
		{
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"update Message set isread=? where uuid in(?)",
					announcements_isread_yes, uuid);
		} else {
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"update Message set isread=? where uuid =?",
					announcements_isread_yes, uuid);
		}

		return true;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

}
