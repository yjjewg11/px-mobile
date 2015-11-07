<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
  <meta charset="UTF-8">
  <title>${group.brand_name}-${data.title}</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<link rel="stylesheet" href="../../css/share.css?090132"/>
</head>
<body>
 
 <h2  class="art_title">${data.title}</h2>
 <div class="art_meta">
  <p> ${group.brand_name}|${group.link_tel}|发布时间:${data.create_time}|浏览${count}次|${group.address}</p>
  </div>
  <div>${data.message}</div>
  
   
<footer class="footer">
  <p><a href="http://www.wenjienet.com/">问界互动家园</a></p>
</footer>
  
</body>
</html>