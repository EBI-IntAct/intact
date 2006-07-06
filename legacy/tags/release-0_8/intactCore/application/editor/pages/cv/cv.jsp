<!--
  - Author: Sugath Mudali (smudali@ebi.ac.uk)
  - Version: $Id$
  - Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
  - All rights reserved. Please see the file LICENSE in the root directory of
  - this distribution.
  -->

<%--
  - This page accepts changes to an Annotated object's short label and full name.
  --%>

<%@ page language="java"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>

<jsp:include page="../js.jsp" />

<html:form action="/cvDispatch">

    <!-- Displays error messages for the short label -->
<%--    <logic:messagesPresent>--%>
<%--        <table width="100%" border="0" cellspacing="1" cellpadding="2">--%>
<%--            <html:messages id="shortLabel">--%>
<%--                <tr class="tableRowEven">--%>
<%--                    <td class="tableErrorCell"><bean:write name="shortLabel"/></td>--%>
<%--                </tr>--%>
<%--            </html:messages>--%>
<%--        </table>--%>
<%--    </logic:messagesPresent>--%>

    <!-- Displays existing short labels. -->
<%--    <logic:messagesPresent message="true">--%>
<%--        <table width="100%" border="0" cellspacing="1" cellpadding="2">--%>
<%--            <html:messages id="message" message="true">--%>
<%--                <tr class="tableRowEven">--%>
<%--                    <td class="tableCell"><bean:write name="message" filter="false"/></td>--%>
<%--                </tr>--%>
<%--            </html:messages>--%>
<%--        </table>--%>
<%--    </logic:messagesPresent>--%>

    <jsp:include page="info.jsp" />
    <jsp:include page="annots.jsp" />
    <jsp:include page="../addAnnots.jsp" />
    <jsp:include page="xrefs.jsp" />
    <jsp:include page="../addXrefs.jsp" />

    <p></p>
    <jsp:include page="../action.jsp" />

</html:form>
