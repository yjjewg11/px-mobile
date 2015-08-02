package com.company.news.rest;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.entity.Parent;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.StudentSignRecordService;
import com.company.news.vo.ResponseMessage;

/**
 * 学生签到记录
 * @author liumingquan
 *
 */
@Controller
@RequestMapping(value = "/studentSignRecord")
public class StudentSignRecordController extends AbstractRESTController {

	@Autowired
	private StudentSignRecordService studentSignRecordService;

	

	/**
	 * 获取我的孩子的刷卡信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryMy", method = RequestMethod.GET)
	public String getClassNewsByMy(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		PaginationData pData = this.getPaginationDataByRequest(request);
		Parent user = this.getUserInfoBySession(request);
		String student_uuid=request.getParameter("studentuuid");
		if(StringUtils.isBlank(student_uuid)){
			student_uuid=this.getMyChildrenUuidsBySession(request);
		}else{
			String mystudent_uuid=this.getMyChildrenUuidsBySession(request);
			if(mystudent_uuid==null||!mystudent_uuid.contains(student_uuid)){
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage("非法参数,不是该学生的家长.student_uuid="+student_uuid);
				//return "";
			}
		}
		if(StringUtils.isBlank(student_uuid)){
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			responseMessage.setMessage("无数据");
			return "";
		}
		PageQueryResult pageQueryResult = studentSignRecordService.query(student_uuid, pData);
		model.addAttribute(RestConstants.Return_ResponseMessage_list,
				pageQueryResult);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

}
