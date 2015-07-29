

var w_ch_user={
	div_id:"div_widget_chooseUser",
	div_body:"div_body",
	bind_inputid_uuid:null,
	bind_inputid_name:null,
	checkedUseruuid:null,
	handleClick:function(m,groupuuid,useruuids,usernames){
		
		w_ch_user.hide();
		if(m=="cancel")return;
		if(w_ch_user.bind_inputid_uuid){
			$("#"+w_ch_user.bind_inputid_uuid).val(useruuids);
		}
		if(w_ch_user.bind_inputid_name){
			$("#"+w_ch_user.bind_inputid_name).val(usernames);
		}
	},
	groupuuid:null,
	callbackFN:null,
	open:function(bind_inputid_uuid,bind_inputid_name,groupuuid){
		w_ch_user.bind_inputid_uuid=bind_inputid_uuid;
		w_ch_user.bind_inputid_name=bind_inputid_name;
		if(w_ch_user.bind_inputid_uuid)
			w_ch_user.checkedUseruuid=$("#"+w_ch_user.bind_inputid_uuid).val();
		if(!w_ch_user.checkedUseruuid)w_ch_user.checkedUseruuid="";
		
		if(!groupuuid)w_ch_user.groupuuid=Store.getCurGroup().uuid;
		else w_ch_user.groupuuid=groupuuid;
		if(!w_ch_user.groupuuid){
			alert("w_ch_user.open groupuuid is null!");
		}
		w_ch_user.show();
		
	},	
	ajax_chooseUser_listByGroup:function(groupuuid){
		$.AMUI.progress.start();
		var url = hostUrl + "rest/userinfo/list.json?groupuuid="+groupuuid;
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
		w_ch_user.groupuuid=groupuuid;
		if(!w_ch_user.groupuuid){
			alert("w_ch_user.reshowBygroup groupuuid is null!");
		}
		w_ch_user.show();
	},
	show:function(){
		var users=Store.getChooseUer(w_ch_user.groupuuid);
		if(users==null){
			w_ch_user.ajax_chooseUser_listByGroup(w_ch_user.groupuuid);
			users=Store.getChooseUer(w_ch_user.groupuuid);
		}
//		for(var i=0;i<100;i++)users.push(Store.getUserinfo());
		React.render(React.createElement(ChooseUser_EventsTable, {
			group_uuid:w_ch_user.groupuuid,
			group_list:Store.getGroup(),
			events: users,
			checkedUseruuid:w_ch_user.checkedUseruuid,
			handleClick:w_ch_user.handleClick,
			responsive: true, bordered: true, striped :true,hover:true,striped:true
			}), document.getElementById(w_ch_user.div_id));
		
		
		$("#"+this.div_body).hide();
		$("#"+this.div_id).show();
		
	},
	
	hide:function(callbackFN){
		$("#"+this.div_body).show();
		$("#"+this.div_id).html("");
		$("#"+this.div_id).hide();
	}
}

//userinfo
function widget_chooseUser_fn(groupuuid,callbackFN){
	//React.render(ChooseUser_modalInstance, document.getElementById('div_body'));
	return;
	if(!groupuuid)groupuuid=Store.getCurGroup().uuid;
	if(!groupuuid){
		alert("widget_chooseUser_fn groupuuid is null!");
	}
	callbackFN=function(){alert(111);};
	
	var users=Store.getChooseUer(groupuuid);
	
	if(users!=null){
		
		for(var i=0;i<50;i++){
			users.push(Store.getUserinfo());
		}
		React.render(React.createElement(AMUIReact_ModalTrigger, {
			modal:React.createElement(ChooseUser_modal, {groupuuid:groupuuid,
				group_list:Store.getGroup(),
				events: users,
				callbackFN:callbackFN,
				responsive: true, bordered: true, striped :true,hover:true,striped:true}),
			onCancel: onCancel,
			onConfirm: callbackFN
			},
			React.createElement(AMUIReact_Button, {amStyle: "primary"}, "选择")), document.getElementById('div_body'));
		return;
	}
	ajax_chooseUser_listByGroup(groupuuid,callbackFN)
}
//老师查询，条件groupuuid
//
function ajax_chooseUser_listByGroup(groupuuid,callbackFN) {
	$.AMUI.progress.start();
	var url = hostUrl + "rest/userinfo/list.json?groupuuid="+groupuuid;
	$.ajax({
		type : "GET",
		url : url,
		data : "",
		dataType : "json",
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				Store.setChooseUer(groupuuid,data.list)
				widget_chooseUser_fn(groupuuid,callbackFN)
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
};

//chooseUser end