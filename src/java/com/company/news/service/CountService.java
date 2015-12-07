package com.company.news.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.cache.PxRedisCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Count;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class CountService extends AbstractService {
	// 20150610 去掉对用户表的TYPE定义，默认都为0
	public static final int count_type_classnews = 1;// 互动类

	
	
	/**
	 * 查询计数(并加1)并且缓存.
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Map getIncrCountByExt_uuids(String ext_uuids) throws Exception {
		Map map=new HashMap();
		if (StringUtils.isBlank(ext_uuids)) {
			return map;
		}
		ext_uuids=PxStringUtil.StringDecComma(ext_uuids);
		String[] ext_uuidsArr=ext_uuids.split(",");
		//1.优先在缓存中取数据
		 List<Object> list=PxRedisCache.getIncrCountByExt_uuids(ext_uuidsArr);
		 
		 //2.构造数据.
		 //noCacheext_uuids 如果为"".表示缓存全部命中直接返回.否则取数据库.
		 String noCacheext_uuids="";
		 for(int i=0;i<ext_uuidsArr.length;i++){
			 Object value= list.get(i);
			 if(value!=null&&!"1".equals(value+"")){
				 map.put(ext_uuidsArr[i], value);
			 }else{
				 noCacheext_uuids+=ext_uuidsArr[i]+",";
			 }
		 }
		 //缓存全部命中直接返回
		 if(StringUtils.isBlank(noCacheext_uuids)){
			 return map;
		 }
		 //3.数据库中取数据
		String sql = "select ext_uuid, sum(count) from px_count where ext_uuid in("+DBUtil.stringsToWhereInValue(noCacheext_uuids)+") group by ext_uuid";
		List<Object[]> listNoCahe=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(sql).list();
		Map<String,String> mapToCache=new HashMap();
		for(Object[] ObjArr:listNoCahe){
			Object valObj=ObjArr[1];
			if(valObj!=null){//计数加一
				valObj=Long.valueOf(valObj.toString())+1;
			}
			map.put(ObjArr[0], valObj);
			mapToCache.put(ObjArr[0]+"", valObj+"");
		}
		//4.有数据则,缓存起来.
		if(!mapToCache.isEmpty()){
			PxRedisCache.setCountByExt_uuids(mapToCache);
		}
		return map;
		
	}
	/**
	 * 批量更新计数,提供效率.使用缓存.getIncrCountByExt_uuids
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	@Deprecated
	public void update_countBatch(String ext_uuids) throws Exception {
		if (StringUtils.isBlank(ext_uuids)) {
			return ;
		}
		String sql = "update px_count set count=count+1,update_time=now() where ext_uuid in("+DBUtil.stringsToWhereInValue(ext_uuids)+")";
		this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(sql).executeUpdate();

	}
	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Count add(String ext_uuid, Integer type) throws Exception {
		if (StringUtils.isBlank(ext_uuid)) {
			return null;
		}
			Count	c = new Count();
		c.setType(type);
		c.setUpdate_time(TimeUtils.getCurrentTimestamp());
		c.setExt_uuid(ext_uuid);
		c.setCount(0l);

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(c);
		//初始化为1
		PxRedisCache.setCountByExt_uuid(ext_uuid, 1L);
		return c;
	}
	
	
	/**
	 * 增加班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Long count(String ext_uuid, Integer type) throws Exception {
		if (StringUtils.isBlank(ext_uuid)) {
			return 0l;
		}

		Count c = (Count) this.nSimpleHibernateDao.getObjectByAttribute(
				Count.class, "ext_uuid", ext_uuid);

		if (c == null)
			c = new Count();

		c.setType(type);
		c.setUpdate_time(TimeUtils.getCurrentTimestamp());
		c.setExt_uuid(ext_uuid);
		long count = 0;
		if (c.getCount() != null)
			count = c.getCount();

		c.setCount(++count);

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().saveOrUpdate(c);

		return count;
	}

	/**
	 * 更新班级
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public Long get(String ext_uuid, Integer type) throws Exception {
		if (StringUtils.isBlank(ext_uuid)) {
			return 0l;
		}

		Count c = (Count) this.nSimpleHibernateDao.getObjectByAttribute(
				Count.class, "ext_uuid", ext_uuid);
		Long count = 0l;
		if (c != null)
			count = c.getCount();

		return count;

	}

	/**
	 * 查询所有班级
	 * 
	 * @return
	 */
	public void delete(String ext_uuid) {
		this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
				"delete from Count where ext_uuid =?", ext_uuid);
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
