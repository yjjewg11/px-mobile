package com.company.news.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.DistanceUtil;
import com.company.news.entity.Group;
import com.company.news.entity.PxCourse;
import com.company.news.entity.PxCourse4Q;
import com.company.news.entity.User;
import com.company.news.jsonform.PxCourseJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class PxCourseService extends AbstractService {


	/**
	 * 查询所有班级
	 * 
	 * @return
	 */
	public List<PxCourse> query(String begDateStr, String endDateStr,
			String classuuid) {
	

		Date begDate = TimeUtils.string2Timestamp(null, begDateStr);

		Date endDate = TimeUtils.string2Timestamp(null, endDateStr);

		return (List<PxCourse>) this.nSimpleHibernateDao
				.getHibernateTemplate()
				.find("from PxCourse where classuuid=? and plandate<=? and plandate >=?  order by plandate asc",
						classuuid, endDate, begDate);
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public PxCourse get(String uuid) {
		PxCourse t = (PxCourse) this.nSimpleHibernateDao.getObjectById(
				PxCourse.class, uuid);
		
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
					"delete from PxCourse where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(PxCourse.class, uuid);

		}

		return true;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * 
	 * @param groupuuid
	 * @param pData
	 * @param point
	 * @return
	 */
	public PageQueryResult queryByPage(String groupuuid, PaginationData pData,String point) {
		String hql = "from PxCourse4Q  where  status=0 ";
		if(StringUtils.isNotBlank(groupuuid)){
			hql+=" and groupuuid in(" + DBUtil.stringsToWhereInValue(groupuuid) + ")";
		}
		hql += " order by updatetime desc ";

		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPaginationToHql(hql, pData);
		List<PxCourse4Q> list=pageQueryResult.getData();
		for(PxCourse4Q pxCourse4Q:list)
		{
			Group group=(Group) CommonsCache.get(pxCourse4Q.getGroupuuid(), Group.class);
			pxCourse4Q.setGroup(group);
			
			//当课程LOGO为空时，取机构的LOGO
			if(StringUtils.isBlank(pxCourse4Q.getLogo()))
				pxCourse4Q.setLogo(group.getImg());
		
			//当前坐标点参数不为空时，进行距离计算
			if(StringUtils.isNotBlank(point))
				pxCourse4Q.setDistance(DistanceUtil.getDistance(point, group.getMap_point()));
		}
		
		return pageQueryResult;
	}

}
