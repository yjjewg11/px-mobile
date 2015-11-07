    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head lang="en">
  <meta charset="UTF-8">
  <title>食谱-${group.brand_name}-${plandate}</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
 
  <style>
  
  	
	
.G_cookplan_Img{width:135px; height:100px; position:relative; margin:0px 10px 10px 0px; float:left;}
.G_cookplan_Img_img{width: 134px;height: 100px;}
.G_cookplan_Img_close{position:absolute; top:0px; right:0px;cursor: pointer;}
  
  
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
 
 <h1>食谱-${group.brand_name}-${plandate}</h1>
 <h2> ${group.brand_name}|${group.link_tel}|${group.address}|浏览${count}次</h2>
  <div>
<div>
 		<label>早餐:</label> 
		<div >
		<c:forEach var="item" items="${data.list_time_1}">   
				<div className="G_cookplan_Img" >
	 	       			<img className="G_cookplan_Img_img"    src="${item.img}" alt="图片不存在" title="${item.name}" />
	 	       			<span >${item.name}</span>
	 	       		</div>	
		</c:forEach>  
		
	     	 	            		
	    		</div>
		 <div className="cls"></div>
		 <br/>
		 <label>早上加餐:</label> 
			<c:forEach var="item" items="${data.list_time_1}">   
				<div className="G_cookplan_Img" >
	 	       			<img className="G_cookplan_Img_img"    src="${item.img}" alt="图片不存在" title="${item.name}" />
	 	       			<span >${item.name}</span>
	 	       		</div>	
		</c:forEach>  
		
		 <div className="cls"></div>
		 <br/>
		 <label>午餐:</label> 
			<c:forEach var="item" items="${data.list_time_1}">   
				<div className="G_cookplan_Img" >
	 	       			<img className="G_cookplan_Img_img"    src="${item.img}" alt="图片不存在" title="${item.name}" />
	 	       			<span >${item.name}</span>
	 	       		</div>	
		</c:forEach>  
		
		 <div className="cls"></div>
		 <br/>
		 <label>下午加餐:</label> 
		 	<c:forEach var="item" items="${data.list_time_1}">   
				<div className="G_cookplan_Img" >
	 	       			<img className="G_cookplan_Img_img"    src="${item.img}" alt="图片不存在" title="${item.name}" />
	 	       			<span >${item.name}</span>
	 	       		</div>	
		</c:forEach>  
		
		 <div className="cls"></div>
		 <br/>
		 <label>晚餐:</label> 
		 	<c:forEach var="item" items="${data.list_time_1}">   
				<div className="G_cookplan_Img" >
	 	       			<img className="G_cookplan_Img_img"    src="${item.img}" alt="图片不存在" title="${item.name}" />
	 	       			<span >${item.name}</span>
	 	       		</div>	
		</c:forEach>  
		
		 <div className="cls"></div>
		 <br/>
		 <label>营养分析:</label> 
		 <div className="g_analysis">
			${data.analysis}
		 </div>

</div>

</div>
</body>
</html>