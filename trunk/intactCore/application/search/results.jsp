<%@ page import="uk.ac.ebi.intact.application.search.struts.framework.util.WebIntactConstants"%>

<%@ page import="javax.xml.transform.*"%>
<%@ page import="javax.xml.transform.stream.*"%>
<%@ page import="javax.xml.parsers.*"%>
<%@ page import="java.io.*"%>

 <%--
   /**
    * Search results page.
    *
    * @author Chris Lewington
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

<%--
<display:table width="75%" name="viewbean"
    decorator="uk.ac.ebi.intact.application.search.struts.view.Wrapper">
    <display:column property="ac" title="AC"
        href="results.do" paramId="shortLabel" paramProperty="shortLabel" />
    <display:column property="shortLabel" title="Short Label" />
</display:table>

--%>
<%
    //Now get the XML source, the XSL source,
    //apply the stylesheet transformation and write the result out...
    StreamSource xml = new StreamSource(new StringReader(viewbean.getAsXml()));
    String filename =  session.getServletContext().getInitParameter(WebIntactConstants.XSL_FILE);
    String xslFile =  session.getServletContext().getRealPath(filename);
    StreamSource xsl = new StreamSource(new File(xslFile));

    //write to the JSP output stream
    StreamResult result = new StreamResult(out);

    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = factory.newTransformer(xsl);

    //now apply the XSL and send it to the output stream
    transformer.transform(xml, result);

    //get the results in XML format, or print as a String if not defined
    /*String xml = viewbean.getAsXml();
    if(xml != null) {
        out.println(xml);
    }
    else {
        out.println(viewbean.getData());
    }*/

%>

<jsp:include page="footer.jsp" flush="true" />

</html>