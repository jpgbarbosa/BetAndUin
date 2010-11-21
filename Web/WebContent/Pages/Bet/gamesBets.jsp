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
		font-size:16px;
	}
	
	.rowStyle{
	 	font-family: Arial, Helvetica, sans-serif;
		color:#000000;
		font-size:14px;
	}
    </style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<h1 class="style1"> Bet </h1>
<h3 class="style1 style2"> Give your best Shot </h3>
<div id="betsTable">
<table width="100%" bordercolor="#FFCC00" style="background-color:#FFFFCC" cellpadding="3" cellspacing="3">
  <tr bordercolor="#000000" style="border-bottom:solid">
    <th width="*" scope="col" class=style3>Home Team</th>
    <th width="15%" scope="col" class=style3>X</th>
    <th width="*" scope="col" class=style3>Away Team</th>
	<th width="9%" scope="col" class=style3>Amount of Cr</th>
    <th width="3%" scope="col" class=style3>Bet!</th>
  </tr>
  <tr>
    <th width="*" scope="col"  class=rowStyle><input type="checkbox"> equipa1</th>
    <th width="15%" scope="col" class=rowStyle><input type="checkbox"> Tie</th>
    <th width="*" scope="col" class=rowStyle><input type="checkbox"> equipa2</th>
	<th width="9%" scope="col" class=rowStyle><input type="text" name="textfield" /> Cr</th>
    <th width="3%" scope="col" class=rowStyle><input width="100%" name="button" type=button value="Bet!"></th>
  </tr>
</table>

</div>
</body>
</html>