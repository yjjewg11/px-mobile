function login_affter_init(){
	var div_header_props = {
			  "title": Store.getCurGroup().brand_name+"-"+Store.getUserinfo().name,
			  "link": "#title-link",
			  data: {
			    "left": [
			             {
			 		        "link": "javascript:Queue.doBackFN();",
			 		        "icon": "chevron-left"
			 		      },
			      {
			        "link": "javascript:menu_dohome();",
			        "icon": "home"
			      }
			    ],
			    "right": [
			      {
			        "link": "#right-link",
			        "icon": "bars"
			      }
			    ]
			  }
			};

	//menu
	var div_menu_data=[
	                   {
	                          "link": "##",
	                          "fn":menu_accounts_list_fn,
	                          "title": "收支记录"
	                        },
	        {
		    "link": "##",
		    "title": "角色权限",
		    "fn":menu_role_list_fn
		   
		  },
		  {
              "link": "##",
              "title": "用户管理",
              "fn":menu_userinfo_list_fn,
              "subCols": 2
             // "channelLink": "进入栏目 »",
            },
		  {
              "link": "##",
              "title": "基础数据",
              "fn":menu_basedatatype_list_fn,
              "subCols": 2
             // "channelLink": "进入栏目 »",
            },
            {
                "link": "##",
                "title": "我",
                "subCols": 3,
                "subMenu": [
    	                    {
    	                    	 "fn":menu_userinfo_update_fn,
    	                      "link": "##",
    	                      "title": "修改资料"
    	                    },
    	                    {
    	                    	 "fn":menu_userinfo_updatepassword_fn,
    	                        "link": "##",
    	                        "title": "修改密码"
    	                      },
    	                     
    	                    {
    	                      "link": "##",
    	                      "title": "设置(未)"
    	                    },
    	                    {
    	                        "link": "##",
    	                        "fn":menu_userinfo_logout_fn,
    	                        "title": "注销"
    	                      }
    	                  ]
              }
        
      ];
	
	

	React.render(React.createElement(AMUIReact.Header,div_header_props), document.getElementById('div_header'));
	React.render(React.createElement(AMUIReact.Menu,{cols:4,data:div_menu_data,onSelect:div_menu_handleClick}), document.getElementById('div_menu'));

}

/**
 * 显示bodydiv,隐藏其他所有控件div
 */
function body_show(){
}

var div_menu_handleClick = function(nav, index, e) {
	  if (nav && nav.subMenu) {
	    // 有二级菜单的链接点击了
		 if( typeof  nav.fn=="function"){
			 body_show();
			 this.closeAll();
			 nav.fn();
			 
		 }
		  console.log('点击的链接为：', nav);
	  } else {
	    e.preventDefault();
	    if( typeof  nav.fn=="function"){
	    	body_show();
	    	this.closeAll();
			 nav.fn();
			 
		 }
	    console.log('点击的链接为：', nav);
	    // do something
	    // this.closeAll(); //关闭二级菜单
	  }
	};

function menu_dohome(){
	Queue.push(menu_dohome);
	var div_Gallery_data=[
	                      
	                    	  {
		                    	    "img": hostUrl+"i/header.png",
		                    	    "link": "###",
		                    	    "title": "我的通知公告"
		                    	  },
		                    	  {
			                    	    "img": hostUrl+"i/header.png",
			                    	    "link": "###",
			                    	    "title": "我的班级通知"
			                    	  },
			                    	  {
				                    	    "img": hostUrl+"i/header.png",
				                    	    "link": "###",
				                    	    "title": "我"
				                    	  },
		                    	  {
	                    	    "img": hostUrl+"i/header.png",
	                    	    "link": "###",
	                    	    "title": "点名"
	                    	  },
		                      {
		                    	    "img": hostUrl+"i/header.png",
		                    	    "link": "###",
		                    	    "title": "我的班级"
		                    	  }
	                    	  ];
	React.render(React.createElement(AMUIReact.Gallery,{sm:3,md:4,lg:6,themes:'bordered',data:div_Gallery_data}), document.getElementById('div_body'));
}



//group
function menu_group_myList_fn() {
	Queue.push(menu_group_myList_fn);
	ajax_group_myList();
}



function menu_kd_group_reg_fn(){
	Queue.push(menu_class_list_fn);
	React.render(React.createElement(Div_kd_group_reg,null)
			, document.getElementById('div_login'));
	$("#div_seesion_body").hide();
}
//班级管理
function menu_class_list_fn() {
	Queue.push(menu_class_list_fn);
	ajax_class_listByGroup(Store.getCurGroup().uuid);
};

function menu_teachingplan_list_fn(){
	//first 选择班级
	w_ch_class.open(ajax_teachingplan_listByClass);
	Queue.push(ajax_teachingplan_listByClass);
}

function menu_cookbookPlan_list_fn(){
	Queue.push(menu_cookbookPlan_list_fn);
	ajax_cookbookPlan_listByGroup(Store.getCurGroup().uuid);
}

//类型'0:普通通知 1:内部通知 2：班级通知',
var announce_types=1;
function menu_announce_list_fn(types) {
	announce_types=types;
	Queue.push(menu_announce_list_fn);
	ajax_announce_listByGroup(Store.getCurGroup().uuid);
};

function menu_userinfo_reg_fn(){
	
	ajax_loaddata_group_list_for_userinfo_reg();
	
}

function menu_userinfo_login_fn(){
	Queue.push(menu_userinfo_login_fn);
	var loginname = getCookie("bs_loginname");
	var password = getCookie("bs_password");
	var pw_checked = getCookie("pw_checked");
	
	React.render(React.createElement(Div_login,{loginname:loginname,password:password,pw_checked:pw_checked})
			, document.getElementById('div_login'));
	$("#div_seesion_body").hide();
}


function menu_userinfo_logout_fn(){
	ajax_userinfo_logout();
}
function menu_body_fn (){
	$("#div_seesion_body").show();
	//$("#div_login").hide();
	$("#div_login").html(null);

	login_affter_init();
	menu_dohome();
}
ajax_getUserinfo(true);

