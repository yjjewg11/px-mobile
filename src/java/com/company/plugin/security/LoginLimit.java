package com.company.plugin.security;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.company.news.ProjectProperties;
import com.company.news.rest.RestConstants;
import com.company.news.vo.ResponseMessage;

public class LoginLimit {
	private static ConcurrentMap<String, Long[]> records = new ConcurrentHashMap<String, Long[]>();
	private static long MINUTE = 1000 * 60L;
	private static long TIME_LIMIT = MINUTE * Long.valueOf(ProjectProperties.getProperty(
			"project.LoginLimit.TIME_LIMIT", "10"));
	private static int COUNT_LIMIT = Integer.valueOf(ProjectProperties.getProperty(
			"project.LoginLimit.TIME_LIMIT", "5"));

	// 记录密码错误
	private static void pwdError(String username, long time) {
		Long[] userRecord = records.get(username);
		if (null == userRecord) {
			records.put(username, setValues(1, time));
		} else {
			records.put(username, updateRecord(userRecord, time));
		}
	}

	/**
	 * 1.验证错误次数是否在允许范围
	 * 2.验证次数在允许通过情况下，密码正确，清除以往错误记录。错误则增加错误记录并给出提示信息。
	 * 
	 */
	public static boolean verifyCount(String username, boolean pwdIsTrue,
			ResponseMessage responseMessage) {
		boolean pass = false;
		long time = new Date().getTime();
		Long[] userRecord = records.get(username);
		//做次数判断，必须优先于密码正确判断。
		if (null == userRecord) {
			pass = true;
		} else if (userRecord[0] < COUNT_LIMIT) {// 错误次数小于5
			pass = true;
		} else if (time - userRecord[1] > TIME_LIMIT) {// 错误次数大于等于5但登录间隔时间长，允许通过
			pass = true;
		} else {// 错误次数大于5且间隔过短，不允许通过
			int timeLeft = (int) Math
					.ceil((TIME_LIMIT - (time - userRecord[1]) + 0.0) / MINUTE);
			responseMessage.setMessage("密码已错误" + COUNT_LIMIT
					+ "次，在" + timeLeft + "分钟内无法登陆！");
			responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
		}
		// 验证次数在允许通过情况下
		if (pass) {
			// 密码正确时，清除以前错误登录记录
			if (pwdIsTrue) {
				clearRecord(username);
			} else {// 密码错误时，往request中写消息
				pwdError(username, time);
				userRecord = records.get(username);
				// 已达次数限制与未达限制次数，分别给出友好提示
				if (userRecord[0] >= COUNT_LIMIT) {
					responseMessage.setMessage("密码已错误"
							+ COUNT_LIMIT + "次，在" + (TIME_LIMIT / MINUTE)
							+ "分钟内无法登陆！");
				} else {
					responseMessage.setMessage("密码已错误"
							+ userRecord[0] + "次，错误" + COUNT_LIMIT + "次后在"
							+ (TIME_LIMIT / MINUTE) + "分钟内无法登陆！");
				}
				responseMessage.setStatus(RestConstants.Return_ResponseMessage_failed);
			}
		}

		return pass;
	}

	private static void clearRecord(String username) {
		if (null != records.get(username)) {
			records.remove(username);
		}
	}

	// 更新记录，次数加1，时间更改
	private static Long[] updateRecord(Long[] record, long time) {
		record[0]++;
		record[1] = time;
		return record;
	}

	private static Long[] setValues(long count, long time) {
		Long[] result = new Long[2];
		result[0] = count;
		result[1] = time;
		return result;
	}
}
