function ajax_group_myList_button_handleClick(m){
	if(m=="add_group"){
		React.render(React.createElement(Group_edit,{formdata:{"type":1,"brand_name":""}}), document.getElementById('div_body'));
	}
};
//获取我的
function ajax_group_myList() {
	
	$.AMUI.progress.start();

	var url = hostUrl + "rest/group/myList.json";
	$.ajax({
		type : "GET",
		url : url,
		data : "",
		dataType : "json",
		success : function(data) {
			$.AMUI.progress.done();
			if (data.ResMsg.status == "success") {
				React.render(React.createElement(Group_EventsTable, {events: data.list,handleClick:ajax_group_myList_button_handleClick, responsive: true, bordered: true, striped :true,hover:true,striped:true}), document.getElementById('div_body'));
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

function ajax_group_edit(data){
	React.render(React.createElement(Group_edit,{formdata:data}), document.getElementById('div_body'));
};

function ajax_group_save(){
$.AMUI.progress.start();
	
	  var objectForm = $('#editGroupForm').serializeJson();
	  var jsonString=JSON.stringify(objectForm);
      var url = hostUrl + "rest/group/save.json";
	$.ajax({
		type : "POST",
		url : url,
		processData: false, //设置 processData 选项为 false，防止自动转换数据格式。
		data : jsonString,
		dataType : "json",
		contentType : false,  
		success : function(data) {
			$.AMUI.progress.done();
			// 登陆成功直接进入主页
			if (data.ResMsg.status == "success") {
				//alert(data.ResMsg.message);
				Queue.doBackFN();
			} else {
				alert(data.ResMsg.message);
			}
		},
		error : function( obj, textStatus, errorThrown ){
			$.AMUI.progress.done();
			alert(url+",error:"+textStatus);
			 console.log(url+',error：', obj);
			 console.log(url+',error：', textStatus);
			 console.log(url+',error：', errorThrown);
		}
	});
}