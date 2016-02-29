package com.company.news.service;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.AbstractClass;
import com.company.news.entity.User;
import com.company.news.entity.UserClassRelation;
import com.company.news.entity.UserForJsCache;
import com.company.news.rest.util.DBUtil;

public class AbstractClassService extends AbstractService {
	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	protected AbstractClass warpVo(AbstractClass o) {
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		try {
			
			String sql="select t2.type  ,group_concat( t1.name) as user_names,group_concat( t1.uuid) as user_uuids from px_user  t1 ";
			sql+=" LEFT JOIN  px_userclassrelation t2 on t2.useruuid=t1.uuid  ";
			sql+=" where t2.type is not null and t2.classuuid='"+o.getUuid()+"'";
			sql+=" GROUP BY t2.type  ";
			Query q = this.nSimpleHibernateDao.createSQLQuery(sql);
			q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			List<Map> list=q.list();
			
			for(Map map:list){
				if(String.valueOf(SystemConstants.class_usertype_head).equals(map.get("type")+"")){
					o.setHeadTeacher((String)map.get("user_uuids"));
					o.setHeadTeacher_name((String)map.get("user_names"));
				}else {
					o.setTeacher((String)map.get("user_uuids"));
					o.setTeacher_name((String)map.get("user_names"));
				}
			}
//
//			List<UserClassRelation> l = (List<UserClassRelation>) this.nSimpleHibernateDao
//					.getHibernateTemplate().find(
//							"from UserClassRelation where classuuid=?",
//							o.getUuid());
//
//			String headTeacher = "";
//			String teacher = "";
//			String headTeacher_name = "";
//			String teacher_name = "";
//			for (UserClassRelation u : l) {
//				UserForJsCache user = (UserForJsCache) CommonsCache
//						.get(u.getUseruuid(), UserForJsCache.class);
//				if (user != null) {
//					if (u.getType().intValue() == SystemConstants.class_usertype_head) {
//
//						headTeacher += (u.getUseruuid() + ",");
//						headTeacher_name += (user.getName() + ",");
//
//					} else {
//						teacher += (u.getUseruuid() + ",");
//						teacher_name += (user.getName() + ",");
//					}
//				}
//			}
//			o.setHeadTeacher(PxStringUtil.StringDecComma(headTeacher));
//			o.setTeacher(PxStringUtil.StringDecComma(teacher));
//			o.setHeadTeacher_name(PxStringUtil.StringDecComma(headTeacher_name));
//			o.setTeacher_name(PxStringUtil.StringDecComma(teacher_name));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	protected List<AbstractClass> warpVoList(List<AbstractClass> list) {
		for (AbstractClass o : list) {
			warpVo(o);
		}
		return list;
	}
	
	/*
	 * 
	 * 判断是否是班级的班主任老师
	 */
	public boolean isheadteacher(String user_uuids, String classuuid) {
		String hql = "select uuid from UserClassRelation where type=? and classuuid=? and useruuid in("
				+ DBUtil.stringsToWhereInValue(user_uuids) + ")";
		List list = this.nSimpleHibernateDao.getHibernateTemplate().find(hql,
				SystemConstants.class_usertype_head, classuuid);
		if (list.size() > 0)
			return true;
		return false;
	}
	/**
	 * 获取老师相关班级的uuid
	 * 
	 * @param classNewsDianzanJsonform
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public List getTeacherRelClassUuids(String user_uuid) throws Exception {
		this.logger.info("user_uuid=" + user_uuid);
		List list = this.nSimpleHibernateDao.getHibernateTemplate()
				.find("select classuuid from UserClassRelation where useruuid=?", user_uuid);
		return list;
	}
	
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return AbstractClassService.class;
	}

}
