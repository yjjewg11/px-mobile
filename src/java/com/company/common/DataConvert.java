package com.company.common;

import java.text.*;
import java.util.*;
import java.util.regex.*;

public class DataConvert {
	public DataConvert() {
	}

	static public Date StringToDate(String DateString, String FormatString) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					FormatString);
			return simpleDateFormat.parse(DateString);
		} catch (Exception e) {
		}
		return null;
	}

	static public String DateToString(Date date, String FormatString) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					FormatString);
			return simpleDateFormat.format(date);
		} catch (Exception e) {
		}
		return null;
	}

	static public String XMLEncode(String str) { // 替换XML字符串中的在Html中非法的字符
		if (str != null && str.trim().length() > 0) {
			str = str.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
					.replaceAll(">", "&gt;").replaceAll("'", "&apos;")
					.replaceAll("\"", "&quot;");
		}
		return str;
	}

	static public String removeSpecialChar(String str) {
		return str.replaceAll("\n", "").replaceAll("\r", "");
	}
}
