<%--
   /**
    * Prompts the user for the confirmation to delete a CV Object. This JSP
    * can be easily replaced with a JavaScript.
    *
    * @author Sugath Mudali (smudali@ebi.ac.uk)
    * @version $Id$
    */
--%>

<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<!-- JSTL tag libraries-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<jsp:useBean id="intactuser" scope="session"
    class="uk.ac.ebi.intact.application.cvedit.business.IntactUserImpl"/>

<c:set var="viewbean" value="${intactuser.view}"/>

<jsp:include page="header.jsp" flush="true" />

<html:errors/>

<!-- a line to separate the header -->
<hr size=2>

AC: <b><c:out value="${viewbean.ac}" /></b>
&nbsp;&nbsp;Short Label: <c:out value="${viewbean.shortLabel}" />
<p>
Select <i>Submit</i> to continue with the deletion or <i>Cancel</i> to
return to the previous screen.
<p>

<html:form action="/delConfirm">
    <html:submit property="action" value="Submit" />
    <html:submit property="action" value="Cancel" />
</html:form>

<jsp:include page="footer.jsp" flush="true" />
