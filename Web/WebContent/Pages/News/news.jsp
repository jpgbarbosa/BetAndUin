<%@page import="server.SoccerReader"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Top 10 Football News</title>
<style type="text/css">
<!--
.style1 {
	font-family: Arial, Helvetica, sans-serif;
	color: #EADE00;
}
-->
</style>
</head>

<style type="text/css">
body {
	background-color:#000000;
}
</style>

<body>

<%
	String news, ID;

	String [] body;

	SoccerReader reader = new SoccerReader();
	String [] newsArray = reader.latestHeadlines("Portugal", "sport");
	//String [] array = reader.recentBody(lastID);
%>

<table align="center" width="90%" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3">
  <tr>
    <th scope="col" style="border-bottom:solid; border-bottom-color: #000000"><span class="style1">Top Ten News</span></th>
  </tr>
  <% for(int i=0; i<newsArray.length; i++){
	  
  		ID = newsArray[i].split("<>")[0];
  		news = newsArray[i].split("<>")[1];
  		
  		body = reader.recentBody(ID);

  	
  %>
  
	  <tr style="border:solid; border-color: #000000">
	    <th scope="col"><%=news%> <div id=<%=i+"-NA"%>> 	 <h3 align="center" > <%=body[0] %></h3>
																   <p><img src="<%=body[2]%>"/></p>
																   <p> <%=body[1] %></p> 
									</div>
		</th>
	  </tr>
  
  <%} %>
</table>

</body>
</html>