<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>BetAndUin - Register</title>
<style type="text/css">
<!--

#Layer1 {
	position:absolute;
	width:200px;
	height:115px;
	z-index:1;
	left: 191px;
	top: 21px;
}
body {
	background-color: #000000;
}
.style4 {
	font-size: 36px;
	color:#FFFFFF;
	font-family: Arial, Helvetica, sans-serif;
}
.style5 {
	font-family: Arial, Helvetica, sans-serif;
}

.style6 {
	font-family: Arial, Helvetica, sans-serif;
	color: #CCCCCC;
	font-size: 16px;
		font-family: Arial, Helvetica, sans-serif;
}
.style7{
background-color:#FFFF33;
border:#000000;
border:solid;
font-family: Arial, Helvetica, sans-serif;

}
.style8{
background-color:#FFFFFF;
border:#000000;
border:solid;
font-family: Arial, Helvetica, sans-serif;
}
.headerTable{
background-color:#CCCCCC;
border:#000000;
border:solid;
font-family: Arial, Helvetica, sans-serif;
}
.style20 {font-family: "Times New Roman", Times, serif}

-->
</style>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
</head>
<body class="sub">
<h1 align="center" class="style4" >&nbsp;</h1>
 <h1 align="center" class="style4" >BetAndUin
 </h1>
 <form action="http://localhost:8080/BetAndUinWeb/WebServer?type=register" method="post" name="form1" id="form1">
  <table width="100%" border="0" cellspacing="0" cellpadding="1" class="TitleColor">
    <tr style="vertical-align: top">
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="4">
          <tr class="headerTable">
            <td width="20%" style="vertical-align: top; border:solid">              <h3>Register</h3>            </td>
            <td width="80%" style="vertical-align: top; border:solid">              <h3>Register</h3>            </td>
          </tr>
          <tr style="vertical-align: top">
            <td width="20%" class="style8">
			  <label for="email"><strong>E-mail</strong></label>&nbsp;<br />
              <input id="email" name="email" type="text" size="25" />
              <br />
			  <br />
			  <label for="username"><strong>User Name</strong></label>&nbsp;<br />
              <input id="username" name="username" type="text" size="25" />
              <br />
			  <br />
              <label for="password"><strong>Password</strong></label>&nbsp;<br />
              <input id="password" name="password" type="password" size="25" />
              <p>
                <input type="submit" name="ButtonName" value="Regist" />
                <a href="/BetAndUinWeb/Pages/Login.jsp" class="style20">Back to Main Page</a></h5></td>
              </p>            </td>
            <td width="80%" class=style7>
              <h4 class="TitleColor">Instructions:</h4>
            	 <%
				String status = (String) session.getAttribute("status");

				if (status == null){
					out.println("\nInsert a username, password and a valid e-mail address, so you can register on BetAndUin!\n");
				}
				else{
			        out.println(status);
			    }
			    
				
				%>  
			</tr>
        </table>      </td>
    </tr>
  </table>
</form>
</body>
</html>
