/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

/*
* This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*
* ---------------------------
* WebHitChart.java
* ---------------------------
* (C) Copyright 2002, by Richard Atkinson.
*
* Original Author:  Richard Atkinson;
*/


package uk.ac.ebi.intact.application.statisticView.business.graphic;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.StandardXYItemRenderer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Collection;
import java.awt.*;

import uk.ac.ebi.intact.util.IntactStatistics;
import uk.ac.ebi.intact.application.statisticView.business.Constants;
import uk.ac.ebi.intact.application.statisticView.business.data.StatisticsBean;
import uk.ac.ebi.intact.application.statisticView.business.data.NoDataException;

/**
 * Construction of the Chart out of a IntactStatistics collection.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public final class ChartBuilder {

    static final Logger logger = Logger.getLogger ( Constants.LOGGER_NAME );

    /**
     * Data to extract from the IntactStatistics
     */
    public static final int PROTEIN_DATA             = 0;
    public static final int ALL_INTERACTIONS_DATA    = 1;
    public static final int EXPERIMENT_DATA          = 2;
    public static final int TERM_DATA                = 3;
    public static final int BINARY_INTERACTIONS_DATA              = 4;


    private static void loadAllInteractionData ( final XYSeries dataSeries,
                                          final Iterator iterator ) {
        while ( iterator.hasNext () ) {
            final IntactStatistics intactStatistics = (IntactStatistics) iterator.next ();

            logger.info ( "Add: " + intactStatistics.getTimestamp ().getTime () + ", " +
                    intactStatistics.getNumberOfInteractions () );

            long start = System.currentTimeMillis();

            dataSeries.add ( intactStatistics.getTimestamp ().getTime (),
                    intactStatistics.getNumberOfInteractions () );

            long stop = System.currentTimeMillis();
            logger.info ("loadAllInteractionData: Time to add: " + (stop - start) + "ms");
        }
    }

    private static void loadExperimentData ( final XYSeries dataSeries,
                                             final Iterator iterator ) {
        while ( iterator.hasNext () ) {
            final IntactStatistics intactStatistics = (IntactStatistics) iterator.next ();
            long start = System.currentTimeMillis();

            logger.info ( "Add: " + intactStatistics.getTimestamp ().getTime () + ", " +
                    intactStatistics.getNumberOfExperiments () );

            dataSeries.add ( intactStatistics.getTimestamp ().getTime (),
                    intactStatistics.getNumberOfExperiments () );
            long stop = System.currentTimeMillis();
            logger.info ("loadExperimentData: Time to add: " + (stop - start) + "ms");
        }
    }

    private static void loadProteinxData ( final XYSeries dataSeries,
                                           final Iterator iterator ) {
        while ( iterator.hasNext () ) {
            final IntactStatistics intactStatistics = (IntactStatistics) iterator.next ();

            logger.info ( "Add: " + intactStatistics.getTimestamp().getTime () + ", " +
                                    intactStatistics.getNumberOfProteins() );
            long start = System.currentTimeMillis();

            dataSeries.add ( intactStatistics.getTimestamp().getTime(),
                             intactStatistics.getNumberOfProteins() );
            long stop = System.currentTimeMillis();
            logger.info ("loadProteinxData: Time to add: " + (stop - start) + "ms");
        }
    }

    private static void loadTermData ( final XYSeries dataSeries,
                                       final Iterator iterator ) {
        while ( iterator.hasNext () ) {
            final IntactStatistics intactStatistics = (IntactStatistics) iterator.next ();
            long start = System.currentTimeMillis();

            logger.info ( "Add: " + intactStatistics.getTimestamp ().getTime () + ", " +
                    intactStatistics.getNumberOfGoTerms () );

            dataSeries.add ( intactStatistics.getTimestamp ().getTime (),
                    intactStatistics.getNumberOfGoTerms () );

            long stop = System.currentTimeMillis();
            logger.info ("loadTermData: Time to add: " + (stop - start) + "ms");
        }
    }

    private static void loadBinaryData ( XYSeries dataSeries, Iterator iterator ) {

        while ( iterator.hasNext () ) {
            final IntactStatistics intactStatistics = (IntactStatistics) iterator.next ();
            long start = System.currentTimeMillis();

            logger.info ( "Add: " + intactStatistics.getTimestamp ().getTime () + ", " +
                    intactStatistics.getNumberOfBinaryInteractions () );

            dataSeries.add ( intactStatistics.getTimestamp ().getTime (),
                    intactStatistics.getNumberOfBinaryInteractions () );

            long stop = System.currentTimeMillis();
            logger.info ("loadBinaryData: Time to add: " + (stop - start) + "ms");
        }

    }

    public static String generateXYChart ( final StatisticsBean statisticsBean,
                                           final int type,
                                           final String title,
                                           final String timeTitle,
                                           final String countTitle,
                                           final HttpSession session,
                                           final PrintWriter pw ) {
        String filename;
        try {
            final Collection statistics = statisticsBean.getStatistics ();

            //  Throw a custom NoDataException if there is no data
            if ( statistics.size () == 0 ) {
                System.out.println ( "No data has been found" );
                throw new NoDataException ();
            }

            //  Create and populate an XYSeries Collection
            final XYSeries dataSeries = new XYSeries( null );
            final Iterator iter = statistics.iterator();
            switch ( type ) {
                case PROTEIN_DATA:
                    loadProteinxData( dataSeries, iter );
                    break;
                case ALL_INTERACTIONS_DATA:
                    loadAllInteractionData( dataSeries, iter );
                    break;
                case EXPERIMENT_DATA:
                    loadExperimentData( dataSeries, iter );
                    break;
                case TERM_DATA:
                    loadTermData( dataSeries, iter );
                    break;
                case BINARY_INTERACTIONS_DATA:
                    loadBinaryData( dataSeries, iter );
                    break;
                default :
                    logger.error( "The requested type of data (" + type + ") doesn't exists." );
                    return "public_error_500x300.png";
                    // error
            }

            final XYSeriesCollection xyDataset = new XYSeriesCollection ( dataSeries );

            /*
             * Kept for eventual later use.
             */
            //  Create tooltip and URL generators
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);
//			TimeSeriesToolTipGenerator ttg =
//                    new TimeSeriesToolTipGenerator ( sdf, NumberFormat.getInstance() );
//			TimeSeriesURLGenerator urlg =
//                    new TimeSeriesURLGenerator ( sdf, "pie_chart.jsp", "series", "hitDate" );

            //  Create the chart object
            final ValueAxis timeAxis = new DateAxis( timeTitle );
            final NumberAxis valueAxis = new NumberAxis( countTitle );
            valueAxis.setAutoRangeIncludesZero( false );  // override default
            final StandardXYItemRenderer renderer =
                    new StandardXYItemRenderer (
                            StandardXYItemRenderer.LINES + StandardXYItemRenderer.SHAPES,
                            null, // ToolTip
                            null ); // URLs
            renderer.setShapesFilled ( true );
            final XYPlot plot = new XYPlot( xyDataset, timeAxis, valueAxis, renderer );
            final JFreeChart chart = new JFreeChart( title, JFreeChart.DEFAULT_TITLE_FONT, plot, false );
            chart.setBackgroundPaint( new Color ( 237, 237, 237 ) );

            //  Write the chart image to the temporary directory
            final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection () );
            filename = ServletUtilities.saveChartAsPNG( chart, 500, 300, info, session );

            //  Write the image map to the PrintWriter
            ChartUtilities.writeImageMap( pw, filename, info );
            pw.flush ();

        } catch ( NoDataException e ) {
            System.out.println ( e.toString () );
            filename = "public_nodata_500x300.png";
        } catch ( Exception e ) {
            System.out.println( "Exception - " + e.toString () );
            e.printStackTrace( System.out );
            filename = "public_error_500x300.png";
        }
        logger.info( "filename: " + filename );
        return filename;
    }


}
