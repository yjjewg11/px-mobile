package com.company.news.rest.util;


import java.io.File;
import java.util.StringTokenizer;

import jcifs.smb.SmbFile;

import org.apache.commons.lang.StringUtils;

public class SmbFileUtil {

	public static SmbFile createSmbFile(String smbStr) throws Exception{
		//  smb://ip/sharefolder（例如：smb://192.168.0.77/test）
		//Smb://username:password@ip/sharefolder（例如：smb://chb:123456@192.168.0.1/test）
		 SmbFile smbFile = new SmbFile("smb://guest:1234@192.168.3.56/share/a.txt");
		 return smbFile;
	}
	
	/**
	 * 判断是不是smb格式路径
	 * jcifs 是 cifs(common internet file system) 
	 * @param smbStr
	 * @return
	 */
	public static boolean isSmbFileFormat(String smbStr){
		if(smbStr!=null){
			if(smbStr.startsWith("smb://"))return true;
		}
		return false;
	}
	public static void mkSmbDirIfNoExist(String smbStr)throws Exception{
		StringTokenizer   st=new  StringTokenizer(smbStr,"/");   
	      String   path1=st.nextToken()+"//";   
	      String path2 =path1;
	      while(   st.hasMoreTokens()   )   
	      {   
	    	  path1=st.nextToken();
	            if(StringUtils.isBlank(path1)){
	            	continue;
	            }
	           path2+=path1+"/"; 
	           SmbFile inbox;
				inbox = new  SmbFile(path2);
				  if(!inbox.exists())   
	                  inbox.mkdir();   
	      }
	}
	public static void mkDirIfNoExist(String Str){
		StringTokenizer   st=new  StringTokenizer(Str,"/");   
	      String   path1=st.nextToken()+"/";   
	      String path2 =path1;
	      while(st.hasMoreTokens())   
	      {
	    	  path1=st.nextToken();
	            if(StringUtils.isBlank(path1)){
	            	continue;
	            }
	           path2+=path1+"/"; 
	           File   inbox  =   new  File(path2);   
	            if(!inbox.exists())   
	                  inbox.mkdir();   
	      }
	}
	
/**
 * 返回文件名:如C:\abc\a.txt 返回a.txt
 * @param path
 * @param smb
 * @return
 */
	public static String getFilename(String path){
		String r=path;
		 if(StringUtils.isEmpty(path))return path;
		 path=path.replaceAll("\\\\", "/");
		 String[] ar=path.split("/");
		  try {
			r=ar[ar.length-1];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return r;
	}
	/**
	 * 盘符转为smb格式:如C:\abc\a 转为:smb://abc/a
	 * @param path
	 * @param smb
	 * @return
	 */
		public static String convertDriveToSmb(String path,String smb){
			 if(StringUtils.isEmpty(path))return path;
				if(path.startsWith("smb://"))return path;
			 if(path.length()<2&&path.charAt(1)!=':')return path;
			 path=path.replaceAll("\\\\", "/");
			 path=path.replaceAll("//", "/");
			 return SmbFileUtil.connectedPathFileName(smb, path.substring(2));
		}
		
		/**
		 * 盘符转为smb格式:如C:\abc\a 转为:smb://abc/a
		 * @param path
		 * @param smb
		 * @return
		 */
			public static String convertDriveToSmb(String path,String smb,String username,String password){
				 if(StringUtils.isEmpty(path))return path;
					if(path.startsWith("smb://"))return path;
				 if(path.length()<2&&path.charAt(1)!=':')return path;
				 path=path.replaceAll("\\\\", "/");
				 path=path.replaceAll("//", "/");
				 return SmbFileUtil.connectedPathFileName(smb, path.substring(2));
			}
			
	public static String connectedPathFileName(String path,String fileName){
		 path=path.replaceAll("\\\\", "/");
		 fileName=fileName.replaceAll("\\\\", "/");
		 if(StringUtils.isEmpty(path))return fileName;
		 if(fileName.charAt(0)=='/'){
			 fileName=fileName.substring(1);
		 }
		char fileSplit = path.charAt(path.length() - 1);
		if (fileSplit == '/')
			path = path + fileName;
		else
			path = path + "/" + fileName;
		
		return path;
	}
	
	public static void main(String[] args) {
		String path="D:\\abcd\\dfdf\\adf.txt";
		String fileName="\\dfadfad.txt";
		String str="smb://infoshare:Infoshare456@172.16.254.211/InfoShare 2.0/cctv/返回数据/tmp/tmp/tmp";

		String d=SmbFileUtil.getFilename(path);
		System.out.println(d);
		if(true)return ;
	    try {
	    	// smb://infoshare:Infoshare456@172.16.254.211/InfoShare 2.0/cctv
	    	//"smb://guest:1234@192.168.3.56/share/a.txt"
	    	String str1="smb://infoshare:Infoshare456@172.16.254.211/InfoShare 2.0/cctv/返回数据/tmp/tmp/tmp";
	    	SmbFileUtil.mkSmbDirIfNoExist(str);
	      SmbFile smbFile = new SmbFile(str);
	      if(!smbFile.exists()){
	    	  smbFile.mkdir();
	      }
//	      int length = smbFile.getContentLength();//得到文件的大小
//	      
//	      byte buffer[] = new byte[length];
//	      SmbFileInputStream in = new SmbFileInputStream(smbFile); //建立smb文件输入流
//	      while ((in.read(buffer)) != -1) {
//	        System.out.write(buffer);
//	        System.out.println(buffer.length);
//	      }
//	      in.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	}
}
