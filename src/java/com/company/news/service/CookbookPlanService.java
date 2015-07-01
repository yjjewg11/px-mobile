package com.company.news.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Cookbook;
import com.company.news.entity.CookbookPlan;
import com.company.news.entity.Group;
import com.company.news.entity.PClass;
import com.company.news.entity.Right;
import com.company.news.entity.User;
import com.company.news.entity.UserClassRelation;
import com.company.news.entity.UserGroupRelation;
import com.company.news.jsonform.ClassRegJsonform;
import com.company.news.jsonform.CookbookPlanJsonform;
import com.company.news.jsonform.GroupRegJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class CookbookPlanService extends AbstractServcice {

	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(CookbookPlanJsonform cookbookPlanJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(cookbookPlanJsonform.getGroupuuid())) {
			responseMessage.setMessage("Groupuuid不能为空！");
			return false;
		}

		if (StringUtils.isBlank(cookbookPlanJsonform.getPlandateStr())) {
			responseMessage.setMessage("Plandate不能为空！");
			return false;
		}

		Date plandate = TimeUtils.string2Timestamp(null,
				cookbookPlanJsonform.getPlandateStr());

		if (plandate == null) {
			responseMessage.setMessage("Plandate格式不正确");
			return false;
		}

		CookbookPlan cookbookPlan = this.getByPlandateAndGroupuuid(plandate,
				cookbookPlanJsonform.getGroupuuid());

		if (cookbookPlan == null)
			cookbookPlan = new CookbookPlan();

		BeanUtils.copyProperties(cookbookPlan, cookbookPlanJsonform);
		cookbookPlan.setPlandate(plandate);
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().saveOrUpdate(
				cookbookPlan);

		return true;
	}

	/**
	 * 
	 * @param plandate
	 * @param groupuuid
	 * @return
	 */
	private CookbookPlan getByPlandateAndGroupuuid(Date plandate,
			String groupuuid) {
		List<CookbookPlan> l = (List<CookbookPlan>) this.nSimpleHibernateDao
				.getHibernateTemplate().find(
						"from CookbookPlan where plandate=? and groupuuid=?",
						plandate, groupuuid);
		if (l != null && l.size() > 0)
			return l.get(0);
		else
			return null;
	}

	/**
	 * 查询所有班级
	 * 
	 * @return
	 */
	public List<CookbookPlan> query(String begDateStr, String endDateStr,
			String groupuuid) {
		if (StringUtils.isBlank(groupuuid)) {
			return null;
		}

		if (StringUtils.isBlank(begDateStr)) {
			return null;
		}

		if (StringUtils.isBlank(endDateStr)) {
			return null;
		}

		Date begDate = TimeUtils.string2Timestamp(null, begDateStr);

		Date endDate = TimeUtils.string2Timestamp(null, endDateStr);
		
		List<CookbookPlan> list=(List<CookbookPlan>) this.nSimpleHibernateDao
				.getHibernateTemplate()
				.find("from CookbookPlan where groupuuid=? and plandate<=? and plandate >=?  order by plandate asc",
						groupuuid, endDate, begDate);
		this.nSimpleHibernateDao.getHibernateTemplate().clear();
		for(CookbookPlan c:list){
			c.setTime_1(this.makeCookbookName(c.getTime_1()));
			c.setTime_2(this.makeCookbookName(c.getTime_2()));
			c.setTime_3(this.makeCookbookName(c.getTime_3()));
			c.setTime_4(this.makeCookbookName(c.getTime_4()));
			c.setTime_5(this.makeCookbookName(c.getTime_5()));			
		}
		
		return list;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public CookbookPlan get(String uuid) {
		CookbookPlan c = (CookbookPlan) this.nSimpleHibernateDao.getObjectById(
				CookbookPlan.class, uuid);
		this.nSimpleHibernateDao.getHibernateTemplate().clear();
		if (c != null) {
			c.setTime_1(this.makeCookbookName(c.getTime_1()));
			c.setTime_2(this.makeCookbookName(c.getTime_2()));
			c.setTime_3(this.makeCookbookName(c.getTime_3()));
			c.setTime_4(this.makeCookbookName(c.getTime_4()));
			c.setTime_5(this.makeCookbookName(c.getTime_5()));

		}
		return c;

	}

	/**
	 * 
	 * @param uuids
	 * @return
	 * 食材uuid$图片uuid$食材名字
	 */
	private String makeCookbookName(String uuids) {
		String rs = "";
		if (StringUtils.isNotBlank(uuids)) {
			String[] uuid = uuids.split(",");
			for (String s : uuid) {
				Cookbook cb = CommonsCache.getCookbook(s);
				if (cb != null)
					rs += (s+"$"+cb.getImg() + "$" + cb.getName() + ",");

			}

		}

		return PxStringUtil.StringDecComma(rs);

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
					"delete from CookbookPlan where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(CookbookPlan.class, uuid);

		}

		return true;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

}
