<%@ page language="java" %>

<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants" %>


<%-- hierarchView graph title page

     This should be displayed in the content part of the IntAct layout,
     it displays the interaction network title

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
                   Interaction network for AC: <%= user.getAC() %>.
             </td>
      </tr>

</table>