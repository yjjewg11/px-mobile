package com.company.news.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.PxTeacher;
import com.company.news.jsonform.PxTeacherJsonform;
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
public class PxTeacherService extends AbstractService {
	private static final String model_name = "培训教师模块";

	public boolean save(PxTeacherJsonform userTeacherJsonform,
			ResponseMessage responseMessage) throws Exception {

		// TEL格式验证
		if (StringUtils.isBlank(userTeacherJsonform.getUseruuid())) {
			responseMessage.setMessage("Useruuid不能为空！");
			return false;
		}
		PxTeacher ut=null;
		if(StringUtils.isBlank(userTeacherJsonform.getUuid())){
			ut=(PxTeacher) this.nSimpleHibernateDao.getHibernateTemplate().get(PxTeacher.class, userTeacherJsonform.getUuid());
			if(ut==null){
				responseMessage.setMessage("更新记录不存在");
				return false;
			}
		}
		
		if(ut==null){
			ut = new PxTeacher();
			userTeacherJsonform.setUuid(null);
		}
		userTeacherJsonform.setImg(PxStringUtil.imgUrlToUuid(userTeacherJsonform.getImg()));
		BeanUtils.copyProperties(ut, userTeacherJsonform);
		ut.setUpdate_time(TimeUtils.getCurrentTimestamp());
		this.nSimpleHibernateDao.getHibernateTemplate().saveOrUpdate(ut);

		return true;
	}


	/**
	 * 
	 * @param groupuuid
	 * @param classuuid
	 * @param name
	 * @param pData
	 * @return
	 */
	public PageQueryResult queryByPage(String groupuuid,String name, PaginationData pData) {
		
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql=" SELECT t1.uuid,t1.img,t1.name,t1.ct_stars,t1.ct_stars,t1.summary,t1.course_title";
		sql+=" FROM px_pxteacher t1 ";
//		sql+=" LEFT JOIN  px_pxcourse t2 on t1.course_uuid=t2.uuid ";
		sql+=" where  t1.status="+SystemConstants.PxCourse_status_fabu;
		
		if(StringUtils.isNotBlank(groupuuid)){
			sql+=" and t1.groupuuid in(" + DBUtil.stringsToWhereInValue(groupuuid) + ")";
		}
		if (StringUtils.isNotBlank(name))
			sql += " and  t1.name  like '%" + name + "%' ";
		sql+=" order by t1.ct_stars asc";
		Query q = s.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		PageQueryResult pageQueryResult =this.nSimpleHibernateDao.findByPageForSqlNoTotal(q, pData);
		List<Map> list=pageQueryResult.getData();
		for(Map pxCourse4Q:list)
		{
			warpMap(pxCourse4Q);
		}
		
		return pageQueryResult;
	}

	
	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	protected void warpMap(Map o) {
		o.put("img", PxStringUtil.imgSmallUrlByUuid((String)o.get("img")));
	}
	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	protected PxTeacher warpVo(PxTeacher o) {
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		o.setImg(PxStringUtil.imgUrlByUuid(o.getImg()));
		
		return o;
	}
	
	/**
	 *获取用户-老师详细信息.
	 * 
	 * @return
	 */
	public PxTeacher get(String uuid) {
		
		PxTeacher o=(PxTeacher) this.nSimpleHibernateDao.getHibernateTemplate().get(PxTeacher.class, uuid);
		this.warpVo(o);
		return o;
	}



	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}





}
