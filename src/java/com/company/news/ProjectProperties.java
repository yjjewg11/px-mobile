package com.company.news;

import java.util.Properties;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 取conf.properties 配置文件.中配置的参数
 * @author Administrator
 *
 */
public class ProjectProperties {
  //conf.properties 配置文件.
  private static Properties appProperty;
  
  @Autowired
  @Qualifier("appProperty")
  public static void setAppProperty(Properties appProperty) {
    ProjectProperties.appProperty = appProperty;
    
  }
  
  /**
   * 取conf.properties 配置文件.中配置的参数
   * @param s
   * @param defaultString
   * @return
   */
  public static String getProperty(String s, String defaultString) {
    return appProperty.getProperty(s,defaultString);
  }
}
