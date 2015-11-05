requirejs.config({
  baseUrl: '.',
  paths: {
    
  }
});


requirejs(function(app) {
	  alert(11);
	});



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
		
	}
	//menu_classnews_getClassNewsByMy_fn();
}

function index_init(){
	  ajax_getUserinfo(true);
}

window.onload=function(){ 
	index_init();
}; 

