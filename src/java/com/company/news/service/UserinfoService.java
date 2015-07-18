package com.company.news.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Parent;
import com.company.news.entity.ParentStudentRelation;
import com.company.news.entity.Student;
import com.company.news.entity.StudentContactRealation;
import com.company.news.entity.StudentOfSession;
import com.company.news.entity.TelSmsCode;
import com.company.news.entity.User;
import com.company.news.form.UserLoginForm;
import com.company.news.jsonform.ParentRegJsonform;
import com.company.news.jsonform.UserRegJsonform;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.TimeUtils;
import com.company.news.validate.CommonsValidate;
import com.company.news.vo.ResponseMessage;
import com.company.plugin.security.LoginLimit;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator 家长模块
 */
@Service
public class UserinfoService extends AbstractServcice {
	@Autowired
	private StudentService studentService;
	
	/**
	 * 用户注册
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean reg(ParentRegJsonform parentRegJsonform,
			ResponseMessage responseMessage) throws Exception {

		// TEL格式验证
		if (!CommonsValidate.checkCellphone(parentRegJsonform.getTel())) {
			responseMessage.setMessage("电话号码格式不正确！");
			return false;
		}

		// 用户名是否存在
		if (isExitSameUserByLoginName(parentRegJsonform.getTel())) {
			responseMessage.setMessage("电话号码已被注册！");
			return false;
		}

		if (parentRegJsonform.getType() == null)
			parentRegJsonform.setType(1);

		Parent parent = new Parent();

		BeanUtils.copyProperties(parent, parentRegJsonform);
		parent.setLoginname(parentRegJsonform.getTel());
		parent.setCreate_time(TimeUtils.getCurrentTimestamp());
		parent.setDisable(SystemConstants.USER_disable_default);
		parent.setLogin_time(TimeUtils.getCurrentTimestamp());
		parent.setTel_verify(SystemConstants.USER_tel_verify_default);
		
		//当昵称为空时，使用登陆名作为初始昵称
		if(StringUtils.isBlank(parent.getName()))
		parent.setName(parent.getLoginname());

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(parent);

		
		/**
		 * 1.根据注册手机,绑定和学生的关联关心.
		 * 2.更新孩子数据时,也会自动绑定学生和家长的数据.
		 */
		List<StudentContactRealation> list = studentService.getStudentByPhone(parent.getTel());
		if (list != null)
			for (StudentContactRealation s : list) {
				// 保存用户机构关联表
				ParentStudentRelation parentStudentRelation = new ParentStudentRelation();
				parentStudentRelation.setParentuuid(parent.getUuid());
				parentStudentRelation.setStudentuuid(s.getStudent_uuid());
				parentStudentRelation.setType(s.getType());
				// 有事务管理，统一在Controller调用时处理异常
				this.nSimpleHibernateDao.getHibernateTemplate().save(
						parentStudentRelation);
			}
		return true;
	}
	
	/**
	 * 用户注册
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Parent update(UserRegJsonform userRegJsonform,
			ResponseMessage responseMessage) throws Exception {

		// name昵称验证
		if (StringUtils.isBlank(userRegJsonform.getName())
				|| userRegJsonform.getName().length() > 15) {
			responseMessage.setMessage("昵称不能为空，且长度不能超过15位！");
			return null;
		}

		Parent user = (Parent) this.nSimpleHibernateDao.getObject(Parent.class,
				userRegJsonform.getUuid());
		if (user == null) {
			responseMessage.setMessage("user不存在！");
			return null;
		}

		user.setName(userRegJsonform.getName());
		user.setEmail(userRegJsonform.getEmail());


		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().update(user);

		return user;
	}

	/**
	 * 
	 * @param loginName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean login(UserLoginForm userLoginForm, ModelMap model,
			HttpServletRequest request, ResponseMessage responseMessage)
			throws Exception {
		String loginname = userLoginForm.getLoginname();
		String password = userLoginForm.getPassword();

		if (StringUtils.isBlank(loginname)) {
			responseMessage.setMessage("用户登录名不能为空!");
			return false;
		}
		if (StringUtils.isBlank(password)) {
			responseMessage.setMessage("登陆密码不能为空!");
			return false;
		}

		String attribute = "loginname";

		Parent parent = (Parent) this.nSimpleHibernateDao.getObjectByAttribute(
				Parent.class, attribute, loginname);

		if (parent == null) {
			responseMessage.setMessage("用户名:" + loginname + ",不存在!");
			return false;
		}

		boolean pwdIsTrue = false;
		{
			// 密码比较
			String smmPWD = parent.getPassword();

			if (password.equals(smmPWD)) {
				pwdIsTrue = true;
			} else {
				pwdIsTrue = false;
			}

			// 在限定次数内
			String project_loginLimit = ProjectProperties.getProperty(
					"project.LoginLimit", "true");
			if ("true".equals(project_loginLimit)) {
				if (!LoginLimit.verifyCount(loginname, pwdIsTrue,
						responseMessage)) {// 密码错误次数验证
					return false;
				}
				if (!pwdIsTrue) {
					responseMessage.setMessage("用户登录名或者密码错误，请重试!");
					return false;
				}

			} else {
				if (!pwdIsTrue) {
					responseMessage.setMessage("用户登录名或者密码错误，请重试!");
					return false;
				}
			}

		}

		// 创建session
		HttpSession session = SessionListener
				.getSession((HttpServletRequest) request);

		if (session != null) {
			Parent userInfo = (Parent) session
					.getAttribute(RestConstants.Session_UserInfo);
			if (userInfo != null && loginname.equals(userInfo.getLoginname())) {
				return true;
			}
		}

		// 更新登陆日期,最近一次登陆日期
		this.nSimpleHibernateDao
				.getHibernateTemplate()
				.bulkUpdate(
						"update Parent set login_time=?,last_login_time=? where uuid=?",
						TimeUtils.getCurrentTimestamp(),
						parent.getLogin_time(), parent.getUuid());
		session = request.getSession(true);
		// this.nSimpleHibernateDao.getHibernateTemplate().evict(user);
		SessionListener.putSessionByJSESSIONID(session);

		session.setAttribute(RestConstants.Session_UserInfo, parent);
		List<StudentOfSession>  studentOfSessionlist=this.getStudentOfSessionByParentuuid(parent.getUuid());
		session.setAttribute(RestConstants.Session_StudentslistOfParent, studentOfSessionlist);
		
		// 返回客户端用户信息放入Map
		//putUserInfoReturnToModel(model, request);

		model.put(RestConstants.Return_JSESSIONID, session.getId());
		// model.put(RestConstants.Return_UserInfo, userInfoReturn);

		return true;
	}

	/**
	 * 是否用户名已被占用
	 * 
	 * @param loginname
	 * @return
	 */
	public boolean isExitSameUserByLoginName(String loginname) {
		String attribute = "loginname";
		Object user = nSimpleHibernateDao.getObjectByAttribute(Parent.class,
				attribute, loginname);

		if (user != null)// 已被占用
			return true;
		else
			return false;

	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return Parent.class;
	}

	/**
	 * 查询所有机构列表
	 * 
	 * @return
	 */
	public List<User> query() {
		return (List<User>) this.nSimpleHibernateDao.getHibernateTemplate()
				.find("from Parent", null);
	}

	/**
	 * 查询指定机构的用户列表
	 * 
	 * @return
	 */
	public List<Student> getStudentByParentuuid(String uuid) {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = "";
		Query q = s
				.createSQLQuery(
						"select {t1.*} from px_parentstudentrelation t0,px_student {t1} where t0.studentuuid={t1}.uuid and t0.parentuuid='"
								+ uuid + "'").addEntity("t1", Student.class);

		return q.list();
	}
	/**
	 * 查询指定机构的用户列表
	 * 
	 * @return
	 */
	public List<StudentOfSession> getStudentOfSessionByParentuuid(String uuid) {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = "";
		Query q = s
				.createSQLQuery(
						"select {t1.*} from px_parentstudentrelation t0,px_student {t1} where t0.studentuuid={t1}.uuid and t0.parentuuid='"
								+ uuid + "'").addEntity("t1", StudentOfSession.class);

		return q.list();
	}

	/**
	 * 
	 * @param disable
	 * @param useruuid
	 */
	public boolean updateDisable(String disable, String useruuids,
			ResponseMessage responseMessage) {
		// 更新用户状态
		// Group_uuid昵称验证
		if (StringUtils.isBlank(useruuids)) {
			responseMessage.setMessage("useruuid不能为空！");
			return false;
		}

		if (StringUtils.isBlank(disable)) {
			responseMessage.setMessage("disable不能为空！");
			return false;
		}

		int disable_i = SystemConstants.USER_disable_default;
		try {
			disable_i = Integer.parseInt(disable);
			if (disable_i != SystemConstants.USER_disable_true)// 不是禁用时，默认都是0
				disable_i = SystemConstants.USER_disable_default;
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"update Parent set disable=? where uuid in(?)", disable_i,
				PxStringUtil.StringDecComma(useruuids));
		return true;
	}

	/**
	 * 
	 * @param disable
	 * @param useruuid
	 */
	public boolean updatePassword(ParentRegJsonform userRegJsonform,
			ResponseMessage responseMessage) {
		// 更新用户状态
		// Group_uuid昵称验证
		if (StringUtils.isBlank(userRegJsonform.getUuid())) {
			responseMessage.setMessage("useruuid不能为空！");
			return false;
		}

		if (StringUtils.isBlank(userRegJsonform.getOldpassword())) {
			responseMessage.setMessage("Oldpassword不能为空！");
			return false;
		}

		if (StringUtils.isBlank(userRegJsonform.getPassword())) {
			responseMessage.setMessage("Password不能为空！");
			return false;
		}

		Parent user = (Parent) this.nSimpleHibernateDao.getObject(Parent.class,
				userRegJsonform.getUuid());
		if (user == null) {
			responseMessage.setMessage("user不存在！");
			return false;
		}

		if (!user.getPassword().equals(userRegJsonform.getOldpassword())) {
			responseMessage.setMessage("Oldpassword不匹配！");
			return false;
		}

		this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"update Parent set password=? where uuid =?",
				userRegJsonform.getPassword(), userRegJsonform.getUuid());
		return true;
	}

	/**
	 * 
	 * @param disable
	 * @param useruuid
	 */
	public boolean updatePasswordBySms(ParentRegJsonform userRegJsonform,
			ResponseMessage responseMessage) {
		// 更新用户状态
		// Group_uuid昵称验证
		if (StringUtils.isBlank(userRegJsonform.getTel())) {
			responseMessage.setMessage("Tel不能为空！");
			return false;
		}

		if (StringUtils.isBlank(userRegJsonform.getSmscode())) {
			responseMessage.setMessage("Smscode不能为空！");
			return false;
		}

		if (StringUtils.isBlank(userRegJsonform.getPassword())) {
			responseMessage.setMessage("Password不能为空！");
			return false;
		}

		List<TelSmsCode> list=(List<TelSmsCode>) this.nSimpleHibernateDao.getHibernateTemplate().find("from TelSmsCode where tel=? and type=? order by createtime desc", userRegJsonform.getTel(),SmsService.SMS_TYPE_USER);
		
		TelSmsCode smsdb;
		if(list!=null&&list.size()>0)
			smsdb=list.get(0);
		else
		{
			responseMessage.setMessage("请重发验证码!");
			return false;
		}
		
		 long timeInterval=TimeUtils.getCurrentTimestamp().getTime()-smsdb.getCreatetime().getTime();
	      if(timeInterval>SmsService.SMS_TIME_LIMIT){//防止暴力破解.
	        responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
	        responseMessage.setMessage("验证码已经失效!");
	        return false;
	      }
		// 验证码成功
		if (smsdb != null
				&& smsdb.getCode().equals(userRegJsonform.getSmscode())) {
//			if (!this.isExitSameUserByLoginName(userRegJsonform.getTel())) {
//				responseMessage
//						.setStatus(RestConstants.Return_ResponseMessage_failed);
//				responseMessage.setMessage("电话号码不存在！");
//				return false;
//			}

			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"update Parent set password=? where loginname =?",
					userRegJsonform.getPassword(), userRegJsonform.getTel());
			//清除验证码,防止暴力破解.
			this.nSimpleHibernateDao.delete(smsdb);
			return true;

		}
		responseMessage.setMessage("短信验证码不正确！");
		return false;

	}
}
