<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - Displays the Source list available for the current central protein
   - of the interaction network.
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ taglib uri="/WEB-INF/tld/hierarchView.tld" prefix="hierarchView" %>
<%@ taglib uri="/WEB-INF/tld/display.tld"      prefix="display" %>

<table border="0" cellspacing="3" cellpadding="3" width="100%">

      <tr>
             <td valign="top">
                   <%-- Prepare available highlightment source for the selected protein in the session --%>
                   <hierarchView:displaySource/>

                   <!-- Displays the available highlightment source -->
                   <display:table
                        name="sources" width="95%"
                        decorator="uk.ac.ebi.intact.application.hierarchView.struts.view.utils.SourceDecorator">
                           <display:column property="label" title="ID" width="25%"/>
                           <display:column property="description" width="75%" />

                           <display:setProperty name="basic.msg.empty_list" value="No source available for that protein" />
                   </display:table>

             </td>
      </tr>

</table>