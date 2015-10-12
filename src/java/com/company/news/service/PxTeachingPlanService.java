package com.company.news.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.entity.PxTeachingplan;
import com.company.news.entity.Teachingplan;
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
		String sql = "SELECT MIN(plandate),classuuid FROM pxdb.px_pxteachingplan"
				+ " where classuuid in(" + classuuids + ") and plandate>now() group by classuuid";
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

}
