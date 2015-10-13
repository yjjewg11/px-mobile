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
import com.company.news.cache.CommonsCache;
import com.company.news.entity.PClass;
import com.company.news.entity.Parent;
import com.company.news.entity.PxClass;
import com.company.news.entity.PxStudent;
import com.company.news.entity.PxStudentPXClassRelation;
import com.company.news.entity.User;
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
	public List listPxStudentPXClassRelationByStudent(Parent parent) {

		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = "select t0.student_uuid,t0.class_uuid,t1.name,t1.isdisable,t1.disable_time,t3.uuid as groupuuid,t3.brand_name as brand_name "
				+ "from px_pxstudentpxclassrelation t0 "
				+ "inner join px_pxclass t1 on t0.class_uuid=t1.uuid "
				+ "inner join px_group t3 on t1.groupuuid=t3.uuid  "
				+ "where student_uuid in( "
				+ "select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
				+ parent.getUuid() + "' ) group by t0.class_uuid";
		Query q = s.createSQLQuery(sql);
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
