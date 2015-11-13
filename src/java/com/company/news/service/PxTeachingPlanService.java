package com.company.news.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.PxTeachingplan;
import com.company.news.entity.User;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class PxTeachingPlanService extends AbstractService {
	private static final String model_name = "培训结构教学计划模块";

	/**
	 * 查询所有班级
	 * 
	 * @return
	 */
	public PageQueryResult query(String begDateStr, String endDateStr,
			String classuuid, PaginationData pData, String cur_user_uuid) {
		if (StringUtils.isBlank(classuuid)) {
			return null;
		}
		String hql = "from PxTeachingplan where classuuid='" + classuuid + "' ";
		if (StringUtils.isNotBlank(begDateStr)) {
			hql += " and  plandate>=" + DBUtil.stringToDateByDBType(begDateStr);
		}
		if (StringUtils.isNotBlank(endDateStr)) {
			endDateStr = endDateStr.split(" ")[0] + " 23:59:59";
			hql += " and  plandate<=" + DBUtil.stringToDateByDBType(endDateStr);
		}
		pData.setOrderFiled("plandate");
		pData.setOrderType(PaginationData.SORT_ASC);

		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHql(hql, pData);

		this.warpVoList(pageQueryResult.getData(), cur_user_uuid);
		return pageQueryResult;
	}
	/**
	 * 查询一个班级所有课程
	 * 
	 * @return
	 */
	public PageQueryResult listAllByclassuuid(
			String classuuid,PaginationData pData) {
		if (StringUtils.isBlank(classuuid)) {
			return null;
		}
		String hql = "from PxTeachingplan where classuuid='" + classuuid + "' order by  plandate  asc";
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHql(hql, pData);

		return pageQueryResult;

	}

	@Autowired
	private CountService countService;

	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	private PxTeachingplan warpVo(PxTeachingplan o, String cur_user_uuid) {
		if (o == null)
			return null;
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		try {
			o.setCount(countService.count(o.getUuid(),
					SystemConstants.common_type_jiaoxuejihua));
			o.setDianzan(this.getDianzanDianzanListVO(o.getUuid(),
					cur_user_uuid));
			o.setReplyPage(this.getReplyPageList(o.getUuid()));
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
	private List<PxTeachingplan> warpVoList(List<PxTeachingplan> list,
			String cur_user_uuid) {
		for (PxTeachingplan o : list) {
			warpVo(o, cur_user_uuid);
		}
		return list;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public PxTeachingplan get(String uuid) {
		PxTeachingplan t = (PxTeachingplan) this.nSimpleHibernateDao
				.getObjectById(PxTeachingplan.class, uuid);

		return t;

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
					"delete from PxTeachingplan where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(PxTeachingplan.class,
					uuid);

		}

		return true;
	}

	/**
	 * 获取班级最近的培训计划
	 * 
	 * @return
	 */
	public Map<String, Date> getMinPlandateByClassuuids(String classuuids) {
		Map<String, Date> map=new HashMap();
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = "SELECT MIN(plandate),classuuid FROM px_pxteachingplan"
				+ " where classuuid in(" + classuuids + ") and plandate>=curdate() group by classuuid";
		Query q = s.createSQLQuery(sql);
		List<Object[]> list = q.list();

		for (Object[] o : list) {
			map.put((String)o[1], (Date)o[0]);
		}

		return map;

	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}

	/**
	 * 根据当前时间显示下一次课表的时间。
	 * @param parentuuid
	 * @return
SELECT * from (
SELECT t1.classuuid,t1.uuid,t1.plandate,t1.name,t1.address,t1.readyfor,t4.headimg as student_headimg,t5.brand_name as group_name,t2.name as class_name
FROM
px_pxteachingplan t1 
LEFT JOIN  px_pxclass t2 on t1.classuuid=t2.uuid 
LEFT JOIN  px_pxstudentpxclassrelation t3 on t3.class_uuid=t2.uuid
LEFT JOIN  px_pxstudent t4 on t3.student_uuid=t4.uuid
LEFT JOIN  px_group t5 on t2.groupuuid=t5.uuid 
 where  t1.plandate>=curdate() 
and t4.uuid in(select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='' )
order by t1.plandate asc
) t GROUP BY t.classuuid


	 */
	public List nextList(String cur_user_uuid) {
		Session s = this.nSimpleHibernateDao.getHibernateTemplate()
				.getSessionFactory().openSession();
		String sql = " SELECT * from (";
		sql+=" SELECT t1.classuuid,t1.uuid,t1.plandate,t1.name,t1.address,t1.readyfor,t4.headimg as student_headimg,t5.brand_name as group_name,t2.name as class_name";
		
		sql+=" ,t4.name as student_name,t6.title as course_title,t2.courseuuid,t2.groupuuid";
		sql+=" FROM px_pxteachingplan t1 ";
		sql+=" LEFT JOIN  px_pxclass t2 on t1.classuuid=t2.uuid ";
		sql+=" LEFT JOIN  px_pxstudentpxclassrelation t3 on t3.class_uuid=t2.uuid";
		sql+=" LEFT JOIN  px_pxstudent t4 on t3.student_uuid=t4.uuid";
		sql+=" LEFT JOIN  px_group t5 on t2.groupuuid=t5.uuid ";
		sql+= " left join px_pxcourse t6 on t2.courseuuid=t6.uuid  ";
		sql+=" where  t1.plandate>=curdate() ";
		sql+=" and t4.uuid in(select  DISTINCT student_uuid from px_pxstudentcontactrealation where parent_uuid='"+cur_user_uuid+"' )";
		sql+=" order by t1.plandate asc";
		sql+=" ) t GROUP BY t.classuuid";
		sql+="";
		Query q = s.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> list = q.list();
		warpVoList_Map(list,cur_user_uuid);
		return list;
	}
	
	
	/**
	 * vo输出转换
	 * 
	 * @param list
	 * @return
	 */
	private Map warpVo_Map(Map
		o, String cur_user_uuid) {
		if (o == null)
			return null;
		//this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		try {
		
			String student_headimg=(String)o.get("student_headimg");
			o.put("student_headimg", PxStringUtil.imgSmallUrlByUuid(student_headimg));
			
			
			o.put("count",countService.count((String)o.get("uuid"),
					SystemConstants.common_type_pxteachingPlan));
			o.put("dianzan", this.getDianzanDianzanListVO((String)o.get("uuid"),
					cur_user_uuid));
			o.put("replyPage", this.getReplyPageList((String)o.get("uuid")));
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
	private List<Map> warpVoList_Map(List<Map> list,
			String cur_user_uuid) {
		for (Map o : list) {
			warpVo_Map(o, cur_user_uuid);
		}
		return list;
	}

}
