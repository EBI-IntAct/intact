/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.struts.view;

import org.apache.log4j.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.servlet.ServletUtilities;
import uk.ac.ebi.intact.application.statisticView.business.data.StatisticHelper;
import uk.ac.ebi.intact.application.statisticView.business.util.Constants;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.File;

/**
 * User: Michael Kleen mkleen@ebi.ac.uk Date: Mar 22, 2005 Time: 4:24:29 PM
 */
public class ViewBeanFactory {

    private static final Log logger = LogFactory.getLog(ViewBeanFactory.class);

    private StatisticHelper helper;
    private String contextPath;

    public ViewBeanFactory( String contextPath ) {
        this.helper = new StatisticHelper();
        this.contextPath = contextPath;
    }


    public IntactStatisticsBean createViewBean( String start, String stop, HttpSession session ) throws IntactException,
                                                                                                        IOException {

        final JFreeChart cvChart = helper.getCvChart( start, stop );

        // building charts that support filtering
        final JFreeChart experimentChart = helper.getExperimentChart( start, stop );
        final JFreeChart interactionChart = helper.getInteractionChart( start, stop );
        final JFreeChart proteinChart = helper.getProteinChart( start, stop );
        final JFreeChart binaryChart = helper.getBinaryInteractionChart( start, stop );

        // build charts that don't support filtering
        final JFreeChart identificationChart = helper.getIdentificationChart();
        final JFreeChart bioSourceChart = helper.getBioSourceChart();

        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        IntactStatisticsBean intactBean = new IntactStatisticsBean( contextPath );
        intactBean.setCvTermChartUrl(writeChart(cvChart, 600, 400, info, session));
        intactBean.setExperimentChartUrl ( writeChart( experimentChart, 600, 400, info, session ) );
        intactBean.setInteractionChartUrl ( writeChart( interactionChart, 600, 400, info, session ) );
        intactBean.setProteinChartUrl ( writeChart( proteinChart, 600, 400, info, session ) );
        intactBean.setBinaryChartUrl ( writeChart( binaryChart, 600, 400, info, session ) );

        intactBean.setCvTermCount( helper.getCvCount() );
        intactBean.setExperimentCount( helper.getExperimentCount() );
        intactBean.setInteractionCount( helper.getInteractionCount() );
        intactBean.setProteinCount( helper.getProteinCount() );
        intactBean.setBinaryInteractionCount( helper.getBinaryInteractionCount() );
        intactBean.setBioSourceChartUrl ( writeChart( bioSourceChart, 600, 400, info, session ) );
        intactBean.setDetectionChartUrl ( writeChart( identificationChart, 600, 400, info, session ) );

        return intactBean;
    }

    private static String writeChart(JFreeChart chart, int length, int width, ChartRenderingInfo info, HttpSession session)
            throws IOException
    {
        ServletContext context = session.getServletContext();

        String chartFilename = session.getId()+"_"+chart.hashCode()+".png";

        String strTempDir = "/temp";
        String chartContextPath = strTempDir+"/"+chartFilename;

        File realChartfile = new File(context.getRealPath(chartContextPath));

        if (realChartfile.exists())
        {
            return chartContextPath;
        }

        ChartUtilities.saveChartAsPNG(realChartfile, chart, length, width, info);

        return chartContextPath;
    }
}