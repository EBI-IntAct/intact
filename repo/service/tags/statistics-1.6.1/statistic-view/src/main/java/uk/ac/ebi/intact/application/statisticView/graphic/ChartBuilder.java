/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.graphic;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.intact.application.statisticView.business.model.BioSourceStatistics;
import uk.ac.ebi.intact.application.statisticView.business.model.IdentificationMethodStatistics;
import uk.ac.ebi.intact.application.statisticView.business.model.IntactStatistics;
import uk.ac.ebi.intact.application.statisticView.business.util.IdentificationComparator;
import uk.ac.ebi.intact.core.IntactException;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Michael Kleen
 *         <p/>
 *         ChartBuilder creates the specific Intact Charts for use in the statisticView application. It provides 8 types
 *         of different chart. It uses the ChartFactory to create an intact Specific JfreeChart. The usage is: You are
 *         entering with a Collection of statistic Objects, and getting the specific Intact Chart back. *
 * @version ChartBuilder.java Date: Feb 17, 2005 Time: 5:54:40 PM
 */
public class ChartBuilder {
    // all titles of the different intact charts

    private static final String IDENTIFICATION_CHART_TITLE = "IntAct interactions by identification method";
    private static final String BIN_INTERACTION_CHART_TITLE = "IntAct binary interaction evidences";
    private static final String INTERACTION_CHART_TITLE = "IntAct interactions";
    private static final String CV_CHART_TITLE = "IntAct controlled vocabulary terms";
    private static final String EXPERIMENT_CHART_TITLE = "IntAct experiments and publications";
    private static final String PROTEIN_CHART_TITLE = "IntAct proteins";
    private static final String PER_ORGANISM_CHART_TITLE = "IntAct binary interactions/proteins per organism";
    private static final String EVIDENCE_PER_EXPERIMENT_CHART_TITLE = "IntAct experiment evidence";

    /**
     * Creates a time series graph over experiments based on  the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart Diagramm which represents a TimeSeries Graph over the Increase/Decrease of the  the period
     *         from the InputData
     * @throws IntactException if someIntactStatistics is null
     */
    public JFreeChart numberOfExperiments( final Collection someIntactStatistics ) throws IntactException {
        // first check if it is not null
        if ( someIntactStatistics == null ) {
            throw new IntactException( "DataSource of intact statistics must no be null" );
        }

        // create the dataseries
        final XYSeries experimentSeries = new XYSeries( "Experiments" );
        for ( Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext(); ) {
            final IntactStatistics anIntactStatistic = ( IntactStatistics ) iterator.next();
            experimentSeries.add( anIntactStatistic.getTimestamp().getTime(),
                                  anIntactStatistic.getNumberOfExperiments() );
        }

        final XYSeries publicationSeries = new XYSeries( "Publications" );
        for ( Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext(); ) {
            final IntactStatistics anIntactStatistic = ( IntactStatistics ) iterator.next();
            publicationSeries.add( anIntactStatistic.getTimestamp().getTime(),
                                   anIntactStatistic.getNumberOfPublications() );
        }
        //  create the chart
        final XYSeriesCollection allSeries = new XYSeriesCollection();
        allSeries.addSeries( experimentSeries );
        allSeries.addSeries( publicationSeries );

        final JFreeChart chart = ChartFactory.getXYChart( allSeries, EXPERIMENT_CHART_TITLE, "", "", true );

        chart.setAntiAlias( true );
        return chart;
    }

    /**
     * Creates a time series graph over proteins based on  the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of proteins over a specific
     *         period
     * @throws IntactException
     */
    public JFreeChart numberOfProteins( final Collection someIntactStatistics ) throws IntactException {
        // first check if it is not null
        if ( someIntactStatistics == null ) {
            throw new IntactException( "DataSource of intact statistics must no be null" );
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries( "" );
        for ( Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext(); ) {
            final IntactStatistics anIntactStatistic = ( IntactStatistics ) iterator.next();
            aDataSeries.add( anIntactStatistic.getTimestamp().getTime(),
                             anIntactStatistic.getNumberOfProteins() );
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection( aDataSeries );
        final JFreeChart chart = ChartFactory.getXYChart( aXYDataset, PROTEIN_CHART_TITLE, "", "" );
        chart.setAntiAlias( true );
        return chart;
    }

    /**
     * Creates a time series graph over experiments based on  the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of interactions over a specific
     *         period
     * @throws IntactException
     */
    public JFreeChart numberOfInteractions( final Collection someIntactStatistics ) throws IntactException {
        // first check if it is not null
        if ( someIntactStatistics == null ) {
            throw new IntactException( "DataSource of intact statistics must no be null" );
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries( "" );
        for ( Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext(); ) {
            final IntactStatistics anIntactStatistic = ( IntactStatistics ) iterator.next();
            aDataSeries.add( anIntactStatistic.getTimestamp().getTime(),
                             anIntactStatistic.getNumberOfInteractions() );
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection( aDataSeries );
        final JFreeChart chart = ChartFactory.getXYChart( aXYDataset, INTERACTION_CHART_TITLE, "", "" );
        chart.setAntiAlias( true );
        return chart;
    }

    /**
     * Creates a time series graph over binary interactions based on the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of binary interactions over a
     *         specific period
     * @throws IntactException
     */
    public JFreeChart numberOfBinaryInteractions( final Collection someIntactStatistics ) throws IntactException {
        // first check if it is not null
        if ( someIntactStatistics == null ) {
            throw new IntactException( "DataSource of intact statistics must no be null" );
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries( "" );
        for ( Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext(); ) {
            final IntactStatistics anIntactStatistic = ( IntactStatistics ) iterator.next();
            aDataSeries.add( anIntactStatistic.getTimestamp().getTime(),
                             anIntactStatistic.getNumberOfBinaryInteractions() );
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection( aDataSeries );
        final JFreeChart chart = ChartFactory.getXYChart( aXYDataset, BIN_INTERACTION_CHART_TITLE, "", "" );
        chart.setAntiAlias( true );
        return chart;
    }

    /**
     * Creates a time series graph over the increase of controlled vocabulary terms based on the given intactstatistic
     * objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of controlled vocabulary terms
     *         over a specific period
     * @throws IntactException
     */
    public JFreeChart numberOfCvTerms( final Collection someIntactStatistics ) throws IntactException {
        // first check if it is not null
        if ( someIntactStatistics == null ) {
            throw new IntactException( "DataSource of intact statistics must no be null" );
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries( "" );
        for ( Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext(); ) {
            final IntactStatistics anIntactStatistic = ( IntactStatistics ) iterator.next();
            aDataSeries.add( anIntactStatistic.getTimestamp().getTime(),
                             anIntactStatistic.getNumberOfCvTerms() );
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection( aDataSeries );
        final JFreeChart chart = ChartFactory.getXYChart( aXYDataset, CV_CHART_TITLE, "", "" );
        chart.setAntiAlias( true );
        return chart;
    }

    /**
     * List of taxid to show in the biosource stat.
     */
    private static List taxidFilter = new ArrayList();

    private static class Item implements Comparable {

        private String name;
        private Collection taxids = new HashSet();
        private long interactionCount = 0;
        private long proteinCount = 0;

        public Item( String name, String[] taxidArray ) {
            this.name = name;
            for ( int i = 0; i < taxidArray.length; i++ ) {
                String taxid = taxidArray[i];
                taxids.add( taxid );
            }
        }

        public String getName() {
            return name;
        }

        public long getInteractionCount() {
            return interactionCount;
        }

        public long getProteinCount() {
            return proteinCount;
        }

        public boolean containsTaxid( String taxid ) {
            return taxids.contains( taxid );
        }

        public void addInteractionCount( long interactionCount ) {
            this.interactionCount += interactionCount;
        }

        public void addProteinCount( long proteinCount ) {
            this.proteinCount += proteinCount;
        }

        public final int compareTo( Object o ) {
            //  final Timestamp t = ( (BioSourceStatistics) o ).getUpdated();

            Item _item = null;
            if ( o instanceof Item ) {
                _item = ( Item ) o;
            } else {
                throw new ClassCastException( o.getClass().getName() );
            }

            // sort the stats by decreasing interaction count
            return ( int ) ( _item.getInteractionCount() - getInteractionCount() );
        }
    }

    private static boolean listInitialized = false;

    static {
        // todo load that from a properties file.
        taxidFilter.add( new Item( "H. sapiens", new String[]{"9606"} ) );
        taxidFilter.add( new Item( "M. musculus", new String[]{"10090"} ) );
        taxidFilter.add( new Item( "C. elegans", new String[]{"6239"} ) );
        taxidFilter.add( new Item( "D. melanogaster", new String[]{"7227"} ) );
        taxidFilter.add( new Item( "A. thaliana", new String[]{"3702"} ) );
        taxidFilter.add( new Item( "S. cerevisiae", new String[]{"559292"} ) );
        taxidFilter.add( new Item( "S. pombe", new String[]{"284812"} ) );
        taxidFilter.add( new Item( "E. coli (K12)", new String[]{"83333"} ) );
        taxidFilter.add( new Item( "O. sativa (japonica)", new String[]{"39947"} ) );
    }

    /**
     * Creates a barchart graph that shows the count of binary interactions and proteins for a selected set of organism,
     * all others are summed up and shown under a category 'others'.
     *
     * @param someBioSourceStatistics a collection of BioSourceStatistics must not be null
     * @return a JfreeChart diagram which represents a barchart graph of the binary interactions/proteins of the
     *         different hosts based on the newt taxid
     * @throws IntactException if someBioSourceStatistics is null
     */
    public JFreeChart binaryInteractionsPerOrganism( final Collection someBioSourceStatistics ) throws IntactException {

        // first check if it is not null
        if ( someBioSourceStatistics == null ) {
            throw new IntactException( "DataSource of BioSourceStatistics must no be null" );
        }

        if ( false == listInitialized ) {
            // this will be done only once.

            Item others = new Item( "Others", new String[]{""} );

            for ( Iterator iterator = someBioSourceStatistics.iterator(); iterator.hasNext(); ) {

                BioSourceStatistics statistics = ( BioSourceStatistics ) iterator.next();

                boolean foundInTheList = false;
                for ( Iterator iterator1 = taxidFilter.iterator(); iterator1.hasNext() && !foundInTheList; ) {
                    Item item = ( Item ) iterator1.next();

                    if ( item.containsTaxid( statistics.getTaxId() ) ) {
                        item.addInteractionCount( statistics.getBinaryInteractions() );
                        item.addProteinCount( statistics.getProteinNumber() );

                        foundInTheList = true;
                    }
                }

                if ( !foundInTheList ) {
                    others.addInteractionCount( statistics.getBinaryInteractions() );
                    others.addProteinCount( statistics.getProteinNumber() );
                }
            }

            // sort the stats by descending interaction count.
            Collections.sort( taxidFilter );

            // displayed 'others' as the last item.
            taxidFilter.add( others );

            // we won't do that part a second time.
            listInitialized = true;
        }

        // create the dataseries
        final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        for ( Iterator iterator = taxidFilter.iterator(); iterator.hasNext(); ) {
            Item item = ( Item ) iterator.next();

            dataSet.addValue( item.getInteractionCount(), "Binary interaction evidences", item.getName() );
            dataSet.addValue( item.getProteinCount(), "Proteins", item.getName() );
        }

        // create the chart
        final JFreeChart chart = ChartFactory.getBarChart( dataSet, PER_ORGANISM_CHART_TITLE, "", "", true );
        chart.setAntiAlias( true );
        return chart;
    }


    public JFreeChart binaryInteractionsPerOrganism_OLD( final Collection someBioSourceStatistics ) throws IntactException {

        // first check if it is not null
        if ( someBioSourceStatistics == null ) {
            throw new IntactException( "DataSource of BioSourceStatistics must no be null" );
        }

        // create the dataseries
        final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        int proteinRestCount = 0;
        int binaryRestCount = 0;

        for ( Iterator iterator = someBioSourceStatistics.iterator(); iterator.hasNext(); ) {

            BioSourceStatistics statistics = ( BioSourceStatistics ) iterator.next();

            if ( taxidFilter.contains( statistics.getTaxId() ) ) {
                // we display that biosource
                dataSet.addValue( statistics.getBinaryInteractions(), "Binary Interactions", statistics.getShortlabel() );
                dataSet.addValue( statistics.getProteinNumber(), "Proteins", statistics.getShortlabel() );
            } else {
                // this goes in to the 'other' category
                proteinRestCount += statistics.getProteinNumber();
                binaryRestCount += statistics.getBinaryInteractions();
            }
        }

        // add the summ of the threshold as  "others"
        dataSet.addValue( binaryRestCount, "Binary Interactions", "others" );
        dataSet.addValue( proteinRestCount, "Proteins", "others" );

        // create the chart
        final JFreeChart chart = ChartFactory.getBarChart( dataSet, PER_ORGANISM_CHART_TITLE, "", "", true );
        chart.setAntiAlias( true );
        return chart;
    }

    /**
     * Creates a piechart graph which represents the interactions based on the identifications  based on  the given
     * intactstatistic objects.
     *
     * @param identificationStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of interactions over a specific
     *         period
     * @throws IntactException
     */
    public JFreeChart identificationMethods( final Collection<IdentificationMethodStatistics> identificationStatistics ) throws IntactException {
        if ( identificationStatistics == null ) {
            throw new IntactException( "DataSource of identificationStatistics must no be null" );
        }

        // Sort the data by decreasing count of interaction
        Collections.sort( ( List ) identificationStatistics, new IdentificationComparator() );
        final DefaultPieDataset dataSet = new DefaultPieDataset();

        // Create the dataset
        for ( IdentificationMethodStatistics method : identificationStatistics ) {
            dataSet.setValue( method.getDetectionName() + " (" + method.getNumberInteractions() + ")", method.getNumberInteractions() );
        }

        // Create the chart
        final JFreeChart chart = ChartFactory.getPieChart( dataSet, IDENTIFICATION_CHART_TITLE );
        chart.setAntiAlias( true );
        chart.setTextAntiAlias( true );

        // Set the default colors for the chart
        final PiePlot piePlot = ( PiePlot ) chart.getPlot();

        // problems with label in JFreeChart 1.0.7: the labels are drawn outside the chart (so we use 1.0.5 instead)
        // http://www.jfree.org/phpBB2/viewtopic.php?p=65865&sid=c9cb34ac517fcd03d10f9da6d7b5b1cf
        int i = 0;
        final int colorCount = chartColors.size();
        for ( IdentificationMethodStatistics method : identificationStatistics ) {
            piePlot.setSectionPaint( method.getDetectionName() + " (" + method.getNumberInteractions() + ")",
                                     chartColors.get( i ) );
            i++;
            if ( i == colorCount ) {
                // enforce rotation of colors
                i = 0;
            }
        }

        return chart;
    }

    static List<Paint> chartColors = new ArrayList<Paint>();

    static {
        chartColors.add( new Color( 238, 228, 83 ) );  // yellow
        chartColors.add( new Color( 204, 219, 237 ) ); // light blue
        chartColors.add( new Color( 214, 179, 214 ) ); // pink
        chartColors.add( new Color( 238, 143, 126 ) ); // redish
    }

    /**
     * Creates a time series graph over experiments based on the given intactstatistic objects.
     *
     * @param someEvidenceExperiments a collection of IntactStatistics must not be null
     *
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of interactions over a specific
     *         period
     *
     * @throws IntactException
     *
    public JFreeChart evidencePerExperiment( Collection someEvidenceExperiments ) throws IntactException {
    // first check if it is not null
    if ( someEvidenceExperiments == null ) {
    throw new IntactException( "DataSource of someEvidenceExperiments must no be null" );
    }
    // create the dataset
    final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
    for ( Iterator iterator = someEvidenceExperiments.iterator(); iterator.hasNext(); ) {
    ExperimentStatistics experimentStatistics = (ExperimentStatistics) iterator.next();
    if ( experimentStatistics.getBinaryInteractions() > Constants.MIN_BINARY_INTERACTIONS ) {
    double result = experimentStatistics.getBinaryInteractions();
    dataSet.addValue( result, "Number of Experiments", Integer.toString( experimentStatistics.getExperimentNumber() ) );
    }
    }
    // create the chart
    final JFreeChart chart = ChartFactory.getBarChart( dataSet, EVIDENCE_PER_EXPERIMENT_CHART_TITLE, "Experiments", "Binary interactions", false );
    chart.setAntiAlias( true );
    return chart;
    }    */
}
