package com.company.news.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.company.news.SystemConstants;
import com.company.news.cache.UserCache;
import com.company.news.cache.redis.SessionUserRedisCache;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.dao.NSimpleHibernateDao;
import com.company.news.entity.ClassNewsReply;
import com.company.news.entity.Operate;
import com.company.news.entity.StudentOfSession;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.RestConstants;
import com.company.news.rest.util.StringOperationUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.session.UserOfSession;
import com.company.news.vo.DianzanListVO;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

public abstract class AbstractService {
  public static final String ID_SPLIT_MARK = ",";
  protected static Logger logger = LoggerFactory.getLogger(AbstractService.class);
  @Autowired
  @Qualifier("NSimpleHibernateDao")
  protected NSimpleHibernateDao nSimpleHibernateDao;
  /**
   * 数据库实体
   * @return
   */
  public abstract Class getEntityClass();
  /**
	 * 查询指定机构的用户列表
	 * 
	 * @return
	 */
  @Deprecated
	public List<StudentOfSession> getStudentOfSessionByParentuuid(String uuid) {
		Session s = this.nSimpleHibernateDao.getSession();
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
  @Deprecated
	public String getPxClassuuidsByMyChild(String parentuuid) {
		String sql = "select class_uuid from px_pxstudentpxclassrelation where student_uuid in( select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
								+DbUtils.safeToWhereString( parentuuid) + "' )";
		Query q = this.nSimpleHibernateDao
				.createSQLQuery(sql);
		List list=q.list();
		return StringUtils.join(list, ",");
	}
	
	
	/**
	 * 查询指定机构的用户列表
	 * 
	 * @return
	 */
	public List<StudentOfSession> getStudentOfSessionByLoginname(String tel) {
		Session s = this.nSimpleHibernateDao.getSession();
		String sql = "";
		Query q = s
				.createSQLQuery(
						"select  DISTINCT {t1.*} from px_studentcontactrealation t0,px_student {t1} where t0.student_uuid={t1}.uuid and t0.tel='"
								+ tel + "'").addEntity("t1",
						StudentOfSession.class);

		return q.list();
	}
	/**
	 * 返回孩子在培训机构登记的uuid.
	 * 
	 * @return
	 */
	public String getPxClassuuidsByMyChildByLoginname(String tel) {
		String sql = "select class_uuid from px_pxstudentpxclassrelation where student_uuid in( select  DISTINCT student_uuid from px_pxstudentcontactrealation where tel='"
								+tel+ "' )";
		Query q = this.nSimpleHibernateDao
				.createSQLQuery(sql);
		List list=q.list();
		return StringUtils.join(list, ",");
	}

	  /**
	   * 获取我的孩子uuid
	   * 
	   * @param request
	   * @return 多个逗号分割.
	   * uuids:uuid1,uuid2
	   */
	  public String  getMyChildrenUuidsBySession(HttpServletRequest request){
		  String uuids="";
		  List<StudentOfSession> list= this.getMyChildrenBySession(request);
		  for (StudentOfSession stu:list){
			  uuids+=stu.getUuid()+",";
		  }
		  return StringOperationUtil.trimSeparatorChars(uuids);
		}
	  /**
	   * 获取我的孩子关联班级的uuid
	   * 
	   * @param request
	   * @return 多个逗号分割.
	   * uuids:uuid1,uuid2
	   */
	  public String  getMyChildrenClassuuidsBySession(HttpServletRequest request){
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
	  public String  getMyChildrenGroupUuidsBySession(HttpServletRequest request){
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
	 * 返回客户端用户信息放入Map.缓存redis没有该数据,根据需要按需加载
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public void putSession( HttpSession session,
			SessionUserInfoInterface parent, HttpServletRequest request)
			 {
//		List<StudentOfSession> studentOfSessionlist=getStudentOfSessionByParentuuid(parent.getUuid());
		
		List<StudentOfSession> studentOfSessionlist=getStudentOfSessionByLoginname(parent.getLoginname());
		if(studentOfSessionlist==null)studentOfSessionlist=new ArrayList();
		session.setAttribute(RestConstants.Session_StudentslistOfParent, studentOfSessionlist);
		//我的孩子参加的培训班
//		String myStudentClassUuids =getPxClassuuidsByMyChild(parent.getUuid());
		
		String myStudentClassUuids =getPxClassuuidsByMyChildByLoginname(parent.getLoginname());
		if(myStudentClassUuids==null)myStudentClassUuids="";
		session.setAttribute(RestConstants.Session_MyStudentClassUuids,myStudentClassUuids);
		
		//redis 缓存sessionid
		SessionUserRedisCache.set(session.getId(), (UserOfSession)parent);
	}
	
	/**
	   *  获取我的孩子列表..缓存redis没有该数据,根据需要按需加载
	   * @param request
	   * @return
	   */
	public List<StudentOfSession>  getMyChildrenBySession(HttpServletRequest request){
		  	HttpSession session =SessionListener.getSession(request);
		  	 List<StudentOfSession>  list= (List<StudentOfSession> )session.getAttribute(RestConstants.Session_StudentslistOfParent);
		  	 if(list==null){
		  		putSession(session,SessionListener.getUserInfoBySession(request),request);
		  	 }
		  	list= (List<StudentOfSession> )session.getAttribute(RestConstants.Session_StudentslistOfParent);
		  	 return list;
		  }
	  
	  
	/**
	 * 重要的操作记录到 日志.
	 * 
	 * @param doorRecord
	 * @throws Exception
	 */
	public void addStudentOperate(String groupuuid,String studentuuid,String message, String note, HttpServletRequest request) {
		try {
			Operate operate = new Operate();
			operate.setCreate_time(TimeUtils.getCurrentTimestamp());

			
			operate.setGroupuuid(groupuuid);
			operate.setStudentuuid(studentuuid);
			SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
			if (user != null) {
				operate.setCreate_user(user.getName());
				operate.setCreate_useruuid(user.getUuid());
			}
			operate.setMessage(message);
			operate.setNote(note);
			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().save(operate);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String getGroupLink_tel(String groupuuid){
		String hql="select link_tel from Group4Q where  uuid ='"+DbUtils.safeToWhereString(groupuuid)+"'";
		List list=this.nSimpleHibernateDao.getHibernateTemplate().find(hql);
		if(list.size()>0){
			return (String)list.get(0);
		}
		return "";
	}
  
  /**
	 * 查询回复列表分页
	 * 
	 * @return
	 */
	@Deprecated
	public PageQueryResult getReplyPageList(String newsuuid) {
		if (StringUtils.isBlank(newsuuid)) {
			return new PageQueryResult();
		}
		
		PaginationData pData=new PaginationData();
		pData.setPageSize(5);
		String hql="from ClassNewsReply where  status ="+SystemConstants.Check_status_fabu +" and  newsuuid='"+DbUtils.safeToWhereString(newsuuid)+"'";
		pData.setOrderFiled("create_time");
		pData.setOrderType("desc");
		
		PageQueryResult pageQueryResult= this.nSimpleHibernateDao.findByPaginationToHqlNoTotal(hql, pData);
		List<ClassNewsReply> list=pageQueryResult.getData();
		
		for(ClassNewsReply o:list){
			this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
			o.setContent(o.getContent());
			UserCache userCache=UserRedisCache.getUserCache(o.getCreate_useruuid());
			if(userCache!=null){
				o.setCreate_user(userCache.getN());
				o.setCreate_img(userCache.getI());
				o.setCreate_img(PxStringUtil.imgSmallUrlByUuid(o.getCreate_img()));
				
			}
			
			
		}
		return pageQueryResult;
				
	}
	
	static final String  SelectReplySql=" SELECT t1.uuid,t1.newsuuid,t1.content,t1.create_time,t1.create_useruuid,t1.status ";
	  /**
		 * 查询回复列表分页
		 * 
		 * @return
		 */
		public PageQueryResult getReplyPageList(String newsuuid,SessionUserInfoInterface parent) {
			if (StringUtils.isBlank(newsuuid)) {
				return new PageQueryResult();
			}
			String cur_user_uuid="";
			if(parent!=null)cur_user_uuid=parent.getUuid();
			
			PaginationData pData=new PaginationData();
			pData.setPageSize(5);
			String hql=SelectReplySql+" from px_classnewsreply t1  where  ( create_useruuid='"+cur_user_uuid+"' or status ="+SystemConstants.Check_status_fabu+") and  newsuuid='"+DbUtils.safeToWhereString(newsuuid)+"'";
			pData.setOrderFiled("create_time");
			pData.setOrderType("desc");
			
			PageQueryResult pageQueryResult= this.nSimpleHibernateDao.findMapByPageForSqlNoTotal(hql, pData);
			List<Map> list=pageQueryResult.getData();
			
			UserRedisCache.warpListMapByUserCache(list, "create_useruuid", "create_user", "create_img");
		
			return pageQueryResult;
					
		}
	

	  /**
		 * 查询回复列表分页(每个回复带点赞数据)
		 * 
		 * @return
	 * @throws Exception 
		 */
		public PageQueryResult getReplyPageListAndRelyDianzan(String newsuuid,String cur_user_uuid) throws Exception {
			if (StringUtils.isBlank(newsuuid)) {
				return new PageQueryResult();
			}
			
			PaginationData pData=new PaginationData();
			pData.setPageSize(5);
			String hql=SelectReplySql+" from px_classnewsreply t1  where  ( create_useruuid='"+cur_user_uuid+"' or status ="+SystemConstants.Check_status_fabu+") and  newsuuid='"+DbUtils.safeToWhereString(newsuuid)+"'";
			pData.setOrderFiled("create_time");
			pData.setOrderType("desc");
			
			PageQueryResult pageQueryResult= this.nSimpleHibernateDao.findMapByPageForSqlNoTotal(hql, pData);
			List<Map> list=pageQueryResult.getData();
			
			UserRedisCache.warpListMapByUserCache(list, "create_useruuid", "create_user", "create_img");
			
			for(Map o:list){
				try {
					o.put("dianzan",(this.getDianzanDianzanListVO((String)o.get("uuid"), cur_user_uuid)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return pageQueryResult;
					
		}
  
  /**
	 * 获取点赞列表信息
	 * @param classNewsDianzanJsonform
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public DianzanListVO getDianzanDianzanListVO(String newsuuid,String cur_user_uuid) throws Exception {
		if (StringUtils.isBlank(newsuuid)) {
			return null;
		}
		DianzanListVO vo=new DianzanListVO();
		 List list=this.nSimpleHibernateDao.getHibernateTemplate().find(
				"select create_user from ClassNewsDianzanOfShow where newsuuid=?", newsuuid);
		
		
		Boolean canDianzan=true;
		if(list.size()>0&&StringUtils.isNotBlank(cur_user_uuid)){
			canDianzan=this.canDianzan(newsuuid,cur_user_uuid);
		}
		vo.setCanDianzan(canDianzan);
		vo.setNames(StringUtils.join(list,","));
		vo.setCount( list.size());
		return vo;
	}
	
	/**
	 * 判断是否能点赞
	 * @param classNewsDianzanJsonform
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public boolean canDianzan(String newsuuid,String create_useruuid) throws Exception {

		List list = this.nSimpleHibernateDao.getHibernateTemplate().find(
				"select newsuuid from ClassNewsDianzan where newsuuid=? and create_useruuid=?",
				newsuuid,create_useruuid);
		if (list != null && list.size() > 0) {
			return false;
		}
		return true;
	}
	

	/**
	 * 是否可以收藏
	 * 
	 * @param loginname
	 * @return
	 */
	public boolean isFavorites(String user_uuid,String reluuid) {
		if(StringUtils.isBlank(reluuid)||StringUtils.isBlank(user_uuid))return false;
		List list = nSimpleHibernateDao.getHibernateTemplate().find("select reluuid from Favorites where reluuid=? and user_uuid=?", reluuid,user_uuid);

		if (list != null&&list.size()>0)// 已被占用
			return false;
		else
			return true;

	}
	
	/**
	 * 
	 * @param s 检验的字符串
	 * @param length 允许的最大长度
	 * @param columnname 错误时提示的字段名
	 * @return
	 */
	protected boolean validateRequireAndLengthByRegJsonform(Object s,int length,String columnname,ResponseMessage responseMessage){
		if(s==null)
			return true;
		
		if (StringUtils.isBlank(s.toString()) || s.toString().length() > length) {
			responseMessage.setMessage(columnname+"不能为空！且长度不能超过"+length+"位！");
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param s 检验的字符串
	 * @param length 允许的最大长度
	 * @param columnname 错误时提示的字段名
	 * @return
	 */
	protected boolean validateRequireByRegJsonform(Object s,String columnname,ResponseMessage responseMessage){
		if(s==null)
			return true;
		
		if (StringUtils.isBlank(s.toString())) {
			responseMessage.setMessage(columnname+"不能为空！");
			return true;
		}
		return false;
	}

	

}
