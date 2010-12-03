<%@page import="server.ClientOperations" %>

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

<script type="text/javascript" src="Bet/comet.js"> </script>

<script type="text/javascript">

var comet = Comet("http://localhost:8080/BetAndUinWeb/");

function reset(user){
	comet.post("WebServer?"+"type=reset&username="+user,'',function(response) {});
	location.reload(true);
}
</script>


<body>
	<div align="right" >
		<table style="background-color:#FBED40" width="100%" border="0" cellspacing="0" cellpadding="4">
		<% 	
			String userName="";
		    String credits="";
		
			try{
				userName = ((String)session.getAttribute("user"));
				credits = ((ClientOperations)session.getAttribute("server")).clientShowCredits(userName);
			} catch(Exception e){
				System.out.println("Exception getting Show Users Credits");
			}
		%>
	      <tr>
	        <td id="homenews" width="10%" align="left" style="font-family:Arial, Helvetica, sans-serif; font-weight: bold"><a href="News/news.jsp" target="_blank">Home News</a></td>
		    <td width="*"></td>
	        <td style="border-right:solid; border-right-color:#000000; font-family:Arial, Helvetica, sans-serif; font-weight: bold" width="25%" align="right" id="username"><%=userName%></td>
	        <td style="font-family:Arial, Helvetica, sans-serif; font-weight: bold" width="10%" align="center" id="credits"><%=credits%> credits</td>
	        <td width="2%" style="border-right:solid; border-right-color:#000000;"> <span style="color: blue; text-decoration: underline; cursor:hand; cursor:pointer;" onclick="reset('<%=userName%>');"> (reset) </span> </td>
	        <td width="2%"> <input align="right" name="buttonCr" type="button" value="Refresh" onclick="location.reload(true);" /> </td>
	        <td id="logout" width="5%" align="center" style="font-family:Arial, Helvetica, sans-serif"><a href="http://localhost:8080/BetAndUinWeb/Logout">Logout</a></td>
	      </tr>
	    </table>
	</div>
</body>
</html>