<%@page import="chatJSP.InfoBean"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="java.util.*" %>

<html>
<META HTTP-EQUIV=Refresh CONTENT="1; URL=http://localhost:8080/BetAndUinWeb/Pages/Bet/chat_log_bean.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Messages</title>
</head>
<body>
<jsp:useBean id="messagebean" class="chatJSP.MessageBean" scope="application"/>
<br>

we are in chat_log_bean
<%

List<String> messages = messagebean.getMessages();
for (Iterator<String> i= messages.iterator(); i.hasNext();)
{
	out.println(i.next());
	out.println("<br>");
}
%>
</body>
</html>