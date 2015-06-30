package com.company.news.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.company.news.ProjectProperties;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Group;
import com.company.news.entity.PClass;
import com.company.news.entity.Student;
import com.company.news.entity.User;
import com.company.news.entity.UserGroupRelation;
import com.company.news.form.UserLoginForm;
import com.company.news.jsonform.StudentJsonform;
import com.company.news.jsonform.UserRegJsonform;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.TimeUtils;
import com.company.news.validate.CommonsValidate;
import com.company.news.vo.ResponseMessage;
import com.company.news.vo.UserInfoReturn;
import com.company.plugin.security.LoginLimit;
import com.company.web.listener.SessionListener;

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

		PClass pClass = (PClass) this.nSimpleHibernateDao.getObjectById(
				PClass.class, studentJsonform.getClassuuid());
		// 班级不存在
		if (pClass == null) {
			responseMessage.setMessage("班级不存在");
			return false;
		}

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
		if (student != null) {

			student.setBa_tel(studentJsonform.getBa_tel());
			student.setBirthday(studentJsonform.getBirthday());
			student.setHeadimg(studentJsonform.getHeadimg());
			student.setMa_tel(studentJsonform.getMa_tel());
			student.setNai_tel(studentJsonform.getNai_tel());
			student.setNickname(studentJsonform.getNickname());
			student.setOther_tel(studentJsonform.getOther_tel());
			student.setSex(studentJsonform.getSex());
			student.setWaigong_tel(studentJsonform.getWaigong_tel());
			student.setWaipo_tel(studentJsonform.getWaipo_tel());
			student.setYe_tel(studentJsonform.getYe_tel());

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
	public List<Student> query(String classuuid) {
		// Group_uuid昵称验证
		if (StringUtils.isBlank(classuuid)) {
			return null;
		}
		return (List<Student>) this.nSimpleHibernateDao.getHibernateTemplate()
				.find("from Student where classuuid=?", classuuid);
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Student get(String uuid) throws Exception {
		return (Student) this.nSimpleHibernateDao.getObjectById(Student.class,
				uuid);
	}

	/**
	 * 
	 * @param tel
	 * @param type
	 * @return
	 */
	public List<Student> getStudentByPhone(String tel, Integer type) {
		String typeStr = "";
		switch (type) {
		case UserinfoService.USER_type_ma:
			typeStr = "ma_tel";
			break;
		case UserinfoService.USER_type_ba:
			typeStr = "ba_tel";
			break;
		case UserinfoService.USER_type_ye:
			typeStr = "ye_tel";
			break;
		case UserinfoService.USER_type_nai:
			typeStr = "nai_tel";
			break;
		case UserinfoService.USER_type_waipo:
			typeStr = "waipo_tel";
			break;
		case UserinfoService.USER_type_waigong:
			typeStr = "waigong_tel";
			break;
		default:
			typeStr = "ma_tel";
			break;
		}
		return (List<Student>) this.nSimpleHibernateDao.getHibernateTemplate()
				.find("from Student where " + typeStr + "=?", tel);
	}

}
