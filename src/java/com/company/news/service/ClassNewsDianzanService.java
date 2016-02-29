package com.company.news.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.commons.util.DbUtils;
import com.company.news.entity.ClassNewsDianzan;
import com.company.news.interfaces.SessionUserInfoInterface;
import com.company.news.jsonform.ClassNewsDianzanJsonform;
import com.company.news.rest.util.DBUtil;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.DianzanListVO;
import com.company.news.vo.ResponseMessage;
import com.company.web.listener.SessionListener;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class ClassNewsDianzanService extends AbstractService {
	public static final int USER_type_default = 1;// 0:老师
	@Autowired
	private CountService countService;


	/**
	 * 
	 * @param classNewsDianzanJsonform
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public boolean save(ClassNewsDianzanJsonform classNewsDianzanJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(classNewsDianzanJsonform.getNewsuuid())) {
			responseMessage.setMessage("Newsuuid不能为空！");
			return false;
		}
		if (!canDianzan(classNewsDianzanJsonform.getNewsuuid(),classNewsDianzanJsonform.getCreate_useruuid())) {
			responseMessage.setMessage("不能重复点赞！");
			return false;
		} else {
			ClassNewsDianzan cndz = new ClassNewsDianzan();
			BeanUtils.copyProperties(cndz, classNewsDianzanJsonform);
			cndz.setCreate_time(TimeUtils.getCurrentTimestamp());
			cndz.setUsertype(USER_type_default);
			this.nSimpleHibernateDao.getHibernateTemplate().save(cndz);
		}

		return true;
	}

	
	/**
	 * 判断是否能点赞
	 * @param classNewsDianzanJsonform
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public boolean canDianzan(String newsuuid,String create_useruuid) throws Exception {

		List list = this.nSimpleHibernateDao.getHibernateTemplate().find(
				"select newsuuid from ClassNewsDianzan where newsuuid=? and create_useruuid=?",
				newsuuid,create_useruuid);
		if (list != null && list.size() > 0) {
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @param classNewsDianzanJsonform
	 * @param responseMessage
	 * @return
	 * @throws Exception
	 */
	public List getDianzanByNewsuuid(String newsuuid) throws Exception {
		if (StringUtils.isBlank(newsuuid)) {
			return null;
		}

		return this.nSimpleHibernateDao.getHibernateTemplate().find(
				"select create_user from ClassNewsDianzanOfShow where newsuuid=?", newsuuid);
	}

	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean delete(
			ClassNewsDianzanJsonform classNewsDianzanJsonform,
			ResponseMessage responseMessage) {
		if (StringUtils.isBlank(classNewsDianzanJsonform.getNewsuuid())) {

			responseMessage.setMessage("Newsuuid不能为空！");
			return false;
		}

		this.nSimpleHibernateDao
				.getHibernateTemplate()
				.bulkUpdate(
						"delete from ClassNewsDianzan where newsuuid=? and create_useruuid=?",
						classNewsDianzanJsonform.getNewsuuid(),
						classNewsDianzanJsonform.getCreate_useruuid());

		return true;
	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return ClassNewsDianzanService.class;
	}


	public DianzanListVO getDianzanListVO(String newsuuid,HttpServletRequest request) throws Exception {
		DianzanListVO vo = new DianzanListVO();
		if (StringUtils.isBlank(newsuuid)) {
			return null;
		}
		
		SessionUserInfoInterface user = SessionListener.getUserInfoBySession(request);
		String useruuid="";
		
		if(user!=null)useruuid=user.getUuid();
		String sql="select group_concat( t1.create_user) as user_names,count(1) as allcount,sum(case t1.create_useruuid when '"+DbUtils.safeToWhereString(useruuid)+"' then 1 else 0 end) as curuser_sum  from px_classnewsdianzan  t1 ";
		sql+=" where t1.newsuuid in("+DBUtil.stringsToWhereInValue(newsuuid)+")";
		sql+=" GROUP BY t1.newsuuid  ";
		Query q = this.nSimpleHibernateDao.createSQLQuery(sql);
		q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map> list=q.list();
	
		for(Map map:list){
			
			//统计当前用户点赞数量,0表示没点赞,可以点赞.
			vo.setCanDianzan("0".equals(map.get("curuser_sum")+""));
			vo.setCount(Integer.valueOf(map.get("allcount")+""));
			vo.setNames(map.get("user_names")+"");
		}

		return vo;
	}
}
