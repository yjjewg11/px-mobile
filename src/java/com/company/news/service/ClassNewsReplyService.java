package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.ClassNews;
import com.company.news.entity.ClassNewsReply;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ClassNewsReplyJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class ClassNewsReplyService extends AbstractService {
	public static final int USER_type_default = 1;// 0:老师
	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(SessionUserInfoInterface user,ClassNewsReplyJsonform classNewsReplyJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(classNewsReplyJsonform.getContent())) {
			responseMessage.setMessage("content不能为空！");
			return false;
		}

		if (StringUtils.isBlank(classNewsReplyJsonform.getNewsuuid())) {
			responseMessage.setMessage("Newsuuid不能为空！");
			return false;
		}

		ClassNewsReply cn=new ClassNewsReply();

		BeanUtils.copyProperties(cn, classNewsReplyJsonform);

		cn.setCreate_time(TimeUtils.getCurrentTimestamp());
//        cn.setUpdate_time(TimeUtils.getCurrentTimestamp());
        cn.setStatus(SystemConstants.Check_status_fabu);
        
        PxStringUtil.addCreateUser(user, cn);
        
        cn.setUsertype(USER_type_default);
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(cn);

		return true;
	}

	/**
	 * 更新班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean update(SessionUserInfoInterface user,ClassNewsReplyJsonform classNewsReplyJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(classNewsReplyJsonform.getContent())) {
			responseMessage.setMessage("content不能为空！");
			return false;
		}
		
		ClassNewsReply cn=(ClassNewsReply) this.nSimpleHibernateDao.getObjectById(ClassNewsReply.class, classNewsReplyJsonform.getUuid());
		
		if(cn!=null){
			cn.setContent(classNewsReplyJsonform.getContent());
//			cn.setUpdate_time(TimeUtils.getCurrentTimestamp());

			this.nSimpleHibernateDao.getHibernateTemplate().update(cn);
		}else{
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
	public PageQueryResult query(String newsuuid, PaginationData pData) {
		String hql="from ClassNewsReply where status ="+SystemConstants.Check_status_fabu;	
		if (StringUtils.isNotBlank(newsuuid))
			hql+=" and  newsuuid='"+newsuuid+"'";
		
		pData.setOrderFiled("create_time");
		pData.setOrderType("desc");
		
		PageQueryResult pageQueryResult= this.nSimpleHibernateDao.findByPaginationToHqlNoTotal(hql, pData);
		
		return pageQueryResult;
				
	}

	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private ClassNewsReply warpVo(ClassNewsReply o,String cur_user_uuid){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		try {
			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(), cur_user_uuid));
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
	public List<ClassNewsReply> warpVoList(List<ClassNewsReply> list,String cur_user_uuid){
		for(ClassNewsReply o:list){
			warpVo(o,cur_user_uuid);
		}
		return list;
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
					"delete from ClassNewsReply where uuid in(?)", uuid);
		} else {
			this.nSimpleHibernateDao.deleteObjectById(ClassNewsReply.class, uuid);
		}

		return true;
	}

	
	public ClassNewsReply get(String uuid) throws Exception {
		return (ClassNewsReply) this.nSimpleHibernateDao.getObjectById(
				ClassNewsReply.class, uuid);	
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return ClassNews.class;
	}

}
