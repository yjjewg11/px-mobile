function login_affter_init(){
	Vo.init();
}

function menu_userinfo_login_fn(){
	Queue.push(menu_userinfo_login_fn);
	var loginname = getCookie("bs_loginname");
	var password = getCookie("bs_password");
	var pw_checked = getCookie("pw_checked");
	
	React.render(React.createElement(Div_login,{loginname:loginname,password:password,pw_checked:pw_checked})
			, document.getElementById('div_seesion_body'));
}

function menu_body_fn (){
	login_affter_init();
	menu_dohome();
}

function index_init(){
	  ajax_getUserinfo(true);
}

window.onload=function(){ 
	index_init();
}; 

