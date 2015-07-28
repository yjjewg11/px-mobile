package com.company.news.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.entity.PushMessage;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class PushMessageService extends AbstractServcice {
	public static final int announcements_isread_yes = 1;// 已读
	public static final int announcements_isread_no = 0;// 未读
	public static final int announcements_isdelete_yes = 1;// 已读
	public static final int announcements_isdelete_no = 0;// 未读

	/**
	 * 查询所有通知
	 * 
	 * @return
	 */
	public PageQueryResult query(String type, String useruuid,PaginationData pData) {

		String hql = "from PushMessage where revice_useruuid='" + useruuid + "'";
		if (StringUtils.isNotBlank(type))
			hql += " and type=" + type;
		hql += " order by create_time desc";
		
		
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHql(hql, pData);
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
//
//		if (uuid.indexOf(",") != -1)// 多ID
//		{
//			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
//					"update PushMessage set isdelete=? where uuid in(?)",
//					announcements_isdelete_yes, uuid);
//		} else {
//			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
//					"update PushMessage set isdelete=? where uuid =?",
//					announcements_isdelete_yes, uuid);
//		}

		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public PushMessage get(String uuid) throws Exception {
		PushMessage message = (PushMessage) this.nSimpleHibernateDao.getObjectById(
				PushMessage.class, uuid);

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
					"update PushMessage set isread=? where uuid in(?)",
					announcements_isread_yes, uuid);
		} else {
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"update PushMessage set isread=? where uuid =?",
					announcements_isread_yes, uuid);
		}

		return true;
	}
	
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
