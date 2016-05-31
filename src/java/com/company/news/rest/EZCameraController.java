package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.EZCameraService;
import com.company.news.vo.ResponseMessage;
import com.hikvision.entity.AccessTokenResultRespone;
/**
 * 家庭相册
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/eZCamera")
public class EZCameraController extends AbstractRESTController {

	@Autowired
	private EZCameraService eZCameraService;

	
	@RequestMapping(value = "/getAccessToken", method = RequestMethod.GET)
	public String getAccessToken(ModelMap model, HttpServletRequest request) {
		model.clear();
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		//设置当前用户
		try {
			boolean flag=eZCameraService.getAccessToken(model,request,responseMessage);
			if(!flag)return "";
			
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
	
	
	@RequestMapping(value = "/getCameraList", method = RequestMethod.GET)
	public String query(ModelMap model, HttpServletRequest request,PaginationData pData) {
		model.clear();
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		//设置当前用户
		SessionUserInfoInterface user=this.getUserInfoBySession(request);
		
		try {
			
//			String family_uuid=request.getParameter("family_uuid");
//			if(StringUtils.isBlank(family_uuid)){
//				responseMessage.setMessage("family_uuid 不能为空");
//				return "";
//			}
//			if(DBUtil.isSqlInjection(family_uuid, responseMessage))return "";
			
			
			String class_uuids=eZCameraService.getMyChildrenClassuuidsBySession(request);
			
			if(StringUtils.isBlank(class_uuids)){//0:标识幼儿园公共区域.
				class_uuids="0";
			}else{
				class_uuids+=",0";
			}
			
			String group_uuids=eZCameraService.getMyChildrenGroupUuidsBySession(request);
			
			

			if(StringUtils.isBlank(group_uuids)){//1:测试启用.
				group_uuids="1";
			}else{
				group_uuids+=",1";
			}
			
			
			PageQueryResult pageQueryResult= eZCameraService.getCameraList(user,group_uuids,class_uuids,pData,model);
			
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
	
}
