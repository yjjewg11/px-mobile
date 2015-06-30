package com.company.news.commons.util;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
/**
 * 不适用该方法
 * @author Administrator
 *
 */
@Deprecated
public class SingleContextLoaderUtils
{

//    private SingleContextLoaderUtils()
//    {
//    }
//
//    public static SingleContextLoaderUtils getInstance()
//    {
//        return contextLoaderUtils;
//    }
//
//    private void initApplicationContext()
//    {
//        BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance();
//        String parentContextKey = "tppV2";
//        BeanFactoryReference parentContextRef = locator.useBeanFactory(parentContextKey);
//        ctx = (ApplicationContext)parentContextRef.getFactory();
//    }
//
//    public void setApplicationContext(ApplicationContext ctx)
//    {
//        this.ctx = ctx;
//    }
//
//    public ApplicationContext getApplicationContext()
//    {
//        if(ctx == null)
//            initApplicationContext();
//        return ctx;
//    }
//
//    private static SingleContextLoaderUtils contextLoaderUtils = new SingleContextLoaderUtils();
//    private ApplicationContext ctx;

}
