package com.company.news.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.company.mq.JobDetails;
import com.company.mq.MQUtils;
import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.cache.PxRedisCache;
import com.company.news.cache.UserCache;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.commons.util.UploadFileUtils;
import com.company.news.commons.util.upload.DiskIUploadFile;
import com.company.news.commons.util.upload.IUploadFile;
import com.company.news.commons.util.upload.OssIUploadFile;
import com.company.news.entity.FPMovie;
import com.company.news.entity.FPPhotoItem;
import com.company.news.entity.FPPhotoItemOfUpdate;
import com.company.news.form.FPPhotoItemForm;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPPhotoItemJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.RestConstants;
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
public class FPPhotoItemService extends AbstractService {
	private static IUploadFile iUploadFile;
	
	public static final String uploadfiletype = ProjectProperties.getProperty(
			"uploadfiletype", "oss");
	
	static {
		if (uploadfiletype.equals("oss"))
			iUploadFile = new OssIUploadFile();
		else
			iUploadFile = new DiskIUploadFile();
	}
	@Autowired
	FPFamilyPhotoCollectionService fPFamilyPhotoCollectionService;
	@Autowired
	BaseDianzanService baseDianzanService;
	
	@Autowired
	BaseReplyService baseReplyService;
	String Selectsql=" SELECT t1.uuid,t1.family_uuid,t1.photo_time,t1.create_useruuid,t1.path,t1.address,t1.note,t1.phone_type,t1.create_time";
	String SqlFrom=" FROM fp_photo_item t1 ";

	
	
	

	/**
	 * 查询根据时间范围查询，新数据总数和变化数据总数。
	 * 修复为只查询新数据范围
	 * @return
	 */
	public boolean  queryOfNewDataCount(String family_uuid,PaginationData pData,ModelMap model) {
		
		//获取MaxTime 时间后的新数据总数量
		String countNewDataSql="select count(*)  FROM fp_photo_item t1 ";
		countNewDataSql += " where   t1.status<2 and   t1.family_uuid ='"+DBUtil.safeToWhereString(family_uuid)+"'";
			 countNewDataSql += " and   t1.create_time >"+DBUtil.queryDateStringToDateByDBType(pData.getMaxTime());
		Object  countNewData=this.nSimpleHibernateDao.createSQLQuery(countNewDataSql).uniqueResult();
		
//		//获取MinTime 和MaxTime 时间直接变化的数据
//		String countUpdateDataSql="select count(*)  FROM fp_photo_item t1 ";
//		countUpdateDataSql += " where   t1.family_uuid ='"+DBUtil.safeToWhereString(family_uuid)+"'";
//		countUpdateDataSql += " and   t1.update_time >"+DBUtil.queryDateStringToDateByDBType(pData.getMaxTime());//在最大时间后更新的数据.
//		
//		countUpdateDataSql += " and   t1.create_time <="+DBUtil.queryDateStringToDateByDBType(pData.getMaxTime());
//		if(StringUtils.isNotBlank(pData.getMinTime())){
//			countUpdateDataSql += " and   t1.create_time >="+DBUtil.queryDateStringToDateByDBType(pData.getMinTime());
//		}
//		Object  countUpdateData=session.createSQLQuery(countUpdateDataSql).uniqueResult();
//		
		model.put(RestConstants.Return_newDataCount, countNewData);
//		model.put(RestConstants.Return_updateDataCount, countUpdateData);
		//返回最后一条的数据。
		return true;
	}
	
	

	/**
	 * 查询增量更新数据，的uuid
	 * 
	 * @return
	 */
	public PageQueryResult queryOfUpdate(SessionUserInfoInterface user ,String family_uuid, String updateTime,PaginationData pData,ModelMap model) {
		String selectsql=" SELECT t1.uuid as u,t1.status as s ";
		String sqlFrom=" FROM fp_photo_item t1 ";
		sqlFrom += " where   t1.family_uuid ='"+DBUtil.safeToWhereString(family_uuid)+"'";
		
		String sql=selectsql+sqlFrom;
		pData.setPageSize(10000);
		
		sql += " and   t1.update_time >"+DBUtil.queryDateStringToDateByDBType(updateTime);//在最大时间后更新的数据.
		
		sql += " and   t1.create_time <="+DBUtil.queryDateStringToDateByDBType(pData.getMaxTime());
		if(StringUtils.isNotBlank(pData.getMinTime())){
			sql += " and   t1.create_time >="+DBUtil.queryDateStringToDateByDBType(pData.getMinTime());
		}
		sql += " order by t1.create_time desc";
		 
		Query  query =this.nSimpleHibernateDao.createSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);
		List<Map> list=pageQueryResult.getData();
		this.warpMapList(list, user);
		//返回最后一条的数据。
		return pageQueryResult;
	}

	/**
	 * 查询增量更新
	 * 
	 * @return
	 */
	public PageQueryResult queryOfIncrement(SessionUserInfoInterface user ,String family_uuid, String user_uuid,PaginationData pData,ModelMap model) {
		pData.setPageSize(50);
		String selectsql=Selectsql;
		String sqlFrom=SqlFrom;
		//修复，增量查询是不查询已经删除了得
		sqlFrom += " where  t1.status<2 and t1.family_uuid ='"+DBUtil.safeToWhereString(family_uuid)+"'";
		
		
		String sql=sqlFrom;
		pData.setPageSize(50);
		////使用创建时间做分页显示,beforeTime 取 2016-01-15 13:13 之前的数据.按照创建时间排倒序
		 if(StringUtils.isNotBlank(pData.getMaxTime())){
				sql += " and   t1.create_time <"+DBUtil.queryDateStringToDateByDBType(pData.getMaxTime());
				sql += " order by t1.create_time desc";
				
		}else if(StringUtils.isNotBlank(pData.getMinTime())){
				sql += " and   t1.create_time >"+DBUtil.queryDateStringToDateByDBType(pData.getMinTime());
				sql += " order by t1.create_time asc";
		}else{//默认查询,当前时间倒叙
			  sql += " order by t1.create_time desc";
		}
		 
		Query  query =this.nSimpleHibernateDao.createSQLQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);
		List<Map> list=pageQueryResult.getData();
		
		//
		Date lastTime=null;
		try {
			if(list.size()>0){
				 Map lastObj=list.get(list.size()-1);
				 lastTime=(Date)lastObj.get("create_time");
				 String lastuuid=(String)lastObj.get("uuid");
				 Calendar nextSecond=TimeUtils.date2Calendar(lastTime);
				 nextSecond.add(Calendar.SECOND, 1);
				 
					//时间戳精确到秒，数据没取完整情况下，需要在根据最后一条数据的时间取数据，如果有则加入list。list的size有可能大于PageSize。防止少去数据bug。
					if(list.size()==pData.getPageSize()){
						String tmpSql=selectsql+sqlFrom+"";
						tmpSql += " and   t1.uuid !='"+lastuuid+"'";
						tmpSql += " and   t1.create_time >="+DBUtil.stringToDateByDBType(TimeUtils.getDateTimeString(lastTime));
						tmpSql += " and   t1.create_time <"+DBUtil.stringToDateByDBType(TimeUtils.getDateTimeString(nextSecond.getTime()));
						  query = this.nSimpleHibernateDao.createSQLQuery(tmpSql);
						  query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
						  List tmpList=query.list();
						  list.addAll(tmpList);
					}
					
					
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		this.warpMapList(list, user);
		//返回最后一条的数据。
		model.put(RestConstants.Return_LastTime, lastTime);
		return pageQueryResult;
	}
	
	
	
	/**
	 * 分页同步所有上传的图片标识
	 * 
	 * @return
	 */
	public PageQueryResult queryAlreadyUploaded(String phone_uuid,PaginationData pData) {
		pData.setPageSize(100);
		String selectsql="SELECT t1.md5,t1.family_uuid ";
		String sql=" FROM fp_photo_item t1 ";
		
		//过滤删除掉的.
		 if (StringUtils.isNotBlank(phone_uuid)) {//根据家庭uuid查询
			sql += " where t1.status<2 and  t1.phone_uuid ='"+phone_uuid+"'";
		}
		 
		  sql += " order by t1.create_time asc";
		
		Query  query =this.nSimpleHibernateDao.createSQLQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);
		List<Map> list=pageQueryResult.getData();
		return pageQueryResult;
	}

	/**
	 * 查询所有通知
	 * 
	 * @return
	 */
	public PageQueryResult query(SessionUserInfoInterface user ,String family_uuid, String user_uuid,PaginationData pData) {
		String selectsql=Selectsql;
		String sql=SqlFrom;
		
		 if (StringUtils.isNotBlank(family_uuid)) {//根据家庭uuid查询
			sql += " where   t1.family_uuid ='"+DBUtil.safeToWhereString(family_uuid)+"'";
		}else if (StringUtils.isNotBlank(user_uuid)) {//查询用户关联家庭照片.或者自己上传的
			sql += " LEFT JOIN  fp_family_members t2 on  t2.family_uuid=t1.family_uuid ";
			sql += " where t1.create_useruuid='"+DBUtil.safeToWhereString(user_uuid)+"' or  t2.user_uuid ='"+DBUtil.safeToWhereString(user_uuid)+"'";
		}
		////使用创建时间做分页显示,beforeTime 取 2016-01-15 13:13 之前的数据.按照创建时间排倒序
		 if(StringUtils.isNotBlank(pData.getMaxTime())){
				pData.setPageNo(1);
				sql += " and   t1.create_time <"+DBUtil.queryDateStringToDateByDBType(pData.getMaxTime());
				
				  sql += " order by t1.create_time asc";
		}else if(StringUtils.isNotBlank(pData.getMinTime())){
				pData.setPageNo(1);
				sql += " and   t1.create_time >"+DBUtil.queryDateStringToDateByDBType(pData.getMinTime());
				
				  sql += " order by t1.create_time desc";
		}else{//默认查询,当前时间倒叙
			  sql += " order by t1.create_time desc";
		}
		Query  query =this.nSimpleHibernateDao.createSQLQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);
		List<Map> list=pageQueryResult.getData();
		this.warpMapList(list, user);
		return pageQueryResult;
	}
	

	/**
	 * 查询收藏的
	 * 
	 * @return
	 */
	public PageQueryResult queryMyFavorites(SessionUserInfoInterface user,PaginationData pData) {
		String selectsql=Selectsql;
		String sql=" FROM fp_photo_item t1  left join fp_photo_favorite f2 on  t1.uuid=f2.rel_uuid";
		sql += " where   f2.create_useruuid ='"+user.getUuid()+"'";
		 sql += " order by t1.create_time desc";
		Query  query =this.nSimpleHibernateDao.createSqlQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,sql, pData);
		List<Map> list=pageQueryResult.getData();
		this.warpMapList(list, user);
		return pageQueryResult;
	}

	/**
	 * 增加
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean addFavorites(SessionUserInfoInterface user,String uuid,
			ResponseMessage responseMessage) throws Exception {
		
		if (StringUtils.isBlank(uuid)) {
			responseMessage.setMessage("uuid必填");
			return false;
		}
		
		
		String insertsql="insert into fp_photo_favorite(rel_uuid,create_useruuid,create_time) values('"+uuid+"','"+user.getUuid()+"',now())";
		try {
			this.nSimpleHibernateDao.createSQLQuery(insertsql).executeUpdate();
		} catch (org.hibernate.exception.ConstraintViolationException e) {
			responseMessage.setMessage("你已收藏!");
			return false;
		}

		return true;
	}

	/**
	 * 是否可以收藏
	 * 
	 * @param loginname
	 * @return
	 */
	public boolean isFavorites(String user_uuid,String reluuid) {
		if(StringUtils.isBlank(reluuid)||StringUtils.isBlank(user_uuid))return false;
		List list = nSimpleHibernateDao.createSQLQuery("select rel_uuid from fp_photo_favorite where rel_uuid='"+reluuid+"' and create_useruuid='"+user_uuid+"'").list();

		if (list != null&&list.size()>0)// 已被占用
			return false;
		else
			return true;

	}
	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean deleteFavorites(SessionUserInfoInterface parent,String rel_uuid, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(rel_uuid)) {

			responseMessage.setMessage("ID不能为空！");
			return false;
		}
		
		//rel_uuid,create_useruuid
		String insertsql="delete from fp_photo_favorite where rel_uuid='"+rel_uuid+"' and create_useruuid='"+parent.getUuid()+"'";
		int count=this.nSimpleHibernateDao.createSqlQuery(insertsql).executeUpdate();
		
		
		
		if(count>0)
			return true;
		responseMessage.setMessage("无数据");
		return false;
	}

	
	private void warpMap(Map o, SessionUserInfoInterface user) {
		try {
			o.put("path", PxStringUtil.imgFPPhotoUrlByRelativePath_sub((String)o.get("path")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List warpMapList(List<Map> list,SessionUserInfoInterface user ) {
		
//		String uuids="";
		for(Map o:list){
			warpMap(o,user);
//			uuids+=o.get("uuid")+",";
		}
		UserRedisCache.warpListMapByUserCache(list, "create_useruuid", "create_user", null);
//		try {
//			countService.getIncrCountByExt_uuids(uuids);
//			Map dianZanMap=classNewsReplyService.getDianzanDianzanMap(uuids, user);
//			for(Map o:list){
////				o.put("count", countMap.get(o.get("uuid")));
//				Object vo= (Object)dianZanMap.get(o.get("uuid"));
//				if(vo==null)vo= new DianzanListVO();
//				o.put("dianzan",vo);
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return list;
	}
	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 * @throws Exception 
	 */
	public boolean delete(HttpServletRequest request,String uuid, ResponseMessage responseMessage) throws Exception {
		
		
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		//防止sql注入.
		if(DBUtil.isSqlInjection(uuid,responseMessage))return false;
		
		FPPhotoItem dbobj = (FPPhotoItem) this.nSimpleHibernateDao.getObjectById(
				FPPhotoItem.class, uuid);
		
		if(!user.getUuid().equals(dbobj.getCreate_useruuid())){
			responseMessage.setMessage("只有创建人,才能删除");
			return false;
		}
		
		//需要删除相关表. 
		//need_code
		try {
			iUploadFile.deleteFile(dbobj.getPath());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		dbobj.setStatus(SystemConstants.FPPhotoItem_Status_delete);
		dbobj.setUpdate_time(TimeUtils.getCurrentTimestamp());
		this.nSimpleHibernateDao.save(dbobj);
		
		//删除相关点赞,评论,收藏.
		String sql="delete from fp_photo_favorite where rel_uuid='"+uuid+"'";
		this.nSimpleHibernateDao.createSQLQuery(sql).executeUpdate();
		try {
		baseDianzanService.update_deleteForRel_uuid(uuid, SystemConstants.common_type_FPPhotoItem, responseMessage);
		baseReplyService.update_deleteForRel_uuid(uuid, SystemConstants.common_type_FPPhotoItem, responseMessage);
		
			PxRedisCache.getPxRedisCache().getfPFamilyPhotoCollectionCounter().getReduceCountByExt_uuid(dbobj.getFamily_uuid());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	/**
	 * 获取状态
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public Map getStatus(String uuid) throws Exception {
		
		String sqlFrom="select t1.status "+SqlFrom;
		sqlFrom += " where   t1.uuid ='"+uuid+"'";
		
		List<Map>  list =nSimpleHibernateDao.queryMapBySql(sqlFrom);
		 Map  map=null;
		if(list.size()>0){
			   map=list.get(0);
		}
		return map;
	}
	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public Map get(String uuid) throws Exception {
		
		String sqlFrom=Selectsql+" ,t1.status "+SqlFrom;
		sqlFrom += " where   t1.uuid ='"+uuid+"'";
		
		List<Map>  list =nSimpleHibernateDao.queryMapBySql(sqlFrom);
		 Map  map=null;
		if(list.size()>0){
			   map=list.get(0);
			 
			 warpMap(map, null);
			 UserCache user=UserRedisCache.getUserCache("create_useruuid");
			 if(user!=null)map.put("create_user", user.getN());
		}

		return map;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}


	public FPPhotoItem uploadImg(FPPhotoItemForm form, CommonsMultipartFile file,
			ResponseMessage responseMessage, HttpServletRequest request) throws Exception {
		SessionUserInfoInterface user=SessionListener.getUserInfoBySession(request);
		// 过滤文件大小等
		if (!UploadFileUtils.fileFilter(responseMessage, file.getSize())) {
			return null;
		}
		
		String extension=FilenameUtils.getExtension(file.getOriginalFilename());
		
	
		
		FPPhotoItem uploadFile = new FPPhotoItem();

		
		BeanUtils.copyProperties(uploadFile, form);
		
		uploadFile.setFile_size(Long.valueOf(file.getSize()));
		uploadFile.setCreate_useruuid(user.getUuid());
		uploadFile.setCreate_time(TimeUtils.getCurrentTimestamp());
		uploadFile.setUpdate_time(TimeUtils.getCurrentTimestamp());
		uploadFile.setPhoto_time(TimeUtils.string2Timestamp(TimeUtils.DEFAULTFORMAT, form.getPhoto_time()));
//		uploadFile.setAddress(form.getAddress());
//		uploadFile.setNote(form.getNote());
//		uploadFile.setFamily_uuid(form.getFamily_uuid());
//		uploadFile.setMd5(form.getMd5());
			
		if(uploadFile.getPhoto_time()==null){//如果上传拍照时间,不正确或为null,则设置为拍照时间.
			uploadFile.setPhoto_time(uploadFile.getCreate_time());
		}
		this.nSimpleHibernateDao.getHibernateTemplate().save(uploadFile);
		
		//2016/uuid.png
		if(StringUtils.isBlank(extension))extension="jpg";
		String filePath ="fp/"+TimeUtils.getCurrentTime("yyyy")+"/"+uploadFile.getUuid()+"."+extension;
		
		uploadFile.setPath(filePath);

		// 上传文件
		if (iUploadFile.uploadFile(file.getInputStream(), filePath, null)) {
			
			
			

			Map map=new HashMap();
	    	map.put("uuid", uploadFile.getFamily_uuid());
//	    	map.put("create_useruuid",user.getUuid());
	    	map.put("title",user.getName()+"上传了新照片");
			
	    	JobDetails job=new JobDetails("doJobMqIservice","sendFPFamilyPhotoCollection",map);
			MQUtils.publish(job);
			Long cacheCount=null;
			try {
				cacheCount = PxRedisCache.getPxRedisCache().getfPFamilyPhotoCollectionCounter().getIncrCountByExt_uuid(uploadFile.getFamily_uuid());
				if(cacheCount==null||cacheCount<=1){
					 cacheCount=fPFamilyPhotoCollectionService.getfamilyPhotoCount(uploadFile.getFamily_uuid());
					PxRedisCache.getPxRedisCache().getfPFamilyPhotoCollectionCounter().setCountByExt_uuid(uploadFile.getFamily_uuid(), cacheCount);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			return uploadFile;
		} else {
			this.logger.error("upload img faild,remove item,uuid="+uploadFile.getUuid());
			responseMessage.setMessage("上传文件失败");
			this.nSimpleHibernateDao.getHibernateTemplate().delete(uploadFile);
			return null;
		}
	}
	
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

	public boolean update(FPPhotoItemJsonform jsonform,
			ResponseMessage responseMessage, HttpServletRequest request) throws Exception {
		if (StringUtils.isBlank(jsonform.getUuid())) {
			responseMessage.setMessage("uuid 不能为空!");
			return false;
		}
		SessionUserInfoInterface user=SessionListener.getUserInfoBySession(request);
		FPPhotoItemOfUpdate obj = (FPPhotoItemOfUpdate) nSimpleHibernateDao.getObject(FPPhotoItemOfUpdate.class,
				jsonform.getUuid());
		
		if(obj==null){
			responseMessage.setMessage("数据不存在.uuid ="+jsonform.getUuid());
			return false;
		}
		
		if(!isRight(request, obj.getFamily_uuid(), responseMessage)){
			return false;
		}
		
		obj.setUpdate_time(TimeUtils.getCurrentTimestamp());
		obj.setNote(jsonform.getNote());
		obj.setAddress(jsonform.getAddress());
		obj.setStatus(SystemConstants.FPPhotoItem_Status_update);
		
		this.nSimpleHibernateDao.save(obj);
		

		Map map=new HashMap();
    	map.put("uuid", obj.getFamily_uuid());
//    	map.put("create_useruuid",user.getUuid());
    	map.put("title",user.getName()+"修改了照片备注");
		
    	JobDetails job=new JobDetails("doJobMqIservice","sendFPPhotoItem",map);
		MQUtils.publish(job);
		return true;
	}
	
	/**
	 * 查询增量更新
	 * 
	 * @return
	 */
	public PageQueryResult queryForMoviePhoto_uuids(String photo_uuids,PaginationData pData,ModelMap model) {
		
		
		
		String selectsql=" SELECT t1.path,t1.address,t1.note ";
		
		String sqlFrom=SqlFrom;
		sqlFrom += " where  t1.uuid in("+DBUtil.stringsToWhereInValue(photo_uuids)+")";
		
		
		String sql=sqlFrom;
		
		////使用创建时间做分页显示,beforeTime 取 2016-01-15 13:13 之前的数据.按照创建时间排倒序
		 if(StringUtils.isNotBlank(pData.getMaxTime())){
				sql += " and   t1.create_time <"+DBUtil.queryDateStringToDateByDBType(pData.getMaxTime());
		}
		 sql += " order by t1.create_time asc";
		 
		Query  query =this.nSimpleHibernateDao.createSqlQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);
		List<Map> list=pageQueryResult.getData();
		
		for(Map o:list){
			try {
				o.put("path", PxStringUtil.imgUrlByRelativePath_sub((String)o.get("path"),"640w"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//返回最后一条的数据。
		return pageQueryResult;
	}
	/**
	 * 查询增量更新
	 * 
	 * @return
	 */
	public List<Map> queryForMovieUuid(SessionUserInfoInterface user,String movie_uuid,ResponseMessage responseMessage) {
		
		
		 
 	   FPMovie fPMovie= (FPMovie)this.nSimpleHibernateDao.getObject(FPMovie.class, movie_uuid);
 	   if(fPMovie==null){
 		  responseMessage.setMessage("相册不存在!movie_uuid="+movie_uuid);
 		   return null;
 	   }
 	   
		String selectsql=Selectsql;
		String sqlFrom=SqlFrom;
		//修复，增量查询是不查询已经删除了得
		sqlFrom += " where t1.uuid in("+DBUtil.stringsToWhereInValue(fPMovie.getPhoto_uuids())+")";
		
		String sql=sqlFrom;
	
		  sql += " order by t1.photo_time asc";
		Query  query =this.nSimpleHibernateDao.createSQLQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//		String countsql="select count(*) "+sql;
		List<Map> list=query.list();
		this.warpMapList(list, user);
		return list;
	}
	
}
