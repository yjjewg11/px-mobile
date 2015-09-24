package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.company.news.entity.StudentBind;
import com.company.news.entity.User;
import com.company.news.jsonform.StudentBindJsonform;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class StudentBindService extends AbstractService {

	/**
	 * 用户注册
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(StudentBindJsonform studentBindJsonform,
			ResponseMessage responseMessage) throws Exception {

		// TEL格式验证
		if (StringUtils.isBlank(studentBindJsonform.getName())) {
			responseMessage.setMessage("name不能为空！");
			return false;
		}

		// name昵称验证
		if (StringUtils.isBlank(studentBindJsonform.getCardid())) {
			responseMessage.setMessage("Cardid不能为空");
			return false;
		}
		
		// Studentuuid昵称验证
		if (StringUtils.isBlank(studentBindJsonform.getStudentuuid())) {
			responseMessage.setMessage("Studentuuid不能为空");
			return false;
		}
		
		if(this.isBind(studentBindJsonform.getCardid())){
			responseMessage.setMessage("Cardid已被绑定");
			return false;
		}


		StudentBind studentBind = new StudentBind();

		BeanUtils.copyProperties(studentBind, studentBindJsonform);
		studentBind.setCreatetime(TimeUtils.getCurrentTimestamp());
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(studentBind);

		return true;
	}

	/**
	 * 用户注册
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean update(StudentBindJsonform studentBindJsonform,
			ResponseMessage responseMessage) throws Exception {
		// TEL格式验证
		if (StringUtils.isBlank(studentBindJsonform.getName())) {
			responseMessage.setMessage("name不能为空！");
			return false;
		}
				
		StudentBind studentBind = (StudentBind) this.nSimpleHibernateDao.getObjectById(
				StudentBind.class, studentBindJsonform.getUuid());
		
		
		if (studentBind != null) {
			studentBind.setName(studentBindJsonform.getName());
			studentBind.setNote(studentBindJsonform.getNote());

			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().update(studentBind);

			return true;
		} else {
			responseMessage.setMessage("更新记录不存在");
			return false;

		}
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

	/**
	 * 查询所有机构列表
	 * 
	 * @return
	 */
	public List<StudentBind> query(String studentuuid) {
		String hql="from StudentBind where 1=1";
		
		// Group_uuid昵称验证
		if (StringUtils.isNotBlank(studentuuid)) {
			hql+=" and studentuuid='"+studentuuid+"'";
		}
		
		hql+=" order by createtime";
		
		return (List<StudentBind>) this.nSimpleHibernateDao.getHibernateTemplate().find(hql, null);
	}
	

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public StudentBind get(String uuid)throws Exception{
		return (StudentBind) this.nSimpleHibernateDao.getObjectById(StudentBind.class, uuid);
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
					"delete from StudentBind where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(StudentBind.class, uuid);

		}

		return true;
	}
	
	
	/**
	 * 
	 * @param uuid
	 * @param responseMessage
	 * @return
	 */
	private boolean isBind(String cardid) {
		StudentBind studentBind=(StudentBind) this.nSimpleHibernateDao.getObjectByAttribute(StudentBind.class, "cardid", cardid);
		
		if(studentBind!=null)
			return true;
		
		return false;
	}

	/**
	 * 查询学生绑定卡号信息.
	 * @param classuuid
	 * @param groupuuid
	 * @param uuid
	 * @return
	 */
	public List<Object[]> query(String classuuid, String groupuuid,String uuid) {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		
		String sql = "select b2.studentuuid,b2.cardid,b2.userid,s1.name ";
		sql+=" from px_student s1  left join px_studentbind b2 on  s1.uuid=b2.studentuuid  ";
		sql+=" where 1=1 ";
		if (StringUtils.isNotBlank(classuuid))
			sql += " and   s1.groupuuid in(" + DBUtil.stringsToWhereInValue(groupuuid) + ")";
		if (StringUtils.isNotBlank(classuuid))
			sql += " and  s1.classuuid in(" + DBUtil.stringsToWhereInValue(classuuid) + ")";
		if (StringUtils.isNotBlank(uuid))
			sql += " and  s1.uuid in(" + DBUtil.stringsToWhereInValue(uuid) + ")";
		
		sql += "order by s1.classuuid,CONVERT( s1.name USING gbk)";
		
		//student_uuid,cardid,userid,student_name
		List<Object[]> list = s.createSQLQuery(sql).list();
		
		return list;
	}
}
