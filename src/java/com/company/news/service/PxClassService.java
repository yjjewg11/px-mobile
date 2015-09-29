package com.company.news.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.company.news.entity.PClass;
import com.company.news.entity.Parent;
import com.company.news.entity.PxClass;
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

		//warpVoList(l);
		return l;
	}
	

	/**
	 * 查询我的孩子参加的班级
	 * 
	 * @return
	 */
	public List<PxClass> listByuuids(String uuids) {
		List l = new ArrayList<PxClass>();
			l = (List<PxClass>) this.nSimpleHibernateDao
					.getHibernateTemplate()
					.find("from PxClass where uuid in("+DBUtil.stringsToWhereInValue(uuids)+") ");
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
		String sql = "select t0.student_uuid,t0.class_uuid,t1.name as class_name from px_pxstudentpxclassrelation t0 inner join px_pxclass t1 on t0.class_uuid=t1.uuid  where student_uuid in( select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
								+ parent.getUuid() + "' ) order by t0.student_uuid";
		Query q = s
				.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List list=q.list();
//		
//		List	l = (List<PxStudentPXClassRelation>) this.nSimpleHibernateDao
//					.getHibernateTemplate()
//					.find("from PxStudentPXClassRelation where student_uuid  in (select student_uuid from PxStudentContactRealation where parent_uuid ='"+parent.getUuid()+"') order by student_uuid");

		//warpVoList(l);
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
