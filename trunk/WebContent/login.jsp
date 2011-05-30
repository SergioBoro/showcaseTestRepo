<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%@page import="ru.curs.showcase.security.SecurityParamsFactory"%>   

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=8"/>
	<title>Авторизация в КУРС: Showcase</title>
	<link rel="shortcut icon" href="resources/favicon.ico" type="image/x-icon" />
	<link rel="icon" href="resources/favicon.ico" type="image/x-icon" />
	

<script src="js/dojo/dojo.js" djConfig="isDebug: false, parseOnLoad: false"></script>
	
<script type="text/javascript">

	function checkAuthenticationImageSize() {
		var pic = document.getElementById("authenticationImage");
		var w = pic.offsetWidth;  
		
		if (w == 178) {	
			dojo.attr("helloMessage", "innerHTML", "");
			dojo.attr("informationMessage", "innerHTML", "Идет проверка подлинности пользователя...<br>Пожалуйста подождите...");
			id = setTimeout("checkIsAuthenticatedSession()",1000);
		}
		else {			
		    document.formlogin.style.display = "";
		}
	}
	
	function checkIsAuthenticatedSession() {
		dojo.xhrGet({
			sync: true,
			url: "<%=request.getContextPath()%>/auth/isAuthenticatedServlet?sesid=<%=request.getSession().getId()%>",
			handleAs: "json",
			preventCache: true,
			timeout: 10000,
			load: function(data) {
				document.getElementsByName("j_username")[0].value = data.login;
				document.getElementsByName("j_password")[0].value = data.pwd;
				document.formlogin.submit();
			},
			error: function(error) {
				alert("Ошибка соединения с сервером аутентификации.");
			}
		});
	}
   

   
</script>	
	
	
	
</head>
<body onLoad="checkAuthenticationImageSize()">
<%
	String authGifSrc = String.format("%s/authentication.gif?sesid=%s",
			SecurityParamsFactory.getAuthServerUrl(), request.getSession()
					.getId());
%>
<c:if test="${not empty param.error}">
  <font color="red">
  <b>Ошибка!</b>
  <br/>
  Имя пользователя и/или пароль неверны!<br/>
  Отказано в доступе. <br/>
  Ответ сервера: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} 
  <br/>
  </font>
</c:if>
<span id="helloMessage" style="font-size: 27px;color:green">Авторизация в КУРС: Showcase</span>
<span id="informationMessage" style="font-family: sans-serif;"></span>
<form name="formlogin" method="POST" action="<c:url value="/j_spring_security_check" />" style="display:none">
<table>
  <tr>
    <td align="right">Имя пользователя</td>
    <td><input id="j_username" type="text" name="j_username" /></td>
  </tr>
  <tr>
    <td align="right">Пароль</td>
    <td><input  id="j_password" type="password" name="j_password" /></td>
  </tr>
  <tr style="display: none;">
    <td align="right">Запомнить меня</td>
    <td><input type="checkbox" name="_spring_security_remember_me" /></td>
  </tr>
  <tr>
    <td colspan="2" align="right">
      <input type="submit" value="Войти" />
      <input type="reset" value="Сбросить" />
    </td>
  </tr>
</table>
</form>

<br/>
<img src="<%=authGifSrc%>" alt=" " id="authenticationImage" style="visibility:hidden" />

</body>
</html>
