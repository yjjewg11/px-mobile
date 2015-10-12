package com.company.news.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.DistanceUtil;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Appraise;
import com.company.news.entity.Group;
import com.company.news.entity.PxCourse4Q;
import com.company.news.entity.TeacherJudge;
import com.company.news.entity.User;
import com.company.news.jsonform.AppraiseJsonform;
import com.company.news.jsonform.TeachingJudgeJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;
import com.company.news.vo.TeacherPhone;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class AppraiseService extends AbstractService {

	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(AppraiseJsonform appraiseJsonform,
			ResponseMessage responseMessage) throws Exception {

		if (this.validateRequireByRegJsonform(appraiseJsonform.getExt_uuid(),
				"Ext_uuid", responseMessage)
				|| this.validateRequireByRegJsonform(
						appraiseJsonform.getScore(), "Score", responseMessage)
				|| this.validateRequireByRegJsonform(
						appraiseJsonform.getType(), "type", responseMessage)) {
			return false;
		}

		Appraise appraise = new Appraise();

		BeanUtils.copyProperties(appraise, appraiseJsonform);
		appraise.setCreate_time(TimeUtils.getCurrentTimestamp());

		this.nSimpleHibernateDao.getHibernateTemplate().save(appraise);

		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Appraise get(String uuid) {
		Appraise t = (Appraise) this.nSimpleHibernateDao.getObjectById(
				Appraise.class, uuid);

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
					"delete from Appraise where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(Appraise.class, uuid);

		}

		return true;
	}

	/**
	 * 
	 * @param groupuuid
	 * @param pData
	 * @param point
	 * @return
	 */
	public PageQueryResult queryByPage(String ext_uuid, PaginationData pData) {

		if (StringUtils.isBlank(ext_uuid)) {
			return null;
		}
		String hql = "from Appraise where ext_uuid='" + ext_uuid
				+ "' order by create_time";

		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHql(hql, pData);

		return pageQueryResult;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return Appraise.class;
	}

}
