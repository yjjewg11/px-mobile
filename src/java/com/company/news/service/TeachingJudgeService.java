package com.company.news.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.TeacherJudge;
import com.company.news.entity.User;
import com.company.news.jsonform.TeachingJudgeJsonform;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;
import com.company.news.vo.TeacherPhone;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class TeachingJudgeService extends AbstractService {

	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(TeachingJudgeJsonform teachingJudgeJsonform,
			ResponseMessage responseMessage) throws Exception {

		
		if (StringUtils.isBlank(teachingJudgeJsonform.getTeacheruuid())) {
			responseMessage.setMessage("Teacheruuid不能为空！");
			return false;
		}
		
		
		Timestamp create_time=TimeUtils.getCurrentTimestamp();
		if(isJudge(teachingJudgeJsonform.getTeacheruuid(), create_time, teachingJudgeJsonform.getCreate_useruuid()))
		{
			responseMessage.setMessage("本月已评价，不能重复评价！");
			return false;
		}

		TeacherJudge teacherJudge=new TeacherJudge();
		
		BeanUtils.copyProperties(teacherJudge, teachingJudgeJsonform);
		teacherJudge.setCreate_time(create_time);

        this.nSimpleHibernateDao.save(teacherJudge);

		return true;
	}


	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public TeacherJudge get(String uuid) {
		TeacherJudge t = (TeacherJudge) this.nSimpleHibernateDao.getObjectById(
				TeacherJudge.class, uuid);
		
		return t;

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
					"delete from TeacherJudge where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(TeacherJudge.class, uuid);

		}

		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @param responseMessage
	 * @return
	 */
	private boolean isJudge(String teacheruuid,Timestamp create_time,String create_useruuid) {
		TeacherJudge teacherJudge=this.getJudgeByDate(teacheruuid, create_time, create_useruuid);
		
		if(teacherJudge!=null)
			return true;
		
		return false;
	}
	
	
	/**
	 * 
	 * @param uuid
	 * @param responseMessage
	 * @return
	 */
	public TeacherJudge getJudgeByDate(String teacheruuid,Timestamp create_time,String create_useruuid) {
		Date lastdate=TimeUtils.getLastDayOfMonth(create_time);
		
		Date Firstdate=TimeUtils.getFirstDayOfMonth(create_time);
		
		List<TeacherJudge> list=(List<TeacherJudge>) this.nSimpleHibernateDao.getHibernateTemplate().find(
				"from TeacherJudge where teacheruuid=? and create_useruuid=? and create_time<=? and create_time >=?",
				teacheruuid, create_useruuid,lastdate,Firstdate);
		
		if(list!=null&&list.size()>0)
			return list.get(0);
		
		return null;
	}
	
	
	
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return TeacherJudge.class;
	}


	public void getTeachersAndJudges(String user_uuid,String class_uuids,ModelMap model) {
		
		
		if(StringUtils.isBlank(class_uuids)){
			model.addAttribute(RestConstants.Return_ResponseMessage_list, new ArrayList());
			model.addAttribute("list_judge", new ArrayList());
			return;
		}
		
		List list=new ArrayList();
		String hql = "from User where uuid in (select useruuid from UserClassRelation where classuuid in("+DBUtil.stringsToWhereInValue(class_uuids)+"))";
		List<User> userList=(List<User> )this.nSimpleHibernateDao.getHibernateTemplate().find(hql, null);
		String users="";
		for (User user : userList) {
			users+=user.getUuid()+",";
			TeacherPhone teacherPhone=new TeacherPhone();
			teacherPhone.setTeacher_uuid(user.getUuid());
			teacherPhone.setName(user.getName());
			teacherPhone.setImg(PxStringUtil.imgSmallUrlByUuid(user.getImg()));
			list.add(teacherPhone);
		}
		
		Timestamp create_time=TimeUtils.getDefaultTimestamp();
			Date lastdate=TimeUtils.getLastDayOfMonth(create_time);
		
		Date Firstdate=TimeUtils.getFirstDayOfMonth(create_time);
		
		List<TeacherJudge> list1=(List<TeacherJudge>) this.nSimpleHibernateDao.getHibernateTemplate().find(
				"from TeacherJudge where teacheruuid in("+DBUtil.stringsToWhereInValue(users)+") and create_useruuid=? and create_time<=? and create_time >=?",
				user_uuid,lastdate,Firstdate);
		
		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		model.addAttribute("list_judge", list1);
	}

}
