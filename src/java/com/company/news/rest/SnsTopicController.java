package com.company.news.rest; 
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.CountService;
import com.company.news.service.SnsTopicService;
import com.company.news.vo.ResponseMessage;

@Controller
@RequestMapping(value = "/snsTopic")
public class SnsTopicController extends AbstractRESTController {
	@Autowired
	private SnsTopicService snsTopicService;


	 /**
	 * 获取列表
	 *	/share/getCourseType.json
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/hotByPage", method = RequestMethod.GET)
	public String hotByPage(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String section_id=request.getParameter("section_id");
			PaginationData pData = this.getPaginationDataByRequest(request);
			PageQueryResult list = snsTopicService.hotByPage(pData,section_id,request);
			
			model.addAttribute(RestConstants.Return_ResponseMessage_list,list);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
	}
		
}
