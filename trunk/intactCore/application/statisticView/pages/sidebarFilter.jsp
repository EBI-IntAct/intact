<%@ page language="java" %>

<!--
   - Copyright (c) 2002 The European Bioinformatics Institute, and others.
   - All rights reserved. Please see the file LICENSE
   - in the root directory of this distribution.
   -
   - This layout displays graph management components for hierarchView.
   - According to the current state of the displayed graph : its depth,
   - we display button in order to get expanded or contracted.
   - We show only available options (e.g. if the depth can be desacrease
   - we don't show the contract button).
   -
   - @author Samuel Kerrien (skerrien@ebi.ac.uk)
   - @version $Id$
-->

<%@ page import="uk.ac.ebi.intact.util.StatisticsDataSet,
                 java.text.SimpleDateFormat,
                 java.util.GregorianCalendar,
                 java.util.Calendar,
                 java.util.ArrayList,
                 uk.ac.ebi.intact.application.statisticView.struts.view.FilterBean,
                 uk.ac.ebi.intact.application.statisticView.business.data.StatisticsBean,
                 uk.ac.ebi.intact.application.statisticView.business.Constants,
                 uk.ac.ebi.intact.application.statisticView.business.data.NoDataException,
                 uk.ac.ebi.intact.util.IntactStatistics,
                 java.sql.Timestamp,
                 java.sql.Date"%>

<%@ taglib uri="/WEB-INF/tld/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<%
    StatisticsBean bean = null;
    Date eldest = null;
    try {
        bean = StatisticsDataSet.getInstance( Constants.LOGGER_NAME ).getStatisticBean();
        IntactStatistics statistics = bean.getFirstRow();
        Timestamp timestamp = statistics.getTimestamp();
        eldest = new Date( timestamp.getTime() );
    } catch (NoDataException nde) {
        // forward to an error page.
    }



    /**
     * create the links
     */
    SimpleDateFormat dateFormater = StatisticsDataSet.dateFormater;
    Calendar calendar = new GregorianCalendar();
    ArrayList filters = new ArrayList();
    calendar.getTime();
    filters.add( new FilterBean( "-", "" ) );

    int i = 1;
    while ( calendar.getTime().after( eldest ) ) {
        calendar.roll( Calendar.MONTH, -1 ); // previous month
        String iMonthAgo = dateFormater.format( calendar.getTime() );
        filters.add( new FilterBean( "" + i, iMonthAgo ) );
        i++;
    }

    pageContext.setAttribute("filters", filters);
%>

    <table width="100%">
        <tr>
          <th colspan="2">
             <div align="left">
                <strong><bean:message key="sidebar.filter.section.title1"/></strong>
             </div>
          </th>
        </tr>

        <tr>
            <td>

            <html:form action="/filter">
               <table>
                  <tr>
                     <td>
                       Last
                       <html:select property="start">
                          <html:options collection="filters" property="value" labelProperty="label"/>
                       </html:select>
                       month.
                    </td>
                 </tr>
                 <tr>
                   <td align="right">

                       <html:submit property="action" titleKey="filter.button.submit.title">
                            <bean:message key="filter.button.submit.title"/>
                       </html:submit>
                 </td>
               </tr>
               </table>
            </html:form>

              <hr>

                <b><bean:message key="sidebar.filter.section.title2"/></b><br>

                   <%
                      String start = request.getParameter( "start" );
                      String stop = request.getParameter( "stop" );

                      String startValue = (start == null ? "" : start);
                      String stopValue = (stop == null ? "" : stop);
                   %>


               <html:form action="/filter">

               <table>
                  <tr>
                     <td align="right">
                        <bean:message key="filter.label.from"/>
                     </td>
                     <td>
                        <html:text property="start" value="<%= startValue %>" size="12" maxlength="11" />
                        <br>
                        <small><font color="#898989">(dd-mmm-yyyy)</font></small>
                     </td>
                  </tr>

                  <tr>
                    <td align="right">
                       <bean:message key="filter.label.to"/>
                    </td>
                    <td>
                       <html:text property="stop" value="<%= stopValue %>" size="12" maxlength="11" />
                       <br>
                       <small><font color="#898989">(dd-mmm-yyyy)</font></small>
                    </td>
                  </tr>
                  <tr>
                    <td colspan="2" align="right">
                       <html:submit property="action" titleKey="filter.button.submit.title">
                            <bean:message key="filter.button.submit.title"/>
                       </html:submit>
                    </td>
                  </tr>
                </table>
               </html:form>

            </td>
        </tr>
    </table>

<hr>