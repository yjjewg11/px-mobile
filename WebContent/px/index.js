//ios 测试代码

 	function call_testFun(){
  		try{
  			window.testFun();
  		}catch(e){
  			
  			alert("window.testFun(); 调用失败");
  		}
  	}
  	function call_testFun(){
  		try{
  			window.testFun1(1);
  		}catch(e){
  			
  			alert("window.testFun1(1); 调用失败");
  		}
  	}
  	
  	
	function call_alert(fx){
  		try{
		alert("call_alert(fx),fx="+fx);
  		}catch(e){
  			alert("call_alert; 调用失败");
  		}
  	}
  	
	
	
	/**
	 * 测试是否登录成功
	 * @param isInit
	 */
	function test_ajax_getUserinfo(isInit) {
		
		React.render(React.createElement(PX_Index, {
			responsive: true, bordered: true, striped :true,hover:true,striped:true
			}), document.getElementById('div_body'));
		
		$.AMUI.progress.start();
		var url = hostUrl + "rest/userinfo/getUserinfo.json";
		$.ajax({
			type : "GET",
			url : url,
			async: false,
			dataType : "json",
			success : function(data) {
				$.AMUI.progress.done();
				if (data.ResMsg.status == "success") {

					alert("自动登录成功");
					
				//	menu_body_fn();
				} else {
					alert("自动登录失败,请设置webview 的cookie:"+data.ResMsg.message);
					//G_resMsg_filter(data.ResMsg);
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
	}
/// ios 测试代码end

function login_affter_init(){
	Vo.init();
}

function menu_userinfo_login_fn(){
	Queue.push(menu_userinfo_login_fn);
	var loginname = getCookie("bs_loginname");
	var password = getCookie("bs_password");
	var pw_checked = getCookie("pw_checked");
	
	React.render(React.createElement(Div_login,{loginname:loginname,password:password,pw_checked:pw_checked})
			, document.getElementById('div_body'));
}



function menu_body_fn (){
	login_affter_init();
	$("#div_body").html("<div>welcome!</div>")
	var fn=$.getUrlParam("fn");
	if(fn){
		try{
			G_jsCallBack[fn]();
		}catch(e){
			alert("调用方法失败,参数fn="+fn);
		    console.log('调用方法失败,参数fn=', e);
		}
		
	}else{
		test_ajax_getUserinfo();
	}
	//menu_classnews_getClassNewsByMy_fn();
}

function index_init(){
	 // ajax_getUserinfo(true);
	menu_body_fn();
}

window.onload=function(){ 
	index_init();
}; 

