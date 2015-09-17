package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.entity.PushMsgDevice;
import com.company.news.jsonform.PushMsgDeviceJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class PushMsgDeviceService extends AbstractServcice {

	/**
	 * 增加
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean save(PushMsgDeviceJsonform jsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(jsonform.getUser_uuid())) {
			responseMessage.setMessage("User_uuid");
			return false;
		}
		String group_uuids=jsonform.getGroup_uuid();
		if(group_uuids==null)group_uuids="";
		String[] group_uuidsArr= group_uuids.split(",");
		for(String o :group_uuidsArr){
			String hql = "from PushMsgDevice where device_type='" + jsonform.getDevice_type()+"'";
			hql += " and type="+jsonform.getType() ;
			hql += " and device_id='"+jsonform.getDevice_id()+"'";
			hql += " and group_uuid='"+o+"'";
			
			List  list= this.nSimpleHibernateDao.getHibernateTemplate().find(hql);
			PushMsgDevice message=null;
			if(list.size()==0){
				message = new PushMsgDevice();
			}else{
				message=(PushMsgDevice)list.get(0);
				jsonform.setUuid(message.getUuid());
			}
			BeanUtils.copyProperties(message, jsonform);
			message.setGroup_uuid(o);
			message.setUpdate_time(TimeUtils.getCurrentTimestamp());
			// 有事务管理，统一在Controller调用时处理异常
			this.nSimpleHibernateDao.getHibernateTemplate().save(message);
		}
		

		return true;
	}

	
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
