//
var AMUIReact_Table=AMUIReact.Table;
var AMUIReact_ButtonToolbar=AMUIReact.ButtonToolbar;
var AMUIReact_Button=AMUIReact.Button;
var AMUIReact_Sticky=AMUIReact.Sticky;

/**
 * ajax_chooseUser_edit
 */

var ChooseUser_EventRow = React.createClass({displayName: "ChooseUser_EventRow", 
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
    var is_Checked=this.props.checkedUseruuid.indexOf(event.uuid)>-1;
    var className = is_Checked ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      React.createElement("tr", {id: "tr_chuser_"+event.uuid, className: className, onClick: this.tr_onClick.bind(this,"tr_chuser_"+event.uuid,"tb_cbox__chuser"+event.uuid)}, 
      React.createElement("td", null, 
      React.createElement("input", {type: "checkbox", alt: event.name, value: event.uuid, id: "tb_cbox__chuser"+event.uuid, name: "table_checkbox", checked: is_Checked?"checked":""})
      ), 
        React.createElement("td", null, event.name), 
        React.createElement("td", null, event.tel), 
        React.createElement("td", null, event.sex=="0"?"男":"女"), 
        React.createElement("td", {className: "px_disable_"+event.disable}, event.disable=="1"?"禁用":"正常")
      ) 
    );
  }
}); 

var ChooseUser_EventsTable = React.createClass({displayName: "ChooseUser_EventsTable",
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
    React.createElement("div", null, 
    React.createElement(AMUIReact_Sticky, null, 
    React.createElement(AMUIReact_ButtonToolbar, null, 
    React.createElement(AMUIReact_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "ok"), round: true}, "确认"), 
    React.createElement(AMUIReact_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "cancel"), round: true}, "取消")
  )
  ), 
  React.createElement("div", {className: "header"}, 
  React.createElement("div", {className: "am-g"}, 
    React.createElement("h1", null, "老师选择")
  ), 
  React.createElement("hr", null)
), 
	  React.createElement("div", {className: "am-form-group"}, 
      React.createElement("select", {id: "selectgroup_uuid_chuser", name: "group_uuid", "data-am-selected": "{btnSize: 'sm'}", value: this.props.group_uuid?this.props.group_uuid:"", onChange: this.handleChange_selectgroup_uuid_chuser}, 
      this.props.group_list.map(function(event) {
          return (React.createElement("option", {value: event.uuid}, event.company_name));
        })
      )
    ), 
	  
      React.createElement(AMUIReact_Table, React.__spread({},  this.props), 
        React.createElement("thead", null, 
          React.createElement("tr", null, 
          	React.createElement("th", null, 
            React.createElement("input", {type: "checkbox", id: "id_checkbox_all_chuser", onChange: this.handleChange_checkbox_all})
            ), 
            React.createElement("th", null, "昵称"), 
            React.createElement("th", null, "电话"), 
            React.createElement("th", null, "性别"), 
            React.createElement("th", null, "状态")
          )
        ), 
        React.createElement("tbody", null, 
          this.props.events.map(function(event) {
            return (React.createElement(ChooseUser_EventRow, {event: event, checkedUseruuid: that.props.checkedUseruuid}));
          })
        )
      )
      )
    );
  }
});
//end chooseUser

    

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
   	    React.createElement("input", {type: "button", id: "btnCrop", value: "剪切", onClick: this.btnCrop_onClick}), 
   	    React.createElement("input", {type: "button", id: "btnZoomIn", value: "放大", onClick: this.btnZoomIn_onClick}), 
   	    React.createElement("input", {type: "button", id: "btnZoomOut", value: "缩小", onClick: this.btnZoomOut_onClick})
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


//chooseClass 
/**
 * ajax_chooseClass_edit
 */

var ChooseClass_EventRow = React.createClass({displayName: "ChooseClass_EventRow", 
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
    var className = event.highlight ? 'am-active' :
      event.disabled ? 'am-disabled' : '';

    return (
      React.createElement("tr", {className: className, onClick: w_ch_class.handleClick.bind(this,"choose",event.uuid,event.name)}, 
        React.createElement("td", null, event.name), 
        React.createElement("td", null, Store.getGroupNameByUuid(event.groupuuid))
      ) 
    );
  }
}); 

var ChooseClass_EventsTable = React.createClass({displayName: "ChooseClass_EventsTable",
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
		  w_ch_user.reshowBygroup(v);
	  },
  render: function() {
	  var that=this;
    return (
    React.createElement("div", null, 
    React.createElement(AMUIReact_Sticky, null, 
    React.createElement(AMUIReact_ButtonToolbar, null, 
    React.createElement(AMUIReact_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "cancel"), round: true}, "取消")
  )
  ), 
  React.createElement("div", {className: "header"}, 
  React.createElement("div", {className: "am-g"}, 
    React.createElement("h1", null, "班级选择")
  ), 
  React.createElement("hr", null)
), 
	  React.createElement("div", {className: "am-form-group"}, 
      React.createElement("select", {id: "selectgroup_uuid_chuser", name: "group_uuid", "data-am-selected": "{btnSize: 'sm'}", value: this.props.group_uuid?this.props.group_uuid:"", onChange: this.handleChange_selectgroup_uuid_chuser}, 
      this.props.group_list.map(function(event) {
          return (React.createElement("option", {value: event.uuid}, event.company_name));
        })
      )
    ), 
	  
      React.createElement(AMUIReact_Table, React.__spread({},  this.props), 
        React.createElement("thead", null, 
          React.createElement("tr", null, 
            React.createElement("th", null, "班级"), 
            React.createElement("th", null, "学校")
          )
        ), 
        React.createElement("tbody", null, 
          this.props.events.map(function(event) {
            return (React.createElement(ChooseClass_EventRow, {event: event}));
          })
        )
      )
      )
    );
  }
});
//end chooseClass

    
    
//   chooseCook    
/**
 * ajax_chooseCook_edit
 */

var ChooseCook_Widget = React.createClass({displayName: "ChooseCook_Widget",
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
    		
	 React.createElement("div", null, 
	    React.createElement(AMUIReact_ButtonToolbar, null, 
	    React.createElement(AMUIReact_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "ok"), round: true}, "确认"), 
	    React.createElement(AMUIReact_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "cancel"), round: true}, "取消")
	  ), 
    		
	 React.createElement(AMUIReact.Tabs, {defaultActiveKey: "1", justify: true}, 
	    React.createElement(AMUIReact.Tabs.Item, {eventKey: "1", title: "主食"}, 
	    React.createElement(ChooseCook_EventRow, {type: 1, checkeduuids: that.props.checkeduuids})
	    ), 
	    React.createElement(AMUIReact.Tabs.Item, {eventKey: "2", title: "汤粥"}, 
	    React.createElement(ChooseCook_EventRow, {type: 2, checkeduuids: that.props.checkeduuids})
	    ), 
	    React.createElement(AMUIReact.Tabs.Item, {eventKey: "3", title: "炒菜"}, 
	    React.createElement(ChooseCook_EventRow, {type: 3, checkeduuids: that.props.checkeduuids})
	    ), 
	    React.createElement(AMUIReact.Tabs.Item, {eventKey: "4", title: "水果"}, 
	    React.createElement(ChooseCook_EventRow, {type: 4, checkeduuids: that.props.checkeduuids})
	    ), 
	      React.createElement(AMUIReact.Tabs.Item, {eventKey: "5", title: "其他"}, 
	      React.createElement(ChooseCook_EventRow, {type: 5, checkeduuids: that.props.checkeduuids})
	    )
	  )
	)
  )}		  
});
    var ChooseCook_EventRow = React.createClass({displayName: "ChooseCook_EventRow",
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
    	    		  React.createElement("div", {id: "div_cook_"+this.props.type}, 
    	    	
    	    		  
     	    			 this.state.items.map(function(event) {
     	    				  var is_Checked=false;
     	    		    		if(that.props.checkeduuids)is_Checked=that.props.checkeduuids.indexOf(event.uuid)>-1;
     	    		      	    var className = is_Checked ? 'G_ch_cook_item_checked' :'G_ch_cook_item';
     	 	            return (
     	 	            		React.createElement("div", {id: "divCookItem_"+event.uuid, title: event.uuid, className: className, onClick: that.div_onClick.bind(this,"divCookItem_"+event.uuid)}, 
 		    	 	       			React.createElement("img", {src: G_imgPath+event.img, name: event.img, alt: "图片不存在", title: event.name}), 
 		    	 	       			React.createElement("span", {title: event.name}, event.name)
 		    	 	       		)		
     	 	            	);
     	 	          }), 
     	 	          

    	    		
    			      React.createElement("div", {id: "divCookItem_add", className: "G_ch_cook_item", onClick: w_ch_cook.add_img.bind(this,that.props.type,that.callbackFN)}, 
    					React.createElement("img", {src: hostUrl+"i/addCook.gif", alt: "本地上传"})
    				)
    	      )
    	    		
    		
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
var Upload_cookImg = React.createClass({displayName: "Upload_cookImg",
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
   	    React.createElement("input", {type: "button", id: "btnCrop", value: "剪切", onClick: this.btnCrop_onClick}), 
   	    React.createElement("input", {type: "button", id: "btnZoomIn", value: "放大", onClick: this.btnZoomIn_onClick}), 
   	    React.createElement("input", {type: "button", id: "btnZoomOut", value: "缩小", onClick: this.btnZoomOut_onClick})
   	), 
   		React.createElement("div", {className: "cropped", id: "upload_file_imageBox_cropped"}
   	   	)
   	), 
   	
  	
    React.createElement("label", {htmlFor: "cook_name"}, "名称:"), 
     React.createElement("input", {type: "text", name: "cook_name", id: "cook_name", placeholder: ""}), 
     React.createElement("br", null), 
    React.createElement(AMUIReact_ButtonToolbar, null, 
    React.createElement(AMUIReact_Button, {amStyle: "primary", onClick: this.handleClick.bind(this, "ok"), round: true}, "确认"), 
    React.createElement(AMUIReact_Button, {amStyle: "danger", onClick: this.handleClick.bind(this, "cancel"), round: true}, "取消")
  )
             )
           );
         }
});
           
           
    //end add cook img