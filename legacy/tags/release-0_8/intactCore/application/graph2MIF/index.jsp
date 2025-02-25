<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   -
   -
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%
    // Parameters collection
    String ac        = request.getParameter("AC");
    if (ac == null) ac = "";

    String myDepth     = request.getParameter("depth");
    if (myDepth == null) myDepth = "1";
    int depth = 1;
    try {
        depth = Integer.parseInt( myDepth );
        if (depth < 1) depth = 1;
    } catch ( NumberFormatException e ) {
        depth = 1;
    }

    String strictMIF = request.getParameter("strict");
    if (strictMIF == null) strictMIF = "false";
    String trueSelected = "";
    String falseSelected = "";
    if (strictMIF.equalsIgnoreCase("true")) trueSelected = "selected";
    else falseSelected = "selected";

%>

<html>

<head>
  <title> Intact2psi </title>
</head>

<body bgcolor="white">

    <h2>
    <a href="http://www.ebi.ac.uk/intact" target="_blank">Intact</a> data extraction in <a href="http://psidev.sourceforge.net" target="_blank">PSI</a> format.
    </h2>

    <hr>

    <form action="<%= request.getContextPath() %>/getXML" method="GET">
        <table>
            <tr>
                <td align="right">
                    <b>IntAct accession number</b> :
                </td>
                <td>
                    <input type="text" name="ac" value="<%= ac %>" size="16">
                </td>
            </tr>
            <tr>
                <td align="right">
                    <b>Depth</b> :
                </td>
                <td>
                    <input type="text" name="depth" size="3" value="<%= depth %>" maxlength="2">
                </td>
            </tr>
            <tr>
                <td align="right">
                    <b>Strict MIF compliance</b> :
                </td>
                <td>
                    <select name="strict" size="2">
                       <option value="false" <%= falseSelected %> > false </option>
                       <option value="true"  <%= trueSelected  %> > true  </option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    <input type="submit" value="extract XML" title="Get XML in PSI format.">
                </td>
            </tr>
        </table>
    </form>

    <hr>

    <p>
      <a href="<%= request.getContextPath() %>/axis">See AxisServlet</a>
    </p>

</body>

</html>
