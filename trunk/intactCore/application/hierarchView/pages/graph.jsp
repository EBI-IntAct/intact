<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/tld/hierarchView.tld" prefix="hierarchView" %>

<%-- hierarchView graph page

     This should be displayed in the content part of the IntAct layout,
     it displays the interaction network

     author : Samuel Kerrien (skerrien@ebi.ac.uk)
 --%>

<table border="0" cellspacing="0" cellpadding="0" width="100%" heigth="100%">

      <tr>
             <td>
                   <!-- Displays the interaction network if the picture has
                        been generated and stored in the session.
                   -->
                   <hierarchView:displayInteractionNetwork/>

             </td>
      </tr>

</table>