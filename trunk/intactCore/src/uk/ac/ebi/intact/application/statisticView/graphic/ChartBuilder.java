/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.graphic;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.intact.application.statisticView.business.model.BioSourceStatistics;
import uk.ac.ebi.intact.application.statisticView.business.model.ExperimentStatistics;
import uk.ac.ebi.intact.application.statisticView.business.model.IdentificationMethodStatistics;
import uk.ac.ebi.intact.application.statisticView.business.model.IntactStatistics;
import uk.ac.ebi.intact.application.statisticView.business.util.Constants;
import uk.ac.ebi.intact.application.statisticView.business.util.IdentificationComparator;
import uk.ac.ebi.intact.business.IntactException;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Michael Kleen
 *         <p/>
 *         ChartBuilder creates the specific Intact Charts for use in the statisticView application.
 *         It provides 8 types of different chart. It uses the ChartFactory to create an intact Specific
 *         JfreeChart. The usage is: You are entering with a Collection of statistic Objects, and getting
 *         the specific Intact Chart back.
 *         *
 * @version ChartBuilder.java Date: Feb 17, 2005 Time: 5:54:40 PM
 */
public class ChartBuilder {
    // all titles of the different intact charts
    
    private static final String IDENTIFICATION_CHART_TITLE = "IntAct interactions by identification method";
    private static final String BIN_INTERACTION_CHART_TITLE = "IntAct binary interactions";
    private static final String INTERACTION_CHART_TITLE = "IntAct interactions";
    private static final String CV_CHART_TITLE = "IntAct controlled vocabulary terms";
    private static final String EXPERIMENT_CHART_TITLE = "IntAct experiments";
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
    public JFreeChart numberOfExperiments(final Collection someIntactStatistics) throws IntactException {
        // first check if it is not null
        if (someIntactStatistics == null) {
            throw new IntactException("DataSource of intact statistics must no be null");
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries("");
        for (Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext();) {
            final IntactStatistics anIntactStatistic = (IntactStatistics) iterator.next();
            aDataSeries.add(anIntactStatistic.getTimestamp().getTime(),
                            anIntactStatistic.getNumberOfExperiments());
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection(aDataSeries);
        final JFreeChart proteinChart = ChartFactory.getXYChart(aXYDataset, EXPERIMENT_CHART_TITLE, "", "");
        return proteinChart;

    }

    /**
     * Creates a time series graph over proteins based on  the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of proteins over a specific period
     * @throws IntactException
     */
    public JFreeChart numberOfProteins(final Collection someIntactStatistics) throws IntactException {
        // first check if it is not null
        if (someIntactStatistics == null) {
            throw new IntactException("DataSource of intact statistics must no be null");
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries("");
        for (Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext();) {
            final IntactStatistics anIntactStatistic = (IntactStatistics) iterator.next();
            aDataSeries.add(anIntactStatistic.getTimestamp().getTime(),
                            anIntactStatistic.getNumberOfProteins());
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection(aDataSeries);
        final JFreeChart proteinChart = ChartFactory.getXYChart(aXYDataset, PROTEIN_CHART_TITLE, "", "");
        return proteinChart;

    }

    /**
     * Creates a time series graph over experiments based on  the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of interactions over a specific period
     * @throws IntactException
     */
    public JFreeChart numberOfInteractions(final Collection someIntactStatistics) throws IntactException {
        // first check if it is not null
        if (someIntactStatistics == null) {
            throw new IntactException("DataSource of intact statistics must no be null");
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries("");
        for (Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext();) {
            final IntactStatistics anIntactStatistic = (IntactStatistics) iterator.next();
            aDataSeries.add(anIntactStatistic.getTimestamp().getTime(),
                            anIntactStatistic.getNumberOfInteractions());
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection(aDataSeries);
        final JFreeChart proteinChart = ChartFactory.getXYChart(aXYDataset, INTERACTION_CHART_TITLE, "", "");
        return proteinChart;

    }

    /**
     * Creates a time series graph over binary interactions based on the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of binary
     *         interactions over a specific period
     * @throws IntactException
     */
    public JFreeChart numberOfBinaryInteractions(final Collection someIntactStatistics) throws IntactException {
        // first check if it is not null
        if (someIntactStatistics == null) {
            throw new IntactException("DataSource of intact statistics must no be null");
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries("");
        for (Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext();) {
            final IntactStatistics anIntactStatistic = (IntactStatistics) iterator.next();
            aDataSeries.add(anIntactStatistic.getTimestamp().getTime(),
                            anIntactStatistic.getNumberOfBinaryInteractions());
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection(aDataSeries);
        final JFreeChart proteinChart = ChartFactory.getXYChart(aXYDataset, BIN_INTERACTION_CHART_TITLE, "", "");
        return proteinChart;
    }

    /**
     * Creates a time series graph over the increase of controlled vocabulary terms based on the given
     * intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of controlled vocabulary
     *         terms over a specific period
     * @throws IntactException
     */
    public JFreeChart numberOfCvTerms(final Collection someIntactStatistics) throws IntactException {
        // first check if it is not null
        if (someIntactStatistics == null) {
            throw new IntactException("DataSource of intact statistics must no be null");
        }
        // create the dataseries
        final XYSeries aDataSeries = new XYSeries("");
        for (Iterator iterator = someIntactStatistics.iterator(); iterator.hasNext();) {
            final IntactStatistics anIntactStatistic = (IntactStatistics) iterator.next();
            aDataSeries.add(anIntactStatistic.getTimestamp().getTime(),
                            anIntactStatistic.getNumberOfCvTerms());
        }
        //  create the chart
        final XYDataset aXYDataset = new XYSeriesCollection(aDataSeries);
        final JFreeChart proteinChart = ChartFactory.getXYChart(aXYDataset, CV_CHART_TITLE, "", "");
        return proteinChart;
    }

    /**
     * Creates a barchart graph over the increase of controlled vocabulary terms based on the given
     * BioSourceStatistics objects.
     *
     * @param someBioSourceStatistics a collection of BioSourceStatistics must not be null
     * @return a JfreeChart diagramm which represents a barchart graph of the binary interactions/proteins  of the different
     *         hosts based on the newt tax id
     * @throws IntactException if someBioSourceStatistics is null
     */
    public JFreeChart binaryInteractionsPerOrganism(final Collection someBioSourceStatistics) throws IntactException {
        // first check if it is not null
        if (someBioSourceStatistics == null) {
            throw new IntactException("DataSource of BioSourceStatistics must no be null");
        }
        // create the dataseries
        final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        int proteinRestCount = 0;
        int binaryRestCount = 0;
        for (Iterator iterator = someBioSourceStatistics.iterator(); iterator.hasNext();) {
            BioSourceStatistics statistics = (BioSourceStatistics) iterator.next();
            // make a threshold, we don't want to display interactions which a lesser than a speficic limit
            if (statistics.getProteinNumber() != 0) {
                if (statistics.getProteinNumber() > Constants.MIN_DISPLAY_PROTEINS) {
                    dataSet.addValue(statistics.getBinaryInteractions(), "Binary Interactions", statistics.getShortlabel());
                    dataSet.addValue(statistics.getProteinNumber(), "Proteins", statistics.getShortlabel());
                }
                else {
                    // summ the threshold up
                    proteinRestCount += statistics.getProteinNumber();
                    binaryRestCount += statistics.getBinaryInteractions();
                }
            }
        }
        // add the summ of the threshold as  "others"
        dataSet.addValue(binaryRestCount, "Binary Interactions", "others");
        dataSet.addValue(proteinRestCount, "Proteins", "others");
        // create the chart
        final JFreeChart chart = ChartFactory.getBarChart(dataSet, PER_ORGANISM_CHART_TITLE, "", "", true);
        return chart;
    }

    /* Creates a piechart graph which represents the interactions based on the identifications  based on  the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of interactions over a specific period
     * @throws IntactException
     */
    public JFreeChart identificationMethods(final Collection identificationStatistics) throws IntactException {
        // first check if it is not null
        if (identificationStatistics == null) {
            throw new IntactException("DataSource of identificationStatistics must no be null");
        }
        // sort the data, to have a nicer chart
        Collections.sort((List) identificationStatistics, new IdentificationComparator());
        final DefaultPieDataset dataSet = new DefaultPieDataset();
        int count;
        int rest = 0;
        // create the dataset
        for (Iterator iterator = identificationStatistics.iterator(); iterator.hasNext();) {
            IdentificationMethodStatistics method = (IdentificationMethodStatistics) iterator.next();
            count = method.getNumberInteractions();
            // sort out the threshold which are unter the limit
            if (count > Constants.MIN_DETECTION_METHODS) {
                dataSet.setValue(method.getDetectionName(), method.getNumberInteractions());
            }
            else {
                // summ the threshold up
                rest = rest + count;
            }
        }
        // add the threshold as "other methods"
        dataSet.setValue("other methods", rest);
        // create the chart
        final JFreeChart chart = ChartFactory.getPieChart(dataSet, IDENTIFICATION_CHART_TITLE);
        return chart;

    }

    /* Creates a time series graph over experiments based on the given intactstatistic objects.
     *
     * @param someIntactStatistics a collection of IntactStatistics must not be null
     * @return a JfreeChart diagramm which represents a timeseries graph of the increase of interactions over a specific period
     * @throws IntactException
     */
    public JFreeChart evidencePerExperiment(Collection someEvidenceExperiments) throws IntactException {
        // first check if it is not null
        if (someEvidenceExperiments == null) {
            throw new IntactException("DataSource of someEvidenceExperiments must no be null");
        }
        // create the dataset
        final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        for (Iterator iterator = someEvidenceExperiments.iterator(); iterator.hasNext();) {
            ExperimentStatistics experimentStatistics = (ExperimentStatistics) iterator.next();
            if (experimentStatistics.getBinaryInteractions() > Constants.MIN_BINARY_INTERACTIONS) {
                double result = experimentStatistics.getBinaryInteractions();
                dataSet.addValue(result, "Number of Experiments", Integer.toString(experimentStatistics.getExperimentNumber()));
            }
        }
        // create the chart
        final JFreeChart chart = ChartFactory.getBarChart(dataSet, EVIDENCE_PER_EXPERIMENT_CHART_TITLE, "Experiments", "Binary interactions", false);
        return chart;
    }
}
