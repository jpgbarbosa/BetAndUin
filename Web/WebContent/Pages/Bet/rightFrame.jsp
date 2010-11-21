<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Untitled Document</title>
</head>

<style type="text/css">
body {
	background-color:#000000;
}
</style>


 <script type="text/javascript">
 
 function splitAndAddNameToTable(id){
	 var result;

 }

function addRow(id,userName){
    var tbody = document.getElementById(id).getElementsByTagName("TBODY")[0];
    var row = document.createElement("TR")
    var td1 = document.createElement("TD")
    td1.appendChild(document.createTextNode(userName))
    row.appendChild(td1);
    tbody.appendChild(row);
  }
</script>

<body>
<p> 
  <input align="middle" name="button" type="button" value="Refresh" onclick="splitAndAddNameToTable('betTable')" />
</p>
<p>
<table align="center" width="100%" bordercolor="#FFCC00" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3" id="betTable">
  <tbody>
    <tr>
      <td bordercolor="#000000" cellpadding="3" cellspacing="3" style=" border-bottom:solid">Clients on-linee</td>
    </tr>
  </tbody>
    <%
 	try{
		((clientRMI.Client)session.getAttribute("user")).getUsername();
		result = ((clientRMI.Client)session.getAttribute("user")).getMainServer().clientShowUsers();
	} catch(Exception e){
		System.out.println("excepetion splitAndAddNameToTable");
	}
	
	alert(result);
	
	result = result.split("\n");
	
	
	
	var i=0;
	while (i < result.length){
		addRow(id,result[i]);
		i++;
	}	
    %>
</table>
</p>
</body>



