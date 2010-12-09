<%@page import="server.ClientOperations" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
</head>

<style type="text/css">
body {
	background-color:#000000;
}
</style>


 <script type="text/javascript">
  
function sendToMsgBox(id){
	parent.chatFrame.document.getElementById("destination").value=document.getElementById(id).innerHTML;;
}
  
</script>

<body>
<br>
<br>

<p>
<table align="center" width="100%" bordercolor="#FFCC00" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3" id="betTable">
  <tbody>
    <tr>
      <td bordercolor="#000000" style=" border-bottom:solid">Clients on-line</td>
    </tr>
  </tbody>
    <%
    String usersOn=null;
    
 	try{
		usersOn = ((ClientOperations)session.getAttribute("server")).clientShowUsers();
	} catch(Exception e){
		System.out.println("Error getting ShowUsers in OnlineUsers.jsp");
	}
	
	if(usersOn!=null){
	 	String [] usersOntArray = usersOn.split("\n");
		int i=0;
		
		while (i < usersOntArray.length){ %>
			<tr><td id=<%=i%> style="cursor:hand; cursor:pointer;" onClick="sendToMsgBox(this.id)"><%=usersOntArray[i]%></td></tr>
			<%
			i++;
		}
	}
    %>
</table>
</body>
