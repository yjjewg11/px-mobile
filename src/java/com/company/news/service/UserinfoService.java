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
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.core.iservice.NewMsgNumberIservice;
import com.company.news.entity.Group;
import com.company.news.entity.Parent;
import com.company.news.entity.ParentData;
import com.company.news.entity.PxStudentContactRealation;
import com.company.news.entity.Student;
import com.company.news.entity.StudentContactRealation;
import com.company.news.entity.StudentOfSession;
import com.company.news.entity.User4Q;
import com.company.news.form.UserLoginForm;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ParentDataJsonform;
import com.company.news.jsonform.ParentRegJsonform;
import com.company.news.jsonform.UserRegJsonform;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.validate.CommonsValidate;
import com.company.news.vo.ResponseMessage;
import com.company.news.vo.TeacherPhone;
import com.company.plugin.security.LoginLimit;
import com.company.web.filter.UserInfoFilter;
import com.company.web.listener.SessionListener;
import com.company.web.session.UserOfSession;

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
		// 电话保密
		if (StringUtils.isBlank(parent.getName())) {
			parent.setName(StringUtils.substring(parent.getLoginname(), 0, 3)
					+ "****" + StringUtils.substring(parent.getLoginname(), 7));
		}

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(parent);

		
		
		
		/**
		 * 培训机构
		 * 1.根据注册手机,绑定和学生的关联关心. 2.更新孩子数据时,也会自动绑定学生和家长的数据.
		 */
		List<PxStudentContactRealation> pxlist = pxStudentService
				.getStudentByPhone(parent.getTel());

		if (pxlist != null)
			for (PxStudentContactRealation s : pxlist) {
				// 更新家长姓名和头像.多个孩子已最后保存为准
				parent.setName(PxStringUtil
						.getParentNameByStudentContactRealation(s));
				parent.setImg(s.getStudent_img());
				this.nSimpleHibernateDao.getHibernateTemplate().save(parent);

				// 有事务管理，统一在Controller调用时处理异常
				s.setIsreg(SystemConstants.USER_isreg_1);
				s.setParent_uuid(parent.getUuid());
				this.nSimpleHibernateDao.getHibernateTemplate().save(s);
			}

		/**
		 * 
		 * 幼儿园
		 * 1.根据注册手机,绑定和学生的关联关心. 2.更新孩子数据时,也会自动绑定学生和家长的数据.
		 */
		List<StudentContactRealation> list = studentService
				.getStudentByPhone(parent.getTel());

		if (list != null)
			for (StudentContactRealation s : list) {
				// 更新家长姓名和头像.多个孩子已最后保存为准
				parent.setName(PxStringUtil
						.getParentNameByStudentContactRealation(s));
				parent.setImg(s.getStudent_img());
				this.nSimpleHibernateDao.getHibernateTemplate().save(parent);

				// 有事务管理，统一在Controller调用时处理异常
				s.setIsreg(SystemConstants.USER_isreg_1);
				s.setParent_uuid(parent.getUuid());
				this.nSimpleHibernateDao.getHibernateTemplate().save(s);
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

		int count = 0;

		
		//更新话题表
				count = this.nSimpleHibernateDao
						.getHibernateTemplate()
						.bulkUpdate(
								"update SnsTopic set create_user=?,create_img=? where create_useruuid =?",
								user.getName(), user.getImg(), user.getUuid());

				this.logger.info("update ClassNewsReply count=" + count);
				
				count = this.nSimpleHibernateDao
						.getHibernateTemplate()
						.bulkUpdate(
								"update SnsReply set create_user=?,create_img=? where create_useruuid =?",
								user.getName(), user.getImg(), user.getUuid());

				this.logger.info("update ClassNewsReply count=" + count);
		//更新互动表
		count = this.nSimpleHibernateDao
				.getHibernateTemplate()
				.bulkUpdate(
						"update ClassNewsReply set create_user=?,create_img=? where create_useruuid =?",
						user.getName(), user.getImg(), user.getUuid());

		this.logger.info("update ClassNewsReply count=" + count);
		
		

		// 这个根据学生uuid生成
		// count= this.getHibernateTemplate().bulkUpdate(
		// "update ClassNews set create_user=?,create_img=? where
		// create_useruuid =?",
		// user.getName(),user.getImg(), user.getUuid());
		// this.logger.info("update ClassNews count="+count);
		//更新消息表发送者
		count = this.nSimpleHibernateDao
				.getHibernateTemplate()
				.bulkUpdate(
						"update Message set send_user=?,send_userimg=? where send_useruuid =?",
						user.getName(), user.getImg(), user.getUuid());
		this.logger.info("update Message count=" + count);
		//更新消息表发送者接收者
		count = this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"update Message set revice_user=? where revice_useruuid =?",
				user.getName(), user.getUuid());
		this.logger.info("update Message count=" + count);

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
		if (parent.getDisable() != null
				&& SystemConstants.USER_disable_true == parent.getDisable()
						.intValue()) {
			responseMessage.setMessage("帐号被禁用,请联系互动家园");
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
			SessionUserInfoInterface userInfo = (SessionUserInfoInterface) session
					.getAttribute(RestConstants.Session_UserInfo);
			if (userInfo != null && loginname.equals(userInfo.getLoginname())) {
				return true;
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

		// 移到CONTROLLER调用，减少长事务执行
		// List<StudentOfSession>
		// studentOfSessionlist=this.getStudentOfSessionByParentuuid(parent.getUuid());
		// session.setAttribute(RestConstants.Session_StudentslistOfParent,
		// studentOfSessionlist);

		// 返回客户端用户信息放入Map
		// putUserInfoReturnToModel(model, request);

		model.put(RestConstants.Return_JSESSIONID, session.getId());
		// model.put(RestConstants.Return_UserInfo, userInfoReturn);

		// 更新登陆日期,最近一次登陆日期
		String sql = "update px_parent set count=count+1,last_login_time=login_time,login_time=now(),sessionid='"+session.getId()+"' where uuid='"
				+ parent.getUuid() + "'";
		this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(sql).executeUpdate();
		//
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
	public List<Parent> query() {
		return (List<Parent>) this.nSimpleHibernateDao.getHibernateTemplate()
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
	public List<StudentOfSession> getStudentOfSessionByParentuuid(String uuid) {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = "";
		Query q = s
				.createSQLQuery(
						"select  DISTINCT {t1.*} from px_studentcontactrealation t0,px_student {t1} where t0.student_uuid={t1}.uuid and t0.parent_uuid='"
								+ DbUtils.safeToWhereString(uuid) + "'").addEntity("t1",
						StudentOfSession.class);

		return q.list();
	}
	

	/**
	 * 返回孩子在培训机构登记的uuid.
	 * 
	 * @return
	 */
	public String getPxStudentuuidsByMy(String parentuuid) {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = "";
		Query q = s
				.createSQLQuery(
						"select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
								+ DbUtils.safeToWhereString(parentuuid) + "'");
		List list=q.list();
		return StringUtils.join(list, ",");
	}
	
	/**
	 * 返回孩子在培训机构登记的uuid.
	 * 
	 * @return
	 */
	public String getPxClassuuidsByMyChild(String parentuuid) {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = "select class_uuid from px_pxstudentpxclassrelation where student_uuid in( select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
								+DbUtils.safeToWhereString( parentuuid) + "' )";
		Query q = s
				.createSQLQuery(sql);
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
	public List getAllClassAndPxClass(SessionUserInfoInterface user) {
		
		Session session=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
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
	    
		Query  query =session.createSQLQuery(sql+" UNION "+sqlpxclass);
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
	public Parent getUserBySessionid(String sessionid) {
		String attribute = "sessionid";
		return (Parent) nSimpleHibernateDao.getObjectByAttribute(Parent.class,
				attribute, sessionid);

	}
	public boolean updateAndloginForJessionid(String jessionid,
			HttpServletRequest request) {
		if(StringUtils.isBlank(jessionid)){
			return false;
		}
		// 登录验证.验证失败则返回.
		try {
			Parent user = null;
			HttpSession session = null;
			synchronized (this) {
				session=SessionListener.getSession(request);
				// 同步加锁情况下,再次判断,防止多次创建session
				if (session != null&&session.getAttribute(RestConstants.Session_UserInfo)!=null) {
					return true;
				}
				user = getUserBySessionid(jessionid);
				if (user == null)// 请求服务返回失败标示
					return false;
				
				
				session = new PxHttpSession(jessionid);
				SessionListener.putSessionByJSESSIONID(session);
			}

			UserOfSession userOfSession = new UserOfSession();
			try {
				BeanUtils.copyProperties(userOfSession, user);
			} catch (Exception e) {
				e.printStackTrace();
			}
			session.setAttribute(RestConstants.Session_UserInfo, userOfSession);
			// 设置session数据
			this.putSession(session, userOfSession, request);

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
		}

	}
	/**
	 * 返回客户端用户信息放入Map
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public void putSession( HttpSession session,
			SessionUserInfoInterface parent, HttpServletRequest request)
			 {
		List<StudentOfSession> studentOfSessionlist=getStudentOfSessionByParentuuid(parent.getUuid());
		session.setAttribute(RestConstants.Session_StudentslistOfParent, studentOfSessionlist);
		//我的孩子参加的培训班
		session.setAttribute(RestConstants.Session_MyStudentClassUuids, getPxClassuuidsByMyChild(parent.getUuid()));
		
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


}
