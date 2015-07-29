
//
var AMR_Table=AMUIReact.Table;
var AMR_ButtonToolbar=AMUIReact.ButtonToolbar;
var AMR_Button=AMUIReact.Button;
var AMR_Sticky=AMUIReact.Sticky;
var AMR_Panel=AMUIReact.Panel;
var AMR_Gallery=AMUIReact.Gallery;
var AMR_Input=AMUIReact.Input;


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
		    
		    React.createElement("select", {id: "reg_group_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'sm'}"}, 
		      this.props.group_list.map(function(event) {
		          return (React.createElement("option", {value: event.uuid}, event.company_name));
		        })
		      )
		        ), 
		      
		      React.createElement("label", {htmlFor: "tel"}, "手机号码:"), 
		      React.createElement("input", {type: "text", name: "tel", id: "tel", placeholder: ""}), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "name"}, "昵称:"), 
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
			      React.createElement("label", {htmlFor: "name"}, "昵称:"), 
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
		 	o.pw_checked=cbox.prop("checked")?"checked":"";
		    this.setState();
	  },
render: function() {
	  var o = this.state;
 return (
 		React.createElement("div", null, 
 		React.createElement("div", {className: "header"}, 
 		  React.createElement("div", {className: "am-g"}, 
 		 React.createElement("h1", null, "幼儿园老师登录"), 
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
 	        React.createElement("input", {type: "button", name: "", value: "忘记密码 ^_^? ", className: "am-btn am-btn-default am-btn-sm am-fr"})
 	      ), 
 	      React.createElement("a", {href: "javascript:menu_kd_group_reg_fn();"}, "幼儿园注册"), "|", 
 	     React.createElement("a", {href: "javascript:menu_userinfo_reg_fn();"}, "老师注册"), 
 	      React.createElement("br", null), 
 	      
 	     React.createElement("a", {href: "http://120.25.248.31/px-rest/kd/"}, " ", React.createElement("img", {src: "ewm_kd.png"}))
 	    
 			
 	    ), 
 	    React.createElement("hr", null), 
 	    React.createElement("p", null, "© 2015 PX, Inc. ")

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
      React.createElement("td", {onClick: ajax_group_edit.bind(this,event)}, event.brand_name), 
        React.createElement("td", {onClick: ajax_group_edit.bind(this,event)}, event.company_name), 
        React.createElement("td", {onClick: ajax_group_edit.bind(this,event)}, " ", event.link_tel), 
        React.createElement("td", {onClick: ajax_group_edit.bind(this,event)}, event.address), 
        React.createElement("td", {onClick: ajax_group_edit.bind(this,event)}, event.create_time)
      ) 
    );
  }
}); 

var Group_EventsTable = React.createClass({displayName: "Group_EventsTable",
	handleClick: function(m) {
		 if(this.props.handleClick){
			 this.props.handleClick(m);
		 }
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
	  },
  render: function() {
    return (
    React.createElement("div", null, 
    React.createElement(AMR_Sticky, null, 
    React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add_group"), round: true}, "添加分校")
	  )
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
    
var Group_edit = React.createClass({displayName: "Group_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editGroupForm').serializeJson());
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
    	      React.createElement("button", {type: "button", onClick: ajax_group_save, className: "am-btn am-btn-primary"}, "提交")
    	     )

    	     )
    	   )
    	   
    	   )
    );
  }
}); 

//userinfo
var Userinfo_EventRow = React.createClass({displayName: "Userinfo_EventRow", 
  render: function() {
    var event = this.props.event;
    var className = event.highlight ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      React.createElement("tr", {className: className}, 
      React.createElement("td", null, 
      React.createElement("input", {type: "checkbox", value: event.uuid, name: "table_checkbox"})
      ), 
        React.createElement("td", null, event.loginname), 
        React.createElement("td", null, event.name), 
        React.createElement("td", null, event.tel), 
        React.createElement("td", null, event.email), 
        React.createElement("td", null, event.sex=="0"?"男":"女"), 
        React.createElement("td", {className: "px_disable_"+event.disable}, event.disable=="1"?"禁用":"正常"), 
        React.createElement("td", null, event.login_time), 
        React.createElement("td", null, event.create_time)
      ) 
    );
  }
}); 

var Userinfo_EventsTable = React.createClass({displayName: "Userinfo_EventsTable",
	handleClick: function(m) {
		 if(this.props.handleClick){
			 if(m=="add_userinfo"){
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
		  ajax_uesrinfo_listByGroup($('#selectgroup_uuid').val());
	  },
  render: function() {
    return (
    React.createElement("div", null, 
    React.createElement(AMR_Sticky, null, 
    React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add_userinfo"), round: true}, "添加老师"), 
	    React.createElement(AMR_Button, {amStyle: "success", onClick: this.handleClick.bind(this, "add_enable"), round: true}, "启用"), 
	    React.createElement(AMR_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "add_disable"), round: true}, "禁用"), 
	    React.createElement(AMR_Button, {amStyle: "success", onClick: this.handleClick.bind(this, "add_enable"), round: true}, "分配权限")
	    )
	), 
	  React.createElement("hr", null), 
	  React.createElement("div", {className: "am-form-group"}, 
      React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'sm'}", value: this.props.group_uuid, onChange: this.handleChange_selectgroup_uuid}, 
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
            React.createElement("th", null, "昵称"), 
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
    
var Userinfo_edit = React.createClass({displayName: "Userinfo_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editUserinfoForm').serializeJson());
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
    		  React.createElement("form", {id: "editUserinfoForm", method: "post", className: "am-form"}, 
    			React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
    		     React.createElement("input", {type: "hidden", name: "type", value: "1"}), 
    		    React.createElement("div", {className: "am-form-group"}, 
    		          React.createElement("select", {id: "group_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'sm'}", value: o.group_uuid, onChange: this.handleChange}, 
    		          this.props.group_list.map(function(event) {
    		              return (React.createElement("option", {value: event.uuid}, event.company_name));
    		            })
    		          )
    		        ), 
    		      React.createElement("label", {htmlFor: "tel"}, "手机号码:"), 
    		      React.createElement("input", {type: "text", name: "tel", id: "tel", value: o.tel, onChange: this.handleChange, placeholder: ""}), 
    		      React.createElement("br", null), 
    		      React.createElement("label", {htmlFor: "name"}, "昵称:"), 
    		      React.createElement("input", {type: "text", name: "name", id: "name", value: o.name, onChange: this.handleChange, placeholder: "不超过15位"}), 
    		      React.createElement("br", null), 
    		       React.createElement("label", {htmlFor: ""}, "Email:"), 
    		      React.createElement("input", {type: "email", name: "email", id: "email", value: o.email, onChange: this.handleChange, placeholder: "输入邮箱", placeholder: ""}), 
    		      React.createElement("br", null), 
    		      React.createElement("label", {htmlFor: "password"}, "密码:"), 
    		      React.createElement("input", {type: "password", name: "password", id: "password", value: o.password, onChange: this.handleChange}), 
    		      React.createElement("br", null), 
    		      
    		      React.createElement("label", {htmlFor: "password1"}, "重复密码:"), 
    		      React.createElement("input", {type: "password", name: "password1", id: "password1", value: o.password1, onChange: this.handleChange}), 
    		      React.createElement("br", null), 
    		      React.createElement("button", {type: "button", onClick: ajax_userinfo_save, className: "am-btn am-btn-primary"}, "提交")
    		    )

    	     )
    	   )
    	   
    	   )
    );
  }
}); 
//end userinfo




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
      React.createElement("td", null, React.createElement("a", {href: "javascript:react_ajax_class_students_manage('"+event.uuid+"')"}, event.name)), 
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
  React.createElement(AMR_Sticky, null, 
  React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add_class"), round: true}, "添加班级"), 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "edit_class"), round: true}, "编辑"), 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "graduate_class"), round: true}, "毕业")
	  )
	  ), 
	  React.createElement("hr", null), 
	  React.createElement("div", {className: "am-form-group"}, 
    React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'sm'}", value: this.props.group_uuid, onChange: this.handleChange_selectgroup_uuid}, 
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
  		          React.createElement("select", {id: "groupuuid", name: "groupuuid", "data-am-selected": "{btnSize: 'sm'}", value: o.groupuuid, onChange: this.handleChange}, 
  		          this.props.group_list.map(function(event) {
  		              return (React.createElement("option", {value: event.uuid}, event.company_name));
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
	  React.createElement(AMR_Sticky, null, 
	  React.createElement(AMR_ButtonToolbar, null, 
		    React.createElement(AMR_Button, {amStyle: "primary", onClick: class_students_manage_onClick.bind(this, "add",this.props.formdata.uuid), round: true}, "添加学生")
		  )
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
		      React.createElement(AMUIReact.FormGroup, null, 
		      React.createElement("label", null, "单选："), 
		      React.createElement(AMUIReact.Input, {type: "radio", name: "sex", value: "0", label: "男", inline: true, onChange: this.handleChange, checked: o.sex==0?"checked":""}), 
		      React.createElement(AMUIReact.Input, {type: "radio", name: "sex", value: "1", label: "女", inline: true, onChange: this.handleChange, checked: o.sex==1?"checked":""})
		    ), 
		      React.createElement("label", {htmlFor: "birthday"}, "生日:"), 

			React.createElement(AMUIReact.DateTimeInput, {format: "YYYY-MM-DD", name: "birthday", id: "birthday", dateTime: o.birthday, onChange: this.handleChange}), 
 		      React.createElement("br", null), 
 		     React.createElement("label", {htmlFor: "nickname"}, "头像:"), 
 		    React.createElement(AMUIReact.Image, {id: "img_head_image", src: G_def_headImgPath, className: "G_img_header"}), 
 		   React.createElement("br", null), 
 		   React.createElement("button", {type: "button", onClick: btn_class_student_uploadHeadere, className: "am-btn am-btn-primary"}, "上传头像"), 
		      React.createElement("br", null), 
		      React.createElement("label", {htmlFor: "nickname"}, "妈妈电话:"), 
 		      React.createElement("input", {type: "text", name: "ma_tel", id: "ma_tel", value: o.ma_tel, onChange: this.handleChange, placeholder: ""}), 
 		      React.createElement("br", null), 
 		     React.createElement("label", {htmlFor: "nickname"}, "爸爸电话:"), 
		      React.createElement("input", {type: "text", name: "ba_tel", id: "ba_tel", value: o.ba_tel, onChange: this.handleChange, placeholder: ""}), 
		      React.createElement("br", null), 
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
		      
 		      React.createElement("button", {type: "button", onClick: btn_ajax_class_student_save, className: "am-btn am-btn-primary"}, "提交")
 		    )

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
      React.createElement("td", null, React.createElement("a", {href: "javascript:react_ajax_announce_show('"+event.uuid+"')"}, event.title)), 
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
  React.createElement(AMR_Sticky, null, 
  React.createElement(AMR_ButtonToolbar, null, 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add"), round: true}, "创建"), 
	    React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "edit"), round: true}, "编辑"), 
	    React.createElement(AMR_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "del"), round: true}, "删除")
	    )
	), 
	  React.createElement("hr", null), 
	  React.createElement("div", {className: "am-form-group"}, 
    React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'sm'}", value: this.props.group_uuid, onChange: this.handleChange_selectgroup_uuid}, 
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
  
var Announcements_edit = React.createClass({displayName: "Announcements_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editAnnouncementsForm').serializeJson());
	  },
	  componentDidMount:function(){
		  $('#announce_message').xheditor();
		  return;
		  if($.fn.xheditor){
			  $('#announce_message').xheditor();
		  }else{
			  loadJS("../js/xheditor/xheditor-1.2.2.min.js",function(){
					loadJS('../js/xheditor/zh-cn.js');
					$('#announce_message').xheditor();
				});
		  }
		 

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
  		          React.createElement("select", {id: "group_uuid", name: "groupuuid", "data-am-selected": "{btnSize: 'sm'}", value: o.group_uuid, onChange: this.handleChange}, 
  		          this.props.group_list.map(function(event) {
  		              return (React.createElement("option", {value: event.uuid}, event.company_name));
  		            })
  		          )
  		        ), 
  		        
  		    type_div, 
  		    
  		      React.createElement("label", {htmlFor: "name"}, "标题:"), 
  		      React.createElement("input", {type: "text", name: "title", id: "title", value: o.title, onChange: this.handleChange, maxlength: "45", placeholder: "不超过45位"}), 
  		      React.createElement("br", null), 
  		    React.createElement(AMR_Input, {id: "announce_message", type: "textarea", rows: "10", label: "内容:", placeholder: "填写内容", name: "message", value: o.message, onChange: this.handleChange}), 
  		      React.createElement("button", {type: "button", onClick: ajax_announcements_save, className: "am-btn am-btn-primary"}, "提交")
  		    )

  	     )
  	   )
  	   
  	   )
  );
}
}); 





var Announcements_show = React.createClass({displayName: "Announcements_show", 
render: function() {
	  var o = this.props.data;
  return (
		  React.createElement(AMUIReact.Article, {
		    title: o.title, 
		    meta: Vo.announce_type(o.type)+" | "+Store.getGroupNameByUuid(o.groupuuid)+" | "+o.create_time}, 
			React.createElement("div", {dangerouslySetInnerHTML: {__html: o.message}})
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
    React.createElement("td", null, React.createElement("a", {href: "javascript:btn_click_teachingplan('edit','"+event.uuid+"')"}, G_week.getWeekStr(event.plandate))), 
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
React.createElement(AMR_Sticky, null, 
React.createElement(AMR_ButtonToolbar, null, 
	React.createElement(AMR_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add",null,this.props.classuuid), round: true}, "添加"), 
    React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "pre"), round: true}, "上周"), 
    React.createElement(AMR_Button, {amStyle: "secondary", onClick: this.handleClick.bind(this, "next"), round: true}, "下周")
    )
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
    React.createElement("td", null, React.createElement("a", {href: "javascript:btn_click_cookbookPlan(null,'"+event.uuid+"')"}, G_week.getWeekStr(event.plandate))), 
    React.createElement("td", null, this.parseTimes(event.time_1)), 
    React.createElement("td", null, this.parseTimes(event.time_2)), 
    React.createElement("td", null, this.parseTimes(event.time_3)), 
    React.createElement("td", null, this.parseTimes(event.time_4)), 
    React.createElement("td", null, this.parseTimes(event.time_5))
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
  React.createElement("select", {id: "selectgroup_uuid", name: "group_uuid", "data-am-selected": "{btnSize: 'sm'}", value: this.props.group_uuid, onChange: this.handleChange_selectgroup_uuid}, 
  this.props.group_list.map(function(event) {
      return (React.createElement("option", {value: event.uuid}, event.company_name));
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
        React.createElement("th", null, "晚餐")
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
	 if(this.props.handleClick){
		 
		 if(m=="add"){
			 this.props.handleClick(m,$('#selectgroup_uuid').val());
			 return;
		 }else if(m=="pre"){
			 ajax_cookbookPlan_listByGroup($('#selectgroup_uuid').val(),--g_cookbookPlan_week_point);
			 return;
		 }else if(m=="next"){
			 ajax_cookbookPlan_listByGroup($('#selectgroup_uuid').val(),++g_cookbookPlan_week_point);
			 return;
		 }
		 
		 
	 }
},
//
handleChange_selectgroup_uuid:function(){
	ajax_cookbookPlan_listByGroup($('#selectgroup_uuid').val(),g_cookbookPlan_week_point);
}
});


var CookbookPlan_edit_EventRow = React.createClass({displayName: "CookbookPlan_edit_EventRow",
	
	 getInitialState: function() {
		    return {
	            items: []
	        };
		  },
	componentDidMount: function() {
		var lists=this.cookbookPlan_timeStr_to_list(this.props.uuids);
		  if (this.isMounted()) {
			   this.setState({
		            items: lists
		        });
			   
		  }
	    
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
return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "每日食谱-编辑")
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g"}, 
		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		  React.createElement("form", {id: "editCookbookPlanForm", method: "post", className: "am-form"}, 
		React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
		     React.createElement("input", {type: "hidden", name: "type", value: "1"}), 
		    React.createElement("div", {className: "am-form-group"}, 
		          React.createElement("select", {id: "groupuuid", name: "groupuuid", "data-am-selected": "{btnSize: 'sm'}", value: o.groupuuid, onChange: this.handleChange}, 
		          this.props.group_list.map(function(event) {
		              return (React.createElement("option", {value: event.uuid}, event.company_name));
		            })
		          )
		        ), 
		        React.createElement("label", {htmlFor: "name"}, "日期:"), 
				 React.createElement(AMUIReact.DateTimeInput, {format: "YYYY-MM-DD", name: "plandateStr", id: "plandateStr", dateTime: o.plandate, onChange: this.handleChange}), 
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
		      React.createElement("button", {type: "button", onClick: ajax_cookbookPlan_save, className: "am-btn am-btn-primary"}, "提交")
		    )

	     )
	   )
	   
	   )
);
}
}); 



//end class

//end cookbookPlan
