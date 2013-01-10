<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%
String error=request.getParameter("error");
if(error==null || error=="null"){
 error="";
}
%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" rel="stylesheet" href="Reviewer.css">
    <title>iWrite - Login</title>    
 </head>
 <body>
  		<div class="loginDiv">
  			<form name="frmLogin" method="POST" action="<%=response.encodeURL(request.getContextPath() + "/j_security_check") %>">
			  	<table class="contentDeco">
			  		<tr>
			  			<td colspan="2">
			  				<center>
			  					<h2>iWrite - Login</h2>
			  				</center>
			  			</td>
			  		</tr>
			  		<%
			  			if(null != request.getParameter("error")){
			  		%>
			  			<tr>
			  				<td class="error" colspan="2">
  								Invalid User Name or Password, please try again.<br/>
							</td>
						</tr>
					<%
			  			}
					%>
			  		<tr>
			  			<td>
			  				<label>Username</label>
			  			</td>
			  			<td>
			  				<input type="text" name="j_username" tabindex="1" size="40"/>
			  			</td>
			  		</tr>
			  		<tr>
			  			<td>
			  				<label>Password</label>
			  			</td>
			  			<td>
			  				<input type="password" name="j_password" tabindex="2" size="40"/>
			  			</td>
			  		</tr>
			  		<tr>
			  			<td>
			  				
			  			</td>
			  			<td>
			  				<input type="submit" name="Log in" tabindex="3"/>
			  				<input type="reset" name="Reset" tabindex="4" />
			  			</td>
			  		</tr>
			  	</table>
		  	</form>
	  	</div>
  </body>
</html>
