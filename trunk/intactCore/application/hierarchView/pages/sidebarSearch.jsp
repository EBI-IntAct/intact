<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This layout displays the search box for hierarchView.
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ page import="java.util.ArrayList,
                 uk.ac.ebi.intact.application.hierarchView.struts.view.utils.OptionGenerator,
                 uk.ac.ebi.intact.application.hierarchView.business.IntactUserI,
                 uk.ac.ebi.intact.application.hierarchView.business.Constants,
                 uk.ac.ebi.intact.application.hierarchView.struts.view.utils.LabelValueBean"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%
    /**
     * Retreive user's data from the session
     */
    IntactUserI user = (IntactUserI) session.getAttribute (Constants.USER_KEY);

    if (user == null) {
        // no user in the session, don't display anything
        return;
    }

    String AC = user.getAC();
    if (AC == null) AC = "";
%>


<!-- Search section -->
<html:form action="/search" focus="AC">

    <table>
        <tr>
          <th>
             <div align="left">
                <strong><bean:message key="sidebar.search.section.title"/></strong>
             </div>
          </th>
        </tr>

        <tr>
            <td>
                   <html:text property="AC" value="<%= AC %>" size="12"/>
            </td>

        </tr>

		    <%
                String methodLabel    = user.getMethodLabel();
                String fieldMethod = (null == methodLabel ? "" : methodLabel);

                /**
                 * get a collection of highlightment sources.
                 */
                ArrayList sources = OptionGenerator.getHighlightmentSources ();

                if (sources.size() > 1) {
                    /**
                     * set the item collection and display it
                     */
                    pageContext.setAttribute("sources", sources, PageContext.PAGE_SCOPE);
            %>
                    <table border=0>
                      <tr>
                         <td align="right">
                           <h4> highlight sources </h4>
                         </td>
                      </tr>
                      <tr>
                         <td>
                            <html:select property="method" size="1" value="<%= fieldMethod %>">
                              <html:options collection="sources" property="value" labelProperty="label"/>
                            </html:select>
                         </td>
                      </tr>
                    </table>
            <%
                } else if (sources.size() == 1) {
                    LabelValueBean lvb = (LabelValueBean) sources.get(0);
                    String methodClassName = lvb.getValue();
            %>
                    <!-- dont't display the method if no choice available -->
                    <html:hidden property="method" value="<%= methodClassName %>"/>
            <%
                }
            %>

        <tr>
            <td>
                <html:submit titleKey="sidebar.search.button.submit.title">
                    <bean:message key="sidebar.search.button.submit"/>
                </html:submit>
            </td>
        </tr>
    </table>

</html:form>
