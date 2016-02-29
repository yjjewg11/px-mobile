package com.company.news.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.cache.PxRedisCache;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.vo.ResponseMessage;

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
		String sql=" SELECT t1.uuid,t1.title,t1.create_time,t1.create_useruuid,t1.reply_count,t1.yes_count,t1.status,t1.no_count,t1.level,t1.summary,t1.imguuids";
		sql+=" FROM sns_topic t1 ";
		sql+=" where t1.status=0 ";
		if(StringUtils.isNotBlank(section_id)){
			sql+=" and t1.section_id= "+section_id;
		}
		sql += " order by t1.create_time desc";

		Query  query =this.nSimpleHibernateDao.createSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForSqlNoTotal(query, pData);
		
		this.warpMapList(pageQueryResult.getData(), null);
		return pageQueryResult;
	}


	/**
	 * 获取发现,每日话题推荐1条.
	 * @return
	 */
	public Map getMainTopic() {
		
		
		//缓存有值优先处理
		Map  obj=PxRedisCache.getObject(PxRedisCache.Key_Name_MainTopic,Map.class);
		if(obj!=null){
			obj.put("url",  PxStringUtil.getSnsTopicWebViewURL((String)obj.get("uuid")));
			
			return obj;
		}
		String selectSql=" SELECT t1.uuid,t1.title ";
		selectSql+=" FROM sns_topic t1 ";
		
		String sqlwhere=" where t1.status= "+SystemConstants.Check_status_fabu ;
		sqlwhere += " order by t1.reply_count desc";
		
		
		String sql=selectSql+sqlwhere;

		Query  query =this.nSimpleHibernateDao.createSQLQuery(sql).setMaxResults(1);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> list=query.list();
		if(list!=null&&list.size()>0){
			Map topicMap=list.get(0);
			topicMap.put("url",  PxStringUtil.getSnsTopicWebViewURL((String)topicMap.get("uuid")));
			
			return topicMap;
		}
		return null;
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
