<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants"%>
<%@ page language="java" %>


<%-- hierarchView highlight title page

     This should be displayed in the content part of the IntAct layout,
     it displays the highlightment sources title.

     author : Samuel Kerrien (skerrien@ebi.ac.uk)
 --%>

<%
   /**
    * Retreive user's data from the session
    */
   IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);
%>

<table border="0" cellspacing="3" cellpadding="3" width="100%" heigth="100%">

      <tr>
             <td>
                   <!-- displays the interaction network -->
                   Existing highlight source for the protein AC: <%= user.getAC() %>.
             </td>
      </tr>

</table>