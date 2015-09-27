package com.company.news.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.entity.PClass;
import com.company.news.entity.Parent;
import com.company.news.entity.PxClass;
import com.company.news.entity.User;
import com.company.news.entity.UserClassRelation;
import com.company.news.jsonform.PxClassRegJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.right.RightConstants;
import com.company.news.right.RightUtils;
import com.company.news.vo.ResponseMessage;

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
					.find("from PxClass where uuid in(select class_uuid from PxStudentPXClassRelation where student_uuid=?) order by  convert(name, 'gbk') ",
							student_uuid);

		//warpVoList(l);
		return l;
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
