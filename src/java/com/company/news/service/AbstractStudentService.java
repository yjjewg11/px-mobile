package com.company.news.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.AbstractStudent;
import com.company.news.entity.Parent;
import com.company.news.entity.Student;
import com.company.news.entity.StudentContactRealation;
import com.company.news.rest.util.TimeUtils;
import com.company.news.validate.CommonsValidate;

public class AbstractStudentService extends AbstractService {
	@Autowired
	private UserinfoService userinfoService;

	protected void updateAllStudentContactRealationByStudent(AbstractStudent student) throws Exception {
		// 添加学生关联联系人表
		this.updateStudentContactRealation(student,
				SystemConstants.USER_type_ba, student.getBa_tel());
		this.updateStudentContactRealation(student,
				SystemConstants.USER_type_ma, student.getMa_tel());
		this.updateStudentContactRealation(student,
				SystemConstants.USER_type_ye, student.getYe_tel());
		this.updateStudentContactRealation(student,
				SystemConstants.USER_type_nai, student.getNai_tel());
		this.updateStudentContactRealation(student,
				SystemConstants.USER_type_waigong, student.getWaigong_tel());
		this.updateStudentContactRealation(student,
				SystemConstants.USER_type_waipo, student.getWaipo_tel());
		this.updateStudentContactRealation(student,
				SystemConstants.USER_type_other, student.getOther_tel());

	}

	/**
	 * 保存学生资料时,更新学生关联家长的手机表 1.根据注册手机,绑定和学生的关联关心. 2.更新孩子数据时,也会自动绑定学生和家长的数据.
	 * 
	 * @param tel
	 * @param type
	 * @return
	 */
	private StudentContactRealation updateStudentContactRealation(
			AbstractStudent student, Integer type, String tel) throws Exception {
		
		
		
		tel=PxStringUtil.repairCellphone(tel);
		String student_uuid = student.getUuid();
		StudentContactRealation studentContactRealation = this
				.getStudentContactRealationBy(student_uuid, type);
		if (studentContactRealation == null) {// 不存在,则新建.
			if (!CommonsValidate.checkCellphone(tel))
				return null;
			studentContactRealation = new StudentContactRealation();
		} else {
			// 验证失败则,表示删除关联关系.
			if (!CommonsValidate.checkCellphone(tel)) {
				nSimpleHibernateDao.delete(studentContactRealation);
				return null;
			}
			// 一样则表示不变,直接返回.
			if (tel.equals(studentContactRealation.getTel())
					&& student.getName().equals(
							studentContactRealation.getStudent_name())) {
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

		Parent parent = (Parent) nSimpleHibernateDao.getObjectByAttribute(
				Parent.class, "loginname", tel);
		// 判断电话,是否已经注册,来设置状态.
		if (parent != null) {
			studentContactRealation.setIsreg(SystemConstants.USER_isreg_1);
			studentContactRealation.setParent_uuid(parent.getUuid());
		} else {
			studentContactRealation.setIsreg(SystemConstants.USER_isreg_0);
		}

		// 1:妈妈,2:爸爸,3:爷爷,4:奶奶,5:外公,6:外婆,7:其他
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

		// 更新家长姓名和头像.多个孩子已最后保存为准
		if (parent != null) {
			String newParentName = PxStringUtil
					.getParentNameByStudentContactRealation(studentContactRealation);
			if (parent.getImg() == null
					|| parent.getImg().equals(student.getHeadimg())) {
				parent.setImg(student.getHeadimg());
			}
			if (newParentName != null
					&& !newParentName.equals(parent.getName())) {
				parent.setName(newParentName);
				userinfoService
						.relUpdate_updateSessionUserInfoInterface(parent);
			}
			nSimpleHibernateDao.save(parent);
		}
		nSimpleHibernateDao.save(studentContactRealation);
		return studentContactRealation;
	}

	/**
	 * 获取
	 * 
	 * @param tel
	 * @param type
	 * @return
	 */
	private StudentContactRealation getStudentContactRealationBy(
			String student_uuid, Integer type) {
		List<StudentContactRealation> list = (List<StudentContactRealation>) this.nSimpleHibernateDao
				.getHibernateTemplate()
				.find("from StudentContactRealation where student_uuid=? and type=?",
						student_uuid, type);
		if (list.size() > 0) {
			return (StudentContactRealation) list.get(0);
		}
		return null;
	}

	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	protected AbstractStudent warpVo(AbstractStudent o) {
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		o.setHeadimg(PxStringUtil.imgUrlByUuid(o.getHeadimg()));
		return o;
	}

	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	protected List<AbstractStudent> warpVoList(List<AbstractStudent> list) {
		for (AbstractStudent o : list) {
			warpVo(o);
		}
		return list;
	}
	
	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	private StudentContactRealation warpStudentContactRealationVo(StudentContactRealation o) {
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		o.setStudent_img(PxStringUtil.imgUrlByUuid(o.getStudent_img()));
		return o;
	}

	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	protected List<StudentContactRealation> warpStudentContactRealationVoList(List<StudentContactRealation> list) {
		for (StudentContactRealation o : list) {
			warpStudentContactRealationVo(o);
		}
		return list;
	}
	
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return AbstractStudentService.class;
	}


}
