<%@ page language="java" %>

<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants"%>

<%@ taglib uri="/WEB-INF/tld/hierarchView.tld" prefix="hierarchView" %>
<%@ taglib uri="/WEB-INF/tld/display.tld" prefix="display" %>

<%-- hierarchView highlight page

     This should be displayed in the content part of the IntAct layout,
     it displays the highlightment sources.

     author : Samuel Kerrien (skerrien@ebi.ac.uk)
 --%>

<%
   /**
    * Retreive user's data from the session
    */
   IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
%>

<table border="0" cellspacing="5" cellpadding="5" width="100%" heigth="100%">

      <tr>
             <td valign="top">
                   <!-- Displays available highlightment source for the selected protein -->
                   <hierarchView:displaySource/>

                   <display:table
                        name="sources" width="100%"
                        decorator="uk.ac.ebi.intact.application.hierarchView.struts.view.SourceDecorator">
                           <display:column property="label" title="ID" />
                           <display:column property="description" />

                           <display:setProperty name="basic.msg.empty_list" value="No source available for that protein" />
                   </display:table>
<%--
                   <br><br>Fake link to try highlight:<br>
                   <a href="http://holbein:8080/hierarchView/source.do?keys=GO:0005829"> GO:0005829 </a>

                   <br><br>Fake link get HTTP content:<br>
                   <a href="http://holbein:8080/hierarchView/displaySourceContent.do?url=http://www.google.com"> Google </a>

                   <br><br>Fake malformed link get HTTP content:<br>
                   <a href="http://holbein:8080/hierarchView/displaySourceContent.do?url=htp:/www.google.com"> Google </a>
  --%>
             </td>
      </tr>


      <tr>
             <td valign="top">
                   <!-- Displays Http content if URL updated in the session -->
                   <%
                        if (user.hasSourceUrlToDisplay()) {
                   %>
                            <hr>
                            <hierarchView:displayHttpContent/>
                   <%
                        }
                   %>
             </td>
      </tr>


</table>