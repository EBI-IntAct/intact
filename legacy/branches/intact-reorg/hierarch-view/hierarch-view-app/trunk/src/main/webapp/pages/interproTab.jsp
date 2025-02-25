<%@ page import="uk.ac.ebi.intact.application.hierarchView.struts.view.utils.SourceBean,
                 uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 java.util.ArrayList"%>
<%@ page language="java" %>

<%@ taglib uri="/WEB-INF/tld/display.tld" prefix="display" %>


<table border="0" cellspacing="0" cellpadding="3" width="100%">

      <tr>
                   <!-- Displays the available highlightment source -->

                   <%
                       ArrayList ListSources = (ArrayList) session.getAttribute("sources");
                       ArrayList tmpListSources = new ArrayList (ListSources.size());

                       int j=0;
                       for (int i=0;i<ListSources.size();i++) {
                           SourceBean b = (SourceBean) ListSources.get(i);
                           if ( b.getType().equals("Interpro") ) {
                               tmpListSources.add(j,ListSources.get(i));
                               j++;
                            }
                       }
                       session.setAttribute("tmpListSources",tmpListSources);
                   %>

             <td valign="top">

                    <!-- Interpro terms -->
                    <display:table
                        name="sessionScope.tmpListSources" width="100%" class="tsources"
                        decorator="uk.ac.ebi.intact.application.hierarchView.struts.view.utils.SourceDecorator">
                        <display:column property="description"        title="Description" width="63%" align="left" />
                        <display:column property="directHighlightUrl" title="Show"        width="8%"  align="center" />
                        <display:column property="count"              title="Count"       width="9%"  align="center" />
                        <display:column property="id"                 title="ID"          width="20%" align="left" />
                        <display:setProperty name="basic.msg.empty_list"
                                                value="No source available for that interaction network" />
                   </display:table>

             </td>
      </tr>
</table>