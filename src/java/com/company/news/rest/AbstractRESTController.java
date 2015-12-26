package com.company.news.rest;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;

import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Parent;
import com.company.news.entity.StudentOfSession;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.StringOperationUtil;
import com.company.news.vo.UserInfoReturn;
import com.company.web.listener.SessionListener;



public class AbstractRESTController   {
	static{
		ConvertUtils.register(new DateConverter(null), java.util.Date.class); 
		ConvertUtils.register(new SqlTimestampConverter(), java.sql.Timestamp.class);
		ConvertUtils.register(new SqlTimestampConverter(null), java.sql.Timestamp.class);
	}
	protected static final String error_bodyJsonToFormObject="请求协议不匹配";//JSON对象解析失败
	  /**
	   * 返回客户端用户信息放入Map
	   * @param request
	   * @return
	   */
	  protected void putUserInfoReturnToModel( ModelMap model,HttpServletRequest request){
		  SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
	    // 返回用户信息
	    UserInfoReturn userInfoReturn = new UserInfoReturn();
	    try {
	      BeanUtils.copyProperties(userInfoReturn, user);
	      userInfoReturn.setImg(PxStringUtil.imgSmallUrlByUuid(userInfoReturn.getImg()));
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
//	    userInfoReturn.setPassword(null);
	    model.addAttribute(RestConstants.Return_UserInfo,userInfoReturn);
	  }
	  
	  /**
	   * 
	   * @param request
	   */
	  protected PaginationData getPaginationDataByRequest(HttpServletRequest request){
		  PaginationData pd=new PaginationData();
		  if(StringUtils.isNotBlank(request.getParameter("pageNo")))
			  pd.setPageNo(Integer.parseInt(request.getParameter("pageNo")));
		  if(StringUtils.isNotBlank(request.getParameter("pageSize")))
			  pd.setPageSize(Integer.parseInt(request.getParameter("pageSize")));
		  
		  return pd;
	  }
	  
	  /**
	   * 
	   * @param request
	   * @return
	   */
	  protected SessionUserInfoInterface getUserInfoBySession(HttpServletRequest request){
		  SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		    // 返回用户信息
		    return user;
		  }
	  
	  /**
	   *  获取我的孩子列表
	   * @param request
	   * @return
	   */
	  protected List<StudentOfSession>  getMyChildrenBySession(HttpServletRequest request){
		  HttpSession session =SessionListener.getSession(request);
		    return (List<StudentOfSession> )session.getAttribute(RestConstants.Session_StudentslistOfParent);
		  }
	  
	  /**
	   *  获取session中的值,根据key
	   * @param request
	   * @return
	   */
	  protected Object  getValueOfSession(HttpServletRequest request,String key){
		  HttpSession session =SessionListener.getSession(request);
		    return session.getAttribute(key);
		  }
	  
	  /**
	   * 获取我的孩子关联班级的uuid
	   * 
	   * @param request
	   * @return 多个逗号分割.
	   * uuids:uuid1,uuid2
	   */
	  protected String  getMyChildrenClassuuidsBySession(HttpServletRequest request){
		  String uuids="";
		  List<StudentOfSession> list= this.getMyChildrenBySession(request);
		  for (StudentOfSession stu:list){
			  if(SystemConstants.DB_String_unrelated_Value.equals(stu.getClassuuid())){
				  continue;
			  }
			  uuids+=stu.getClassuuid()+",";
		  }
		  return StringOperationUtil.trimSeparatorChars(uuids);
		}
	  
	  /**
	   * 获取我的孩子关联的组织uuid
	   * 
	   * @param request
	   * @return 多个逗号分割.
	   * uuids:uuid1,uuid2
	   */
	  protected String  getMyChildrenGroupUuidsBySession(HttpServletRequest request){
		  String uuids="";
		  List<StudentOfSession> list= this.getMyChildrenBySession(request);
		  for (StudentOfSession stu:list){
			  if(SystemConstants.DB_String_unrelated_Value.equals(stu.getGroupuuid())){
				  continue;
			  }
			  if(uuids.indexOf(stu.getGroupuuid())==-1)
				  uuids+=stu.getGroupuuid()+",";
		  }
		  return StringOperationUtil.trimSeparatorChars(uuids);
		}
	  
	  /**
	   * 获取我的孩子uuid
	   * 
	   * @param request
	   * @return 多个逗号分割.
	   * uuids:uuid1,uuid2
	   */
	  protected String  getMyChildrenUuidsBySession(HttpServletRequest request){
		  String uuids="";
		  List<StudentOfSession> list= this.getMyChildrenBySession(request);
		  for (StudentOfSession stu:list){
			  uuids+=stu.getUuid()+",";
		  }
		  return StringOperationUtil.trimSeparatorChars(uuids);
		}
	  /**
	   * 
	   * @param bodyJson
	   * @param clazz
	   * @param model
	   * @return
	   * @throws Exception
	   */
	  public static Collection bodyJsonToObjectCollection(String bodyJson,Class clazz) throws Exception{
	  

//	    return JSONUtils.jsonToObject(bodyJson, clazz);
	    JSONObject jsonObject = null;
//	    JsonConfig jsonConfig = new JsonConfig();
//	    jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonDateValueProcessor() );
//	    jsonConfig.registerJsonValueProcessor(Timestamp.class, new JsonDateValueProcessor() );
	//    
	//  //  jsonObject = JSONObject.fromObject(bodyJson, jsonConfig);
//	    JSONUtils.getMorpherRegistry().registerMorpher(
//	      new NpmsDateMorpher(new String[] {"yyyy-MM-dd HH:mm:ss" }));
	    JSONArray jSONArray = JSONArray.fromObject(bodyJson);
	    
	    return  JSONArray.toCollection(jSONArray, clazz);
//	    return JSONObject.toBean(jsonObject, clazz);
	  }
	  /**
	   * 
	   * @param bodyJson
	   * @param clazz
	   * @param model
	   * @return
	   * @throws Exception
	   */
	  public static Object bodyJsonToFormObject(String bodyJson,Class clazz) throws Exception{
	  

//	    return JSONUtils.jsonToObject(bodyJson, clazz);
	    JSONObject jsonObject = null;
//	    JsonConfig jsonConfig = new JsonConfig();
//	    jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonDateValueProcessor() );
//	    jsonConfig.registerJsonValueProcessor(Timestamp.class, new JsonDateValueProcessor() );
	//    
	//  //  jsonObject = JSONObject.fromObject(bodyJson, jsonConfig);
//	    JSONUtils.getMorpherRegistry().registerMorpher(
//	      new NpmsDateMorpher(new String[] {"yyyy-MM-dd HH:mm:ss" }));
	    jsonObject = JSONObject.fromObject(bodyJson);
	    return  JSONObject.toBean(jsonObject,clazz);
//	    return JSONObject.toBean(jsonObject, clazz);
	  }

	  public static Properties bodyJsonToProperties(String bodyJson) throws Exception{
//	    return (Properties)JSONUtils.jsonToObject(bodyJson, Properties.class);
	    JSONObject jsonObject = null;
	    JsonConfig jsonConfig = new JsonConfig();
//	    jsonConfig.registerDefaultValueProcessor(java.util.Date.class, new JsonDateValueProcessor() );
//	    jsonObject = JSONObject.toBean(bodyJson, jsonConfig);//fromObject(bodyJson);
	    jsonObject = JSONObject.fromObject(bodyJson);
	    return (Properties)JSONObject.toBean(jsonObject, Properties.class);
	  }

}
