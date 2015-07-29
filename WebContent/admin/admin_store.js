var ADStore={
	map:{},
	clear:function(){
		if (!$.AMUI.store.enabled) {
			$.AMUI.store.clear();
		}
		Store.map={};
		Store.setCurGroup(null);
	},
	enabled:function(){
		if (!$.AMUI.store.enabled) {
			  alert('你的浏览器禁用 LocalStorage，部分显示有问题，请启用LocalStorage');
			  return false;
			}
		return true;
	},
	getAllGroup:function(){
		 if(this.map["AllGroup"])return this.map["AllGroup"];
			 //从后台重新获取
		 ADstore_ajax_group_allList_toStroe();
			 if(this.map["AllGroup"])return this.map["AllGroup"];
		 return [];
	},
	setAllGroup:function(v){
		this.map["AllGroup"]=v;
	},
	
	/**
	 * 设置人员选择控件到内存缓存。
	 * @param v
	 */
	setChooseUer:function(groupuuid,v){
		this.map["ChooseUer"+groupuuid]=v;
	},
	getChooseUer:function(groupuuid){
		 return this.map["ChooseUer"+groupuuid];
	},
//	/store : $.AMUI.store,
	/**
	 * 根据uuid获取机构名称
	 */
	getGroupNameByUuid:function(uuid){
		var arr=this.getGroup();
		for(var i=0;i<arr.length;i++){
			if(uuid==arr[i].uuid)return arr[i].company_name;
		}
		return "";
	},
	getCurGroup:function(){
		 if(this.map["CurGroup"])return this.map["CurGroup"];
		 var o=$.AMUI.store.get("CurGroup");
		 if(o==null){
			 var group=Store.getGroup();
			 if(group.length>0){
				 o=group[0];
			 	Store.setCurGroup(o);
			 }else{
			 	cur_group={};
			 }
		 }
		 return o;
	},
	setCurGroup:function(v){
		this.map["CurGroup"]=v;
		if(!Store.enabled())return;
		$.AMUI.store.set("CurGroup", v);
	},
	getRightList:function(v){
		var key="RightList"+v;
		 if(this.map[key])return this.map[key];
		 
		 ADstore_ajax_right_list(v);
		 if(this.map[key])return this.map[key];
		 
		 return [];
	},
	setRightList:function(v,val){
		var key="RightList"+v;
		this.map[key]=val;
	},
	getRoleList:function(){
		 if(this.map["RoleList"])return this.map["RoleList"];
			 //从后台重新获取
			 ADstore_ajax_RoleList_toStroe();
			 if(this.map["RoleList"])return this.map["RoleList"];
		 return [];
	},
	setRoleList:function(v){
		this.map["RoleList"]=v;
	},
	getUserinfo:function(){
		 if(this.map["userinfo"])return this.map["userinfo"];
		 var o=$.AMUI.store.get("userinfo");
		 if(o==null){
			 //从后台重新获取
			 o={};
		 }
		 return o;
	},
	setUserinfo:function(v){
		this.map["userinfo"]=v;
		if(!Store.enabled())return;
		$.AMUI.store.set("userinfo", v);
	}
};
function ADstore_ajax_right_list(type) {
	if(!type)type="0";
	$.AMUI.progress.start();
	
	var url = hostUrl + "rest/right/list.json?type="+type;
	$.ajax({
		type : "GET",
		url : url,
		async: false,
		dataType : "json",
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				ADStore.setRightList(type,data.list);
				
			} else {
				alert(data.ResMsg.message);
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

//获取我的
function ajax_group_myList_toStroe() {
	$.AMUI.progress.start();
	var url = hostUrl + "rest/group/myList.json";
	$.ajax({
		type : "GET",
		url : url,
		data : "",
		dataType : "json",
		async: false,
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				Store.setGroup(data.list);
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



function ADstore_ajax_group_allList_toStroe() {
	$.AMUI.progress.start();
	var url = hostUrl + "rest/group/list.json";
	$.ajax({
		type : "GET",
		url : url,
		data : "",
		dataType : "json",
		async: false,
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				ADStore.setAllGroup(data.list);
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


function ADstore_ajax_RoleList_toStroe() {
	$.AMUI.progress.start();
	var url = hostUrl + "rest/role/list.json";
	$.ajax({
		type : "GET",
		url : url,
		async: false,
		dataType : "json",
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				ADStore.setRoleList(data.list)
			} else {
				alert(data.ResMsg.message);
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