package com.company.news.commons.util;

public class ClassUtil {
  /** * 根据类名反射创建对象 * @param name 类名 * @return 对象 * @throws Exception */
  public static Object getInstance(String name) throws Exception {
    Class<?> cls = Class.forName(name);
    return cls.newInstance();
  }
}
