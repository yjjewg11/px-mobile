package com.company.news.rest.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class EncryptUtils {

	/**密码加密。加密规则，密码先进行base64(4的整数倍)和md5(32)，base64后的字符串分成四段md5后的数据分成四段，进行md5[0]+base64[0]+...+md5[4]+base64[4]
	 * @param password
	 * @return加密后的密码
	 * @throws UnsupportedEncodingException
	 */
	public static String encrypt(String password) throws UnsupportedEncodingException{
		if(StringUtils.trimToNull(password)==null){
			return "";
		}else{
			password = password.trim();
		}
		String base64Encode = Base64.encodeBase64String(password.getBytes("UTF-8"));
		String md5Encode = md5Encrypt(password);
		int count = base64Encode.length()/4;
		StringBuffer encryptPassword = new StringBuffer();
		for(int i=0;i<4;i++){
			encryptPassword.append(StringUtils.substring(md5Encode, i*8, (i+1)*8));
			encryptPassword.append(StringUtils.substring(base64Encode, i*count, (i+1)*count));
		}
		return encryptPassword.toString();
	}

	public static String md5Encrypt(byte[] obj) {
		return DigestUtils.md5Hex(obj);
	}

	public static String md5Encrypt(String password) throws UnsupportedEncodingException {
		String md5 = md5Encrypt(password.getBytes("UTF-8"));
		return md5;
	}

	public static Map decrypt(String encryptPassword){
		int length = encryptPassword.length();
		if(length<36||(length-32)%4!=0){
			return new HashMap();
		}
		int count = (length-32)/4;
		StringBuffer md5 = new StringBuffer();
		StringBuffer base64Encode = new StringBuffer();
		for(int i=0;i<(length/(8+count));i++){
			md5.append(StringUtils.substring(encryptPassword, i*(8+count), (i+1)*(8+count)-count));
			base64Encode.append(StringUtils.substring(encryptPassword, i*(8+count)+8, (i+1)*(8+count)));
		}
		byte[] base64decode = Base64.decodeBase64(base64Encode.toString());
		String password = new String(base64decode);
//		System.out.println(password);
//		System.out.println(md5.toString());
//		System.out.println(base64Encode.toString());
		Map map = new HashMap();
		map.put("password", password);
		map.put("md5", md5.toString().toUpperCase());
		map.put("base64Encode", base64Encode.toString());
		return map;
	}


	public static void main(String[] a) throws UnsupportedEncodingException{
//		String password = encrypt("123");
//		System.out.println("63:"+password);//202cb962Mac59075bT964b0715I2d234b70z
//		String aa = (String)decrypt("202cb962Mac59075bT964b0715I2d234b70z").get("md5");
	}
}
