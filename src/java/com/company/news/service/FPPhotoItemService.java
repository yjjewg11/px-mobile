package com.company.news.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.company.news.ProjectProperties;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.commons.util.UploadFileUtils;
import com.company.news.commons.util.upload.DiskIUploadFile;
import com.company.news.commons.util.upload.IUploadFile;
import com.company.news.commons.util.upload.OssIUploadFile;
import com.company.news.entity.FPPhotoItem;
import com.company.news.entity.FPPhotoItemOfUpdate;
import com.company.news.form.FPPhotoItemForm;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPPhotoItemJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
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


	/**
	 * 查询所有通知
	 * 
	 * @return
	 */
	public PageQueryResult queryOfIncrement(SessionUserInfoInterface user ,String family_uuid, String user_uuid,PaginationData pData) {
		Session session=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		String selectsql=" SELECT t1.uuid,t1.photo_time,t1.create_useruuid,t1.path,t1.address,t1.note,t1.type ";
		String sql=" FROM fp_photo_item t1 ";
		sql += " where   t1.family_uuid ='"+DbUtils.safeToWhereString(family_uuid)+"'";
		pData.setPageSize(50);
		////使用创建时间做分页显示,beforeTime 取 2016-01-15 13:13 之前的数据.按照创建时间排倒序
		 if(StringUtils.isNotBlank(pData.getMaxTime())){
				sql += " and   t1.create_time <="+DbUtils.stringToDateByDBType(pData.getMaxTime());
				sql += " order by t1.create_time desc";
				
		}else if(StringUtils.isNotBlank(pData.getMinTime())){
				sql += " and   t1.create_time >="+DbUtils.stringToDateByDBType(pData.getMinTime());
				sql += " order by t1.create_time asc";
		}else{//默认查询,当前时间倒叙
			  sql += " order by t1.create_time desc";
		}
		 
		Query  query =session.createSQLQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,sql, pData);
		List<Map> list=pageQueryResult.getData();
		if(list.size()>0){
			Object min=list.get(0);
			Object max=list.get(list.size()-1);
		}
		this.warpMapList(list, user);
		
		return pageQueryResult;
	}
	
	

	/**
	 * 查询所有通知
	 * 
	 * @return
	 */
	public PageQueryResult query(SessionUserInfoInterface user ,String family_uuid, String user_uuid,PaginationData pData) {
		Session session=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		String selectsql=" SELECT t1.uuid,t1.photo_time,t1.create_useruuid,t1.path,t1.address,t1.note,t1.type ";
		String sql=" FROM fp_photo_item t1 ";
		
		 if (StringUtils.isNotBlank(family_uuid)) {//根据家庭uuid查询
			sql += " where   t1.family_uuid ='"+DbUtils.safeToWhereString(family_uuid)+"'";
		}else if (StringUtils.isNotBlank(user_uuid)) {//查询用户关联家庭照片.或者自己上传的
			sql += " LEFT JOIN  fp_family_members t2 on  t2.family_uuid=t1.family_uuid ";
			sql += " where t1.create_useruuid='"+DbUtils.safeToWhereString(user_uuid)+"' or  t2.user_uuid ='"+DbUtils.safeToWhereString(user_uuid)+"'";
		}
		////使用创建时间做分页显示,beforeTime 取 2016-01-15 13:13 之前的数据.按照创建时间排倒序
		 if(StringUtils.isNotBlank(pData.getMaxTime())){
				pData.setPageNo(1);
				sql += " and   t1.create_time <="+DbUtils.stringToDateByDBType(pData.getMaxTime());
				
				  sql += " order by t1.create_time asc";
		}else if(StringUtils.isNotBlank(pData.getMinTime())){
				pData.setPageNo(1);
				sql += " and   t1.create_time <="+DbUtils.stringToDateByDBType(pData.getMinTime());
				
				  sql += " order by t1.create_time desc";
		}else{//默认查询,当前时间倒叙
			  sql += " order by t1.create_time desc";
		}
		Query  query =session.createSQLQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,sql, pData);
		List<Map> list=pageQueryResult.getData();
		this.warpMapList(list, user);
		return pageQueryResult;
	}
	
	
	
	private void warpMap(Map o, SessionUserInfoInterface user) {
		try {
			o.put("path", PxStringUtil.imgMiddleUrlByRelativePath_sub((String)o.get("path")));
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
	 */
	public boolean delete(HttpServletRequest request,String uuid, ResponseMessage responseMessage) {
		
		
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		//防止sql注入.
		if(DbUtils.isSqlInjection(uuid,responseMessage))return false;
		
		FPPhotoItem dbobj = (FPPhotoItem) this.nSimpleHibernateDao.getObjectById(
				FPPhotoItem.class, uuid);
		
		if(!user.getUuid().equals(dbobj.getCreate_useruuid())){
			responseMessage.setMessage("只有创建人,才能删除");
			return false;
		}
		
		//需要删除相关表. 
		//need_code
		iUploadFile.deleteFile(dbobj.getPath());
		
		this.nSimpleHibernateDao.delete(dbobj);
		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public FPPhotoItem get(String uuid) throws Exception {
		FPPhotoItem favorites = (FPPhotoItem) this.nSimpleHibernateDao.getObjectById(
				FPPhotoItem.class, uuid);

		return favorites;
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

		uploadFile.setFile_size(Long.valueOf(file.getSize()));
		uploadFile.setCreate_useruuid(user.getUuid());
		uploadFile.setCreate_time(TimeUtils.getCurrentTimestamp());
		uploadFile.setUpdate_time(TimeUtils.getCurrentTimestamp());
		uploadFile.setPhoto_time(TimeUtils.string2Timestamp(TimeUtils.DEFAULTFORMAT, form.getPhoto_time()));
		uploadFile.setAddress(form.getAddress());
		uploadFile.setNote(form.getNote());
		uploadFile.setFamily_uuid(form.getFamily_uuid());
		
		
		if(uploadFile.getPhoto_time()==null){//如果上传拍照时间,不正确或为null,则设置为拍照时间.
			uploadFile.setPhoto_time(uploadFile.getCreate_time());
		}
		uploadFile.setContent_type(file.getContentType());
		this.nSimpleHibernateDao.getHibernateTemplate().save(uploadFile);
		
		//2016/uuid.png
		
		String filePath ="fp/"+TimeUtils.getCurrentTime("yyyy")+"/"+uploadFile.getUuid()+"."+extension;
		
		uploadFile.setPath(filePath);

		// 上传文件
		if (iUploadFile.uploadFile(file.getInputStream(), filePath, null)) {
			return uploadFile;
		} else {
			responseMessage.setMessage("上传文件失败");
			return null;
		}
	}

	public boolean update(FPPhotoItemJsonform jsonform,
			ResponseMessage responseMessage, HttpServletRequest request) throws Exception {
		if (StringUtils.isBlank(jsonform.getUuid())) {
			responseMessage.setMessage("uuid 不能为空!");
			return false;
		}

		FPPhotoItemOfUpdate obj = (FPPhotoItemOfUpdate) nSimpleHibernateDao.getObject(FPPhotoItemOfUpdate.class,
				jsonform.getUuid());
		
		if(obj==null){
			responseMessage.setMessage("数据不存在.uuid ="+jsonform.getUuid());
			return false;
		}
		obj.setUpdate_time(TimeUtils.getCurrentTimestamp());
		obj.setNote(jsonform.getNote());
		obj.setAddress(jsonform.getAddress());
		
		this.nSimpleHibernateDao.save(obj);
		return true;
	}
}
