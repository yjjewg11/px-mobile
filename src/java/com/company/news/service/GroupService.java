package com.company.news.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.commons.util.DistanceUtil;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Group;
import com.company.news.entity.Group4Q;
import com.company.news.entity.UserGroupRelation;
import com.company.news.jsonform.GroupRegJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class GroupService extends AbstractService {
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
	 * 查询所有培训机构列表
	 * @return
	 */
	public Group get(String uuid){
		Group group=(Group)this.nSimpleHibernateDao.getObjectById(Group.class, uuid);
		this.nSimpleHibernateDao.getHibernateTemplate().evict(group);
		group.setDescription(PxStringUtil.warpHtml5Responsive(group.getDescription()));
		 return group;
	}
	/**
	 * 查询所有培训机构列表
	 * @return
	 */
	public Group4Q getGroup4Q(String uuid){
		Group4Q group=(Group4Q)this.nSimpleHibernateDao.getObjectById(Group4Q.class, uuid);
//		this.nSimpleHibernateDao.getHibernateTemplate().evict(group);
//		group.setDescription(PxStringUtil.warpHtml5Responsive(group.getDescription()));
		 return group;
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
		return null;
	}

	public PageQueryResult pxlistByPage(String type,String sort, PaginationData pData,String point) {
		
		
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql=" SELECT DISTINCT t1.uuid,t1.brand_name,t1.img,t1.ct_stars,t1.ct_study_students,t1.link_tel,t1.map_point,t1.address,t1.summary";
		sql+=" FROM px_group t1 ";
		
		if(StringUtils.isNotBlank(type)){
			sql+=" LEFT JOIN  px_pxcourse t2 on t2.groupuuid=t1.uuid ";
			sql+=" where  t1.type=2 and t1.status=9 and t2.type="+type;
		}else{
			sql+=" where t1.type=2 and t1.status=9 ";
			
		}
		double[] lngLatArr=null;
		if(StringUtils.isNotBlank(point)){
			lngLatArr=DistanceUtil.getLongitudeAndLatitude(point);
		}
		
		if("distance".equals(sort)&&lngLatArr!=null){
			double lng1=lngLatArr[0];
			double lat1=lngLatArr[1];
			//String range_of_distance=ProjectProperties.getProperty("range_of_distance","20");
			
			sql+=" order by ACOS(SIN(("+lat1+" * 3.1415) / 180 ) *SIN((t1.lat * 3.1415) / 180 ) " +
					"+COS(("+lat1+" * 3.1415) / 180 ) * COS((t1.lat * 3.1415) / 180 ) " +
							"*COS(("+lng1+"* 3.1415) / 180 - (t1.lng * 3.1415) / 180 ) ) * 6380 asc";
			
//			sql+=" order by t1.updatetime asc";
		}else if("appraise".equals(sort)){//
			sql+=" order by t1.ct_stars desc";
		}else{//自能排序
			sql+=" order by t1.ct_study_students desc";
		}
		
		Query q = s.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		PageQueryResult pageQueryResult =this.nSimpleHibernateDao.findByPageForSqlNoTotal(q, pData);
		List<Map> list=pageQueryResult.getData();
		for(Map obj:list)
		{
			//当课程LOGO为空时，取机构的LOGO
			if(StringUtils.isNotBlank((String)obj.get("img")))
				obj.put("img", PxStringUtil.imgSmallUrlByUuid((String)obj.get("img")));
			
			//当前坐标点参数不为空时，进行距离计算
			if(StringUtils.isNotBlank(point)){
				obj.put("distance", DistanceUtil.getDistance(point, (String)obj.get("map_point")));
			}else{
				obj.put("distance", "");
			}
		}
		
		return pageQueryResult;
//		
//		String hql = "from Group4Q where type="+SystemConstants.Group_type_2+" and status=9";
//		hql += " order by create_time asc";
//		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
//				.findByPaginationToHql(hql, pData);
//		this.warpVoList(pageQueryResult.getData());
//		return pageQueryResult;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public Group4Q warpVo(Group4Q o){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		o.setImg(PxStringUtil.imgSmallUrlByUuid(o.getImg()));
		return o;
	}
	
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public Group warpVo(Group o){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		o.setImg(PxStringUtil.imgSmallUrlByUuid(o.getImg()));
		return o;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	public List<Object> warpVoList(List<Object> list){
		for(Object o:list){
			if(o instanceof Group){
				warpVo((Group)o);
			}else if(o instanceof Group4Q){
				warpVo((Group4Q)o);
			}
		}
		return list;
	}
	
	
}
