package com.company.news.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.PxStudent;
import com.company.news.entity.PxStudentContactRealation;
import com.company.news.entity.PxStudentPXClassRelation;
import com.company.news.entity.Student;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.PxStudentJsonform;
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
public class PxStudentService extends AbstractStudentService {
	private static final String model_name = "培训结构学生模块";
	@Autowired
	private UserinfoService userinfoService;

	/**
	 * @throws Exception 
	 * 
	 */
	private void addStudentClassRelation(String student_uuid,String class_uuid) throws Exception{
		PxStudentPXClassRelation pp=new PxStudentPXClassRelation();
		pp.setClass_uuid(class_uuid);
		pp.setStudent_uuid(student_uuid);
		this.nSimpleHibernateDao.save(pp);		
	}
	

	/**
	 * 修改
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean update(PxStudentJsonform pxstudentJsonform, ResponseMessage responseMessage) throws Exception {

		PxStudent pxStudent = (PxStudent) this.nSimpleHibernateDao.getObjectById(PxStudent.class, pxstudentJsonform.getUuid());

		PxStudent old_student = new PxStudent();
		ConvertUtils.register(new DateConverter(null), java.util.Date.class);
		BeanUtils.copyProperties(old_student, pxStudent);
		pxstudentJsonform.setHeadimg(PxStringUtil.imgUrlToUuid(pxstudentJsonform.getHeadimg()));
		if (pxStudent != null) {
			BeanUtils.copyProperties(pxStudent, pxstudentJsonform);
			// 设置不能被修改的字段
			pxStudent.setUuid(old_student.getUuid());
			// student.setName(old_student.getName());
			//pxStudent.setClassuuid(old_student.getClassuuid());
			pxStudent.setGroupuuid(old_student.getGroupuuid());
			pxStudent.setCreate_time(old_student.getCreate_time());
			// 格式纠正
			pxStudent.setBirthday(TimeUtils.getDateFormatString(pxStudent.getBirthday()));

			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().update(pxStudent);

			this.updateAllStudentContactRealationByStudent(pxStudent);
			return true;
		} else {
			responseMessage.setMessage("更新记录不存在");
			return false;

		}
	}




	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return Student.class;
	}

	/**
	 * 查询我的孩子.
	 * 
	 * @return
	 */
	public List<PxStudent> listByMy(	SessionUserInfoInterface parent) {
		String hql = "from PxStudent where uuid in(select student_uuid from PxStudentContactRealation where parent_uuid ='"+parent.getUuid()+"')";
		List list = (List<PxStudent>) this.nSimpleHibernateDao.getHibernateTemplate().find(hql, null);
		warpVoList(list);

		return list;
	}




	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public PxStudent get(String uuid) throws Exception {
		PxStudent o = (PxStudent) this.nSimpleHibernateDao.getObjectById(PxStudent.class, uuid);
		if (o == null)
			return null;
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		warpVo(o);
		return o;
	}




	/**
	 * 
	 * @param groupuuid
	 * @param classuuid
	 * @param name
	 * @param pData
	 * @return
	 */
	public PageQueryResult queryByPage(String groupuuid, String classuuid, String name, PaginationData pData) {
		String hql = "from PxStudent where 1=1";
		if (StringUtils.isNotBlank(groupuuid))
			hql += " and  groupuuid in(" + DBUtil.stringsToWhereInValue(groupuuid) + ")";
		if (StringUtils.isNotBlank(classuuid))
			hql += " and  uuid in (select student_uuid from PxStudentPXClassRelation where class_uuid in("+DBUtil.stringsToWhereInValue(classuuid)+"))";
		if (StringUtils.isNotBlank(name))
			hql += " and  name  like '%" + name + "%' ";

		hql += " order by groupuuid,classuuid, convert(name, 'gbk') ";

		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPaginationToHql(hql, pData);
		this.warpVoList(pageQueryResult.getData());

		return pageQueryResult;
	}



	/**
	 * 
	 * @return
	 */
	public List parentContactByMyStudent(List listClassuuids, String student_name) {
		if (listClassuuids.size() == 0)
			return new ArrayList();
		// student_uuid in(select uuid from Student classuuid
		// in("+StringOperationUtil.dateStr)+"))
		String where_student_name = "";
		if (StringUtils.isNotBlank(student_name)) {
			where_student_name = " and student_name like '%" + DbUtils.safeToWhereString(student_name) + "%'";
		}
		String hql = "from StudentContactRealation  where student_uuid in"
				+ "(select student_uuid from PxStudentPXClassRelation where class_uuid in("
				+ DBUtil.stringsToWhereInValue(StringUtils.join(listClassuuids, ",")) + ") )" + where_student_name
				+ "  order by student_name,type";

		List list = this.nSimpleHibernateDao.getHibernateTemplate().find(hql);
		warpStudentContactRealationVoList(list);
		return list;
	}
	/**
	 * 幼儿园更新学生资料时,同步更新培训机构的学生资料
	 * @param tel
	 * @param type
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void updatePxStudentByKDstudent(Student student)  {
		try {
			String sql="select DISTINCT t1.student_uuid from px_pxstudentcontactrealation t1 LEFT JOIN px_studentcontactrealation t2";
			sql+=" on t1.student_name=t2.student_name and t1.tel=t2.tel";
			sql+=" where t2.student_uuid='"+student.getUuid()+"'";
			Session s = this.nSimpleHibernateDao.getHibernateTemplate()
					.getSessionFactory().openSession();
			Query q = s.createSQLQuery(sql);
			List list =q.list();
			if(list.size()==0){
				return;
			}
			if(list.size()>1){
				this.logger.error(" duplicate px student,student_uuid="+StringUtils.join(list,","));
				return;
			}
			
//		PxStudent o = (PxStudent) this.nSimpleHibernateDao.getObjectById(PxStudent.class, (String)list.get(0));
//		if (o == null)
//			return ;
//		
			PxStudentJsonform form=new PxStudentJsonform();

			BeanUtils.copyProperties(form, student);

			form.setUuid((String)list.get(0));
			
			this.update(form, new ResponseMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 根据电话号码,获取孩子信息
	 * @param tel
	 * @param type
	 * @return
	 */
	public List<PxStudentContactRealation> getStudentByPhone(String tel) {
		
		return (List<PxStudentContactRealation>) this.nSimpleHibernateDao.getHibernateTemplate()
				.find("from PxStudentContactRealation  where tel=?)", tel);
	}

}
