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

function setButton(name, thisId){
	 var buttonGroup = name+"B";
	 alert("inicio nice: ");
	 for (var i=0; i<buttonGroup.length; i++) {
         buttonGroup[i].checked=false;
         alert("almost nice: "+buttonGroup[i].checked + " | "+thisId);
         if(buttonGroup[i].id === thisId){
        	 alert("nice");
        	 buttonGroup[i].checked=true;
         }
	 }
}

function makeBet(id) {
   var buttonGroup = id+"B";
   if (buttonGroup[0]) {
      for (var i=0; i<buttonGroup.length; i++) {
         if (buttonGroup[i].checked) {
            var bet=buttonGroup.id.substring(buttonGroup.id.length-1,buttonGroup.id.length);
            var gameNumber = document.getElementById(id+"-N").innerHTML;
            var credits = document.getElementById(id+"-N").value;
            
            if(!isNaN(parseInt(credits)) || !isNaN(parseInt(gameNumber))){
            	comet.post("serverWeb.BetServlet","gameNumber="+gameNumber+"&bet="+bet+"&credits="+credits,function(response) {
            		alert("Message was sent");
            	});            	
            } else {
            	alert("Credits inserted are not valid!");
            }
         }
         alert("cenas2"+buttonGroup[i].checked);
      }
   } else {
      alert("butao nao encontrado");
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
<form width="100%" bordercolor="#FFCC00" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3">
<table>
  <tr bordercolor="#000000" style="border-bottom:solid; border-bottom-color:#000000">
    <th width="4%" scope="col" class=style3>Game No.</th>
    <th width="*" scope="col" class=style3>Home Team</th>
    <th width="10%" scope="col" class=style3>X</th>
    <th width="*" scope="col" class=style3>Away Team</th>
	<th width="6%" scope="col" class=style3>Amount of Cr</th>
    <th width="3%" scope="col" class=style3>Bet!</th>
  </tr>
  </table>
 </form>
  
     <% 
    String[] games;
    String gameString=null;
     
  	try{
 		//((clientRMI.Client)session.getAttribute("user")).getUsername();
 		//TODO: How to do it now?
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
    		<form id=<%="betTableRow-"+i%>>
    			<span id=<%=i+"-N"%>><%=gameNo%></span>
    			<input  name=<%=i+"B"%> type="radio" id=<%=i+"-1"%> onClick="setButton('<%=i+""%>',this.id)"> <%=gameH%>
    			<input  name=<%=i+"B"%> type="radio" id=<%=i+"-X"%> onClick="setButton('<%=i+""%>',this.id)"> Tie
    			<input  name=<%=i+"B"%> type="radio" id=<%=i+"-2"%> onClick="setButton('<%=i+""%>',this.id)"> <%=gameA%>
    			<input id=<%=i+"-C"%> width="60px" type="text" name="textfield" />
    			<input onClick="makeBet('<%=i+""%>')" width="100%" name="button" type=button value="Bet!">
    		</form>
    	<% }%>

<br />
<input type="button" value="Reload" onClick="location.reload(true);">
</div>
</body>
</html>