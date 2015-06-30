package com.company.common;

/**
 * MSB2.0
 * Copyright (c) 2007 www.company.com. All rights reserved.
 * @author CL
 * 初试化系统配置文件，该文件必须放在类路径下(msbus.properties)。
 * */

import java.util.Properties;
import java.io.InputStream;

public class PropertyUtils {
    private static Properties properties;
    private static final String filename ="msbus.properties";
    public PropertyUtils() {
    }
    /**
     * 初始化properties
     * @return
     */
    public synchronized static void initProperties(){
       if( properties==null){
           try {

               InputStream is = PropertyUtils.class.getClassLoader().
                                getResourceAsStream(filename);
               properties = new Properties();
               properties.load(is);
           } catch (Exception ex) {
               System.out.println(
                       "PropertyUtilities.java:类路径下文件msbus.properties初始化出错!");
               ex.printStackTrace();
           }
       }
    }

    /*返回properties信息
     *@return
    */
    public static Properties getPropertites(){
      initProperties();
      return properties;
    }
    /**
     * 根据KEY得到相应的VALUE
     * @param String key
     *        properties中的KEY
     * @param boolean changeEncoding
     *        是否进行编码格式的转换
     * @return KEY对应的value
     */
    public static String getProperty(String key,boolean changeEncoding) {
       initProperties();
       if(StringUtils.isNullOrEmpty(key)){
           System.out.println("PropertyUtils.java中方法getProperty传入参数key为NULL或空字符串,返回NULL");
           return null;
       }
       String result = null;
       try {
        if(properties.getProperty(key)!=null){
          if(changeEncoding==true){
             result =StringUtils.unicodeToChinese(properties.getProperty(key));
          }
          else{
             result=properties.getProperty(key);
          }
         }
         else{
           System.out.println("PropertyUtils.java中方法getProperty未在配置文件中找到 key "+key+"对应的value.");
         }
       }
       catch (Exception e) {
           System.out.println(
                       "PropertyUtilities.java:类路径下文件msbus.properties读取 KEY: "+key+"信息失败!");
           e.printStackTrace();
       }
       return result;
   }
   /**
    * 根据KEY得到相应的VALUE，如果为空，就返回默认值
    * @param String key
    *        properties中的KEY
    * @param String defaultValue
    *        默认值
    * @param boolean changeEncoding
    *        是否进行编码格式的转换
    * @return KEY对应的value
    */
    public static String getProperty(String key, String defaultValue,boolean changeEncoding) {
       String result=getProperty(key,changeEncoding);
       if(result==null){
           return defaultValue;
       }
       return result;
    }

}
