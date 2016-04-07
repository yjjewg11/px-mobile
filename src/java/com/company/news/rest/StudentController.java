package com.company.news.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.SystemConstants;
import com.company.news.entity.Student;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.StudentJsonform;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.StudentService;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

@Controller
@RequestMapping(value = "/student")
public class StudentController extends AbstractRESTController {
	@Autowired
	private StudentService studentService;



	/**
	 * 获取机构信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getStudentByClassuuid", method = RequestMethod.GET)
	public String getStudentByClassuuid(ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		if(true)	return "";
		List<Student> list = studentService.query(
				request.getParameter("classuuid"),
				request.getParameter("groupuuid"));
		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

	

	/**
	 * 查询我的所有孩子列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listByMyChildren", method = RequestMethod.GET)
	public String listByMyChildren(ModelMap model,
			HttpServletRequest request) {
		SessionUserInfoInterface parent=SessionListener.getUserInfoBySession(request);
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			List<Student> list = studentService.listByMyChildren(studentService.getMyChildrenUuidsBySession(request));
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		return "";
	}

	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	public String get(@PathVariable String uuid, ModelMap model,
			HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		Student s;
		try {
			if(DBUtil.isSqlInjection(uuid, responseMessage))return "";
			
			s = studentService.get(uuid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		model.addAttribute(RestConstants.Return_G_entity, s);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		return "";
	}

	/**
	 * 添加用户
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
		StudentJsonform studentJsonform;
		try {
			studentJsonform = (StudentJsonform) this.bodyJsonToFormObject(
					bodyJson, StudentJsonform.class);
			
			if(studentJsonform.getAddress()!=null&&studentJsonform.getAddress().length()>300){
				responseMessage.setMessage("地址不能超过300个字.");
				return "";
			}
			
			if(StringUtils.isBlank(studentJsonform.getUuid())){
				responseMessage.setMessage("学生uuid不能为空");
				return "";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}

		try {
			boolean flag;
				flag = studentService.update(studentJsonform, responseMessage,request);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("增加成功");
		return "";
	}
	/**
	 * 添加用户
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		StudentJsonform studentJsonform;
		try {
			studentJsonform = (StudentJsonform) this.bodyJsonToFormObject(
					bodyJson, StudentJsonform.class);
			
			if(studentJsonform.getAddress()!=null&&studentJsonform.getAddress().length()>300){
				responseMessage.setMessage("地址不能超过300个字.");
				return "";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}

		try {
			SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
			
			
			if(user.getUuid().equals(user.getLoginname())){
				responseMessage.setMessage("请先绑定手机号码,在创建宝宝信息!");
				
				model.put(RestConstants.Return_UserThirdLogin_needBindTel, SystemConstants.UserThirdLogin_needBindTel_1);
				return "";
			}
			
			Student flag;
			studentJsonform.setUuid(null);
			flag = studentService.add(studentJsonform, responseMessage,request);
			if (flag==null)// 请求服务返回失败标示
				return "";
			model.addAttribute(RestConstants.Return_G_entity, studentService.get(flag.getUuid()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage
					.setStatus(RestConstants.Return_ResponseMessage_failed);
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("增加成功");
		return "";
	}
	
	

	/**
	 * 获取用户信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getClassHeaderTeacher", method = RequestMethod.GET)
	public String getClassHeaderTeacherName(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String classuuid=request.getParameter("classuuid");
			if(DBUtil.isSqlInjection(classuuid, responseMessage))return "";
			
			if (StringUtils.isBlank(classuuid)) {
				responseMessage.setMessage("参数:classuuid不能为空！");
				return "";
			}
			
			List list = studentService.getClassHeaderTeacher(classuuid);
		
			model.put(RestConstants.Return_ResponseMessage_list, list);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {

			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
		return "";
	}


}
