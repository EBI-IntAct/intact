<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
    This layout displays the search box to search the CV database and another
    input box to create a new Annotated object.
--%>

<%@ page language="java" %>
<%@ page import="uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants,
                 uk.ac.ebi.intact.application.editor.business.EditorService"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%
    // To Allow access to Editor Service.
    EditorService service = (EditorService)
            application.getAttribute(EditorConstants.EDITOR_SERVICE);
%>

<html:form action="/sidebar" focus="AC">
    <table>
        <tr>
            <td align="left">
                <html:select property="topic">
                    <html:options name="<%=EditorConstants.EDITOR_TOPICS%>"/>
                </html:select>
            </td>
        </tr>

        <tr>
            <td>
                <table>
                    <tr>
                        <td>
                            <html:submit tabindex="1" property="dispatch">
                                <bean:message key="button.search"/>
                            </html:submit>
                        </td>
                        <td><html:text property="searchString" size="12"/></td>
                        <td>
                            <%=service.getHelpLinkAsHTML("search")%>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <html:submit property="dispatch" onclick="return validate()">
                                <bean:message key="button.create"/>
                            </html:submit>
                        </td>
                        <td>
                            <%=service.getHelpLinkAsHTML("cv.editors")%>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</html:form>
