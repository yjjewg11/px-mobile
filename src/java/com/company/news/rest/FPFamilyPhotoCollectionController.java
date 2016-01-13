package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPFamilyPhotoCollectionJsonform;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.FPFamilyPhotoCollectionService;
import com.company.news.vo.ResponseMessage;
/**
 * 家庭相册
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/fpFamilyPhotoCollection")
public class FPFamilyPhotoCollectionController extends AbstractRESTController {

	@Autowired
	private FPFamilyPhotoCollectionService fPFamilyPhotoCollectionService;

	/**
	 * 
	 * 查询我关联的家庭相册
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryMy", method = RequestMethod.GET)
	public String queryMy(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		
		try {
			String md5=request.getParameter("md5");
			
//			PaginationData pData = this.getPaginationDataByRequest(request);
			List list= fPFamilyPhotoCollectionService.queryMy(user.getUuid());
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
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
//
//	/**
//	 * 
//	 * 查询我的及时消息
//	 * @param model
//	 * @param request
//	 * @return
//	 */
//	@RequestMapping(value = "/query", method = RequestMethod.GET)
//	public String query(ModelMap model, HttpServletRequest request) {
//		ResponseMessage responseMessage = RestUtil
//				.addResponseMessageForModelMap(model);
//		//设置当前用户
//		SessionUserInfoInterface user=this.getUserInfoBySession(request);
//		
//		try {
//			PaginationData pData = this.getPaginationDataByRequest(request);
//			PageQueryResult pageQueryResult= fPFamilyPhotoCollectionService.query(null,user.getUuid(),pData);
//			model.addAttribute(RestConstants.Return_ResponseMessage_list, pageQueryResult);
//			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
//			responseMessage.setMessage("服务器异常:"+e.getMessage());
//			return "";
//		}
//		return "";
//	}


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
		FPFamilyPhotoCollectionJsonform favoritesJsonform;
		try {
			favoritesJsonform = (FPFamilyPhotoCollectionJsonform) this.bodyJsonToFormObject(
					bodyJson, FPFamilyPhotoCollectionJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}
		
		//设置当前用户
		
		try {
			Object flag;
			    flag = fPFamilyPhotoCollectionService.add(favoritesJsonform, responseMessage, request);

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
		responseMessage.setMessage("操作成功");
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
			
			boolean flag = fPFamilyPhotoCollectionService.delete(request,request.getParameter("uuid")
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
			m = fPFamilyPhotoCollectionService.get(uuid);
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
