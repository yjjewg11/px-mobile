package com.company.news;


//系统常量表
public class SystemConstants {
    //------------------------上传模块
	static public final Integer UploadFile_type_head = Integer.valueOf(1);//我的头像
	static public final Integer UploadFile_type_cook = Integer.valueOf(2);//菜谱图片
	static public final Integer UploadFile_type_xheditorimg = Integer.valueOf(3);//xheditor编辑器菜单图片上传
//	
//    static public final String UploadFile_type_myhead = "head";//我的头像
//    static public final String UploadFile_type_identity_card ="identity";//身份证
//    static public final String UploadFile_type_marathon = "marathon";//马拉松认证照片
//    
    static public final String UploadFile_imgtype = "jpg";//马拉松认证照片 
	 
    //认证。默认0，0:没验证。1:验证。2：提交验证，3.验证失败
    static public final String User_Verify_NO = "0";//用户验证状态-未验证
    static public final String User_Verify_Pass = "1";//用户验证状态-验证通过
    static public final String User_Verify_Apply = "2";//用户验证状态-提交验证
    static public final String User_Verify_Fail= "3";//用户验证状态-验证失败

    
    static public String Charset="UTF-8";
    
}


