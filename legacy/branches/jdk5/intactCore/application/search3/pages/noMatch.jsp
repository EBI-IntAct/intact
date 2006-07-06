<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<%@ page import="uk.ac.ebi.intact.application.search3.struts.util.SearchConstants,
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

<!-- top line info -->
    <span class="middletext">Search Results: No Matches!  <br></span

<h3>Sorry - could not find any Interactor, Interaction, Experiment, or CvObject
  by trying to match  <font color="red"> <%= info.substring(info.indexOf('=') + 1) %> </font> with: </h3>

  <ul>
      <li>AC,
      <li>short label,
      <li>xref Primary ID or
      <li>a full name.
  </ul>

  <h3>Please try again!</h3>

</html>