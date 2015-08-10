<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head lang="en">
  <meta charset="UTF-8">
  <title>${group.brand_name}-${data.title}</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
 
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
 
 <h1>${data.title}</h1>
  <h2> ${group.brand_name}|${group.link_tel}|${group.address}</h2>
  <h3>作者:${data.create_user},发布时间:${data.create_time}, 浏览${count}次</h3>
  <div>${data.message}</div>
</body>
</html>