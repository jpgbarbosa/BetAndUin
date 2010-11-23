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
    
<script type="text/javascript">
  
function checkBoxes(parentID,boxID){

	alert(rowID+"");
	var rowID=parentID.split("-")[1];
	var checkedNow=rowID+boxID;
	
	if ((checkedNow!=(rowID+"-H")) && eval("getElementByID(rowID+"+"-H"+").checked")  == true) {
		getElementByID(rowID+"-H").checked  = false;
	}
	else if ((checkedNow!=(rowID+"-T")) && eval("getElementByID(rowID+"+"-T"+").checked")  == true) {
		getElementByID(rowID+"-T").checked  = false;
	}
	else if ((checkedNow!=(rowID+"-A")) && eval("getElementByID(rowID+"+"-A"+").checked")  == true) {
		getElementByID(rowID+"-A").checked  = false;
	}
}

function checkBoxValidate(cb) {
	for (j = 0; j < 8; j++) {
	if (eval("document.myform.ckbox[" + j + "].checked") == true) {
	document.myform.ckbox[j].checked = false;
	if (j == cb) {
	document.myform.ckbox[j].checked = true;
	         }
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
 		((clientRMI.Client)session.getAttribute("user")).getUsername();
 		gameString = ((clientRMI.Client)session.getAttribute("user")).getMainServer().clientShowMatches();
 	} catch(Exception e){
 		System.out.println("exception splitAndAddNameToTable");
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
    			<th width="*" scope="col" align="left" class=rowStyle ><input name=<%=i+"B"%> type="checkbox" onClick="checkBoxValidate(this.parent.parent.parent.ID)" id=<%=i+"-H"%>> <%=gameH%></th>
    			<th width="10%" scope="col" align="left" class=rowStyle ><input name=<%=i+"B"%> type="checkbox" onClick="checkBoxValidate(this.parent.parent.parent.ID)" id=<%=i+"-T"%>> Tie</th>
    			<th width="*" scope="col" align="left" class=rowStyle ><input name=<%=i+"B"%> type="checkbox" onClick="checkBoxValidate(this.parent.parent.parent.ID)" id=<%=i+"-A"%>> <%=gameA%></th>
    			<th width="6%" scope="col"  class=rowStyle id=<%=i+"-C"%>><input width="60px" type="text" name="textfield" /></th>
    			<th width="3%" scope="col"  class=rowStyle id=<%=i+"-B"%>><input width="100%" name="button" type=button value="Bet!"></th>
    		</tr>
    	<% }%>
</table>
<br />
<input type="button" value="Reload" onClick="location.reload(true);">
</div>
</body>
</html>