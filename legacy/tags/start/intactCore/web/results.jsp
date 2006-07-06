<%@ page import="uk.ac.ebi.intact.struts.framework.util.WebIntactConstants"%>
 <%--
   /**
    * Logon page. Prompts a user with with user name and password.
    *
    * @author Sugath Mudali (smudali@ebi.ac.uk)
    * @version $Id$
    */
--%>
<!doctype html public "-//w3c//dtd html 4.0 transitional//en">

<%@ page language="java" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<jsp:include page="header.jsp" flush="true" />

<h1>Search Results from WebIntact for
    <%=session.getAttribute(WebIntactConstants.SEARCH_CRITERIA) %></h1>
<!-- a line to separate the header -->
<hr size=2>

<display:table width="75%" name="match" decorator="uk.ac.ebi.intact.struts.view.Wrapper">
    <display:column property="ac" title="AC"
        href="results.do" paramId="shortLabel" paramProperty="shortLabel" />
    <display:column property="shortLabel" title="Short Label" />
</display:table>

<jsp:include page="footer.jsp" flush="true" />

</html>