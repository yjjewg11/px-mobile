package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.FPPhotoItem;
import com.company.news.form.FPPhotoItemForm;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPPhotoItemJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.FPPhotoItemService;
import com.company.news.vo.ResponseMessage;
/**
 * 家庭照片
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/fPPhotoItem")
public class FPPhotoItemController extends AbstractRESTController {

	@Autowired
	private FPPhotoItemService fPPhotoItemService;
	
	

	/**
	 * 上传我的头像
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload(@RequestParam("file") CommonsMultipartFile file,
			 FPPhotoItemForm form,ModelMap model,
			HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			
//			String md5=request.getParameter("md5");
//			String photo_time=request.getParameter("photo_time");
//			String address=request.getParameter("address");
//			String note=request.getParameter("note");
			FPPhotoItem uploadFile = fPPhotoItemService.uploadImg(form, file,
					responseMessage, request);
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
	 * 
	 * 查询我关联的所有家庭的相片.
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryMy", method = RequestMethod.GET)
	public String queryMy(ModelMap model, HttpServletRequest request,PaginationData pData) {
		model.clear();
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		
		try {
			
			String family_uuid=request.getParameter("family_uuid");
		//	String photo_time=request.getParameter("photo_time");
			if(DBUtil.isSqlInjection(family_uuid, responseMessage))return "";
			PageQueryResult pageQueryResult= fPPhotoItemService.query(user,family_uuid,user.getUuid(),pData);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		return "";
	}



	/**
	 * 
	 * 查询根据时间范围查询，新数据总数和变化数据总数。
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryOfNewDataOrUpdate", method = RequestMethod.GET)
	public String queryOfNewDataOrUpdate(ModelMap model, HttpServletRequest request,PaginationData pData) {
		model.clear();
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		
		try {
			
			String family_uuid=request.getParameter("family_uuid");
		//	String photo_time=request.getParameter("photo_time");
			if(StringUtils.isBlank(family_uuid)){
				responseMessage.setMessage("family_uuid 不能为空");
				return "";
			}
			if(DBUtil.isSqlInjection(family_uuid, responseMessage))return "";
			
			if(StringUtils.isBlank(pData.getMaxTime())){
				responseMessage.setMessage("maxTime 必填");
				return "";
			}
			if(DBUtil.isSqlInjection(pData.getMaxTime(), responseMessage))return "";
			if(StringUtils.isBlank(family_uuid)){
				responseMessage.setMessage("maxTime 不能为空");
				return "";
			}

//			if(StringUtils.isBlank(pData.getMinTime())){
//				responseMessage.setMessage("minTime 必填");
//				return "";
//			}
			if(DBUtil.isSqlInjection(pData.getMinTime(), responseMessage))return "";
			
			
			 boolean flag=fPPhotoItemService.queryOfNewDataOrUpdate(family_uuid,pData,model);
			 if(!flag)return "";
//			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		return "";
	}



	/**
	 * 
	 * 查询增量更新数据，的uuid
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryOfUpdate", method = RequestMethod.GET)
	public String queryOfUpdate(ModelMap model, HttpServletRequest request,PaginationData pData) {
		model.clear();
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		
		try {
			
			String family_uuid=request.getParameter("family_uuid");
		//	String photo_time=request.getParameter("photo_time");
//			PaginationData pData = this.getPaginationDataByRequest(request);
		//	String photo_time=request.getParameter("photo_time");
			if(StringUtils.isBlank(family_uuid)){
				responseMessage.setMessage("family_uuid 不能为空");
				return "";
			}
			if(DBUtil.isSqlInjection(family_uuid, responseMessage))return "";
			
			if(StringUtils.isBlank(pData.getMaxTime())){
				responseMessage.setMessage("maxTime 必填");
				return "";
			}
			if(DBUtil.isSqlInjection(pData.getMaxTime(), responseMessage))return "";
			
			
			if(StringUtils.isBlank(pData.getMinTime())){
				responseMessage.setMessage("minTime 不能为空");
				return "";
			}
			if(DBUtil.isSqlInjection(pData.getMinTime(), responseMessage))return "";
			PageQueryResult pageQueryResult= fPPhotoItemService.queryOfUpdate(user,family_uuid,user.getUuid(),pData,model);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		return "";
	}



	/**
	 * 
	 * 查询我关联的所有家庭的相片.
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryOfIncrement", method = RequestMethod.GET)
	public String queryOfIncrement(ModelMap model, HttpServletRequest request,PaginationData pData) {
		model.clear();
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		
		try {
			
			String family_uuid=request.getParameter("family_uuid");
		//	String photo_time=request.getParameter("photo_time");
			
			PageQueryResult pageQueryResult= fPPhotoItemService.queryOfIncrement(user,family_uuid,user.getUuid(),pData,model);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		return "";
	}

	/**
	 * 
	 * 指定家庭相册的相片列表.
	 * @param model
	 * @param request
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/queryByFamily_uuid", method = RequestMethod.GET)
	public String queryByFamily_uuid(ModelMap model, HttpServletRequest request,PaginationData pData) {
		model.clear();
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		
		try {
			
			String family_uuid=request.getParameter("family_uuid");
		//	String photo_time=request.getParameter("photo_time");
			
			PageQueryResult pageQueryResult= fPPhotoItemService.query(user,family_uuid,null,pData);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		return "";
	}


	/**
	 * 修改保存相册基本资料
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(ModelMap model, HttpServletRequest request) {
		
		
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		FPPhotoItemJsonform favoritesJsonform;
		try {
			favoritesJsonform = (FPPhotoItemJsonform) this.bodyJsonToFormObject(
					bodyJson, FPPhotoItemJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}
		
		//设置当前用户
		
		try {
			Object flag;
			    flag = fPPhotoItemService.update(favoritesJsonform, responseMessage, request);

			if (flag==null)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("更新成功");
		return "";
	}



	

	/**
	 * 班级删除
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
			
			SessionUserInfoInterface user=this.getUserInfoBySession(request);
			
			boolean flag = fPPhotoItemService.delete(request,request.getParameter("uuid")
					,responseMessage);
			if (!flag)
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("操作成功");
		return "";
	}
	
	

	
	

	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid,ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		Object m;
		try {
			
			if(DbUtils.isSqlInjection(uuid, responseMessage)){
				return "";
			}
			m = fPPhotoItemService.get(uuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity,m);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}
}
