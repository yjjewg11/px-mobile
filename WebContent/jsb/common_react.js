var AMR_Table=AMUIReact.Table;
var AMR_ButtonToolbar=AMUIReact.ButtonToolbar;
var AMR_Button=AMUIReact.Button;
var AMR_Sticky=AMUIReact.Sticky;
var AMR_Panel=AMUIReact.Panel;
var AMR_Gallery=AMUIReact.Gallery;
var AMR_Input=AMUIReact.Input;

var G_upload_img_Div=React.createElement(AMR_Input, {type: "file", label: "上传图片", id: "file_img_upload", help: "选择图片", accept: "image/*", capture: "camera"})
if(window.JavaScriptCall){
	G_upload_img_Div=React.createElement(AMR_Button, {amStyle: "primary", id: "file_img_upload", round: true}, "上传图片")
}
//userinfo
var Userinfo_EventRow = React.createClass({displayName: "Userinfo_EventRow", 
  render: function() {
    var event = this.props.event;
    var className = event.highlight ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      React.createElement("tr", {className: className}, 
      React.createElement("td", null, 
      React.createElement("input", {type: "checkbox", value: event.uuid, alt: event.name, name: "table_checkbox"})
      ), 
        React.createElement("td", null, event.loginname), 
        React.createElement("td", null, event.name), 
        React.createElement("td", null, event.tel), 
        React.createElement("td", null, event.email), 
        React.createElement("td", null, event.sex=="0"?"男":"女"), 
        React.createElement("td", {className: "px_disable_"+event.disable}, Vo.get("disable_"+event.disable)), 
        React.createElement("td", null, event.login_time), 
        React.createElement("td", null, event.create_time)
      ) 
    );
  }
}); 
/*
 * 老师管理服务器请求后绘制处理方法；
 * @逻辑：如果点击的不是添加按钮，则先检查是否勾选选框再处理其他判断；
 * @btn_click_userinfo：判断后程序跳转至d_service做各个按钮的处理; 
 * @调用LIS.events.map方法循环绘制老师数组； 
 * @</select>下拉多选框;
 * */
var Userinfo_EventsTable = React.createClass({displayName: "Userinfo_EventsTable",
	handleClick: function(m) {
		
		 if(m=="add"){
			 btn_click_userinfo(m,{group_uuid:$('#selectgroup_uuid').val(),office:"老师"});
			 return;
		 }
		 var uuids=null;
		 var usernames=null;
		 $($("input[name='table_checkbox']")).each(function(){
			　if(this.checked){
				 if(uuids==null){
					 uuids=this.value;
					 usernames=this.alt;
				 }
				 else{
					 uuids+=','+this.value ;  
					 usernames+=','+this.alt;
				 }
			　}
			});
		  if(!uuids){
			  alert("请勾选复选框！");
			  return;
		  }
		  if(m=="getRole"){
			  if(!uuids&&uuids.indexOf(",")>-1){
					alert("只能选择一个！");
					return;
				}
		  }
		  btn_click_userinfo(m,uuids,usernames);
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
	  },
	  handleChange_selectgroup_uuid:function(){
		  this.props.handleChange_selectgroup_uuid($('#selectgroup_uuid').val());
	  },
  render: function() {
    return (
    React.createElement("div", null, 
    React.createElement("div", {className: "header"}, 
    React.createElement("div", {className: "am-g"}, 
      React.createElement("h1", null, "用户管理")
    ), 
    React.createElement("hr", null)
    ), 
    React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add"), round: true}, "添加"), 
	    React.createElement(AMR_Button, {amStyle: "success", onClick: this.handleClick.bind(this, "enable"), round: true}, "启用"), 
	    React.createElement(AMR_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "disable"), round: true}, "禁用"), 
	    React.createElement(AMR_Button, {amStyle: "success", onClick: this.handleClick.bind(this, "getRole"), round: true}, "分配权限"), 
	    React.createElement(AMR_Button, {amStyle: "revise", onClick: this.handleClick.bind(this, "edit"), round: true}, "修改")
	    ), 
	  React.createElement("hr", null), 
	  React.createElement("div", {className: "am-form-group"}, 
      React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'lg'}", value: this.props.group_uuid, onChange: this.handleChange_selectgroup_uuid}, 
      this.props.group_list.map(function(event) {
          return (React.createElement("option", {value: event.uuid}, event.company_name));
        })
      )
    ), 
	  
      React.createElement(AMR_Table, React.__spread({},  this.props), 
        React.createElement("thead", null, 
          React.createElement("tr", null, 
          	React.createElement("th", null, 
            React.createElement("input", {type: "checkbox", id: "id_checkbox_all", onChange: this.handleChange_checkbox_all})
            ), 
            React.createElement("th", null, "帐号"), 
            React.createElement("th", null, "姓名"), 
            React.createElement("th", null, "电话"), 
            React.createElement("th", null, "邮箱"), 
            React.createElement("th", null, "性别"), 
            React.createElement("th", null, "状态"), 
            React.createElement("th", null, "登录时间"), 
            React.createElement("th", null, "创建时间")
          )
        ), 
        React.createElement("tbody", null, 
          this.props.events.map(function(event) {
            return (React.createElement(Userinfo_EventRow, {key: event.id, event: event}));
          })
        )
      )
      )
    );
  }
});
/*
* 老师管理Button事件(添加和修改按钮绘制与标签事件处理)；
* @formdata:选中的老师对象；
* @m：是启用还是禁用功能；"add"-添加  "edit"-修改；
* */    
var Userinfo_edit = React.createClass({displayName: "Userinfo_edit", 
	 getInitialState: function() {
			if(this.props.mygroup_uuids)this.props.formdata.group_uuid=this.props.mygroup_uuids;
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editUserinfoForm').serializeJson());
	  },
	  handleChange_Selected: function(event) {
			 $('#group_uuid').val(event);
			    this.setState($('#editUserinfoForm').serializeJson());
		  },
  render: function() {
	  var o = this.state;
	  var passwordDiv=null;
	  if(!o.uuid){
		  passwordDiv=(
				  React.createElement("div", null, 
				  React.createElement("label", {htmlFor: "password"}, "密码:"), 
    		      React.createElement("input", {type: "password", name: "password", id: "password", value: o.password, onChange: this.handleChange}), 
    		      React.createElement("br", null), 
    		      
    		      React.createElement("label", {htmlFor: "password1"}, "重复密码:"), 
    		      React.createElement("input", {type: "password", name: "password1", id: "password1", value: o.password1, onChange: this.handleChange}), 
    		      React.createElement("br", null)
				  )
				  );
	  }
	  
    return (
    		React.createElement("div", null, 
    		React.createElement("div", {className: "header"}, 
    		  React.createElement("div", {className: "am-g"}, 
    		    React.createElement("h1", null, "编辑")
    		  ), 
    		  React.createElement("hr", null)
    		), 
    		React.createElement("div", {className: "am-g"}, 
    		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
    		  React.createElement("form", {id: "editUserinfoForm", method: "post", className: "am-form"}, 
    			React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
    		     React.createElement("input", {type: "hidden", name: "type", value: "1"}), 
    			 React.createElement("input", {type: "hidden", id: "group_uuid", name: "group_uuid", value: ""}), 
    		    React.createElement("div", {className: "am-form-group"}, 
    		    React.createElement(AMUIReact.Selected, {name: "group_uuid", onChange: this.handleChange_Selected, btnWidth: "300", multiple: true, data: this.props.select_group_list, btnStyle: "primary", value: o.group_uuid})
    		        ), 
    		      React.createElement("label", {htmlFor: "tel"}, "手机号码:"), 
    		      React.createElement("input", {type: "text", name: "tel", id: "tel", value: o.tel, onChange: this.handleChange, placeholder: ""}), 
    		      React.createElement("br", null), 
    		      React.createElement("label", {htmlFor: "name"}, "姓名:"), 
    		      React.createElement("input", {type: "text", name: "name", id: "name", value: o.name, onChange: this.handleChange, placeholder: "不超过15位"}), 
    		      React.createElement("br", null), 
    		       React.createElement("label", {htmlFor: ""}, "Email:"), 
    		      React.createElement("input", {type: "email", name: "email", id: "email", value: o.email, onChange: this.handleChange, placeholder: "输入邮箱", placeholder: ""}), 
    		      React.createElement("br", null), 
    		      passwordDiv, 
    		      React.createElement("label", {htmlFor: "office"}, "职位:"), 
    		      React.createElement("input", {type: "text", name: "office", id: "office", value: o.office, onChange: this.handleChange}), 
    		      React.createElement("br", null), 
    		      React.createElement("button", {type: "button", onClick: ajax_userinfo_saveByAdmin, className: "am-btn am-btn-primary"}, "提交")
    		    )

    	     )
    	   )
    	   
    	   )
    );
  }
}); 

//Userinfo_getRole


var Userinfo_getRole = React.createClass({displayName: "Userinfo_getRole", 
	
	render: function() {
		  var o = this.props.formdata;
	  return (
	  		React.createElement("div", null, 
		  		React.createElement("div", {className: "header"}, 
			  		  React.createElement("div", {className: "am-g"}, 
			  		    React.createElement("h1", null, "用户绑定角色-【", o.username, "】")
			  		  )
		  		), 
	  			React.createElement("button", {type: "button", onClick: btn_ajax_updateRole.bind(this, o.useruuid), className: "am-btn am-btn-primary"}, "提交"), 
		  		React.createElement(UserChooseRole_EventsTable, React.__spread({},  this.props))
		  	   
	  	   )
	  );
	}
	}); 




//Div_userinfo_updatepassword
var Div_userinfo_updatepassword = React.createClass({displayName: "Div_userinfo_updatepassword", 
	
	render: function() {
	return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "修改密码")
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g"}, 
		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		    React.createElement("form", {id: "commonform", method: "post", className: "am-form"}, 

		      React.createElement("label", {htmlFor: "oldpassword"}, "当前密码:"), 
		      React.createElement("input", {type: "password", name: "oldpassword"}), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "password"}, "密码:"), 
		      React.createElement("input", {type: "password", name: "password"}), 
		      React.createElement("br", null), 
		      
		      React.createElement("label", {htmlFor: "password1"}, "重复密码:"), 
		      React.createElement("input", {type: "password", name: "password1"}), 
		      React.createElement("br", null), 
		      React.createElement("button", {type: "button", onClick: ajax_userinfo_updatepassword, className: "am-btn am-btn-primary"}, "提交")
		    ), 
		    React.createElement("hr", null)
		  
		  )
		)
		)
	);
	}
}); 


//userinfo update
var Div_userinfo_update = React.createClass({displayName: "Div_userinfo_update", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#commonform').serializeJson());
	  },
	 handle_uploadHeader: function(event) {
			w_uploadImg.open(function(guid){
				$("#img").val(guid);
				 $("#img_head_image").attr("src",G_imgPath+guid); 
				 G_img_down404("#img_head_image");
			},1);
	  },
	  componentDidMount:function(){
		  var imgGuid=this.state.img;
		 if(imgGuid){
			 $("#img_head_image").attr("src",G_imgPath+imgGuid); 
			 G_img_down404("#img_head_image");
		 }

	  },
	render: function() {
		 var o = this.state;
	return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "修改资料")
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g"}, 
		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		  
		    React.createElement("form", {id: "commonform", method: "post", className: "am-form"}, 
			React.createElement("input", {type: "hidden", name: "img", id: "img", value: o.img, onChange: this.handleChange}), 
		    React.createElement("label", {htmlFor: "nickname"}, "头像:"), 
 		    React.createElement(AMUIReact.Image, {id: "img_head_image", src: G_def_headImgPath, className: "G_img_header"}), 
 		
 		   React.createElement("button", {type: "button", onClick: this.handle_uploadHeader, className: "am-btn am-btn-primary"}, "上传头像"), 
 		   React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "name"}, "姓名:"), 
		      React.createElement("input", {type: "text", name: "name", id: "name", value: o.name, onChange: this.handleChange, placeholder: "必填，不超过15位"}), 
		      React.createElement("br", null), 
		       React.createElement("label", {htmlFor: ""}, "Email:"), 
		      React.createElement("input", {type: "email", name: "email", id: "email", value: o.email, onChange: this.handleChange, placeholder: "输入邮箱"}), 
		      React.createElement("br", null), 
		      React.createElement("label", null, "性别:"), 
		      React.createElement("div", {className: "am-form-group"}, 
		      React.createElement(AMUIReact.UCheck, {type: "radio", name: "sex", label: "男", value: "0", inline: true, defaultChecked: true, checked: o.sex==0, onChange: this.handleChange}), 
		      React.createElement(AMUIReact.UCheck, {type: "radio", name: "sex", label: "女", value: "1", inline: true, checked: o.sex==1, onChange: this.handleChange})
		      ), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "office"}, "职位:"), 
		      React.createElement("input", {type: "text", name: "office", id: "office", value: o.office, onChange: this.handleChange, placeholder: "必填，不超过15位"}), 
		      React.createElement("br", null), 
		      React.createElement("button", {type: "button", onClick: ajax_userinfo_update, className: "am-btn am-btn-primary"}, "提交")
		    ), 
		    React.createElement("hr", null)
		  
		  )
		)
		)
	);
	}
}); 

//userinfo update end

//role
var UserChooseRole_EventRow = React.createClass({displayName: "UserChooseRole_EventRow", 
	tr_onClick:function(trid,cbid){
		var cbox=$("#"+cbid);
		var tr=$("#"+trid);
		if(cbox.prop("checked")){
			cbox.prop("checked",false); 
			$(tr).removeClass("am-active");
		}else{
			cbox.prop("checked", true); 
			$(tr).addClass("am-active");
		}
	},
  render: function() {
    var event = this.props.event;
    var is_Checked=this.props.chooselist&&this.props.chooselist.indexOf(event.uuid)>-1;
    var className = is_Checked ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
		 React.createElement("tr", {name: "table_tr_checkbox", name: "table_tr_checkbox", id: "tr_chright_"+event.uuid, className: className, onClick: this.tr_onClick.bind(this,"tr_chright_"+event.uuid,"tb_cbox__chright"+event.uuid)}, 
	      React.createElement("td", null, 
	      React.createElement("input", {type: "checkbox", alt: event.name, value: event.uuid, id: "tb_cbox__chright"+event.uuid, name: "table_checkbox", checked: is_Checked?"checked":""})
	      ), 
    React.createElement("td", null, event.name), 
    React.createElement("td", null, event.description), 
    React.createElement("td", null, Vo.type(event.type))
  ) 
);
}
}); 

var UserChooseRole_EventsTable = React.createClass({displayName: "UserChooseRole_EventsTable",
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
		  if( $("#id_checkbox_all")[0].checked){
			  $('tr[name="table_tr_checkbox"]').addClass("am-active");
		  }else{
			  $('tr[name="table_tr_checkbox"]').removeClass("am-active");
		  }
	  },
render: function() {
	  var that=this;
return (
React.createElement("div", null, 

  React.createElement(AMUIReact.Table, React.__spread({},  this.props), 
    React.createElement("thead", null, 
      React.createElement("tr", null, 
      	React.createElement("th", null, 
        React.createElement("input", {type: "checkbox", id: "id_checkbox_all", onChange: this.handleChange_checkbox_all})
        ), 
        React.createElement("th", null, "名称"), 
        React.createElement("th", null, "描述"), 
        React.createElement("th", null, "类型")
      )
    ), 
    React.createElement("tbody", null, 
      this.props.events.map(function(event) {
        return (React.createElement(UserChooseRole_EventRow, {chooselist: that.props.chooselist, event: event}));
      })
    )
  )
  )
);
}
});
//end Userinfo_getRole



//Div_userinfo_updatePasswordBySms
var Div_userinfo_updatePasswordBySms = React.createClass({displayName: "Div_userinfo_updatePasswordBySms", 
	
	render: function() {
	return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "重置密码")
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g"}, 
		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		    React.createElement("form", {id: "commonform", method: "post", className: "am-form"}, 

		    React.createElement("label", {htmlFor: "tel"}, "手机号码:"), 
		      React.createElement("input", {type: "text", name: "tel", id: "tel", placeholder: ""}), 
		      React.createElement("button", {type: "button", onClick: ajax_sms_sendCode_byReset, className: "am-btn am-btn-primary"}, "发送验证码"), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "smscode"}, "验证码:"), 
		      React.createElement("input", {type: "text", name: "smscode", id: "smscode", placeholder: ""}), 
		    
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "password"}, "密码:"), 
		      React.createElement("input", {type: "password", name: "password"}), 
		      React.createElement("br", null), 
		      
		      React.createElement("label", {htmlFor: "password1"}, "重复密码:"), 
		      React.createElement("input", {type: "password", name: "password1"}), 
		      React.createElement("br", null), 
		      React.createElement("button", {type: "button", onClick: ajax_userinfo_updatePasswordBySms, className: "am-btn am-btn-primary"}, "提交")
		    ), 
		    React.createElement("hr", null)
		  
		  )
		)
		)
	);
	}
}); 



//upload headImg
var Upload_headImg_options =
{
    thumbBox: '.thumbBox',
    spinner: '.spinner',
    imgSrc: ''
};
var Upload_headImg = React.createClass({displayName: "Upload_headImg",
   	handleClick: function(m) {
   		w_uploadImg.handleClick(m);
   	  },
   	upload_file_onChange:function(){
   	  var reader = new FileReader();
      reader.onload = function(e) {
    	  Upload_headImg_options.imgSrc = e.target.result;
          w_uploadImg.cropper = $('#upload_file_imageBox').cropbox(Upload_headImg_options);
      }
      reader.readAsDataURL(this.files[0]);
      this.files = [];
   	},
   	btnZoomIn_onClick: function(){
   		if(w_uploadImg.cropper)w_uploadImg.cropper.zoomIn();
    },
    btnZoomOut_onClick: function(){
    	 if(w_uploadImg.cropper)w_uploadImg.cropper.zoomOut();
   },
   btnRotate_onClick: function(){
    	 if(w_uploadImg.cropper)w_uploadImg.cropper.chRotate();
   },
   btnCrop_onClick: function(){
	   var img = w_uploadImg.cropper.getDataURL();
	   w_uploadImg.base64=img;
       $('#upload_file_imageBox_cropped').html('<img src="'+img+'">');
   },
   	 componentDidMount:function(){
       $('#upload_imgfile').on('change', function(){
           var reader = new FileReader();
           reader.onload = function(e) {
        	   Upload_headImg_options.imgSrc = e.target.result;
        	   w_uploadImg.cropper = $('.imageBox').cropbox(Upload_headImg_options);
           }
           reader.readAsDataURL(this.files[0]);
           this.files = [];
       })

         
	  },
     render: function() {
    	 var spinner_divStyle={
    			 display: "none"
    	 };
       return (
       React.createElement("div", null, 
  	
     React.createElement("div", {className: "header"}, 
     React.createElement("div", {className: "am-g"}, 
       React.createElement("h1", null, "上传图片")
     ), 
     React.createElement("hr", null)
   ), 
   React.createElement("div", {className: "container"}, 

   	React.createElement("div", {className: "imageBox", id: "upload_file_imageBox"}, 
   	    React.createElement("div", {className: "thumbBox"}), 
   	    React.createElement("div", {className: "spinner", style: spinner_divStyle}, "加载中...")
   	), 
	React.createElement("div", {className: "action"}, 
	    React.createElement("input", {type: "file", id: "upload_imgfile", accept: "image/*"}), 
	 React.createElement(AMUIReact_Button, {amStyle: "warning", onClick: this.btnCrop_onClick, round: true}, "剪切"), 
	 React.createElement(AMUIReact_Button, {amStyle: "warning", onClick: this.btnZoomIn_onClick, round: true}, "放大"), 
	 React.createElement(AMUIReact_Button, {amStyle: "warning", onClick: this.btnZoomOut_onClick, round: true}, "缩小")

	), 
		React.createElement("div", {className: "cropped", id: "upload_file_imageBox_cropped"}
	   	)
	), 

React.createElement(AMUIReact_ButtonToolbar, null, 
React.createElement(AMUIReact_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "ok"), round: true}, "确认"), 
React.createElement(AMUIReact_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "cancel"), round: true}, "取消")
)
         )
       );
     }
});
       
       
//end uploadImg


//点赞模板
/*
 * 
 * 
 *@bind（this）方法中This代表对象前一步函数构造成对象传过来; 
 **/
var Common_Dianzan_show = React.createClass({displayName: "Common_Dianzan_show", 

render: function() {	
	 var dianzanObject=commons_ajax_dianzan_getByNewsuuid(this.props.uuid);
  return (
		   React.createElement("div", {id: "dianzan", class: "dianzan"}, "♡", 
		   dianzanObject.names, ",等一共", dianzanObject.count, "人点赞", 
		   React.createElement(AMUIReact.Button, {onClick: common_ajax_dianzan_save.bind(this,this.props.uuid,this.props.type,dianzanObject.canDianzan), amStyle: dianzanObject.canDianzan?"primary":"warning"}, dianzanObject.canDianzan?"点赞":"取消点赞")		   
		   )
//		   <div id="delete_dianzan" class="delete_dianzan">
//		   {dianzanList.map(function(event) {
//			      return (
//			    		  <a href="javascript:void(0);">,{event.create_user}</a>
//			    		  )
//			  })}		   
//		   <button type="button2"  onClick={common_ajax_dianzan_save.bind(this,this.props.uuid,this.props.type)}  className="am-btn am-btn-primary">取消点赞</button>
		   
  );
}
}); 


var Common_Classnewsreply_listshow = React.createClass({displayName: "Common_Classnewsreply_listshow", 
	
render: function() {
  return (
		  React.createElement("div", null, 
		  this.props.events.data.map(function(event) {
		      return (
		    		  React.createElement("div", {className: "event"}, 
		  		 React.createElement("div", {dangerouslySetInnerHTML: {__html: event.content}}), 
		  		 React.createElement("strong", null, event.create_user+" | "+event.update_time)
		  		 )
		    		  )
		  })
		
		    )
		   
  );
}
}); 
//评论模板
var Common_reply_list = React.createClass({displayName: "Common_reply_list", 
	load_more_btn_id:"load_more_",
	pageNo:1,
	classnewsreply_list_div:"classnewsreply_list_div",
	componentWillReceiveProps:function(){
		this.load_more_data();
	},
	componentDidMount:function(){
		this.load_more_data();
	},
	load_more_data:function(){
		$("#"+this.classnewsreply_list_div).append("<div id="+this.classnewsreply_list_div+this.pageNo+">加载中...</div>");
		var re_data=commons_ajax_reply_list(this.props.uuid,this.classnewsreply_list_div+this.pageNo,this.pageNo);
		if(re_data.data.length<re_data.pageSize){
			$("#"+this.load_more_btn_id).hide();
		}
		  
		  this.pageNo++;
	},
	reply_save_callback:function(){
		this.forceUpdate();
	},
render: function() {
	this.load_more_btn_id="load_more_"+this.props.uuid;
  return (
		  React.createElement("div", {className: "G_reply"}, 
		   React.createElement("h4", null, "评论"), 
		   React.createElement("div", {id: this.classnewsreply_list_div}
		   ), 
			React.createElement("button", {id: this.load_more_btn_id, type: "button", onClick: this.load_more_data.bind(this), className: "am-btn am-btn-primary"}, "加载更多")
			)
		   
  );
}
}); 

//我要评论模块 
var Common_reply_save = React.createClass({displayName: "Common_reply_save", 
	classnewsreply_list_div:"classnewsreply_list_div",
	componentDidMount:function(){
		
		  var editor=$( '#classnews_content_replay' ).xheditor(xhEditor_upImgOption_emot);
          w_img_upload_nocut.bind_onchange("#file_img_upload" , function(imgurl){
                editor.pasteHTML( '<img  width="198" height="198" src="'+imgurl+ '"/>')
          });

	},
	reply_save_btn_click:function(){
		common_ajax_reply_save(this.props.reply_save_callback);
	},
render: function() {
  return (
		   React.createElement("form", {id: "editClassnewsreplyForm", method: "post", className: "am-form"}, 
			React.createElement("input", {type: "hidden", name: "newsuuid", value: this.props.uuid}), 
			React.createElement("input", {type: "hidden", name: "uuid"}), 
			React.createElement("input", {type: "hidden", name: "type", value: this.props.uuid}), 
			
			
			React.createElement(AMR_Input, {id: "classnews_content_replay", type: "textarea", rows: "10", label: "我要回复", placeholder: "填写内容", name: "content"}), 
			G_upload_img_Div, 
			React.createElement("button", {type: "button", onClick: this.reply_save_btn_click.bind(this), className: "am-btn am-btn-primary"}, "提交")
		      
		    )	   
  );
}
}); 


