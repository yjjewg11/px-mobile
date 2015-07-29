
//用户登陆
function ajax_userinfo_login() {
	
	 var $btn = $("#btn_login");
	  $btn.button('loading');
	$.AMUI.progress.start();

	var loginname = $("#loginname").val();
	var password = $("#password").val();
	if(password.length!=32){
		 password=$.md5(password); 
	}
	
	var url = hostUrl + "rest/userinfo/login.json?loginname=" + loginname + "&password="
			+ password;
	$.ajax({
		type : "POST",
		url : url,
		data : "",
		dataType : "json",
		success : function(data) {
			 $btn.button('reset');
			$.AMUI.progress.done();
			// 登陆成功直接进入主页
			if (data.ResMsg.status == "success") {
				Store.clear();
				//判断是否保存密码，如果保存则放入cookie，否则清除cookie
				setCookie("bs_loginname", loginname);
				if($("#pw_checked")[0].checked){
					setCookie("bs_password", password);
					setCookie("pw_checked", "checked");
				} else {
					setCookie("bs_password", ""); 
					setCookie("pw_checked", "");
				}
				Store.setUserinfo(data.userinfo);
				Store.setGroup(data.list);
				Store.setUserRights(data.S_User_rights);
				
				G_CallPhoneFN.jsessionToPhone(data.JSESSIONID);
				
				menu_body_fn();
				
				
			} else {
				alert(data.ResMsg.message);
			}
		},
		error : function( obj, textStatus, errorThrown ){
			 $btn.button('reset');
			$.AMUI.progress.done();
			alert(url+","+textStatus+"="+errorThrown);
			 console.log(url+',error：', obj);
			 console.log(url+',error：', textStatus);
			 console.log(url+',error：', errorThrown);
		}
	});
}


function ajax_userinfo_logout(){
	Queue.clear();
	$.AMUI.progress.start();
	var url = hostUrl + "rest/userinfo/logout.json";
	$.ajax({
		type : "POST",
		url : url,
		data : "",
		dataType : "json",
		success : function(data) {
			$.AMUI.progress.done();
			menu_userinfo_login_fn();
			G_CallPhoneFN.jsessionToPhone("");
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			alert(url+","+textStatus+"="+errorThrown);
			 console.log(url+',error：', obj);
			 console.log(url+',error：', textStatus);
			 console.log(url+',error：', errorThrown);
			 window.location = hostUrl + "login.html";
		}
	});
}
;

;
;

function ajax_getUserinfo(isInit) {
	$.AMUI.progress.start();
	var url = hostUrl + "rest/userinfo/getUserinfo.json";
	$.ajax({
		type : "GET",
		url : url,
		async: false,
		dataType : "json",
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				if(data.userinfo)Store.setUserinfo(data.userinfo);
				if(data.list)Store.setGroup(data.list);
				G_CallPhoneFN.jsessionToPhone(data.JSESSIONID);
				
				menu_body_fn();
			} else {
				if(!isInit)alert(data.ResMsg.message);
				G_resMsg_filter(data.ResMsg);
			}
			
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			alert(url+","+textStatus+"="+errorThrown);
			 console.log(url+',error：', obj);
			 console.log(url+',error：', textStatus);
			 console.log(url+',error：', errorThrown);
		}
	});
}

//userinfo end



function menu_classnewsbyMy_list_fn() {
	Queue.push(menu_classnewsbyMy_list_fn);
	g_classnews_url=hostUrl + "rest/classnews/getClassNewsByMy.json";
	g_classnews_class_list=Store.getMyClassList();
	ajax_classnews_list();
};


//

function commons_ajax_reply_list(newsuuid,list_div,pageNo){
	var re_data=null;
	 if(!pageNo)pageNo=1;
	$.AMUI.progress.start();
	var url = hostUrl + "rest/reply/getReplyByNewsuuid.json?newsuuid="+newsuuid+"&pageNo="+pageNo;
	$.ajax({
		type : "GET",
		url : url,
		dataType : "json",
		async: false,
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				React.render(React.createElement(Common_Classnewsreply_listshow, {
					events: data.list,
					newsuuid:newsuuid,
					responsive: true, bordered: true, striped :true,hover:true,striped:true
					}), document.getElementById(list_div));
				re_data=data.list;
			} else {
				alert(data.ResMsg.message);
			}
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			alert(url+","+textStatus+"="+errorThrown);
			 console.log(url+',error：', obj);
			 console.log(url+',error：', textStatus);
			 console.log(url+',error：', errorThrown);
		}
	});
	return re_data;
};
//记录当前翻页
var g_classnews_pageNo_point=1;
var g_classnews_classuuid=null;
var g_classnews_url=null;
function ajax_classnews_list(classuuid,pageNo) {
	if(!classuuid)classuuid=g_classnews_classuuid;
	else g_classnews_classuuid=classuuid;
	if(!pageNo)pageNo=1;
	g_classnews_pageNo_point=pageNo;
	if(!g_classnews_url){
		alert("ajax_classnews_list 缺少参数:g_classnews_url");
		return;
	}
	$.AMUI.progress.start();
	var url = g_classnews_url+"?uuid="+classuuid+"&pageNo="+pageNo;
	$.ajax({
		type : "GET",
		url : url,
		dataType : "json",
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				React.render(React.createElement(Classnews_EventsTable, {
					events: data.list,
					class_list:g_classnews_class_list,
					handleClick:btn_click_classnews,
					responsive: true, bordered: true, striped :true,hover:true,striped:true
					}), document.getElementById('div_body'));
				
			} else {
				alert(data.ResMsg.message);
			}
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			alert(url+","+textStatus+"="+errorThrown);
			 console.log(url+',error：', obj);
			 console.log(url+',error：', textStatus);
			 console.log(url+',error：', errorThrown);
		}
	});
};

//classnews end
