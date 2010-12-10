<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bet</title>
</head>

<%
	if (session.getAttribute("user") == null)
	{
%>
    <jsp:forward page="/Pages/Login.jsp"></jsp:forward>
<%
	} 
%>

<frameset rows="7%,*,7%" cols="*" frameborder="yes" border="5" framespacing="0">
  <frame src="top.jsp" name="topFrame" noresize scrolling=no id="topFrame" />
  <frameset rows="*" cols="40%,40%,20%" framespacing="0" frameborder=no border="0">
    <frame src="Bet/gamesBets.jsp" name="gamesBets" scrolling=yes id="gamesBets" />
    <frame src="Bet/chat.jsp" name="chatFrame" scrolling=yes id="chatFrame" />
    <frame src="Bet/onlineUsers.jsp" name="onlineUsers" scrolling=yes id="onlineUsers" />
  </frameset>
  <frame src="bottom.html" name="bottomFrame" scrolling=no id="bottomFrame" noresize="noresize" />
</frameset>

<body>

</body>
</html>