<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
   <head>
      <title th:utext="${title}"></title>
   </head>
    
   <body>
    
      <!-- Include _menu.html -->
      <th:block th:include="/_menu"></th:block>  
       
      <h2>Home Page!</h2>
       
   </body>
</html>