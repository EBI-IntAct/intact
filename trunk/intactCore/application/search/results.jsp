<%@ page import="uk.ac.ebi.intact.application.search.struts.framework.util.WebIntactConstants"%>
<%@ page import="uk.ac.ebi.intact.application.search.struts.view.*"%>

<%@ page import="javax.xml.transform.*"%>
<%@ page import="javax.xml.transform.stream.*"%>
<%@ page import="javax.xml.parsers.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>

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
    String filename =  session.getServletContext().getInitParameter(WebIntactConstants.XSL_FILE);
    String xslFile =  session.getServletContext().getRealPath(filename);
    StreamSource xml = null;
    StreamSource xsl = null;
    //write to the JSP output stream
    StreamResult result = new StreamResult(out);

    //set up the transformer
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer transformer = null;

    if(xslFile != null) {
        xsl = new StreamSource(new File(xslFile));
        transformer = factory.newTransformer(xsl);
    }
    //check to see if we have single or multiple results..
    boolean singleMatch = ((Boolean)session.getAttribute(WebIntactConstants.SINGLE_MATCH)).booleanValue();
    if(singleMatch) {

        if(viewbean.getAsXml() != null) {

            xml = new StreamSource(new StringReader(viewbean.getAsXml()));

            //now apply the XSL and send it to the output stream
            transformer.transform(xml, result);
        }
        else {

%>
            <%= viewbean.getData() %>
<%
        }

    }
    else {

        //multiple results - transform and display each bean in turn
        //NB assuming in this case the XML string has been set (just lazy...)
        Collection results = (Collection)session.getAttribute(WebIntactConstants.FORWARD_MATCHES);
        Iterator it = results.iterator();
        while(it.hasNext()) {

            IntactViewBean bean = (IntactViewBean)it.next();
            xml = new StreamSource(new StringReader(bean.getAsXml()));

            //apply the XSL and send it to the output stream
            transformer.transform(xml, result);
        }
    }
%>

<jsp:include page="footer.jsp" flush="true" />

</html>