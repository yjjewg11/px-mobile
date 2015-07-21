package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.news.entity.ClassNewsDianzan;
import com.company.news.jsonform.ClassNewsDianzanJsonform;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class ClassNewsDianzanService extends AbstractServcice {
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
	public boolean dianzan(ClassNewsDianzanJsonform classNewsDianzanJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(classNewsDianzanJsonform.getNewsuuid())) {
			responseMessage.setMessage("Newsuuid不能为空！");
			return false;
		}
		if (canDianzan(classNewsDianzanJsonform.getNewsuuid(),classNewsDianzanJsonform.getCreate_useruuid())) {
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
	public boolean cancelDianzan(
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

}
