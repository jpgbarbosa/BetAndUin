<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
</head>
<body>
<jsp:useBean id="messagebean" class="chatJSP.MessageBean" scope="application"/>
<jsp:useBean id="infoBean" class="chatJSP.InfoBean" scope="request"/>
<jsp:setProperty name="infoBean" property="*"/>
<%
	users.User user = (users.User) session.getAttribute("user");


	if (user != null && infoBean.getMessage() != null)
	{
		//TODO 3 use messagebean.setMessage(...) to set a message that includes
		// the users nickname and also the message sent (available at the infoBean) 
		
		messagebean.setMessage(user.getUserName() + " says: " + infoBean.getMessage() + "\n");
	}
%>


<form action="chat_in_bean.jsp" method="post">
<input type="text" name="message" id="message" size="100">
<br>
<input type="submit" name="buttonsend" value="Send">
</form>
</body>
</html>
