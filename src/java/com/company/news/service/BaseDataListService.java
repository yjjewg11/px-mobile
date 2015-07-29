package com.company.news.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.company.news.entity.BaseDataList;
import com.company.news.entity.BaseDataListCacheVO;
import com.company.news.jsonform.BaseDataListJsonform;
import com.company.news.vo.ResponseMessage;

/**
 * 
 * @author Administrator
 * 
 */
@Service
public class BaseDataListService extends AbstractServcice {
	public static final int enable_default = 1;// 可用

	/**
	 * 新增权限
	 * 
	 * @param name
	 * @param description
	 * @param responseMessage
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public BaseDataList add(BaseDataListJsonform baseDataListJsonform, ResponseMessage responseMessage) throws IllegalAccessException, InvocationTargetException {
		if (StringUtils.isBlank(baseDataListJsonform.getDatavalue()) || baseDataListJsonform.getDatavalue().length() > 45) {
			responseMessage.setMessage("权限名不能为空！，且长度不能超过45位！");
			return null;
		}

		if (StringUtils.isBlank(baseDataListJsonform.getTypeuuid())) {
			responseMessage.setMessage("描述不能为空！，且长度不能超过45位！");
			return null;
		}

		if (StringUtils.isBlank(baseDataListJsonform.getDatakey())) {
			responseMessage.setMessage("KEY不能为空");
			return null;
		}

		BaseDataList baseDataList = new BaseDataList();

		if (baseDataListJsonform.getEnable()==null) {
			baseDataListJsonform.setEnable(1);
		} 

		BeanUtils.copyProperties(baseDataList, baseDataListJsonform);

		this.nSimpleHibernateDao.getHibernateTemplate().save(baseDataList);
		return baseDataList;

	}

	/**
	 * 
	 * @param uuid
	 * @param value
	 * @param description
	 * @param key
	 * @param typeuuid
	 * @param enable
	 * @param responseMessage
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public BaseDataList update(BaseDataListJsonform baseDataListJsonform,
			ResponseMessage responseMessage) throws IllegalAccessException, InvocationTargetException {
		if (StringUtils.isBlank(baseDataListJsonform.getDatavalue()) || baseDataListJsonform.getDatavalue().length() > 45) {
			responseMessage.setMessage("权限名不能为空！，且长度不能超过45位！");
			return null;
		}

		if (StringUtils.isBlank(baseDataListJsonform.getTypeuuid())) {
			responseMessage.setMessage("描述不能为空！，且长度不能超过45位！");
			return null;
		}

		if (StringUtils.isBlank(baseDataListJsonform.getDatakey())) {
			responseMessage.setMessage("KEY不能为空");
			return null;
		}

		BaseDataList baseDataList = (BaseDataList) this.nSimpleHibernateDao
				.getObject(BaseDataList.class, baseDataListJsonform.getUuid());
		if (baseDataList != null) {
			if (baseDataListJsonform.getEnable()==null) {
				baseDataListJsonform.setEnable(enable_default);
			} 

			BeanUtils.copyProperties(baseDataList, baseDataListJsonform);
			this.nSimpleHibernateDao.getHibernateTemplate()
					.update(baseDataList);
		}else{
			responseMessage.setMessage("更新对象不存在，");
		}

		return baseDataList;

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
					"delete from BaseDataList where uuid in(?)", uuid);

		} else {
			this.nSimpleHibernateDao.deleteObjectById(BaseDataList.class, uuid);
		}

		return true;
	}

	/**
	 * 查询所有权限
	 * 
	 * @return
	 */
	public List<BaseDataList> getBaseDataListByTypeuuid(String typeuuid) {
		if(StringUtils.isEmpty(typeuuid))
			return null;
		
		return (List<BaseDataList>) this.nSimpleHibernateDao
				.getHibernateTemplate().find(
						"from BaseDataList where typeuuid=? order by datakey asc", typeuuid);

	}
	
	/**
	 * 查询所有权限
	 * 
	 * @return
	 */
	public List<BaseDataListCacheVO> getBaseDataAllList() {
		return (List<BaseDataListCacheVO>) this.nSimpleHibernateDao
				.getHibernateTemplate().find(
						"from BaseDataListCacheVO  order by datakey asc");

	}

	@Override
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return BaseDataListService.class;
	}

}
