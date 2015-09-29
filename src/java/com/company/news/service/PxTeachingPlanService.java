package com.company.news.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.entity.PxTeachingplan;
import com.company.news.entity.User;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class PxTeachingPlanService extends AbstractService {
	private static final String model_name = "培训结构教学计划模块";


	/**
	 * 查询所有班级
	 * 
	 * @return
	 */
	public PageQueryResult query(String begDateStr, String endDateStr,
			String classuuid,PaginationData pData) {
		if (StringUtils.isBlank(classuuid)) {
			return null;
		}
		String hql="from PxTeachingplan where classuuid='"+classuuid+"' ";
		if (StringUtils.isNotBlank(begDateStr)) {
			hql+=" and  plandate>="+DBUtil.stringToDateByDBType(begDateStr);
		}
		if (StringUtils.isNotBlank(endDateStr)) {
			endDateStr=endDateStr.split(" ")[0]+" 23:59:59";
			hql+=" and  plandate<="+DBUtil.stringToDateByDBType(endDateStr);
		}
		pData.setOrderFiled("plandate");
		pData.setOrderType(PaginationData.SORT_ASC);
		
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPaginationToHql(hql, pData);
		
		
		return pageQueryResult;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public PxTeachingplan get(String uuid) {
		PxTeachingplan t = (PxTeachingplan) this.nSimpleHibernateDao.getObjectById(
				PxTeachingplan.class, uuid);
		
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
					"delete from PxTeachingplan where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(PxTeachingplan.class, uuid);

		}

		return true;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

}
