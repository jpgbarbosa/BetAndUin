<%@page import="serverWeb.WebServer" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<style type="text/css">
body {
	background-color:#FAE701;
}
</style>



<body>
	<div align="right" >
		<table style="background-color:#FBED40" width="100%" border="0" cellspacing="0" cellpadding="4">
		<% 	
			String userName="";
		    String credits="";
		
			try{
				userName = (String)session.getAttribute("user");
				credits = WebServer.getMainServer().clientShowCredits(userName);
			} catch(Exception e){
				System.out.println("Exception getting Show Users Credits");
			}
		%>
	      <tr>
	        <td id="homenews" width="10%" align="left" style="font-family:Arial, Helvetica, sans-serif; font-weight: bold"><a href="Logout">Home News</a></td>
		    <td width="*"></td>
		    <td width="7%"> <input align="right" name="buttonCr" type="button" value="Refresh" onclick="location.reload(true);" /> </td>
	        <td style="border-right:solid; border-right-color:#000000; font-family:Arial, Helvetica, sans-serif; font-weight: bold" width="25%" align="right" id="username"><%=userName%></td>
	        <td style="border-right:solid; border-right-color:#000000; font-family:Arial, Helvetica, sans-serif; font-weight: bold" width="10%" align="center" id="credits"><%=credits%> credits</td>
	        <td id="logout" width="7%" align="center" style="font-family:Arial, Helvetica, sans-serif"><a href="http://localhost:8080/BetAndUinWeb/Logout">Logout</a></td>
	      </tr>
	    </table>
	</div>
</body>
</html>