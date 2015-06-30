package com.company.common;

/**
 * MSB2.0
 * Copyright (c) 2007 www.company.com. All rights reserved.
 * @author CL
 * String操作的实用类
 * */
public class StringUtils {
    public StringUtils() {
    }

    // Unicode字符->GBK字符
    public static String unicodeToChinese(String str) throws Exception {
            if (str != null) {
                    return new String(str.getBytes("ISO-8859-1"), "GBK");
            } else {
                    return null;
            }
      }

    // 判断字符串是否为NULL或为空字符号串
    public static boolean isNullOrEmpty(String stringToTest) {
          return (stringToTest == null || stringToTest.trim().equals(""));
    }

    /*
     * 根据KEY得到相应的VALUE
     * @param String string
     *        需要分割的字符串
     * @param String separator
     *        分割符
     */
    public static String[] split(String string,String separator) {
        if (isNullOrEmpty(string)) {
            return null;
        }
        if(isNullOrEmpty(separator)){
            return  new String[]{string};
        }
        return string.split(separator);
    }
}
