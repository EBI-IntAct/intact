/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.struts.view;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.servlet.ServletUtilities;
import uk.ac.ebi.intact.application.statisticView.business.data.StatisticHelper;
import uk.ac.ebi.intact.application.statisticView.business.util.Constants;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * User: Michael Kleen mkleen@ebi.ac.uk Date: Mar 22, 2005 Time: 4:24:29 PM
 */
public class ViewBeanFactory {

    private static Logger logger = Logger.getLogger( Constants.LOGGER_NAME );

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
        intactBean.setCvTermChartName( ServletUtilities.saveChartAsPNG( cvChart, 600, 400, info, session ) );
        intactBean.setExperimentChartName( ServletUtilities.saveChartAsPNG( experimentChart, 600, 400, info, session ) );
        intactBean.setInteractionChartName( ServletUtilities.saveChartAsPNG( interactionChart, 600, 400, info, session ) );
        intactBean.setProteinChartName( ServletUtilities.saveChartAsPNG( proteinChart, 600, 400, info, session ) );
        intactBean.setBinaryChartName( ServletUtilities.saveChartAsPNG( binaryChart, 600, 400, info, session ) );

        intactBean.setCvTermCount( helper.getCvCount() );
        intactBean.setExperimentCount( helper.getExperimentCount() );
        intactBean.setInteractionCount( helper.getInteractionCount() );
        intactBean.setProteinCount( helper.getProteinCount() );
        intactBean.setBinaryInteractionCount( helper.getBinaryInteractionCount() );
        intactBean.setBioSourceChartName( ServletUtilities.saveChartAsPNG( bioSourceChart, 600, 400, info, session ) );
        intactBean.setDetectionChartName( ServletUtilities.saveChartAsPNG( identificationChart, 600, 400, info, session ) );

        return intactBean;
    }
}