package com.company.news.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.PClass;
import com.company.news.entity.Student;
import com.company.news.entity.StudentContactRealation;
import com.company.news.entity.User;
import com.company.news.jsonform.StudentJsonform;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class StudentService extends AbstractServcice {

	/**
	 * 用户注册
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(StudentJsonform studentJsonform,
			ResponseMessage responseMessage) throws Exception {

		// TEL格式验证
		if (StringUtils.isBlank(studentJsonform.getName())) {
			responseMessage.setMessage("name不能为空！");
			return false;
		}

		// name昵称验证
		if (StringUtils.isBlank(studentJsonform.getClassuuid())) {
			responseMessage.setMessage("Classuuid不能为空");
			return false;
		}
		
		PClass pClass=(PClass) this.nSimpleHibernateDao.getObjectById(PClass.class, studentJsonform.getClassuuid());
		//班级不存在
		if(pClass==null){
			responseMessage.setMessage("班级不存在");
			return false;
		}

		studentJsonform.setHeadimg(PxStringUtil.imgUrlToUuid(studentJsonform.getHeadimg()));
		Student student = new Student();

		BeanUtils.copyProperties(student, studentJsonform);
		student.setCreate_time(TimeUtils.getCurrentTimestamp());
		student.setGroupuuid(pClass.getGroupuuid());
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(student);

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
	public boolean update(StudentJsonform studentJsonform,
			ResponseMessage responseMessage) throws Exception {

		Student student = (Student) this.nSimpleHibernateDao.getObjectById(
				Student.class, studentJsonform.getUuid());
		studentJsonform.setHeadimg(PxStringUtil.imgUrlToUuid(studentJsonform.getHeadimg()));
		Student old_student=new Student();
		ConvertUtils.register(new DateConverter(null), java.util.Date.class); 
		BeanUtils.copyProperties(old_student, student);
		
		if (student != null) {
			BeanUtils.copyProperties(student, studentJsonform);
			//设置不能被修改的字段
			student.setUuid(old_student.getUuid());
			//student.setName(old_student.getName());
			student.setClassuuid(old_student.getClassuuid());
			student.setGroupuuid(old_student.getGroupuuid());
			student.setCreate_time(old_student.getCreate_time());

			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().update(student);

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
	public List<Student> query(String classuuid,String groupuuid) {
		String hql="from Student where 1=1";
		
		// Group_uuid昵称验证
		if (StringUtils.isNotBlank(classuuid)) {
			hql+=" and classuuid='"+classuuid+"'";
		}
		
		if (StringUtils.isNotBlank(groupuuid)) {
			hql+=" and groupuuid='"+groupuuid+"'";
		}		
		
		return (List<Student>) this.nSimpleHibernateDao.getHibernateTemplate().find(hql, null);
	}
	
	/**
	 * 查询所有机构列表
	 * 
	 * @return
	 */
	public List<Student> listByMyChildren(String uuids) {
		if(StringUtils.isBlank(uuids))return new ArrayList();
		String hql="from Student where uuid in ("+DBUtil.stringsToWhereInValue(uuids)+")";
		
		
		List<Student> list=(List<Student>) this.nSimpleHibernateDao.getHibernateTemplate().find(hql,null );
		this.nSimpleHibernateDao.getHibernateTemplate().clear();
		for(Student o:list){
			o.setHeadimg(PxStringUtil.imgUrlByUuid(o.getHeadimg()));
		}
		return list;
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Student get(String uuid)throws Exception{
		Student o= (Student) this.nSimpleHibernateDao.getObjectById(Student.class, uuid);
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
			o.setHeadimg(PxStringUtil.imgUrlByUuid(o.getHeadimg()));
		return o;
	}
	
	/**
	 * 根据电话号码,获取孩子信息
	 * @param tel
	 * @param type
	 * @return
	 */
	public List<StudentContactRealation> getStudentByPhone(String tel) {
		
		return (List<StudentContactRealation>) this.nSimpleHibernateDao.getHibernateTemplate()
				.find("from StudentContactRealation  where tel=?)", tel);
	}


}
