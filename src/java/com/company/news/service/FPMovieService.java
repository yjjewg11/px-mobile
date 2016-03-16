package com.company.news.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.company.mq.JobDetails;
import com.company.mq.MQUtils;
import com.company.news.SystemConstants;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.FPMovie;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPMovieJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class FPMovieService extends AbstractService {
	/**
	 * 增加
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public FPMovie save(FPMovieJsonform jsonform,
			ResponseMessage responseMessage, HttpServletRequest request) throws Exception {
		if (StringUtils.isBlank(jsonform.getTitle())) {
			responseMessage.setMessage("Title不能为空!");
			return null;
		}
		if (StringUtils.isBlank(jsonform.getPhoto_uuids())) {
			responseMessage.setMessage("请选择照片");
			return null;
		}
		;
		
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		if(StringUtils.isBlank(jsonform.getUuid())){
			FPMovie dbobj = new FPMovie();
			BeanUtils.copyProperties(dbobj, jsonform);
			dbobj.setCreate_useruuid(user.getUuid());
			//修复修改保存时,没有保存图片数量.
			dbobj.setPhoto_count(Long.valueOf(StringUtils.countMatches(jsonform.getPhoto_uuids(), ",")));
			
			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().save(dbobj);
			return dbobj;
		}//end 新建
		
		//修改
		
		FPMovie dbobj = (FPMovie) this.nSimpleHibernateDao.getObjectById(
				FPMovie.class, jsonform.getUuid());
		
		if(dbobj==null){
			responseMessage.setMessage("更新记录不存在");
			return null;
		}
		if(!user.getUuid().equals(dbobj.getCreate_useruuid())){
			responseMessage.setMessage("不是创建人不能修改");
			return null;
		}
		//判断逻辑是否是家庭成员.
		//need_code
		//end code
		BeanUtils.copyProperties(dbobj, jsonform);
		
		dbobj.setPhoto_count(Long.valueOf(StringUtils.countMatches(jsonform.getPhoto_uuids(), ",")));
		dbobj.setCreate_time(TimeUtils.getCurrentTimestamp());
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(dbobj);
		
		
		if(SystemConstants.Check_status_fabu.equals(dbobj.getStatus())){
			Map map=new HashMap();
	    	map.put("uuid", dbobj.getUuid());
//	    	map.put("create_useruuid",user.getUuid());
	    	map.put("title",user.getName()+"发布:"+dbobj.getTitle());
	    	JobDetails job=new JobDetails("doJobMqIservice","sendFPMovie",map);
			MQUtils.publish(job);
		}
	
		
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
		
		FPMovie dbobj = (FPMovie) this.nSimpleHibernateDao.getObjectById(
				FPMovie.class, uuid);
		if(dbobj==null){
			responseMessage.setMessage("相册不存在!");
			return false;
		}
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
	public Map get(String uuid) throws Exception {
		
		String sql= " SELECT t1.uuid,t1.create_time,t1.title,t1.herald,t1.photo_count,t1.create_useruuid,t1.status,t1.photo_uuids  FROM fp_movie t1   where   t1.uuid ='"+uuid+"'";
		
		Query  query =this.nSimpleHibernateDao.createSqlQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> list=query.list();
		 if(list.isEmpty()){
			 return null;
		 }
	    warpMapList(list,null);
	   
		return list.get(0);
	}
	
	

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	String Selectsql=" SELECT t1.uuid, date_format(t1.create_time,'%Y-%m-%d') as create_time,t1.title,t1.herald,t1.photo_count,t1.create_useruuid,t1.status ";
	String SqlFrom=" FROM fp_movie t1 ";

	/**
	 * 查询相册
	 * @param user
	 * @param pData
	 * @param model
	 * @return
	 */
	public PageQueryResult query(SessionUserInfoInterface user,
			PaginationData pData, ModelMap model) {
		String selectsql=Selectsql;
		String sqlFrom=SqlFrom;
		sqlFrom += " where   t1.status ="+SystemConstants.Check_status_fabu;
		String sql=sqlFrom;
		pData.setPageSize(10);
		
		  sql += " order by t1.create_time desc";
		 
		Query  query =this.nSimpleHibernateDao.createSqlQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);

	    warpMapList(pageQueryResult.getData(),null);
		return pageQueryResult;
	}
	/**
	 * 查询我创建的相册
	 * @param user
	 * @param pData
	 * @param model
	 * @return
	 */
	public PageQueryResult queryMy(SessionUserInfoInterface user,
			PaginationData pData, ModelMap model) {
		String selectsql=Selectsql;
		String sqlFrom=SqlFrom;
		sqlFrom += " where   t1.create_useruuid ='"+user.getUuid()+"'";
		String sql=sqlFrom;
		pData.setPageSize(10);
		
		  sql += " order by t1.create_time desc";
		 
		Query  query =this.nSimpleHibernateDao.createSqlQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);

	    warpMapList(pageQueryResult.getData(),null);
		return pageQueryResult;
	}
	
	private void warpMap(Map o, SessionUserInfoInterface user) {
		try {
			o.put("herald", PxStringUtil.imgFPPhotoUrlByRelativePath_sub((String)o.get("herald")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Autowired
	private BaseDianzanService baseDianzanService;
	@Autowired
	private BaseReplyService baseReplyService;
	
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List warpMapList(List<Map> list,SessionUserInfoInterface user ) {
		UserRedisCache.warpListMapByUserCache(list, "create_useruuid", "create_username", null);
		
		String uuids=PxStringUtil.getMapVaules(list, "uuid");
		String user_uuid=null;
		if(user!=null){
			user_uuid=user.getUuid();
		}
		 Map<String,Map>  dianZanListMap=baseDianzanService.queryByRel_uuids(uuids, SystemConstants.common_type_FPMovie, user_uuid);
		 
		 Map<String,Map>  baseReplyListMap=baseReplyService.queryCountByRel_uuids(uuids, SystemConstants.common_type_FPMovie);
			
		 
		for(Map o:list){
			//添加点赞对象
			Map dianzanMap=dianZanListMap.get(o.get("uuid"));
			if(dianzanMap==null){
				dianzanMap=new HashMap();
				dianzanMap.put("dianzan_count", 0);
				dianzanMap.put("yidianzan", 0);
			}
			if(dianzanMap.get("yidianzan")==null){
				dianzanMap.put("yidianzan", 0);
			}
			o.put(RestConstants.Return_ResponseMessage_dianZan, dianzanMap);
			
			
			Map baseReplyMap=baseReplyListMap.get(o.get("uuid"));
			Object reply_count=0;
			if(baseReplyMap!=null){
				reply_count=baseReplyMap.get("reply_count");
			}
			o.put(RestConstants.Return_ResponseMessage_reply_count, reply_count);
			
			//点击评论
			
			warpMap(o,user);
		}
		
		return list;
	}
}
