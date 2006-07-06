<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<%@ page import="uk.ac.ebi.intact.application.hierarchView.highlightment.source.*" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.struts.Constants" %>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.struts.*"%>
<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader" %>

<%@page import="java.util.Collection" %>
<%@page import="java.util.Iterator" %>
<%@page import="java.util.Properties"%>

<html:html locale="true">
<head>

   <META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
   <META HTTP-EQUIV="Expires" CONTENT="-1">

   <title>
      <bean:message key="hierarchView.hierarchy.title"/>
   </title>

   <link rel="stylesheet" href="hierarchview.css" type="text/css">

   <html:base/>


   <script language="JavaScript">
    <!--
    /**
     * Allows to forward to a specified URL inside a frame
     */
    function forward ( absoluteUrl ) {
       parent.frameHierarchy.location.href = absoluteUrl;
    }
    //-->
   </script>



</head>

<body>


<% 
   String AC = (String) session.getAttribute (Constants.ATTRIBUTE_AC);
   String method_class = (String) session.getAttribute (Constants.ATTRIBUTE_METHOD_CLASS);

   Properties properties = PropertyLoader.load (Constants.PROPERTY_FILE);
   String debug = null;
   if (null != properties) {
      debug = properties.getProperty ("application.debug");
   } else {
      debug = "disable";
   }

   if (debug.equalsIgnoreCase ("enable")) {
      out.println("AC : " + AC + "<br>");
      out.println("method : " + method_class + " <br>");
   }

   if (null != AC) { 
      HighlightmentSource source = HighlightmentSource.getHighlightmentSource(method_class);

      if (null == source) {
         out.println("source is null! <br>");
      } else {
      
	  Collection urls = source.getUrl(AC); 

	  Iterator iterator = urls.iterator();
	  int size = urls.size();

    	  if (0 == size) {
	  
	    out.println ("no GO term for that protein (AC=" + AC + ")");
    
	  } else if (1 == size) {
	    LabelValueBean url = (LabelValueBean) iterator.next();
	    String adress = url.getValue();
	    String label = url.getLabel();

	    String absoluteUrl = "http://holbein:8080/interpro/DisplayGoTerm?context=GO:0000192&id=GO:0030447&format=simple";

	    /* redirection to this URL */
         %>

	    <script language="JavaScript">
	      <!--
		 forward ( '<%= absoluteUrl %>' );
	      //-->
	    </script>

          <%

	  } else {
	       out.println("Select an element in the list : <br>");

	       while (iterator.hasNext()) {
		   LabelValueBean url = (LabelValueBean) iterator.next();
		   String adress = url.getValue();
		   String label = url.getLabel();

		   out.println("<a href=" + adress +" target=\"frameHierarchy\">" + label + "</a> <br>");
	       } // while

	   } // else

       } // else

    } // if
%>

</body>
</html:html>
