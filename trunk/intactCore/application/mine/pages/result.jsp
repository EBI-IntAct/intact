<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - Forwards the result of MiNe to HierarchView
   -
   - @author Andreas Groscurth (groscurt@ebi.ac.uk)
-->
<%@ page language="java" %>
<%@ page import="uk.ac.ebi.intact.application.mine.business.IntactUserI,
				uk.ac.ebi.intact.application.mine.business.Constants" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%	IntactUserI user = (IntactUserI)session.getAttribute(Constants.USER);

	// the base link
	String baseLink = request.getContextPath();
	baseLink = baseLink.substring(0, baseLink.lastIndexOf("/")); 

	// all not connecting proteins are fetched
	String singletons = user.getSingletons().toString();
	singletons = singletons.substring(1, singletons.length() - 1); 
	
	// the link to HierarchView is set up
	StringBuffer link = new StringBuffer(baseLink);
	link.append("/hierarchView/display.jsp?AC=");
	link.append(user.getHVLink());
	link.append("&method=GO&depth=1");
   	// if singletons are given they are added to the link
	if(singletons.length() > 0) {
		link.append("&singletons=" + singletons);
	}
%>
<meta http-equiv="refresh" content="3; URL=<%= link.toString() %>">
<strong>The results are forwarded to HierarchView !<br>
If the forward is not working please click <a href="<%= link.toString() %>">here</a>
</strong>


