package com.company.news.rest.util;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.company.news.ProjectProperties;

public class StringOperationUtil {
	public static String stringToDateByDBType(String dateStr) {
	    String mysqlStringToDate = "DATE_FORMAT('aaaa','%Y-%m-%d %H:%i:%s')";
	    String oracleStringToDate = "to_date('aaaa','yyyy-mm-dd hh24:mi:ss')";
	    String db2StringToDate = "TIMESTAMP('aaaa')";
	    String currStringToDate = oracleStringToDate;
	    if ("mysql".equals(DBUtil.dbtype)) {
	      currStringToDate = mysqlStringToDate;
	    } else if ("db2".equals(DBUtil.dbtype)) {
	      currStringToDate = db2StringToDate;
	    }

	    return currStringToDate.replace("aaaa", dateStr);
	  }

	/**
	 * @param str
	 * @return
	 * 2013-6-14
	 * TODO 前后增加“，”
	 * @author yl
	 */
	public static String specialFormateUsercode(String str){
		
		if(StringUtils.isEmpty(str)){
			return str;
		}	
		StringBuffer sb=new StringBuffer();
		if(!str.startsWith(",")){
			sb.append(",");
		}
		sb.append(str);
		if(!str.endsWith(",")){
			sb.append(",");
		}		
		return sb.toString();
	}
	
	/**
	 * 去除两边“，”
	 *<p><code>trimSeparatorChars</code></p>
	 *Description:
	 * @param str
	 * @return
	 */
	public static String trimSeparatorChars(String str){
		if(StringUtils.isEmpty(str)){
			return str;
		}
		if(str.startsWith(",")){
			str = str.substring(1);
		}
		if(str.endsWith(",")){
			str =str.substring(0,str.length()-1);
		}		
		return str;
		
	}
	
	
	/**
     * 传入基础数据名称字段,根据语言标识取字段中相应的数据名称
     * @param basicdataname(基础数据名称字段)
     * @param LanagerStg(语言标识)
     * @return
     */
    public static String getNameOfLanagerByLanagerStg(String basicdataname,String language){
        if(StringUtils.isEmpty(basicdataname)){
            return "";
        }
        try{
            JSONObject js = JSONObject.fromObject(basicdataname);
            basicdataname = js.getString(language);
        }catch(JSONException e){
        }finally{
            return basicdataname;
        }
        
    }
	
}
