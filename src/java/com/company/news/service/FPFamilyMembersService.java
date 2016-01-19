package com.company.news.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.commons.util.DbUtils;
import com.company.news.entity.FPFamilyMembers;
import com.company.news.entity.FPFamilyPhotoCollection;
import com.company.news.entity.FPFamilyPhotoCollectionOfUpdate;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPFamilyPhotoCollectionJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class FPFamilyMembersService extends AbstractService {


	/**
	 * 增加
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Object add(FPFamilyPhotoCollectionJsonform jsonform,
			ResponseMessage responseMessage, HttpServletRequest request) throws Exception {
		if (StringUtils.isBlank(jsonform.getTitle())) {
			responseMessage.setMessage("Title不能为空!");
			return false;
		}
		
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		if(StringUtils.isBlank(jsonform.getUuid())){
			FPFamilyPhotoCollection dbobj = new FPFamilyPhotoCollection();
			BeanUtils.copyProperties(dbobj, jsonform);
			dbobj.setCreate_useruuid(user.getUuid());
			dbobj.setCreate_time(TimeUtils.getCurrentTimestamp());
			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().save(dbobj);
			
			//添加家庭成员表.
			FPFamilyMembers  fPFamilyMembers=new FPFamilyMembers();
			fPFamilyMembers.setCreate_time(TimeUtils.getCurrentTimestamp());
			fPFamilyMembers.setFamily_name(user.getName());
			fPFamilyMembers.setUser_uuid(user.getUuid());
			fPFamilyMembers.setTel(user.getLoginname());
			fPFamilyMembers.setFamily_uuid(dbobj.getUuid());
			this.nSimpleHibernateDao.getHibernateTemplate().save(fPFamilyMembers);
			return dbobj;
		}
		
		FPFamilyPhotoCollectionOfUpdate dbobj = (FPFamilyPhotoCollectionOfUpdate) this.nSimpleHibernateDao.getObjectById(
				FPFamilyPhotoCollectionOfUpdate.class, jsonform.getUuid());
		
		if(dbobj==null){
			responseMessage.setMessage("更新记录不存在");
			return null;
		}
		//判断逻辑是否是家庭成员.
		//need_code
		
		
		
		
		
		//end code
		BeanUtils.copyProperties(dbobj, jsonform);
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(dbobj);
		return dbobj;
	}


	/**
	 * 查询我相关的(可以修改)
	 * 
	 * @return
	 */
	public List queryMy(String user_uuid) {

		String sql = "select uuid,title,herald,photo_count,status from fp_family_photo_collection where uuid in (select family_uuid from fp_family_members where user_uuid='"+DbUtils.safeToWhereString(user_uuid)+"')";
		
		List list = this.nSimpleHibernateDao.queryMapBySql(sql);
		
		return list;
	}
	
	/**
	 * 查询我相关的(可以修改)
	 * 
	 * @return
	 */
	public List listByFamily_uuid(String family_uuid) {

		String sql = "select * from fp_family_members where family_uuid='"+DbUtils.safeToWhereString(family_uuid)+"' order by create_time";
		
		List list = this.nSimpleHibernateDao.queryMapBySql(sql);
		
		return list;
	}
//	/**
//	 * 查询所有通知
//	 * 
//	 * @return
//	 */
//	public PageQueryResult query(String type, String user_uuid,PaginationData pData) {
//
//		String hql = "from Favorites where user_uuid='"+DbUtils.safeToWhereString(user_uuid)+"'";
//		if (StringUtils.isNotBlank(type))
//			hql += " and type=" + DbUtils.safeToWhereString(type);
//		pData.setOrderFiled("createtime");
//		pData.setOrderType("desc");
//		
//		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
//				.findByPaginationToHqlNoTotal(hql, pData);
//		
//		this.warpVoList(pageQueryResult.getData(), null);
//		return pageQueryResult;
//	}

	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean delete(HttpServletRequest request,String uuid, ResponseMessage responseMessage) {
		
		
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		//防止sql注入.
		if(DbUtils.isSqlInjection(uuid,responseMessage))return false;
		
		FPFamilyPhotoCollection dbobj = (FPFamilyPhotoCollection) this.nSimpleHibernateDao.getObjectById(
				FPFamilyPhotoCollection.class, uuid);
		
		if(!user.getUuid().equals(dbobj.getCreate_useruuid())){
			responseMessage.setMessage("只有创建人,才能删除");
			return false;
		}
		
		//需要删除相关表. 
		//need_code
		
		
		this.nSimpleHibernateDao.delete(dbobj);
		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public FPFamilyPhotoCollection get(String uuid) throws Exception {
		FPFamilyPhotoCollection favorites = (FPFamilyPhotoCollection) this.nSimpleHibernateDao.getObjectById(
				FPFamilyPhotoCollection.class, uuid);

		return favorites;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
