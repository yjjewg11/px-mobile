package com.company.news.springMVC;

import java.util.Properties;

import javax.annotation.Resource;

import net.sf.json.util.JSONUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.company.news.ProjectProperties;
import com.company.news.dao.NSimpleHibernateDao;
import com.company.news.json.NpmsDateMorpher;
import com.company.news.json.NpmsTimestampMorpher;

/**
 * 用于启动后,初始化cache和dao.
 * @author Administrator
 *
 */
@Component  
public class SpringBeanPostProcessor implements BeanPostProcessor {
	
  //conf.properties 配置文件.
  private static Properties appProperty;
  
  @Autowired
  @Qualifier("appProperty")
  public void setAppProperty(Properties appProperty) {
    ProjectProperties.setAppProperty(appProperty);
    
  }

	
    private static NSimpleHibernateDao nSimpleHibernateDao; 
  
    
    public static NSimpleHibernateDao getNSimpleHibernateDao() {
    return nSimpleHibernateDao;
  }
    @Autowired
    @Qualifier("NSimpleHibernateDao") 
  public  void  setNSimpleHibernateDao(NSimpleHibernateDao simpleHibernateDao) {
    nSimpleHibernateDao = simpleHibernateDao;
  }
    private static SessionFactory sessionFactory;  

    private static boolean isInit=false;
//    //conf.properties 配置文件.
//    private static Properties appProperty;
//    public static Properties getAppProperty() {
//      return appProperty;
//    }
//    
//    @Resource  
//    public void setAppProperty(Properties appProperty) {
//      SpringBeanPostProcessor.appProperty = appProperty;
//    }
    public static SessionFactory getSessionFactory() {
      return sessionFactory;
    }
    @Resource  
    public  void setSessionFactory(SessionFactory sessionFactory) {
      SpringBeanPostProcessor.sessionFactory = sessionFactory;
    }


    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {  
        // Bean实例化之前执行  
        return bean;  
    }  
  

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {  
        // Bean实例化之后执行   
      if(isInit)return bean;
      isInit=true;
      Session session = sessionFactory.openSession();  
      TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));  
//      DocColumnConverter.setDao(nSimpleHibernateDao);
//      NewsPopedomUtil.setDao(nSimpleHibernateDao);
      JSONUtils.getMorpherRegistry().registerMorpher(
				new NpmsTimestampMorpher(new String[] { "yyyy-MM-dd HH:mm:ss" }));
//      JSONUtils.getMorpherRegistry().registerMorpher(
//				new NpmsDateMorpher(new String[] { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" }));
        initAll();
        return bean;  
    }  
    //初始化所有数据.
    public static void  initAll() {
//      NewsBasicdataCache.init();
//      ColumnCache.init();
//      UITemplateCache.init();
//      NewsProperties.init();
//      DocColumnConverter.init();
      //NewsPopedomUtil.initColumnPopedoms(nSimpleHibernateDao);
    }
    
//    public static NSimpleHibernateDao getDao() {
//      return dao;
//    }
//    @Resource  
//    public  void setDao(NSimpleHibernateDao dao) {
//      SpringBeanPostProcessor.dao = dao;
//    }
}  