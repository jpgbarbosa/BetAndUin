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
.style2 {
	font-family: Arial, Helvetica, sans-serif;
	color: #FFF;
}
-->
</style>
</head>

<style type="text/css">
body {
	background-color:#000000;
}
</style>

<script type="text/javascript">

function showHide(id){
	
	for(var i=0; i<10; i++){
		document.getElementById(i+"-CA").style.display='none';
	}
	
	
	
	document.getElementById(id.split("-")[0]+"-CA").style.display='inherit';
}

</script>


<body>

<%
	String news="", ID;

	String [] body;

	SoccerReader reader = new SoccerReader();
	String [] newsArray = reader.latestHeadlines("Benfica", "sport");

	for(int i=0; i<newsArray.length; i++){
	  
  		ID = newsArray[i].split("<>")[0];
  		news = newsArray[i].split("<>")[1];
  		
  		body = reader.recentBody(ID);

  	
  %>	  
	  <div align="center" style="display: none" id=<%=i+"-CA"%>> 	 <h3 align="center" class="style2"> <%=body[0] %></h3>
																   <p><img src="<%=body[2]%>"/></p>
																   <p class="style2"> <%=body[1] %></p> 
	  </div>
	
  
  <%} %>

<table align="center" width="90%" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3">
  <tr>
    <th scope="col" style="border-bottom:solid; border-bottom-color: #000000"><span class="style1">Top Ten News</span></th>
  </tr>
  
<% for(int x=0; x<newsArray.length; x++){%>

	<tr style="border:solid; border-color: #000000">
	    <th  style="cursor:hand; cursor:pointer;" id=<%=x+"-NA"%> scope="col" onclick="showHide(this.id)"> <%=newsArray[x].split("<>")[1] %></th>
	</tr> 
	
<%	}%>

</table>

</body>
</html>