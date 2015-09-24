package com.company.news.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import sun.misc.BASE64Decoder;

import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.UUIDGenerator;
import com.company.news.commons.util.upload.DiskIUploadFile;
import com.company.news.commons.util.upload.IUploadFile;
import com.company.news.commons.util.upload.OssIUploadFile;
import com.company.news.entity.Parent;
import com.company.news.entity.UploadFile;
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
public class UploadFileService extends AbstractService {
	private static Logger logger = Logger.getLogger(UploadFileService.class);

	private static final String imgFormat = ".png";
	private static final String fileImgExt = ",jpg,jpeg,bmp,gif,png,";
	public static final String uploadfiletype = ProjectProperties.getProperty(
			"uploadfiletype", "oss");
	private static IUploadFile iUploadFile;
	static {
		if (uploadfiletype.equals("oss"))
			iUploadFile = new OssIUploadFile();
		else
			iUploadFile = new DiskIUploadFile();
	}

	/**
	 * 上载附件
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 *             base64='data:image/png;base64,iVBORw0KG...'
	 */
	public UploadFile uploadImg(String base64, Integer type,
			ResponseMessage responseMessage, HttpServletRequest request,
			Parent user) throws Exception {
		String contentType = null;
		if (StringUtils.isEmpty(base64)) {
			responseMessage.setMessage("上传内容是空的！");
			return null;
		}

		byte[] b = null;
		if (base64 != null) {
			String tmpbase64 = base64.substring(base64.indexOf(";base64,")
					+ ";base64,".length());
			contentType = base64.substring("data:".length(),
					base64.indexOf(";base64,"));
			// extension=contentType.substring("image/".length());

			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(tmpbase64);
			} catch (Exception e) {
				e.printStackTrace();
				responseMessage.setMessage("解析错误：" + e.getMessage());
				return null;
			}
		}

		// 过滤文件大小等
		if (!fileFilter(responseMessage, b.length)) {
			return null;
		}

		String guid = new UUIDGenerator().generate().toString();
		String filePath = this.getSecondPath(type, user) + guid + imgFormat;

		// 上传文件
		if (iUploadFile.uploadFile(new ByteArrayInputStream(b), filePath, type)) {
			UploadFile uploadFile = new UploadFile();

			uploadFile.setType(type);
			uploadFile.setFile_size(Long.valueOf(b.length));
			uploadFile.setUser_uuid(user.getUuid());
			uploadFile.setCreate_time(TimeUtils.getCurrentTimestamp());
			uploadFile.setFile_path(filePath);
			uploadFile.setContent_type(contentType);
			this.nSimpleHibernateDao.getHibernateTemplate().save(uploadFile);

			return uploadFile;
		} else {
			responseMessage.setMessage("上传文件失败");
			return null;

		}
	}

	/**
	 * 上载附件
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */

	public UploadFile uploadImg(int type, CommonsMultipartFile file,
			ResponseMessage responseMessage, HttpServletRequest request,
			Parent user) throws Exception {
		String contentType = null;

		// 过滤文件大小等
		if (!fileFilter(responseMessage, file.getSize())) {
			return null;
		}

		String guid = new UUIDGenerator().generate().toString();
		String filePath = this.getSecondPath(type, user) + guid + imgFormat;

		// 上传文件
		if (iUploadFile.uploadFile(file.getInputStream(), filePath, type)) {
			UploadFile uploadFile = new UploadFile();

			uploadFile.setType(type);
			uploadFile.setFile_size(Long.valueOf(file.getSize()));
			uploadFile.setUser_uuid(user.getUuid());
			uploadFile.setCreate_time(TimeUtils.getCurrentTimestamp());
			uploadFile.setFile_path(filePath);
			uploadFile.setContent_type(contentType);
			this.nSimpleHibernateDao.getHibernateTemplate().save(uploadFile);

			return uploadFile;
		} else {
			responseMessage.setMessage("上传文件失败");
			return null;

		}
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
	public String down(String uuid, String type, HttpServletResponse response,
			String contentType) throws Exception {
		UploadFile uploadFile = (UploadFile) CommonsCache.get(uuid, UploadFile.class);

		if (uploadFile == null) {
			this.logger.info("uploadFile record is empty!");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}

		if (UploadFileService.uploadfiletype.equals("oss")) {
			//type 为访问后缀			
			return "redirect:"
					+ ProjectProperties.getProperty("oss_imgHostId", "")
					+ uploadFile.getFile_path()+(StringUtils.isNotBlank(type)?"@"+type:"");
		} else {

			String uploadPath = ProjectProperties.getProperty("UploadFilePath",
					"c:/px_upload/");
			String filePath = uploadPath + uploadFile.getFile_path();
			if (StringUtils.isNotBlank(type) && type.equals("old"))
				filePath = (filePath + "@old");

			boolean result = getStream(filePath, response, contentType);
			if (!result) {
				this.logger.info("file not found.may be delete!");
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
			return null;
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
			this.nSimpleHibernateDao.delete(uploadFile);

			iUploadFile.deleteFile(path);

		} catch (Exception e) {
			e.printStackTrace();
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

	/**
	 * 
	 * @return
	 */
	private int[] getThumbSize(int type) {
		// 默认
		int[] size = { 80, 80 };
		if (SystemConstants.UploadFile_type_head.equals(type)) {
			size = new int[] { 100, 100 };
		} else if (SystemConstants.UploadFile_type_cook.equals(type)) {
			size = new int[] { 120, 120 };
		} else if (SystemConstants.UploadFile_type_xheditorimg.equals(type)) {
			size = new int[] { 160, 160 };
		}
		return size;
	}

	/**
	 * 根据文件类型，获取上传到服务器的路径信息
	 * 
	 * @param t
	 * @param user
	 * @param fileExt
	 * @return
	 */
	private static String getSecondPath(int t, Parent user) {
		String secondPath = "";
		if (SystemConstants.UploadFile_type_head.equals(t)) {
			secondPath += "head/";

		} else if (SystemConstants.UploadFile_type_cook.equals(t)) {
			secondPath += "cook/";
		} else if (SystemConstants.UploadFile_type_xheditorimg.equals(t)) {
			secondPath += "xheditor/";
		} else {
			secondPath += user.getUuid() + "/";
		}
		return secondPath;
	}

	/**
	 * 判断上传文件是否合法
	 * 
	 * @param responseMessage
	 * @param fileExt
	 * @return
	 */
	private static boolean fileFilter(ResponseMessage responseMessage,
			long fileSize) {
		Long maxfileSize = Long.valueOf(ProjectProperties.getProperty(
				"UploadFilePath_maxSize_M", "2"));
		if (fileSize > maxfileSize * 1024 * 1024) {
			responseMessage.setMessage("上载文件太大，不能超过" + maxfileSize + "M");
			return false;
		}

		return true;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return UploadFile.class;
	}

}
