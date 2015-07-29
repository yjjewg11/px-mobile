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
 		 React.createElement("h1", null, "问界科技管理云平台"), 
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
 	      React.createElement("br", null), 
 	      
 	     React.createElement("a", {href: "http://120.25.248.31/px-rest/admin/"}, " ", React.createElement("img", {src: "ew_admin.png"}))
 	    
 			
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



//right
var Right_EventRow = React.createClass({displayName: "Right_EventRow", 
	tr_onClick:function(trid,cbid,e){
		var cbox=$("#"+cbid);
		var tr=$("#"+trid);
		if(tr.hasClass("am-active")){
				cbox.prop("checked",false); 
			tr.removeClass("am-active");
		}else{
				cbox.prop("checked", true); 
			tr.addClass("am-active");
		}
	},
	ajax_right_edit_onClick:function(s){
		ajax_right_button_handleClick("edit",s);
	},
	componentDidMount:function(){
		$(".am-active input[type='checkbox']").prop("checked",true); 
	},
  render: function() {
    var event = this.props.event;
    var is_Checked=this.props.chooselist&&this.props.chooselist.indexOf(event.uuid)>-1;
    var className = is_Checked ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
		 React.createElement("tr", {name: "table_tr_checkbox_right", id: "tr_chright_"+event.uuid, className: className}, 
	      React.createElement("td", {onClick: this.tr_onClick.bind(this,"tr_chright_"+event.uuid,"tb_cbox__chright"+event.uuid)}, 
	      React.createElement("input", {type: "checkbox", alt: event.name, value: event.uuid, id: "tb_cbox__chright"+event.uuid, name: "table_checkbox"})
	      ), 
        React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: this.ajax_right_edit_onClick.bind(this, JSON.stringify(event))}, event.name)), 
        React.createElement("td", null, event.description), 
        React.createElement("td", null, Vo.type(event.type))
      ) 
    );
  }
}); 

var Right_EventsTable = React.createClass({displayName: "Right_EventsTable",
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
		  if( $("#id_checkbox_all")[0].checked){
			  $('tr[name="table_tr_checkbox_right"]').addClass("am-active");
		  }else{
			  $('tr[name="table_tr_checkbox_right"]').removeClass("am-active");
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
            return (React.createElement(Right_EventRow, {chooselist: that.props.chooselist, event: event}));
          })
        )
      ), 
      React.createElement("button", {type: "button", onClick: ajax_right_button_handleClick.bind(this, "add_right",this.props.type), className: "am-btn am-btn-primary"}, "添加权限")
      )
    );
  }
});
    
var Right_edit = React.createClass({displayName: "Right_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editRightForm').serializeJson());
	  },
  render: function() {
	  var o = this.state;
    return (
    		React.createElement("div", null, 
    		React.createElement("div", {className: "header"}, 
    		  React.createElement("div", {className: "am-g"}, 
    		    React.createElement("h1", null, "编辑权限-【", Vo.type(o.type), "】")
    		  ), 
    		  React.createElement("hr", null)
    		), 
    		React.createElement("div", {className: "am-g"}, 
    		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
    		  React.createElement("form", {id: "editRightForm", method: "post", className: "am-form"}, 
    			React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
    			React.createElement("input", {type: "hidden", name: "type", value: o.type}), 
    		      React.createElement("label", {htmlFor: "name"}, "名字:"), 
    		      React.createElement("input", {type: "text", name: "name", id: "name", value: o.name, onChange: this.handleChange, placeholder: "不超过15位"}), 
    		      React.createElement("br", null), 
    		       React.createElement("label", {htmlFor: "description"}, "描述:"), 
    		      React.createElement("input", {type: "text", name: "description", id: "description", value: o.description, onChange: this.handleChange}), 
    		      React.createElement("button", {type: "button", onClick: ajax_right_save, className: "am-btn am-btn-primary"}, "提交")
    		    )

    	     )
    	   )
    	   
    	   )
    );
  }
}); 
//end right




//role
var Role_EventRow = React.createClass({displayName: "Role_EventRow", 
render: function() {
  var event = this.props.event;
  var className = event.highlight ? 'am-active' :
    event.disabled ? 'am-disabled' : '';

  return (
    React.createElement("tr", {className: className}, 
    React.createElement("td", null, 
    React.createElement("input", {type: "checkbox", value: event.uuid, name: "table_checkbox"})
    ), 
      React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: ajax_role_edit.bind(this, event)}, event.name)), 
      React.createElement("td", null, event.description), 
      React.createElement("td", null, Vo.type(event.type)), 
      React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: ajax_role_bind_right.bind(this, event)}, "绑定权限")
     )
    ) 
  );
}
}); 

var Role_EventsTable = React.createClass({displayName: "Role_EventsTable",
	handleClick: function(m) {
		 if(this.props.handleClick){
			 if(m=="add_role"){
				 this.props.handleClick(m,$('#select_role_type').val());
				 return;
			 } 
			 
			 var uuids=null;
			 $("input[name='table_checkbox']").each(function(){
				
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
			  
			  
			  if(m=="role_bind_right"){
				if(uuids.indexOf(",")>-1){
					 alert("只能选择一条数据!");
					  return;
				}
			  
			  }
			 this.props.handleClick(m,$('#selectgroup_uuid').val(),uuids);
			 
		 }
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
	  },
	  //
	  handleChange_select_role_type:function(){
		  ajax_role_list($('#select_role_type').val());
	  },
render: function() {
  return (
  React.createElement("div", null, 
  React.createElement(AMUIReact.ButtonToolbar, null, 
	    React.createElement(AMUIReact.Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add_role"), round: true}, "添加")
	 ), 
	  React.createElement("hr", null), 
	  React.createElement("div", {className: "am-form-group"}, 
    React.createElement("select", {id: "select_role_type", name: "group_uuid", value: this.props.type, onChange: this.handleChange_select_role_type}, 
    React.createElement("option", {value: "0"}, Vo.type(0)), 
    React.createElement("option", {value: "1"}, Vo.type(1))
    )
  ), 
  React.createElement("div", {className: "header"}, 
  React.createElement("div", {className: "am-g"}, 
    React.createElement("h1", null, "角色管理")
  ), 
  React.createElement("hr", null)
), 
    React.createElement(AMUIReact.Table, React.__spread({},  this.props), 
      React.createElement("thead", null, 
        React.createElement("tr", null, 
        	React.createElement("th", null, 
          React.createElement("input", {type: "checkbox", id: "id_checkbox_all", onChange: this.handleChange_checkbox_all})
          ), 
          React.createElement("th", null, "名称"), 
          React.createElement("th", null, "描述"), 
          React.createElement("th", null, "类型"), 
          React.createElement("th", null, "操作")
        )
      ), 
      React.createElement("tbody", null, 
        this.props.events.map(function(event) {
          return (React.createElement(Role_EventRow, {key: event.id, event: event}));
        })
      )
    )
    )
  );
}
});
  
var Role_edit = React.createClass({displayName: "Role_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editRoleForm').serializeJson());
	  },
render: function() {
	  var o = this.state;
  return (
  		React.createElement("div", null, 
  		React.createElement("div", {className: "header"}, 
  		  React.createElement("div", {className: "am-g"}, 
  		    React.createElement("h1", null, "编辑角色")
  		  ), 
  		  React.createElement("hr", null)
  		), 
  		React.createElement("div", {className: "am-g"}, 
  		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
  		  React.createElement("form", {id: "editRoleForm", method: "post", className: "am-form"}, 
  			React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
  		    React.createElement("div", {className: "am-form-group"}, 
  		          React.createElement("select", {id: "type", name: "type", value: o.type, onChange: this.handleChange}, 
  		          React.createElement("option", {value: "0"}, Vo.type(0)), 
  		          React.createElement("option", {value: "1"}, Vo.type(1))
  		          )
  		        ), 
  		      React.createElement("label", {htmlFor: "name"}, "名字:"), 
  		      React.createElement("input", {type: "text", name: "name", id: "name", value: o.name, onChange: this.handleChange, placeholder: "不超过15位"}), 
  		      React.createElement("br", null), 
  		       React.createElement("label", {htmlFor: "description"}, "描述:"), 
  		      React.createElement("input", {type: "text", name: "description", id: "description", value: o.description, onChange: this.handleChange}), 
  		      React.createElement("button", {type: "button", onClick: ajax_role_save, className: "am-btn am-btn-primary"}, "提交")
  		    )

  	     )
  	   )
  	   
  	   )
  );
}
}); 


var Role_bind_right = React.createClass({displayName: "Role_bind_right", 
	
render: function() {
	  var o = this.props.formdata;
  return (
  		React.createElement("div", null, 
	  		React.createElement("div", {className: "header"}, 
		  		  React.createElement("div", {className: "am-g"}, 
		  		    React.createElement("h1", null, "角色绑定权限-【", Vo.type(o.type), "】-【", o.name, "】")
		  		  )
	  		), 
  			React.createElement("button", {type: "button", onClick: btn_ajax_updateRight.bind(this, o.uuid), className: "am-btn am-btn-primary"}, "提交"), 
	  		React.createElement(Right_EventsTable, React.__spread({},  this.props))
	  	   
  	   )
  );
}
}); 
//end role 

//end role bind right




//basedatatype
var Basedatatype_EventRow = React.createClass({displayName: "Basedatatype_EventRow", 
	handleClick: function(m,data) {
		ajax_basedatatype_button_handleClick(m,data);
	  },
render: function() {
var event = this.props.event;
var className = event.highlight ? 'am-active' :
  event.disabled ? 'am-disabled' : '';

return (
  React.createElement("tr", {className: className}, 
  React.createElement("td", null, 
  React.createElement("input", {type: "checkbox", value: event.uuid, name: "table_checkbox"})
  ), 
    React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: this.handleClick.bind(this,"edit", event)}, event.name)), 
    React.createElement("td", null, event.description), 
    React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: this.handleClick.bind(this,"detail", event)}, "详细"))
  ) 
);
}
}); 

var Basedatatype_EventsTable = React.createClass({displayName: "Basedatatype_EventsTable",
	handleClick: function(m) {
		 if(this.props.handleClick){
			 if(m=="add_basedatatype"){
				 ajax_basedatatype_button_handleClick(m,{})
				 return;
			 }
		 }
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all")[0].checked); 
	  },
	  //
	  handleChange_select_basedatatype_type:function(){
		  ajax_basedatatype_list($('#select_basedatatype_type').val());
	  },
render: function() {
return (
React.createElement("div", null, 
React.createElement("div", {className: "header"}, 
React.createElement("div", {className: "am-g"}, 
  React.createElement("h1", null, "编辑基础数据类型")
), 
React.createElement("hr", null)
), 
React.createElement(AMUIReact.ButtonToolbar, null, 
	    React.createElement(AMUIReact.Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "add_basedatatype"), round: true}, "添加")
	 ), 
	  React.createElement("hr", null), 
	  
  React.createElement(AMUIReact.Table, React.__spread({},  this.props), 
    React.createElement("thead", null, 
      React.createElement("tr", null, 
      	React.createElement("th", null, 
        React.createElement("input", {type: "checkbox", id: "id_checkbox_all", onChange: this.handleChange_checkbox_all})
        ), 
        React.createElement("th", null, "名称"), 
        React.createElement("th", null, "描述"), 
        React.createElement("th", null, "操作")
      )
    ), 
    React.createElement("tbody", null, 
      this.props.events.map(function(event) {
        return (React.createElement(Basedatatype_EventRow, {key: event.id, event: event}));
      })
    )
  )
  )
);
}
});

var Basedatatype_edit = React.createClass({displayName: "Basedatatype_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editBasedatatypeForm').serializeJson());
	  },
render: function() {
	  var o = this.state;
return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "编辑基础数据类型")
		  ), 
		  React.createElement("hr", null)
		), 
		React.createElement("div", {className: "am-g"}, 
		  React.createElement("div", {className: "am-u-lg-6 am-u-md-8 am-u-sm-centered"}, 
		  React.createElement("form", {id: "editBasedatatypeForm", method: "post", className: "am-form"}, 
			React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
		      React.createElement("label", {htmlFor: "name"}, "名字:"), 
		      React.createElement("input", {type: "text", name: "name", id: "name", value: o.name, onChange: this.handleChange, placeholder: "不超过15位"}), 
		      React.createElement("br", null), 
		       React.createElement("label", {htmlFor: "description"}, "描述:"), 
		      React.createElement("input", {type: "text", name: "description", id: "description", value: o.description, onChange: this.handleChange}), 
		      React.createElement("button", {type: "button", onClick: ajax_basedatatype_save, className: "am-btn am-btn-primary"}, "提交")
		    )

	     )
	   )
	   
	   )
);
}
}); 
//end basedatatype

// basedatatypelist
var Basedatatype_bind_basedatalist = React.createClass({displayName: "Basedatatype_bind_basedatalist", 
	
render: function() {
	  var o = this.props.formdata;
  return (
  		React.createElement("div", null, 
	  		React.createElement("div", {className: "header"}, 
		  		  React.createElement("div", {className: "am-g"}, 
		  		    React.createElement("h1", null, "基础数据【", o.name, "】")
		  		  )
	  		), 
  			React.createElement("button", {type: "button", onClick: Queue.doBackFN.bind(Queue), className: "am-btn am-btn-primary"}, "返回"), 
	  		React.createElement(Basedatalist_EventsTable, React.__spread({},  this.props))
	  	   
  	   )
  );
}
}); 

var Basedatalist_EventRow = React.createClass({displayName: "Basedatalist_EventRow", 
	
  render: function() {
    var event = this.props.event;

    return (
		 React.createElement("tr", null, 
        React.createElement("td", null, React.createElement("a", {href: "javascript:void(0);", onClick: btn_click_basedatatypelist.bind(this,"edit", JSON.stringify(event))}, event.datakey)), 
        React.createElement("td", null, event.datavalue), 
        React.createElement("td", null, Vo.get("enable_"+event.enable)), 
        React.createElement("td", null, event.description)
      ) 
    );
  }
}); 

var Basedatalist_EventsTable = React.createClass({displayName: "Basedatalist_EventsTable",
  render: function() {
	  var that=this;
    return (
    		React.createElement("div", null, 
      React.createElement(AMUIReact.Table, React.__spread({},  this.props), 
        React.createElement("thead", null, 
          React.createElement("tr", null, 
            React.createElement("th", null, "Key"), 
            React.createElement("th", null, "显示名"), 
            React.createElement("th", null, 
            "状态"
            ), 
            React.createElement("th", null, "描述")
          )
        ), 
        React.createElement("tbody", null, 
          this.props.events.map(function(event) {
            return (React.createElement(Basedatalist_EventRow, {event: event}));
          })
        )
      ), 
      React.createElement("button", {type: "button", onClick: btn_click_basedatatypelist.bind(this, "add",{typeuuid:this.props.formdata.name}), className: "am-btn am-btn-primary"}, "添加")
      )
    );
  }
});
    

var Basedatatypelist_edit = React.createClass({displayName: "Basedatatypelist_edit", 
	 getInitialState: function() {
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editBasedatatypelistForm').serializeJson());
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
    		  React.createElement("form", {id: "editBasedatatypelistForm", method: "post", className: "am-form"}, 
    			React.createElement("input", {type: "hidden", name: "uuid", value: o.uuid}), 
    			React.createElement("input", {type: "hidden", name: "typeuuid", value: o.typeuuid}), 
    		      React.createElement("label", null, "key[数字1-100]:"), 
    		      React.createElement("input", {type: "text", name: "datakey", value: o.datakey, onChange: this.handleChange, placeholder: "不超过15位,一般是数字,[0-100]"}), 
    		      React.createElement("br", null), 
    		       React.createElement("label", null, "显示名:"), 
    		      React.createElement("input", {type: "text", name: "datavalue", 	  value: o.datavalue, onChange: this.handleChange}), 
    		      React.createElement("label", {htmlFor: "description"}, "描述:"), 
    		      React.createElement("input", {type: "text", name: "description", value: o.description, onChange: this.handleChange}), 
    		      React.createElement("label", {htmlFor: "enable"}, "描述:"), 
    		      React.createElement("div", {className: "am-form-group"}, 
    		      
    		      React.createElement("select", {name: "enable", value: this.props.enable, onChange: this.handleChange}, 
    		      React.createElement("option", {value: "1"}, Vo.get("enable_1")), 
    		      React.createElement("option", {value: "0"}, Vo.get("enable_0"))
    		      )
    		    ), 
    			      
    		      
    		      React.createElement("button", {type: "button", onClick: ajax_basedatatypelist_save, className: "am-btn am-btn-primary"}, "提交")
    		    )

    	     )
    	   )
    	   
    	   )
    );
  }
}); 
//end basedatatypelist


//accounts
var Accounts_EventRow = React.createClass({displayName: "Accounts_EventRow", 
render: function() {
var event = this.props.event;
var className = event.highlight ? 'am-active' :
  event.disabled ? 'am-disabled' : '';

return (
  React.createElement("tr", {className: className}, 
  React.createElement("td", null, " ", Vo.get("AD_Accounts_type_"+event.type)), 
  React.createElement("td", null, event.title), 
  React.createElement("td", null, " ", event.num), 
    React.createElement("td", null, G_getDateYMD(event.accounts_time)), 
    React.createElement("td", null, " ", event.description), 
    React.createElement("td", null, Store.getGroupNameByUuid(event.groupuuid)), 
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
        React.createElement("th", null, "备注"), 
        React.createElement("th", null, "学校"), 
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
		    return this.props.formdata;
		  },
	 handleChange: function(event) {
		    this.setState($('#editAccountsForm').serializeJson());
	  },
	 
render: function() {
	  var o = this.state;
return (
		React.createElement("div", null, 
		React.createElement("div", {className: "header"}, 
		  React.createElement("div", {className: "am-g"}, 
		    React.createElement("h1", null, "添加收支记录")
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
		React.createElement("label", {htmlFor: "type"}, "类型:"), 
		 React.createElement("div", {className: "am-form-group"}, 
		React.createElement("select", {id: "type", name: "type", "data-am-selected": "{btnSize: 'lg'}", value: o.type, onChange: this.handleChange}, 
		 this.props.type_list.map(function(event) {
             return (React.createElement("option", {value: event.key}, event.val));
           })
      )
      ), 
	      React.createElement("br", null), 
	    React.createElement("label", {htmlFor: "accounts_timeStr"}, "收支日期:"), 
	    React.createElement(AMUIReact.DateTimeInput, {format: "YYYY-MM-DD", name: "accounts_timeStr", id: "accounts_timeStr", dateTime: o.accounts_time, showTimePicker: false, onChange: this.handleChange}), 
	       React.createElement("label", {htmlFor: "title"}, "内容:"), 
	      React.createElement("input", {type: "text", name: "title", id: "title", value: o.title, onChange: this.handleChange, placeholder: "必填,不超过45位"}), 
	      React.createElement("br", null), 
	
	       React.createElement("label", {htmlFor: "num"}, "金额:"), 
	      React.createElement("input", {type: "number", name: "num", id: "num", value: o.num, onChange: this.handleChange, placeholder: "必填"}), 
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