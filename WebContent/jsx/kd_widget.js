//
var AMUIReact_Table=AMUIReact.Table;
var AMUIReact_ButtonToolbar=AMUIReact.ButtonToolbar;
var AMUIReact_Button=AMUIReact.Button;

/**
 * ajax_chooseUser_edit
 */

var ChooseUser_EventRow = React.createClass({ 
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
	componentDidMount:function(){
		$(".am-active input[type='checkbox']").prop("checked",true); 
	},
  render: function() {
    var event = this.props.event;
    var is_Checked=this.props.checkedUseruuid&&this.props.checkedUseruuid.indexOf(event.uuid)>-1;
    var className = is_Checked ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      <tr id={"tr_chuser_"+event.uuid} className={className} onClick={this.tr_onClick.bind(this,"tr_chuser_"+event.uuid,"tb_cbox__chuser"+event.uuid)}>
      <td> 
      <input type="checkbox" alt={event.name} value={event.uuid} id={"tb_cbox__chuser"+event.uuid} name="table_checkbox"  />
      </td>
        <td>{event.name}</td>
        <td>{event.tel}</td>
        <td>{event.sex=="0"?"男":"女"}</td>
        <td  className={"px_disable_"+event.disable}>{event.disable=="1"?"禁用":"正常"}</td>
      </tr> 
    );
  }
}); 

var ChooseUser_EventsTable = React.createClass({
//	 getInitialState: function() {
//		 	alert(this.props.group_uuid);
//		    return this.props.group_uuid;
//		  },
//	
	handleClick: function(m) {
		 if(this.props.handleClick){
			 if(m=="cancel"){
				 this.props.handleClick(m,$('#selectgroup_uuid_chuser').val());
				 return;
			 }
			 var uuids=null;
			 var names=null;
			 $($("input[name='table_checkbox']")).each(function(){
				　if(this.checked){
					 if(uuids==null){
						 uuids=this.value;
						 names=this.alt;
					 }
					 else{
						 uuids+=','+this.value ; 
						 names+=','+this.alt; 
					 }
					　   //遍历被选中CheckBox元素的集合 得到Value值
				　}
				});
			  
			 this.props.handleClick(m,$('#selectgroup_uuid_chuser').val(),uuids,names);
		 }
	  },
	  handleChange_checkbox_all:function(){
		  $('input[name="table_checkbox"]').prop("checked", $("#id_checkbox_all_chuser")[0].checked); 
	  },
	  //
	  handleChange_selectgroup_uuid_chuser:function(){
		  var v=$('#selectgroup_uuid_chuser').val();
		//  this.setState(v);
		  w_ch_user.reshowBygroup(v);
	  },
  render: function() {
	  var that=this;
    return (
    <div>
    <AMUIReact_ButtonToolbar>
    <AMUIReact_Button amStyle="primary" onClick={this.handleClick.bind(this, "ok")} round>确认</AMUIReact_Button>
    <AMUIReact_Button amStyle="danger" onClick={this.handleClick.bind(this, "cancel")} round>取消</AMUIReact_Button>
  </AMUIReact_ButtonToolbar>
  <div className="header">
  <div className="am-g">
    <h1>老师选择</h1>
  </div>
  <hr />
</div>
	  <div className="am-form-group">
      <select id="selectgroup_uuid_chuser" name="group_uuid" data-am-selected="{btnSize: 'lg'}" value={this.props.group_uuid?this.props.group_uuid:""} onChange={this.handleChange_selectgroup_uuid_chuser}>
      {this.props.group_list.map(function(event) {
          return (<option value={event.uuid} >{event.company_name}</option>);
        })}
      </select>
    </div>
	  
      <AMUIReact_Table {...this.props}>  
        <thead> 
          <tr>
          	<th>  
            <input type="checkbox" id="id_checkbox_all_chuser" onChange={this.handleChange_checkbox_all} />
            </th>
            <th>昵称</th>
            <th>电话</th>
            <th>性别</th>
            <th>状态</th>
          </tr> 
        </thead>
        <tbody>
          {this.props.events.map(function(event) {
            return (<ChooseUser_EventRow  event={event} checkedUseruuid={that.props.checkedUseruuid}  />);
          })}
        </tbody>
      </AMUIReact_Table>
      </div>
    );
  }
});
//end chooseUser

    


//chooseClass 
/**
 * ajax_chooseClass_edit
 */

var ChooseClass_EventRow = React.createClass({ 
  render: function() {
    var event = this.props.event;
    var className = event.highlight ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      <tr  className={className} onClick={w_ch_class.handleClick.bind(this,"choose",event.uuid,event.name)}>
        <td>{event.name}</td>
        <td>{Store.getGroupNameByUuid(event.groupuuid)}</td>
      </tr> 
    );
  }
}); 

var ChooseClass_EventsTable = React.createClass({
//	 getInitialState: function() {
//		 	alert(this.props.group_uuid);
//		    return this.props.group_uuid;
//		  },
//	
	handleClick: function(m) {
		 if(this.props.handleClick){
			 if(m=="cancel"){
				 w_ch_class.handleClick.bind(m);
				 return;
			 }
		 }
	  },
	  handleChange_selectgroup_uuid_chuser:function(){
		  var v=$('#selectgroup_uuid_chuser').val();
		  w_ch_class.reshowBygroup(v);
	  },
  render: function() {
	  var that=this;
    return (
    <div>
    <AMUIReact_ButtonToolbar>
    <AMUIReact_Button amStyle="danger" onClick={this.handleClick.bind(this, "cancel")} round>取消</AMUIReact_Button>
  </AMUIReact_ButtonToolbar>
  <div className="header">
  <div className="am-g">
    <h1>班级选择</h1>
  </div>
  <hr />
</div>
	  <div className="am-form-group">
      <select id="selectgroup_uuid_chuser" name="group_uuid" data-am-selected="{btnSize: 'lg'}" value={this.props.group_uuid?this.props.group_uuid:""} onChange={this.handleChange_selectgroup_uuid_chuser}>
      {this.props.group_list.map(function(event) {
          return (<option value={event.uuid} >{event.company_name}</option>);
        })}
      </select>
    </div>
	  
      <AMUIReact_Table {...this.props}>  
        <thead> 
          <tr>
            <th>班级</th>
            <th>学校</th>
          </tr> 
        </thead>
        <tbody>
          {this.props.events.map(function(event) {
            return (<ChooseClass_EventRow  event={event}   />);
          })}
        </tbody>
      </AMUIReact_Table>
      </div>
    );
  }
});
//end chooseClass

    
    
//   chooseCook    
/**
 * ajax_chooseCook_edit
 */

var ChooseCook_Widget = React.createClass({
	handleClick: function(m) {
		 if(this.props.handleClick){
			 if(m=="cancel"){
				 this.props.handleClick(m);
				 return;
			 }
			 var uuids=null;
			 //rs += (cb.getUuid() + "$" + cb.getName() + ",");
			 $($(".G_ch_cook_item_checked")).each(function(){
					 if(uuids==null){
						 uuids=this.title+"$"+this.children[0].name+"$"+this.children[1].title;
					 }
					 else{
						 uuids+=','+this.title+"$"+this.children[0].name+"$"+this.children[1].title;; 
					 }
//					 var tmpO={};
//					 tmpO.uuid=this.title;
//					 tmpO.src=this.children[0].src;
//					 tmpO.name=this.children[1].title;
//					 
//					 imgArr.push(tmpO);
					　   //遍历被选中CheckBox元素的集合 得到Value值
				});
			  
			 this.props.handleClick(m,uuids);
		 }
	  },
	  
	  
  render: function() {
	  var that=this;
	  if(!this.props.events)this.props.events=[];
	  
    return (
    		
	 <div>
	    <AMUIReact_ButtonToolbar>
	    <AMUIReact_Button amStyle="primary" onClick={this.handleClick.bind(this, "ok")} round>确认</AMUIReact_Button>
	    <AMUIReact_Button amStyle="danger" onClick={this.handleClick.bind(this, "cancel")} round>取消</AMUIReact_Button>
	  </AMUIReact_ButtonToolbar>
    		
	 <AMUIReact.Tabs defaultActiveKey="1" justify>
	    <AMUIReact.Tabs.Item eventKey="1" title="主食">
	    <ChooseCook_EventRow type={1} checkeduuids={that.props.checkeduuids}  />
	    </AMUIReact.Tabs.Item>
	    <AMUIReact.Tabs.Item eventKey="2" title="汤粥">
	    <ChooseCook_EventRow type={2} checkeduuids={that.props.checkeduuids}  />
	    </AMUIReact.Tabs.Item>
	    <AMUIReact.Tabs.Item eventKey="3" title="炒菜">
	    <ChooseCook_EventRow type={3} checkeduuids={that.props.checkeduuids}  />
	    </AMUIReact.Tabs.Item>
	    <AMUIReact.Tabs.Item eventKey="4" title="水果">
	    <ChooseCook_EventRow type={4} checkeduuids={that.props.checkeduuids}  />
	    </AMUIReact.Tabs.Item>
	      <AMUIReact.Tabs.Item eventKey="5" title="其他">
	      <ChooseCook_EventRow type={5} checkeduuids={that.props.checkeduuids}  />
	    </AMUIReact.Tabs.Item>
	  </AMUIReact.Tabs>
	</div>
  )}		  
});
    var ChooseCook_EventRow = React.createClass({
    	div_onClick:function(trid){
    		var tr=$("#"+trid);
    		
    		if(tr.attr("class").indexOf("G_ch_cook_item_checked")>=0){ 
    			tr.removeClass("G_ch_cook_item_checked");
    			tr.addClass("G_ch_cook_item");
			}else{ 
				tr.removeClass("G_ch_cook_item");
				tr.addClass("G_ch_cook_item_checked");
			} 
    	},
    	callbackFN:function(){
    		var tmptype=this.props.type;
			this.ajax_chooseCook_list(tmptype);
			var lists=Store.getChooseCook(tmptype);
			this.setState({
	            items: lists,
	        });
    	},
    	 getInitialState: function() {
    		    return {
    	            items: [],
    	        };
    		  },
    	componentDidMount: function() {
    		
    		var tmptype=this.props.type;
    		
    		var lists=Store.getChooseCook(tmptype);
    		if(!lists){
    			this.ajax_chooseCook_list(tmptype);
    			lists=Store.getChooseCook(tmptype);
    		}
    		
    	       this.setState({
    	            items: lists,
    	        });
    	  },
    	  ajax_chooseCook_list:function(type){
    			$.AMUI.progress.start();
    			var url = hostUrl + "rest/cookbook/list.json?type="+type;
    			$.ajax({
    				type : "GET",
    				url : url,
    				async: false,
    				data : "",
    				dataType : "json",
    				success : function(data) {
    					$.AMUI.progress.done();
    					if (data.ResMsg.status == "success") {
    						Store.setChooseCook(type,data.list);
    					} else {
    						alert(data.ResMsg.message);
    						G_resMsg_filter(data.ResMsg);
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
    		},
    	  render: function() {
    		var that=this;
      	 var event = this.props.event;
    	    return (
    	    		  <div id={"div_cook_"+this.props.type}>
    	    	
    	    		  {
     	    			 this.state.items.map(function(event) {
     	    				  var is_Checked=false;
     	    		    		if(that.props.checkeduuids)is_Checked=that.props.checkeduuids.indexOf(event.uuid)>-1;
     	    		      	    var className = is_Checked ? 'G_ch_cook_item_checked' :'G_ch_cook_item';
     	 	            return (
     	 	            		<div id={"divCookItem_"+event.uuid} title={event.uuid} className={className} onClick={that.div_onClick.bind(this,"divCookItem_"+event.uuid)}>
 		    	 	       			<img  src={G_imgPath+event.img} name={event.img} alt="图片不存在" title={event.name} />
 		    	 	       			<span title={event.name}>{event.name}</span>
 		    	 	       		</div>		
     	 	            	);
     	 	          })
     	 	          }

    	    		
    			      <div id={"divCookItem_add"} className="G_ch_cook_item" onClick={w_ch_cook.add_img.bind(this,that.props.type,that.callbackFN)}>
    					<img src={hostUrl+"i/addCook.gif"} alt="本地上传" />
    				</div>
    	      </div>
    	    		
    		
    	  )}
    	});
//end chooseCook    
    

  //add cook img
var Upload_cookImg_options =
    {
        thumbBox: '.thumbBox',
        spinner: '.spinner',
        imgSrc: ''
    };
var Upload_cookImg = React.createClass({
       	handleClick: function(m) {
       		w_ch_cookAddImg.handleClick(m);
       	  },
       	upload_file_onChange:function(){
       	  var reader = new FileReader();
          reader.onload = function(e) {
        	  Upload_cookImg_options.imgSrc = e.target.result;
              w_ch_cookAddImg.cropper = $('#upload_file_imageBox').cropbox(Upload_cookImg_options);
          }
          reader.readAsDataURL(this.files[0]);
          this.files = [];
       	},
       	btnZoomIn_onClick: function(){
       		if(w_ch_cookAddImg.cropper)w_ch_cookAddImg.cropper.zoomIn();
        },
        btnZoomOut_onClick: function(){
        	 if(w_ch_cookAddImg.cropper)w_ch_cookAddImg.cropper.zoomOut();
       },
       btnRotate_onClick: function(){
      	 if(w_ch_cookAddImg.cropper)w_ch_cookAddImg.cropper.chRotate();
     },
       btnCrop_onClick: function(){
    	   var img = w_ch_cookAddImg.cropper.getDataURL();
    	   w_ch_cookAddImg.base64=img;
           $('#upload_file_imageBox_cropped').html('<img src="'+img+'">');
	   },
	   
       	 componentDidMount:function(){
           $('#upload_imgfile').on('change', function(){
               var reader = new FileReader();
               reader.onload = function(e) {
            	   Upload_cookImg_options.imgSrc = e.target.result;
            	   w_ch_cookAddImg.cropper = $('.imageBox').cropbox(Upload_cookImg_options);
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
   	
  	
    <label htmlFor="cook_name">名称:</label>
     <input type="text" name="cook_name" id="cook_name" placeholder=""/>
     <br/>
    <AMUIReact_ButtonToolbar>
    <AMUIReact_Button amStyle="primary" onClick={this.handleClick.bind(this, "ok")} round>确认</AMUIReact_Button>
    <AMUIReact_Button amStyle="danger" onClick={this.handleClick.bind(this, "cancel")} round>取消</AMUIReact_Button>
  </AMUIReact_ButtonToolbar>
             </div>
           );
         }
});
           
           
    //end add cook img