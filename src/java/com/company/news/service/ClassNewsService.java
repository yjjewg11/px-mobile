package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.ClassNews;
import com.company.news.entity.PClass;
import com.company.news.entity.Parent;
import com.company.news.entity.StudentContactRealation;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ClassNewsJsonform;
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
public class ClassNewsService extends AbstractService {
	public static final int USER_type_default = 1;// 0:老师
	@Autowired
	private CountService countService;

	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(SessionUserInfoInterface user,ClassNewsJsonform classNewsJsonform,
			ResponseMessage responseMessage) throws Exception {
//		if (StringUtils.isBlank(classNewsJsonform.getTitle())
//				|| classNewsJsonform.getTitle().length() > 128) {
//			responseMessage.setMessage("班级名不能为空！，且长度不能超过45位！");
//			return false;
//		}

		if (StringUtils.isBlank(classNewsJsonform.getClassuuid())) {
			responseMessage.setMessage("classuuid不能为空！");
			return false;
		}
		
		
		PClass pClass=(PClass)this.nSimpleHibernateDao.getObject(PClass.class, classNewsJsonform.getClassuuid());
		if(pClass==null){
			responseMessage.setMessage("选择的班级不存在");
			return false;
		}
		
		
		
		StudentContactRealation studentContactRealation=this.getStudentContactRealationBy(user.getUuid(), classNewsJsonform.getClassuuid());
		
		ClassNews cn = new ClassNews();

		BeanUtils.copyProperties(cn, classNewsJsonform);
		
		cn.setGroupuuid(pClass.getGroupuuid());
		cn.setCreate_time(TimeUtils.getCurrentTimestamp());
		cn.setUpdate_time(TimeUtils.getCurrentTimestamp());
		cn.setReply_time(TimeUtils.getCurrentTimestamp());
		cn.setUsertype(USER_type_default);
		cn.setStatus(SystemConstants.Check_status_fabu);
		cn.setIllegal(0l);
		PxStringUtil.addCreateUser(user, cn);
		
		if (studentContactRealation!=null) {
			//班级互动用孩子的信息发布.
			cn.setCreate_user(PxStringUtil.getParentNameByStudentContactRealation(studentContactRealation));
			cn.setCreate_img(studentContactRealation.getStudent_img());
		}
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(cn);

		return true;
	}
	
	public StudentContactRealation getStudentContactRealationBy(String parent_uuid,String class_uuid){
		List<StudentContactRealation> list=(List<StudentContactRealation>)this.nSimpleHibernateDao.getHibernateTemplate().find("from StudentContactRealation where parent_uuid=? and class_uuid=?", parent_uuid,class_uuid);

		if(list.size()==0){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 更新班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean update(SessionUserInfoInterface user,ClassNewsJsonform classNewsJsonform,
			ResponseMessage responseMessage) throws Exception {
//		if (StringUtils.isBlank(classNewsJsonform.getTitle())
//				|| classNewsJsonform.getTitle().length() > 128) {
//			responseMessage.setMessage("班级名不能为空！，且长度不能超过45位！");
//			return false;
//		}

		ClassNews cn = (ClassNews) this.nSimpleHibernateDao.getObjectById(
				ClassNews.class, classNewsJsonform.getUuid());

		if (cn != null) {
			cn.setImgs(classNewsJsonform.getImgs());
			cn.setContent(classNewsJsonform.getContent());
			cn.setTitle(classNewsJsonform.getTitle());
			cn.setUpdate_time(TimeUtils.getCurrentTimestamp());

			this.nSimpleHibernateDao.getHibernateTemplate().update(cn);
		} else {
			responseMessage.setMessage("对象不存在");
			return true;
		}

		return true;
	}

	/**
	 * 查询所有班级
	 * 
	 * @return
	 */
	public PageQueryResult query(Parent user ,String type,String classuuid, PaginationData pData) {
		String hql = "from ClassNews where status=0 ";
		if (StringUtils.isNotBlank(classuuid)){
			hql += " and  classuuid in("+DBUtil.stringsToWhereInValue(classuuid)+")";
		}else{
			hql += " and  groupuuid not in ('group_wj1','group_wj2','group_px1')";
		}
	
		pData.setOrderFiled("create_time");
		pData.setOrderType("desc");
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHqlNoTotal(hql, pData);
		this.warpVoList(pageQueryResult.getData(), user.getUuid());
		
		return pageQueryResult;

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
					"delete from ClassNews where uuid in(?)", uuid);
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from ClassNewsReply where newsuuid in(?)", uuid);
		} else {
			this.nSimpleHibernateDao.deleteObjectById(ClassNews.class, uuid);
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from ClassNewsReply where newsuuid =?", uuid);
		}

		return true;
	}

	public ClassNews get(Parent user ,String uuid) throws Exception {
		ClassNews cn = (ClassNews) this.nSimpleHibernateDao.getObjectById(
				ClassNews.class, uuid);
		this.warpVo(cn, user.getUuid());
		return cn;

	}



	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return ClassNews.class;
	}
	

	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private ClassNews warpVo(ClassNews o,String cur_user_uuid){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		
		o.setImgsList(PxStringUtil.uuids_to_imgMiddleurlList(o.getImgs()));
		o.setShare_url(PxStringUtil.getClassNewsByUuid(o.getUuid()));
		try {
			o.setCount(countService.count(o.getUuid(), SystemConstants.common_type_hudong));
			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(), cur_user_uuid));
			o.setReplyPage(this.getReplyPageList(o.getUuid()));
			o.setCreate_img(PxStringUtil.imgSmallUrlByUuid(o.getCreate_img()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List<ClassNews> warpVoList(List<ClassNews> list,String cur_user_uuid){
		for(ClassNews o:list){
			warpVo(o,cur_user_uuid);
		}
		return list;
	}

}
