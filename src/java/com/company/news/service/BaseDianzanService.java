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
			responseMessage.setMessage("你已点赞!");
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
			Map map= list.get(0);
			if(map.get("yidianzan")==null){
				map.put("yidianzan", 0);
			}
			return map;
		}
		Map map=new HashMap();
		map.put("dianzan_count", 0);
		map.put("yidianzan", 0);
		return map;
				
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
		int count=this.nSimpleHibernateDao.createSqlQuery(insertsql).executeUpdate();
		
		
		
		if(count>0)
			return true;
		responseMessage.setMessage("无数据");
		return false;
	}
	
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
