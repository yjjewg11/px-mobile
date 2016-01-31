package com.company.news.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.cache.UserCache;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.MyUbbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.core.iservice.PushMsgIservice;
import com.company.news.entity.AbstractBaseReply;
import com.company.news.entity.BaseReply21;
import com.company.news.entity.BaseReply22;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.BaseReplyJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public  class BaseReplyService extends AbstractService {
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
	public boolean add(SessionUserInfoInterface user,BaseReplyJsonform baseReplyJsonform,
			ResponseMessage responseMessage) throws Exception {

		if (baseReplyJsonform.getType()==null) {
			responseMessage.setMessage("type不能为空！");
			return false;
		}
		if (StringUtils.isBlank(baseReplyJsonform.getContent())) {
			responseMessage.setMessage("内容不能为空！");
			return false;
		}

		if (StringUtils.isBlank(baseReplyJsonform.getRel_uuid())) {
			responseMessage.setMessage("rel_uuid不能为空！");
			return false;
		}
		AbstractBaseReply cn=(AbstractBaseReply)this.getEntityClassByType(baseReplyJsonform.getType()).newInstance();

		BeanUtils.copyProperties(cn, baseReplyJsonform);
		 cn.setStatus(SystemConstants.Check_status_fabu);
		 cn.setContent(MyUbbUtils.htmlToMyUbb(cn.getContent()));
		cn.setCreate_time(TimeUtils.getCurrentTimestamp());
        cn.setCreate_useruuid(user.getUuid());
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(cn);
//
//		
//		if(cn.getType()!=null){
//			if(SystemConstants.common_type_hudong==cn.getType().intValue()){
//				pushMsgIservice.pushMsg_replay_to_classNews_to_teacherOrParent(cn.getNewsuuid(), user.getName()+":"+cn.getContent());
//			}
//		}
		
		return true;
	}


	/**
	 * 查询
	 * 
	 * @return
	 */
	public PageQueryResult query(String rel_uuid,Integer type, PaginationData pData,String cur_user_uuid) {
		String hql="from "+this.getEntityClassByType(type).getName()+" where ( create_useruuid='"+cur_user_uuid+"' or status ="+SystemConstants.Check_status_fabu+")" ;	
			hql+=" and  rel_uuid='"+rel_uuid+"'";
		 if(StringUtils.isNotBlank(pData.getMaxTime())){
			 hql += " and   create_time <"+DBUtil.queryDateStringToDateByDBType(pData.getMaxTime());
		 }
		pData.setOrderFiled("create_time");
		pData.setOrderType("desc");
		
		PageQueryResult pageQueryResult= this.nSimpleHibernateDao.findByPaginationToHql(hql, pData);
		
		return pageQueryResult;
				
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
	public boolean delete(SessionUserInfoInterface parent,String uuid,Integer type, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(uuid)) {

			responseMessage.setMessage("ID不能为空！");
			return false;
		}
		AbstractBaseReply obj=(AbstractBaseReply)this.nSimpleHibernateDao.getObject(this.getEntityClassByType(type), uuid);
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
	
	static private Map<Integer,Class> tableNameMap=new HashMap();
	static{
		tableNameMap.put(SystemConstants.common_type_FPPhotoItem, BaseReply21.class);
		tableNameMap.put(SystemConstants.common_type_FPMovie, BaseReply22.class);
	}
	public Class getEntityClassByType(Integer type) {
		
		Class tableName=tableNameMap.get(type);
		if(tableName==null){
			throw new RuntimeException("BaseReply is null.type="+type);
		}
		return tableName;
	}
	@Override
	public Class getEntityClass() {
		
		// TODO Auto-generated method stub
		return null;
	}
	


}
