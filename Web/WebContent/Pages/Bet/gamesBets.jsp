<%@page import="serverWeb.WebServer" %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <style type="text/css">
    	body {
			background-color: #000000;
			cursor:default;
		}
    .style1 {
	font-family: Arial, Helvetica, sans-serif;
	font-weight: bold;
	color: #FFFFFF;
}
    .style2 {color: #FFFF00}
	
	.style3{
	 	font-family: Arial, Helvetica, sans-serif;
		color:#FFCC00;
		font-size:15px;
	}
	
	.rowStyle{
	 	font-family: Arial, Helvetica, sans-serif;
		color:#000000;
		font-size:13px;
	}
    </style>
    
<script type="text/javascript" src="comet.js"> </script>
<script type="text/javascript">

var comet = Comet("http://localhost:8080/BetAndUinWeb/");
var codeToBet = ['1','X','2'];
function makeBet(id) {
	
   var buttonGroup = id+"B";
   var f = document.forms[0];
   var radios = f[id + "B"];
   var ans = -1;
   for(var i=0;i<radios.length;i++){
	   if (radios[i].value == "on") ans = i;
   }
   if (ans == -1) {
	   alert("You have to bet on some result");
   } else {
	   
	   var gameNumber = document.getElementById(id+"-N").innerHTML;
       var credits = document.getElementById(id+"-C").value;
	   
	   if(!isNaN(parseInt(credits)) || !isNaN(parseInt(gameNumber))){           
           
           alert("gameNumber="+gameNumber+"&bet="+codeToBet[ans]+"&credits="+credits);
		   
       	comet.post("BetServlet?"+"gameNumber="+gameNumber+"&bet="+codeToBet[ans]+"&credits="+credits,'',function(response) {
       		alert("Message was sent");
       	});
       	
       	 
       	var crs = document.getElementById("credits").innerHTML;
       	document.getElementById("credits").innerHTML = crs - credits;
       } else {
       		alert("Credits inserted are not valid!");
       }
   }
}

  
</script>
    
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h1 class="style1"> Bet </h1>
<h3 class="style1 style2"> Give your best Shot </h3>
<div id="betsTable">
<form name="betForms">
<table width="100%" bordercolor="#FFCC00" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3">
  <tr bordercolor="#000000" style="border-bottom:solid; border-bottom-color:#000000">
    <th width="4%" scope="col" class=style3>Game No.</th>
    <th width="*" scope="col" class=style3>Home Team</th>
    <th width="10%" scope="col" class=style3>X</th>
    <th width="*" scope="col" class=style3>Away Team</th>
	<th width="6%" scope="col" class=style3>Amount of Cr</th>
    <th width="3%" scope="col" class=style3>Bet!</th>
  </tr>
  
     <% 
    String[] games;
    String gameString=null;
     
  	try{
  		//TODO: Again, how to do it?
 		//((clientRMI.Client)session.getAttribute("user")).getUsername();
 		gameString = WebServer.getMainServer().clientShowMatches();
 	} catch(Exception e){
 		System.out.println("Error getting session by RMI");
 	}
    	
    	games = gameString.split("\n");
    	String gameNo,gameH,gameA;
    	String [] temp1,temp2;
    	int numGames = games.length;
    	
    	for(int i=2; i< numGames; i++){
    		temp1 = games[i].split(" - ");
    		gameNo = temp1[0];
    		temp2 = temp1[1].split(" vs ");
    		gameH =temp2[0];
    		gameA =temp2[1];
    		%>
    		<tr id=<%="betTableRow-"+i%>>
    			<th width="4%" scope="col" class=rowStyle id=<%=i+"-N"%>><%=gameNo%></th>
    			<th width="*" scope="col" align="left" class=rowStyle ><input  name="<%=i+"B"%>" type="radio" id="bet_<%=i%>_1"> <%=gameH%></th>
    			<th width="10%" scope="col" align="left" class=rowStyle ><input  name="<%=i+"B"%>" type="radio" id="bet_<%=i%>_X"> Tie</th>
    			<th width="*" scope="col" align="left" class=rowStyle ><input  name="<%=i+"B"%>" type="radio" id="bet_<%=i%>_2"><%=gameA%></th>
    			<th width="6%" scope="col"  class=rowStyle ><input id=<%=i+"-C"%> width="60px" type="text" name="textfield" /></th>
    			<th width="3%" scope="col"  class=rowStyle id=<%=i+"-B"%>><input onClick="makeBet('<%=i%>')" width="100%" name="button" type=button value="Bet!"></th>
    		</tr>
    	<% }%>
</table>
</form>
<br />
<input type="button" value="Reload" onClick="location.reload(true);">
</div>
</body>
</html>