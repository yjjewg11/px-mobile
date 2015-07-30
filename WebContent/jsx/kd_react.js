
//login   
var Div_login = React.createClass({ 
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
 		<div>
 		<div className="header">
 		  <div className="am-g">
 		 <h1>问界互动家园-家长登录</h1>
 	    <p>WenJie Interactive Home</p>
 		  </div>
 		  <hr />
 		</div>
 		<div className="am-g">
 		  <div className="am-u-lg-6 am-u-md-8 am-u-sm-centered">
 		 <form id="login_form" method="post" className="am-form">
 	      <label htmlFor="loginname">手机号:</label>
 	      <input type="text" name="loginname" id="loginname" value={o.loginname} onChange={this.handleChange}/>
 	      <br/>
 	      <label htmlFor="password">密码:</label>
 	      <input type="password" name="password" id="password" value={o.password} onChange={this.handleChange}/>
 	      <br/>
 	      <label htmlFor="pw_checked">
 	        <input id="pw_checked" name="pw_checked" type="checkbox"  checked={o.pw_checked=="checked"?"checked":""} onChange={this.handleChange}/>
 	        记住密码
 	      </label>
 	      <br />
 	      <div className="am-cf">
 	        <input id="btn_login" onClick={ajax_userinfo_login} type="button" name="" value="登 录" className="am-btn am-btn-primary am-btn-sm am-fl" />
 	        <input type="button" onClick={menu_userinfo_updatePasswordBySms_fn} value="忘记密码 ^_^? " className="am-btn am-btn-default am-btn-sm am-fr" />
 	      </div>
 	      <br/>
 	     <a  href="http://120.25.248.31/px-moblie/kd/" > <img src="ewm_kd.png" /></a>
 	    </form>
 	    <hr/>
 	   <p>© 2015 成都问界科技有限公司  | 蜀ICP备15021053号-1</p>
 	     </div> 
 	   </div>
 	   
 	   </div>
 );
}
}); 

//end login



//1.1.班级互动分页列表的每一页
var ClassnewsList_Page_show = React.createClass({ 
render: function() {
	  var o = this.props.formdata;
  return (
		  <div>
		  {this.props.events.data.map(function(o) {
		      return (
		    		  <div>
		    		  <AMUIReact.Article
		  		    title={o.title}
		  		    meta={o.create_user+" | "+o.update_time+" | 阅读"+o.count+"次"}>
		  			<div dangerouslySetInnerHTML={{__html: o.content}}></div>
		  		   </AMUIReact.Article>	
		  		  <Common_Dianzan_show uuid={o.uuid} type={0} />
		  		  <Common_reply_list uuid={o.uuid}  type={0}/>
		  		   </div>	
		      )
		  })}
		    </div>
		   
  );
}
}); 

//1.班级互动分页列表
var Classnews_getClassNewsByMy_fenye_show = React.createClass({ 
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
		  <div className="G_reply">
		   <div id={this.list_div}>
		   </div>
			<button id={this.load_more_btn_id}  type="button"  onClick={this.load_more_data.bind(this)}  className="am-btn am-btn-primary">加载更多</button>
		</div>
		   
  );
}
}); 
