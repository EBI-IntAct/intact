<%@ page import=
    "uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants"
 %>
 <%--
   /**
    * Prompts the user to enter a short label for the a new CV Object. A list
    * of existing short labels are displayed for selected topic in a table.
    *
    * @author Sugath Mudali (smudali@ebi.ac.uk)
    * @version $Id$
    */
--%>

<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html" %>

<!-- JSTL tag libraries-->
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<!-- To display of tables -->
<%@ taglib uri="http://jakarta.apache.org/taglibs/display" prefix="display" %>

<jsp:include page="header.jsp" flush="true" />

<html:errors/>

<html:form action="/addCv">
    Enter the short label for the new CV:
    <html:text property="shortLabel" size="16" />

    <!-- a line to separate the header -->
    <hr size=2>

    <html:submit property="action" value="Submit" />
    <html:reset/>
    <html:submit property="action" value="Cancel" />
</html:form>

<hr size=2>

Existing short label(s) for <b><c:out value="${IntactUser.selectedTopic}" /></b>:

<%
    // Aliases to save us from typing the full name.
    String labels = WebIntactConstants.SHORT_LABEL_BEAN;
%>

<display:table width="100%" name="<%=labels%>" pagesize="5">
    <display:setProperty name="basic.show.header" value="false"/>
    <display:column property="value0"/>
    <display:column property="value1"/>
    <display:column property="value2"/>
    <display:column property="value3"/>
    <display:column property="value4"/>
</display:table>

<jsp:include page="footer.jsp" flush="true" />
