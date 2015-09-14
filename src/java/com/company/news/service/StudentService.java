package com.company.news.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.PClass;
import com.company.news.entity.Parent;
import com.company.news.entity.Student;
import com.company.news.entity.StudentContactRealation;
import com.company.news.entity.User;
import com.company.news.jsonform.StudentJsonform;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.validate.CommonsValidate;
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
		//格式纠正
		student.setBirthday(TimeUtils.getDateFormatString(student.getBirthday()));

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
			//格式纠正
			student.setBirthday(TimeUtils.getDateFormatString(student.getBirthday()));


			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().update(student);

			this.updateStudentContactRealation(student, SystemConstants.USER_type_ba, student.getBa_tel());
			this.updateStudentContactRealation(student, SystemConstants.USER_type_ma, student.getMa_tel());
			this.updateStudentContactRealation(student, SystemConstants.USER_type_ye, student.getYe_tel());
			this.updateStudentContactRealation(student, SystemConstants.USER_type_nai, student.getNai_tel());
			this.updateStudentContactRealation(student, SystemConstants.USER_type_waigong, student.getWaigong_tel());
			this.updateStudentContactRealation(student, SystemConstants.USER_type_waipo, student.getWaipo_tel());
			this.updateStudentContactRealation(student, SystemConstants.USER_type_other, student.getOther_tel());
			
			return true;
		} else {
			responseMessage.setMessage("更新记录不存在");
			return false;

		}
	}

	/**
	 * 保存学生资料时,更新学生关联家长的手机表
		 * 1.根据注册手机,绑定和学生的关联关心.
		 * 2.更新孩子数据时,也会自动绑定学生和家长的数据.
	 * @param tel
	 * @param type
	 * @return
	 */
	private StudentContactRealation updateStudentContactRealation(Student student,Integer type,String tel)  throws Exception {
		if(!CommonsValidate.checkCellphone(tel)){
			return null;
		}
		String student_uuid=student.getUuid();
		StudentContactRealation studentContactRealation= this.getStudentContactRealationBy(student_uuid, type);
		if(studentContactRealation==null){//不存在,则新建.
			if(!CommonsValidate.checkCellphone(tel))return null;
			studentContactRealation=new StudentContactRealation();
		}else{
			//验证失败则,表示删除关联关系.
			if(!CommonsValidate.checkCellphone(tel)){
				nSimpleHibernateDao.delete(studentContactRealation);
				return null;
			}
			//一样则表示不变,直接返回.
			if(tel.equals(studentContactRealation.getTel())
					&&student.getName().equals(studentContactRealation.getStudent_name())
					&&student.getHeadimg()!=null&&student.getHeadimg().equals(studentContactRealation.getStudent_img())){
				return studentContactRealation;
			}
		}
		studentContactRealation.setStudent_uuid(student.getUuid());
		studentContactRealation.setStudent_name(student.getName());
		studentContactRealation.setIsreg(SystemConstants.USER_isreg_0);
		studentContactRealation.setGroupuuid(student.getGroupuuid());
		
		studentContactRealation.setTel(tel);
		studentContactRealation.setType(type);
		studentContactRealation.setUpdate_time(TimeUtils.getCurrentTimestamp());
		
		studentContactRealation.setClass_uuid(student.getClassuuid());
		studentContactRealation.setStudent_img(student.getHeadimg());
		
		Parent parent=(Parent)nSimpleHibernateDao.getObjectByAttribute(Parent.class,"loginname", tel);
		//判断电话,是否已经注册,来设置状态.
		if(parent!=null){
			studentContactRealation.setIsreg(SystemConstants.USER_isreg_1);
			studentContactRealation.setParent_uuid(parent.getUuid());			
		}else{
			studentContactRealation.setIsreg(SystemConstants.USER_isreg_0);
		}
		
		
		//1:妈妈,2:爸爸,3:爷爷,4:奶奶,5:外公,6:外婆,7:其他
		switch (type) {
		case 1:
			studentContactRealation.setTypename("妈妈");
			break;
		case 2:
			studentContactRealation.setTypename("爸爸");
			break;
		case 3:
			studentContactRealation.setTypename("爷爷");
			break;
		case 4:
			studentContactRealation.setTypename("奶奶");
			break;
		case 5:
			studentContactRealation.setTypename("外公");
			break;
		case 6:
			studentContactRealation.setTypename("外婆");
			break;
		case 7:
			studentContactRealation.setTypename("其他");
			break;
		default:
			break;
		}

		//更新家长姓名和头像.多个孩子已最后保存为准
		if(parent!=null){
			String newParentName=PxStringUtil.getParentNameByStudentContactRealation(studentContactRealation);
			if(parent.getImg()==null||parent.getImg().equals(student.getHeadimg())){
				parent.setImg(student.getHeadimg());
			}
			if(newParentName!=null&&!newParentName.equals(parent.getName())){
				parent.setName(newParentName);
				nSimpleHibernateDao.relUpdate_updateSessionUserInfoInterface(parent);
			}
			nSimpleHibernateDao.save(parent);
		}
		nSimpleHibernateDao.save(studentContactRealation);
		return studentContactRealation;
	}
	
	/**
	 * 获取
	 * @param tel
	 * @param type
	 * @return
	 */
	public StudentContactRealation getStudentContactRealationBy(String student_uuid,Integer type) {
		List<StudentContactRealation> list=(List<StudentContactRealation>) this.nSimpleHibernateDao.getHibernateTemplate().
				find("from StudentContactRealation where student_uuid=? and type=?", student_uuid,type);
		if(list.size()>0){
			return (StudentContactRealation)list.get(0);
		}
		return null;
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
		
		List<Student> list=(List<Student>) this.nSimpleHibernateDao.getHibernateTemplate().find(hql, null);
		 
		warpVoList(list);
		 
		 return list;
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

		this.warpVoList(list);
		return list;
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Student get(String uuid)throws Exception{
		Student o= (Student) this.nSimpleHibernateDao.getObjectById(Student.class, uuid);
		warpVo(o);
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

	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private Student warpVo(Student o){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
			o.setHeadimg(PxStringUtil.imgSmallUrlByUuid(o.getHeadimg()));
		return o;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List<Student> warpVoList(List<Student> list){
		for(Student o:list){
			warpVo(o);
		}
		return list;
	}


}
