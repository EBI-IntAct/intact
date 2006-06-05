<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - success.jsp calculates and displays the result image
   -
   - @author Markus Brosch (markus @ brosch.cc)
   - @version $Id$
-->

<%@ page import="uk.ac.ebi.intact.application.goDensity.business.image.DensityImage,
                 uk.ac.ebi.intact.application.goDensity.business.binaryInteractions.GoGoDensity,
                 org.apache.log4j.Logger"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%
    String applicationPath = request.getContextPath();
    long time = System.currentTimeMillis();

    String radio = (String)session.getAttribute("radio"); // bw & color
    GoGoDensity[][] dens = (GoGoDensity[][]) session.getAttribute("gogodensity");
    DensityImage image = null;

    //if (session.getAttribute("radio") != null && session.getAttribute("gogodensity") == null)
    //    return;  // should never happen, as ColorAction forward to imput as long no "gogodensity" avilables

    if (dens == null || radio == null) {
        out.println("No session attribute. ");
        out.println("If you entered a valid GO:ID the term is not available for this application!");
    } else {
        if (radio.equals("color"))
            image = new DensityImage(dens, true);
        if (radio.equals("bw"))
            image = new DensityImage(dens, false);
    }

%>

<html:html>

    <head>
        <title>goDensity - result </title>
        <script LANGUAGE="JavaScript" TYPE="text/javascript" SRC="pages/infobox.js"></script>
        <script LANGUAGE="JavaScript" TYPE="text/javascript">
            /* PopupImage script from Peter Todorov at http://www.sitepoint.com/article/1022 */
            function PopupPic(sPicURL) {
                window.open( "pages/popup.html?"+sPicURL, "","resizable=1,HEIGHT=200,WIDTH=200");
            }
        </script>
    </head>

    <body bgcolor="#FFFFFF">
        <div ID="infodiv" STYLE="position:absolute; visibility:hidden; z-index:20; top:0px; left:0px;"></div>
        <% if (image != null) {
            out.println(image.getImageMap(applicationPath));
            session.setAttribute("bufferedImage", image.getBufferedImage());
        %>
        <br>
        <div align="center">
            <html:errors/>
            <img src="<%= applicationPath %>/densityServlet?time=<%=time%>" ISMAP USEMAP="#go">
        </div>
        <% } %>
    </body>

</html:html>