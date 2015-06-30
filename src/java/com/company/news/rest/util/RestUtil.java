package com.company.news.rest.util;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;

import com.company.news.SystemConstants;
import com.company.news.rest.RestConstants;
import com.company.news.vo.ResponseMessage;


public class RestUtil {
	private static Logger logger = LoggerFactory.getLogger(RestUtil.class);

	/**
	 * 返回消息对象中追加用户登录seesion不存在页面..
	 * 
	 * @param model
	 * @return
	 */
	static public ResponseMessage addNoSessionForResponseMessage(ResponseMessage responseMessage) {
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_sessionTimeout);
		responseMessage.setMessage("用户登录超时,请重新登录!");
		return responseMessage;
	}

	/**
	 * 返回消息对象中追加用户登录seesion不存在页面..
	 * 
	 * @param model
	 * @return
	 */
	static public ResponseMessage addNoTokenForResponseMessage(ResponseMessage responseMessage) {
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_nopower);
		responseMessage.setMessage("用户令牌过期!");
		return responseMessage;
	}

	/**
	 * 返回消息对象中追加用户登录seesion不存在页面..
	 * 
	 * @param model
	 * @return
	 */
	static public ResponseMessage addNoPowerForResponseMessage(ModelMap model) {
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_nopower);
		responseMessage.setMessage("没有操作权限");
		model.addAttribute(RestConstants.Return_ResponseMessage, responseMessage);
		return responseMessage;
	}
	/**
	 * 返回消息对象中追加用户登录seesion不存在页面..
	 * 
	 * @param model
	 * @return
	 */
	static public ResponseMessage addMessageForResponseMessage(ModelMap model, String msg) {
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage(msg);
		responseMessage.setStatus(RestConstants.Return_ResponseMessage_nopower);
		model.addAttribute(RestConstants.Return_ResponseMessage, responseMessage);
		return responseMessage;
	}

	/**
	 * 返回消息对象中追加返回消息状态.
	 * 
	 * @param model
	 * @return
	 */
	static public ResponseMessage addResponseMessageForModelMap(ModelMap model) {
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("操作成功");
		model.addAttribute(RestConstants.Return_ResponseMessage, responseMessage);

		return responseMessage;
	}


	/**
	 * put post请求时,业务数据放到内容区,json格式.
	 * 
	 * @param request
	 * @return
	 */
	static public String getJsonStringByRequest(HttpServletRequest request) {
		String syncRequestString = "";
		try {
			// // request.getParameter 读取URL参数.....
			// // 读取请求内容，转换为String
			ServletInputStream inputStream = request.getInputStream();
			byte[] buffer = new byte[8192];
			int length = 0;
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			while ((length = inputStream.read(buffer)) > 0) {
				byteArrayOutputStream.write(buffer, 0, length);
			}
			byteArrayOutputStream.flush();
			syncRequestString = new String(byteArrayOutputStream.toByteArray(), SystemConstants.Charset);
			logger.info("request.getInputStream="+syncRequestString);
		} catch (Exception e) {
		  e.printStackTrace();
		} 

		return syncRequestString;
	}
	
	/**
	 * put post请求时,业务数据放到内容区,json格式.
	 * 
	 * @param request
	 * @return
	 */
	static public String getAttJsonStringByRequest(HttpServletRequest request) {
		String syncRequestString = "";
		try {
			// // request.getParameter 读取URL参数.....
			// // 读取请求内容，转换为String
			syncRequestString = (String)request.getAttribute("json");
		} catch (Exception e) {
		} finally {

		}

		return syncRequestString;
	}

	public Field getFileByPropertyName(String propertyName, Class clazz) {
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredField(propertyName);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
