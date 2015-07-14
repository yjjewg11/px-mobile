package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.entity.Group;
import com.company.news.entity.Group4Q;
import com.company.news.entity.User;
import com.company.news.entity.UserGroupRelation;
import com.company.news.jsonform.GroupRegJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class GroupService extends AbstractServcice {
	@Autowired
	private UserinfoService userinfoService;


	
	/**
	 * 增加机构
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(GroupRegJsonform groupRegJsonform,
			ResponseMessage responseMessage,String useruuid) throws Exception {
		if (StringUtils.isBlank(groupRegJsonform.getBrand_name())||groupRegJsonform.getBrand_name().length()>45) {
			responseMessage.setMessage("品牌名不能为空！，且长度不能超过45位！");
			return false;
		}
		
		if (StringUtils.isBlank(groupRegJsonform.getCompany_name())||groupRegJsonform.getCompany_name().length()>45) {
			responseMessage.setMessage("机构名不能为空！，且长度不能超过45位！");
			return false;
		}
		
		if (StringUtils.isBlank(groupRegJsonform.getMap_point())) {
			responseMessage.setMessage("位置信息不能为空！");
			return false;
		}
		
		if (StringUtils.isBlank(groupRegJsonform.getAddress())||groupRegJsonform.getAddress().length()>64) {
			responseMessage.setMessage("联系地址不能为空！，且长度不能超过64位！");
			return false;
		}
		
		if (StringUtils.isBlank(groupRegJsonform.getLink_tel())) {
			responseMessage.setMessage("联系方式不能为空！");
			return false;
		}
		
		if (groupRegJsonform.getType()==null) {
			groupRegJsonform.setType(SystemConstants.Group_type_1);//默认幼儿园
//			responseMessage.setMessage("机构类型不能为空！");
//			return false;
		}
		if(SystemConstants.Group_type_0.equals(groupRegJsonform.getType())){
			responseMessage.setMessage("非法数据,异常注册");
			return false;
		}
		
		// 机构名是否存在
		if (isExitSameUserByCompany_name(groupRegJsonform.getCompany_name(),null)) {
			responseMessage.setMessage("机构名已被注册！");
			return false;
		}
		
		Group group = new Group();

		BeanUtils.copyProperties(group, groupRegJsonform);

		group.setCreate_time(TimeUtils.getCurrentTimestamp());

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(group);
		
		//设置保存后的机构UUID
		groupRegJsonform.setGroup_uuid(group.getUuid());
		//保存用户机构关联表
		UserGroupRelation userGroupRelation=new UserGroupRelation();
		userGroupRelation.setUseruuid(useruuid);
		userGroupRelation.setGroupuuid(groupRegJsonform.getGroup_uuid());
		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(userGroupRelation);
		
		return true;
	}
	
	
	/**
	 * 增加机构
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean update(GroupRegJsonform groupRegJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(groupRegJsonform.getBrand_name())||groupRegJsonform.getBrand_name().length()>45) {
			responseMessage.setMessage("品牌名不能为空！，且长度不能超过45位！");
			return false;
		}
		
		if (StringUtils.isBlank(groupRegJsonform.getCompany_name())||groupRegJsonform.getCompany_name().length()>45) {
			responseMessage.setMessage("机构名不能为空！，且长度不能超过45位！");
			return false;
		}
		
		if (StringUtils.isBlank(groupRegJsonform.getMap_point())) {
			responseMessage.setMessage("位置信息不能为空！");
			return false;
		}
		
		if (StringUtils.isBlank(groupRegJsonform.getAddress())||groupRegJsonform.getAddress().length()>64) {
			responseMessage.setMessage("联系地址不能为空！，且长度不能超过64位！");
			return false;
		}
		
		if (StringUtils.isBlank(groupRegJsonform.getLink_tel())) {
			responseMessage.setMessage("联系方式不能为空！");
			return false;
		}
		
		if (groupRegJsonform.getType()==null) {
			responseMessage.setMessage("机构类型不能为空！");
			return false;
		}
		
		// 机构名是否存在
		if (isExitSameUserByCompany_name(groupRegJsonform.getCompany_name(),groupRegJsonform.getUuid())) {
			responseMessage.setMessage("机构名已被注册！");
			return false;
		}
			
		Group group = (Group) this.nSimpleHibernateDao.getObject(Group.class, groupRegJsonform.getUuid());
		if (group != null) {
			BeanUtils.copyProperties(group, groupRegJsonform);

			this.nSimpleHibernateDao.getHibernateTemplate().update(group);
		}else{
			responseMessage.setMessage("更新对象不存在，");
		}
		
		return true;
	}
	
	
	/**
	 * 查询所有机构列表
	 * @return
	 */
	public List<Group4Q> query(){
		return (List<Group4Q>) this.nSimpleHibernateDao.getHibernateTemplate().find("from Group4Q", null);
	}
	
	/**
	 * 查询所有机构列表
	 * @return
	 */
	public Group get(String uuid){
		return (Group)this.nSimpleHibernateDao.getObjectById(Group.class, uuid);
	}

	
	/**
	 * 查询指定用户的机构列表
	 * @return
	 */
	public List getGroupByUseruuid(String uuid){
		Session s = this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		String sql="";
		Query q = s.createSQLQuery("select {t1.*} from px_usergrouprelation t0,px_group {t1} where t0.groupuuid={t1}.uuid and t0.useruuid='"+uuid+"'")
				.addEntity("t1",Group.class);
		
		return q.list();
	}



	/**
	 * 品牌名是否存在
	 * 
	 * @param loginname
	 * @return
	 */
	private boolean isExitSameUserByBrand_name(String brand_name) {
		String attribute = "brand_name";
		Object group = nSimpleHibernateDao.getObjectByAttribute(Group.class,
				attribute, brand_name);

		if (group != null)// 已被占用
			return true;
		else
			return false;

	}
	
	/**
	 * 公司名是否存在
	 * @param company_name
	 * @return
	 */
	private boolean isExitSameUserByCompany_name(String company_name,String uuid) {
		String attribute = "company_name";
		Group group = (Group) nSimpleHibernateDao.getObjectByAttribute(
				Group.class, attribute, company_name);

		if (group != null)// 已被占用
			{
			// 判断的是自身
			if (StringUtils.isNotEmpty(uuid) && group.getUuid().equals(uuid))
				return false;
			else
				return true;
			}
		else
			return false;

	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

}
