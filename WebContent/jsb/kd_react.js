
//
var AMR_Table=AMUIReact.Table;
var AMR_ButtonToolbar=AMUIReact.ButtonToolbar;
var AMR_Button=AMUIReact.Button;
var AMR_Sticky=AMUIReact.Sticky;
var AMR_Panel=AMUIReact.Panel;
var AMR_Gallery=AMUIReact.Gallery;
var AMR_Input=AMUIReact.Input;
var Grid=AMUIReact.Grid;
var Col=AMUIReact.Col;


//userinfo reg
var Div_userinfo_reg = React.createClass({displayName: "Div_userinfo_reg", 
	
	render: function() {
	return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "老师注册")
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g"}, 
		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		    React.createElement("form", {id: "regform", method: "post", className: "am-form"}, 
		     React.createElement("input", {type: "hidden", name: "type", value: "1"}), 
		    React.createElement("div", {className: "am-form-group"}, 
		    
		    React.createElement("select", {id: "reg_group_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'lg'}"}, 
		      this.props.group_list.map(function(event) {
		          return (React.createElement("option", {value: event.uuid}, event.brand_name));
		        })
		      )
		        ), 
		      
		      React.createElement("label", {htmlFor: "tel"}, "手机号码:"), 
		      React.createElement("input", {type: "text", name: "tel", id: "tel", placeholder: ""}), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "name"}, "姓名:"), 
		      React.createElement("input", {type: "text", name: "name", id: "name", placeholder: "必填，不超过15位"}), 
		      React.createElement("br", null), 
		       React.createElement("label", {htmlFor: ""}, "Email:"), 
		      React.createElement("input", {type: "email", name: "email", id: "email", placeholder: "输入邮箱", placeholder: ""}), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "password"}, "密码:"), 
		      React.createElement("input", {type: "password", name: "password", id: "password"}), 
		      React.createElement("br", null), 
		      
		      React.createElement("label", {htmlFor: "password1"}, "重复密码:"), 
		      React.createElement("input", {type: "password", name: "password1", id: "password1"}), 
		      React.createElement("br", null), 
		      React.createElement("button", {type: "button", onClick: ajax_userinfo_reg, className: "am-btn am-btn-primary"}, "注册"), 
		      React.createElement("button", {type: "button", onClick: menu_userinfo_login_fn, className: "am-btn am-btn-primary"}, "返回")
		    ), 
		    React.createElement("hr", null)
		  
		  )
		)
		)
	);
	}
}); 

//userinfo reg end

//kd group reg
var Div_kd_group_reg = React.createClass({displayName: "Div_kd_group_reg", 
	
	render: function() {
	return (
		React.createElement("div", null, 
			React.createElement("div", {className: "header"}, 
			  React.createElement("div", {className: "am-g"}, 
			    React.createElement("h1", null, "幼儿园注册")
			  ), 
			  React.createElement("hr", null)
			), 
			React.createElement("div", {className: "am-g"}, 
			  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
			    React.createElement("form", {id: "kd_group_reg_form", method: "post", className: "am-form"}, 
			    React.createElement("input", {type: "hidden", name: "type", value: "1"}), 
			      React.createElement("label", {htmlFor: "brand_name"}, "品牌名:"), 
			      React.createElement("input", {type: "text", name: "brand_name", id: "brand_name", placeholder: "必填，不超过45位"}), 
			      React.createElement("br", null), 
			       React.createElement("label", {htmlFor: "company_name"}, "机构全称:"), 
			      React.createElement("input", {type: "text", name: "company_name", id: "company_name", placeholder: "必填，不超过45位"}), 
			      React.createElement("br", null), 
			       React.createElement("label", {htmlFor: "address"}, "公司地址:"), 
			      React.createElement("input", {type: "text", name: "address", id: "address", placeholder: "必填，不超过64位"}), 
			      React.createElement("br", null), 
			       React.createElement("label", {htmlFor: "map_point"}, "地址坐标:"), 
			      React.createElement("input", {type: "text", name: "map_point", id: "map_point", placeholder: "拾取坐标后，复制到这里。格式：1.1,1.1"}), 
			      React.createElement("a", {href: "http://api.map.baidu.com/lbsapi/getpoint/index.html", target: "_blank"}, "坐标拾取"), 
			      React.createElement("br", null), 
			       React.createElement("label", {htmlFor: "link_tel"}, "公司电话:"), 
			      React.createElement("input", {type: "text", name: "link_tel", id: "link_tel", placeholder: ""}), 
			      React.createElement("br", null), 
			      
			      React.createElement("legend", null, React.createElement("b", null, "管理人员")), 
			     
			      React.createElement("label", {htmlFor: "tel"}, "手机号码:"), 
			      React.createElement("input", {type: "text", name: "tel", id: "tel", placeholder: ""}), 
			      React.createElement("br", null), 
			      React.createElement("label", {htmlFor: "name"}, "姓名:"), 
			      React.createElement("input", {type: "text", name: "name", id: "name", placeholder: "必填，不超过15位"}), 
			      React.createElement("br", null), 
			       React.createElement("label", {htmlFor: ""}, "Email:"), 
			      React.createElement("input", {type: "email", name: "email", id: "email", placeholder: "name@xx.com"}), 
			      React.createElement("br", null), 
			      React.createElement("label", {htmlFor: "password"}, "密码:"), 
			      React.createElement("input", {type: "password", name: "password", id: "password"}), 
			      React.createElement("br", null), 
			      
			      React.createElement("label", {htmlFor: "password1"}, "重复密码:"), 
			      React.createElement("input", {type: "password", name: "password1", id: "password1"}), 
			      React.createElement("br", null), 
			      React.createElement("button", {type: "button", onClick: ajax_kd_group_reg, className: "am-btn am-btn-primary"}, "注册"), 
			      React.createElement("button", {type: "button", onClick: menu_userinfo_login_fn, className: "am-btn am-btn-primary"}, "返回")
			     ), 
			    React.createElement("hr", null)
			  
			  )
			)
		)
	);
	}
}); 

//kd group reg end

//login   
var Div_login = React.createClass({displayName: "Div_login", 
	 getInitialState: function() {
		    return this.props;
		  },
	 handleChange: function(event) {
		 var o=$('#login_form').serializeJson();
		 	o.pw_checked=$("#pw_checked").prop("checked")?"checked":"";
		    this.setState(o);
	  },
render: function() {
	  var o = this.state;
 return (
 		React.createElement("div", null, 
 		React.createElement("div", {className: "header"}, 
 		  React.createElement("div", {className: "am-g"}, 
 		 React.createElement("h1", null, "问界互动家园-幼儿园老师登录"), 
 	    React.createElement("p", null, "PX Background Management System", React.createElement("br", null), "快捷管理，大数据分析")
 		  ), 
 		  React.createElement("hr", null)
 		), 
 		React.createElement("div", {className: "am-g"}, 
 		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
 		 React.createElement("form", {id: "login_form", method: "post", className: "am-form"}, 
 	      React.createElement("label", {htmlFor: "loginname"}, "手机号:"), 
 	      React.createElement("input", {type: "text", name: "loginname", id: "loginname", value: o.loginname, onChange: this.handleChange}), 
 	      React.createElement("br", null), 
 	      React.createElement("label", {htmlFor: "password"}, "密码:"), 
 	      React.createElement("input", {type: "password", name: "password", id: "password", value: o.password, onChange: this.handleChange}), 
 	      React.createElement("br", null), 
 	      React.createElement("label", {htmlFor: "pw_checked"}, 
 	        React.createElement("input", {id: "pw_checked", name: "pw_checked", type: "checkbox", checked: o.pw_checked=="checked"?"checked":"", onChange: this.handleChange}), 
 	        "记住密码"
 	      ), 
 	      React.createElement("br", null), 
 	      React.createElement("div", {className: "am-cf"}, 
 	        React.createElement("input", {id: "btn_login", onClick: ajax_userinfo_login, type: "button", name: "", value: "登 录", className: "am-btn am-btn-primary am-btn-sm am-fl"}), 
 	        React.createElement("input", {type: "button", onClick: menu_userinfo_updatePasswordBySms_fn, value: "忘记密码 ^_^? ", className: "am-btn am-btn-default am-btn-sm am-fr"})
 	      ), 
 	      React.createElement("a", {href: "javascript:void(0);", onClick: menu_kd_group_reg_fn}, "幼儿园注册"), "|", 
 	     React.createElement("a", {href: "javascript:void(0);", onClick: menu_userinfo_reg_fn}, "老师注册"), 
 	      React.createElement("br", null), 
 	      
 	     React.createElement("a", {href: "http://120.25.248.31/px-rest/kd/"}, " ", React.createElement("img", {src: "ewm_kd.png"}))
 	    
 			
 	    ), 
 	    React.createElement("hr", null), 
 	    React.createElement("p", null, "© 2015 成都问界科技有限公司 ")

 	     )
 	   )
 	   
 	   )
 );
}
}); 

//end login


//group
var Group_EventRow = React.createClass({displayName: "Group_EventRow", 
  render: function() {
    var event = this.props.event;
    var className = event.highlight ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      React.createElement("tr", {className: className}, 
      React.createElement("td", null, 
      React.createElement("input", {type: "checkbox", value: event.uuid, name: "table_checkbox"})
      ), 
      React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: btn_click_group.bind(this,"edit", event)}, event.brand_name)), 
        React.createElement("td", null, event.company_name), 
        React.createElement("td", null, " ", event.link_tel), 
        React.createElement("td", null, event.address), 
        React.createElement("td", null, event.create_time)
      ) 
    );
  }
}); 

var Group_show = React.createClass({displayName: "Group_show", 
render: function() {
	  var o = this.props.formdata;
  return (
		  React.createElement(AMUIReact.Article, {
		    title: o.brand_name, 
		    meta: o.company_name+" | "+o.link_tel+" | "+o.address+" | 阅读0次"}, 
			React.createElement("div", {dangerouslySetInnerHTML: {__html: o.description}})
		   )	
		   
		   
  );
}
}); 

//end group





//class

var Class_EventRow = React.createClass({displayName: "Class_EventRow", 
render: function() {
  var event = this.props.event;
  var className = event.highlight ? 'am-active' :
    event.disabled ? 'am-disabled' : '';

  return (
    React.createElement("tr", {className: className}, 
    React.createElement("td", null, 
    React.createElement("input", {type: "checkbox", value: event.uuid, name: "table_checkbox"})
    ), 
      React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: react_ajax_class_students_manage.bind(this, event.uuid)}, event.name)), 
      React.createElement("td", null, event.createUser), 
      React.createElement("td", null, Store.getGroupNameByUuid(event.groupuuid)), 
      React.createElement("td", null, event.create_time)
    ) 
  );
}
}); 
var Class_EventsTable = React.createClass({displayName: "Class_EventsTable",
render: function() {
  return (
  React.createElement("div", null, 
  React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add_class"), round: true}, "添加班级"), 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "edit_class"), round: true}, "编辑"), 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "graduate_class"), round: true}, "毕业")
	  ), 
	  React.createElement("hr", null), 
	  React.createElement("div", {className: "am-form-group"}, 
    React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'lg'}", value: this.props.group_uuid, onChange: this.handleChange_selectgroup_uuid}, 
    this.props.group_list.map(function(event) {
        return (React.createElement("option", {value: event.uuid}, event.brand_name));
      })
    )
  ), 
	  
    React.createElement(AMR_Table, React.__spread({},  this.props), 
      React.createElement("thead", null, 
        React.createElement("tr", null, 
        	React.createElement("th", null, 
          React.createElement("input", {type: "checkbox", id: "id_checkbox_all", onChange: this.handleChange_checkbox_all})
          ), 
          React.createElement("th", null, "班级"), 
          React.createElement("th", null, "创建人"), 
          React.createElement("th", null, "学校"), 
          React.createElement("th", null, "创建时间")
        )
      ), 
      React.createElement("tbody", null, 
        this.props.events.map(function(event) {
          return (React.createElement(Class_EventRow, {key: event.id, event: event}));
        })
      )
    )
    )
  );
},
handleClick: function(m) {
	 if(this.props.handleClick){
		 
		 if(m=="add_class"){
			 this.props.handleClick(m,$('#selectgroup_uuid').val());
			 return;
		 }
		 var uuids=null;
		 $($("input[name='table_checkbox']")).each(function(){
			
			　if(this.checked){
				 if(uuids==null)uuids=this.value;
				 else
				　uuids+=','+this.value ;    //遍历被选中CheckBox元素的集合 得到Value值
			　}
			});
		  if(!uuids){
			  alert("请勾选复选框！");
			  return;
		  }
		  
		 this.props.handleClick(m,$('#selectgroup_uuid').val(),uuids);
	 }
 },
 handleChange_checkbox_all:function(){
	  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
 },
 //
 handleChange_selectgroup_uuid:function(){
	  ajax_class_listByGroup($('#selectgroup_uuid').val());
 }
});
  
var Class_edit = React.createClass({displayName: "Class_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editClassForm').serializeJson());
	  },
render: function() {
	  var o = this.state;
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
  		  React.createElement("form", {id: "editClassForm", method: "post", className: "am-form"}, 
  		React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
  		     React.createElement("input", {type: "hidden", name: "type", value: "1"}), 
  		    React.createElement("div", {className: "am-form-group"}, 
  		          React.createElement("select", {id: "groupuuid", name: "groupuuid", "data-am-selected": "{btnSize: 'lg'}", value: o.groupuuid, onChange: this.handleChange}, 
  		          this.props.group_list.map(function(event) {
  		              return (React.createElement("option", {value: event.uuid}, event.brand_name));
  		            })
  		          )
  		        ), 
  		    
  		      React.createElement("label", {htmlFor: "name"}, "班级:"), 
  		      React.createElement("input", {type: "text", name: "name", id: "name", value: o.name, onChange: this.handleChange, placeholder: "不超过45位！"}), 
  		      React.createElement("br", null), 
  		   
		      React.createElement("label", {htmlFor: "name"}, "班主任:"), 
	  		    React.createElement("input", {type: "hidden", name: "headTeacher", id: "headTeacher", value: o.headTeacher, onChange: this.handleChange}), 
			      React.createElement("input", {type: "text", id: "headTeacher_name", value: o.headTeacher_name, onChange: this.handleChange, onClick: w_ch_user.open.bind(this,"headTeacher","headTeacher_name",$('#selectgroup_uuid').val()), placeholder: ""}), 
			      React.createElement("br", null), 
			      React.createElement("label", {htmlFor: "name"}, "其他老师:"), 
		  		    React.createElement("input", {type: "hidden", name: "teacher", id: "teacher", value: o.teacher, onChange: this.handleChange}), 
				      React.createElement("input", {type: "text", id: "teacher_name", value: o.teacher_name, onChange: this.handleChange, onClick: w_ch_user.open.bind(this,"teacher","teacher_name",$('#selectgroup_uuid').val()), placeholder: ""}), 
				      React.createElement("br", null), 
  		      React.createElement("button", {type: "button", onClick: ajax_class_save, className: "am-btn am-btn-primary"}, "提交")
  		    )

  	     )
  	   )
  	   
  	   )
  );
}
}); 


var AMR_Grid=AMUIReact.Grid;
var AMR_Col=AMUIReact.Col;
var Class_students_manage = React.createClass({displayName: "Class_students_manage",
	 componentDidMount:function(){
			 G_img_down404();

	  },
	render: function() {
		var o=this.props.formdata;
	  return (
	  React.createElement("div", null, 
	  React.createElement(AMR_ButtonToolbar, null, 
		    React.createElement(AMR_Button, {amStyle: "primary", onClick: class_students_manage_onClick.bind(this, "add",this.props.formdata.uuid), round: true}, "添加学生")
		  ), 
		  React.createElement("hr", null), 
		  React.createElement(AMR_Panel, null, 
			  React.createElement(AMR_Grid, {className: "doc-g"}, 
			    React.createElement(AMR_Col, {sm: 4}, " 班级:", o.name), 
			    React.createElement(AMR_Col, {sm: 4}, "班主任:", o.headTeacher_name), 
			    React.createElement(AMR_Col, {sm: 4}, "其他老师:", o.teacher_name)
			  )
		  ), 
		  React.createElement(AMR_Gallery, {data: this.props.students, sm: 3, md: 4, lg: 6})
	    )
	  );
	}
	});


//编辑学生信息
var Class_student_edit = React.createClass({displayName: "Class_student_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editClassStudentForm').serializeJson());
	  },
	  componentDidMount:function(){
		  var imgGuid=this.state.headimg;
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
 		    React.createElement("h1", null, "学生编辑")
 		  ), 
 		  React.createElement("hr", null)
 		), 
 		React.createElement("div", {className: "am-g"}, 
 		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
 		  React.createElement("form", {id: "editClassStudentForm", method: "post", className: "am-form"}, 
 		React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
 		     React.createElement("input", {type: "hidden", name: "classuuid", value: o.classuuid}), 
 		React.createElement("input", {type: "hidden", name: "headimg", id: "headimg", value: o.headimg, onChange: this.handleChange}), 
 		      React.createElement("label", {htmlFor: "name"}, "姓名:"), 
 		      React.createElement("input", {type: "text", name: "name", id: "name", value: o.name, onChange: this.handleChange, placeholder: ""}), 
 		      React.createElement("br", null), 
 		     React.createElement("label", {htmlFor: "nickname"}, "昵称:"), 
		      React.createElement("input", {type: "text", name: "nickname", id: "nickname", value: o.nickname, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "nickname"}, "头像:"), 
	 		    React.createElement(AMUIReact.Image, {id: "img_head_image", src: G_def_headImgPath, className: "G_img_header"}), 
	 		   React.createElement("br", null), 
	 		   React.createElement("button", {type: "button", onClick: btn_class_student_uploadHeadere, className: "am-btn am-btn-primary"}, "上传头像"), 
			      React.createElement("br", null), 
			      
		      React.createElement(AMUIReact.FormGroup, null, 
		      React.createElement("label", null, "单选："), 
		      React.createElement(AMUIReact.Input, {type: "radio", name: "sex", value: "0", label: "男", inline: true, onChange: this.handleChange, checked: o.sex==0?"checked":""}), 
		      React.createElement(AMUIReact.Input, {type: "radio", name: "sex", value: "1", label: "女", inline: true, onChange: this.handleChange, checked: o.sex==1?"checked":""})
		    ), 
		      React.createElement("label", {htmlFor: "birthday"}, "生日:"), 
			React.createElement(AMUIReact.DateTimeInput, {format: "YYYY-MM-DD", name: "birthday", id: "birthday", dateTime: o.birthday, onChange: this.handleChange}), 
 		      React.createElement("br", null), 
			 React.createElement("label", {htmlFor: "birthday"}, "身份证:"), 
			React.createElement("input", {type: "text", name: "idcard", id: "idcard", value: o.idcard, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null), 
 		    
		      React.createElement("fieldset", null, 
		      React.createElement("legend", null, "爸爸妈妈信息"), 
		      React.createElement("label", {htmlFor: "nickname"}, "妈妈姓名:"), 
 		      React.createElement("input", {type: "text", name: "ma_name", id: "ma_name", size: "10", maxLength: "45", value: o.ma_name, onChange: this.handleChange, placeholder: ""}), 
 		      React.createElement("br", null), 
 		     React.createElement("label", {htmlFor: "nickname"}, "妈妈电话:"), 
		      React.createElement("input", {type: "text", name: "ma_tel", id: "ma_tel", value: o.ma_tel, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlfor: "nickname"}, "妈妈的工作:"), 
 		      React.createElement("input", {type: "text", name: "ma_work", id: "ma_work", value: o.ma_work, onChange: this.handleChange, placeholder: ""}), 
 		      React.createElement("br", null), 		      		      
		     
 		      React.createElement("label", {htmlfor: "nickname"}, "爸爸姓名:"), 
 		      React.createElement("input", {type: "text", name: "ba_name", id: "ba_name", size: "10", maxLength: "45", value: o.ba_name, onChange: this.handleChange, placeholder: ""}), 
 		      React.createElement("br", null), 
 		     React.createElement("label", {htmlFor: "nickname"}, "爸爸电话:"), 
		      React.createElement("input", {type: "text", name: "ba_tel", id: "ba_tel", value: o.ba_tel, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null), 
 		          
 		      React.createElement("label", {htmlfor: "nickname"}, "爸爸的工作:"), 
 		      React.createElement("input", {type: "text", name: "ba_work", id: "ba_work", value: o.ba_work, onChange: this.handleChange, placeholder: ""}), 
 		      React.createElement("br", null), 		     
 		     React.createElement("label", {htmlfor: "nickname"}, "家庭住址:"), 
		      React.createElement("input", {type: "text", name: "address", id: "address", value: o.address, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null)		
		    ), 
		    React.createElement("fieldset", null, 
		      React.createElement("legend", null, "其他信息"), 
		      React.createElement("label", {htmlFor: "nickname"}, "奶奶电话:"), 
 		      React.createElement("input", {type: "text", name: "nai_tel", id: "nai_tel", value: o.nai_tel, onChange: this.handleChange, placeholder: ""}), 
 		      React.createElement("br", null), 
 		     React.createElement("label", {htmlFor: "nickname"}, "爷爷电话:"), 
		      React.createElement("input", {type: "text", name: "ye_tel", id: "ye_tel", value: o.ye_tel, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "nickname"}, "外婆电话:"), 
 		      React.createElement("input", {type: "text", name: "waipo_tel", id: "waipo_tel", value: o.waipo_tel, onChange: this.handleChange, placeholder: ""}), 
 		      React.createElement("br", null), 
 		      React.createElement("label", {htmlFor: "nickname"}, "外公电话:"), 
		      React.createElement("input", {type: "text", name: "waigong_tel", id: "waigong_tel", value: o.waigong_tel, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "nickname"}, "其他电话:"), 
		      React.createElement("input", {type: "text", name: "other_tel", id: "other_tel", value: o.other_tel, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null), 
	 		   React.createElement(AMUIReact.Input, {type: "textarea", 
		 	 	      label: "说明", 
		 	 	    	 name: "note", 
		 	 	      labelClassName: "am-u-sm-2", 
		 	 	      placeholder: "备注", 
		 	 	      wrapperClassName: "am-u-sm-10", 
		 	 	      amSize: "lg"}), 
	 		  React.createElement("br", null)
	 		 ), 
 		      React.createElement("button", {type: "button", onClick: btn_ajax_class_student_save, className: "am-btn am-btn-primary"}, "提交")
 		    )

 	     )
 	   )
 	   
 	   )
 );
}
}); 
var Class_student_look_info =React.createClass({displayName: "Class_student_look_info",
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editClassStudentForm').serializeJson());
	  },
	  componentDidMount:function(){
		  var imgGuid=this.state.headimg;
		 if(imgGuid){
			 $("#img_head_image").attr("src",G_imgPath+imgGuid); 
			 G_img_down404("#img_head_image");
		 }

	  },
		render: function() {
	     var o =this.state;
		 return (
		 		React.createElement("div", null, 
			    React.createElement(AMUIReact.List, {static: true}, 
			      React.createElement(AMUIReact.ListItem, null, "头像:"), 
				  React.createElement(AMUIReact.Image, {id: "img_head_image", src: G_def_headImgPath, className: "G_img_header"}), 
				  React.createElement("br", null), 
			      React.createElement(AMUIReact.ListItem, null, "姓名:", o.name), 
			      React.createElement(AMUIReact.ListItem, null, "昵称:", o.nickname), 
			      React.createElement(AMUIReact.ListItem, null, "性别:", Vo.get("sex_"+o.sex)), 
			      React.createElement(AMUIReact.ListItem, null, "生日:", o.birthday), 
			      React.createElement(AMUIReact.ListItem, null, "妈妈姓名:", o.ma_name), 
			      React.createElement(AMUIReact.ListItem, null, "妈妈电话:", o.ma_tel), 
			      React.createElement(AMUIReact.ListItem, null, "妈妈的工作:", o.ma_work), 
			      React.createElement(AMUIReact.ListItem, null, "爸爸姓名:", o.ba_name), 
			      React.createElement(AMUIReact.ListItem, null, "爸爸的工作:", o.ba_work), 
			      React.createElement(AMUIReact.ListItem, null, "爸爸电话:", o.ba_tel), 
			      React.createElement(AMUIReact.ListItem, null, "家庭住址:", o.address), 
			      React.createElement(AMUIReact.ListItem, null, "爷爷电话:", o.ye_tel), 
			      React.createElement(AMUIReact.ListItem, null, "奶奶电话:", o.nai_tel), 
			      React.createElement(AMUIReact.ListItem, null, "外公电话:", o.waigong_tel), 
			      React.createElement(AMUIReact.ListItem, null, "外婆电话:", o.waipo_tel), 
			      React.createElement(AMUIReact.ListItem, null, "其他电话:", o.other_tel), 			      
			      React.createElement(AMUIReact.ListItem, null, 
			      React.createElement("div", {dangerouslySetInnerHTML: {__html:G_textToHTML("说明:"+o.note)}})
			      )			      
			      
			      )
		 	     ) 
		     );
	        }
		 });
/*
* 我的班级（主页） show绘制2级界面班级选择绘制；
* @show老师查看状态进入查看学生详情;
* @Class_students_show（kd_service中服务器请求时调用）;
* */
var Class_students_show= React.createClass({displayName: "Class_students_show",
	 componentDidMount:function(){
			 G_img_down404();

	  },
	render: function() {
		var o=this.props.formdata;
	  return (
	  React.createElement("div", null, 
	 
		  React.createElement(AMR_Panel, null, 
			  React.createElement(AMR_Grid, {className: "doc-g"}, 
			    React.createElement(AMR_Col, {sm: 4}, " 班级:", o.name), 
			    React.createElement(AMR_Col, {sm: 4}, "班主任:", o.headTeacher_name), 
			    React.createElement(AMR_Col, {sm: 4}, "其他老师:", o.teacher_name)
			  )
		  ), 
		  React.createElement(AMR_Gallery, {data: this.props.students, sm: 3, md: 4, lg: 6})
	    )
	  );
	}
	});
/*班级管理中查看学生信息
 * Class_student_look_info@:此方法模板为单独查看每个学生详细信息但不能编辑；
 * <AMUIReact.ListItem>调用的为AMUIReact中的List 标签；
 * 
 * */
var Class_student_look_info =React.createClass({displayName: "Class_student_look_info",
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editClassStudentForm').serializeJson());
	  },
	  componentDidMount:function(){
		  var imgGuid=this.state.headimg;
		 if(imgGuid){
			 $("#img_head_image").attr("src",G_imgPath+imgGuid); 
			 G_img_down404("#img_head_image");
		 }

	  },
		render: function() {
	     var o =this.state;
		 return (
		 		React.createElement("div", null, 
			    React.createElement(AMUIReact.List, {static: true}, 
			      React.createElement(AMUIReact.ListItem, null, "头像:"), 
				  React.createElement(AMUIReact.Image, {id: "img_head_image", src: G_def_headImgPath, className: "G_img_header"}), 
				  React.createElement("br", null), 
			      React.createElement(AMUIReact.ListItem, null, "姓名:", o.name), 
			      React.createElement(AMUIReact.ListItem, null, "昵称:", o.nickname), 
			      React.createElement(AMUIReact.ListItem, null, "性别:", Vo.get("sex_"+o.sex)), 
			      React.createElement(AMUIReact.ListItem, null, "生日:", o.birthday), 
			      React.createElement(AMUIReact.ListItem, null, "妈妈姓名:", o.ma_name), 
			      React.createElement(AMUIReact.ListItem, null, "妈妈电话:", o.ma_tel), 
			      React.createElement(AMUIReact.ListItem, null, "妈妈的工作:", o.ma_work), 
			      React.createElement(AMUIReact.ListItem, null, "爸爸姓名:", o.ba_name), 
			      React.createElement(AMUIReact.ListItem, null, "爸爸的工作:", o.ba_work), 
			      React.createElement(AMUIReact.ListItem, null, "爸爸电话:", o.ba_tel), 
			      React.createElement(AMUIReact.ListItem, null, "家庭住址:", o.address), 
			      React.createElement(AMUIReact.ListItem, null, "爷爷电话:", o.ye_tel), 
			      React.createElement(AMUIReact.ListItem, null, "奶奶电话:", o.nai_tel), 
			      React.createElement(AMUIReact.ListItem, null, "外公电话:", o.waigong_tel), 
			      React.createElement(AMUIReact.ListItem, null, "外婆电话:", o.waipo_tel), 
			      React.createElement(AMUIReact.ListItem, null, "其他电话:", o.other_tel), 			      
			      React.createElement(AMUIReact.ListItem, null, 
			      React.createElement("div", {dangerouslySetInnerHTML: {__html:G_textToHTML("说明:"+o.note)}})
			      )			      
			      
			      )
		 	     ) 
		     );
	        }
		 });
//end class



//announcements
/**
* ajax_announcements_edit
*/

var Announcements_EventRow = React.createClass({displayName: "Announcements_EventRow", 
render: function() {
  var event = this.props.event;
  var className = event.highlight ? 'am-active' :
    event.disabled ? 'am-disabled' : '';

  return (
    React.createElement("tr", {className: className}, 
    React.createElement("td", null, 
    React.createElement("input", {type: "checkbox", value: event.uuid, name: "table_checkbox"})
    ), 
      React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: react_ajax_announce_show.bind( this, event.uuid)}, event.title)), 
      React.createElement("td", null, Vo.announce_type(event.type)), 
      React.createElement("td", null, Store.getGroupNameByUuid(event.groupuuid)), 
      React.createElement("td", null, 0), 
      React.createElement("td", null, event.create_user), 
      React.createElement("td", null, event.create_time)
    ) 
  );
}
}); 

var Announcements_EventsTable = React.createClass({displayName: "Announcements_EventsTable",
	handleClick: function(m) {
			 if(m=="add"){
				 btn_click_announce(m,$('#selectgroup_uuid').val());
				 return;
			 }
			 var uuids=null;
			 $($("input[name='table_checkbox']")).each(function(){
				　if(this.checked){
					 if(uuids==null)uuids=this.value;
					 else
					　uuids+=','+this.value ;    //遍历被选中CheckBox元素的集合 得到Value值
				　}
				});
			  if(!uuids){
				  alert("请勾选复选框！");
				  return;
			  }
			  btn_click_announce(m,$('#selectgroup_uuid').val(),uuids);
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
	  },
	  //
	  handleChange_selectgroup_uuid:function(){
		  ajax_announce_listByGroup($('#selectgroup_uuid').val());
	  },
render: function() {
  return (
  React.createElement("div", null, 
  React.createElement("div", {className: "header"}, 
	  React.createElement("div", {className: "am-g"}, 
	    React.createElement("h1", null, Vo.announce_type(announce_types))
	  ), 
	  React.createElement("hr", null)
	), 
  React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add"), round: true}, "创建"), 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "edit"), round: true}, "编辑"), 
	    React.createElement(AMR_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "del"), round: true}, "删除")
	    ), 
	  React.createElement("hr", null), 
	  React.createElement("div", {className: "am-form-group"}, 
    React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'lg'}", value: this.props.groupuuid, onChange: this.handleChange_selectgroup_uuid}, 
    this.props.group_list.map(function(event) {
        return (React.createElement("option", {value: event.uuid}, event.brand_name));
      })
    )
  ), 
	  
    React.createElement(AMR_Table, React.__spread({},  this.props), 
      React.createElement("thead", null, 
        React.createElement("tr", null, 
        	React.createElement("th", null, 
          React.createElement("input", {type: "checkbox", id: "id_checkbox_all", onChange: this.handleChange_checkbox_all})
          ), 
          React.createElement("th", null, "标题"), 
          React.createElement("th", null, "类型"), 
          React.createElement("th", null, "幼儿园"), 
          React.createElement("th", null, "浏览次数"), 
          React.createElement("th", null, "创建人"), 
          React.createElement("th", null, "创建时间")
        )
      ), 
      React.createElement("tbody", null, 
        this.props.events.map(function(event) {
          return (React.createElement(Announcements_EventRow, {key: event.id, event: event}));
        })
      )
    )
    )
  );
}
});
  
  var Announcements_mylist_EventRow = React.createClass({displayName: "Announcements_mylist_EventRow", 
	  render: function() {
	    var event = this.props.event;
	    var className = event.highlight ? 'am-active' :
	      event.disabled ? 'am-disabled' : '';

	    return (
	      React.createElement("tr", {className: className}, 
	        React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: react_ajax_announce_show.bind( this, event.uuid)}, event.title)), 
	        React.createElement("td", null, Vo.announce_type(event.type)), 
	        React.createElement("td", null, Store.getGroupNameByUuid(event.groupuuid)), 
	        React.createElement("td", null, 0), 
	        React.createElement("td", null, event.create_user), 
	        React.createElement("td", null, event.create_time)
	      ) 
	    );
	  }
	  });
  /*
   * （首页）公告功能绘制
   * 
   * */
  var Announcements_mylist_EventsTable = React.createClass({displayName: "Announcements_mylist_EventsTable",
  render: function() {
    return (
    React.createElement("div", null, 
      React.createElement(AMR_Table, React.__spread({},  this.props), 
        React.createElement("thead", null, 
          React.createElement("tr", null, 
            React.createElement("th", null, "标题"), 
            React.createElement("th", null, "类型"), 
            React.createElement("th", null, "幼儿园"), 
            React.createElement("th", null, "浏览次数"), 
            React.createElement("th", null, "创建人"), 
            React.createElement("th", null, "创建时间")
          )
        ), 
        React.createElement("tbody", null, 
          this.props.events.map(function(event) {
            return (React.createElement(Announcements_mylist_EventRow, {event: event}));
          })
        )
      )
      )
    );
  }
  });
  
var Announcements_edit = React.createClass({displayName: "Announcements_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editAnnouncementsForm').serializeJson());
	  },
	  componentDidMount:function(){
	  var editor= $('#announce_message').xheditor(xhEditor_upImgOption_mfull);
          w_img_upload_nocut.bind_onchange("#announce_message" ,function(imgurl){
                editor.pasteHTML( '<img  width="198" height="198" src="'+imgurl+'"/>')
          });

	  },
render: function() {
	  var o = this.state;
	  
	  var type_div;
	  if (announce_types==2) {
		  type_div= 
			   React.createElement("div", {className: "am-form-group", id: "div_classuuids"}, 
		  		React.createElement("input", {type: "hidden", name: "type", value: o.type}), 
		  		React.createElement("label", {htmlFor: "tel"}, "班级通知:"), 
		  		React.createElement("input", {type: "text", name: "classuuids", id: "classuuids", value: o.classuuids, onChange: this.handleChange, placeholder: "班级通知，才填写"})
  		     );
	  } else {
		  type_div =
		  React.createElement("input", {type: "hidden", name: "type", value: o.type})
	  }
  return (
  		React.createElement("div", null, 
  		React.createElement("div", {className: "header"}, 
  		  React.createElement("div", {className: "am-g"}, 
  		    React.createElement("h1", null, Vo.announce_type(o.type), "-编辑")
  		  ), 
  		  React.createElement("hr", null)
  		), 
  		React.createElement("div", {className: "am-g"}, 
  		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
  		  React.createElement("form", {id: "editAnnouncementsForm", method: "post", className: "am-form"}, 
  		React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
  		React.createElement("input", {type: "hidden", name: "isimportant", value: o.isimportant}), 
  		
  		React.createElement("div", {className: "am-form-group"}, 
  		          React.createElement("select", {id: "group_uuid", name: "groupuuid", "data-am-selected": "{btnSize: 'lg'}", value: o.group_uuid, onChange: this.handleChange}, 
  		          this.props.group_list.map(function(event) {
  		              return (React.createElement("option", {value: event.uuid}, event.brand_name));
  		            })
  		          )
  		        ), 
  		        
  		    type_div, 
  		    
  		      React.createElement("label", {htmlFor: "name"}, "标题:"), 
  		      React.createElement("input", {type: "text", name: "title", id: "title", value: o.title, onChange: this.handleChange, maxlength: "45", placeholder: "不超过45位"}), 
  		      React.createElement("br", null), 
  		    React.createElement(AMR_Input, {id: "announce_message", type: "textarea", rows: "10", label: "内容:", placeholder: "填写内容", name: "message", value: o.message, onChange: this.handleChange}), 
 			G_upload_img_Div, 
  		      React.createElement("button", {type: "button", onClick: ajax_announcements_save, className: "am-btn am-btn-primary"}, "提交")
  		    )

  	     )
  	   )
  	   
  	   )
  );
}
}); 




//主页公告添加模板
var Announcements_show = React.createClass({displayName: "Announcements_show", 
render: function() {
	  var o = this.props.data;
  return (
		  React.createElement("div", null, 
		  React.createElement(AMUIReact.Article, {
		    title: o.title, 
		    meta: Vo.announce_type(o.type)+" | "+Store.getGroupNameByUuid(o.groupuuid)+" | "+o.create_time+ "|阅读"+ this.props.count+"次"}, 
			React.createElement("div", {dangerouslySetInnerHTML: {__html: o.message}})
		   ), 
		   "//点赞回复模板", 
			  React.createElement(Common_Dianzan_show, {uuid: o.uuid, type: 0}), 
			  React.createElement(Common_reply_list, {uuid: o.uuid, type: 0}), 
			  React.createElement(Common_reply_save, {uuid: o.uuid, type: 0})
		   )
  );
}
}); 

//end announcements





//teachingplan
/**
* ajax_teachingplan_edit
*/

var Teachingplan_EventRow = React.createClass({displayName: "Teachingplan_EventRow", 
render: function() {
var event = this.props.event;
if(G_week.getWeekStr(event.plandate)==G_week.getWeekStr(new Date())){
	event.highlight=true;
}
var className = event.highlight ? 'am-active' :
  event.disabled ? 'am-disabled' : '';

return (
  React.createElement("tr", {className: className}, 
    React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: btn_click_teachingplan.bind( this, "edit",event.uuid)}, G_week.getWeekStr(event.plandate))), 
    React.createElement("td", null, event.morning), 
    React.createElement("td", null, event.afternoon)
  ) 
);
}
}); 

var Teachingplan_EventsTable = React.createClass({displayName: "Teachingplan_EventsTable",
	handleClick: function(m,uuid,classuuid) {
			 if(m=="add"){
				 btn_click_teachingplan(m,null,classuuid);
				 return;
			 }else if(m=="pre"){
				 ajax_teachingplan_listByClass(null,null,--g_cookbookPlan_week_point);
				 return;
			 }else if(m=="next"){
				 ajax_teachingplan_listByClass(null,null,++g_cookbookPlan_week_point);
				 return;
			 }
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
	  },
	  //
	  handleChange_selectgroup_uuid:function(){
		  ajax_announce_listByGroup($('#selectgroup_uuid').val());
	  },
render: function() {
return (
React.createElement("div", null, 
React.createElement("div", {className: "header"}, 
	  React.createElement("div", {className: "am-g"}, 
	  React.createElement("h1", null, "【", this.props.classname, "】[", this.props.begDateStr, " 到 ", this.props.endDateStr, "]")
	  ), 
	  React.createElement("hr", null)
	), 
React.createElement(AMR_ButtonToolbar, null, 
	React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add",null,this.props.classuuid), round: true}, "添加"), 
    React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "pre"), round: true}, "上周"), 
    React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "next"), round: true}, "下周")
    ), 
	  React.createElement("hr", null), 
  React.createElement(AMR_Table, React.__spread({},  this.props), 
    React.createElement("thead", null, 
      React.createElement("tr", null, 
      	React.createElement("th", null, "一周"), 
        React.createElement("th", null, "上午"), 
        React.createElement("th", null, "下午")
      )
    ), 
    React.createElement("tbody", null, 
      this.props.events.map(function(event) {
        return (React.createElement(Teachingplan_EventRow, {event: event}));
      })
    )
  )
  )
);
}
});



/**
 * 显示每天的教学计划
 */
var Teachingplan_showByOneDay = React.createClass({displayName: "Teachingplan_showByOneDay", 
	handleClick: function(m) {
		if(m=="pre"){
			ajax_teachingplan_dayShow(--g_teachingplan_listToShow_point);
			 return;
		 }else if(m=="next"){
			 ajax_teachingplan_dayShow(++g_teachingplan_listToShow_point);
			 return;
		 }
	},
	componentDidMount: function() {
		  if(!this.props.formdata){
			  $("#div_detail").html("今日没有发布教学计划");
		  }
	    
	  },
	render: function() {
	  var o = this.props.formdata;
	  
	  if(!o){
		  o={};
	  }
	
	  return (
		React.createElement("div", null, 
		
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		  
		  React.createElement(Grid, null, 
		    React.createElement(Col, {sm: 3}, 
		    React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "pre"), round: true}, "上一天")
		    ), 
		    React.createElement(Col, {sm: 6}, 
		    React.createElement("h1", null, "课程安排-【", this.props.ch_class.name, "】-", this.props.ch_day)
		    ), 
		    React.createElement(Col, {sm: 3}, 
		    React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "next"), round: true}, "下一天")	
		    )
		  )
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g", id: "div_detail"}, 
		 React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		 React.createElement("label", null, "早上:"), 
		 React.createElement("div", {className: "g_teachingplan"}, 
			React.createElement("div", {dangerouslySetInnerHTML: {__html:G_textToHTML(o.morning)}})
		 ), 
		 React.createElement("label", null, "下午:"), 
		 React.createElement("div", {className: "g_teachingplan"}, 
			React.createElement("div", {dangerouslySetInnerHTML: {__html:G_textToHTML(o.afternoon)}})
		 )
		)
		)
	   
	   )
);
}
}); 

var Teachingplan_edit = React.createClass({displayName: "Teachingplan_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editTeachingplanForm').serializeJson());
	  },
render: function() {
	  var o = this.state;
	
return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "某某班级课程1-编辑")
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g"}, 
		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		  React.createElement("form", {id: "editTeachingplanForm", method: "post", className: "am-form"}, 
		React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
		React.createElement("input", {type: "hidden", name: "classuuid", value: o.classuuid}), 
		 React.createElement("label", {htmlFor: "name"}, "日期:"), 
		 React.createElement(AMUIReact.DateTimeInput, {format: "YYYY-MM-DD", name: "plandateStr", id: "plandateStr", dateTime: o.plandate, onChange: this.handleChange}), 
		      React.createElement("br", null), 
	    React.createElement(AMR_Input, {id: "morning", name: "morning", type: "textarea", rows: "2", label: "早上:", placeholder: "填写内容", value: o.morning, onChange: this.handleChange}), 
		React.createElement(AMR_Input, {id: "afternoon", name: "afternoon", type: "textarea", rows: "2", label: "下午:", placeholder: "填写内容", value: o.afternoon, onChange: this.handleChange}), 
		      React.createElement("button", {type: "button", onClick: ajax_teachingplan_save, className: "am-btn am-btn-primary"}, "提交")
	 )

	     )
	   )
	   
	   )
);
}
}); 

//end teachingplan




//cookbookPlan
var CookbookPlan_EventRow = React.createClass({displayName: "CookbookPlan_EventRow", 
	parseTimes:function(s){
		var rs=null;
		if(s==null||s=="")return "";
		var arr=s.split(",");
		for(var i=0;i<arr.length;i++){
			var t_arr=arr[i].split("$");
			if(rs==null)rs=t_arr[t_arr.length-1];
			else rs+=","+t_arr[t_arr.length-1];
		}  
		return rs;
	},
render: function() {
var event = this.props.event;
var className = event.highlight ? 'am-active' :
  event.disabled ? 'am-disabled' : '';

return (
  React.createElement("tr", {className: className}, 
    React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: btn_click_cookbookPlan.bind( this, 'edit',event)}, G_week.getWeekStr(event.plandate))), 
    React.createElement("td", null, this.parseTimes(event.time_1)), 
    React.createElement("td", null, this.parseTimes(event.time_2)), 
    React.createElement("td", null, this.parseTimes(event.time_3)), 
    React.createElement("td", null, this.parseTimes(event.time_4)), 
    React.createElement("td", null, this.parseTimes(event.time_5)), 
    React.createElement("td", null, event.analysis)
  ) 
);
}
}); 
var CookbookPlan_EventsTable = React.createClass({displayName: "CookbookPlan_EventsTable",
render: function() {
return (
React.createElement("div", null, 
React.createElement(AMR_ButtonToolbar, null, 
React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add",null,this.props.group_uuid), round: true}, "添加"), 
React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "pre"), round: true}, "上周"), 
React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "next"), round: true}, "下周")	
), 
React.createElement("div", {className: "header"}, 
React.createElement("div", {className: "am-g"}, 
  React.createElement("h1", null, "[", this.props.begDateStr, " 到 ", this.props.endDateStr, "]")
), 
React.createElement("hr", null)
), 
	  React.createElement("div", {className: "am-form-group"}, 
  React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'lg'}", value: this.props.group_uuid, onChange: this.handleChange_selectgroup_uuid}, 
  this.props.group_list.map(function(event) {
      return (React.createElement("option", {value: event.uuid}, event.brand_name));
    })
  )
), 
	  
  React.createElement(AMR_Table, React.__spread({},  this.props), 
    React.createElement("thead", null, 
      React.createElement("tr", null, 
        React.createElement("th", null, "一周"), 
        React.createElement("th", null, "早餐"), 
        React.createElement("th", null, "早上加餐"), 
        React.createElement("th", null, "午餐"), 
        React.createElement("th", null, "下午加餐"), 
        React.createElement("th", null, "晚餐"), 
        React.createElement("th", null, "营养分析")
      )
    ), 
    React.createElement("tbody", null, 
      this.props.events.map(function(event) {
        return (React.createElement(CookbookPlan_EventRow, {event: event}));
      })
    )
  )
  )
);
},


handleClick: function(m) {
	
	
	if(m=="add"){
		btn_click_cookbookPlan(m,{groupuuid:$('#selectgroup_uuid').val()});
		 return;
	 }if(m=="edit"){
		
		 var uuids=null;
		 $($("input[name='table_checkbox']")).each(function(){
			
			　if(this.checked){
				 if(uuids==null)uuids=this.value;
				 else
				　uuids+=','+this.value ;    //遍历被选中CheckBox元素的集合 得到Value值
			　}
			});
		  if(!uuids){
			  alert("请勾选复选框！");
			  return;
		  }
		  if(!uuids&&uuids.indexOf(",")>-1){
				alert("只能选择一个进行编辑！");
				return;
			}
		  btn_click_cookbookPlan(m,{uuid:uuids});
	 } else if(m=="pre"){
		 ajax_cookbookPlan_listByGroup($('#selectgroup_uuid').val(),--g_cookbookPlan_week_point);
		 return;
	 }else if(m=="next"){
		 ajax_cookbookPlan_listByGroup($('#selectgroup_uuid').val(),++g_cookbookPlan_week_point);
		 return;
	 }
},
//
handleChange_selectgroup_uuid:function(){
	ajax_cookbookPlan_listByGroup($('#selectgroup_uuid').val(),g_cookbookPlan_week_point);
}
});


var CookbookPlan_edit_EventRow = React.createClass({displayName: "CookbookPlan_edit_EventRow",
	
	 getInitialState: function() {
		 var lists=this.cookbookPlan_timeStr_to_list(this.props.uuids);
		    return {
	            items: lists
	        };
		  },
	
	  //uuids=rs += (cb.getUuid() + "$" + cb.getName() + ",");
	  cookbookPlan_timeStr_to_list:function(cooks){
		  if(cooks==null)return [];
		  return cooks.split(",");
		  
	  },
	  
		deleteImg:function(divid){
			$("#"+divid).hide();
		},
		 btn_addCookplan: function(divid) {
			 var that=this;
			  var checkeduuids =null;
			  $("#"+divid+" > .G_cookplan_Img").each(function(){
				  		if($(this).is(":hidden")){
				  			return;
				  		}
						 if(checkeduuids==null)checkeduuids=this.title;
						 else
						　checkeduuids+=','+this.title ;    //遍历被选中CheckBox元素的集合 得到Value值
					});
			w_ch_cook.open(function(cooks){
				  that.setState({
			            items: that.cookbookPlan_timeStr_to_list(cooks)
			        });
				  $(".G_cookplan_Img").show();
				  
			  },checkeduuids);
		  },
	  render: function() {
		var that=this;
	    return (
	    		  React.createElement("div", {id: "div_cookPlan_"+this.props.type}, 
	    		  
	    		  
	    			  this.state.items.map(function(event) {
	    				  //rs += (cb.getUuid() + "$" + cb.getName() + ",");
	    				  var arr=event.split("$");
	    				  if(arr.length!=3)return;
	    				  var t_uuid=arr[0];
	    				  var t_imguuid=arr[1];
	    				  var t_name=arr[2];
 	    					 return (
 	     	 	            		React.createElement("div", {id: "div_cookPlan_Item_"+t_uuid, title: t_uuid, className: "G_cookplan_Img"}, 
 	    		    	 	       			React.createElement("img", {className: "G_cookplan_Img_img", id: "divCookItem_img_"+t_uuid, src: G_imgPath+t_imguuid, alt: "图片不存在", title: t_name}), 
 	    		    	 	       			React.createElement("div", {className: "G_cookplan_Img_close", onClick: that.deleteImg.bind(this,"div_cookPlan_Item_"+t_uuid)}, React.createElement("img", {src: hostUrl+"i/close.png", border: "0"})), 
 	    		    	 	       			React.createElement("span", null, t_name)
 	    		    	 	       		)		
 	     	 	            	);
 	     	 	          
 	    				
 	    			 }), //end map
	    		  
	    		  React.createElement("button", {type: "button", onClick: that.btn_addCookplan.bind(this,"div_cookPlan_"+that.props.type), className: "am-btn am-btn-primary"}, "添加")
 	    		)
		
	  )
	  }
	});

var CookbookPlan_edit = React.createClass({displayName: "CookbookPlan_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editCookbookPlanForm').serializeJson());
	  },
	 
render: function() {
	  var o = this.state;
	  if(!o.time_1_arr)o.time_1_arr=[];
	  
	  var plandateStr_div;
	  if (o.uuid) {//只读
		//2015-07-04 00:00:00=>2015-07-04
		  o.plandate=o.plandate.split(" ")[0];
		  plandateStr_div = React.createElement("input", {type: "text", name: "plandateStr", id: "plandateStr", value: o.plandate})
	  } else {
		  plandateStr_div = React.createElement(AMUIReact.DateTimeInput, {format: "YYYY-MM-DD", name: "plandateStr", id: "plandateStr", dateTime: o.plandate, showTimePicker: false, onChange: this.handleChange})
	  }
	  return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "【", Store.getGroupNameByUuid(o.groupuuid), "】-每日食谱-编辑")
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g"}, 
		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		  React.createElement("form", {id: "editCookbookPlanForm", method: "post", className: "am-form"}, 
		React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
		React.createElement("input", {type: "hidden", name: "groupuuid", value: o.groupuuid}), 
		React.createElement("input", {type: "hidden", name: "type", value: "1"}), 
		        React.createElement("label", {htmlFor: "name"}, "日期:"), 
				 plandateStr_div, 
				 React.createElement("br", null), 
		      React.createElement("label", null, "早餐:"), 
		      React.createElement(CookbookPlan_edit_EventRow, {uuids: o.time_1, type: "time_1"}), 
		      React.createElement("div", {className: "cls"}), 
		      React.createElement("br", null), 
		      React.createElement("label", null, "早上加餐:"), 
		      React.createElement(CookbookPlan_edit_EventRow, {uuids: o.time_2, type: "time_2"}), 
		      React.createElement("div", {className: "cls"}), 
		      React.createElement("br", null), 
		      React.createElement("label", null, "午餐:"), 
		      React.createElement(CookbookPlan_edit_EventRow, {uuids: o.time_3, type: "time_3"}), 
		      React.createElement("div", {className: "cls"}), 
		      React.createElement("br", null), 
		      React.createElement("label", null, "下午加餐:"), 
		      React.createElement(CookbookPlan_edit_EventRow, {uuids: o.time_4, type: "time_4"}), 
		      React.createElement("div", {className: "cls"}), 
		      React.createElement("br", null), 
		      React.createElement("label", null, "晚餐:"), 
		      React.createElement(CookbookPlan_edit_EventRow, {uuids: o.time_5, type: "time_5"}), 
		      React.createElement("div", {className: "cls"}), 
		      React.createElement("br", null), 
		      React.createElement(AMR_Input, {name: "analysis", type: "textarea", rows: "2", label: "营养分析:", placeholder: "填写内容", value: o.analysis, onChange: this.handleChange}), 
				
		      React.createElement("button", {type: "button", onClick: ajax_cookbookPlan_save, className: "am-btn am-btn-primary"}, "提交")
		    )

	     )
	   )
	   
	   )
);
}
}); 

var CookbookPlanShow_EventRow = React.createClass({displayName: "CookbookPlanShow_EventRow",
	//第而
		componentWillReceiveProps: function(nextProps) {
			 var lists=this.cookbookPlan_timeStr_to_list(this.props.uuids);
			  this.setState({
				  items: lists
			  });
			},
	 getInitialState: function() {
		 var lists=this.cookbookPlan_timeStr_to_list(this.props.uuids);
		    return {
	            items: lists
	        };
		  },
	  //uuids=rs += (cb.getUuid() + "$" + cb.getName() + ",");
	  cookbookPlan_timeStr_to_list:function(cooks){
		  if(!cooks)return [];
		  return cooks.split(",");
		  
	  },
	  
	  render: function() {
	    return (
	    		  React.createElement("div", {id: "div_cookPlan_"+this.props.type}, 
	    		  
	    			  this.state.items.map(function(event) {
	    				  //rs += (cb.getUuid() + "$" + cb.getName() + ",");
	    				  var arr=event.split("$");
	    				  if(arr.length!=3)return;
	    				  var t_uuid=arr[0];
	    				  var t_imguuid=arr[1];
	    				  var t_name=arr[2];
	    					 return (
	     	 	            		React.createElement("div", {id: "div_cookPlan_Item_"+t_uuid, title: t_uuid, className: "G_cookplan_Img"}, 
	    		    	 	       			React.createElement("img", {className: "G_cookplan_Img_img", id: "divCookItem_img_"+t_uuid, src: G_imgPath+t_imguuid, alt: "图片不存在", title: t_name}), 
	    		    	 	       			React.createElement("span", null, t_name)
	    		    	 	       		)		
	     	 	            	);
	    				
	    			 })//end map
	    		  
	    		)
		
	  )
	  }
	});

/**
 * 显示每天的食谱
 */
var CookbookPlan_showByOneDay = React.createClass({displayName: "CookbookPlan_showByOneDay", 
	handleClick: function(m) {
		if(m=="pre"){
			ajax_cookbookPlan_dayShow(--g_cookbookPlan_listToShow_point);
			 return;
		 }else if(m=="next"){
			 ajax_cookbookPlan_dayShow(++g_cookbookPlan_listToShow_point);
			 return;
		 }
	},
	componentDidMount: function() {
		  if(!this.props.formdata){
			  $("#div_detail").html("今日没有发布食谱");
		  }
	    
	  },
	render: function() {
	  var o = this.props.formdata;
	  
	  if(!o){
		  o={};
	  }
	
	  return (
		React.createElement("div", null, 
		
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		  
		  React.createElement(Grid, null, 
		    React.createElement(Col, {sm: 3}, 
		    React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "pre"), round: true}, "上一天")
		    ), 
		    React.createElement(Col, {sm: 6}, 
		    React.createElement("h1", null, "【", this.props.ch_group.brand_name, "】-每日食谱-", this.props.ch_day)
		    ), 
		    React.createElement(Col, {sm: 3}, 
		    React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "next"), round: true}, "下一天")	
		    )
		  )
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g", id: "div_detail"}, 
		 React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		 React.createElement("label", null, "早餐:"), 
		 React.createElement(CookbookPlanShow_EventRow, {uuids: o.time_1, type: "time_1"}), 
		 React.createElement("div", {className: "cls"}), 
		 React.createElement("br", null), 
		 React.createElement("label", null, "早上加餐:"), 
		 React.createElement(CookbookPlanShow_EventRow, {uuids: o.time_2, type: "time_2"}), 
		 React.createElement("div", {className: "cls"}), 
		 React.createElement("br", null), 
		 React.createElement("label", null, "午餐:"), 
		 React.createElement(CookbookPlanShow_EventRow, {uuids: o.time_3, type: "time_3"}), 
		 React.createElement("div", {className: "cls"}), 
		 React.createElement("br", null), 
		 React.createElement("label", null, "下午加餐:"), 
		 React.createElement(CookbookPlanShow_EventRow, {uuids: o.time_4, type: "time_4"}), 
		 React.createElement("div", {className: "cls"}), 
		 React.createElement("br", null), 
		 React.createElement("label", null, "晚餐:"), 
		 React.createElement(CookbookPlanShow_EventRow, {uuids: o.time_5, type: "time_5"}), 
		 React.createElement("div", {className: "cls"}), 
		 React.createElement("br", null), 
		 React.createElement("label", null, "营养分析:"), 
		 React.createElement("div", {className: "g_analysis"}, 
			React.createElement("div", {dangerouslySetInnerHTML: {__html:G_textToHTML(o.analysis)}})
		 )
		)
		)
	   
	   )
);
}
}); 

//end cookbookPlan



//classnews
var Classnews_EventRow = React.createClass({displayName: "Classnews_EventRow", 
render: function() {
var event = this.props.event;
var className = event.highlight ? 'am-active' :
event.disabled ? 'am-disabled' : '';

return (
React.createElement("tr", {className: className}, 
React.createElement("td", null, 
React.createElement("input", {type: "checkbox", value: event.uuid, name: "table_checkbox"})
), 
  React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: btn_click_classnews.bind(this,"show", event)}, event.title)), 
  React.createElement("td", null, event.create_user), 
  React.createElement("td", null, event.update_time), 
  React.createElement("td", null, event.reply_time)
  
) 
);
}
}); 

var Classnews_EventsTable = React.createClass({displayName: "Classnews_EventsTable",
	handleClick: function(m) {
		if(m=="add"){
			 btn_click_classnews(m,{classuuid:$('#selectclass_uuid').val()});
			 return;
		 }if(m=="edit"){
			
			 var uuids=null;
			 $($("input[name='table_checkbox']")).each(function(){
				
				　if(this.checked){
					 if(uuids==null)uuids=this.value;
					 else
					　uuids+=','+this.value ;    //遍历被选中CheckBox元素的集合 得到Value值
				　}
				});
			  if(!uuids){
				  alert("请勾选复选框！");
				  return;
			  }
			  if(!uuids&&uuids.indexOf(",")>-1){
					alert("只能选择一个进行编辑！");
					return;
				}
			  btn_click_classnews(m,{uuid:uuids});
		 }
		else if(m=="pre"){
			 if(g_classnews_pageNo_point==1)return;
			 ajax_classnews_list(g_classnews_classuuid,--g_classnews_pageNo_point);
			 return;
		 }else if(m=="next"){
			 ajax_classnews_list(g_classnews_classuuid,++g_classnews_pageNo_point);
			 return;
		 }
	  },
	  
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
	  },
	  //
	  handleChange_select_classnews_type:function(){
		  ajax_classnews_list($('#select_classnews_type').val());
	  },
	  handleChange_selectclass_uuid:function(){
		  ajax_classnews_list($('#selectclass_uuid').val());
	  },
render: function() {
	var totalCount=this.props.events.totalCount;
	var pageSize=this.props.events.pageSize;
	var maxPageNo=Math.floor(totalCount/pageSize)+1;
	var that=this;
	var pre_disabled=g_classnews_pageNo_point<2;
	var next_disabled=g_classnews_pageNo_point>=maxPageNo;
return (
React.createElement("div", null, 
React.createElement(AMUIReact.ButtonToolbar, null, 
	    React.createElement(AMUIReact.Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add"), round: true}, "添加"), 
	    React.createElement(AMUIReact.Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "edit"), round: true}, "编辑")
	 ), 
	  React.createElement("hr", null), 
	  
	  React.createElement(AMR_Button, {amStyle: "secondary", disabled: pre_disabled, onClick: this.handleClick.bind(this, "pre"), round: true}, "« 上一页"), 
	  React.createElement("label", null, g_classnews_pageNo_point, "\\", maxPageNo), 
	    React.createElement(AMR_Button, {amStyle: "secondary", disabled: next_disabled, onClick: this.handleClick.bind(this, "next"), round: true}, "下一页 »"), 
	      React.createElement("select", {id: "selectclass_uuid", name: "class_uuid", value: this.props.class_uuid, onChange: this.handleChange_selectclass_uuid}, 
	      React.createElement("option", {value: ""}, "所有"), 
	      this.props.class_list.map(function(event) {
	          return (React.createElement("option", {value: event.uuid}, event.name));
	        })
	      ), 
React.createElement(AMUIReact.Table, React.__spread({},  this.props), 
  React.createElement("thead", null, 
    React.createElement("tr", null, 
    	React.createElement("th", null, 
      React.createElement("input", {type: "checkbox", id: "id_checkbox_all", onChange: this.handleChange_checkbox_all})
      ), 
      React.createElement("th", null, "标题"), 
      React.createElement("th", null, "创建人"), 
      React.createElement("th", null, "更新时间"), 
      React.createElement("th", null, "回复时间")
    )
  ), 
  React.createElement("tbody", null, 
    this.props.events.data.map(function(event) {
      return (React.createElement(Classnews_EventRow, {event: event}));
    })
  )
)
)
);
}
});

var Classnews_edit = React.createClass({displayName: "Classnews_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editClassnewsForm').serializeJson());
	  },
	  componentDidMount:function(){
		 var editor=$('#classnews_content').xheditor(xhEditor_upImgOption_emot);
		  w_img_upload_nocut.bind_onchange("#file_img_upload",function(imgurl){
			  editor.pasteHTML('<img  width="198" height="198" src="'+imgurl+'"/>') 
		  });
		
	},
render: function() {
	  var o = this.state;
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
		  React.createElement("form", {id: "editClassnewsForm", method: "post", className: "am-form"}, 
			React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
			React.createElement("input", {type: "hidden", name: "classuuid", value: o.classuuid}), 
			React.createElement("label", {htmlFor: "title"}, "标题:"), 
		      React.createElement("input", {type: "text", name: "title", id: "title", value: o.title, onChange: this.handleChange, placeholder: "不超过128位"}), 
		      React.createElement("br", null), 
		      React.createElement(AMR_Input, {id: "classnews_content", type: "textarea", rows: "10", label: "内容:", placeholder: "填写内容", name: "content", value: o.content, onChange: this.handleChange}), 
  			G_upload_img_Div, 
		      React.createElement("button", {type: "button", onClick: ajax_classnews_save, className: "am-btn am-btn-primary"}, "提交")
		    )

	     )
	   )
	   
	   )
);
}
}); 



var Classnews_show = React.createClass({displayName: "Classnews_show", 
render: function() {
	  var o = this.props.formdata;
	 
	  if(!o.dianzanList)o.dianzanList=[];
  return (
		  React.createElement("div", null, 
		  React.createElement(AMUIReact.Article, {
		    title: o.title, 
		    meta: o.create_user+" | "+Store.getClassNameByUuid(o.classuuid)+" | "+o.update_time+" | 阅读"+this.props.count+"次"}, 
			React.createElement("div", {dangerouslySetInnerHTML: {__html: o.content}})
		   ), 	
		  React.createElement(Common_Dianzan_show, {uuid: o.uuid, type: 0}), 
		  React.createElement(Common_reply_list, {uuid: o.uuid, type: 0}), 
		  React.createElement(Common_reply_save, {uuid: o.uuid, type: 0})
		    )
		   
  );
}
}); 



//end classnews





//accounts
var Accounts_EventRow = React.createClass({displayName: "Accounts_EventRow", 
render: function() {
  var event = this.props.event;
  var className = event.highlight ? 'am-active' :
    event.disabled ? 'am-disabled' : '';

  return (
    React.createElement("tr", {className: className}, 
    React.createElement("td", null, " ", Vo.get("KD_Accounts_type_"+event.type)), 
    React.createElement("td", null, event.title), 
    React.createElement("td", null, " ", event.num), 
      React.createElement("td", null, G_getDateYMD(event.accounts_time)), 
     
      React.createElement("td", null, " ", event.studentname), 
      React.createElement("td", null, " ", Store.getClassByUuid(event.classuuid).name), 
      React.createElement("td", null, Store.getGroupNameByUuid(event.groupuuid)), 
      React.createElement("td", null, " ", event.description), 
      React.createElement("td", null, event.create_user), 
      React.createElement("td", null, event.create_time)
    ) 
  );
}
}); 

var Accounts_EventsTable = React.createClass({displayName: "Accounts_EventsTable",
	handleClick: function(m) {
		if(m=="add"){
			btn_click_accounts(m,{groupuuid:$('#selectgroup_uuid' ).val()});
			 return;
		 }
	  },
	  handleChange_selectgroup_uuid: function(){
		  ajax_accounts_listByGroup($( '#selectgroup_uuid' ).val());
    },
render: function() {
  return (
  React.createElement("div", null, 
  React.createElement("div", {className: "header"}, 
	  React.createElement("div", {className: "am-g"}, 
	    React.createElement("h1", null, "收支记录")
	  ), 
	  React.createElement("hr", null)
	), 
  React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add"), round: true}, "添加")
	  ), 
	  React.createElement("hr", null), 
	  React.createElement("div", {className: "am-form-group"}, 
	    React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'lg'}", value: this.props.group_uuid, onChange: this.handleChange_selectgroup_uuid}, 
	    this.props.group_list.map(function(event) {
	        return (React.createElement("option", {value: event.uuid}, event.brand_name));
	      })
	    )
	  ), 
    React.createElement(AMR_Table, React.__spread({},  this.props), 
      React.createElement("thead", null, 
        React.createElement("tr", null, 
          React.createElement("th", null, "类型"), 
          React.createElement("th", null, "内容"), 
          React.createElement("th", null, "金额"), 
          React.createElement("th", null, "收费时间"), 
          
          React.createElement("th", null, "学生"), 
          React.createElement("th", null, "班级"), 
          React.createElement("th", null, "学校"), 
          React.createElement("th", null, "备注"), 
          React.createElement("th", null, "创建人"), 
          React.createElement("th", null, "创建时间")
        )
      ), 
      React.createElement("tbody", null, 
        this.props.events.map(function(event) {
          return (React.createElement(Accounts_EventRow, {key: event.id, event: event}));
        })
      )
    )
    )
  );
}
});
  
var Accounts_edit = React.createClass({displayName: "Accounts_edit", 
	 getInitialState: function() {
		    return this.loadData(this.props.formdata);
		  },
	 handleChange: function(event) {
		 	var formdata=$('#editAccountsForm').serializeJson();
		 	
		    this.setState(this.loadData(formdata));
	  },
	  loadData:function(formdata){
		  
		  formdata.tmp_classList=Store.getChooseClass(formdata.groupuuid);
		  if(formdata.classuuid){
			  formdata.tmp_studentList=Store.getClassStudentsList(formdata.classuuid);
		  }else{
			  formdata.tmp_studentList=[];
		  }
		  return formdata;
	  },
	  
render: function() {
	  var o = this.state;
  return (
  		React.createElement("div", null, 
  		React.createElement("div", {className: "header"}, 
  		  React.createElement("div", {className: "am-g"}, 
  		    React.createElement("h1", null, "收支记录")
  		  ), 
  		  React.createElement("hr", null)
  		), 
  		React.createElement("div", {className: "am-g"}, 
  		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
  		React.createElement("form", {id: "editAccountsForm", method: "post", className: "am-form"}, 
  		React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
  		 React.createElement("div", {className: "am-form-group"}, 
	          React.createElement("select", {id: "groupuuid", name: "groupuuid", "data-am-selected": "{btnSize: 'lg'}", value: o.groupuuid, onChange: this.handleChange}, 
	          this.props.group_list.map(function(event) {
	              return (React.createElement("option", {value: event.uuid}, event.brand_name));
	            })
	          )
	        ), 
  		React.createElement("label", {htmlFor: "type"}, "收支类型:"), 
  		 React.createElement("div", {className: "am-form-group"}, 
  		React.createElement("select", {id: "type", name: "type", "data-am-selected": "{btnSize: 'lg'}", value: o.type, onChange: this.handleChange}, 
  		 this.props.type_list.map(function(event) {
             return (React.createElement("option", {value: event.key}, event.val));
           })
        ), 
        React.createElement("select", {id: "classuuid", name: "classuuid", "data-am-selected": "{btnSize: 'lg'}", value: o.classuuid, onChange: this.handleChange}, 
 		React.createElement("option", {value: "0"}, "班级选择"), 
 		o.tmp_classList.map(function(event) {
            return (React.createElement("option", {value: event.uuid}, event.name));
          })
       ), 
       React.createElement("select", {id: "studentuuid", name: "studentuuid", "data-am-selected": "{btnSize: 'lg'}", value: o.studentuuid, onChange: this.handleChange}, 
   	React.createElement("option", {value: "0"}, "学生选择"), 
   	o.tmp_studentList.map(function(event) {
        return (React.createElement("option", {value: event.uuid}, event.name));
      })
      )
        ), 
  	      React.createElement("br", null), 
 	    
  	    React.createElement("label", {htmlFor: "accounts_timeStr"}, "收支日期:"), 
  	    React.createElement(AMUIReact.DateTimeInput, {format: "YYYY-MM-DD", name: "accounts_timeStr", id: "accounts_timeStr", dateTime: o.accounts_time, showTimePicker: false, onChange: this.handleChange}), 
  	       React.createElement("label", {htmlFor: "title"}, "内容:"), 
  	      React.createElement("input", {type: "text", name: "title", id: "title", value: o.title, onChange: this.handleChange, placeholder: "不超过64位"}), 
  	      React.createElement("br", null), 
  	
  	       React.createElement("label", {htmlFor: "num"}, "金额:"), 
  	      React.createElement("input", {type: "number", name: "num", id: "num", value: o.num, onChange: this.handleChange, placeholder: ""}), 
  	    React.createElement("label", {htmlFor: "description"}, "备注:"), 
	      React.createElement("input", {type: "text", name: "description", id: "description", value: o.description, onChange: this.handleChange, placeholder: "不超过100位"}), 
	      React.createElement("br", null), 
	      React.createElement("button", {type: "button", onClick: ajax_accounts_saveAndAdd, className: "am-btn am-btn-primary"}, "保存继续"), 
  	      React.createElement("button", {type: "button", onClick: ajax_accounts_save, className: "am-btn am-btn-primary"}, "保存返回")
  	     )

  	     )
  	   )
  	   
  	   )
  );
}
}); 
//end accounts

//Div_body_index reg
var Div_body_index = React.createClass({displayName: "Div_body_index", 
	componentDidMount:function(){
		(BAIDU_DUP=window.BAIDU_DUP||[]).push(['fillAsync','1110291','baidu_dup_1110291']);
	},
	render: function() {
	return (
		React.createElement("div", null, 
		React.createElement("div", {id: "baidu_dup_1110291"}), 
		React.createElement(AMUIReact.Gallery, React.__spread({},   this.props))
		)
		
	);
	}
}); 
/*
 *(校务管理)<校园列表>绘制 ;
 *@handleClick:绑定的事件根据M来区分点击事件并做处理；
 *@add:添加分校;
 *@edit:
 * */
var Group_EventsTable = React.createClass({displayName: "Group_EventsTable",
	handleClick: function(m) {
		if(m=="add"){
			btn_click_group(m,{type:"1"});
			 return;
		 }if(m=="edit"){
			
			 var uuids=null;
			 $($("input[name='table_checkbox']")).each(function(){
				
				　if(this.checked){
					 if(uuids==null)uuids=this.value;
					 else
					　uuids+=','+this.value ;    //遍历被选中CheckBox元素的集合 得到Value值
				　}
				});
			  if(!uuids){
				  alert("请勾选复选框！");
				  return;
			  }
			  if(!uuids&&uuids.indexOf(",")>-1){
					alert("只能选择一个进行编辑！");
					return;
				}
			  btn_click_group(m,{uuid:uuids});
		 }
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
	  },
  render: function() {
    return (
    React.createElement("div", null, 
    React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add"), round: true}, "添加分校")
	  ), 
	  React.createElement("hr", null), 
      React.createElement(AMR_Table, React.__spread({},  this.props), 
        React.createElement("thead", null, 
          React.createElement("tr", null, 
          React.createElement("th", null, 
          React.createElement("input", {type: "checkbox", id: "id_checkbox_all", onChange: this.handleChange_checkbox_all})
          ), 
            React.createElement("th", null, "品牌名"), 
            React.createElement("th", null, "机构全称"), 
            React.createElement("th", null, "电话"), 
            React.createElement("th", null, "公司地址"), 
            React.createElement("th", null, "创建时间")
          )
        ), 
        React.createElement("tbody", null, 
          this.props.events.map(function(event) {
            return (React.createElement(Group_EventRow, {key: event.id, event: event}));
          })
        )
      )
      )
    );
  }
});
    
    /*
     * (校务管理)<校园列表>添加分校绘制界面；
     * */    
    var Group_edit = React.createClass({displayName: "Group_edit", 
    	 getInitialState: function() {
    		    return this.props.formdata;
    		  },
    	 handleChange: function(event) {
    		    this.setState($('#editGroupForm').serializeJson());
    	  },
    	  componentDidMount:function(){
    			  var editor=$('#description').xheditor(xhEditor_upImgOption_mfull);
              w_img_upload_nocut.bind_onchange("#file_img_upload" ,function(imgurl){
                    editor.pasteHTML( '<img  width="198" height="198" src="'+imgurl+'"/>')
              });
    	},
      render: function() {
    	  var o = this.state;
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
        		  
        		React.createElement("form", {id: "editGroupForm", method: "post", className: "am-form"}, 
        		React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
        	    React.createElement("input", {type: "hidden", name: "type", value: o.type}), 
        	      React.createElement("label", {htmlFor: "brand_name"}, "品牌名:"), 
        	      React.createElement("input", {type: "text", name: "brand_name", id: "brand_name", value: o.brand_name, onChange: this.handleChange, placeholder: "不超过45位"}), 
        	      React.createElement("br", null), 
        	       React.createElement("label", {htmlFor: "company_name"}, "机构全称:"), 
        	      React.createElement("input", {type: "text", name: "company_name", id: "company_name", value: o.company_name, onChange: this.handleChange, placeholder: "不超过45位"}), 
        	      React.createElement("br", null), 
        	       React.createElement("label", {htmlFor: "address"}, "公司地址:"), 
        	      React.createElement("input", {type: "text", name: "address", id: "address", value: o.address, onChange: this.handleChange, placeholder: "不超过64位"}), 
        	      React.createElement("br", null), 
        	       React.createElement("label", {htmlFor: "map_point"}, "地址坐标:"), 
        	      React.createElement("input", {type: "text", name: "map_point", id: "map_point", value: o.map_point, onChange: this.handleChange, placeholder: "拾取坐标后，复制到这里。格式：1.1,1.1"}), 
        	      React.createElement("a", {href: "http://api.map.baidu.com/lbsapi/getpoint/index.html", target: "_blank"}, "坐标拾取"), 
        	      React.createElement("br", null), 
        	       React.createElement("label", {htmlFor: "link_tel"}, "公司电话:"), 
        	      React.createElement("input", {type: "text", name: "link_tel", id: "link_tel", value: o.link_tel, onChange: this.handleChange, placeholder: ""}), 
        	      React.createElement("br", null), 
        	      React.createElement(AMR_Input, {id: "description", type: "textarea", rows: "50", label: "校园介绍:", placeholder: "校园介绍", name: "description", value: o.description, onChange: this.handleChange}), 
        		  	G_upload_img_Div, 
        	      React.createElement("button", {type: "button", onClick: ajax_group_save, className: "am-btn am-btn-primary"}, "提交")
        	     )

        	     )
        	   )
        	   
        	   )
        );
      }
    }); 

//——————————————————————————家长通讯录<绘制>—————————————————————————— 
/*
 * 通讯录学生家长联系详情绘制；
 * @{this.props.formdata.map(function(event)：
 * 封装好的一个MAP方法只对数组起作用，其内部自己For循环;
 * @event:Map方法用event.XX调用数组内 数据；
 * @amStyle:按钮颜色；
 * @parent_uuid:老师给每个用户的ID发message时需要的参数;
 * web页面一键电话功能<a href={"tel:"+event.tel}></a>;
 * */
var Class_student_tel =React.createClass({displayName: "Class_student_tel",	 
		render: function() {
	     var o =this.state;	
		 return (
		 		React.createElement("div", null, 
			    React.createElement(AMUIReact.List, {static: true}, 
		    	this.props.formdata.map(function(event) {
		            return (React.createElement(AMUIReact.ListItem, null, event.student_name, "的", event.typename, ":", event.tel, 
		            React.createElement(AMR_ButtonToolbar, null, 
		            React.createElement("a", {href: "tel:"+event.tel}, React.createElement(AMUIReact.Button, {amStyle: "disable"}, "电话"), " "), 
		            
		            React.createElement(AMUIReact.Button, {onClick: ajax_parentContactByMyStudent_message_list.bind(this,event.parent_uuid), amStyle: "success"}, "@信息")	
		            
		            )
		            ));
		          })		      			      
			      )
		 	     ) 
		     );
	        }
		 });
/* 
 * 家长通讯录中的<信息>绘制舞台
 * @逻辑：绘制一个Div 每次点击加载更多按钮事把 新的一个Div添加到舞台上；
 * @我要发信息 加载更多等模板和按钮在此处添加上舞台 和DIV<信息>分离开；
 * @Parent_message_save我要保存模板；
 * */
var ParentContactByMyStudent_message_list = React.createClass({displayName: "ParentContactByMyStudent_message_list", 
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
		var re_data=ajax_message_queryByParent(this.props.parent_uuid,this.classnewsreply_list_div+this.pageNo,this.pageNo);
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
		  React.createElement("div", null, 
		   React.createElement("div", {id: this.classnewsreply_list_div}
		   
		   ), 
			React.createElement("button", {id: this.load_more_btn_id, type: "button", onClick: this.load_more_data.bind(this), className: "am-btn am-btn-primary"}, "加载更多"), 
			React.createElement(Parent_message_save, {uuid: this.props.parent_uuid})
			)
			
  );
}
}); 

/*
 * 我要发信息模块；(家长通讯录发信息)
 * */
var Parent_message_save = React.createClass({displayName: "Parent_message_save", 
	classnewsreply_list_div:"classnewsreply_list_div",
	componentDidMount:function(){
		 var editor=$( '#classnews_content_replay').xheditor(xhEditor_upImgOption_emot);
         w_img_upload_nocut.bind_onchange("#file_img_upload" ,function(imgurl){
               editor.pasteHTML( '<img  width="198" height="198" src="'+imgurl+'"/>')
         });

	},
	reply_save_btn_click:function(){
		ajax_parent_message_save();
	},
render: function() {
  return (
		   React.createElement("form", {id: "editForm", method: "post", className: "am-form"}, 
			React.createElement("input", {type: "hidden", name: "revice_useruuid", value: this.props.uuid}), 
			
			React.createElement(AMR_Input, {id: "classnews_content_replay", type: "textarea", rows: "10", label: "信息发送", placeholder: "填写内容", name: "message"}), 
   G_upload_img_Div, 
		      React.createElement("button", {type: "button", onClick: this.reply_save_btn_click.bind(this), className: "am-btn am-btn-primary"}, "发送")
		      
		    )	   
  );
}
}); 
/* 首页家长通讯录功能2级发信息界面功能
 * @ 绘制 信息
 * */
var Message_queryByParent_listpage =React.createClass({displayName: "Message_queryByParent_listpage",	 
	render: function() {
	  return (
			  React.createElement("div", null, 
			 
			  this.props.events.data.map(function(event) {
			      return (React.createElement(AMUIReact.ListItem, null, event.name, ":", event.message))
			  })
			    )
			   
	  );
	}
})
   

//——————————————————————————园长信箱<绘制>——————————————————————————        
    /*
     * <园长信箱>一层界面绘制;
     * @send_user:家长名字；
     * @send_useruuid:幼儿园ID；
     * @revice_useruuid：家长ID；
     * @ajax_boss_message_list绑定事件然后开始绘制舞台；
     * */
    var Boss_student_tel =React.createClass({displayName: "Boss_student_tel", 	 
    		render: function() {
    	     var o =this.state;	
    		 return (
    		 		React.createElement("div", null, 
    			    React.createElement(AMUIReact.List, {static: true}, 
    		    	this.props.formdata.map(function(event) {
    		            return (React.createElement(AMUIReact.ListItem, null, "家长", event.send_user, "的信息",     
    		            React.createElement(AMR_ButtonToolbar, null, 		            
    		            React.createElement(AMUIReact.Button, {onClick: ajax_boss_message_list.bind(this,event.send_useruuid,event.revice_useruuid), amStyle: "success"}, "@信息"), "你们总共发了", event.count, "条信息"
    		            )	
            
    		            ));
    		          })		      			      
    			      )
    		 	     ) 
    		     );
    	        }
    		 });
/* 
 * <园长信箱>绘制舞台
 * @ajax_message_queryByParent：园长信箱2层详情界面服务器请求‘
 * @逻辑：绘制一个Div 每次点击加载更多按钮事把 新的一个Div添加到舞台上；
 * @我要发信息 加载更多等模板和按钮在此处添加上舞台 和DIV<信息>分离开；
 * @revice_useruuid:家长ID；
 * @send_useruuid:幼儿园ID；
 * @Boss_message_save我要保存模板
 * */
var Boss_message_stage = React.createClass({displayName: "Boss_message_stage", 
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
		var re_data=ajax_boss_message(this.props.send_useruuid,this.props.revice_useruuid,this.classnewsreply_list_div+this.pageNo,this.pageNo);
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
		  React.createElement("div", null, 
		   React.createElement("div", {id: this.classnewsreply_list_div}
		   
		   ), 
			React.createElement("button", {id: this.load_more_btn_id, type: "button", onClick: this.load_more_data.bind(this), className: "am-btn am-btn-primary"}, "加载更多"), 
			React.createElement(Boss_message_save, {send_useruuid: this.props.send_useruuid, revice_useruuid: this.props.revice_useruuid})
			)
			
  );
}
}); 

/*
 *<园长信箱>发送信息模板
 *@ajax_boss_message_save：发送信息服务器请求；
 * * @revice_useruuid:家长ID；
 * @send_useruuid:幼儿园ID；
 * */
var Boss_message_save = React.createClass({displayName: "Boss_message_save", 
	classnewsreply_list_div:"classnewsreply_list_div",
	componentDidMount:function(){
		$('#classnews_content_replay').xheditor(xhEditor_upImgOption_emot);
	},
	reply_save_btn_click:function(){
		ajax_boss_message_save();
	},
render: function() {
  return (
		   React.createElement("form", {id: "editForm", method: "post", className: "am-form"}, 
		   React.createElement("input", {type: "hidden", name: "revice_useruuid", value: this.props.revice_useruuid}), 
			React.createElement("input", {type: "hidden", name: "send_useruuid", value: this.props.send_useruuid}), 			
			React.createElement(AMR_Input, {id: "classnews_content_replay", type: "textarea", rows: "10", label: "信息发送", placeholder: "填写内容", name: "message"}), 
		      React.createElement("button", {type: "button", onClick: this.reply_save_btn_click.bind(this), className: "am-btn am-btn-primary"}, "发送")
		      
		    )	   
  );
}
}); 


/* <园长信箱>2层发信息详情界面绘制；
 * @send_user：信息者名字；
 * @message：信息内容；
 * */
var Message_queryLeaderMsgByParents_listpage =React.createClass({displayName: "Message_queryLeaderMsgByParents_listpage",	 
	render: function() {
	  return (
			  React.createElement("div", null, 			 
			  this.props.events.data.map(function(event) {
			      return (React.createElement(AMUIReact.ListItem, null, event.send_user, ":", event.message))
			  })
			    )
			   
	  );
	}
})
