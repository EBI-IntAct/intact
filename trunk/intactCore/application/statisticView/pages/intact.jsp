<%@ page import="uk.ac.ebi.intact.application.statisticView.struts.view.IntactStatisticsBean,
                 java.util.ArrayList,
                 uk.ac.ebi.intact.application.statisticView.struts.view.DisplayStatisticsBean,
                 java.util.List"%>
<%@ page language="java"  %>
<%@ page buffer="none"    %>
<%@ page autoFlush="true" %>

<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/display.tld"     prefix="display" %>

<%@ taglib uri="/WEB-INF/tld/intact.tld"      prefix="intact" %>

<%
  IntactStatisticsBean intactBean = (IntactStatisticsBean) request.getAttribute("intactbean");
  List stats = intactBean.getDisplayBeans();
  request.setAttribute("statistics", stats);
%>

<table width="100%" height="100%">
    <tbody>
        <tr>
            <td valign="top" height="*">
            <!-- Displays the available highlightment source -->
                <display:table name="statistics" width="80%">
                    <display:column property="object" title="Object" width="22%" styleClass="tableCellAlignRight" /><display:column property="count"                 width="8%"  styleClass="tableCellAlignRight" />
                    <display:column property="description"           width="70%"  />
                    <display:setProperty name="basic.msg.empty_list" value="No statistics available..." />
                </display:table>
            </td>
        </tr>
        <tr>
            <td valign="top" height="*">
                <img src="<%= intactBean.getProteinChartUrl() %>" width=600 height=400 border=0 >
                <br\><br\>
                <img src="<%= intactBean.getInteractionChartUrl() %>" width=600 height=400 border=0  >
                <br\><br\>
                <img src="<%= intactBean.getBinaryChartUrl() %>" width=600 height=400 border=0 >
                <br\><br\>
                <img src="<%= intactBean.getExperimentChartUrl() %>" width=600 height=400 border=0>
                <br\><br\>
                <img src="<%= intactBean.getCvTermChartUrl() %>" width=600 height=400 border=0 >
                <br\><br\>
                <img src="<%=intactBean.getBioSourceChartUrl() %>" width=600 height=400 border=0 >
                <br\>
                <b>Note<b\>: This figure is static. It does not change when you select a specific time period.
                <br\><br\>
                <img src="<%= intactBean.getDetectionChartUrl()  %>" width=600 height=400 border=0 >
                <br\>
                <b>Note<b\>: This figure is static. It does not change when you select a specific time period.
                <br\><br\>
            </td>
        </tr>
    </tbody>
</table>













