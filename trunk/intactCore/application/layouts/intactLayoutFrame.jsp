<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld"  prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld"       prefix="intact"%>

<%--
    Intact default look & feel layout with frames.
    It consists of a sidebar and a display area.
    The display area conatns the header, contents and the footer as shown below:
    +-----------------+
    | side | header   +
    | bar  | contents +
    |      | footer   +
    |-----------------+

    Author: Samuel Kerrien (skerrien@ebi.ac.uk)
    Version: $Id$
--%>

<html:html>

<head>
    <title><tiles:getAsString name="title"/></title>
    <html:base target="_top"/>
    <link rel="stylesheet" type="text/css" href="styles/intact.css"/>

</head>

<frameset cols="10%,*" border=0>

   <frame src="<tiles:getAsString name="sidebar"/>" name="sidebarFrame">

   <frameset ROWS="8%,*, 8%">
      <frame src="<tiles:getAsString name="header"/>"  name="headerFrame">
      <frame src="<tiles:getAsString name="content"/>" name="contentFrame">
      <!--frame src="/pages/error.jsp" name="contentFrame"-->
      <frame src="footerFrame.jsp"  name="footerFrame">
   </frameset>

   <noframes>
   Your browser doesn't support frames.
   </noframes>

</frameset>

<body bgcolor="#FFFFFF" topmargin="0" leftmargin="0">

<%--<%--%>
<%--    // in case some arrors are discovered, let's save them in the session for later--%>
<%--    // displaying. The page in charge of the displaying will have to check it any--%>
<%--    // errors are stored and need to be displayed--%>
<%--    ActionErrors errors = (ActionErrors) pageContext.findAttribute(Globals.ERROR_KEY);--%>
<%----%>
<%--    if ( null != errors ) {--%>
<%--        session.setAttribute("MY_ERRORS", errors);--%>
<%--    }--%>
<%--%>--%>

<intact:saveErrors/>

</body>
</html:html>
