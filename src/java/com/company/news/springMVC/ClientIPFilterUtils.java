package com.company.news.springMVC;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.company.news.ProjectProperties;

public class ClientIPFilterUtils {
  public static List<String> excludeFiltersList_URL = new ArrayList<String>();
  
  public static List<String> excludeFiltersList_menu = new ArrayList<String>();
  static{
    String _excludeFilters =ProjectProperties.getProperty(
      "Filter_clientIP_regexp", "");
    
    String[] excludeFiltersArray = org.springframework.util.StringUtils
            .tokenizeToStringArray(_excludeFilters,
                    ",");
    for (int i = 0, j = excludeFiltersArray.length; i < j; i++) {
      excludeFiltersList_URL.add(excludeFiltersArray[i]);
    }
    String _excludeFilters_menu =ProjectProperties.getProperty(
      "Filter_clientIP_regexp_menu", "");
    
    String[] excludeFiltersArray_menu = org.springframework.util.StringUtils
            .tokenizeToStringArray(_excludeFilters_menu,
                    ",");
    for (int i = 0, j = excludeFiltersArray_menu.length; i < j; i++) {
      excludeFiltersList_menu.add(excludeFiltersArray_menu[i]);
    }
  }
  
  
  /**
   * 获取真实ip
   * @param request
   * @return
   */
  public static String getIpAddr(HttpServletRequest request) {
      String ip = request.getHeader("x-forwarded-for");
      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
      }
      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
      }
      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
      }
      if(ip!=null&&ip.length()>20){
          System.out.println("ip.length()>20 trim:"+ip);
          ip=ip.substring(0, 20);
      }
      return ip;
      }
  /**
   * 判读客户端是否被受限
   * @return
   */
  public static boolean  limitedByClientIP(HttpServletRequest request){
    String clientIPregexp=ProjectProperties.getProperty(
      "Filter_clientIP_regexp", "");
    if(StringUtils.isBlank(clientIPregexp))return true;//为空则不限制.
    String clientIp=ClientIPFilterUtils.getIpAddr(request);
    Pattern pat=Pattern.compile(clientIPregexp);
    Matcher mat = pat.matcher(clientIp);   
    boolean rs = mat.find();   
    return rs;
  }
}
