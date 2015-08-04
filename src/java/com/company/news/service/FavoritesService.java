package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.entity.Favorites;
import com.company.news.entity.Group;
import com.company.news.entity.Message;
import com.company.news.entity.Parent;
import com.company.news.entity.StudentBind;
import com.company.news.entity.User;
import com.company.news.jsonform.FavoritesJsonform;
import com.company.news.jsonform.MessageJsonform;
import com.company.news.query.PageQueryResult;
import com.company.news.query.PaginationData;
import com.company.news.rest.util.TimeUtils;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class FavoritesService extends AbstractServcice {


	/**
	 * 增加
	 * 
	 * @param entityStr
	 * @param model
	 * @param request
	 * @return
	 */
	public boolean add(FavoritesJsonform favoritesJsonform,
			ResponseMessage responseMessage) throws Exception {
		if (StringUtils.isBlank(favoritesJsonform.getTitle())) {
			responseMessage.setMessage("Title不能为空!");
			return false;
		}

		if (StringUtils.isBlank(favoritesJsonform.getReluuid())) {
			responseMessage.setMessage("Reluuid不能为空！");
			return false;
		}
		
		if(isExitFavorites(favoritesJsonform.getUser_uuid(), favoritesJsonform.getReluuid())){
			responseMessage.setMessage("已收藏，不需要再进行收藏");
			return false;
		}

		
		Favorites favorites = new Favorites();
		BeanUtils.copyProperties(favorites, favoritesJsonform);
		favorites.setCreatetime(TimeUtils.getCurrentTimestamp());

		// 有事务管理，统一在Controller调用时处理异常
		this.nSimpleHibernateDao.getHibernateTemplate().save(favorites);

		return true;
	}

	/**
	 * 查询所有通知
	 * 
	 * @return
	 */
	public PageQueryResult query(String type, String user_uuid,PaginationData pData) {

		String hql = "from Favorites where user_uuid='"+user_uuid+"'";
		if (StringUtils.isNotBlank(type))
			hql += " and type=" + type;
		pData.setOrderFiled("createtime");
		pData.setOrderType("desc");
		
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHql(hql, pData);
		return pageQueryResult;
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
					"delete from Favorites where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(Favorites.class, uuid);

		}
		return true;
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public Favorites get(String uuid) throws Exception {
		Favorites favorites = (Favorites) this.nSimpleHibernateDao.getObjectById(
				Favorites.class, uuid);

		return favorites;
	}


	/**
	 * 是否用户名已被占用
	 * 
	 * @param loginname
	 * @return
	 */
	private boolean isExitFavorites(String user_uuid,String reluuid) {
		if(StringUtils.isBlank(reluuid))return false;
		List list = nSimpleHibernateDao.getHibernateTemplate().find("from Favorites where reluuid=? and user_uuid=?", reluuid,user_uuid);

		if (list != null&&list.size()>0)// 已被占用
			return true;
		else
			return false;

	}
	
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return User.class;
	}
}
