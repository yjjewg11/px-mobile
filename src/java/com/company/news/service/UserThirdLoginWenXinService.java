package com.company.news.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.weixin4j.OAuth2;
import org.weixin4j.OAuth2User;
import org.weixin4j.WeixinException;
import org.weixin4j.http.OAuth2Token;

import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.cache.UserCache;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Parent;
import com.company.news.entity.UserThirdLoginWenXin;
import com.company.news.interfaces.SessionUserInfoInterface;
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
public  class UserThirdLoginWenXinService extends AbstractService {
	static public String WeixinAppSecret= ProjectProperties.getProperty(
				"WeixinAppSecret", "123123123123");
	
	static public String WeixinAppappId= ProjectProperties.getProperty(
			"WeixinApp_appId", "wx6699cf8b21e12618");
	@Autowired
	private SmsService smsService;
	
	@Autowired
	private UserinfoService userinfoService;
	
	
	public boolean update_access_tokenByAccess_token(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String appid,String access_token,String openid,String refresh_token) throws WeixinException,Exception {
		OAuth2 oAuth2=new OAuth2();
		oAuth2.init(access_token, WeixinAppappId, WeixinAppSecret, "snsapi_userinfo", refresh_token, openid, "", 7200);
		
		//获取用户信息
				OAuth2User oAuth2User=oAuth2.getUserInfo();
				
				UserThirdLoginWenXin userdb=(UserThirdLoginWenXin)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginWenXin.class, "openid", oAuth2User.getOpenid());
				
				if(userdb==null){//创建
					userdb=new UserThirdLoginWenXin();
					BeanUtils.copyProperties(userdb, oAuth2User);
					userdb.setAppid(appid);
					userdb.setOpenid(oAuth2User.getOpenid()); 
				}
				//更新
				userdb.setAccess_token(access_token);
				
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
				model.put(RestConstants.Return_UserThirdLogin_access_token, access_token);
				
				
				return true;
	}
	/**
	 * 1.根据app 获取到的code,获取Access_token
	 * 
	 * 暂时不启用
	 * @return
	 * @throws WeixinException 
	 */
	@Deprecated
	public boolean update_access_token(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String appid,String code) throws WeixinException,Exception {
		
//		"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
		OAuth2 oAuth2=new OAuth2();
		
		OAuth2Token token=oAuth2.login(appid, WeixinAppSecret, code);
		OAuth2Token dd=new OAuth2Token();
		dd.setAccess_token(code);
		dd.setOpenid(appid);
		//获取用户信息
		OAuth2User oAuth2User=oAuth2.getUserInfo();
		
		
		UserThirdLoginWenXin userdb=(UserThirdLoginWenXin)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginWenXin.class, "openid", oAuth2User.getOpenid());
		
		if(userdb==null){//创建
			userdb=new UserThirdLoginWenXin();
			BeanUtils.copyProperties(userdb, oAuth2User);
			userdb.setAppid(appid);
			userdb.setOpenid(token.getOpenid());
		}
		//更新
		userdb.setAccess_token(token.getAccess_token());
		
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
		model.put(RestConstants.Return_UserThirdLogin_access_token, token.getAccess_token());
		
		
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
		
		// 用户名是否存在
		//parent ,Loginname=Loginname 表示没有绑定过手机,否则已经绑定了手机号码
		Parent parent=null;
		if(StringUtils.isNotBlank(userdb.getRel_useruuid())){
			parent=(Parent)nSimpleHibernateDao.getObject(Parent.class, userdb.getRel_useruuid());
			if(parent!=null){
				if(!parent.getLoginname().equals(userdb.getRel_useruuid())){
					this.logger.warn("用户已绑定过手机.access_token="+access_token);
					responseMessage.setMessage("用户已绑定过手机.access_token="+access_token);
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
		
		if(StringUtils.isNotBlank(userdb.getRel_useruuid())){
			this.logger.warn("用户已绑定过帐号.access_token="+access_token);
			responseMessage.setMessage("用户已绑定过帐号.access_token="+access_token);
			return false;
		}

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
			parent = new Parent();

				parent.setLoginname(userdb.getUuid());//不能为空,和必须唯一.临时填写,后面变更为parent uuid
//				parent.setTel();
				parent.setName(PxStringUtil.getSubString(userdb.getNickname(), 45));
				parent.setImg(PxStringUtil.getSubString(userdb.getHeadimgurl(), 256));
				
				parent.setCreate_time(TimeUtils.getCurrentTimestamp());
				parent.setDisable(SystemConstants.USER_disable_default);
				parent.setLogin_time(TimeUtils.getCurrentTimestamp());
				parent.setTel_verify(SystemConstants.USER_tel_verify_default);
				parent.setCount(0l);
				parent.setType(11);
				nSimpleHibernateDao.save(parent);//生成主键uuid
				
//				parent=userinfoService.update_regSecond(parent, responseMessage);
				
				//设置关联关心
				userdb.setRel_useruuid(parent.getUuid());
				nSimpleHibernateDao.save(userdb);
				
				//不能为空,和必须唯一.临时填写,后面变更为parent uuid
				parent.setLoginname(parent.getUuid());
				
				
				nSimpleHibernateDao.save(parent);//
				
		}
		UserRedisCache.setUserCacheByParent(parent);
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
