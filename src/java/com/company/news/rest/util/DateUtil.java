package com.company.news.rest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * 日期Util类
 * 
 * @author cwx
 *
 */
public class DateUtil {
    private static String defaultDatePattern = "yyyy-MM-dd";
    private static String TIMESTAMP_FORMAT_CONS = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 获得默认的 date pattern
     */
    public static String getDatePattern() {
        return defaultDatePattern;
    }

    /**
     * 返回预设Format的当前日期字符串
     */
    public static String getToday() {
        Date today = new Date();
        return format(today);
    }

    /**
     * 使用预设Format格式化Date成日期字符串
     */
    public static String format(Date date) {
        return date == null ? "" : format(date, getDatePattern());
    }

    
    /**
     * 使用预设Format格式化Date成时间字符串
     */
    public static String formatTime(Date date) {
        return date == null ? "" : format(date, TIMESTAMP_FORMAT_CONS);
    }
    
    /**
     * 使用参数Format格式化Date成字符串
     */
    public static String format(Date date, String pattern) {
        return date == null ? "" : new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 使用预设格式将字符串转为Date
     */
    public static Date parse(String strDate) throws ParseException {
        return StringUtils.isBlank(strDate) ? null : parse(strDate, getDatePattern());
    }
    
    /**
     * 使用预设格式将字符串转为Time
     */
    public static Date parseTime(String strDate) throws ParseException {
        return StringUtils.isBlank(strDate) ? null : parse(strDate, TIMESTAMP_FORMAT_CONS);
    }
    

    /**
     * 使用参数Format将字符串转为Date
     */
    public static Date parse(String strDate, String pattern) throws ParseException {
        return StringUtils.isBlank(strDate) ? null : new SimpleDateFormat(pattern).parse(strDate);
    }

    /**
     * 在日期上增加数个整月
     */
    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * 根据日期获得该日的周一
     * 
     * @param date
     * @return 该日的周一
     */
    public static String getFirstDayOfWeek(Date date) {
    	Calendar c = new GregorianCalendar();
    	c.setFirstDayOfWeek(Calendar.MONDAY);
    	c.setTime(date);
    	c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
    	return format(c.getTime(),"yyyyMMdd");
    }
	/**
	 * 根据日期获得该日的周一
	 * 
	 * @param date
	 * @return 该日的周一
	 */
	public static Date getFirstDayofWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
		return c.getTime();
	}
	/**
	 * 根据日期获得该日的周二
	 * 
	 * @param date
	 * @return 该周的周二
	 */
	public static String getLastDayOfTuesday(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 1); // Sunday
		return format(c.getTime(),"yyyyMMdd");
	}
	/**
	 * 根据日期获得该日的周三
	 * 
	 * @param date
	 * @return 该周的周三
	 */
	public static String getLastDayOfWednesday(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+2); // Sunday
		return format(c.getTime(),"yyyyMMdd");
	}
	/**
	 * 根据日期获得该日的周四
	 * 
	 * @param date
	 * @return 该周的周四
	 */
	public static String getLastDayOfTursday(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+3); // Sunday
		return format(c.getTime(),"yyyyMMdd");
	}
	/**
	 * 根据日期获得该日的周五
	 * 
	 * @param date
	 * @return 该周的周五
	 */
	public static String getLastDayOfFriday(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+4); // Sunday
		return format(c.getTime(),"yyyyMMdd");
	}
	/**
	 * 根据日期获得该日的周六
	 * 
	 * @param date
	 * @return 该周的周六
	 */
	public static String getLastDayOfSaterday(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+5); // Sunday
		return format(c.getTime(),"yyyyMMdd");
	}
	/**
	 * 根据日期获得该日的周日
	 * 
	 * @param date
	 * @return 该日的周日
	 */
	public static String getLastDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+6); // Sunday
		return format(c.getTime(),"yyyyMMdd");
	}
	
	/**
	 * 根据日期获得该日的周日
	 * 
	 * @param date
	 * @return 该日的周日
	 */
	public static Date getLastDayofWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+6); // Sunday
		return c.getTime();
	}
	/**
	 * 根据指定参数,返回当前日期的上周,或者下周(1,为下周,-1为上周,以此类推)
	 * 
	 * @param week
	 * @return 该周的周一和周五的日期
	 */
	public static Map GetDay(int week) {
		Map map = new HashMap();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd"); 		
     	Calendar calendar= Calendar.getInstance();
        calendar.add(5, week * 7); 
		map.put("startMonday",getFirstDayOfWeek(calendar.getTime ()));
		map.put("tuesday",getLastDayOfTuesday(calendar.getTime ()));
		map.put("wednesday",getLastDayOfWednesday(calendar.getTime ()));
		map.put("turesday",getLastDayOfTursday(calendar.getTime ()));
		map.put("friday",getLastDayOfFriday(calendar.getTime ()));
		map.put("saterday",getLastDayOfSaterday(calendar.getTime ()));
		map.put("endSunday",getLastDayOfWeek(calendar.getTime ()));
		return map;
		}
	/**
	 * 指定周数，返回当前周的周一，周日是的日期
	 *
	 * @param week
	 *            周数
	 * 
	 *           


	 * @return 挂号资源列表
	 */
	public static String getWeekForDate(int week) {

		Map map = DateUtil.GetDay(week);
		String firstDay = (String) map.get("startMonday");
		String tuesday = (String) map.get("tuesday");
		String wednesday = (String) map.get("wednesday");
		String turesday = (String) map.get("turesday");
		String friday = (String) map.get("friday");
		String saterday = (String) map.get("saterday");
		String lastDay = (String) map.get("endSunday");
		return firstDay+","+tuesday+","+wednesday+","+turesday+","+friday+","+saterday+","+lastDay;
	}
	
	/**
	 * 计算距离指定日期天数的日期
	 * 
	 * @param dateFrom
	 * @param days
	 * @return 新日期
	 */
	public static Date dateAddDays(Date dateFrom, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateFrom);
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
		Date date = calendar.getTime();
		return date;
	}
	
	/**
	 * 计算从dateFrom到dateTo有多少天，如果dataFrom在dateTo之后则返回负数
	 * 
	 * @param dateFrom
	 * @param dateTo
	 * @return 两个日期之间的差
	 */
	public static int dateDiff(Date dateFrom, Date dateTo) {

		long dateDiffs = (dateTo.getTime() - dateFrom.getTime()) / (24 * 60 * 60 * 1000);
		int num = Integer.parseInt(Long.toString(dateDiffs));
		// 如果两个日期包含时间则可能出现误差,所以需要修正
		Date temp = new Date(dateFrom.getTime() + 24 * 60 * 60 * 1000 * Long.valueOf(num));
		if (DateUtils.isSameDay(temp, dateTo)) {
			if (dateTo.compareTo(temp) > 0)
				num++;
			if (dateTo.compareTo(temp) < 0)
				num--;
		}
		return num;
	}
	
	
	/**
	 *   为时间增加减少一天
	 * @param datestr需要减少的时间
	 * @param timestr   根据时间判断
	 * @return
	 * @throws ParseException
	 */
	public static Map<String,Object> getReallyDateforSch04(String datestr,String timestr) throws ParseException{
		Map<String,Object> map = new HashMap<String,Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT_CONS);
		SimpleDateFormat newdateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(dateFormat.parse("1970-1-1 "+timestr).before(dateFormat.parse("1970-1-1 "+"04:00:00"))){
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(newdateFormat.parse(datestr));
			calendar.add(GregorianCalendar.DATE, -1);
			map.put("date", calendar.getTime());
			map.put("formatStr","1970-1-2 ");
		}else{
			map.put("date", newdateFormat.parse(datestr));
			map.put("formatStr","1970-1-1 ");
		}
		return map;
	}
	
}