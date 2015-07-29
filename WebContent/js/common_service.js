


function menu_userinfo_updatePasswordBySms_fn(){
	Queue.push(menu_userinfo_updatePasswordBySms_fn);
	
	React.render(React.createElement(Div_userinfo_updatePasswordBySms,null)
			, document.getElementById('div_login'));
}
function ajax_sms_sendCode_byReset(){
	  var url = hostUrl + "rest/sms/sendCode.json";
		$.ajax({
			type : "GET",
			url : url,
			dataType : "json",
			data:{tel:$("#tel").val(),type:2},
			 async: true,
			success : function(data) {
				$.AMUI.progress.done();
				// 登陆成功直接进入主页
				if (data.ResMsg.status == "success") {
					 G_msg_pop(data.ResMsg.message);
				} else {
					alert("验证码发送失败："+data.ResMsg.message);
				}
			},
			error : function( obj, textStatus, errorThrown ){
				$.AMUI.progress.done();
				alert(url+",error:"+textStatus);
			}
		});
}


//用户登陆
function ajax_userinfo_updatePasswordBySms() {
	$.AMUI.progress.start();
	
	// var data = $("#form1").serializeArray(); //自动将form表单封装成json
//alert(JSON.stringify(data));
	  var objectForm = $('#commonform').serializeJson();
	  if(!objectForm.tel){
		  alert("请输入手机号码!");
		  return;
	  }
	  if(!objectForm.smscode){
		  alert("请输入短信验证码!");
		  return;
	  }
	  if(objectForm.password!=objectForm.password1){
		  alert("2次输入密码不匹配!");
		  return;
	  }
	  
	  delete objectForm.password1;
	  objectForm.password=$.md5(objectForm.password); 
var jsonString=JSON.stringify(objectForm);
var url = hostUrl + "rest/userinfo/updatePasswordBySms.json";
			
	$.ajax({
		type : "POST",
		url : url,
		processData: false, 
		data : jsonString,
		dataType : "json",
		contentType : false,  
		success : function(data) {
			$.AMUI.progress.done();
			// 登陆成功直接进入主页
			if (data.ResMsg.status == "success") {
				G_msg_pop(data.ResMsg.message);
				Queue.doBackFN();
			} else {
				alert(data.ResMsg.message);
			}
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			alert("error:"+textStatus);
		}
	});
}


//获取班级信息公用模板方法 return 出去做
//dianzan/getByNewsuuid
function commons_ajax_dianzan_getByNewsuuid(newsuuid){
	var reObj=[];
	$.AMUI.progress.start();
  var url = hostUrl + "rest/dianzan/getByNewsuuid.json?newsuuid="+newsuuid;
	$.ajax({
		type : "GET",
		url : url,
		dataType : "json",
		 async: false,
		success : function(data) {
			$.AMUI.progress.done();
			// 登陆成功直接进入主页
			if (data.ResMsg.status == "success") {
				reObj=data;
			} else {
				alert("加载数据失败："+data.ResMsg.message);
			}
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			alert(url+",error:"+textStatus);
		}
	});
	
	return reObj;
};
/*
* 点赞功能模板服务器请求
* @canDianzan：根据Data数据中的是否可以点赞判断进行请求 ;
* True表示可以点赞,false表示点赞过了.可以取消点赞;
* @newsuuid:哪篇文章的ID;
* @type:哪个模板的点赞功能;
* @that.forceUpdate():点赞或取消点赞在数据返回后强制刷新当前页面的方法;
*/
function common_ajax_dianzan_save(newsuuid,type,canDianzan){
	var that=this;
	var objectForm={newsuuid:newsuuid,type:type,canDianzan:canDianzan};
	var jsonString=JSON.stringify(objectForm);
	$.AMUI.progress.start();
	var url =hostUrl +(canDianzan?"rest/dianzan/save.json":"rest/dianzan/delete.json");
		$.ajax({
			type : "POST",
			url : url,
			processData: false, 
			data : jsonString,
			dataType : "json",
			contentType : false,  
			success : function(data) {
				$.AMUI.progress.done();
				// 登陆成功直接进入主页
				if (data.ResMsg.status == "success") {
					that.forceUpdate();

					//$('#dianzan').html($('#dianzan').html()+', <a href="javascript:void(0);">'+Store.getUserinfo().name+'</a>');
				} else {
					alert(data.ResMsg.message);
					G_resMsg_filter(data.ResMsg);
				}
			},
			error : function( obj, textStatus, errorThrown ){
				$.AMUI.progress.done();
				alert(url+",error:"+textStatus);
				 console.log(url+',error：', obj);
				 console.log(url+',error：', textStatus);
				 console.log(url+',error：', errorThrown);
			}
		});
	
	
}



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