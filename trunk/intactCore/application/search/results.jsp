 <%--
   /**
    * Search results page.
    *
    * @author Chris Lewington and Sugath Mudali
    * @version $Id$
    */
--%>

<%@ page language="java"%>

<%@ page import="uk.ac.ebi.intact.application.search.struts.framework.util.WebIntactConstants,
                 java.util.Map"%>
<%@ page import="uk.ac.ebi.intact.application.search.struts.view.IntactViewBean"%>

<%@ page import="javax.xml.transform.TransformerFactory"%>
<%@ page import="javax.xml.transform.stream.StreamResult"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>

<jsp:include page="header.jsp" flush="true" />

<h1>Search Results for
    <%=session.getAttribute(WebIntactConstants.SEARCH_CRITERIA) %></h1>
<!-- a line to separate the header -->
<hr size=2>

<html:form action="/view">

<!-- Get the view Bean and dump its data to the web page-->
<%
    //write to the JSP output stream
    StreamResult result = new StreamResult(out);

    //set up the transformer factory to share for each bean.
    TransformerFactory factory = TransformerFactory.newInstance();

    // The collection of beans to process.
    Map idToView = (Map) session.getAttribute(WebIntactConstants.FORWARD_MATCHES);

    // Process each bean.
    for (Iterator it = idToView.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry entry = (Map.Entry) it.next();
        IntactViewBean bean = (IntactViewBean) entry.getValue();
        String id = (String) entry.getKey();
        bean.transform(factory, id, result);
    }
%>
    <table cellpadding="1" cellspacing="0" border="1" width="100%">
        <tr>
            <td align="center">
                <input type="submit" Name="submit" value="Submit"/>
            </td>
        </tr>
    </table>
</html:form>

<jsp:include page="footer.jsp" flush="true" />

</html>