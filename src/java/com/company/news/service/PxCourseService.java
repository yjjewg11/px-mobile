package com.company.news.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.commons.util.DistanceUtil;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.PxCourse;
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

	public PxCourse warpVo(PxCourse o){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		//自适应
		o.setContext(PxStringUtil.warpHtml5Responsive(o.getContext()));
		o.setLogo(PxStringUtil.imgSmallUrlByUuid(o.getLogo()));
		return o;
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
	 * 
	 * 	//sort	 否	排序.取值: intelligent(智能排序). appraise(评价最高).distance(距离最近)
			String sort = request.getParameter("sort");
	 */
	public PageQueryResult queryByPage(String groupuuid, String type,PaginationData pData,String point,String teacheruuid ,String sort ) {
//		String hql = "from PxCourse4Q  where  status=0 ";
//		if(StringUtils.isNotBlank(groupuuid)){
//			hql+=" and groupuuid in(" + DBUtil.stringsToWhereInValue(groupuuid) + ")";
//		}
//		hql += " order by updatetime desc ";
//
//		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPaginationToHql(hql, pData);
//		List<PxCourse4Q> list=pageQueryResult.getData();
//		
//		
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql=" SELECT t1.uuid,t1.logo,t2.img as group_img,t1.title,t1.ct_stars,t1.ct_study_students,t1.updatetime,t2.brand_name as group_name,t2.map_point,t2.address";
		sql+=" FROM px_pxcourse t1 ";
		sql+=" LEFT JOIN  px_group t2 on t1.groupuuid=t2.uuid ";
		sql+=" where  t1.status="+SystemConstants.PxCourse_status_fabu;
		
		if(StringUtils.isNotBlank(groupuuid)){
			sql+=" and t1.groupuuid in(" + DBUtil.stringsToWhereInValue(groupuuid) + ")";
		}
		if(StringUtils.isNotBlank(type)){
			sql+=" and t1.type="+type;
		}
		if(StringUtils.isNotBlank(teacheruuid)){
			sql+=" and t1.uuid in ( select courseuuid  from px_userpxcourserelation where useruuid ='" + teacheruuid + "')";
		}
		
		
		double[] lngLatArr=null;
		if(StringUtils.isNotBlank(point)){
			lngLatArr=DistanceUtil.getLongitudeAndLatitude(point);
		}
		
		if("distance".equals(sort)&&lngLatArr!=null){
			double lng1=lngLatArr[0];
			double lat1=lngLatArr[1];
			//String range_of_distance=ProjectProperties.getProperty("range_of_distance","20");
			
			sql+=" order by ACOS(SIN(("+lat1+" * 3.1415) / 180 ) *SIN((t2.lat * 3.1415) / 180 ) " +
					"+COS(("+lat1+" * 3.1415) / 180 ) * COS((t2.lat * 3.1415) / 180 ) " +
							"*COS(("+lng1+"* 3.1415) / 180 - (t2.lng * 3.1415) / 180 ) ) * 6380 asc";
			
//			sql+=" order by t1.updatetime asc";
		}else if("appraise".equals(sort)){//
			sql+=" order by t1.ct_stars desc,t1.ct_study_students desc";
		}else{
			sql+=" order by t1.updatetime asc";
		}
		
		
		Query q = s.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		PageQueryResult pageQueryResult =this.nSimpleHibernateDao.findByPageForSqlNoTotal(q, pData);
		List<Map> list=pageQueryResult.getData();
		for(Map pxCourse4Q:list)
		{
			//当课程LOGO为空时，取机构的LOGO
			if(StringUtils.isBlank((String)pxCourse4Q.get("logo")))
				pxCourse4Q.put("logo", pxCourse4Q.get("group_img"));
			pxCourse4Q.put("logo", PxStringUtil.imgSmallUrlByUuid((String)pxCourse4Q.get("logo")));
			
			//当前坐标点参数不为空时，进行距离计算
			if(StringUtils.isNotBlank(point)){
				pxCourse4Q.put("distance", DistanceUtil.getDistance(point, (String)pxCourse4Q.get("map_point")));
			}else{
				pxCourse4Q.put("distance", "");
			}
		}
		
		return pageQueryResult;
	}

}
