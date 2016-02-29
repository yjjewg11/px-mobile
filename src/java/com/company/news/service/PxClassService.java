package com.company.news.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.common.PxStringUtils;
import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.PClass;
import com.company.news.entity.PxClass;
import com.company.news.entity.PxStudent;
import com.company.news.entity.User;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;

/**
 * 
 * @author Administrator 培训机构班级 px_pxclass
 */
@Service
public class PxClassService extends AbstractClassService {
	private static final String model_name = "培训机构班级模块";
	@Autowired
	private PxTeachingPlanService pxTeachingPlanService;
	/**
	 * 班级切换学校时
	 * 
	 * @param uuid
	 * @param name
	 * @param img
	 */
	private void relUpdate_classChangeGroup(PxClass obj, String oldGroupuuid) {
		this.logger
				.info("relUpdate_classChangeGroup,obj uuid=" + obj.getUuid());
		// 根据班级的学校uuid
		this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"update PxStudent set  groupuuid=? where groupuuid=?",
				obj.getGroupuuid(), oldGroupuuid);
		// 更新班级互动学校uuid
		this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"update ClassNews set  groupuuid=? where classuuid=?",
				obj.getGroupuuid(), obj.getUuid());
		// 更新家长学生联系吧
		this.nSimpleHibernateDao
				.getHibernateTemplate()
				.bulkUpdate(
						"update StudentContactRealation set  groupuuid=? where class_uuid=?",
						obj.getGroupuuid(), obj.getUuid());

	}

	/**
	 * 查询我的孩子参加的班级
	 * 
	 * @return
	 */
	public List<PxClass> listByStudent(String student_uuid) {
		List l = new ArrayList<PxClass>();
		l = (List<PxClass>) this.nSimpleHibernateDao
				.getHibernateTemplate()
				.find("from PxClass where uuid in(select class_uuid from PxStudentPXClassRelation where student_uuid=?) ",
						student_uuid);

		// warpVoList(l);
		return l;
	}

	/**
	 * 查询我的孩子参加的班级
	 * 
	 * @return
	 */
	public List<PxClass> listByuuids(String uuids) {
		List l = new ArrayList<PxClass>();
		l = (List<PxClass>) this.nSimpleHibernateDao.getHibernateTemplate()
				.find("from PxClass where uuid in("
						+ DBUtil.stringsToWhereInValue(uuids) + ") ");
		return l;
	}

	/**
	 * 查询我的孩子参加的班级关联列表
	 * 
	 * @return
	 */
	public List listPxStudentPXClassRelationByStudent(SessionUserInfoInterface parent) {

		String sql = "select t0.student_uuid,t0.class_uuid,t1.name,t1.isdisable,t1.disable_time,t3.uuid as groupuuid,t3.brand_name as brand_name "
				+ "from px_pxstudentpxclassrelation t0 "
				+ "inner join px_pxclass t1 on t0.class_uuid=t1.uuid "
				+ "inner join px_group t3 on t1.groupuuid=t3.uuid  "
				+ "where student_uuid in( "
				+ "select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
				+ parent.getUuid() + "' ) group by t0.class_uuid";
		Query q = this.nSimpleHibernateDao.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> list = q.list();
		
		String classuuids="";
		for(Map m:list)
		{
			//只查询当前正在学习中的
			if(!(m.get("isdisable")!=null&&m.get("isdisable").toString().equals("1")))
			classuuids+=("'"+m.get("class_uuid")+"',");
			//增加学生姓名
			PxStudent pxStudent=(PxStudent) CommonsCache.get((String) m.get("student_uuid"), PxStudent.class);
			m.put("student_name",pxStudent.getName());
		}
		
		if(StringUtils.isNotBlank(classuuids))
		{
		Map<String, Date> plandateMap=pxTeachingPlanService.getMinPlandateByClassuuids(PxStringUtils.StringDecComma(classuuids));
		//组合返回最近培训时间
		for(Map m:list)
			m.put("plandate", plandateMap.get(m.get("class_uuid")));
		}
		
		//
		// List l = (List<PxStudentPXClassRelation>) this.nSimpleHibernateDao
		// .getHibernateTemplate()
		// .find("from PxStudentPXClassRelation where student_uuid  in (select student_uuid from PxStudentContactRealation where parent_uuid ='"+parent.getUuid()+"') order by student_uuid");

		// warpVoList(l);
		return list;
	}

	/**
	 * 查询指定用户相关的班级
	 * 
	 * @return
	 */
	public List queryClassByUseruuid(String useruuid) {

		List l = (List<PClass>) this.nSimpleHibernateDao
				.getHibernateTemplate()
				.find("from PxClass where uuid in (select classuuid from UserClassRelation where useruuid=?) order by create_time desc",
						useruuid);
		warpVoList(l);
		return l;
	}
	
	

	/**
	 * 
	 * @param groupuuid
	 * @param pData
	 * @param point
	 * @return
	 */
	public PageQueryResult listMyChildClassByPage(String cur_user_uuid,PaginationData pData,String isdisable) {
//		String hql = "from PxCourse4Q  where  status=0 ";
//		if(StringUtils.isNotBlank(groupuuid)){
//			hql+=" and groupuuid in(" + DBUtil.stringsToWhereInValue(groupuuid) + ")";
//		}
//		hql += " order by updatetime desc ";
//
//		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPaginationToHql(hql, pData);
//		List<PxCourse4Q> list=pageQueryResult.getData();
String sql = "select t1.uuid,t1.courseuuid,t1.groupuuid,t1.disable_time,t2.logo,t3.img as group_img,t2.title as course_title,t4.name as student_name,t1.name as class_name,t3.brand_name as group_name"
		+ " from px_pxstudentpxclassrelation t0 "
		+ " inner join px_pxclass t1 on t0.class_uuid=t1.uuid "
		+ " left join px_pxcourse t2 on t1.courseuuid=t2.uuid  "
		+ " left join px_group t3 on t1.groupuuid=t3.uuid  "
		+ " inner join px_pxstudent t4 on t0.student_uuid=t4.uuid  "
		+ " where t0.student_uuid  in( "
		+ " select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
		+ DbUtils.safeToWhereString(cur_user_uuid) + "' ) ";
	if(StringUtils.isNotBlank(isdisable)){
		sql+=" and t1.isdisable ="+isdisable;
	}
	
Query q = this.nSimpleHibernateDao.createSQLQuery(sql);
q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		PageQueryResult pageQueryResult =this.nSimpleHibernateDao.findByPageForSqlNoTotal(q, pData);
		List<Map> list=pageQueryResult.getData();
		String classuuids="";
		if(SystemConstants.Class_isdisable_0.toString().equals(isdisable)){
			for(Map m:list)
			{
				//只查询当前正在学习中的
				classuuids+=("'"+m.get("uuid")+"',");
			}
		}
	
		Map<String, Date> plandateMap=null;
		if(StringUtils.isNotBlank(classuuids))
		{
		 plandateMap=pxTeachingPlanService.getMinPlandateByClassuuids(PxStringUtils.StringDecComma(classuuids));
		
		}
		for(Map map:list)
		{
			//当课程LOGO为空时，取机构的LOGO
			if(StringUtils.isBlank((String)map.get("logo")))
				map.put("logo", map.get("group_img"));
			map.put("logo", PxStringUtil.imgSmallUrlByUuid((String)map.get("logo")));
			
			if(plandateMap!=null)map.put("plandate", plandateMap.get(map.get("uuid")));
		}
		
		return pageQueryResult;
	}
	

	/**
	 * 
	 * @param groupuuid
	 * @param pData
	 * @param point
	 * @return
	 */
	public List listclassTeacher(String classuuid) {
String sql = "select t0.uuid,t0.name"
		+ " from px_user t0 "
		+ " inner join px_userclassrelation t1 on t1.useruuid=t0.uuid and t1.type="+SystemConstants.class_usertype_teacher
		+ " where t1.classuuid ='"+DbUtils.safeToWhereString(classuuid) + "'  ";
		
	Query q = this.nSimpleHibernateDao.createSQLQuery(sql);
	q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	
		
		return q.list();
	}

	/**
	 * 获取班级
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public PxClass get(String uuid) throws Exception {
		PxClass pclass = (PxClass) this.nSimpleHibernateDao.getObjectById(
				PxClass.class, uuid);
		if (pclass == null)
			return null;

		warpVo(pclass);
		return pclass;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

}
