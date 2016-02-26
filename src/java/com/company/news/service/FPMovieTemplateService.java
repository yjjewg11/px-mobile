package com.company.news.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.FPMovie;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.FPMovieJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class FPMovieTemplateService extends AbstractService {

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	String Selectsql=" SELECT t1.key,t1.title,t1.herald,t1.mp3 ";
	String SqlFrom=" FROM fp_movie_template t1 ";

	/**
	 * 查询我创建的相册
	 * @param user
	 * @param pData
	 * @param model
	 * @return
	 */
	public PageQueryResult query(
			PaginationData pData, ModelMap model) {
		Session session=this.nSimpleHibernateDao.getHibernateTemplate().getSessionFactory().openSession();
		String selectsql=Selectsql;
		String sqlFrom=SqlFrom;
		String sql=sqlFrom;
		pData.setPageSize(20);
		
		  sql += " order by CONVERT( t1.title USING gbk)";
		 
		Query  query =session.createSQLQuery(selectsql+sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		String countsql="select count(*) "+sql;
	    PageQueryResult pageQueryResult = this.nSimpleHibernateDao.findByPageForQueryTotal(query,countsql, pData);

		return pageQueryResult;
	}
	
	private void warpMap(Map o, SessionUserInfoInterface user) {
		try {
			o.put("herald", PxStringUtil.imgFPPhotoUrlByRelativePath_sub((String)o.get("herald")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List warpMapList(List<Map> list,SessionUserInfoInterface user ) {
		
		for(Map o:list){
			warpMap(o,user);
		}
		
		return list;
	}
}
