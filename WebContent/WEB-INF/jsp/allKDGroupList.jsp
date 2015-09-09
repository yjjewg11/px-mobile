<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>  
<!DOCTYPE html>
<html>
<head lang="en">
  <meta charset="UTF-8">
  <title>问界互动家园</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
 <link rel="stylesheet" href="http://cdn.amazeui.org/amazeui/2.4.2/css/amazeui.min.css">
</head>
<body>

  <div data-am-widget="list_news" class="am-list-news am-list-news-default" >
  <!--列表标题-->
    <div class="am-list-news-hd am-cf">
       <!--带更多链接-->
        <a href="###" class="">
          <h2>幼儿园介绍</h2>
            <span class="am-list-news-more am-fr">欢迎</span>
        </a>
          </div>

  <div class="am-list-news-bd">
  <ul class="am-list">
 <c:forEach items="${list.data}" var="item" >  
      <li class="am-g am-list-item-desced am-list-item-thumbed am-list-item-thumb-left">
      
      <div class="am-cf">
  <p class="am-align-left">
   <a href="http://jz.wenjienet.com/px-mobile/rest/share/getKDInfo.html?uuid=${item.uuid}" class="">
            <img width="80px" height="80px" src="${item.img}" alt="${item.brand_name}"/>
          </a>
          
             <div class="am-list-main">
            <h3 class="am-list-item-hd"><a href="http://jz.wenjienet.com/px-mobile/rest/share/getKDInfo.html?uuid=${item.uuid}" class="">${item.brand_name}</a></h3>
            <div class="am-list-item-text">${item.company_name} | 联系电话:${item.link_tel} | 地址:${item.address}</div>

        </div>
  </p>

</div>
       
      </li>
</c:forEach> 
    
    </ul>
  </div>

</div>
 
 
</body>
</html>