package com.company.news.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.company.news.commons.util.PxStringUtil;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class SnsTopicService extends AbstractService {
	private static final String model_name = "基础数据类型模块";
	public PageQueryResult hotByPage(PaginationData pData,String section_id,
			HttpServletRequest request) {
		Session session=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		String sql=" SELECT t1.uuid,t1.title,t1.create_time,t1.create_useruuid,t1.reply_count,t1.yes_count,t1.status,t1.no_count,t1.level,t1.summary,t1.imguuids";
		sql+=" FROM sns_topic t1 ";
		sql+=" where t1.status=0 ";
		if(StringUtils.isNotBlank(section_id)){
			sql+=" and t1.section_id= "+section_id;
		}
		sql += " order by t1.create_time desc";

		Query  query =session.createSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForSqlNoTotal(query, pData);
		
		this.warpMapList(pageQueryResult.getData(), null);
		return pageQueryResult;
	}

	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List warpMapList(List<Map> list,SessionUserInfoInterface user ) {
		for(Map o:list){
			warpMap(o);
		}
		return list;
	}
	
	private void warpMap(Map o) {
		o.put("imgList", PxStringUtil.uuids_to_imgSmallUrlurlList((String)o.get("imguuids")));
		o.put("webview_url",  PxStringUtil.getSnsTopicWebViewURL((String)o.get("uuid")));
		
	}
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
