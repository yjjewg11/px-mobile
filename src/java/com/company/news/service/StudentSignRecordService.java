package com.company.news.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.entity.DoorRecord;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class StudentSignRecordService extends AbstractServcice {

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public PageQueryResult query(String student_uuid, PaginationData pData) {
		String hql = "from StudentSignRecord where ";
		//if (StringUtils.isNotBlank(student_uuid)){
			hql += "  studentuuid in("+DBUtil.stringsToWhereInValue(student_uuid)+")";
		//}
		pData.setOrderType("desc");
		pData.setOrderFiled("sign_time");
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPaginationToHql(hql, pData);
				List<DoorRecord> list=pageQueryResult.getData();

		return pageQueryResult;
	}

}
