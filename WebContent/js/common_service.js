/**
 * 公用模板
 *1我的头像,2:菜谱
* w_uploadImg.open(callbackFN,type);
* w_uploadImg.base64='data:image/png;base64,iVBORw0KG...'

 * 保存通用方法
var opt={
	 formName:"editClassnewsForm",
	 url:hostUrl + "rest/classnewsreply/save.json",
	 cbFN:null,
	 }
	 G_ajax_abs_save(opt);
 */
function G_ajax_abs_save(opt){
$.AMUI.progress.start();
	
	  var objectForm = $('#'+opt.formName).serializeJson();
	  var jsonString=JSON.stringify(objectForm);
	  var async=true;
	  if(opt.async===false){
		  async=opt.async;
	  }
	$.ajax({
		type : "POST",
		url : opt.url,
		processData: false, //设置 processData 选项为 false，防止自动转换数据格式。
		data:jsonString,
		dataType : "json",
		contentType : false, 
		async:async,
		success : function(data) {
			$.AMUI.progress.done();
			// 登陆成功直接进入主页
			if (data.ResMsg.status == "success") {
				if(opt.cbFN){
					opt.cbFN(data);
				}else{
					G_msg_pop(data.ResMsg.message);
					Queue.doBackFN();
				}
				
			} else {
				alert(data.ResMsg.message);
			}
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			 alert(opt.url+",error:"+textStatus);
			 console.log(opt.url+',error：', obj);
			 console.log(opt.url+',error：', textStatus);
			 console.log(opt.url+',error：', errorThrown);
		}
	});
}

/**
 * 上传不用裁剪的图片,只是压缩上传
 */
w_img_upload_nocut={
		div_id:"div_widget_chooseUser",
		div_body:"div_body",
		cropper:null,
		callbackFN:null,
		type:4,
		base64:null,
		bind_onchange:function(fileId,callbackFN){
			w_img_upload_nocut.callbackFN=callbackFN;
			if(window.JavaScriptCall){
				$(fileId).bind("click", function(){
					//优先调用手机
	            	G_CallPhoneFN.selectImgPic();
				});
				return;
			}
			$(fileId).bind("change", function(){
				 lrz(this.files[0], {
			            before: function() {
			                console.log('压缩开始');
			            },
			            fail: function(err) {
			                console.error(err);
			            },
			            always: function() {
			                console.log('压缩结束');
			            },
			            done: function (results) {
			            // 你需要的数据都在这里，可以以字符串的形式传送base64给服务端转存为图片。
			            console.log(results);
			            /*
			            var data = {
			                    base64: results.base64,
			                    size: results.base64.length // 校验用，防止未完整接收
			                };*/
				            if(results&&results.base64){
				            	
				            	w_img_upload_nocut.ajax_uploadByphone(results.base64);
				            }
			            }
			            });//end done fn
			
				
				
			  });//end change
		},
		ajax_uploadByphone:function(base64){
			$.AMUI.progress.start();
		    var url = hostUrl + "rest/uploadFile/uploadBase64.json";
			$.ajax({
				type : "POST",
				url : url,
				dataType : "json",
				data:{type:w_img_upload_nocut.type,base64:base64},
				 async: true,
				success : function(data) {
					$.AMUI.progress.done();
					// 登陆成功直接进入主页
					if (data.ResMsg.status == "success") {
						if(w_img_upload_nocut.callbackFN){
							w_img_upload_nocut.callbackFN(data.imgUrl);
						}
					} else {
						alert(data.ResMsg.message);
					}
				},
				error : function( obj, textStatus, errorThrown ){
					$.AMUI.progress.done();
					alert(url+",error:"+textStatus);
				}
			});
			
		}
};
//uploadImg
/**1我的头像,2:菜谱
* w_uploadImg.open(callbackFN,type);
* w_uploadImg.base64='data:image/png;base64,iVBORw0KG...'
* 
* w_uploadImg.base64.ajax_uploadByphone(base64);
*/
var w_uploadImg={
		div_id:"div_widget_chooseUser",
		div_body:"div_body",
		cropper:null,
		callbackFN:null,
		type:1,
		base64:null,
		ajax_uploadByphone:function(base64){
			$.AMUI.progress.start();
		    var url = hostUrl + "rest/uploadFile/uploadBase64.json";
			$.ajax({
				type : "POST",
				url : url,
				dataType : "json",
				data:{type:w_uploadImg.type,base64:base64},
				 async: true,
				success : function(data) {
					$.AMUI.progress.done();
					// 登陆成功直接进入主页
					if (data.ResMsg.status == "success") {
						if(w_uploadImg.callbackFN){
							w_uploadImg.callbackFN(data.data.uuid);
						}
						w_uploadImg.hide();
						
					} else {
						alert(data.ResMsg.message);
					}
				},
				error : function( obj, textStatus, errorThrown ){
					$.AMUI.progress.done();
					alert(url+",error:"+textStatus);
				}
			});
			
		},
		ajax_upload:function(){
			$.AMUI.progress.start();
		    var url = hostUrl + "rest/uploadFile/uploadBase64.json";
			$.ajax({
				type : "POST",
				url : url,
				dataType : "json",
				data:{type:w_uploadImg.type,base64:w_uploadImg.base64},
				 async: true,
				success : function(data) {
					$.AMUI.progress.done();
					// 登陆成功直接进入主页
					if (data.ResMsg.status == "success") {
						if(w_uploadImg.callbackFN){
							w_uploadImg.callbackFN(data.data.uuid);
						}
						w_uploadImg.hide();
						
					} else {
						alert(data.ResMsg.message);
					}
				},
				error : function( obj, textStatus, errorThrown ){
					$.AMUI.progress.done();
					alert(url+",error:"+textStatus);
				}
			});
			
		},
		handleClick: function(m) {
			if("cancel"==m){
				w_uploadImg.hide();
				return;
				
			} if("ok"==m){
				if(w_uploadImg.base64==null){
					alert("请先剪切图片，在提交。");
					return;
				}
				w_uploadImg.ajax_upload();
				
			}
			 
     	  },
		open:function(callbackFN,type){
			if(!type)type=1;
			w_uploadImg.type=type;
			w_uploadImg.base64=null;
			w_uploadImg.callbackFN=callbackFN;
			if(G_CallPhoneFN.selectHeadPic())return;
			w_uploadImg.show();
		},
		show:function(){
			
			React.render(React.createElement(Upload_headImg, {
				responsive: true, bordered: true, striped :true,hover:true,striped:true
				}), document.getElementById(w_ch_user.div_id));
			$("#"+this.div_body).hide();
			$("#"+this.div_id).show();
			
		},
		
		hide:function(callbackFN){
			$("#"+this.div_body).show();
			$("#"+this.div_id).html("");
		}	
}
function menu_userinfo_updatepassword_fn(){
	Queue.push(menu_userinfo_updatepassword_fn);
	
	React.render(React.createElement(Div_userinfo_updatepassword,null)
			, document.getElementById('div_body'));
}

//用户登陆
function ajax_userinfo_updatepassword() {
	$.AMUI.progress.start();
	
	// var data = $("#form1").serializeArray(); //自动将form表单封装成json
//alert(JSON.stringify(data));
	  var objectForm = $('#commonform').serializeJson();
	  if(objectForm.password!=objectForm.password1){
		  alert("2次输入密码不匹配");
		  return;
	  }
	  delete objectForm.password1;
	  objectForm.oldpassword=$.md5(objectForm.oldpassword); 
	  objectForm.password=$.md5(objectForm.password); 
  var jsonString=JSON.stringify(objectForm);
  var url = hostUrl + "rest/userinfo/updatepassword.json";
			
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
function ajax_userinfo_getRole(useruuid,usernames,roleList){
	$.AMUI.progress.start();
	var url = hostUrl + "rest/userinfo/getRole.json?userUuid="+useruuid;
	$.ajax({
		type : "GET",
		url : url,
		dataType : "json",
		async: false,
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				
				React.render(React.createElement(Userinfo_getRole, {
					formdata:{useruuid:useruuid,username:usernames},
					events: roleList,
					chooselist: JSON.stringify(data.list),
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
/*
 * 老师管理Button事件(启用和禁用按钮功能)；
 * @useruuids:选中的老师对象；
 * @disable：是启用还是禁用功能；1-启用  0-禁用；
 * @ajax_uesrinfo_listByGroup：服务器请求处理结束后调回最初方法做数据更新；
 * */
function ajax_userinfo_updateDisable(useruuids,disable){
	var groupuuid=$('#selectgroup_uuid').val();
	$.AMUI.progress.start();
	      var url = hostUrl + "rest/userinfo/updateDisable.json";
		$.ajax({
			type : "POST",
			url : url,
			data : {useruuids:useruuids,disable:disable},
			dataType : "json",
			success : function(data) {
				$.AMUI.progress.done();
				// 登陆成功直接进入主页
				if (data.ResMsg.status == "success") {
					G_msg_pop(data.ResMsg.message);
					ajax_uesrinfo_listByGroup(groupuuid);
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


function menu_userinfo_update_fn(){
	Queue.push(menu_userinfo_update_fn);
	
	React.render(React.createElement(Div_userinfo_update,{formdata:Store.getUserinfo()})
			, document.getElementById('div_body'));
}

function ajax_userinfo_update() {
	var opt={
			 formName:"commonform",
			 url:hostUrl + "rest/userinfo/update.json",
			 cbFN:function(data){
				 G_msg_pop(data.ResMsg.message);
				 store_ajax_getUserinfo();
				 menu_body_fn();
			 },
			 };
	G_ajax_abs_save(opt);
	
}


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
//我要评论保存操作
function common_ajax_reply_save(callback){
	var opt={
	 formName:"editClassnewsreplyForm",
	 url:hostUrl + "rest/reply/save.json",
	 cbFN:function(data){
		 G_msg_pop(data.ResMsg.message);
		 if(callback)callback();
	 }
	 };
	 G_ajax_abs_save(opt);
}
