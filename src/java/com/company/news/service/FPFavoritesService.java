package com.company.news.service;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.SystemConstants;
import com.company.news.cache.CommonsCache;
import com.company.news.commons.util.DbUtils;
import com.company.news.commons.util.PxStringUtil;
import com.company.news.entity.Announcements;
import com.company.news.entity.Announcements4Q;
import com.company.news.entity.ClassNews;
import com.company.news.entity.Favorites;
import com.company.news.entity.Group;
import com.company.news.entity.Group4Q;
import com.company.news.entity.Message;
import com.company.news.entity.Parent;
import com.company.news.entity.StudentBind;
import com.company.news.entity.User;
import com.company.news.jsonform.FavoritesJsonform;
import com.company.news.jsonform.MessageJsonform;
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
public class FPFavoritesService extends AbstractService {


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
			responseMessage.setMessage("已收藏");
			return false;
		}
		Favorites favorites = new Favorites();
		BeanUtils.copyProperties(favorites, favoritesJsonform);
		favorites.setCreatetime(TimeUtils.getCurrentTimestamp());
		if(favorites.getType()==null||StringUtils.isBlank(favorites.getReluuid())){
			
		}else if(SystemConstants.common_type_gonggao==favorites.getType().intValue()
				||SystemConstants.common_type_zhaoshengjihua==favorites.getType().intValue()){//公告,招生计划显示学校名和头像
				Announcements4Q tmp=(Announcements4Q)CommonsCache.get(favorites.getReluuid(), Announcements4Q.class);
				if(tmp!=null){
					Group4Q tmp_Group4Q=(Group4Q)CommonsCache.get(tmp.getGroupuuid(), Group4Q.class);
					if(tmp_Group4Q!=null){
						favorites.setShow_img(tmp_Group4Q.getImg());
						favorites.setShow_uuid(tmp.getGroupuuid());
						favorites.setShow_name(tmp_Group4Q.getBrand_name());
					}
				}
		}else if(SystemConstants.common_type_jingpinwenzhang==favorites.getType().intValue()){//精品文章显示发布这名和头像
			Announcements4Q tmp=(Announcements4Q)CommonsCache.get(favorites.getReluuid(), Announcements4Q.class);
			favorites.setShow_uuid(tmp.getCreate_useruuid());
			if(tmp.getCreate_useruuid()!=null){
				User tmp_User=(User)CommonsCache.get(tmp.getCreate_useruuid(), User.class);
				if(tmp_User!=null){//是老师发布
					favorites.setShow_img(tmp_User.getImg());
					favorites.setShow_name(tmp_User.getName());
				}
			}
		}
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

		String hql = "from Favorites where user_uuid='"+DbUtils.safeToWhereString(user_uuid)+"'";
		if (StringUtils.isNotBlank(type))
			hql += " and type=" + DbUtils.safeToWhereString(type);
		pData.setOrderFiled("createtime");
		pData.setOrderType("desc");
		
		PageQueryResult pageQueryResult = this.nSimpleHibernateDao
				.findByPaginationToHqlNoTotal(hql, pData);
		
		this.warpVoList(pageQueryResult.getData(), null);
		return pageQueryResult;
	}

	/**
	 * 删除 支持多个，用逗号分隔
	 * 
	 * @param uuid
	 */
	public boolean delete(String user_uuid,String uuid,String reluuid, ResponseMessage responseMessage) {
		if (StringUtils.isBlank(uuid)&&StringUtils.isBlank(reluuid)) {
			responseMessage.setMessage("参数:uuid或reluuid不能同时为空！");
			return false;
		}	
		int count=0;
		if (StringUtils.isNotBlank(uuid))// 多ID
		{
			count=this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from Favorites where user_uuid=? and uuid in("+DBUtil.stringsToWhereInValue(uuid)+")", user_uuid);

		} else {
			count=this.nSimpleHibernateDao.getHibernateTemplate().bulkUpdate(
					"delete from Favorites where user_uuid=? and reluuid in("+DBUtil.stringsToWhereInValue(reluuid)+")", user_uuid);


		}
		this.logger.info("delete Favorites count="+count);
//		if(count==0){
//			return false;
//		}
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
		List list = nSimpleHibernateDao.getHibernateTemplate().find("select reluuid from Favorites where reluuid=? and user_uuid=?", reluuid,user_uuid);

		if (list != null&&list.size()>0)// 已被占用
			return true;
		else
			return false;

	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private Favorites warpVo(Favorites o,String cur_user_uuid){
		this.nSimpleHibernateDao.getHibernateTemplate().evict(o);
		o.setShow_img(PxStringUtil.imgSmallUrlByUuid(o.getShow_img()));
		return o;
	}
	/**
	 * vo输出转换
	 * @param list
	 * @return
	 */
	private List<Favorites> warpVoList(List<Favorites> list,String cur_user_uuid){
		for(Favorites o:list){
			warpVo(o,cur_user_uuid);
		}
		return list;
	}
	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
