<%@page import="java.util.*" %>

<html>
<head>

<title>BetAndUin - Sign In</title>

<script language="JavaScript" type="text/javascript">
<!--
function breakout_of_frame()
{
  // see http://www.thesitewizard.com/archive/framebreak.shtml
  // for an explanation of this script and how to use it on your
  // own website
  if (top.location != location) {
    top.location.href = document.location.href ;
  }
}

</script>

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
<body onload="breakout_of_frame()" class="sub" >
<h1 align="center" class="style4" >&nbsp;</h1>
 <h1 align="center" class="style4" >BetAndUin
 </h1>
 <form action="http://localhost:8080/BetAndUinWeb/WebServer?type=login" method="post" name="form1" id="form1">
  <table width="100%" border="0" cellspacing="0" cellpadding="1" class="TitleColor">
    <tr style="vertical-align: top">
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="4">
          <tr class="headerTable">
            <td width="20%" style="vertical-align: top; border:solid">              <h3>Sign In</h3>            </td>
            <td width="80%" style="vertical-align: top; border:solid">              <h3>Sign In</h3>            </td>
          </tr>
          <tr style="vertical-align: top">
            <td width="20%" class="style8">
              <label for="username"><strong>User Name</strong></label>&nbsp;<br />
              <input id="username" name="username" type="text" size="25" />
              <br />
			  <br />
              <label for="password"><strong>Password</strong></label>&nbsp;<br />
              <input id="password" name="password" type="password" size="25" />
              <h5>
                <input type="submit" name="ButtonName" value="Sign In" />
                <a href="Register/Register.jsp" class="style20">Not Registed?</a></h5></td>
            <td width="80%" class=style7>
              <h4 class="TitleColor">Instructions:</h4>
              <%
				String status = (String) session.getAttribute("status");

				if (status == null){
					out.println("\nInsert your username and password. Register if you don't have an account yet!\n");
				}
				else{
			        out.println(status);
			    }
			    
				
				%>
              <br />		    </td>
          </tr>
        </table>      </td>
    </tr>
  </table>
</form>
</body>
</html>
