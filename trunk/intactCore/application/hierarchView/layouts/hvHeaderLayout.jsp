<%@ taglib uri="/WEB-INF/tld/struts-tiles.tld" prefix="tiles"%>
<%@ taglib uri="/WEB-INF/tld/intact.tld"      prefix="intact"%>

<%--
    HierarchView header
    Author: Samuel Kerrien (skerrien@ebi.ac.uk)
    Version: $Id$
--%>

        <table width="100%" height="100%">

            <%-- Application Header section --%>
            <tr>
                <td valign="top" height="5%">
                    <span class="header"><tiles:getAsString name="header.title"/></span>
                    <intact:documentation section="hierarchView" />
                </td>
            </tr>

            <tr>
                <td height="1" background="<%=request.getContextPath()%>/images/hor.gif"></td>
            </tr>

        </table>