
window.onload = function() {
	init();
}

//如果之前已经记住密码，需要将账号密码填充到输入框中
function init() {
	var username = getCookie("bs_loginname");
	var password = getCookie("bs_password");
	var password_checked = getCookie("password_checked");
	
	if(username && username != "") {
		$("#loginname").attr("value",username);
	}
	
	if(password_checked && password_checked == "checked") {
		$("#password").attr("value",password);
		$("#remember-me").attr("checked",'true');
	}
}
//用户登陆
function login() {
	
	 var $btn = $("#btn_login");
	  $btn.button('loading');
//	    setTimeout(function(){
//	      $btn.button('reset');
//	  }, 5000);
//	    
	
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
				//判断是否保存密码，如果保存则放入cookie，否则清除cookie
				setCookie("bs_loginname", loginname);
				if($("#remember-me")[0].checked){
					setCookie("bs_password", password);
					setCookie("password_checked", "checked");
				} else {
					setCookie("bs_password", ""); 
					setCookie("password_checked", "");
				}
				//data.userinfo.name;
				Store.setUserinfo(data.userinfo);
				Store.setGroup(data.list);
				window.location = hostUrl + "admin/index.html";
				
				
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
// 实现回车键进行登陆功能
document.onkeydown = function(event) {
	var e = event || window.event || arguments.callee.caller.arguments[0];
	if (e && e.keyCode == 13) {
		login();
	}
};

//获取cookie
function getCookie(cookie_name) {
	var allcookies = document.cookie;
	var cookie_pos = allcookies.indexOf(cookie_name); // 索引的长度
	// 如果找到了索引，就代表cookie存在，
	// 反之，就说明不存在。
	if (cookie_pos != -1) {
		// 把cookie_pos放在值的开始，只要给值加1即可。
		cookie_pos_i = cookie_pos + cookie_name.length;
		//兼容ie9、ie8，
		if(allcookies.substring(cookie_pos_i, cookie_pos_i+1) != "=") {
			return "";
		}
		cookie_pos += cookie_name.length + 1; // 这里我自己试过，容易出问题，所以请大家参考的时候自己好好研究一下。。。
		
		var cookie_end = allcookies.indexOf(";", cookie_pos);
		if (cookie_end == -1) {
			cookie_end = allcookies.length;
		}
		var value = unescape(allcookies.substring(cookie_pos, cookie_end)); // 这里就可以得到你想要的cookie的值了。。。
	}
	return value;
}

// 设置cookie
function setCookie(name, value) {
	var timeout = 60*60*24*30; // 设置cookie超时时间为30天。
	var expires = "";
	if (timeout != 0) { // 设置cookie生存时间
		var date = new Date();
		date.setTime(date.getTime() + (timeout * 1000));
		expires = "; expires=" + date.toGMTString();
	}
	document.cookie = name + "=" + escape(value) + expires + "; path=/"; // 转码并赋值
}

//时间工具
Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}