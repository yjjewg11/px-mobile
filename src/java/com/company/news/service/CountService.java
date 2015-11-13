package com.company.news.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.entity.Count;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class CountService extends AbstractService {
	// 20150610 去掉对用户表的TYPE定义，默认都为0
	public static final int count_type_classnews = 1;// 互动类

	
	/**
	 * 批量更新计数,提供效率
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public void update_countBatch(String ext_uuids) throws Exception {
		if (StringUtils.isBlank(ext_uuids)) {
			return ;
		}
		String sql = "update px_count set count=count+1,update_time=now() where ext_uuid in("+DBUtil.stringsToWhereInValue(ext_uuids)+")";
		this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(sql).executeUpdate();

	}
	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Count add(String ext_uuid, Integer type) throws Exception {
		if (StringUtils.isBlank(ext_uuid)) {
			return null;
		}
			Count	c = new Count();
		c.setType(type);
		c.setUpdate_time(TimeUtils.getCurrentTimestamp());
		c.setExt_uuid(ext_uuid);
		c.setCount(0l);

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(c);

		return c;
	}
	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Long count(String ext_uuid, Integer type) throws Exception {
		if (StringUtils.isBlank(ext_uuid)) {
			return 0l;
		}

		Count c = (Count) this.nSimpleHibernateDao.getObjectByAttribute(
				Count.class, "ext_uuid", ext_uuid);

		if (c == null)
			c = new Count();

		c.setType(type);
		c.setUpdate_time(TimeUtils.getCurrentTimestamp());
		c.setExt_uuid(ext_uuid);
		long count = 0;
		if (c.getCount() != null)
			count = c.getCount();

		c.setCount(++count);

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().saveOrUpdate(c);

		return count;
	}

	/**
	 * 更新班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Long get(String ext_uuid, Integer type) throws Exception {
		if (StringUtils.isBlank(ext_uuid)) {
			return 0l;
		}

		Count c = (Count) this.nSimpleHibernateDao.getObjectByAttribute(
				Count.class, "ext_uuid", ext_uuid);
		Long count = 0l;
		if (c != null)
			count = c.getCount();

		return count;

	}

	/**
	 * 查询所有班级
	 * 
	 * @return
	 */
	public void delete(String ext_uuid) {
		this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"delete from Count where ext_uuid =?", ext_uuid);
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
