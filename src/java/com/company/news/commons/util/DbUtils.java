package com.company.news.commons.util;


public class DbUtils {
  public static String stringToDateByDBType(String dateStr) {
    String mysqlStringToDate = "DATE_FORMAT('aaaa','%Y-%m-%d %H:%i:%s')";
//    String oracleStringToDate = "to_date('aaaa','yyyy-mm-dd hh24:mi:ss')";
//    String db2StringToDate = "TIMESTAMP('aaaa')";
//    String currStringToDate = oracleStringToDate;

    return mysqlStringToDate.replace("aaaa", dateStr);
  }
}
