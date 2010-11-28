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
				((clientRMI.Client)session.getAttribute("user")).getUsername();
				userName = ((clientRMI.Client)session.getAttribute("user")).getUsername();
				credits = ((clientRMI.Client)session.getAttribute("user")).getMainServer().clientShowCredits(userName);
			} catch(Exception e){
				System.out.println("exception splitAndAddNameToTable");
			}
		%>
	      <tr>
	        <td id="homenews" width="10%" align="left" style="font-family:Arial, Helvetica, sans-serif; font-weight: bold"><a href="NewsMain.html" target="_blank">Home News</a></td>
		    <td width="*"></td>
	        <td style="border-right:solid; border-right-color:#000000; font-family:Arial, Helvetica, sans-serif; font-weight: bold" width="25%" align="right" id="username"><%=userName%></td>
	        <td style="border-right:solid; border-right-color:#000000; font-family:Arial, Helvetica, sans-serif; font-weight: bold" width="10%" align="center" id="credits"><%=credits%> credits</td>
	        <td id="logout" width="7%" align="center" style="font-family:Arial, Helvetica, sans-serif"><a href="Logout">Logout</a></td>
	      </tr>
	    </table>
	</div>
</body>
</html>