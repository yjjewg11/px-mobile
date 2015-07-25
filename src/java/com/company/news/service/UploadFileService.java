package com.company.news.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import sun.misc.BASE64Decoder;

import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.commons.util.FileUtils;
import com.company.news.commons.util.UUIDGenerator;
import com.company.news.entity.UploadFile;
import com.company.news.entity.User;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.SmbFileUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 文件上传
 * 
 * @author Administrator
 * 
 */
@Service
public class UploadFileService extends AbstractServcice {
	private static Logger logger = LoggerFactory
			.getLogger(UploadFileService.class);

	
	/**
	 * 上载附件
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 * base64='data:image/png;base64,iVBORw0KG...'
	 */
	public UploadFile uploadImg(String base64,Integer type, ResponseMessage responseMessage,
			HttpServletRequest request, String user_uuid) throws Exception {
		String extension="png";
		String contentType=null;
		if(StringUtils.isEmpty(base64)){
			 responseMessage.setMessage("上传内容是空的！");
			return null;
		}
		  byte[] b = null;  
	        if (base64 != null) {  
	        	String tmpbase64=base64.substring(base64.indexOf(";base64,")+";base64,".length());
	        	contentType=base64.substring("data:".length(),base64.indexOf(";base64,"));
	        	extension=contentType.substring("image/".length());
	        	
	            BASE64Decoder decoder = new BASE64Decoder();  
	            try {  
	                b = decoder.decodeBuffer(tmpbase64);  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	                responseMessage.setMessage("解析错误："+e.getMessage());
	    			return null;
	            }  
	        }  
		String guid = new UUIDGenerator().generate().toString();

		Long maxfileSize = Long.valueOf(ProjectProperties.getProperty(
				"UploadFilePath_maxSize_M", "2"));
		if (b.length > maxfileSize * 1024 * 1024) {
			responseMessage.setMessage("上载文件太大，不能超过" + maxfileSize + "M");
			return null;
		}
		

		String uploadPath = ProjectProperties.getProperty("UploadFilePath",
				"c:/px_upload/");
		String secondPath="";
		String fileName =guid;
		
		if(SystemConstants.UploadFile_type_head.equals(type)){
			fileName =guid;
			secondPath += "head/";
			
		}else if(SystemConstants.UploadFile_type_cook.equals(type)){
			fileName =guid;
			secondPath += "cook/";
		}else{
			secondPath += user_uuid+"/";
		};
		
		FileUtils.createDirIfNoExists(uploadPath);
		fileName = fileName + "." + extension;

		// 业务数据，关联用户保存

		if (!FileUtils.saveFile(b, uploadPath+secondPath, fileName)) {
			responseMessage.setMessage("上载文件保存失败!");
			return null;
		}
		
		UploadFile uploadFile = new UploadFile();

		uploadFile.setType(type);
		uploadFile.setFile_size(Long.valueOf(b.length));
		uploadFile.setUser_uuid(user_uuid);
		uploadFile.setCreate_time(TimeUtils.getCurrentTimestamp());
		uploadFile.setFile_path(secondPath + fileName);
		uploadFile.setContent_type(contentType);
		this.nSimpleHibernateDao.getHibernateTemplate().save(uploadFile);

		/*
		 * //更新用户头像访问地址 String uploadFilePath_url=
		 * ProjectProperties.getProperty("uploadFilePath_url",
		 * "rest/uploadFile/getImgFile.json?guid=")+uploadFile.getGuid();
		 * 
		 * //业务数据，关联用户保存
		 * if(SystemConstants.UploadFile_type_myhead.equals(uploadFile
		 * .getType())){ userInfo.setHead_imgurl(uploadFilePath_url);
		 * this.nSimpleHibernateDao.save(userInfo); }else
		 * if(SystemConstants.UploadFile_type_identity_card
		 * .equals(uploadFile.getType())){
		 * userInfo.setIdentity_card_imgurl(uploadFilePath_url);
		 * this.nSimpleHibernateDao.save(userInfo);
		 * putUserInfoReturnToModel(model,request); }else
		 * if(SystemConstants.UploadFile_type_marathon
		 * .equals(uploadFile.getType())){
		 * userInfo.setMarathon_imgurl(uploadFilePath_url);
		 * 
		 * this.nSimpleHibernateDao.save(userInfo); }
		 * 
		 * model.addAttribute("imgurl",uploadFilePath_url);
		 * 
		 * //oldFile需要删除好似，才不会null，删除原文件。 if(oldFile!=null){
		 * this.logger.info("delete old img File="+oldFile); if
		 * (SmbFileUtil.isSmbFileFormat(oldFile)) { String
		 * username=ProjectProperties.getProperty("UploadFilePath_username",
		 * "runman"); String
		 * password=ProjectProperties.getProperty("UploadFilePath_password",
		 * "Ruanman2015"); FileUtils.deletesmbFile(oldFile, username, password);
		 * }else{ FileUtils.deleteFile(oldFile);
		 * 
		 * } } // if (isImage(description)) { //
		 * makeThumbnail(files.getInputStream(), uploadPath,
		 * uploadFile.getTitle()); // } // model.addAttribute("attachmentId",
		 * uploadFile.getId()); } catch (Exception e) { logger.error("", e);
		 * responseMessage
		 * .setStatus(RestConstants.Return_ResponseMessage_failed);
		 * responseMessage.setMessage("上载文件失败!");
		 * model.addAttribute(RestConstants.Return_ResponseMessage,
		 * responseMessage); } return model;
		 */

		return uploadFile;
	}
	/**
	 * 上载附件
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private static String fileImgExt = ",jpg,jpeg,bmp,gif,png,";
	public UploadFile uploadImg(String type,
			CommonsMultipartFile file, ResponseMessage responseMessage,
			HttpServletRequest request, String user_uuid) throws Exception {
		if(StringUtils.isBlank(type)){
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("type不能为空");
			return null;
		}
		
		String guid = new UUIDGenerator().generate().toString();

		long fileSize = file.getSize();
		Long maxfileSize = Long.valueOf(ProjectProperties.getProperty(
				"UploadFilePath_maxSize_M", "2"));
		if (fileSize > maxfileSize * 1024 * 1024) {
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("上载文件太大，不能超过" + maxfileSize + "M");
			return null;
		}

		String uploadPath = ProjectProperties.getProperty("UploadFilePath",
				"c:/px_upload/");
		String fileName = file.getOriginalFilename();
		String fileExt=FilenameUtils.getExtension(fileName);
		String secondPath="";
		if(SystemConstants.UploadFile_type_head.equals(Integer.parseInt(type))){
			secondPath += "head/";
			
		}else if(SystemConstants.UploadFile_type_cook.equals(Integer.parseInt(type))){
			secondPath += "cook/";
		}else if(SystemConstants.UploadFile_type_xheditorimg.equals(Integer.parseInt(type))){
			/*检查文件类型*/
			if ((fileImgExt).indexOf("," + fileExt.toLowerCase() + ",") < 0){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("不允许上传此类型的文件");
				return null;
			}
			secondPath += "xheditor/";
		}else{
			secondPath += user_uuid+"/";
		};
		
		FileUtils.createDirIfNoExists(uploadPath);
		fileName = guid + "." + FilenameUtils.getExtension(fileName);

		// 业务数据，关联用户保存

		if (!FileUtils.saveFile(file.getInputStream(), uploadPath+secondPath, fileName)) {
			responseMessage.setMessage("上载文件保存失败!");
			return null;
		}
		// String img_max_with=ProjectProperties.getProperty("img_max_with",
		// "200");
		// if(SystemConstants.UploadFile_type_identity_card.equals(upladFileForm.getType())){
		// img_max_with=ProjectProperties.getProperty("img_max_with_identity_card",
		// "1000");
		// }else
		// if(SystemConstants.UploadFile_type_marathon.equals(upladFileForm.getType())){
		// img_max_with=ProjectProperties.getProperty("img_max_with_marathon",
		// "1000");
		// }
		// if(!FileUtils.makeThumbnail(file.getInputStream(),
		// uploadPath,fileName,Integer.valueOf(img_max_with))){
		// responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
		// responseMessage.setMessage("上载文件保存失败!");
		// model.addAttribute(RestConstants.Return_ResponseMessage,
		// responseMessage);
		// return model;
		// }
		// 上传头像,身份证，马拉松认证唯一。其他情况新建
		/*
		 * UploadFile uploadFile =null; String oldFile=null;
		 * if(Integer.valueOf(1).equals(upladFileForm.getDeleteOldFile())){
		 * String hql="from UploadFile where type='"+upladFileForm.getType()+
		 * "' and create_userid= " +userInfo.getId(); List
		 * queryList=this.nSimpleHibernateDao.getHibernateTemplate().find(hql);
		 * if(queryList.size()>0){ uploadFile =(UploadFile) queryList.get(0); }
		 * }
		 * 
		 * //新建 if(uploadFile==null){ uploadFile =new UploadFile(); }else{
		 * oldFile=uploadFile.getFile_path(); }
		 */
		UploadFile uploadFile = new UploadFile();

		uploadFile.setType(Integer.parseInt(type));
		uploadFile.setFile_size(file.getSize());
		uploadFile.setUser_uuid(user_uuid);
		uploadFile.setCreate_time(TimeUtils.getCurrentTimestamp());
		uploadFile.setFile_path(secondPath + fileName);
		uploadFile.setContent_type(file.getContentType());
		this.nSimpleHibernateDao.getHibernateTemplate().save(uploadFile);

		/*
		 * //更新用户头像访问地址 String uploadFilePath_url=
		 * ProjectProperties.getProperty("uploadFilePath_url",
		 * "rest/uploadFile/getImgFile.json?guid=")+uploadFile.getGuid();
		 * 
		 * //业务数据，关联用户保存
		 * if(SystemConstants.UploadFile_type_myhead.equals(uploadFile
		 * .getType())){ userInfo.setHead_imgurl(uploadFilePath_url);
		 * this.nSimpleHibernateDao.save(userInfo); }else
		 * if(SystemConstants.UploadFile_type_identity_card
		 * .equals(uploadFile.getType())){
		 * userInfo.setIdentity_card_imgurl(uploadFilePath_url);
		 * this.nSimpleHibernateDao.save(userInfo);
		 * putUserInfoReturnToModel(model,request); }else
		 * if(SystemConstants.UploadFile_type_marathon
		 * .equals(uploadFile.getType())){
		 * userInfo.setMarathon_imgurl(uploadFilePath_url);
		 * 
		 * this.nSimpleHibernateDao.save(userInfo); }
		 * 
		 * model.addAttribute("imgurl",uploadFilePath_url);
		 * 
		 * //oldFile需要删除好似，才不会null，删除原文件。 if(oldFile!=null){
		 * this.logger.info("delete old img File="+oldFile); if
		 * (SmbFileUtil.isSmbFileFormat(oldFile)) { String
		 * username=ProjectProperties.getProperty("UploadFilePath_username",
		 * "runman"); String
		 * password=ProjectProperties.getProperty("UploadFilePath_password",
		 * "Ruanman2015"); FileUtils.deletesmbFile(oldFile, username, password);
		 * }else{ FileUtils.deleteFile(oldFile);
		 * 
		 * } } // if (isImage(description)) { //
		 * makeThumbnail(files.getInputStream(), uploadPath,
		 * uploadFile.getTitle()); // } // model.addAttribute("attachmentId",
		 * uploadFile.getId()); } catch (Exception e) { logger.error("", e);
		 * responseMessage
		 * .setStatus(RestConstants.Return_ResponseMessage_failed);
		 * responseMessage.setMessage("上载文件失败!");
		 * model.addAttribute(RestConstants.Return_ResponseMessage,
		 * responseMessage); } return model;
		 */

		return uploadFile;
	}

	/**
	 * 下载附件
	 * 
	 * @param guid
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * 
	 *             Status Code
	 * 
	 *             response header: 200 OK Content-Disposition : attachment;
	 *             filename=4_headimg_402886fc4d606189014d6061892b0000.jpg
	 *             Content-Type : application/octet-stream;charset=UTF-8 Date :
	 *             Sun, 17 May 2015 05:52:13 GMT Server : Apache-Coyote/1.1
	 *             Transfer-Encoding : chunked
	 */
	public void down(String uuid, HttpServletResponse response,
			String contentType) throws Exception {
		// ResponseMessage responseMessage =
		// RestUtil.addResponseMessageForModelMap(model);
		// User userInfo = SessionListener.getUserInfoBySession(request);
		// String hql = "from UploadFile where  guid='" + guid + "'";
		UploadFile uploadFile = (UploadFile) this.nSimpleHibernateDao
				.getObjectById(UploadFile.class, uuid);
		if (uploadFile == null) {
			this.logger.info("uploadFile record is empty!");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
		String uploadPath = ProjectProperties.getProperty("UploadFilePath",
				"c:/px_upload/");
		boolean result = getStream(uploadPath+uploadFile.getFile_path(), response,
				contentType);
		if (!result) {
			this.logger.info("file not found.may be delete!");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * 删除附件
	 * 
	 * @param uuid
	 * @param request
	 * @param model
	 * @return
	 */
	public boolean delete(String uuid, HttpServletRequest request,
			ResponseMessage responseMessage) {

		try {
			UploadFile uploadFile = (UploadFile) this.nSimpleHibernateDao
					.getObjectById(getEntityClass(), uuid);

			// 先删除数据库，再删除文件
			if (uploadFile == null) {
				responseMessage.setMessage("数据不存在！");
				return false;
			}
			String path = uploadFile.getFile_path();
			this.nSimpleHibernateDao.deleteObjectById(getEntityClass(), uuid);

			if (SmbFileUtil.isSmbFileFormat(path)) {
				SmbFile file = new SmbFile(path);
				file.delete();
			} else {
				File file = new File(path);
				file.delete();
			}

		} catch (Exception e) {
			logger.error("", e);
			responseMessage.setMessage("删除附件失败!");
			return false;
		}
		return true;
	}

	/**
	 * 读取文件流并保存到response
	 * 
	 * @param request
	 * @param response
	 * @param actionerrors
	 * @param downInfo
	 * @return
	 * @see
	 * 
	 * @throws Exception
	 */
	private boolean getStream(String filePath, HttpServletResponse response,
			String contentType) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("getStreamInfo(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse) - start");
		}

		String fileName = FilenameUtils.getName(filePath);
		char fileSplit;

		// "image/gif"
		if (contentType != null) {
			response.setContentType(contentType);
		} else {
			response.setContentType("application/x-msdownload;");
		}
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ fileName);
		this.logger.info("Down file path:" + filePath);
		if (SmbFileUtil.isSmbFileFormat(filePath)) {
			SmbFile file = null;
			file = new SmbFile(filePath);
			if (file.isDirectory() && file.exists()) {
				// 兼容以前模式,如果是文件夹,则需要加文件名
				filePath = SmbFileUtil
						.connectedPathFileName(filePath, fileName);
				file = new SmbFile(filePath);
			}
			if (!file.exists()) {
				this.logger.error("down file is not exsist!path=" + filePath);
				return false;
			}
			// 获取文件输入流
			SmbFileInputStream fis = null;
			BufferedInputStream stream = null;

			try {
				fis = new SmbFileInputStream(file);
				stream = new BufferedInputStream(fis);
				org.apache.commons.io.IOUtils.copy(stream,
						response.getOutputStream());
			} catch (Exception e) {
				logger.error("", e);
				return false;
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		} else {
			File file = null;
			file = new File(filePath);
			if (file.exists() && file.isDirectory()) {
				// 兼容以前模式,如果是文件夹,则需要加文件名
				// filePath = SmbFileUtil.connectedPathFileName(filePath,
				// fileName);
				file = new File(filePath);
			}
			if (!file.exists()) {
				this.logger.error("down file is not exsist!path=" + filePath);
				return false;
			}
			// 获取文件输入流
			FileInputStream fis = null;
			BufferedInputStream stream = null;
			try {
				fis = new FileInputStream(file);
				stream = new BufferedInputStream(fis);
				org.apache.commons.io.IOUtils.copy(stream,
						response.getOutputStream());
			} catch (Exception e) {
				logger.error("", e);
				return false;
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getStreamInfo(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse) - end");
		}
		return true;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
