package com.company.news.service;

import java.util.List;

import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.AbstractClass;
import com.company.news.entity.User;
import com.company.news.entity.UserClassRelation;
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

			List<UserClassRelation> l = (List<UserClassRelation>) this.nSimpleHibernateDao
					.getHibernateTemplate().find(
							"from UserClassRelation where classuuid=?",
							o.getUuid());

			String headTeacher = "";
			String teacher = "";
			String headTeacher_name = "";
			String teacher_name = "";
			for (UserClassRelation u : l) {
				User user = (User) CommonsCache
						.get(u.getUseruuid(), User.class);
				if (user != null) {
					if (u.getType().intValue() == SystemConstants.class_usertype_head) {

						headTeacher += (u.getUseruuid() + ",");
						headTeacher_name += (((User) CommonsCache.get(
								u.getUseruuid(), User.class)).getName() + ",");

					} else {
						teacher += (u.getUseruuid() + ",");
						teacher_name += (((User) CommonsCache.get(
								u.getUseruuid(), User.class)).getName() + ",");
					}
				}
			}
			o.setHeadTeacher(PxStringUtil.StringDecComma(headTeacher));
			o.setTeacher(PxStringUtil.StringDecComma(teacher));
			o.setHeadTeacher_name(PxStringUtil.StringDecComma(headTeacher_name));
			o.setTeacher_name(PxStringUtil.StringDecComma(teacher_name));
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
