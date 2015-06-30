package com.company.common;

import java.util.*;

public class ConfigReader {
	public ConfigReader() {
	}

	private static final String CONFIG_BUNDLE_NAME = "msbus";

	static public String getParameter(String paraName) {
		PropertyResourceBundle configBundle = (PropertyResourceBundle) PropertyResourceBundle
				.getBundle(CONFIG_BUNDLE_NAME);
		String result = null;
		try {
			result = new String(configBundle.getString(paraName).getBytes("ISO-8859-1"), "gb2312");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return result;
	}
}
