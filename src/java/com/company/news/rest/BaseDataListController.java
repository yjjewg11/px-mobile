package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.company.news.entity.BaseDataList;
import com.company.news.entity.BaseDataListCacheVO;
import com.company.news.json.JSONUtils;
import com.company.news.jsonform.BaseDataListJsonform;
import com.company.news.rest.util.MD5Until;
import com.company.news.rest.util.RestUtil;
import com.company.news.right.RightConstants;
import com.company.news.right.RightUtils;
import com.company.news.service.BaseDataListService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/basedatalist")
public class BaseDataListController extends AbstractRESTController {

	@Autowired
	private BaseDataListService baseDataListService;


	

	
	
	/**
	 * 组织增加
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
		if(!RightUtils.hasRight(RightConstants.AD_basedata_m,request)){
			responseMessage.setMessage(RightConstants.Return_msg);
			return "";
		}
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		BaseDataListJsonform baseDataListJsonform;
		try {
			baseDataListJsonform = (BaseDataListJsonform) this.bodyJsonToFormObject(bodyJson,
					BaseDataListJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage(error_bodyJsonToFormObject);
			return "";
		}

		
		try {
			BaseDataList baseDatalist;
			String uuid=baseDataListJsonform.getUuid();
			if(StringUtils.isEmpty(uuid))
				baseDatalist = baseDataListService.add(baseDataListJsonform,responseMessage);
				else
			baseDatalist = baseDataListService.update(baseDataListJsonform,responseMessage);
			if(baseDatalist!=null)
			model.addAttribute(baseDatalist);
			else
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage(e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("增加成功");
		return "";
	}

    /**
     * 获取机构信息,Typeuuid保存typename
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/getBaseDataListByTypeuuid", method = RequestMethod.GET)
    public String getBaseDataListByTypeuuid( ModelMap model, HttpServletRequest request) {
    	ResponseMessage responseMessage =RestUtil.addResponseMessageForModelMap(model);
        List<BaseDataList> list=baseDataListService.getBaseDataListByTypeuuid(request.getParameter("typeuuid"));
        model.addAttribute(RestConstants.Return_ResponseMessage_list,list);
        responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
        return "";
    }
    
    
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete( ModelMap model,HttpServletRequest request) {
		//返回消息体
		ResponseMessage responseMessage = RestUtil.addResponseMessageForModelMap(model);
		if(!RightUtils.hasRight(RightConstants.AD_basedata_del,request)){
			responseMessage.setMessage(RightConstants.Return_msg);
			return "";
		}
		try {
			boolean flag=baseDataListService.delete(request.getParameter("uuid"), responseMessage);
		    if(!flag)
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
    
	 /**
     * 获取机构信息,Typeuuid保存typename
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/getAllList", method = RequestMethod.GET)
    public String getAllList( ModelMap model, HttpServletRequest request,@RequestParam(value="md5",required=false) String md5) {
    	ResponseMessage responseMessage =RestUtil.addResponseMessageForModelMap(model);
    	
        List<BaseDataListCacheVO> list=baseDataListService.getBaseDataAllList();
        String md5Db=MD5Until.getMD5String(JSONUtils.getJsonString(list));
        if(md5Db.equals(md5)){//一样,表示没变化,不反馈,节省流量
        	list=null;
        }
        model.addAttribute(RestConstants.Return_ResponseMessage_list,list);
        model.addAttribute(RestConstants.Return_ResponseMessage_md5,md5Db);
        responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
        return "";
    }
}
