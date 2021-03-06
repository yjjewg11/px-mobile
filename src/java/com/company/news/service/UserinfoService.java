package com.company.news.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.company.http.PxHttpSession;
import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.cache.UserCache;
import com.company.news.cache.redis.SessionUserRedisCache;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.core.iservice.NewMsgNumberIservice;
import com.company.news.entity.Group;
import com.company.news.entity.Parent;
import com.company.news.entity.ParentBaseInfo;
import com.company.news.entity.ParentData;
import com.company.news.entity.PxStudentContactRealation;
import com.company.news.entity.Student;
import com.company.news.entity.StudentContactRealation;
import com.company.news.entity.User4Q;
import com.company.news.form.UserLoginForm;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ParentDataJsonform;
import com.company.news.jsonform.ParentRegJsonform;
import com.company.news.jsonform.UserRegJsonform;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.session.UserOfSession;
import com.company.news.validate.CommonsValidate;
import com.company.news.vo.ResponseMessage;
import com.company.news.vo.TeacherPhone;
import com.company.news.vo.UserInfoReturn;
import com.company.plugin.security.LoginLimit;
import com.company.web.filter.UserInfoFilter;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator 家长模块
 */
@Service
public class UserinfoService extends AbstractService {
	@Autowired
	private StudentService studentService;
	@Autowired
	private PxStudentService pxStudentService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SmsService smsService;
	
	@Autowired
	private NewMsgNumberIservice newMsgNumberIservice;
	
	
	/**
	 * 注册第2步
	 * @param parentRegJsonform
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public Parent update_regSecond(Parent parent,
			ResponseMessage responseMessage) throws Exception {
		// 电话保密
				if (StringUtils.isBlank(parent.getName())) {
					parent.setName(StringUtils.substring(parent.getLoginname(), 0, 3)
							+ "****" + StringUtils.substring(parent.getLoginname(), 7));
				}
				parent.setNickname(parent.getName());
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(parent);
		
		/**
		 * 培训机构
		 * 1.根据注册手机,绑定和学生的关联关心. 2.更新孩子数据时,也会自动绑定学生和家长的数据.
		 */
		List<PxStudentContactRealation> pxlist = pxStudentService
				.getStudentByPhone(parent.getTel());

		if (pxlist != null&&pxlist.size()>0){
			
			for (PxStudentContactRealation s : pxlist) {
				// 更新家长姓名和头像.多个孩子已最后保存为准
				parent.setName(PxStringUtil
						.getParentNameByStudentContactRealation(s));
				parent.setImg(s.getStudent_img());
				this.nSimpleHibernateDao.getHibernateTemplate().save(parent);
//				
//				// 有事务管理，统一在Controller调用时处理异常
//				s.setIsreg(SystemConstants.USER_isreg_1);
//				s.setParent_uuid(parent.getUuid());
//				this.nSimpleHibernateDao.getHibernateTemplate().save(s);
			}
			

			//更新互动表
			Integer count=this.nSimpleHibernateDao
					.getHibernateTemplate()
					.bulkUpdate(
							"update PxStudentContactRealation set isreg=?,parent_uuid=? where tel =?",
							SystemConstants.USER_isreg_1, parent.getUuid(), parent.getTel());

			this.logger.info(parent.getTel()+"=tel.update PxStudentContactRealation count=" + count);
			
		}

		/**
		 * 
		 * 幼儿园
		 * 1.根据注册手机,绑定和学生的关联关心. 2.更新孩子数据时,也会自动绑定学生和家长的数据.
		 */
		List<StudentContactRealation> list = studentService
				.getStudentByPhone(parent.getTel());

		if (list != null&&list.size()>0){
			for (StudentContactRealation s : list) {
				// 更新家长姓名和头像.多个孩子已最后保存为准
				parent.setName(PxStringUtil
						.getParentNameByStudentContactRealation(s));
				parent.setImg(s.getStudent_img());
				this.nSimpleHibernateDao.getHibernateTemplate().save(parent);
				
//				// 有事务管理，统一在Controller调用时处理异常
//				s.setIsreg(SystemConstants.USER_isreg_1);
//				s.setParent_uuid(parent.getUuid());
//				this.nSimpleHibernateDao.getHibernateTemplate().save(s);
			}
			
			

			//更新互动表
			Integer count=this.nSimpleHibernateDao
					.getHibernateTemplate()
					.bulkUpdate(
							"update StudentContactRealation set isreg=?,parent_uuid=? where tel =?",
							SystemConstants.USER_isreg_1, parent.getUuid(), parent.getTel());

			this.logger.info(parent.getTel()+"=tel.update StudentContactRealation count=" + count);
			
			
		}

		
		
		
		UserRedisCache.setUserCacheByParent(parent);
		
		return parent;
	}
	/**
	 * 用户注册.第一步验证
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

		if (!smsService.VerifySmsCode(responseMessage,
				parentRegJsonform.getTel(), parentRegJsonform.getSmscode())) {
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
		parent.setCount(0l);
	
		
		  parent=this.update_regSecond(parent, responseMessage);
		  
		if(parent==null)return false;
		 
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
		boolean needUpdateCreateImg = false;
		// 头像有变化,更新相应的表.
		if (userRegJsonform.getImg() != null
				&& !userRegJsonform.getImg().equals(user.getImg())) {
			needUpdateCreateImg = true;
		}
		// 名字有变化更新相应的表.
		else if (!userRegJsonform.getName().equals(user.getName())) {
			needUpdateCreateImg = true;
		}

		user.setName(userRegJsonform.getName());
		user.setEmail(userRegJsonform.getEmail());
		user.setImg(userRegJsonform.getImg());
		user.setRealname(userRegJsonform.getRealname());
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().update(user);

		if (needUpdateCreateImg)
			this.relUpdate_updateSessionUserInfoInterface(user);

		return user;
	}

	/**
	 * 用户或老师资料修改时变更数据.
	 * 
	 * @param uuid
	 * @param name
	 * @param img
	 */
	public void relUpdate_updateSessionUserInfoInterface(
			SessionUserInfoInterface user) {
		UserRedisCache.setUserCacheByParent(user);
		return;
//		int count = 0;

//		
//		//更新话题表
//				count = this.nSimpleHibernateDao
//						.getHibernateTemplate()
//						.bulkUpdate(
//								"update SnsTopic set create_user=?,create_img=? where create_useruuid =?",
//								user.getName(), user.getImg(), user.getUuid());
//
//				this.logger.info("update ClassNewsReply count=" + count);
//				
//				count = this.nSimpleHibernateDao
//						.getHibernateTemplate()
//						.bulkUpdate(
//								"update SnsReply set create_user=?,create_img=? where create_useruuid =?",
//								user.getName(), user.getImg(), user.getUuid());
//
//				this.logger.info("update ClassNewsReply count=" + count);
		//更新互动表
//		count = this.nSimpleHibernateDao
//				.getHibernateTemplate()
//				.bulkUpdate(
//						"update ClassNewsReply set create_user=?,create_img=? where create_useruuid =?",
//						user.getName(), user.getImg(), user.getUuid());
//
//		this.logger.info("update ClassNewsReply count=" + count);
//		
		

		// 这个根据学生uuid生成
		// count= this.getHibernateTemplate().bulkUpdate(
		// "update ClassNews set create_user=?,create_img=? where
		// create_useruuid =?",
		// user.getName(),user.getImg(), user.getUuid());
		// this.logger.info("update ClassNews count="+count);
		//更新消息表发送者
//		count = this.nSimpleHibernateDao
//				.getHibernateTemplate()
//				.bulkUpdate(
//						"update Message set send_user=?,send_userimg=? where send_useruuid =?",
//						user.getName(), user.getImg(), user.getUuid());
//		this.logger.info("update Message count=" + count);
//		//更新消息表发送者接收者
//		count = this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
//				"update Message set revice_user=? where revice_useruuid =?",
//				user.getName(), user.getUuid());
//		this.logger.info("update Message count=" + count);

	}

	/**
	 * 
	 * @param loginName
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public Parent getParentByLoginForm(UserLoginForm userLoginForm, ModelMap model,
			HttpServletRequest request, ResponseMessage responseMessage)
			throws Exception {
		String loginname = userLoginForm.getLoginname();
		String password = userLoginForm.getPassword();

		if (StringUtils.isBlank(loginname)) {
			responseMessage.setMessage("用户登录名不能为空!");
			return null;
		}
		if (StringUtils.isBlank(password)) {
			responseMessage.setMessage("登陆密码不能为空!");
			return null;
		}

		String attribute = "loginname";

		Parent parent = (Parent) this.nSimpleHibernateDao.getObjectByAttribute(
				Parent.class, attribute, loginname);

		if (parent == null) {
			responseMessage.setMessage("用户名:" + loginname + ",不存在!");
			return null;
		}
		if (parent.getDisable() != null
				&& SystemConstants.USER_disable_true == parent.getDisable()
						.intValue()) {
			responseMessage.setMessage("帐号被禁用,请联系互动家园");
			return null;
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
					return null;
				}
				if (!pwdIsTrue) {
					responseMessage.setMessage("用户登录名或者密码错误，请重试!");
					return null;
				}

			} else {
				if (!pwdIsTrue) {
					responseMessage.setMessage("用户登录名或者密码错误，请重试!");
					return null;
				}
			}

		}
		
		return parent;
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
		
		Parent parent=this.getParentByLoginForm(userLoginForm, model, request, responseMessage);
		if(parent==null){
			return false;
		}
		// 创建session
		HttpSession session = SessionListener
				.getSession((HttpServletRequest) request);

		if (session != null) {
			SessionUserInfoInterface userInfo = (SessionUserInfoInterface) session
					.getAttribute(RestConstants.Session_UserInfo);
			if (userInfo != null && userLoginForm.getLoginname().equals(userInfo.getLoginname())) {
				return true;
			}
		}

		session=this.sessionCreateByParent(parent, model, request, responseMessage);

		// 更新登陆日期,最近一次登陆日期
		String sql = "update px_parent set count=count+1,last_login_time=login_time,login_time=now() where uuid='"
				+ parent.getUuid() + "'";
		this.nSimpleHibernateDao.createSQLQuery(sql).executeUpdate();
		//
		return true;
	}
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
	    String isBindParent=SystemConstants.UserThirdLogin_needBindTel_0;
	    if(user.getUuid().equals(user.getLoginname())){//表明未绑定手机号码.
	    	isBindParent=SystemConstants.UserThirdLogin_needBindTel_1;
	    }
	    
	    model.put(RestConstants.Return_UserThirdLogin_needBindTel, isBindParent);
	    
//	    userInfoReturn.setPassword(null);
	    model.addAttribute(RestConstants.Return_UserInfo,userInfoReturn);
	  }
	/**
	 * 获取登录用户和机构
	 * @param model
	 * @param request
	 * @param responseMessage
	 * @return
	 */
	public boolean getUserAndStudent(ModelMap model,
			HttpServletRequest request,ResponseMessage responseMessage){
		List list = new ArrayList();
		try {
//			list = getStudentByParentuuid(SessionListener.getUserInfoBySession(
//					request).getUuid());
			SessionUserInfoInterface user=SessionListener.getUserInfoBySession(
					request);
			
			list = getStudentByParentLoginname(user.getLoginname());
			
			String group_uuids=getMyChildrenGroupUuidsBySession(request);
			//String class_uuids=this.getMyChildrenClassuuidsBySession(request);
			model.addAttribute("group_list", getGroupVObyUuids(group_uuids));
			//model.addAttribute("class_list", userinfoService.getPClassbyUuids(class_uuids));
			model.addAttribute("class_list", getAllClassAndPxClass2(SessionListener.getUserInfoBySession(request)));
			
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
	 * 根据用户创建session
	 * @param parent
	 * @param model
	 * @param request
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public HttpSession sessionCreateByParent(Parent parent, ModelMap model,
			HttpServletRequest request, ResponseMessage responseMessage)
			throws Exception {
		


		// 创建session
		HttpSession session = SessionListener
				.getSession((HttpServletRequest) request);

		if (session != null) {
			SessionUserInfoInterface userInfo = (SessionUserInfoInterface) session
					.getAttribute(RestConstants.Session_UserInfo);
			if (userInfo != null && parent.getLoginname().equals(userInfo.getLoginname())) {
				return session;
			}
		}

		session = request.getSession(true);
		// this.nSimpleHibernateDao.getHibernateTemplate().evict(user);
		SessionListener.putSessionByJSESSIONID(session);
		
		UserOfSession userOfSession = new UserOfSession();
		try {
			BeanUtils.copyProperties(userOfSession, parent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.setAttribute(RestConstants.Session_UserInfo, userOfSession);

		model.put(RestConstants.Return_JSESSIONID, session.getId());

		return session;
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
	public List<Parent> query() {
		return (List<Parent>) this.nSimpleHibernateDao.getHibernateTemplate()
				.find("from Parent", null);
	}

	/**
	 * 查询指定机构的用户列表
	 * 
	 * @return
	 */
	@Deprecated
	public List<Student> getStudentByParentuuid(String uuid) {
		Session s = this.nSimpleHibernateDao.getSession();
		String sql = "";
		Query q = s
				.createSQLQuery(
						"select  DISTINCT {t1.*} from px_studentcontactrealation t0,px_student {t1} where t0.student_uuid={t1}.uuid and t0.parent_uuid='"
								+ DbUtils.safeToWhereString(uuid) + "'").addEntity("t1", Student.class);
		List<Student> list = q.list();
		s.clear();
		for (Student o : list) {
			o.setHeadimg(PxStringUtil.imgSmallUrlByUuid(o.getHeadimg()));
		}
		return list;
	}
	/**
	 * 查询指定机构的用户列表
	 * 
	 * @return
	 */
	public List<Student> getStudentByParentLoginname(String tel) {
		Session s = this.nSimpleHibernateDao.getSession();
		String sql = "";
		Query q = s
				.createSQLQuery(
						"select  DISTINCT {t1.*} from px_studentcontactrealation t0,px_student {t1} where t0.student_uuid={t1}.uuid and t0.tel='"
								+ DbUtils.safeToWhereString(tel) + "'").addEntity("t1", Student.class);
		List<Student> list = q.list();
		s.clear();
		for (Student o : list) {
			o.setHeadimg(PxStringUtil.imgSmallUrlByUuid(o.getHeadimg()));
		}
		return list;
	}

	

	/**
	 * 返回孩子在培训机构登记的uuid.
	 * 
	 * @return
	 */
	public String getPxStudentuuidsByMy(String parentuuid) {
		String sql = "";
		Query q = this.nSimpleHibernateDao
				.createSQLQuery(
						"select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
								+ DbUtils.safeToWhereString(parentuuid) + "'");
		List list=q.list();
		return StringUtils.join(list, ",");
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
			responseMessage.setMessage("用户不能为空！");
			return false;
		}

		if (StringUtils.isBlank(userRegJsonform.getOldpassword())) {
			responseMessage.setMessage("原密码不能为空！");
			return false;
		}

		if (StringUtils.isBlank(userRegJsonform.getPassword())) {
			responseMessage.setMessage("新密码不能为空！");
			return false;
		}

		Parent user = (Parent) this.nSimpleHibernateDao.getObject(Parent.class,
				userRegJsonform.getUuid());
		if (user == null) {
			responseMessage.setMessage("用户不存在！");
			return false;
		}

		if (!user.getPassword().equals(userRegJsonform.getOldpassword())) {
			responseMessage.setMessage("原密码输入错误！");
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
			responseMessage.setMessage("手机号码不能为空！");
			return false;
		}

		if (StringUtils.isBlank(userRegJsonform.getSmscode())) {
			responseMessage.setMessage("验证码不能为空！");
			return false;
		}

		if (StringUtils.isBlank(userRegJsonform.getPassword())) {
			responseMessage.setMessage("密码不能为空！");
			return false;
		}

		if (!smsService.VerifySmsCode(responseMessage,
				userRegJsonform.getTel(), userRegJsonform.getSmscode())) {
			return false;
		}

		this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"update Parent set password=? where loginname =?",
				userRegJsonform.getPassword(), userRegJsonform.getTel());
		return true;

	}

	/**
	 * 获取园长通信录
	 * 
	 * @param group_uuids
	 * @return
	 */
	public List getKDTeacherPhoneList(String group_uuids) {
		List list = new ArrayList();
	
		{
			TeacherPhone teacherPhone = new TeacherPhone();
			teacherPhone.setType(SystemConstants.TeacherPhone_type_0);
			teacherPhone.setTeacher_uuid(SystemConstants.Group_uuid_wjd);
			teacherPhone.setName("问界科技问题反馈");
			teacherPhone.setTel("");
			teacherPhone.setImg("http://img.wenjienet.com/i/logo.png");
			list.add(teacherPhone);
		
		}
		if (StringUtils.isNotBlank(group_uuids)) {
			String[] uuid = group_uuids.split(",");
			for (String s : uuid) {
				Group cb = (Group) CommonsCache.get(s, Group.class);
				if (cb == null)
					continue;
				TeacherPhone teacherPhone = new TeacherPhone();
				teacherPhone.setType(SystemConstants.TeacherPhone_type_0);
				teacherPhone.setTeacher_uuid(s);
				teacherPhone.setName(cb.getBrand_name() + "园长");
				teacherPhone.setTel("");
				teacherPhone.setImg("http://img.wenjienet.com/i/KD_header.png");
				list.add(teacherPhone);
			}
		}
		return list;
	}

	/**
	 * 获取关联老师通信录
	 * 
	 * @param group_uuids
	 * @return
	 */
	public List getTeacherPhoneList(String class_uuids) {
		List list = new ArrayList();
		if(StringUtils.isBlank(class_uuids)){
			return list;
		}
		String hql = "from User4Q where uuid in (select useruuid from UserClassRelation where classuuid in("
				+ DBUtil.stringsToWhereInValue(class_uuids) + "))";
		List<User4Q> userList = (List<User4Q>) this.nSimpleHibernateDao
				.getHibernateTemplate().find(hql, null);
		
		for (User4Q user : userList) {
			TeacherPhone teacherPhone = new TeacherPhone();
			teacherPhone.setType(SystemConstants.TeacherPhone_type_1);
			teacherPhone.setTeacher_uuid(user.getUuid());
			teacherPhone.setName(user.getName());
			teacherPhone.setTel(user.getTel());
			teacherPhone.setImg(PxStringUtil.imgSmallUrlByUuid(user.getImg()));
			list.add(teacherPhone);
		}
		return list;
	}

	public StudentService getStudentService() {
		return studentService;
	}

	public void setStudentService(StudentService studentService) {
		this.studentService = studentService;
	}

	public SmsService getSmsService() {
		return smsService;
	}

	public void setSmsService(SmsService smsService) {
		this.smsService = smsService;
	}

	public List getDynamicMenu() {
		return this.nSimpleHibernateDao.getHibernateTemplate().find(
				"from DynamicMenu where enable=1 and type=1 order by index",
				null);
	}

	public List getGroupVObyUuids(String uuids) {
		if (StringUtils.isBlank(uuids))
			return new ArrayList();
		List list = this.nSimpleHibernateDao.getHibernateTemplate().find(
				"from Group4Q where type=1 and uuid in("
						+ DBUtil.stringsToWhereInValue(uuids) + ")");
		groupService.warpVoList(list);
		return list;
	}
	
	public List getAllClassby(String uuids) {
		if (StringUtils.isBlank(uuids))
			return new ArrayList();
		return this.nSimpleHibernateDao.getHibernateTemplate().find(
				"from PClass where  uuid in("
						+ DBUtil.stringsToWhereInValue(uuids) + ")");
	}

	
	/**
	 * 查询所有班级,包括:幼儿园班级和培训机构
	 * @param user
	 * @return
	 */
	@Deprecated
	public List getAllClassAndPxClass(SessionUserInfoInterface user) {
		
		//查询幼儿园班级
		String sql="SELECT t1.uuid,t1.name,t1.groupuuid from px_class t1"
				+" inner join px_student t2 on t2.classuuid=t1.uuid  " 
				+" inner join px_studentcontactrealation t3 on t3.student_uuid=t2.uuid  " 
				+"   where parent_uuid='"
				+ DbUtils.safeToWhereString(user.getUuid()) + "'  ";
		//查询培训机构班级
		String sqlpxclass = "select t1.uuid,t1.name,t1.groupuuid"
				+ " from px_pxstudentpxclassrelation t0 "
				+ " inner join px_pxclass t1 on t0.class_uuid=t1.uuid "
				+ " inner join px_pxstudent t4 on t0.student_uuid=t4.uuid  "
				+ " where t0.student_uuid  in( "
				+ " select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
				+ DbUtils.safeToWhereString(user.getUuid()) + "' ) "
				+" and t1.isdisable ="+SystemConstants.Class_isdisable_0;
	    
		Query  query =this.nSimpleHibernateDao.createSQLQuery(sql+" UNION "+sqlpxclass);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
		
	}
	

	/**
	 * 查询所有班级,包括:幼儿园班级和培训机构
	 * @param user
	 * @return
	 */
	public List getAllClassAndPxClass2(SessionUserInfoInterface user) {
		
		//查询幼儿园班级
		String sql="SELECT t1.uuid,t1.name,t1.groupuuid from px_class t1"
				+" inner join px_student t2 on t2.classuuid=t1.uuid  " 
				+" inner join px_studentcontactrealation t3 on t3.student_uuid=t2.uuid  " 
				+"   where t3.tel='"
				+ user.getLoginname() + "'  ";
		//查询培训机构班级
		String sqlpxclass = "select t1.uuid,t1.name,t1.groupuuid"
				+ " from px_pxstudentpxclassrelation t0 "
				+ " inner join px_pxclass t1 on t0.class_uuid=t1.uuid "
				+ " inner join px_pxstudent t4 on t0.student_uuid=t4.uuid  "
				+ " where t0.student_uuid  in( "
				+ " select  DISTINCT student_uuid from px_pxstudentcontactrealation where tel='"
				+ user.getLoginname() + "' ) "
				+" and t1.isdisable ="+SystemConstants.Class_isdisable_0;
	    
		Query  query =this.nSimpleHibernateDao.createSQLQuery(sql+" UNION "+sqlpxclass);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
		
	}
	

	public List getPClassbyUuids(String uuids) {
		if (StringUtils.isBlank(uuids))
			return new ArrayList();
		return this.nSimpleHibernateDao.getHibernateTemplate().find(
				"from PClass where  uuid in("
						+ DBUtil.stringsToWhereInValue(uuids) + ")");
	}

	public boolean updateParentData(ParentDataJsonform jsonform,
			HttpServletRequest request, ResponseMessage responseMessage){
		
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		ParentData ut=(ParentData) this.nSimpleHibernateDao.getObjectByAttribute(ParentData.class, "parent_uuid", user.getUuid());
				if(ut==null){
					ut = new ParentData();
					
				}else{
					jsonform.setUuid(ut.getUuid());
				}
//				try {
//					BeanUtils.copyProperties(ut, jsonform);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					
//				}
				if(StringUtils.isNotBlank(jsonform.getApp_verion())){
					ut.setApp_verion(jsonform.getApp_verion());
				}
				if(StringUtils.isNotBlank(jsonform.getCity())){
					if(jsonform.getCity().endsWith("市")){
						jsonform.setCity(StringUtils.substring(jsonform.getCity(), 0, jsonform.getCity().length()-1));
					}
					ut.setCity(jsonform.getCity());
				}
				if(StringUtils.isNotBlank(jsonform.getPhone_type())){
					ut.setPhone_type(jsonform.getPhone_type());
				}
				if(StringUtils.isNotBlank(jsonform.getPhone_version())){
					ut.setPhone_version(jsonform.getPhone_version());
				}
					ut.setParent_uuid(user.getUuid());	
				ut.setUpdate_time(TimeUtils.getCurrentTimestamp());
				ut.setIp(UserInfoFilter.getIpAddr(request));
				ut.setLoginname(user.getLoginname());
				
				
				this.nSimpleHibernateDao.getHibernateTemplate().saveOrUpdate(ut);

				return true;
	}
	
	/**
	 * 根据手机号码获取
	 * 
	 * @param loginname
	 * @return
	 */
	@Deprecated
	public Parent getUserBySessionid(String sessionid) {
		
		
		
		String hql="from Parent where sessionid=:sessionid or uuid in (select DISTINCT user_uuid from PushMsgDevice where sessionid=:sessionid2)";
		
		Query q= nSimpleHibernateDao.getSession().createQuery(hql);  
		q.setString("sessionid", sessionid);//名称  
		q.setString("sessionid2", sessionid);//密码  
		List list=q.list();
		if(list!=null&&list.size()>0){
			return (Parent)list.get(0);
		}
		return null;
		
//		
//		String attribute = "sessionid";
//		return (Parent) nSimpleHibernateDao.getObjectByAttribute(Parent.class,
//				attribute, sessionid);

	}
	public boolean updateAndloginForJessionid(String jessionid,
			HttpServletRequest request) {
		if(StringUtils.isBlank(jessionid)){
			return false;
		}
		// 登录验证.验证失败则返回.
		
		//根据参数相同视为同一把锁.
	
		try {
//			Object lockObject=JavaLockUtils.getLockObj(jessionid);
			UserCache user = null;
			HttpSession session = null;
			
				//导致死锁
				//synchronized (lockObject)
				{
					
					
					//从缓存中取
					UserOfSession userOfSession =SessionUserRedisCache.getUserOfSessionBySessionid(jessionid);
//					user = getUserBySessionid(jessionid);
					if (userOfSession == null){// 请求服务返回失败标示
						this.logger.info("user is null,jessionid="+jessionid);
						return false;
					
					}
					
					session=SessionListener.getSession(request);
					// 同步加锁情况下,再次判断,防止多次创建session
					if (session != null&&session.getAttribute(RestConstants.Session_UserInfo)!=null) {
						return true;
					}
					
					this.logger.info("jessionid="+jessionid);
					
					session = new PxHttpSession(jessionid);
					SessionListener.putSessionByJSESSIONID(session);

					session.setAttribute(RestConstants.Session_UserInfo, userOfSession);
					//修复多并发取不到.Session_UserInfo bug.
//					try {
//						BeanUtils.copyProperties(userOfSession, user);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
					
					// 设置session数据
					this.putSession(session, userOfSession, request);
				}
			
			// 更新登陆日期,最近一次登陆日期
//			String sql = "update px_parent set sessionid='" + session.getId() + "' where uuid='"
//					+ user.getUuid() + "'";
//			Session session1=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
//			Transaction transaction=session1.beginTransaction();
//			session1.createSQLQuery(sql).executeUpdate();
//			transaction.commit();
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
			
		}finally{
//        	JavaLockUtils.removeLockObj(jessionid);
		}

	}
	

	public Map getNewMsgNumber(HttpServletRequest request,
			ResponseMessage responseMessage) {
		
		Map map=new HashMap();
		//空字符串表示不启用话题.否则未话题的地址.
		//map.put("today_hudong",0);
		map.put("today_snsTopic",newMsgNumberIservice.today_snsTopic());
		map.put("today_goodArticle",newMsgNumberIservice.today_goodArticle());
		map.put("today_pxbenefit",newMsgNumberIservice.today_pxbenefit());
		map.put("today_unreadPushMsg",newMsgNumberIservice.today_unreadPushMessage(SessionListener.getUserInfoBySession(request)));
		
		return map;
	}
	
	/**
	 * 根据手机号码获取
	 * 
	 * @param loginname
	 * @return
	 */
	public ParentBaseInfo getParentBaseInfoByUUid(String uuid) {
		
		ParentBaseInfo dd= (ParentBaseInfo)nSimpleHibernateDao.getObject(ParentBaseInfo.class, uuid);
		nSimpleHibernateDao.getHibernateTemplate().evict(dd);
		dd.setImg(PxStringUtil.imgSmallUrlByUuid(dd.getImg()));
		
		return dd;
		
	}


}
