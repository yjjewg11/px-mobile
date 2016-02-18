package com.company.news.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.MyUbbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.core.iservice.PushMsgIservice;
import com.company.news.entity.ClassNews;
import com.company.news.entity.ClassNewsReply;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ClassNewsReplyJsonform;
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
public class ClassNewsReplyService extends AbstractService {
	public static final int USER_type_default = 1;// 0:老师
	
	@Autowired
	public PushMsgIservice pushMsgIservice;
	
	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(SessionUserInfoInterface user,ClassNewsReplyJsonform classNewsReplyJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(classNewsReplyJsonform.getContent())) {
			responseMessage.setMessage("内容不能为空！");
			return false;
		}

		if (StringUtils.isBlank(classNewsReplyJsonform.getNewsuuid())) {
			responseMessage.setMessage("Newsuuid不能为空！");
			return false;
		}

		ClassNewsReply cn=new ClassNewsReply();

		BeanUtils.copyProperties(cn, classNewsReplyJsonform);

		cn.setCreate_time(TimeUtils.getCurrentTimestamp());
//        cn.setUpdate_time(TimeUtils.getCurrentTimestamp());
        cn.setStatus(SystemConstants.Check_status_fabu);
        
        PxStringUtil.addCreateUser(user, cn);
        
        cn.setUsertype(USER_type_default);
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(cn);

		
		if(cn.getType()!=null){
			if(SystemConstants.common_type_hudong==cn.getType().intValue()){
				pushMsgIservice.pushMsg_replay_to_classNews_to_teacherOrParent(cn.getNewsuuid(), user.getName()+":"+cn.getContent());
			}
		}
		
		return true;
	}

	/**
	 * 更新班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean update(SessionUserInfoInterface user,ClassNewsReplyJsonform classNewsReplyJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(classNewsReplyJsonform.getContent())) {
			responseMessage.setMessage("content不能为空！");
			return false;
		}
		
		ClassNewsReply cn=(ClassNewsReply) this.nSimpleHibernateDao.getObjectById(ClassNewsReply.class, classNewsReplyJsonform.getUuid());
		
		if(cn!=null){
			cn.setContent(classNewsReplyJsonform.getContent());
//			cn.setUpdate_time(TimeUtils.getCurrentTimestamp());

			this.nSimpleHibernateDao.getHibernateTemplate().update(cn);
		}else{
			responseMessage.setMessage("对象不存在");
			return true;
		}

		

		return true;
	}

	
	
	
	static final String  SelectSql=" SELECT t1.uuid,t1.newsuuid,t1.content,t1.create_time,t1.create_useruuid,t1.status ";
	/**
	 * 查询所有班级
	 * 
	 * @return
	 */
	public PageQueryResult query(String newsuuid, PaginationData pData,String cur_user_uuid) {
		
		String sql = SelectSql+"from px_classnewsreply t1 where ( create_useruuid='"+cur_user_uuid+"' or status ="+SystemConstants.Check_status_fabu+")" ;	
		if (StringUtils.isNotBlank(newsuuid))
			sql+=" and  newsuuid='"+DBUtil.safeToWhereString(newsuuid)+"'";
		
		pData.setOrderFiled("create_time");
		pData.setOrderType("desc");
		
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findMapByPageForSqlNoTotal(sql, pData);
//		List<ClassNewsReply> list = pageQueryResult.getData();
		this.warpMapList(pageQueryResult.getData(), cur_user_uuid);
		
		return pageQueryResult;
				
	}
	
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List warpMapList(List<Map> list,String cur_user_uuid ) {
		if(list.size()==0)return list;
		//从缓存中获取用户资料,包装用户名和头像.
		UserRedisCache.warpListMapByUserCache(list, "create_useruuid", "create_user", "create_img");
		//t1.create_useruuid,t1.create_user,t1.create_img
		for(Map o:list){
			warpMap(o,cur_user_uuid);
		}
		return list;
	}
	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	private Map warpMap(Map o,String cur_user_uuid) {
		try {
			o.put("dianzan",(this.getDianzanDianzanListVO((String)o.get("uuid"), cur_user_uuid)));
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
	private ClassNewsReply warpVo(ClassNewsReply o,String cur_user_uuid){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		try {
			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(), cur_user_uuid));
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
	public List<ClassNewsReply> warpVoList(List<ClassNewsReply> list,String cur_user_uuid){
		for(ClassNewsReply o:list){
			warpVo(o,cur_user_uuid);
		}
		return list;
	}

	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean delete(SessionUserInfoInterface parent,String uuid, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(uuid)) {

			responseMessage.setMessage("ID不能为空！");
			return false;
		}
		ClassNewsReply obj=(ClassNewsReply)this.nSimpleHibernateDao.getObject(ClassNewsReply.class, uuid);
		if(obj==null){
			responseMessage.setMessage("对象不存在！");
			return false;
		}
		if(!parent.getUuid().equals(obj.getCreate_useruuid())){
			responseMessage.setMessage("无权删除!");
			return false;
		}
		this.nSimpleHibernateDao.delete(obj);

		return true;
	}
	
	/**
	 * 获取点赞列表信息
	 * 
	 * @param classNewsDianzanJsonform
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public Map<String,DianzanListVO> getDianzanDianzanMap(String reluuids, SessionUserInfoInterface user) throws Exception {
		Map<String,DianzanListVO> returnmap =new HashMap();
		if (StringUtils.isBlank(reluuids)) {
			return returnmap;
		}
		String useruuid="";
		
		if(user!=null)useruuid=user.getUuid();
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql="select t1.newsuuid  ,group_concat( t1.create_user) as user_names,count(1) as allcount,sum(case t1.create_useruuid when '"+DBUtil.safeToWhereString(useruuid)+"' then 1 else 0 end) as curuser_sum  from px_classnewsdianzan  t1 ";
		sql+=" where t1.newsuuid in("+DBUtil.stringsToWhereInValue(reluuids)+")";
		sql+=" GROUP BY t1.newsuuid  ";
		Query q = s.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> list=q.list();
		
		for(Map map:list){
			DianzanListVO vo = new DianzanListVO();
			
			//统计当前用户点赞数量,0表示没点赞,可以点赞.
			vo.setCanDianzan("0".equals(map.get("curuser_sum")+""));
			vo.setCount(Integer.valueOf(map.get("allcount")+""));
			vo.setNames(map.get("user_names")+"");
			
			returnmap.put(map.get("newsuuid")+"", vo);
			
		}
		return returnmap;
	}
	
	public ClassNewsReply get(String uuid) throws Exception {
		return (ClassNewsReply) this.nSimpleHibernateDao.getObjectById(
				ClassNewsReply.class, uuid);	
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return ClassNews.class;
	}

}
