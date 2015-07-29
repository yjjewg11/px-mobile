//chooseRight
var w_ch_right={
	div_id:"div_widget_chooseRight",
	div_body:"div_body",
	bind_inputid_uuid:null,
	bind_inputid_name:null,
	checkedRightuuid:null,
	handleClick:function(m,groupuuid,rightuuids,rightnames){
		
		w_ch_right.hide();
		if(m=="cancel")return;
		if(w_ch_right.bind_inputid_uuid){
			$("#"+w_ch_right.bind_inputid_uuid).val(rightuuids);
		}
		if(w_ch_right.bind_inputid_name){
			$("#"+w_ch_right.bind_inputid_name).val(rightnames);
		}
	},
	groupuuid:null,
	callbackFN:null,
	open:function(bind_inputid_uuid,bind_inputid_name,groupuuid){
		w_ch_right.bind_inputid_uuid=bind_inputid_uuid;
		w_ch_right.bind_inputid_name=bind_inputid_name;
		if(w_ch_right.bind_inputid_uuid)
			w_ch_right.checkedRightuuid=$("#"+w_ch_right.bind_inputid_uuid).val();
		if(!w_ch_right.checkedRightuuid)w_ch_right.checkedRightuuid="";
		
		if(!groupuuid)w_ch_right.groupuuid=Store.getCurGroup().uuid;
		else w_ch_right.groupuuid=groupuuid;
		if(!w_ch_right.groupuuid){
			alert("w_ch_right.open groupuuid is null!");
		}
		w_ch_right.show();
		
	},	
	ajax_chooseRight_listByGroup:function(groupuuid){
		$.AMUI.progress.start();
		var url = hostUrl + "rest/rightinfo/list.json?groupuuid="+groupuuid;
		$.ajax({
			type : "GET",
			url : url,
			async: false,
			data : "",
			dataType : "json",
			success : function(data) {
				$.AMUI.progress.done();
				if (data.ResMsg.status == "success") {
					Store.setChooseUer(groupuuid,data.list)
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
	reshowBygroup:function(groupuuid){
		w_ch_right.groupuuid=groupuuid;
		if(!w_ch_right.groupuuid){
			alert("w_ch_right.reshowBygroup groupuuid is null!");
		}
		w_ch_right.show();
	},
	show:function(){
		var rights=Store.getChooseUer(w_ch_right.groupuuid);
		if(rights==null){
			w_ch_right.ajax_chooseRight_listByGroup(w_ch_right.groupuuid);
			rights=Store.getChooseUer(w_ch_right.groupuuid);
		}
//		for(var i=0;i<100;i++)rights.push(Store.getRightinfo());
		React.render(React.createElement(ChooseRight_EventsTable, {
			group_uuid:w_ch_right.groupuuid,
			group_list:Store.getGroup(),
			events: rights,
			checkedRightuuid:w_ch_right.checkedRightuuid,
			handleClick:w_ch_right.handleClick,
			responsive: true, bordered: true, striped :true,hover:true,striped:true
			}), document.getElementById(w_ch_right.div_id));
		
		
		$("#"+this.div_body).hide();
		$("#"+this.div_id).show();
		
	},
	
	hide:function(callbackFN){
		$("#"+this.div_body).show();
		$("#"+this.div_id).html("");
		$("#"+this.div_id).hide();
	}
}

//chooseRight end
