package com.company.news.right;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.company.news.rest.RestConstants;
import com.company.web.listener.SessionListener;

public class RightUtils {
	/**
	 * 判断当前用户是否有某个权限
	 * @param rightName
	 * @param request
	 * @return
	 */
	public static boolean hasRight(String rightName,HttpServletRequest request){
		HttpSession session =SessionListener.getSession(request);
		List rights=(List)session.getAttribute(RestConstants.Session_UserInfo_rights);
		 if(rights.contains(rightName)){
			 return true;
		 }
		 return false;
	}
	
	/**
	 * 判断当前用户是否是管理员登录
	 * @param rightName
	 * @param request
	 * @return
	 */
	public static boolean isAdmin(HttpServletRequest request){
		HttpSession session =SessionListener.getSession(request);
		Boolean isAdmin=(Boolean)session.getAttribute(RestConstants.Session_isAdmin);
		 if(isAdmin==null){
			 return false;
		 }
		 return isAdmin;
	}

}
