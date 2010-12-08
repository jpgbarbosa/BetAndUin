<%@page import="server.SoccerReader"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Top 30 Football News</title>
<style type="text/css">
<!--
.style1 {
	font-family: Arial, Helvetica, sans-serif;
	color: #EADE00;
}
.style2 {
	font-family: Arial, Helvetica, sans-serif;
	color: #FFFFF;
}

.style3 {
	font-family: Arial, Helvetica, sans-serif;
	color: #FFFFF;
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

	/* Watchout for this magic numbers! */
	for(var i=0; i<30; i++){
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
	String [] headlines = {"Benfica", "Sporting", "Porto"};
	
	ArrayList<String> newsArray = reader.mergeHeadlines(headlines, "sport");
	if(newsArray == null){%>
		<div align="center" class="style1"> It looks like we had a fatal error. We already send two superb engineers to solve the problem. Try again later! </div>
		<br>
		<div align="center"> <img alt="" src="fatal-error-cartoon.jpg"> </div>
		<br>
		<div align="center" class="style1"> Just in case, please take a walk while you wait... </div>
	<%
		return;
	}

	for(int i=0; i<newsArray.size(); i++){
	  
  		ID = newsArray.get(i).split("<>")[0];
  		news = newsArray.get(i).split("<>")[1];
  		
  		body = reader.recentBody(ID);
  		if(body == null){
  			
  		}
  	
  %>	  
	  <div align="center" style="display: none; color: white; font-family: Arial, Helvetica, sans-serif;" id=<%=i+"-CA"%>> 	 <h3 align="center" style="font-family: Arial, Helvetica, sans-serif;"> <%=body[0] %></h3>
																   <p><img src="<%=body[2]%>"/></p>
																   <p style="font-family: Arial, Helvetica, sans-serif;"> <%=body[1] %></p> 
	  </div>
	
  
  <%} %>

<table align="center" width="90%" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3">
  <tr>
    <th scope="col" style="border-bottom:solid; border-bottom-color: #000000"><span class="style1">Top Thirty News</span></th>
  </tr>
  
<% for(int x=0; x<newsArray.size(); x++){%>

	<tr style="border:solid; border-color: #000000">
	    <th  style="cursor:hand; cursor:pointer;" id=<%=x+"-NA"%> scope="col" onclick="showHide(this.id)"> <%=newsArray.get(x).split("<>")[1] %></th>
	</tr> 
	
<%	}%>

</table>

</body>
</html>