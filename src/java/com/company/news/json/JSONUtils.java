package com.company.news.json;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;



public final class JSONUtils {

  public JSONUtils() {}

  public static String getJsonString(Object obj) {
    if ((obj instanceof Array) || (obj instanceof Map) || (obj instanceof Collection))
      return arrayToJSONString(obj);
    else
      return objectToJsonString(obj);
  }

  private static String arrayToJSONString(Object obj) {
    String json = JSONArray.fromObject(obj).toString();
    return json;
  }

  private static String objectToJsonString(Object obj) {
    String json = JSONObject.fromObject(obj).toString();
    return json;
  }

  public static List jsonToList(String jsonstr, Object t) {
    JSONArray jsonArray = JSONArray.fromObject(jsonstr);
    List list = JSONArray.toList(jsonArray, t, new JsonConfig());
    return list;
  }

  public static Object jsonToObject(String jsonstr, Class tclazz) {
    JSONObject jsonObject = JSONObject.fromObject(jsonstr);
    Object t = JSONObject.toBean(jsonObject, tclazz);
    return t;
  }


}