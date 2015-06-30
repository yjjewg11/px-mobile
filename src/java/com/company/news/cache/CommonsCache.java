package com.company.news.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.company.common.SpringContextHolder;
import com.company.news.dao.NSimpleHibernateDao;
import com.company.news.entity.Cookbook;
import com.company.news.entity.CookbookPlan;
import com.company.news.entity.PClass;
import com.company.news.entity.Right;
import com.company.news.entity.User;
import com.company.news.service.AbstractServcice;


public class CommonsCache{
	private static Logger logger = Logger.getLogger("CommonsCache");
	private static Cache dbDataCache = (Cache) SpringContextHolder
			.getBean("dbDataCache");
    private static NSimpleHibernateDao nSimpleHibernateDao=SpringContextHolder.getBean("NSimpleHibernateDao");

	
	// 获取自动保存内容
	public static User getUser(String uuid) {
		logger.info("getUser:uuid-->" + uuid);
		Element user = dbDataCache.get(uuid);

		if (user != null)
			return (User) user.getObjectValue();
		else {
			User object =(User) nSimpleHibernateDao.getObject(User.class, uuid);
			if (object != null)
				putUser(uuid, object);
			return object;
		}
	}

	// 存入自动保存内容
	public static void putUser(String uuid, User user) {
		logger.info("putUser:uuid-->" + uuid);
		Element e = new Element(uuid, user);
		dbDataCache.put(e);
	}
	
	
	// 获取自动保存内容
	public static PClass getClass(String uuid) {
		logger.info("getClass(String):uuid-->" + uuid);
		Element c = dbDataCache.get(uuid);

		if (c != null)
			return (PClass) c.getObjectValue();
		else {
			PClass object =(PClass) nSimpleHibernateDao.getObject(PClass.class, uuid);
			if (object != null)
				putClass(uuid, object);
			return object;
		}
	}

	// 存入自动保存内容
	public static void putClass(String uuid, PClass c) {
		logger.info("putClass:uuid-->" + uuid);
		Element e = new Element(uuid, c);
		dbDataCache.put(e);
	}

	// 获取自动保存内容
	public static Right getRight(String uuid) {
		logger.info("getRight:uuid-->" + uuid);
		Element c = dbDataCache.get(uuid);

		if (c != null)
			return (Right) c.getObjectValue();
		else {
			Right object =(Right) nSimpleHibernateDao.getObject(Right.class, uuid);
			if (object != null)
				putRight(uuid, object);
			return object;
		}
	}

	// 存入自动保存内容
	public static void putRight(String uuid, Right c) {
		logger.info("putRight:uuid-->" + uuid);
		Element e = new Element(uuid, c);
		dbDataCache.put(e);
	}
	
	// 获取自动保存内容
	public static Cookbook getCookbook(String uuid) {
		logger.info("getCookbook:uuid-->" + uuid);
		Element c = dbDataCache.get(uuid);

		if (c != null)
			return (Cookbook) c.getObjectValue();
		else {
			Cookbook object =(Cookbook) nSimpleHibernateDao.getObject(Cookbook.class, uuid);
			if (object != null)
				putCookbook(uuid, object);
			return object;
		}
	}

	// 存入自动保存内容
	public static void putCookbook(String uuid, Cookbook c) {
		logger.info("putCookbook:uuid-->" + uuid);
		Element e = new Element(uuid, c);
		dbDataCache.put(e);
	}
}
