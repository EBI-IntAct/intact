<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<%@ page import="uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants,
                 uk.ac.ebi.intact.application.search3.struts.controller.SearchAction,
                 java.util.Collection,
                 java.util.Iterator"%>
 <%--
   /**
    * no matches page.
    *
    * @author Chris Lewington
    * @version $Id$
    */
--%>
<!doctype html public "-//w3c//dtd html 4.0 transitional//en">

<%
    // get the search query 
    String info = (String) session.getAttribute( SearchConstants.SEARCH_CRITERIA );

%>

<h2>Search Results: No Matches!!</h2>
<!-- a line to separate the header -->
<hr size=2>

<h3>Sorry - could not find any Protein, Interaction, Experiment, or CvObject
  by trying to match  <font color="red"> <%= info.substring(info.indexOf('=') + 1) %> </font> with: </h3>

  <ul>
      <li>AC,
      <li>short label,
      <li>xref Primary ID or
      <li>a full name.
  </ul>

  <h2>Please try again!</h2>

</html>