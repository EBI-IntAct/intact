<%@ page import="uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants"%>
 <%--
   /**
    * no matches page.
    *
    * @author Chris Lewington
    * @version $Id$
    */
--%>
<!doctype html public "-//w3c//dtd html 4.0 transitional//en">

<%@ page language="java" %>

<jsp:include page="header.jsp" flush="true" />

<%
    String info = (String)session.getAttribute(SearchConstants.SEARCH_CRITERIA);
%>

<h1>Search Results: No Matches!!</h1>
<!-- a line to separate the header -->
<hr size=2>

<h2>Sorry - could not find any <%= session.getAttribute(SearchConstants.SEARCH_TYPE) %>
  by trying to match <%= info.substring(info.indexOf('=') + 1) %> with: </h2>
  <ul>
      <li>AC,
      <li>short label,
      <li>xref Primary ID or
      <li>a full name.
  </ul>

  <h2>Please try again!</h2>


<jsp:include page="footer.jsp" flush="true" />

</html>