package com.company.news.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.cache.UserCache;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.core.iservice.PushMsgIservice;
import com.company.news.entity.AbstractBaseReply;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.BaseDianzanJsonform;
import com.company.news.query.PaginationData;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public  class BaseDianzanService extends AbstractService {
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
	public boolean add(SessionUserInfoInterface user,BaseDianzanJsonform baseReplyJsonform,
			ResponseMessage responseMessage) throws Exception {

		if (baseReplyJsonform.getType()==null) {
			responseMessage.setMessage("type不能为空！");
			return false;
		}

		if (StringUtils.isBlank(baseReplyJsonform.getRel_uuid())) {
			responseMessage.setMessage("rel_uuid不能为空！");
			return false;
		}
		
		String insertsql="insert into "+this.getTableNameByType(baseReplyJsonform.getType())+"(rel_uuid,create_useruuid,create_time) values('"+baseReplyJsonform.getRel_uuid()+"','"+user.getUuid()+"',now())";
		try {
			this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(insertsql).executeUpdate();
		} catch (org.hibernate.exception.ConstraintViolationException e) {
			responseMessage.setMessage("你已过投票!");
			return false;
		}
		return true;
	}


	/**
	 * 查询
	 * 
	 * @return
	 */
	public Map query(String rel_uuid,Integer type,String cur_user_uuid) {
		if(cur_user_uuid==null)cur_user_uuid="0";
		//String sql="select count(1),sum(case when create_useruuid ='1' then 1 else 0 end)  from px_base_dianzan_21 where rel_uuid='1'";
		String sql="select count(1) as dianzan_count,sum(case when create_useruuid ='"+cur_user_uuid+"' then 1 else 0 end)  as yidianzan   from "+this.getTableNameByType(type)+" where rel_uuid='"+rel_uuid+"'";
		List<Map> list=this.nSimpleHibernateDao.queryMapBySql(sql);
		if(list.size()>0){
			return list.get(0);
		}
		Map map=new HashMap();
		map.put("dianzan_count", 0);
		map.put("yidianzan", 0);
		return map;
				
	}

	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public List<AbstractBaseReply> warpVoList(List<AbstractBaseReply> list,String cur_user_uuid){
//		for(ClassNewsReply o:list){
//			warpVo(o,cur_user_uuid);
//		}
		
		
		//批量获取redis中用户
		String[] userUuids=new String[list.size()];
		for(int i=0,len=list.size();i<len;i++){
			AbstractBaseReply o=list.get(i);
			String useruuid=o.getCreate_useruuid();
			if(StringUtils.isBlank(useruuid)){
				useruuid="-1";
			}
			userUuids[i]=useruuid;
		}
		
		
		Map<String,UserCache> userMap=UserRedisCache.getUserCache(userUuids);
		
		for(AbstractBaseReply o:list){
			String useruuid=o.getCreate_useruuid();
			if(StringUtils.isBlank(useruuid)){
				continue;
			}
			
			UserCache userCahce=userMap.get(useruuid);
			if(userCahce==null){
				logger.error("redis userCahce is null,uuid="+useruuid);
				continue;
			}
			o.setCreate_user(userCahce.getN());
			o.setCreate_img(PxStringUtil.imgSmallUrlByUuid(userCahce.getI()));
			
		}
		return list;
	}

	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean delete(SessionUserInfoInterface parent,String rel_uuid,Integer type, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(rel_uuid)) {

			responseMessage.setMessage("ID不能为空！");
			return false;
		}
		
		//rel_uuid,create_useruuid
		String insertsql="delete from "+this.getTableNameByType(type)+" where rel_uuid='"+rel_uuid+"' and create_useruuid='"+parent.getUuid()+"'";
		this.nSimpleHibernateDao.createSqlQuery(insertsql).executeUpdate();

		return true;
	}
//	/**
//	 * 获取点赞列表信息
//	 * 
//	 * @param classNewsDianzanJsonform
//	 * @param responseMessage
//	 * @return
//	 * @throws Exception
//	 */
//	public Map<String,DianzanListVO> getDianzanDianzanMap(String reluuids, SessionUserInfoInterface user) throws Exception {
//		Map<String,DianzanListVO> returnmap =new HashMap();
//		if (StringUtils.isBlank(reluuids)) {
//			return returnmap;
//		}
//		String useruuid="";
//		
//		if(user!=null)useruuid=user.getUuid();
//		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
//				.getSessionFactory().openSession();
//		String sql="select t1.newsuuid  ,group_concat( t1.create_user) as user_names,count(1) as allcount,sum(case t1.create_useruuid when '"+DBUtil.safeToWhereString(useruuid)+"' then 1 else 0 end) as curuser_sum  from px_classnewsdianzan  t1 ";
//		sql+=" where t1.newsuuid in("+DBUtil.stringsToWhereInValue(reluuids)+")";
//		sql+=" GROUP BY t1.newsuuid  ";
//		Query q = s.createSQLQuery(sql);
//		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
//		List<Map> list=q.list();
//		
//		for(Map map:list){
//			DianzanListVO vo = new DianzanListVO();
//			
//			//统计当前用户点赞数量,0表示没点赞,可以点赞.
//			vo.setCanDianzan("0".equals(map.get("curuser_sum")+""));
//			vo.setCount(Integer.valueOf(map.get("allcount")+""));
//			vo.setNames(map.get("user_names")+"");
//			
//			returnmap.put(map.get("newsuuid")+"", vo);
//			
//		}
//		return returnmap;
//	}
//	
//	public ClassNewsReply get(String uuid) throws Exception {
//		return (ClassNewsReply) this.nSimpleHibernateDao.getObjectById(
//				ClassNewsReply.class, uuid);	
//	}
	
	static private Map<Integer,String> tableNameMap=new HashMap();
	static{
		tableNameMap.put(SystemConstants.common_type_FPPhotoItem, "px_base_dianzan_21");
		tableNameMap.put(SystemConstants.common_type_FPMovie,"px_base_dianzan_22");
	}
	public String getTableNameByType(Integer type) {
		//insert into px_base_dianzan_21(rel_uuid,create_useruuid,create_time) 
		String tableName=tableNameMap.get(type);
		if(tableName==null){
			throw new RuntimeException("px_base_dianzan is null.type="+type);
		}
		return tableName;
	}
	@Override
	public Class getEntityClass() {
		
		// TODO Auto-generated method stub
		return null;
	}
	


}
