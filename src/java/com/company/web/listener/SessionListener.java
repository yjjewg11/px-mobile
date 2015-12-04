package com.company.web.listener;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.company.http.PxHttpSession;
import com.company.news.SystemConstants;
import com.company.news.cache.SessionCache;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.rest.RestConstants;
import com.company.web.filter.UserInfoFilter;

public class SessionListener implements HttpSessionListener {
   
  //sessionMap<JSESSIONID,session>
  static Map sessionMapBySessionid=new ConcurrentHashMap ();
  
  public static void   putSessionByJSESSIONID(HttpSession s ){
		if(s instanceof PxHttpSession){//手动设置最后访问时间.
			SessionCache.putPxHttpSession((PxHttpSession)s);
			return;
		}
    sessionMapBySessionid.put(s.getId(), s);
  }
  
  public static void removeSessionByJSESSIONID(String jessionid) {
	  sessionMapBySessionid.remove(jessionid);
	}
  @Deprecated
  public static void putSessionByToken(String jessionid, HttpSession session) {
	  sessionMapBySessionid.put(jessionid, session);
	}
  
  private static HttpSession   getSessionFromCache(String jessionid){
	  //1优先取sessionMapBySessionid
	  HttpSession	session=(HttpSession)sessionMapBySessionid.get(jessionid);
      if(session!=null)return session;
      //2从SessionCache中取
      session=(HttpSession)SessionCache.getPxHttpSession(jessionid);
     return session;
  }
  /**
   * 获取session统一使用该方法.策略:JSESSIONID 优先于token
   * @param request
   * @return
   */
  public static HttpSession   getSession(HttpServletRequest request){
    //1.优先根据默认关系取
    HttpSession session=request.getSession(false);;
    if(session!=null)return session;
    
    
    //2.根据JSESSIONID 参数获取
    String JSESSIONID=request.getParameter(RestConstants.Return_JSESSIONID);
    if(StringUtils.isNotBlank(JSESSIONID)){//使用JSESSIONID
    	session=getSessionFromCache(JSESSIONID);
      if(session!=null)return session;
      //修复参数模式下面特殊字符处理
      String tmpsession=JSESSIONID.replaceAll(" ", "+");
      if(!JSESSIONID.equals(tmpsession)){
        logger.warn(session+",JSESSIONID Contains special characters,After the escape="+tmpsession);
      }
      session=getSessionFromCache(tmpsession);
      if(session!=null)return session;
      
   }
    //从cookie中取sessionid
    JSESSIONID=UserInfoFilter.getJSESSIONIDCookies(request);
    session=(HttpSession)SessionCache.getPxHttpSession(JSESSIONID);
    
    return session;
  }
  
  /**
   * 获取session  中用户信息,统一使用该方法.
   * @param request
   * @return
   */
  public static SessionUserInfoInterface   getUserInfoBySession(HttpServletRequest request){
    HttpSession session =SessionListener.getSession(request);
    if(session==null)return null;
    return (SessionUserInfoInterface)session.getAttribute(RestConstants.Session_UserInfo);
  }
  

  /**
   * 是否是培训机构登陆
   * @param request
   * @return
   */
  public static boolean   isPXLogin(HttpServletRequest request){
	  if(SystemConstants.Group_type_2.toString().equals(SessionListener.getLoginTypeBySession(request))){
		  return true;
	  }
	  return false;
  }
  
  /**
   * 获取session  中用户信息,统一使用该方法.
   * @param request
   * @return
   */
  public static String   getLoginTypeBySession(HttpServletRequest request){
    HttpSession session =SessionListener.getSession(request);
    if(session==null)return null;
    return (String)session.getAttribute(RestConstants.LOGIN_TYPE);
  }
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(SessionListener.class);

    private static int count = 0;
    public void sessionCreated(HttpSessionEvent se) {
        count++;
        logger.info("sessionCreated,Session count="+count);
    }

    /**
     * session invalid, unlock doc by user.
     */
    public void sessionDestroyed(HttpSessionEvent se) {
        if (logger.isDebugEnabled()) {
            logger.debug("sessionDestroyed(HttpSessionEvent) - start");
        }

        HttpSession session = se.getSession();
        sessionMapBySessionid.remove(session.getId());
        count--;
        logger.info("sessionDestroyed,Session count="+count);
        logger.debug("sessionDestroyed(HttpSessionEvent) - end");
    }
	
}