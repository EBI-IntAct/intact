<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld"  prefix="html"%>

<%-- hierarchView content page

     This should be displayed in the content part of the IntAct layout,
     it is organised as follow :
        - Left cells: displays the interaction network and title
        - Right cells: Display the highligh tool anf title

     author : Samuel Kerrien (skerrien@ebi.ac.uk)
 --%>

 <!-- Content of the hierarchView application -->

<table border="1" cellpadding="3" cellspacing="0" width="100%" heigth="100%">

      <tr>
             <td width="60%" valign="top">
                  <!-- Top Left cell: displays the interaction network title -->
                  <tiles:insert attribute="graphTitle"/>

             </td>

             <td width="40%" valign="top">
                   <!-- Top Right cell: Display the highligh tool title -->
                   <tiles:insert attribute="highlightTitle"/>

             </td>
      </tr>

      <tr>
             <td width="60%" valign="top">
                 <!-- Left cell: displays the interaction network -->
                 <tiles:insert attribute="graph"/>
             </td>

             <td width="40%" valign="top">
                 <!-- Right cell: Display the highligh tool -->
                 <tiles:insert attribute="highlight"/>
             </td>
      </tr>

</table>