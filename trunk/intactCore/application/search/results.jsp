<%@ page import="uk.ac.ebi.intact.application.search.struts.framework.util.WebIntactConstants"%>
 <%--
   /**
    * Search results page.
    *
    * @author Sugath Mudali (smudali@ebi.ac.uk)
    * @version $Id$
    */
--%>
<!doctype html public "-//w3c//dtd html 4.0 transitional//en">

<%@ page language="java" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean" %>

<jsp:include page="header.jsp" flush="true" />

<h1>Search Results for
    <%=session.getAttribute(WebIntactConstants.SEARCH_CRITERIA) %></h1>
<!-- a line to separate the header -->
<hr size=2>

<!-- just get the view Bean and dump its data to the web page-->
<jsp:useBean id="viewbean" scope="session" class="uk.ac.ebi.intact.application.search.struts.view.IntactViewBean" />

<%= viewbean.getData() %>
<%--
<display:table width="75%" name="viewbean"
    decorator="uk.ac.ebi.intact.application.search.struts.view.Wrapper">
    <display:column property="ac" title="AC"
        href="results.do" paramId="shortLabel" paramProperty="shortLabel" />
    <display:column property="shortLabel" title="Short Label" />
</display:table>

--%>


<jsp:include page="footer.jsp" flush="true" />

</html>