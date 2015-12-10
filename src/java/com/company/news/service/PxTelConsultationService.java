package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Appraise;
import com.company.news.entity.PxTelConsultation;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.PxTelConsultationJsonform;
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
public class PxTelConsultationService extends AbstractService {

	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(PxTelConsultationJsonform jsonform,
			ResponseMessage responseMessage,SessionUserInfoInterface user) throws Exception {
		
		if (this.validateRequireByRegJsonform(
				jsonform.getType(), "type", responseMessage)) {
			return false;
		}
		
		if (this.validateRequireByRegJsonform(
						jsonform.getExt_uuid(), "Ext_uuid", responseMessage)) {
			return false;
		}
		
		jsonform.setExt_uuid(DbUtils.safeToWhereString(jsonform.getExt_uuid()));
		String ext_context=null;
		String grouuuid=null;
		if(SystemConstants.common_type_pxgroup==jsonform.getType().intValue()){
			List list=this.nSimpleHibernateDao.getHibernateTemplate().find("select brand_name from Group where uuid='"+DbUtils.safeToWhereString(jsonform.getExt_uuid())+"'");
			
			if(list==null||list.size()==0){
				responseMessage.setMessage("学校没找到!Ext_uuid="+jsonform.getExt_uuid());
				return false;
			}
			ext_context=(String)list.get(0);
			grouuuid=jsonform.getExt_uuid();
		}else if(SystemConstants.common_type_pxcourse==jsonform.getType().intValue()){
				List list=this.nSimpleHibernateDao.getHibernateTemplate().find("select title,groupuuid from PxCourse where uuid='"+DbUtils.safeToWhereString(jsonform.getExt_uuid())+"'");
			if(list==null||list.size()==0){//修复空指针异常.
				responseMessage.setMessage("课程没找到!Ext_uuid="+jsonform.getExt_uuid());
				return false;
			}
			Object[] tmp=(Object[])list.get(0);
			ext_context=(String)tmp[0];
			grouuuid=(String)tmp[1];
		}else if(SystemConstants.common_type_pxbenefit==jsonform.getType().intValue()){
			List list=this.nSimpleHibernateDao.getHibernateTemplate().find("select title,groupuuid from Announcements where uuid='"+jsonform.getExt_uuid()+"'");
			if(list==null||list.size()==0){
				responseMessage.setMessage("优惠活动没找到!Ext_uuid="+jsonform.getExt_uuid());
				return false;
			}
			Object[] tmp=(Object[])list.get(0);
			ext_context=(String)tmp[0];
			grouuuid=(String)tmp[1];
		}else{
			responseMessage.setMessage("type未定义,无效."+jsonform.getType());
			return false;
		}
			
		
		
		jsonform.setUuid(null);
		PxTelConsultation data = new PxTelConsultation();

		BeanUtils.copyProperties(data, jsonform);
		data.setCreate_time(TimeUtils.getCurrentTimestamp());
		data.setGroup_uuid(grouuuid);
		data.setExt_context(PxStringUtil.getSubString(ext_context, 100));
		data.setTel(user.getLoginname());
		data.setTel_name(user.getLoginname());
		data.setUuid(null);
		
		this.nSimpleHibernateDao.getHibernateTemplate().save(data);

		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Appraise get(String uuid) {
		Appraise t = (Appraise) this.nSimpleHibernateDao.getObjectById(
				Appraise.class, uuid);

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
					"delete from Appraise where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(Appraise.class, uuid);

		}

		return true;
	}

	/**
	 * 
	 * @param groupuuid
	 * @param pData
	 * @param point
	 * @return
	 */
	public PageQueryResult queryByPage(String ext_uuid, PaginationData pData) {

		if (StringUtils.isBlank(ext_uuid)) {
			return null;
		}
		String hql = "from Appraise where ext_uuid='" + ext_uuid
				+ "' order by create_time desc";

		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHql(hql, pData);

		return pageQueryResult;
	}

	

	/**
	 * 
	 * @param groupuuid
	 * @param pData
	 * @param point
	 * @return
	 */
	public PageQueryResult queryMyByPage(String class_uuid,String create_useruuid, PaginationData pData) {
		if (StringUtils.isBlank(class_uuid)) {
			return null;
		}
		String hql = "from Appraise where  class_uuid='" + class_uuid+" and create_useruuid='"+create_useruuid+"'";
				hql+= "' order by create_time desc";
		pData.setPageSize(50);
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHql(hql, pData);

		return pageQueryResult;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return Appraise.class;
	}

}
