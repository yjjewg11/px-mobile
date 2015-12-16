package com.company.news.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.MyUbbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.AbstractClass;
import com.company.news.entity.ClassNews;
import com.company.news.entity.PClass;
import com.company.news.entity.PxClass;
import com.company.news.entity.StudentContactRealation;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ClassNewsJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.DianzanListVO;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class ClassNewsService extends AbstractService {
	public static final int USER_type_default = 1;// 0:老师
	@Autowired
	private CountService countService;

	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(SessionUserInfoInterface user,ClassNewsJsonform classNewsJsonform,
			ResponseMessage responseMessage) throws Exception {
//		if (StringUtils.isBlank(classNewsJsonform.getTitle())
//				|| classNewsJsonform.getTitle().length() > 128) {
//			responseMessage.setMessage("班级名不能为空！，且长度不能超过45位！");
//			return false;
//		}

		if (StringUtils.isBlank(classNewsJsonform.getClassuuid())) {
			responseMessage.setMessage("classuuid不能为空！");
			return false;
		}
		
		AbstractClass pClass=(PClass)this.nSimpleHibernateDao.getObject(PClass.class, classNewsJsonform.getClassuuid());
		//兼容培训机构
		if(pClass==null){
			 pClass=(AbstractClass)this.nSimpleHibernateDao.getObject(PxClass.class, classNewsJsonform.getClassuuid());
		}
		
		if(pClass==null){
			responseMessage.setMessage("选择的班级不存在");
			return false;
		}
		
		
		StudentContactRealation studentContactRealation=this.getStudentContactRealationBy(user.getUuid(), classNewsJsonform.getClassuuid());
		
		ClassNews cn = new ClassNews();

		BeanUtils.copyProperties(cn, classNewsJsonform);
		
		cn.setGroupuuid(pClass.getGroupuuid());
		
		cn.setGroup_name(nSimpleHibernateDao.getGroupName(pClass.getGroupuuid()));
		cn.setClass_name(pClass.getName());
		
		cn.setCreate_time(TimeUtils.getCurrentTimestamp());
		cn.setUsertype(USER_type_default);
		cn.setStatus(SystemConstants.Check_status_fabu);
		cn.setUrl(StringUtils.trim(cn.getUrl()));
		cn.setIllegal(0l);
		PxStringUtil.addCreateUser(user, cn);
		
		if (studentContactRealation!=null) {
			//班级互动用孩子的信息发布.
			cn.setCreate_user(PxStringUtil.getParentNameByStudentContactRealation(studentContactRealation));
			cn.setCreate_img(studentContactRealation.getStudent_img());
		}
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(cn);
		//初始话计数
		countService.add(cn.getUuid(), SystemConstants.common_type_hudong);
		return true;
	}
	
	public StudentContactRealation getStudentContactRealationBy(String parent_uuid,String class_uuid){
		List<StudentContactRealation> list=(List<StudentContactRealation>)this.nSimpleHibernateDao.getHibernateTemplate().find("from StudentContactRealation where parent_uuid=? and class_uuid=?", parent_uuid,class_uuid);

		if(list.size()==0){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 更新班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean update(SessionUserInfoInterface user,ClassNewsJsonform classNewsJsonform,
			ResponseMessage responseMessage) throws Exception {
//		if (StringUtils.isBlank(classNewsJsonform.getTitle())
//				|| classNewsJsonform.getTitle().length() > 128) {
//			responseMessage.setMessage("班级名不能为空！，且长度不能超过45位！");
//			return false;
//		}

		ClassNews cn = (ClassNews) this.nSimpleHibernateDao.getObjectById(
				ClassNews.class, classNewsJsonform.getUuid());

		if (cn != null) {
			cn.setImgs(classNewsJsonform.getImgs());
			cn.setContent(classNewsJsonform.getContent());
//			cn.setTitle(classNewsJsonform.getTitle());
//			cn.setUpdate_time(TimeUtils.getCurrentTimestamp());

			this.nSimpleHibernateDao.getHibernateTemplate().update(cn);
		} else {
			responseMessage.setMessage("对象不存在");
			return true;
		}

		return true;
	}

	/**
	 * 查询所有班级
	 * 
	 * @return
	 * @throws Exception 
	 */
	public PageQueryResult query(SessionUserInfoInterface user ,String classuuid, PaginationData pData) throws Exception {
		
		//修复班级互动,头像没显示bug.
		Session session=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		String sql=" SELECT t1.uuid,t1.classuuid,t1.create_user,t1.create_useruuid,t1.create_img,t1.create_time,t1.title,t1.content,t1.imgs,t1.groupuuid,t1.illegal,t1.illegal_time,t1.reply_time,t1.status,t1.update_time,t1.usertype,t1.group_name,t1.class_name,t1.url";
		sql+=" FROM px_classnews t1 ";
		sql+=" where t1.status=0  ";	
		if (StringUtils.isNotBlank(classuuid))
			sql += " and  t1.classuuid in("+DBUtil.stringsToWhereInValue(classuuid)+")";
		else  {
			sql += " and  t1.groupuuid not in ('group_wj1','group_wj2','group_px1')";
		}
		
	    sql += " order by t1.create_time desc";
		Query  query =session.createSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForSqlNoTotal(query, pData);
		List<Map> list=pageQueryResult.getData();
		this.warpMapList(list, user);
//		
//		String hql = "from ClassNews where status=0 ";
//		if (StringUtils.isNotEmpty(classuuid)){
//			hql += " and  classuuid in("+DBUtil.stringsToWhereInValue(classuuid)+")";
//		}else{
//			hql += " and  groupuuid not in ('group_wj1','group_wj2','group_px1')";
//		}
//	
//		pData.setOrderFiled("create_time");
//		pData.setOrderType("desc");
//		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
//				.findByPaginationToHqlNoTotal(hql, pData);
//		this.warpVoList(pageQueryResult.getData(), user.getUuid());
		
		return pageQueryResult;

	}

//	private List warpMapList(List<Map> list, String cur_user_uuid) {
//		for(Map o:list){
//			warpMap(o,cur_user_uuid);
//		}
//		return list;
//		
//	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List warpMapList(List<Map> list,SessionUserInfoInterface user ) {
		
		String uuids="";
		for(Map o:list){
			warpMap(o,user);
			uuids+=o.get("uuid")+",";
		}
		
		try {
			countService.getIncrCountByExt_uuids(uuids);
			Map dianZanMap=classNewsReplyService.getDianzanDianzanMap(uuids, user);
			for(Map o:list){
//				o.put("count", countMap.get(o.get("uuid")));
				Object vo= (Object)dianZanMap.get(o.get("uuid"));
				if(vo==null)vo= new DianzanListVO();
				o.put("dianzan",vo);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}

	@Autowired
	private ClassNewsReplyService classNewsReplyService;
	
	private void warpMap(Map o, SessionUserInfoInterface user) {
		try {
			//网页版本需要转为html显示.
			//o.put("content", MyUbbUtils.myUbbTohtml((String)o.get("content")));
			o.put("imgsList", PxStringUtil.uuids_to_imgMiddleurlList((String)o.get("imgs")));
			o.put("share_url", PxStringUtil.getClassNewsByUuid((String)o.get("uuid")));
			//o.put("dianzan", this.getDianzanDianzanListVO((String)o.get("uuid"),cur_user_uuid));
			o.put("replyPage", this.getReplyPageList((String)o.get("uuid")));
			o.put("create_img", PxStringUtil.imgSmallUrlByUuid((String)o.get("create_img")));
			//显示姓名和学校
			if(o.get("group_name")!=null)
				o.put("create_user", o.get("create_user")+"|"+o.get("group_name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
//		
//		o.setImgsList(PxStringUtil.uuids_to_imgMiddleurlList(o.getImgs()));
//		o.setShare_url(PxStringUtil.getClassNewsByUuid(o.getUuid()));
//		try {
//			o.setCount(countService.count(o.getUuid(), SystemConstants.common_type_hudong));
//			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(), cur_user_uuid));
//			o.setReplyPage(this.getReplyPageList(o.getUuid()));
//			o.setCreate_img(PxStringUtil.imgSmallUrlByUuid(o.getCreate_img()));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return o;
		
	}
	
	
	/**
	 * 查询所有班级
	 * 
	 * @return
	 * @throws Exception 
	 */
	@Deprecated
	public List<Map> queryMyClassForAdd(SessionUserInfoInterface user ,String courseuuid,String groupuuid, PaginationData pData) throws Exception {
		Session session=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		String sql="SELECT t1.uuid,t1.name from px_class t1"
				+" inner join px_student t2 on t2.class_uuid=t1.uuid  " 
				+" inner join px_studentcontactrealation t3 on t3.student_uuid=t2.uuid  " 
				+"   where parent_uuid='"
				+ DbUtils.safeToWhereString(user.getUuid()) + "' ";
		String sqlpxclass = "select t1.uuid,t1.name"
				+ " from px_pxstudentpxclassrelation t0 "
				+ " inner join px_pxclass t1 on t0.class_uuid=t1.uuid "
				+ " inner join px_pxstudent t4 on t0.student_uuid=t4.uuid  "
				+ " where t0.student_uuid  in( "
				+ " select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"
				+ DbUtils.safeToWhereString(user.getUuid()) + "' )"
				+" and t1.isdisable ="+SystemConstants.Class_isdisable_0;
	    
		Query  query =session.createSQLQuery(sql+" UNION "+sqlpxclass);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		

		return query.list();

	}
	
	/**
	 * 查询所有班级
	 * 
	 * @return
	 * @throws Exception 
	 */
	public PageQueryResult queryPxClassNewsBy(SessionUserInfoInterface user ,String courseuuid,String groupuuid, PaginationData pData) throws Exception {
		
		
		Session session=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		String sql=" SELECT t1.uuid,t1.classuuid,t1.create_user,t1.create_useruuid,t1.create_img,t1.create_time,t1.title,t1.content,t1.imgs,t1.groupuuid,t1.illegal,t1.illegal_time,t1.reply_time,t1.status,t1.update_time,t1.usertype,t1.group_name,t1.class_name,t1.url";
		sql+=" FROM px_classnews t1 ";
		
		if (StringUtils.isNotBlank(courseuuid)) {
			sql += " LEFT JOIN  px_pxclass t2 on  t1.classuuid=t2.uuid ";
			sql += " where  t1.status=0 and t2.courseuuid ='"+DbUtils.safeToWhereString(courseuuid)+"'";
		}else if (StringUtils.isNotBlank(groupuuid)) {
			sql += " where    t1.status=0 and t1.groupuuid ='"+DbUtils.safeToWhereString(groupuuid)+"'";
		}
	    sql += " order by t1.create_time desc";
	    
	    
		Query  query =session.createSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForSqlNoTotal(query, pData);
		List<Map> list=pageQueryResult.getData();
		this.warpMapList(list, user);
//		
//		
//		Session s = this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
//		String sql = "select DISTINCT {t1.*} from px_classnews  {t1} ";
//		if (StringUtils.isNotBlank(courseuuid)) {
//			sql += " LEFT JOIN  px_pxclass t2 on  {t1}.classuuid=t2.uuid ";
//			sql += " where t2.courseuuid ='"+courseuuid+"'";
//		}else if (StringUtils.isNotBlank(groupuuid)) {
//			sql += " where  {t1}.groupuuid ='"+groupuuid+"'";
//		}
//		sql += "order by {t1}.create_time desc";
//		
//		SQLQuery q = s.createSQLQuery(sql).addEntity("t1", ClassNews.class);
//
//		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForSqlNoTotal(q, pData);
//		
//		this.warpVoList(pageQueryResult.getData(), user.getUuid());
//		
		return pageQueryResult;

	}

	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean delete(String uuid, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(uuid)) {

			responseMessage.setMessage("ID不能为空！");
			return false;
		}

		if (uuid.indexOf(",") != -1)// 多ID
		{
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from ClassNews where uuid in(?)", uuid);
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from ClassNewsReply where newsuuid in(?)", uuid);
		} else {
			this.nSimpleHibernateDao.deleteObjectById(ClassNews.class, uuid);
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from ClassNewsReply where newsuuid =?", uuid);
		}

		return true;
	}

	public ClassNews get(SessionUserInfoInterface user ,String uuid) throws Exception {
		ClassNews cn = (ClassNews) this.nSimpleHibernateDao.getObjectById(
				ClassNews.class, uuid);
		this.warpVo(cn, user.getUuid());
		return cn;

	}



	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return ClassNews.class;
	}
	

	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private ClassNews warpVo(ClassNews o,String cur_user_uuid){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		
		o.setImgsList(PxStringUtil.uuids_to_imgMiddleurlList(o.getImgs()));
		o.setShare_url(PxStringUtil.getClassNewsByUuid(o.getUuid()));
		try {
			o.setCount(countService.count(o.getUuid(), SystemConstants.common_type_hudong));
			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(), cur_user_uuid));
			o.setReplyPage(this.getReplyPageList(o.getUuid()));
			o.setCreate_img(PxStringUtil.imgSmallUrlByUuid(o.getCreate_img()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List<ClassNews> warpVoList(List<ClassNews> list,String cur_user_uuid){
		for(ClassNews o:list){
			warpVo(o,cur_user_uuid);
		}
		return list;
	}

}
