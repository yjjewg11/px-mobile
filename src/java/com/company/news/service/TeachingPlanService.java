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
import com.company.news.entity.Teachingplan;
import com.company.news.entity.User;
import com.company.news.entity.UserClassRelation;
import com.company.news.entity.UserGroupRelation;
import com.company.news.jsonform.ClassRegJsonform;
import com.company.news.jsonform.CookbookPlanJsonform;
import com.company.news.jsonform.GroupRegJsonform;
import com.company.news.jsonform.TeachingPlanJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class TeachingPlanService extends AbstractServcice {

	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(TeachingPlanJsonform teachingPlanJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(teachingPlanJsonform.getPlandateStr())) {
			responseMessage.setMessage("plandateStr不能为空！");
			return false;
		}
		
		if (StringUtils.isBlank(teachingPlanJsonform.getClassuuid())) {
			responseMessage.setMessage("classuuid不能为空！");
			return false;
		}

		Date plandate = TimeUtils.string2Timestamp(null,teachingPlanJsonform.getPlandateStr());

		if (plandate == null) {
			responseMessage.setMessage("Plandate格式不正确");
			return false;
		}

		Teachingplan teachingplan = this.getByPlandateAndClassuuid(plandate,teachingPlanJsonform.getClassuuid());

		if (teachingplan == null)
			teachingplan = new Teachingplan();

		teachingplan.setAfternoon(teachingPlanJsonform.getAfternoon());
		teachingplan.setClassuuid(teachingPlanJsonform.getClassuuid());
		teachingplan.setPlandate(plandate);
		teachingplan.setMorning(teachingPlanJsonform.getMorning());
		teachingplan.setCreate_useruuid(teachingPlanJsonform.getCreate_useruuid());
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().saveOrUpdate(
				teachingplan);

		return true;
	}

	/**
	 * 
	 * @param plandate
	 * @param groupuuid
	 * @return
	 */
	private Teachingplan getByPlandateAndClassuuid(Date plandate,
			String classuuid) {
		List<Teachingplan> l = (List<Teachingplan>) this.nSimpleHibernateDao
				.getHibernateTemplate().find(
						"from Teachingplan where plandate=? and classuuid=?",
						plandate, classuuid);
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
	public List<Teachingplan> query(String begDateStr, String endDateStr,
			String classuuid) {
		if (StringUtils.isBlank(classuuid)) {
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

		return (List<Teachingplan>) this.nSimpleHibernateDao
				.getHibernateTemplate()
				.find("from Teachingplan where classuuid=? and plandate<=? and plandate >=?  order by plandate asc",
						classuuid, endDate, begDate);
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Teachingplan get(String uuid) {
		Teachingplan t = (Teachingplan) this.nSimpleHibernateDao.getObjectById(
				Teachingplan.class, uuid);
		
		return t;

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
					"delete from Teachingplan where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(Teachingplan.class, uuid);

		}

		return true;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

}
