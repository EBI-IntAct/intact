<!doctype html public "-//w3c//dtd html 4.0 transitional//en">
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<!-- Our own tags to display intact types -->
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact" %>

<html>
	<head><title><bean:message key="main.heading"/></title>

<META HTTP-EQUIV="Expires" CONTENT="-1">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">

   <link rel=stylesheet href="./site.css" type="text/css">
	</head>
<!-- vlink="#85917C" link="#42593C"  -->
<body bgcolor="#F8F8F0" text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
    <td bgcolor="#ffffff" width="20%"><img src="intact-logo.jpg" border="0"></td>
		<td bgcolor="#ffffff" width="100">
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
      <td valign="center" align="center"></td>
      </tr>
      </table>
      </td>
		<td bgcolor="#ffeeaa" width="100%">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td width="100%"><font size="+2" color="#000000"><b> </b></font>
						<table width="100%" border="0" cellspacing="0" cellpadding="9">
							<tr>
								<td><span class="header">
						<bean:message key="main.heading"/>
						</span><br>
                        <html:link page="/welcome.jsp"><bean:message key="exit.label"/></html:link>
                   </font></td>
							</tr>
                            <tr>
                                <td bgcolor="#ffeeaa" width="100%">
                                <html:form action="/search" focus="AC">
                                    <table>
                                        <tr>
                                            <th align="left">
                                                <bean:message key="intact.types"/>
                                            </th>
                                            <td align="left">
                                                <intact:listIntactTypes resource="config/IntactTypes"/>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <html:submit property="action" value="Search"></html:submit>
                                            </td>
                                            <td><html:text property="searchString" size="10" maxlength="10"/></td>
                                        </tr>


                                    </table>
                                </html:form>
                                </td>
                            </tr>
						</table>
						</td>
               <td bgcolor="#ffeeaa" align="right" valign="bottom">
               <table border="0" cellspacing="7" cellpadding="0">
               <tr>
               <td align="right" valign="bottom" nowrap>Version: 0.2 (28/11/2002)</td>
               </tr>
               </table>
               </td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td bgcolor="#000000" width="100"><img src="./images/px.gif" border="0" width="1" height="1"></td>
		<td bgcolor="#000000" width="1"><img src="./images/px.gif" border="0" width="1" height="1"></td>
		<td bgcolor="#000000" width="100%"><img src="./images/px.gif" border="0" width="1" height="1"></td>
	</tr>

	<tr>
		<td colspan="3" align="left" valign="top">
      <table width="100%" cellspacing="0" cellpadding="0" border="0">
      <tr>
      <td width="1" bgcolor="#ffffff" valign="top" align="left">
         <img src="./images/px.gif" border="0" width="1" height="1">
		</td>
		<td bgcolor="#ffffff" width="1"><img src="./images/px.gif" border="0" width="1" height="1"></td>
        <td width="*" height="100%" valign="top">
			<table height="100%" border="0" cellspacing="0" cellpadding="15" >
				<tr>
					<td valign="top" width="100%" height="600" >

