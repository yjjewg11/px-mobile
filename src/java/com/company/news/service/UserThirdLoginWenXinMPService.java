package com.company.news.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.id.GUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.weixin4j.WeixinException;

import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.commons.util.HttpClientUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Parent;
import com.company.news.entity.UserThirdLoginWenXin;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.json.JSONUtils;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.TimeUtils;
import com.company.news.validate.CommonsValidate;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public  class UserThirdLoginWenXinMPService extends AbstractService {
	
	//微信小程序-测试登录
	static public Boolean TestLogin= "true".equals(ProjectProperties.getProperty(
			"WeixinAppTestLogin", "true"));
	
	//微信小程序-密钥
	static public String WeixinAppSecret= ProjectProperties.getProperty(
				"WeixinAppSecret", "123123123123");
	
	//微信小程序-appid
	static public String WeixinAppappId= ProjectProperties.getProperty(
			"WeixinApp_appId", "wx6699cf8b21e12618");
	@Autowired
	private SmsService smsService;
	
	@Autowired
	private UserinfoService userinfoService;
	
	
	/**
	 * 1.根据app 获取到的code,获取Access_token
	 * 
	 * 暂时不启用
	 * @return
	 * @throws WeixinException 
	 */
	public boolean update_access_tokenByCode(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String appid,String code) throws WeixinException,Exception {
		
		/**
		 * //正常返回的JSON数据包
{
      "openid": "OPENID",
      "session_key": "SESSIONKEY"
}
//错误时返回JSON数据包(示例为Code无效)
{
    "errcode": 40029,
    "errmsg": "invalid code"
}
		 */
		
		if(appid==null)appid=WeixinAppappId;
		
		String url="https://api.weixin.qq.com/sns/jscode2session?appid="+WeixinAppappId+"&secret="+WeixinAppSecret+"&js_code="+code+"&grant_type=authorization_code";
		String responseJson=HttpClientUtils.get(url);
		
		
		Map map=(Map)JSONUtils.jsonToObject(responseJson, Map.class);
		
		if(map.get("errmsg")!=null){
			
			
			if(TestLogin){
				map.put("openid", "WenXin_app_test");
				map.put("session_key", TimeUtils.getCurrentTime(TimeUtils.DEFAULTFORMAT));
			}else{
				String errmsg=map.get("errmsg")+"";
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
				responseMessage.setMessage(errmsg);
				return false;
			}
			

		}
		String openid=map.get("openid")+"";
		String session_key=map.get("session_key")+"";
				
		//获取用户信息
		UserThirdLoginWenXin userdb=(UserThirdLoginWenXin)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginWenXin.class, "openid", openid);
		
		if(userdb==null){//创建
			userdb=new UserThirdLoginWenXin();
			userdb.setAppid(appid);
			userdb.setOpenid(openid);
			userdb.setAccess_token(session_key);
		}
		//更新
		userdb.setAccess_token(session_key);
		
		nSimpleHibernateDao.save(userdb);
		
		String isBindParent=SystemConstants.UserThirdLogin_needBindTel_1;
		// 用户名是否存在
		if(StringUtils.isNotBlank(userdb.getRel_useruuid())){
			List list=nSimpleHibernateDao.createSqlQuery("select uuid from px_parent where uuid='"+userdb.getRel_useruuid()+"'").list();
			if(!list.isEmpty()){//// 用户名是否存在,则绑定
				isBindParent=SystemConstants.UserThirdLogin_needBindTel_0;
			}
		}
		
		model.put(RestConstants.Return_UserThirdLogin_needBindTel, isBindParent);
		model.put(RestConstants.Return_UserThirdLogin_access_token, session_key);
		
		return true;
	}

	
	/**
	 * 2.根据微信票据绑定用户,不存在则自动注册.
	 * @param model
	 * @param request
	 * @param responseMessage
	 * @param access_token
	 * @param tel
	 * @param smsCode
	 * @return
	 * @throws WeixinException
	 * @throws Exception
	 */
	public boolean update_bindTel(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String access_token,String tel,String smsCode) throws WeixinException,Exception {
		
//		"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
		
		UserThirdLoginWenXin userdb=(UserThirdLoginWenXin)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginWenXin.class, "access_token", access_token);
		
		if(userdb==null){//创建用户
			responseMessage.setMessage("无效票据,请重新授权.access_token="+access_token);
			return false;
		}
		
		// TEL格式验证
		if (!CommonsValidate.checkCellphone(tel)) {
			responseMessage.setMessage("电话号码格式不正确！");
			return false;
		}

		//验证码判断
		if (!smsService.VerifySmsCode(responseMessage,
				tel, smsCode)) {
			return false;
		}
//		
//		//判断手机是否已经注册
//		String attribute = "loginname";
//
//		Parent parent = (Parent) this.nSimpleHibernateDao.getObjectByAttribute(
//				Parent.class, attribute, tel);
//		
//		if(parent!=null){
//			userdb.setRel_useruuid(parent.getUuid());
//			nSimpleHibernateDao.save(userdb);
//			return true;
//		}
		
		
		// 用户名是否存在
		//parent ,Loginname=getRel_useruuid 表示没有绑定过手机,否则已经绑定了手机号码
		Parent parent=null;
		if(StringUtils.isNotBlank(userdb.getRel_useruuid())){
			parent=(Parent)nSimpleHibernateDao.getObject(Parent.class, userdb.getRel_useruuid());
			if(parent!=null){
				if(!parent.getLoginname().equals(userdb.getRel_useruuid())){
					this.logger.warn("用户已绑定过手机.access_token="+access_token);
					responseMessage.setMessage("用户已绑定过手机.access_token="+access_token);
					return false;
				}
				
				
				//判断是否存在的用户
				List list=nSimpleHibernateDao.createSqlQuery("select uuid from px_parent where tel='"+tel+"'").list();
				
				if(!list.isEmpty()){//// 用户名是否存在,则绑定
					this.logger.warn("该手机号码已经已被注册,不能绑定到当前帐号下面."+access_token);
					responseMessage.setMessage("该手机号码已经已被注册,不能绑定到当前帐号下面.");
					return false;
				}
			
				//已经登录而没有绑定手机号码的则绑定手机号码即可.
				parent.setLoginname(tel);
				parent.setTel(tel);
				 userinfoService.update_regSecond(parent, responseMessage);
				return true;
				
				
			}
		}
		
		
		// 用户名是否存在
		List list=nSimpleHibernateDao.createSqlQuery("select uuid from px_parent where tel='"+tel+"'").list();
		
		if(!list.isEmpty()){//// 用户名是否存在,则绑定
			userdb.setRel_useruuid((String)list.get(0));
			nSimpleHibernateDao.save(userdb);
			return true;
		}
		
		 parent = new Parent();

		parent.setLoginname(tel);
		parent.setTel(tel);
		parent.setName(PxStringUtil.getSubString(userdb.getNickname(), 45));
		parent.setImg(PxStringUtil.getSubString(userdb.getHeadimgurl(), 256));
		
		parent.setCreate_time(TimeUtils.getCurrentTimestamp());
		parent.setDisable(SystemConstants.USER_disable_default);
		parent.setLogin_time(TimeUtils.getCurrentTimestamp());
		parent.setTel_verify(SystemConstants.USER_tel_verify_default);
		parent.setCount(0l);
		nSimpleHibernateDao.save(parent);//
		
		parent=userinfoService.update_regSecond(parent, responseMessage);
		
		
		userdb.setRel_useruuid(parent.getUuid());
		nSimpleHibernateDao.save(userdb);
		return true;
				
	}
	
	/**
	 * 2.根据微信票据绑定注册用户
	 * @param model
	 * @param request
	 * @param responseMessage
	 * @param access_token
	 * @param tel
	 * @param smsCode
	 * @return
	 * @throws WeixinException
	 * @throws Exception
	 */
	public boolean update_bindAccount(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String access_token,SessionUserInfoInterface parent) throws WeixinException,Exception {
		
//		"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
		
		UserThirdLoginWenXin userdb=(UserThirdLoginWenXin)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginWenXin.class, "access_token", access_token);
		
		if(userdb==null){//创建用户
			responseMessage.setMessage("无效票据,请重新授权.access_token="+access_token);
			return false;
		}
		if(parent==null){//创建用户
			responseMessage.setMessage("用户不存在,access_token="+access_token);
			return false;
		}
		
//		if(StringUtils.isNotBlank(userdb.getRel_useruuid())){
//			this.logger.warn("用户已绑定过帐号.access_token="+access_token);
//			responseMessage.setMessage("用户已绑定过帐号.access_token="+access_token);
//			return false;
//		}

		userdb.setRel_useruuid(parent.getUuid());
		nSimpleHibernateDao.save(userdb);
		return true;
				
	}
	
	/**
	 * 根据app 获取到的code,获取Access_token
	 * 
	 * @return
	 * @throws WeixinException 
	 */
	public Parent update_loginByaccess_token(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String access_token) throws WeixinException,Exception {
		
//		"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
		
		UserThirdLoginWenXin userdb=(UserThirdLoginWenXin)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginWenXin.class, "access_token", access_token);
		
		if(userdb==null){//创建用户
			responseMessage.setMessage("无效票据,请重新授权.access_token="+access_token);
			return null;
		}
		
		Parent parent=null;
		if(StringUtils.isNotBlank(userdb.getRel_useruuid())){
			parent=(Parent)nSimpleHibernateDao.getObject(Parent.class, userdb.getRel_useruuid());
			
			
			
		}
		
		
		if(parent==null){//初始化用户成功!
			responseMessage.setMessage("请先绑定手机号码。");
			return null;
		}
	
//		if(parent==null){//初始化用户成功!
//			parent = new Parent();
//
//				parent.setLoginname(userdb.getUuid());//不能为空,和必须唯一.临时填写,后面变更为parent uuid
////				parent.setTel();
//				parent.setName(PxStringUtil.getSubString(userdb.getNickname(), 45));
//				parent.setImg(PxStringUtil.getSubString(userdb.getHeadimgurl(), 256));
//				
//				parent.setCreate_time(TimeUtils.getCurrentTimestamp());
//				parent.setDisable(SystemConstants.USER_disable_default);
//				parent.setLogin_time(TimeUtils.getCurrentTimestamp());
//				parent.setTel_verify(SystemConstants.USER_tel_verify_default);
//				parent.setCount(0l);
//				parent.setType(11);
//				nSimpleHibernateDao.save(parent);//生成主键uuid
//				
////				parent=userinfoService.update_regSecond(parent, responseMessage);
//				
//				//设置关联关心
//				userdb.setRel_useruuid(parent.getUuid());
//				nSimpleHibernateDao.save(userdb);
//				
//				//不能为空,和必须唯一.临时填写,后面变更为parent uuid
//				parent.setLoginname(parent.getUuid());
//				
//				
//				nSimpleHibernateDao.save(parent);//
//				
//		}
//		UserRedisCache.setUserCacheByParent(parent);
//		HttpSession session =userinfoService.sessionCreateByParent(parent, model, request, responseMessage);
//		// 将关联系学生信息放入
//		
//		userinfoService.putSession(session, parent, request);
//		boolean flag = userinfoService.getUserAndStudent(model, request, responseMessage);
//		
		return parent;
				
	}
	

	
	/**
	 * 根据app 获取到的code,获取Access_token
	 * 
	 * @return
	 * @throws WeixinException 
	 */
	public boolean update_logoutByaccess_token(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String access_token) throws WeixinException,Exception {
		
//		"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
		
		SessionUserInfoInterface user=SessionListener.getUserInfoBySession(request);
		UserThirdLoginWenXin userdb=(UserThirdLoginWenXin)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginWenXin.class, "access_token", access_token);
		
		if(userdb==null){//创建用户
			responseMessage.setMessage("无效票据,请重新授权.access_token="+access_token);
			return false;
		}
		
		if(user.getUuid().equals(userdb.getRel_useruuid())){
			userdb.setAccess_token(null);
			this.nSimpleHibernateDao.save(userdb);
			return true;
		}
		
		return false;
				
	}
	
	@Override
	public Class getEntityClass() {
		
		// TODO Auto-generated method stub
		return null;
	}
	


}
