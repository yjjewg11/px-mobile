目录说明：
jsx/--jsx文件夹，不引入html，需要翻译为js文件。
jsb/--jsb文件夹下，翻译后的js文件，引入到html中。
kd/--幼儿园模块
w/--第三分插件,不修改,加离线缓存
kd/kd_service.js--。业务层，与后台数据交互层
方法名以：
ajax_模块名_请求方法：所有数据请求
ajax_userinfo_login
btn_click__模块名_对象：所有toolbar按钮事件
btn_click_class_list
_react：展现UI模板定义。
kd/index.js--主页对于js部分，菜单方法，body内容。。
菜单明明规则：menu_xx_fn
menu_kd_group_reg_fn
命名规则：
菜单功能按钮，调用方法：menu_xx_fn
公共组件（widget）。入口方法：weget_xx_fn

js/common_service.js --公共业务层，与后台数据交互层
命名规则：commons_XX_模块_对象
commons_ajax_reply_list

jsx/common_react.js  --公共UI模板定义
命名规则：Common_XX_模块_对象
方法说明：
menu_userinfo_login_fn();//跳转登录页面