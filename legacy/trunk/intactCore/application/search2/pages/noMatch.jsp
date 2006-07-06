<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<%@ page import="uk.ac.ebi.intact.application.search2.struts.framework.util.SearchConstants,
                 uk.ac.ebi.intact.application.search2.struts.controller.SearchAction,
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
    String info = (String) session.getAttribute( SearchConstants.SEARCH_CRITERIA );
    Collection searchList = SearchAction.SEARCH_CLASSES;
    Iterator iterator = searchList.iterator ();
    StringBuffer sb =  new StringBuffer( (String) iterator.next() );
    while (  iterator.hasNext () ) {
        String classname = (String) iterator.next ();
        if (iterator.hasNext ()) {
           sb.append(", ");
        } else {
           sb.append(" or ");
        }
        sb.append( classname );
    }
%>

<h1>Search Results: No Matches!!</h1>
<!-- a line to separate the header -->
<hr size=2>

<h2>Sorry - could not find any <%= sb.toString()%>
  by trying to match <%= info.substring(info.indexOf('=') + 1) %> with: </h2>
  <ul>
      <li>AC,
      <li>short label,
      <li>xref Primary ID or
      <li>a full name.
  </ul>

  <h2>Please try again!</h2>

</html>