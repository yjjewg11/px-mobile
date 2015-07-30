
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
 		 React.createElement("h1", null, "问界互动家园-家长登录"), 
 	    React.createElement("p", null, "WenJie Interactive Home")
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
 	     React.createElement("a", {href: "http://120.25.248.31/px-moblie/kd/"}, " ", React.createElement("img", {src: "ewm_kd.png"}))
 	    ), 
 	    React.createElement("hr", null), 
 	   React.createElement("p", null, "© 2015 成都问界科技有限公司  | 蜀ICP备15021053号-1")
 	     )
 	   )
 	   
 	   )
 );
}
}); 

//end login



//1.1.班级互动分页列表的每一页
var ClassnewsList_Page_show = React.createClass({displayName: "ClassnewsList_Page_show", 
render: function() {
	  var o = this.props.formdata;
  return (
		  React.createElement("div", null, 
		  this.props.events.data.map(function(o) {
		      return (
		    		  React.createElement("div", null, 
		    		  React.createElement(AMUIReact.Article, {
		  		    title: o.title, 
		  		    meta: o.create_user+" | "+o.update_time+" | 阅读"+o.count+"次"}, 
		  			React.createElement("div", {dangerouslySetInnerHTML: {__html: o.content}})
		  		   ), 	
		  		  React.createElement(Common_Dianzan_show, {uuid: o.uuid, type: 0}), 
		  		  React.createElement(Common_reply_list, {uuid: o.uuid, type: 0})
		  		   )	
		      )
		  })
		    )
		   
  );
}
}); 

//1.班级互动分页列表
var Classnews_getClassNewsByMy_fenye_show = React.createClass({displayName: "Classnews_getClassNewsByMy_fenye_show", 
	load_more_btn_id:"load_more_",
	pageNo:1,
	list_div:"classnews_list_div",
	componentWillReceiveProps:function(){
		this.load_more_data();
	},
	componentDidMount:function(){
		this.load_more_data();
	},
	load_more_data:function(){
		$("#"+this.list_div).append("<div id="+this.list_div+this.pageNo+">加载中...</div>");
		var re_data=ajax_classnews_getClassNewsByMy_list(this.props.uuid,this.list_div+this.pageNo,this.pageNo);
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
		   React.createElement("div", {id: this.list_div}
		   ), 
			React.createElement("button", {id: this.load_more_btn_id, type: "button", onClick: this.load_more_data.bind(this), className: "am-btn am-btn-primary"}, "加载更多")
		)
		   
  );
}
}); 
