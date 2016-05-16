package com.company.news.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.cache.PxRedisCache;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.FPFamilyMembers;
import com.company.news.entity.FPFamilyPhotoCollection;
import com.company.news.entity.FPFamilyPhotoCollectionOfUpdate;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPFamilyPhotoCollectionJsonform;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class FPFamilyPhotoCollectionService extends AbstractService {

	/**
	 * 增加第一个家庭相册,自动根据宝贝关联关系创建.
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Object updateFirst(
			ResponseMessage responseMessage, HttpServletRequest request) throws Exception {
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
	
			FPFamilyPhotoCollection dbobj = new FPFamilyPhotoCollection();
			dbobj.setTitle(user.getName()+"家相册");
			dbobj.setCreate_useruuid(user.getUuid());
			dbobj.setCreate_time(TimeUtils.getCurrentTimestamp());
			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().save(dbobj);
			
			//parent_uuid,student_name,typename,tel 
			//211ee9bf-1645-4797-a122-60e75462dfdc	周星星1	妈妈	13628037996
			//
			String sql="select DISTINCT parent_uuid,typename,tel,student_name from px_studentcontactrealation where student_uuid in ";
			sql+=" (select student_uuid from px_studentcontactrealation where parent_uuid='"+user.getUuid()+"')";
			List<Map> list=this.nSimpleHibernateDao.queryMapBySql(sql);
			//有宝贝的,添加关联家庭成员
			if(list.size()>0){
				for(Map o:list){
					
					dbobj.setTitle(o.get("student_name")+"家相册");
					FPFamilyMembers  fPFamilyMembers=new FPFamilyMembers();
					fPFamilyMembers.setCreate_time(TimeUtils.getCurrentTimestamp());
					fPFamilyMembers.setFamily_name((String)o.get("typename"));
					fPFamilyMembers.setUser_uuid((String)o.get("parent_uuid"));
					fPFamilyMembers.setTel((String)o.get("tel"));
					fPFamilyMembers.setFamily_uuid(dbobj.getUuid());
					
					if(fPFamilyMembers.getTel()!=null&&fPFamilyMembers.getTel().length()>11){
						fPFamilyMembers.setTel(null);
					}
					this.nSimpleHibernateDao.getHibernateTemplate().save(fPFamilyMembers);
				}
				this.nSimpleHibernateDao.getHibernateTemplate().save(dbobj);
			}else{
				//否则,只添加自己家庭成员表.
				FPFamilyMembers  fPFamilyMembers=new FPFamilyMembers();
				fPFamilyMembers.setCreate_time(TimeUtils.getCurrentTimestamp());
				fPFamilyMembers.setFamily_name(user.getName());
				fPFamilyMembers.setUser_uuid(user.getUuid());
				fPFamilyMembers.setTel(user.getLoginname());
				
				if(fPFamilyMembers.getTel()!=null&&fPFamilyMembers.getTel().length()>11){
					fPFamilyMembers.setTel(null);
				}
				fPFamilyMembers.setFamily_uuid(dbobj.getUuid());
				this.nSimpleHibernateDao.getHibernateTemplate().save(fPFamilyMembers);
			}
			
			return dbobj;
		
	}

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
			
			if(fPFamilyMembers.getTel()!=null&&fPFamilyMembers.getTel().length()>11){
				fPFamilyMembers.setTel(null);
			}
			fPFamilyMembers.setFamily_uuid(dbobj.getUuid());
			this.nSimpleHibernateDao.getHibernateTemplate().save(fPFamilyMembers);
			return dbobj.getUuid();
		}//end 新建
		
		//修改
		
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
		return dbobj.getUuid();
	}


	/**
	 * 查询我相关的(可以修改)
	 * 
	 * @return
	 */
	public List queryMy(String user_uuid) {

		String sql = "select uuid,title,herald,photo_count,status,create_time from fp_family_photo_collection where uuid in (select family_uuid from fp_family_members where user_uuid='"+DbUtils.safeToWhereString(user_uuid)+"')";
		
		List list = this.nSimpleHibernateDao.queryMapBySql(sql);
		warpMapList(list,null);
		return list;
	}
	
	/**
	 * 查询我相关的(可以修改)
	 * 
	 * @return
	 */
	public Long getfamilyPhotoCount(String uuid) {
		//
		String sql = "select count(*) from fp_photo_item where status <"+SystemConstants.FPPhotoItem_Status_delete+" and family_uuid ='"+uuid+"'";
		
		Object list = this.nSimpleHibernateDao.createSQLQuery(sql).uniqueResult();
		
		return Long.valueOf(list+"");
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List warpMapList(List<Map> list,SessionUserInfoInterface user ) {
		if(list.size()==0)return list;
		for(Map o:list){
			warpMap(o);
		}
		return list;
	}
	private void warpMap(Map o) {
			String uuid=(String)o.get("uuid");

		Long cacheCount=null;
		try {
			cacheCount = PxRedisCache.getPxRedisCache().getfPFamilyPhotoCollectionCounter().getCountByExt_uuid(uuid);
			if(cacheCount==null){
				 cacheCount=getfamilyPhotoCount(uuid);
				PxRedisCache.getPxRedisCache().getfPFamilyPhotoCollectionCounter().setCountByExt_uuid(uuid, cacheCount);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		o.put("photo_count", cacheCount);
		
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
		if(DBUtil.isSqlInjection(uuid,responseMessage))return false;
		
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
		if(favorites==null)return null;
		
		Long cacheCount=null;
		try {
			cacheCount = PxRedisCache.getPxRedisCache().getfPFamilyPhotoCollectionCounter().getCountByExt_uuid(uuid);
			if(cacheCount==null){
				 cacheCount=getfamilyPhotoCount(uuid);
				PxRedisCache.getPxRedisCache().getfPFamilyPhotoCollectionCounter().setCountByExt_uuid(uuid, cacheCount);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.nSimpleHibernateDao.getHibernateTemplate().evict(favorites);
		favorites.setPhoto_count(cacheCount);
		return favorites;
	}
	
	

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
