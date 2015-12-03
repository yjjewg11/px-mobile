package com.company.news.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Parent;
import com.company.news.entity.StudentOfSession;
import com.company.news.entity.User4Q;
import com.company.news.form.UserLoginForm;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.json.JSONUtils;
import com.company.news.jsonform.ParentDataJsonform;
import com.company.news.jsonform.ParentRegJsonform;
import com.company.news.jsonform.UserRegJsonform;
import com.company.news.rest.util.MD5Until;
import com.company.news.rest.util.RestUtil;
import com.company.news.service.UserinfoService;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

@Controller
@RequestMapping(value = "/userinfo")
public class UserinfoController extends AbstractRESTController {

	@Autowired
	private UserinfoService userinfoService;


	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(UserLoginForm userLoginForm, ModelMap model,
			HttpServletRequest request) {
		model.clear();
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		boolean flag;
		String md5=request.getParameter(RestConstants.Return_ResponseMessage_md5);
		try {
			
			
			flag = userinfoService.login(userLoginForm, model, request,
					responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}
		
		
		// 将关联系学生信息放入
		HttpSession session = SessionListener
						.getSession((HttpServletRequest) request);
		
		SessionUserInfoInterface parent=this.getUserInfoBySession(request);
		userinfoService.putSession(session, parent, request);
//		
//		List<StudentOfSession> studentOfSessionlist=userinfoService.getStudentOfSessionByParentuuid(parent.getUuid());
//		session.setAttribute(RestConstants.Session_StudentslistOfParent, studentOfSessionlist);
//		//我的孩子参加的培训班
//		session.setAttribute(RestConstants.Session_MyStudentClassUuids, userinfoService.getPxClassuuidsByMyChild(parent.getUuid()));
//		
		flag = this.getUserAndStudent(model, request, responseMessage);

		if (!flag)// 请求服务返回失败标示
			return "";
		

		String md5_source=JSONUtils.getJsonString(model.get("group_list"));
		md5_source+=JSONUtils.getJsonString(model.get("class_list"));
		md5_source+=JSONUtils.getJsonString(model.get(RestConstants.Return_ResponseMessage_list));
		String md5_key=MD5Until.getMD5String(md5_source);
		if(md5_key.equals(md5)){
			model.clear();
			 responseMessage = RestUtil
					.addResponseMessageForModelMap(model);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_unchange);
			return "";
		}
		model.put(RestConstants.Return_ResponseMessage_md5, md5_key);
		
		
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("登陆成功");
		return "";
	}

	
	/**
	 * 保存我的城市,或其他信息.
	 * @param userLoginForm
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveParentData", method = RequestMethod.POST)
	public String saveCity(UserLoginForm userLoginForm, ModelMap model,
			HttpServletRequest request) {
		model.clear();
		// 返回消息体
				ResponseMessage responseMessage = RestUtil
						.addResponseMessageForModelMap(model);
				// 请求消息体
				String bodyJson = RestUtil.getJsonStringByRequest(request);
				ParentDataJsonform jsonform;
				try {
					jsonform = (ParentDataJsonform) this.bodyJsonToFormObject(
							bodyJson, ParentDataJsonform.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					responseMessage.setMessage(error_bodyJsonToFormObject);
					return "";
				}
				try {
					boolean flag = userinfoService
							.updateParentData(jsonform,request, responseMessage);
					if (!flag)// 请求服务返回失败标示
						return "";
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					responseMessage.setMessage(e.getMessage());
					return "";
				}

				responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
				responseMessage.setMessage("保存成功");
				return "";
	}

	/**
	 * 教师注册
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/reg", method = RequestMethod.POST)
	public String reg(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		ParentRegJsonform parentRegJsonform;
		try {
			parentRegJsonform = (ParentRegJsonform) this.bodyJsonToFormObject(
					bodyJson, ParentRegJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}


		try {
			boolean flag = userinfoService
					.reg(parentRegJsonform, responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("注册成功");
		return "";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String logout(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 创建session
		try {
			HttpSession session = SessionListener.getSession(request);
			if (session != null) {
				// UserInfo
				// userInfo=(UserInfo)session.getAttribute(RestConstants.Session_UserInfo);
				session.invalidate();
			}

	
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
		// responseMessage.setMessage(new Message("失败消息!", "Failure message"));
		return "";
	}

	/**
	 * 获取用户信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getUserinfo", method = RequestMethod.GET)
	public String getUserinfo(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		try {
			String md5=request.getParameter(RestConstants.Return_ResponseMessage_md5);
			
			boolean flag = this.getUserAndStudent(model, request, responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
			
			
			String md5_source=JSONUtils.getJsonString(model.get("group_list"));
			md5_source+=JSONUtils.getJsonString(model.get("class_list"));
			md5_source+=JSONUtils.getJsonString(model.get(RestConstants.Return_ResponseMessage_list));
			
			String md5_key=MD5Until.getMD5String(md5_source);
			
			
			if(md5_key.equals(md5)){
				model.clear();
				 responseMessage = RestUtil
						.addResponseMessageForModelMap(model);
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_unchange);
				return "";
			}
			model.put(RestConstants.Return_ResponseMessage_md5, md5_key);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
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
		ParentRegJsonform parentRegJsonform;
		try {
			parentRegJsonform = (ParentRegJsonform) this.bodyJsonToFormObject(
					bodyJson, ParentRegJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}


		try {
			boolean flag = userinfoService
					.reg(parentRegJsonform, responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("增加成功");
		return "";
	}


	/**
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updateDisable", method = RequestMethod.POST)
	public String updateDisable(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);

		try {
			boolean flag = userinfoService.updateDisable(
					request.getParameter("disable"), request.getParameter("useruuids"),
					responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";

			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			responseMessage.setMessage("操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
		return "";
	}
	
	
	/**
	 * 获取登录用户和机构
	 * @param model
	 * @param request
	 * @param responseMessage
	 * @return
	 */
	private boolean getUserAndStudent(ModelMap model,
			HttpServletRequest request,ResponseMessage responseMessage){
		List list = new ArrayList();
		try {
			list = userinfoService.getStudentByParentuuid(this.getUserInfoBySession(
					request).getUuid());
			String group_uuids=this.getMyChildrenGroupUuidsBySession(request);
			String class_uuids=this.getMyChildrenClassuuidsBySession(request);
			model.addAttribute("group_list", userinfoService.getGroupVObyUuids(group_uuids));
			model.addAttribute("class_list", userinfoService.getPClassbyUuids(class_uuids));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return false;
		}
		model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
		
		HttpSession session = SessionListener.getSession(request);
		// 返回用户信息
		this.putUserInfoReturnToModel(model, request);
		model.put(RestConstants.Return_JSESSIONID, session.getId());
		
		return true;
	}
	
	/**
	 * 修改
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		UserRegJsonform userRegJsonform;
		try {
			userRegJsonform = (UserRegJsonform) this.bodyJsonToFormObject(
					bodyJson, UserRegJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}
		// 默认注册未普通用户类型
		userRegJsonform.setUuid(this.getUserInfoBySession(request).getUuid());

		try {
			Parent user = userinfoService
					.update(userRegJsonform, responseMessage);
			if (user==null)// 请求服务返回失败标示
				return "";
			
			//更新session中用户信息
			HttpSession session=SessionListener.getSession(request);
			session.setAttribute(RestConstants.Session_UserInfo, user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}


		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("修改成功");
		return "";
	}
	
	/**
	 * 修改
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updatepassword", method = RequestMethod.POST)
	public String updatepassword(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		ParentRegJsonform parentRegJsonform;
		try {
			parentRegJsonform = (ParentRegJsonform) this.bodyJsonToFormObject(
					bodyJson, ParentRegJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}
		// 默认注册未普通用户类型
		parentRegJsonform.setUuid(this.getUserInfoBySession(request).getUuid());

		try {
			boolean flag = userinfoService
					.updatePassword(parentRegJsonform, responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("修改成功");
		return "";
	}
	
	
	/**
	 * 修改
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/updatePasswordBySms", method = RequestMethod.POST)
	public String updatePasswordBySms(ModelMap model, HttpServletRequest request) {
		// 返回消息体
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		// 请求消息体
		String bodyJson = RestUtil.getJsonStringByRequest(request);
		ParentRegJsonform parentRegJsonform;
		try {
			parentRegJsonform = (ParentRegJsonform) this.bodyJsonToFormObject(
					bodyJson, ParentRegJsonform.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+error_bodyJsonToFormObject);
			return "";
		}

		try {
			boolean flag = userinfoService
					.updatePasswordBySms(parentRegJsonform, responseMessage);
			if (!flag)// 请求服务返回失败标示
				return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器异常:"+e.getMessage());
			return "";
		}

		responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
		responseMessage.setMessage("修改成功");
		return "";
	}
	
	/**
	 * 获取家长版本动态数据
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getDynamicMenu", method = RequestMethod.GET)
	public String getDynamicMenu(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			List list = userinfoService.getDynamicMenu();
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
		
	}
	
	/**
	 * 查询我孩子相关老师和园长通信录列表
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getTeacherPhoneBook", method = RequestMethod.GET)
	public String getTeacherPhoneBook(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			String group_uuids=this.getMyChildrenGroupUuidsBySession(request);
			String class_uuids=this.getMyChildrenClassuuidsBySession(request);
			
			List listKD = userinfoService.getKDTeacherPhoneList(group_uuids);
			List list = userinfoService.getTeacherPhoneList(class_uuids);
			model.addAttribute(RestConstants.Return_ResponseMessage_list, list);
			model.addAttribute("listKD", listKD);
			
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
	}
	
	/**
	 * 获取老师信息
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getTeacherInfo", method = RequestMethod.GET)
	public String getTeacherInfo(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			String uuid=request.getParameter("uuid");
			if(StringUtils.isBlank(uuid)){
				responseMessage.setMessage("uuid 不能空");
				return "";
			}
			User4Q user=(User4Q)CommonsCache.get(uuid, User4Q.class);
			if(user==null){
				responseMessage.setMessage("没有查询到老师数据.uuid="+uuid);
				return "";
			}
			user.setImg(PxStringUtil.imgSmallUrlByUuid(user.getImg()));
			model.addAttribute(RestConstants.Return_G_entity, user);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
	}
	
	
	/**
	 * 获取主要话题只有一条
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getMainTopic", method = RequestMethod.GET)
	public String getMainTopic(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			Map map=new HashMap();
			//空字符串表示不启用话题.否则未话题的地址.
			map.put("img",null);
			map.put("title","亲子英语对话课堂");
			map.put("url","http://kd.wenjienet.com/px-rest/sns/index.html?topicid=abc");
			model.addAttribute(RestConstants.Return_G_entity, map);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
		
	}
	

	/**
	 * 获取主要话题只有一条.点击后的事件记录.
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getMainTopic_cb", method = RequestMethod.GET)
	public String getMainTopic_callback(ModelMap model, HttpServletRequest request) {
		ResponseMessage responseMessage = RestUtil
				.addResponseMessageForModelMap(model);
		
		try {
			Map map=new HashMap();
			//空字符串表示不启用话题.否则未话题的地址.
//			map.put("title","亲子英语对话课堂");
//			map.put("url","http://kd.wenjienet.com/px-rest/sns/index.html?topicid=abc");
//			model.addAttribute(RestConstants.Return_G_entity, map);
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_success);
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseMessage.setMessage("服务器错误:"+e.getMessage());
			return "";
		}
		
	}
}
