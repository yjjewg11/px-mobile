package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.company.news.ContentTypeConstants;
import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.UploadFile;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.UploadFileService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/uploadFile")
public class UploadFileController extends AbstractRESTController {
	@Autowired
	private UploadFileService uploadFileService;

	/**
	 * 根据guid返回图片下载流
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getImgFile", method = RequestMethod.GET)
	public String getImg(@RequestParam("uuid") String uuid, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String path = uploadFileService.down(uuid,
				request.getParameter("type"), response,
				ContentTypeConstants.Image_png);

			return path;

	}

	/**
	 * 上传我的头像
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/uploadBase64", method = RequestMethod.POST)
	public String upload(@RequestParam("base64") String base64,
			@RequestParam("type") Integer type, ModelMap model,
			HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			UploadFile uploadFile = uploadFileService.uploadImg(base64, type,
					responseMessage, request,
					this.getUserInfoBySession(request));
			if (uploadFile == null)
				return "";

			model.addAttribute(RestConstants.Return_G_entity, uploadFile);
			model.addAttribute(RestConstants.Return_G_imgUrl,
					PxStringUtil.imgUrlByUuid(uploadFile.getUuid()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("上传成功");
		return "";
	}

	/**
	 * 上传我的头像
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload(@RequestParam("file") CommonsMultipartFile file,
			@RequestParam("type") Integer type, ModelMap model,
			HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			UploadFile uploadFile = uploadFileService.uploadImg(type, file,
					responseMessage, request,
					this.getUserInfoBySession(request));
			if (uploadFile == null)
				return "";

			model.addAttribute(RestConstants.Return_G_entity, uploadFile);
			model.addAttribute(RestConstants.Return_G_imgUrl,
					PxStringUtil.imgUrlByUuid(uploadFile.getUuid()));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("上传成功");
		return "";
	}

	/**
	 * xheditor 控件上传 按照控件要求实现接口
	 * 
	 * @param model
	 * @param request
	 * @return
	 */

	@RequestMapping(value = "/xheditorUpload", method = RequestMethod.POST)
	public String xheditorUpload(
			@RequestParam("filedata") CommonsMultipartFile file,
			ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			UploadFile uploadFile = uploadFileService.uploadImg(
					SystemConstants.UploadFile_type_xheditorimg, file,
					responseMessage, request,
					this.getUserInfoBySession(request));
			if (uploadFile == null) {
				// xheditor 返回规范
				model.clear();
				model.addAttribute("err", responseMessage.getMessage());
				model.addAttribute("msg", "");
				return "";
			}
			model.clear();
			model.addAttribute("err", "");
			model.addAttribute("msg",
					request.getContextPath()
							+ "/rest/uploadFile/getImgFile.json?uuid="
							+ uploadFile.getUuid());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			model.clear();
			model.addAttribute("err", responseMessage.getMessage());
			model.addAttribute("msg", "");
			return "";
		}
		return "";
	}
	
	
	/**
	 * 删除
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);

		try {
			boolean flag = uploadFileService.delete(
					request.getParameter("uuid"),request, responseMessage);
			if (!flag)
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("删除成功");
		return "";
	}

}
