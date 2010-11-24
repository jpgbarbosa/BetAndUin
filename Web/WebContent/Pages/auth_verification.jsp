<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<%
// TODO 2 This page is a helper that will check for unauthorized accesses (chat_bean.jsp includes it).
// Try to retrieve the User object from the user's session:
// User user = session.getAttribute(...);
// if the retrieved object is null an invalid user page should be displayed

	users.User user = (users.User) session.getAttribute("user");

	if (user == null)
	{
%>
    <jsp:forward page="/invaliduser.html"></jsp:forward>
<%
	} 
%>
