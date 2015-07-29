var AMR_Table=AMUIReact.Table;
var AMR_ButtonToolbar=AMUIReact.ButtonToolbar;
var AMR_Button=AMUIReact.Button;
var AMR_Sticky=AMUIReact.Sticky;
var AMR_Panel=AMUIReact.Panel;
var AMR_Gallery=AMUIReact.Gallery;
var AMR_Input=AMUIReact.Input;

var G_upload_img_Div=<AMR_Input type= "file" label="上传图片" id="file_img_upload" help= "选择图片" accept="image/*" capture= "camera" />
if(window.JavaScriptCall){
	G_upload_img_Div=<AMR_Button amStyle="primary"  id="file_img_upload" round>上传图片</AMR_Button>
}
//userinfo
var Userinfo_EventRow = React.createClass({ 
  render: function() {
    var event = this.props.event;
    var className = event.highlight ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      <tr className={className} >
      <td> 
      <input type="checkbox" value={event.uuid} alt={event.name} name="table_checkbox" />
      </td>
        <td>{event.loginname}</td>
        <td>{event.name}</td>
        <td>{event.tel}</td>
        <td>{event.email}</td>
        <td>{event.sex=="0"?"男":"女"}</td>
        <td  className={"px_disable_"+event.disable}>{Vo.get("disable_"+event.disable)}</td>
        <td>{event.login_time}</td>
        <td>{event.create_time}</td>
      </tr> 
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
var Userinfo_EventsTable = React.createClass({
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
    <div>
    <div className="header">
    <div className="am-g">
      <h1>用户管理</h1>
    </div>
    <hr />
    </div>
    <AMR_ButtonToolbar>
	    <AMR_Button amStyle="primary" onClick={this.handleClick.bind(this, "add")} round>添加</AMR_Button>
	    <AMR_Button amStyle="success" onClick={this.handleClick.bind(this, "enable")} round>启用</AMR_Button>
	    <AMR_Button amStyle="danger" onClick={this.handleClick.bind(this, "disable")} round>禁用</AMR_Button>
	    <AMR_Button amStyle="success" onClick={this.handleClick.bind(this, "getRole")} round>分配权限</AMR_Button>
	    <AMR_Button amStyle="revise" onClick={this.handleClick.bind(this, "edit")} round>修改</AMR_Button>
	    </AMR_ButtonToolbar>
	  <hr/>
	  <div className="am-form-group">
      <select id="selectgroup_uuid" name="group_uuid" data-am-selected="{btnSize: 'lg'}" value={this.props.group_uuid} onChange={this.handleChange_selectgroup_uuid}>
      {this.props.group_list.map(function(event) {
          return (<option value={event.uuid} >{event.company_name}</option>);
        })}
      </select>
    </div>
	  
      <AMR_Table {...this.props}>  
        <thead> 
          <tr>
          	<th>  
            <input type="checkbox" id="id_checkbox_all" onChange={this.handleChange_checkbox_all} />
            </th>
            <th>帐号</th>
            <th>姓名</th>
            <th>电话</th>
            <th>邮箱</th>
            <th>性别</th>
            <th>状态</th>
            <th>登录时间</th>
            <th>创建时间</th>
          </tr> 
        </thead>
        <tbody>
          {this.props.events.map(function(event) {
            return (<Userinfo_EventRow key={event.id} event={event} />);
          })}
        </tbody>
      </AMR_Table>
      </div>
    );
  }
});
/*
* 老师管理Button事件(添加和修改按钮绘制与标签事件处理)；
* @formdata:选中的老师对象；
* @m：是启用还是禁用功能；"add"-添加  "edit"-修改；
* */    
var Userinfo_edit = React.createClass({ 
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
				  <div>
				  <label htmlFor="password">密码:</label>
    		      <input type="password" name="password" id="password" value={o.password} onChange={this.handleChange} />
    		      <br/>
    		      
    		      <label htmlFor="password1">重复密码:</label>
    		      <input type="password" name="password1" id="password1" value={o.password1} onChange={this.handleChange}/>
    		      <br/>
				  </div>
				  );
	  }
	  
    return (
    		<div>
    		<div className="header">
    		  <div className="am-g">
    		    <h1>编辑</h1>
    		  </div>
    		  <hr />
    		</div>
    		<div className="am-g">
    		  <div className="am-u-lg-6 am-u-md-8 am-u-sm-centered">
    		  <form id="editUserinfoForm" method="post" className="am-form">
    			<input type="hidden" name="uuid"  value={o.uuid}/>
    		     <input type="hidden" name="type"  value="1"/>
    			 <input type="hidden" id="group_uuid" name="group_uuid"  value=""/>
    		    <div className="am-form-group">
    		    <AMUIReact.Selected name="group_uuid" onChange={this.handleChange_Selected} btnWidth="300"  multiple= {true} data={this.props.select_group_list} btnStyle="primary" value={o.group_uuid} />
    		        </div>
    		      <label htmlFor="tel">手机号码:</label>
    		      <input type="text" name="tel" id="tel" value={o.tel} onChange={this.handleChange} placeholder=""/>
    		      <br/>
    		      <label htmlFor="name">姓名:</label>
    		      <input type="text" name="name" id="name" value={o.name} onChange={this.handleChange} placeholder="不超过15位"/>
    		      <br/>
    		       <label htmlFor="">Email:</label>
    		      <input type="email" name="email" id="email" value={o.email} onChange={this.handleChange} placeholder="输入邮箱" placeholder=""/>
    		      <br/>
    		      {passwordDiv}
    		      <label htmlFor="office">职位:</label>
    		      <input type="text" name="office" id="office" value={o.office} onChange={this.handleChange}/>
    		      <br/>
    		      <button type="button"  onClick={ajax_userinfo_saveByAdmin}  className="am-btn am-btn-primary">提交</button>
    		    </form>

    	     </div>
    	   </div>
    	   
    	   </div>
    );
  }
}); 

//Userinfo_getRole


var Userinfo_getRole = React.createClass({ 
	
	render: function() {
		  var o = this.props.formdata;
	  return (
	  		<div>
		  		<div className="header">
			  		  <div className="am-g">
			  		    <h1>用户绑定角色-【{o.username}】</h1>
			  		  </div>
		  		</div>
	  			<button type="button"  onClick={btn_ajax_updateRole.bind(this, o.useruuid)}  className="am-btn am-btn-primary">提交</button>
		  		<UserChooseRole_EventsTable {...this.props}/>
		  	   
	  	   </div>
	  );
	}
	}); 




//Div_userinfo_updatepassword
var Div_userinfo_updatepassword = React.createClass({ 
	
	render: function() {
	return (
		<div>
		<div className="header">
		  <div className="am-g">
		    <h1>修改密码</h1>
		  </div>
		  <hr />
		</div>
		<div className="am-g">
		  <div className="am-u-lg-6 am-u-md-8 am-u-sm-centered">
		    <form id="commonform" method="post" className="am-form">

		      <label htmlFor="oldpassword">当前密码:</label>
		      <input type="password" name="oldpassword"  />
		      <br/>
		      <label htmlFor="password">密码:</label>
		      <input type="password" name="password"   />
		      <br/>
		      
		      <label htmlFor="password1">重复密码:</label>
		      <input type="password" name="password1"  />
		      <br/>
		      <button type="button" onClick={ajax_userinfo_updatepassword} className="am-btn am-btn-primary">提交</button>
		    </form>
		    <hr/>
		  
		  </div>
		</div>
		</div>
	);
	}
}); 


//userinfo update
var Div_userinfo_update = React.createClass({ 
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
		<div>
		<div className="header">
		  <div className="am-g">
		    <h1>修改资料</h1>
		  </div>
		  <hr />
		</div>
		<div className="am-g">
		  <div className="am-u-lg-6 am-u-md-8 am-u-sm-centered">
		  
		    <form id="commonform" method="post" className="am-form">
			<input type="hidden" name="img" id="img" value={o.img} onChange={this.handleChange}/>
		    <label htmlFor="nickname">头像:</label>
 		    <AMUIReact.Image  id="img_head_image"  src={G_def_headImgPath} className={"G_img_header"}/>
 		
 		   <button type="button"  onClick={this.handle_uploadHeader}  className="am-btn am-btn-primary">上传头像</button>
 		   <br/>
		      <label htmlFor="name">姓名:</label>
		      <input type="text" name="name" id="name"  value={o.name} onChange={this.handleChange}  placeholder="必填，不超过15位"/>
		      <br/>
		       <label htmlFor="">Email:</label>
		      <input type="email" name="email" id="email"  value={o.email} onChange={this.handleChange}  placeholder="输入邮箱" />
		      <br/>
		      <label >性别:</label>
		      <div className="am-form-group">
		      <AMUIReact.UCheck type="radio" name="sex" label="男" value="0" inline defaultChecked checked={o.sex==0}   onChange={this.handleChange} />
		      <AMUIReact.UCheck type="radio" name="sex" label="女" value="1" inline checked={o.sex==1} onChange={this.handleChange}/>
		      </div>
		      <br/>
		      <label htmlFor="office">职位:</label>
		      <input type="text" name="office" id="office" value={o.office} onChange={this.handleChange}  placeholder="必填，不超过15位"/>
		      <br/>
		      <button type="button" onClick={ajax_userinfo_update} className="am-btn am-btn-primary">提交</button>
		    </form>
		    <hr/>
		  
		  </div>
		</div>
		</div>
	);
	}
}); 

//userinfo update end

//role
var UserChooseRole_EventRow = React.createClass({ 
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
		 <tr name="table_tr_checkbox" name="table_tr_checkbox"  id={"tr_chright_"+event.uuid} className={className} onClick={this.tr_onClick.bind(this,"tr_chright_"+event.uuid,"tb_cbox__chright"+event.uuid)}>
	      <td> 
	      <input type="checkbox" alt={event.name} value={event.uuid} id={"tb_cbox__chright"+event.uuid} name="table_checkbox" checked={is_Checked?"checked":""} />
	      </td>
    <td>{event.name}</td>
    <td>{event.description}</td>
    <td>{Vo.type(event.type)}</td>
  </tr> 
);
}
}); 

var UserChooseRole_EventsTable = React.createClass({
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
<div>

  <AMUIReact.Table {...this.props}>  
    <thead> 
      <tr>
      	<th>  
        <input type="checkbox" id="id_checkbox_all" onChange={this.handleChange_checkbox_all} />
        </th>
        <th>名称</th>
        <th>描述</th>
        <th>类型</th>
      </tr> 
    </thead>
    <tbody>
      {this.props.events.map(function(event) {
        return (<UserChooseRole_EventRow  chooselist={that.props.chooselist} event={event} />);
      })}
    </tbody>
  </AMUIReact.Table>
  </div>
);
}
});
//end Userinfo_getRole



//Div_userinfo_updatePasswordBySms
var Div_userinfo_updatePasswordBySms = React.createClass({ 
	
	render: function() {
	return (
		<div>
		<div className="header">
		  <div className="am-g">
		    <h1>重置密码</h1>
		  </div>
		  <hr />
		</div>
		<div className="am-g">
		  <div className="am-u-lg-6 am-u-md-8 am-u-sm-centered">
		    <form id="commonform" method="post" className="am-form">

		    <label htmlFor="tel">手机号码:</label>
		      <input type="text" name="tel" id="tel"  placeholder=""/>
		      <button type="button" onClick={ajax_sms_sendCode_byReset} className="am-btn am-btn-primary">发送验证码</button>
		      <br/>
		      <label htmlFor="smscode">验证码:</label>
		      <input type="text" name="smscode" id="smscode"  placeholder=""/>
		    
		      <br/>
		      <label htmlFor="password">密码:</label>
		      <input type="password" name="password"   />
		      <br/>
		      
		      <label htmlFor="password1">重复密码:</label>
		      <input type="password" name="password1"  />
		      <br/>
		      <button type="button" onClick={ajax_userinfo_updatePasswordBySms} className="am-btn am-btn-primary">提交</button>
		    </form>
		    <hr/>
		  
		  </div>
		</div>
		</div>
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
var Upload_headImg = React.createClass({
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
       <div>
  	
     <div className="header">
     <div className="am-g">
       <h1>上传图片</h1>
     </div>
     <hr />
   </div>
   <div className="container">

   	<div className="imageBox" id="upload_file_imageBox">
   	    <div className="thumbBox"></div>
   	    <div className="spinner"  style={spinner_divStyle}>加载中...</div>
   	</div>
	<div className="action">
	    <input type="file" id="upload_imgfile" accept="image/*" />
	 <AMUIReact_Button amStyle="warning"onClick={this.btnCrop_onClick} round>剪切</AMUIReact_Button>
	 <AMUIReact_Button amStyle="warning"onClick={this.btnZoomIn_onClick} round>放大</AMUIReact_Button>
	 <AMUIReact_Button amStyle="warning"onClick={this.btnZoomOut_onClick} round>缩小</AMUIReact_Button>

	</div>
		<div className="cropped" id="upload_file_imageBox_cropped">
	   	</div>
	</div>

<AMUIReact_ButtonToolbar>
<AMUIReact_Button amStyle="primary" onClick={this.handleClick.bind(this, "ok")} round>确认</AMUIReact_Button>
<AMUIReact_Button amStyle="danger" onClick={this.handleClick.bind(this, "cancel")} round>取消</AMUIReact_Button>
</AMUIReact_ButtonToolbar>
         </div>
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
var Common_Dianzan_show = React.createClass({ 

render: function() {	
	 var dianzanObject=commons_ajax_dianzan_getByNewsuuid(this.props.uuid);
  return (
		   <div id="dianzan" class="dianzan">♡
		   {dianzanObject.names},等一共{dianzanObject.count}人点赞
		   <AMUIReact.Button   onClick={common_ajax_dianzan_save.bind(this,this.props.uuid,this.props.type,dianzanObject.canDianzan)}  amStyle={dianzanObject.canDianzan?"primary":"warning"}>{dianzanObject.canDianzan?"点赞":"取消点赞"}</AMUIReact.Button>		   
		   </div>
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


var Common_Classnewsreply_listshow = React.createClass({ 
	
render: function() {
  return (
		  <div>
		  {this.props.events.data.map(function(event) {
		      return (
		    		  <div className="event">
		  		 <div  dangerouslySetInnerHTML={{__html: event.content}}></div>
		  		 <strong>{event.create_user+" | "+event.update_time}</strong>
		  		 </div>
		    		  )
		  })}
		
		    </div>
		   
  );
}
}); 
//评论模板
var Common_reply_list = React.createClass({ 
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
		   <h4>评论</h4>
		   <div id={this.classnewsreply_list_div}>
		   </div>
			<button id={this.load_more_btn_id}  type="button"  onClick={this.load_more_data.bind(this)}  className="am-btn am-btn-primary">加载更多</button>
			</div>
		   
  );
}
}); 

//我要评论模块 
var Common_reply_save = React.createClass({ 
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
		   <form id="editClassnewsreplyForm" method="post" className="am-form">
			<input type="hidden" name="newsuuid"  value={this.props.uuid}/>
			<input type="hidden" name="uuid" />
			<input type="hidden" name="type"  value={this.props.uuid}/>
			
			
			<AMR_Input id="classnews_content_replay" type="textarea" rows="10" label="我要回复" placeholder="填写内容" name="content" />
			{G_upload_img_Div}
			<button type="button"  onClick={this.reply_save_btn_click.bind(this)}  className="am-btn am-btn-primary">提交</button>
		      
		    </form>	   
  );
}
}); 


