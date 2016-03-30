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
import com.company.news.entity.Parent;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPFamilyMembersJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.validate.CommonsValidate;
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
	public FPFamilyMembers add(FPFamilyMembersJsonform jsonform,
			ResponseMessage responseMessage, HttpServletRequest request) throws Exception {
		if (validateRequireAndLengthByRegJsonform(jsonform.getFamily_name(), 20, "家庭称呼", responseMessage)) {
			return null;
		}
		if (validateRequireByRegJsonform(jsonform.getFamily_uuid(), "关联家庭相册号", responseMessage)) {
			return null;
		}
		
		// TEL格式验证
			if (StringUtils.isNotBlank(jsonform.getTel())&&!CommonsValidate.checkCellphone(jsonform.getTel())) {
				responseMessage.setMessage("电话号码格式不正确！");
				return null;
			}
			
			if(!isRight(request, jsonform.getFamily_uuid(), responseMessage))return null;
		
		FPFamilyPhotoCollectionOfUpdate fPFamilyPhoto = (FPFamilyPhotoCollectionOfUpdate) this.nSimpleHibernateDao.getObjectById(
				FPFamilyPhotoCollectionOfUpdate.class, jsonform.getFamily_uuid());
			
		if(fPFamilyPhoto==null){
			responseMessage.setMessage("关联家庭相册不存在!");
			return null;
		}
		
		
		
//		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		if(StringUtils.isBlank(jsonform.getUuid())){
			FPFamilyMembers dbobj = new FPFamilyMembers();
			BeanUtils.copyProperties(dbobj, jsonform);
			dbobj.setCreate_time(TimeUtils.getCurrentTimestamp());
//			dbobj.setFamily_name(user.getName());
//			dbobj.setUser_uuid(user.getUuid());
//			dbobj.setTel(user.getLoginname());
//			dbobj.setFamily_uuid(dbobj.getUuid());
			// 有事务管理，统一在Controller调用时处理异常

			if(StringUtils.isNotBlank(jsonform.getTel())){
				Parent parent = (Parent) nSimpleHibernateDao.getObjectByAttribute(
						Parent.class, "loginname", jsonform.getTel());
				if(parent!=null)dbobj.setUser_uuid(parent.getUuid());
			}
			this.nSimpleHibernateDao.getHibernateTemplate().save(dbobj);
			return dbobj;
		}
		
		FPFamilyMembers dbobj = (FPFamilyMembers) this.nSimpleHibernateDao.getObjectById(
				FPFamilyMembers.class, jsonform.getUuid());
		
		if(dbobj==null){
			responseMessage.setMessage("更新记录不存在");
			return null;
		}
		//判断逻辑是否是家庭成员.
		//need_code
		
		
		
		
		
		//end code
		BeanUtils.copyProperties(dbobj, jsonform);
		dbobj.setCreate_time(TimeUtils.getCurrentTimestamp());
		if(StringUtils.isNotBlank(jsonform.getTel())){
			Parent parent = (Parent) nSimpleHibernateDao.getObjectByAttribute(
					Parent.class, "loginname", jsonform.getTel());
			if(parent!=null)dbobj.setUser_uuid(parent.getUuid());
		}
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

	
	public boolean isRight(HttpServletRequest request,String family_uuid, ResponseMessage responseMessage) {
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		
		String sql="select 1 from fp_family_members where family_uuid='"+family_uuid+"' and ( user_uuid='"+user.getUuid()+"' or tel='"+user.getLoginname()+"')";
		List list=this.nSimpleHibernateDao.createSqlQuery(sql).list();
		if(list.size()>0){
			return true;
		}
		responseMessage.setMessage("不是家庭成员,无权操作!");
		
		return false;
	}
	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean delete(HttpServletRequest request,String uuid, ResponseMessage responseMessage) {
		
		
	
		FPFamilyMembers dbobj = (FPFamilyMembers) this.nSimpleHibernateDao.getObjectById(
				FPFamilyMembers.class, uuid);
		
		//需要删除相关表. 
		//need_code
		if(dbobj==null){
			responseMessage.setMessage("参数错误,对象不存在");
			return false;
		}
		
		
		if(!isRight(request, dbobj.getFamily_uuid(), responseMessage))return false;
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
