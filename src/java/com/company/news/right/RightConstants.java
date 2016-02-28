package com.company.news.right;

public class RightConstants {
static public String Return_msg="无操作权限";//添加老师
	
	
	static public Integer Role_Type_AD=Integer.valueOf(0);//平台权限类型
	static public Integer Role_Type_KD=Integer.valueOf(1);//幼儿园权限类型
	static public Integer Role_Type_PX=Integer.valueOf(2);//培训权限类型
	//注册帐号给与的最高权限
	static public String Role_AD_admini="Role_AD_admin";//平台最高管理员权限
	//注册帐号给与的最高权限
	static public String Role_KD_admini="Role_KD_admin";//幼儿园最高管理员权限
	//注册帐号给与的最高权限
	static public String Role_PX_admini="Role_PX_admin";//幼儿园最高管理员权限
	/**
	 * 命名说明:
	 * AD_:后台管理权限
	 * KD_:幼儿园管理权限
	 * PX_:培训机构管理权限
	 */
	static public String AD_right_m="AD_right_m";//权限管理权限(增改)
	static public String AD_right_del="AD_right_del";//权限管理权限(删)
	static public String AD_role_m="AD_role_m";//	角色管理(增改)
	static public String AD_role_del="AD_role_del";//角色管理(删)
	static public String AD_basedata_m="AD_basedata_m";//基础数据权限(增改)
		static public String AD_basedata_del="AD_basedata_del";//基础数据权限(删)
		static public String AD_class_m="AD_class_m";//	班级管理员权限-增傻查改
		static public String AD_user_m="AD_user_m";//	用户管理权限
		static public String AD_user_del="AD_user_del";//	用户删除权限
		static public String AD_classnew_m="AD_classnew_m";//	班级互动浏览
		static public String AD_parent_m="AD_parent_m";//	家长数据浏览.
		static public String AD_announce_m="AD_announce_m";//发布消息权限-帮助文档(增改)
		static public String AD_group_m="AD_group_m";	//浏览所有学校权限
		
		static public String AD_checkSns_m="AD_checkSns_m";	//话题和话题评论审核权限
		
	static public String KD_group_m="KD_group_m";//添加分校权限
	static public String KD_announce_m="KD_announce_m";//发布消息权限(增改)
	static public String KD_cookbookplan_m="KD_cookbookplan_m";//发布每日食谱
	static public String KD_student_m="KD_student_m";//学生管理
	static public String KD_student_allquery="KD_student_allquery";//所有学生查看权限
	static public String KD_teachingplan_m="KD_teachingplan_m";//教学计划
	static public String KD_teacher_m="KD_teacher_m";//添加老师权限	
	static public String KD_accounts_m="KD_accounts_m";//添加账目权限
	static public String KD_Leader_Msg_m="KD_Leader_Msg_m";//园长信箱
	static public String KD_statistics_m="KD_statistics_m";//统计数据查看
	static public String KD_teachingjudge_q="KD_teachingjudge_q";//查看老师评价
	static public String KD_class_m="KD_class_m";//	班级管理员权限-增傻查改
	static public String KD_class_del="KD_class_del";//	班级管理员权限-增傻查改
	static public String KD_role_m="KD_role_m";//	角色管理(增改)
	
	static public String PX_group_m="PX_group_m";//添加分校权限
	static public String PX_announce_m="PX_announce_m";//发布消息权限(增改)
	static public String PX_student_m="PX_student_m";//学生管理
	static public String PX_student_allquery="PX_student_allquery";//所有学生查看权限
	static public String PX_teacher_m="PX_teacher_m";//添加老师权限	
	static public String PX_Leader_Msg_m="PX_Leader_Msg_m";//园长信箱
	static public String PX_statistics_m="PX_statistics_m";//统计数据查看
	static public String PX_teachingjudge_q="PX_teachingjudge_q";//查看老师评价
	static public String PX_class_m="PX_class_m";//	班级管理员权限-增傻查改
	static public String PX_class_del="PX_class_del";//	班级管理员权限-增傻查改
	static public String PX_role_m="PX_role_m";//	角色管理(增改)
	static public String PX_course_m="PX_course_m";//	对外课程管理权限
	
	
}
