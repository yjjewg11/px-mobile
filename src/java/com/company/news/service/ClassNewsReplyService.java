package com.company.news.service;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.entity.ClassNews;
import com.company.news.entity.ClassNewsReply;
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
public class ClassNewsReplyService extends AbstractServcice {

	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(ClassNewsReplyJsonform classNewsReplyJsonform,
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
        cn.setUpdate_time(TimeUtils.getCurrentTimestamp());
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
	public boolean update(ClassNewsReplyJsonform classNewsReplyJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(classNewsReplyJsonform.getContent())) {
			responseMessage.setMessage("content不能为空！");
			return false;
		}
		
		ClassNewsReply cn=(ClassNewsReply) this.nSimpleHibernateDao.getObjectById(ClassNewsReply.class, classNewsReplyJsonform.getUuid());
		
		if(cn!=null){
			cn.setContent(classNewsReplyJsonform.getContent());
			cn.setUpdate_time(TimeUtils.getCurrentTimestamp());
			
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
		String hql="from ClassNewsReply where 1=1";	
		if (StringUtils.isNotBlank(newsuuid))
			hql+=" and  newsuuid='"+newsuuid+"'";
		
		hql+=" order by create_time";
		
		PageQueryResult pageQueryResult= this.nSimpleHibernateDao.findByPaginationToHql(hql, pData);
		
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
