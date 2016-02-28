package com.company.news.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.mq.JobDetails;
import com.company.mq.MQUtils;
import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.cache.UserCache;
import com.company.news.cache.redis.UserRedisCache;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.core.iservice.PushMsgIservice;
import com.company.news.entity.Group;
import com.company.news.entity.Message;
import com.company.news.entity.User;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.MessageJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.TimeUtils;
import com.company.news.right.RightConstants;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class MessageService extends AbstractService {
	public static final int announcements_isread_yes = 1;// 已读
	public static final int announcements_isread_no = 0;// 未读
	public static final int announcements_isdelete_yes = 1;// 已读
	public static final int announcements_isdelete_no = 0;// 未读

	@Autowired
	public PushMsgIservice pushMsgIservice;

	/**
	 * 增加
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(MessageJsonform messageJsonform,
			ResponseMessage responseMessage,HttpServletRequest request) throws Exception {
	
		if (StringUtils.isBlank(messageJsonform.getMessage())) {
			responseMessage.setMessage("内容不能为空!");
			return false;
		}

		if (StringUtils.isBlank(messageJsonform.getRevice_useruuid())) {
			responseMessage.setMessage("Revice_useruuid不能为空！");
			return false;
		}

		if(SystemConstants.Message_type_2.equals(messageJsonform.getType())){
			Group user = (Group) CommonsCache.get(messageJsonform.getRevice_useruuid(),Group.class);
			if (user == null) {
				responseMessage.setMessage("无效数据幼儿园不存在！Revice_useruuid="+messageJsonform.getRevice_useruuid());
				return false;
			}
			messageJsonform.setRevice_user(user.getBrand_name()+"园长");
		}else{
			
			UserCache user=UserRedisCache.getUserCache(messageJsonform.getRevice_useruuid());
//			User user = (User) CommonsCache.get(messageJsonform.getRevice_useruuid(),User.class);
			if (user == null) {
				responseMessage.setMessage("无效数据老师不存在！Revice_useruuid="+messageJsonform.getRevice_useruuid());
				return false;
			}
			messageJsonform.setRevice_user(user.getN());
		}
		Message message = new Message();
		BeanUtils.copyProperties(message, messageJsonform);
		message.setCreate_time(TimeUtils.getCurrentTimestamp());
		message.setIsread(announcements_isread_no);
		message.setIsdelete(announcements_isdelete_no);

		
		
		SessionUserInfoInterface user=SessionListener.getUserInfoBySession(request);
		String msg=user.getName()+"说:"+message.getMessage();
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(message);
		if(!SystemConstants.Message_type_2.equals(messageJsonform.getType())){
			pushMsgIservice.pushMsg_to_teacher(SystemConstants.common_type_messageTeaher, message.getSend_useruuid(), message.getRevice_useruuid(),msg);
		}else{
			Map map=new HashMap();
	    	map.put("uuid", message.getUuid());
	    	map.put("groupuuid",messageJsonform.getRevice_useruuid());
	    	map.put("title",msg);
			
	    	JobDetails job=new JobDetails("doJobMqIservice","sendPushMessageKD",map);
			MQUtils.publish(job);
			
		}
		return true;
	}
	
	static final String  SelectSql=" SELECT t1.uuid,t1.create_time,t1.send_useruuid,t1.title,t1.type,t1.isread,t1.message,t1.group_uuid,t1.revice_useruuid,t1.revice_user,t1.send_user,t1.send_userimg ";

	/**
	 * 查询所有通知
	 * 
	 * @return
	 */
	public PageQueryResult query(String type, String useruuid,PaginationData pData) {

		String hql = SelectSql+" from px_message t1 where isdelete=" + announcements_isdelete_no;
		if (StringUtils.isNotBlank(type))
			hql += " and type=" + type;
		if (StringUtils.isNotBlank(useruuid))
			hql += " and revice_useruuid='" + DbUtils.safeToWhereString(useruuid) + "'";
		hql += " order by create_time desc" ;
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findMapByPageForSqlNoTotal(hql, pData);
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
					"update Message set isdelete=? where uuid in(?)",
					announcements_isdelete_yes, uuid);
		} else {
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"update Message set isdelete=? where uuid =?",
					announcements_isdelete_yes, uuid);
		}

		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public Message get(String uuid) throws Exception {
		Message message = (Message) this.nSimpleHibernateDao.getObjectById(
				Message.class, uuid);

		return message;
	}

	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean read(String uuid, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(uuid)) {

			responseMessage.setMessage("ID不能为空！");
			return false;
		}

		if (uuid.indexOf(",") != -1)// 多ID
		{
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"update Message set isread=? where uuid in(?)",
					announcements_isread_yes, uuid);
		} else {
			this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"update Message set isread=? where uuid =?",
					announcements_isread_yes, uuid);
		}

		return true;
	}

	
	/**
	 * 查询我(家长)和老师所有信件
	 * 
	 * @return
	 */
	public PageQueryResult queryMessageByTeacher(String useruuid,String parentuuid,PaginationData pData) {
		
		
		useruuid=DbUtils.safeToWhereString(useruuid);
		parentuuid=DbUtils.safeToWhereString(parentuuid);
		String hql = SelectSql+" from px_message t1 where isdelete=" + announcements_isdelete_no;
		hql += " and type=1 " ;
		hql += " and (" ;
			hql += "  (revice_useruuid='" + useruuid + "' and send_useruuid='" + parentuuid + "')";//家长发给我的.
			hql += " or (send_useruuid='" + useruuid + "' and revice_useruuid='" + parentuuid + "')";//我发给家长的.
		hql += "  )" ;
		
		hql += " order by create_time desc" ;
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findMapByPageForSqlNoTotal(hql, pData);
		this.warpMapList(pageQueryResult.getData());
		return pageQueryResult;
	}
	/**
	 * 查询我(家长)和园长所有信件
	 * 
	 * @return
	 */
	public PageQueryResult queryByLeader(String useruuid,String parentuuid,PaginationData pData) {
		useruuid=DbUtils.safeToWhereString(useruuid);
		parentuuid=DbUtils.safeToWhereString(parentuuid);
		
		String hql = SelectSql+" from px_message t1 where isdelete=" + announcements_isdelete_no;
		hql += " and type=2 " ;
		hql += " and (" ;
			hql += "  (revice_useruuid='" + useruuid + "' and send_useruuid='" + parentuuid + "')";//家长发给我的.
			hql += " or (send_useruuid='" + useruuid + "' and revice_useruuid='" + parentuuid + "')";//我发给家长的.
		hql += "  )" ;
		hql += " order by create_time desc" ;
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findMapByPageForSqlNoTotal(hql, pData);
		this.warpMapList(pageQueryResult.getData());
		return pageQueryResult;
	}
	
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public List<Map> warpMapList(List<Map> list){
		if(list.size()>0){
			Map o=list.get(0);
			if(SystemConstants.Message_type_1.toString().equals(o.get("type")+"")){
				UserRedisCache.warpListMapByUserCache(list, "send_useruuid", "send_user", "send_userimg");
				UserRedisCache.warpListMapByUserCache(list, "revice_useruuid", "revice_user", null);
			}
		}
		for(Map o:list){
			warpMap(o);
		}
		return list;
	}
	
	
	private void warpMap(Map o) {
		o.put("send_userimg", PxStringUtil.imgSmallUrlByUuid((String)o.get("send_userimg")));
		
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private Message warpVo(Message o){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		o.setSend_userimg(PxStringUtil.imgSmallUrlByUuid(o.getSend_userimg()));
		return o;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List<Message> warpVoList(List<Message> list){
		for(Message o:list){
			warpVo(o);
		}
		return list;
	}
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
