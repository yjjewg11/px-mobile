package com.company.news.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.weixin4j.WeixinException;

import com.company.common.HttpRequestUtils;
import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Parent;
import com.company.news.entity.UserThirdLoginQQ;
import com.company.news.entity.UserThirdLoginQQ;
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
public  class UserThirdLoginQQService extends AbstractService {
	//String url="https://graph.qq.com/user/get_simple_userinfo?oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json";
	
	static public String QQ_Appid= ProjectProperties.getProperty(
				"QQ_Appid", "100330589");
	
	static public String QQ_appkey= ProjectProperties.getProperty(
			"QQ_appkey", "639c78a45d012434370f4c1afc57acd1");
	@Autowired
	private SmsService smsService;
	
	@Autowired
	private UserinfoService userinfoService;
	/**
	 * 1.根据app 获取到的code,获取Access_token
{
    "ret": 0,
    "msg": "",
    "is_lost": 0,
    "nickname": "飨受人生",
    "gender": "男",
    "province": "四川",
    "city": "成都",
    "figureurl": "http://qzapp.qlogo.cn/qzapp/100330589/0020D2823810BC79EAA0E228E14F1688/30",
    "figureurl_1": "http://qzapp.qlogo.cn/qzapp/100330589/0020D2823810BC79EAA0E228E14F1688/50",
    "figureurl_2": "http://qzapp.qlogo.cn/qzapp/100330589/0020D2823810BC79EAA0E228E14F1688/100",
    "figureurl_qq_1": "http://q.qlogo.cn/qqapp/100330589/0020D2823810BC79EAA0E228E14F1688/40",
    "figureurl_qq_2": "http://q.qlogo.cn/qqapp/100330589/0020D2823810BC79EAA0E228E14F1688/100",
    "is_yellow_vip": "0",
    "vip": "0",
    "yellow_vip_level": "0",
    "level": "0",
    "is_yellow_year_vip": "0"
}
	 * @return
	 * @throws WeixinException 
	 */
	public boolean update_access_token(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String appid,String access_token,String openid) throws WeixinException,Exception {
		//oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json
		String para="format=json&oauth_consumer_key="+QQ_Appid+"&access_token="+access_token+"&openid="+openid;
		String url="https://graph.qq.com/user/get_simple_userinfo?"+para;
		JSONObject json=HttpRequestUtils.httpGet(url);
		
		
		if(json==null){
			responseMessage.setMessage("QQ用户信息返回数据失败");
			return false;
		}
		this.logger.info("QQ_access_token:"+json);
		try {
			Integer ret= json.getInt("ret");
			if(!Integer.valueOf(0).equals(ret)){
				responseMessage.setMessage("QQ返回消息:"+json.getString("msg"));
				return false;
			}
		} catch (Exception e) {
			
			responseMessage.setMessage("QQ用户信息获取失败.返回数据异常:"+json);
			return false;
		}
		
		UserThirdLoginQQ userdb=(UserThirdLoginQQ)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginQQ.class, "openid", openid);
		
		if(userdb==null){//创建
			userdb=new UserThirdLoginQQ();
			
			
			try {
				userdb.setNickname(json.getString("nickname"));
				userdb.setCity(json.getString("city"));
				userdb.setProvince(json.getString("province"));
				userdb.setHeadimgurl(json.getString("figureurl_2"));
				Integer sex=0;
				if("男".equals(json.getString("province"))){
					sex=1;
				}else if("女".equals(json.getString("province"))){
					sex=2;
				}
				userdb.setSex(sex);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			userdb.setAppid(appid);
			userdb.setOpenid(openid);
		}
		//更新
		userdb.setAccess_token(access_token);
		
		nSimpleHibernateDao.save(userdb);
		
		String isBindParent=SystemConstants.UserThirdLogin_needBindTel_1;
		// 用户名是否存在
		if(StringUtils.isNotBlank(userdb.getRel_useruuid())){
			List list=nSimpleHibernateDao.createSqlQuery("select uuid,loginname from px_parent where uuid='"+userdb.getRel_useruuid()+"'").list();
			if(!list.isEmpty()){//// 用户名是否存在,则绑定
				isBindParent=SystemConstants.UserThirdLogin_needBindTel_0;
			}
		}
		
		model.put(RestConstants.Return_UserThirdLogin_needBindTel, isBindParent);
		model.put(RestConstants.Return_UserThirdLogin_access_token, access_token);
		
		
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
		
		UserThirdLoginQQ userdb=(UserThirdLoginQQ)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginQQ.class, "access_token", access_token);
		
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
		
		
		//判断是否存在的用户
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
		
		UserThirdLoginQQ userdb=(UserThirdLoginQQ)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginQQ.class, "access_token", access_token);
		
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
	public Parent loginByaccess_token(ModelMap model, HttpServletRequest request,ResponseMessage responseMessage,String access_token) throws WeixinException,Exception {
		
//		"https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code"
		
		UserThirdLoginQQ userdb=(UserThirdLoginQQ)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginQQ.class, "access_token", access_token);
		
		if(userdb==null){//创建用户
			responseMessage.setMessage("无效票据,请重新授权.access_token="+access_token);
			return null;
		}
		
		Parent parent=null;
		if(StringUtils.isBlank(userdb.getRel_useruuid())){
			responseMessage.setMessage("没有关联用户手机号码请关联.access_token="+access_token);
			return null;
			
			
		}
		parent=(Parent)nSimpleHibernateDao.getObject(Parent.class, userdb.getRel_useruuid());
		
		if(parent==null){//初始化用户成功!
//			responseMessage.setMessage("没有关联用户信息.access_token="+access_token);
//			return null;
			
			//自动创建用户

			 parent = new Parent();

			parent.setLoginname(userdb.getUuid());//不能为空,和必须唯一.临时填写,后面变更为parent uuid
//			parent.setTel();
			parent.setName(PxStringUtil.getSubString(userdb.getNickname(), 45));
			parent.setImg(PxStringUtil.getSubString(userdb.getHeadimgurl(), 256));
			
			parent.setCreate_time(TimeUtils.getCurrentTimestamp());
			parent.setDisable(SystemConstants.USER_disable_default);
			parent.setLogin_time(TimeUtils.getCurrentTimestamp());
			parent.setTel_verify(SystemConstants.USER_tel_verify_default);
			parent.setCount(0l);
			nSimpleHibernateDao.save(parent);//生成主键uuid
			
//			parent=userinfoService.update_regSecond(parent, responseMessage);
			
			//设置关联关心
			userdb.setRel_useruuid(parent.getUuid());
			nSimpleHibernateDao.save(userdb);
			
			//不能为空,和必须唯一.临时填写,后面变更为parent uuid
			parent.setLoginname(parent.getUuid());
			nSimpleHibernateDao.save(parent);//
			
			
		}
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
		UserThirdLoginQQ userdb=(UserThirdLoginQQ)this.nSimpleHibernateDao.getObjectByAttribute(UserThirdLoginQQ.class, "access_token", access_token);
		
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
	
	
	public static void main(String[] s)throws Exception{
//		HttpClient dd=new HttpClient();
//		dd.setOpenID("0020D2823810BC79EAA0E228E14F1688");
//		dd.setToken("BF796FA297CC548136E8819D9BC1EA92");
//		PostParameter[] par=new PostParameter[4];
//		//oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json
////		par[0]=new PostParameter("grant_type","authorization_code");
////		par[0]=new PostParameter("client_id",appid);
////		par[0]=new PostParameter("client_secret",QQ_appkey);
////		par[0]=new PostParameter("code",code);
////		par[0]=new PostParameter("grant_type","authorization_code");
//		
//		par[0]=new PostParameter("oauth_consumer_key","100330589");
//		par[1]=new PostParameter("access_token","BF796FA297CC548136E8819D9BC1EA92");
//		par[2]=new PostParameter("openid","0020D2823810BC79EAA0E228E14F1688");
//		par[3]=new PostParameter("format","json");
//		Response response=dd.get("https://graph.qq.com/user/get_simple_userinfo", par);
//		String responseStr=response.asString();
//		
//		System.out.println(responseStr);
		
		UserThirdLoginQQService dd=new UserThirdLoginQQService();
		String url="https://graph.qq.com/user/get_simple_userinfo?oauth_consumer_key=100330589&access_token=BF796FA297CC548136E8819D9BC1EA92&openid=0020D2823810BC79EAA0E228E14F1688&format=json";
		Object responseStr=HttpRequestUtils.httpGet(url);
		System.out.print(responseStr);
	}


}
