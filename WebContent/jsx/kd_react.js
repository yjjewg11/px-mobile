
var ClassnewsList_Page_show = React.createClass({ 
render: function() {
	  var o = this.props.formdata;
  return (
		  <div>
		  
		  
		  
		  <AMUIReact.Article
		    title={o.title}
		    meta={o.create_user+" | "+Store.getClassNameByUuid(o.classuuid)+" | "+o.update_time+" | 阅读"+this.props.count+"次"}>
			<div dangerouslySetInnerHTML={{__html: o.content}}></div>
		   </AMUIReact.Article>	
		  <Common_Dianzan_show uuid={o.uuid} type={0} />
		  <Common_reply_list uuid={o.uuid}  type={0}/>
		<button type="button"  onClick={this.reply_save_btn_click.bind(this)}  className="am-btn am-btn-primary">回复</button>
		    </div>
		   
  );
}
}); 

//评论模板
var ClassnewsList_fenye_show = React.createClass({ 
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
		  <div className="G_reply">
		   <div id={this.classnewsreply_list_div}>
		   </div>
			<button id={this.load_more_btn_id}  type="button"  onClick={this.load_more_data.bind(this)}  className="am-btn am-btn-primary">加载更多</button>
		</div>
		   
  );
}
}); 
