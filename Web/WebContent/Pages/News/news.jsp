<%@page import="server.SoccerReader"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Top 10 Football News</title>
</head>

<style type="text/css">
body {
	background-color:#000000;
}
</style>

<body>

<%

	SoccerReader reader = new SoccerReader();

	 //String lastID = reader.latestHeadlines("Portugal", "sport");
	
	// Then we print the main body of the first.
	System.out.println("\nMore Info:");
	System.out.println("==========");
	
	//String [] array = reader.recentBody(lastID);



%>



<table width="100%" bordercolor="#FFCC00" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3">
  <tr>
    <th width="40%" rowspan="2" scope="col" id="newsCell">&nbsp;</th>
    <th width="20%" scope="col" id="imgCell">&nbsp;</th>
    <th width="*" scope="col" id="headCell"=>&nbsp;</th>
  </tr>
  <tr>
    <th width="20%" scope="col">&nbsp;</th>
    <th scope="col" id="bodyCell">&nbsp;</th>
  </tr>
</table>
</body>
</html>