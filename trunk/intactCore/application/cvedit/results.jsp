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

<!-- Display tag library -->
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<!-- JSTL tag libraries-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<!-- Our own tags to display CV topics -->
<%@ taglib uri="/WEB-INF/tld/intact.tld" prefix="intact" %>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<!-- Need to to save this in a page context for display library to access -->
<% pageContext.setAttribute("matchlist", intactuser.getCacheSearchResult()); %>

<jsp:include page="header.jsp" flush="true" />

Topic: <b><c:out value="${intactuser.selectedTopic}"/></b>
&nbsp;&nbsp;Query: <b><c:out value="${intactuser.searchQuery}"/></b>

<!-- a line to separate the header -->
<hr size=2>

<display:table width="100%" name="matchlist"
    decorator="uk.ac.ebi.intact.application.cvedit.struts.view.Wrapper">
    <display:column property="ac" title="AC"
        href="results.do" paramId="shortLabel" paramProperty="shortLabel" />
    <display:column property="shortLabel" title="Short Label" />
</display:table>

<jsp:include page="footer.jsp" flush="true" />

</html>