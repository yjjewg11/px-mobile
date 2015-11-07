<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  
    <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>  
<!DOCTYPE html>
<html>
<head lang="en">
  <meta charset="UTF-8">
  <title>问界互动家园</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
 <script type="text/javascript" src="http://liaoning.sinaimg.cn/ztassist/js/lightbox/prototype.js"></script>
<script type="text/javascript" src="http://liaoning.sinaimg.cn/ztassist/js/lightbox/scriptaculous.js?load=effects"></script>
<script type="text/javascript" src="http://liaoning.sinaimg.cn/ztassist/js/lightbox/lightbox.js"></script>
<link rel="stylesheet" href="http://liaoning.sinaimg.cn/ztassist/css/lightbox.css" type="text/css" media="screen" />
  <style>
    .header {
      text-align: center;
    }
    .header h1 {
      font-size: 200%;
      color: #333;
      margin-top: 30px;
    }
    .header p {
      font-size: 14px;
    }
  </style>
</head>
<body>
 

  <div>${data.content}</div>
  <div>
    <c:forEach items="${data.imgsList}" varStatus="i" var="item" >  
    
    <a href='${fn:substringBefore(item, "@")}' rel="lightbox[roadtrip]"> <img src="${item}"></img></a>
          
            </td>  
        </tr>  
        </c:forEach>  
  </div>
</body>
</html>