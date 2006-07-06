<%@ page import="uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants"%>
<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - hierarchView graph page
   - This should be displayed in the content part of the IntAct layout,
   - it displays the interaction network
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ taglib uri="/WEB-INF/tld/hierarchView.tld" prefix="hierarchView" %>

<table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">

      <tr>
             <td>
                   <jsp:include page="warning.jsp" />

                   <!--
                         Displays the interaction network if the picture
                         has been generated and stored in the session.
                     -->
                   <hierarchView:displayInteractionNetwork/>
             </td>
      </tr>

</table>