/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.plugins.dbupdate.experiments;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.ExperimentShortlabelGenerator;
import uk.ac.ebi.intact.util.SearchReplace;
import uk.ac.ebi.intact.util.cdb.IntactCitation;
import uk.ac.ebi.intact.util.cdb.IntactCitationFactory;
import uk.ac.ebi.intact.util.cdb.PubmedIdChecker;
import uk.ac.ebi.intact.util.cdb.UpdateExperimentAnnotationsFromPudmed;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Updates all Experiments found in the database. <br> it updates: <li> <ul> shortlabel </ul> <ul> fullname </ul> <ul>
 * Annotation( contact-email ) </ul> <ul> Annotation( author-list ) </ul> </li>
 * <p/>
 * <br> it also generated a report on what has been done during the update process.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>14-Jul-2005</pre>
 */
public class UpdateExperiments {

    ////////////////////////
    // Constants
    public static final String PUBMED_ID_FLAG = "${PUBMED}";
    public static final String CITEXPLORE_URL = "http://www.ebi.ac.uk/citations/citationDetails.do?externalId=" + PUBMED_ID_FLAG + "&dataSource=MED";

    public static final String NEW_LINE = System.getProperty( "line.separator" );

    private static ExperimentShortlabelGenerator suffixGenerator = new ExperimentShortlabelGenerator();

    ////////////////////////
    // Private methods

    /**
     * Retreive a pubmed ID from an IntAct experience. <br> That information should be found in Xref( CvDatabase( pubmed
     * ), CvXrefQualifier( primary-reference ) ).
     *
     * @param experiment the experiment from which we try to retreive the pubmedId.
     *
     * @return the pubmedId as a String or null if none were found.
     */
    private static String getPubmedId( Experiment experiment ) {

        if ( experiment == null ) {
            return null;
        }

        // TODO use the helper to get the real CV term instead of comparing shortlabel.

        String pubmedId = null;
        for ( Iterator iterator = experiment.getXrefs().iterator(); iterator.hasNext() && pubmedId == null; ) {
            Xref xref = (Xref) iterator.next();

            if ( CvDatabase.PUBMED.equals( xref.getCvDatabase().getShortLabel() ) ) {

                if ( CvXrefQualifier.PRIMARY_REFERENCE.equals( xref.getCvXrefQualifier().getShortLabel() ) ) {

                    pubmedId = xref.getPrimaryId();
                }
            }
        }

        return pubmedId;
    }

    private static String generateCitexploreUrl( String pubmedId ) {
        return SearchReplace.replace( CITEXPLORE_URL, PUBMED_ID_FLAG, pubmedId );
    }

    ////////////////////////
    // Public methods

    /**
     * Update the given experiment and generate a report for it.
     *
     * @param experiment      the experiemnt to update
     */
    public static UpdateSingleExperimentReport updateExperiment(  Experiment experiment, PrintStream printStream, boolean dryRun ) {
        String dryRunMode = (dryRun)? " (DRY RUN MODE)" : "";

        UpdateSingleExperimentReport report = new UpdateSingleExperimentReport(experiment.getAc(), experiment.getShortLabel());

        printStream.println( "=======================================================================================" );
        printStream.println( "Updating experiment"+dryRunMode+": " + experiment.getAc() + " " + experiment.getShortLabel() );

        if (isCuratedComplex(experiment)) {
            printStream.println(" [SKIP] This is a curated complex.");
            report.setInvalidMessage(experiment.getShortLabel() + " is a curated complex.");
            return report;
        }

        // find experiment pubmed id
        String pubmedId = getPubmedId( experiment );

        if ( pubmedId == null ) {
            final String message = experiment.getShortLabel() + " doesn't have a primary-reference pubmed id.";
            printStream.println(message);
            report.setInvalidMessage(message);
            
            return report;
        }

        if (!PubmedIdChecker.isPubmedId(pubmedId)) {
            final String message = experiment.getShortLabel() + " doesn't have a valid pubmed id: " + pubmedId;
            printStream.println(message);
            report.setInvalidMessage(message);

            return report;
        }

        try {

            IntactCitation citation = IntactCitationFactory.getInstance().buildCitation( pubmedId );

            // get the year of publication
            int year = citation.getYear();

            // get the first author last name
            String authorLastName = null;
            if ( false == citation.hasAuthorLastName() ) {
                throw new Exception( experiment.getShortLabel() + ", " + pubmedId + ": Could not find an author name." );
            } else {
                authorLastName = citation.getAuthorLastName();
            }

            // generate a suffix based upon the author name, the year and the pubmed ID
            String suffix = suffixGenerator.getSuffix( authorLastName, year, pubmedId );

            // Build the shortlabel
            // Here we don't care (yet) about the suffixes ... but keeping a list of all already generated
            // shortlabel in the scope of the experimentList should allow us to generate it easily.
            String experimentShortlabel = authorLastName + "-" + year + suffix;

            String current = experiment.getShortLabel();

            // check if the intact experiment matches the shortlabel prefix (author-year[suffix])
            if ( ! current.startsWith( experimentShortlabel ) ) {
                String warnMessage = "WARNING - the current shortlabel is " + current +
                                    " though we were expecting it to start with " + experimentShortlabel;
                printStream.println( warnMessage );
                report.addWarningMessage(warnMessage);
            }

            //////////////////////////////
            // update the experiment

            boolean updated = false;
            if ( ! experiment.getShortLabel().equals( experimentShortlabel ) ) {
                String oldLabel = experiment.getShortLabel();
                experiment.setShortLabel( experimentShortlabel );
                printStream.println( "shortlabel updated." );
                updated = true;
                report.setShortLabelValue(new UpdatedValue(oldLabel, experimentShortlabel));
            }

            String title = citation.getTitle();

            if ( ! title.equals( experiment.getFullName() ) ) {
                String oldFulName = experiment.getFullName();
                experiment.setFullName( title );
                printStream.println( "Fullname updated" );
                updated = true;
                report.setFullNameValue(new UpdatedValue(oldFulName, title));
            }

            UpdateExperimentAnnotationsFromPudmed.UpdateReport updateReport = UpdateExperimentAnnotationsFromPudmed.update( experiment, pubmedId, dryRun );
            report.setUpdateReport(updateReport);

            printReport( updateReport, printStream );

            ////////////////////////////////
            // Write report.
            printStream.println( StringUtils.rightPad( experiment.getAc(), 15 ) +
                                StringUtils.rightPad( current + " / " + experimentShortlabel, 50 ) +
                                pubmedId + "   " + generateCitexploreUrl( pubmedId ) );


        } catch ( Exception e ) {

            printStream.println( "An exception was thrown during the update process of:" );
            printStream.println( StringUtils.rightPad( experiment.getAc(), 15 ) +
                                StringUtils.rightPad( experiment.getShortLabel(), 23 ) +
                                pubmedId + "   " + generateCitexploreUrl( pubmedId ) );

            // display exception and causes (if any)
            Throwable t = (Throwable) e;
            while ( t != null ) {

                report.setInvalidMessage("Exception updating: "+t.getMessage());
                t.printStackTrace(printStream);

                t = t.getCause();
                if ( t != null ) {
                    printStream.println( "============================ CAUSED BY  ========================" );
                }
            }
        }

        return report;
    }

    private static boolean isCuratedComplex(Experiment experiment) {
        for (Annotation annotation : experiment.getAnnotations()) {
            final CvTopic cvTopic = annotation.getCvTopic();

            if (cvTopic == null) {
                continue;
            }

            if (CvTopic.CURATED_COMPLEX.equals(cvTopic.getShortLabel())) {
                return true;
            }
        }

        return false;
    }

    public static List<UpdateSingleExperimentReport> startUpdate(PrintStream printStream, boolean dryRun) throws SQLException
    {
        return startUpdate(printStream, "%", dryRun);
    }

    public static List<UpdateSingleExperimentReport> startUpdate(PrintStream printStream, String expLabelPattern, boolean dryRun)
    {
        final DataContext dataContext = IntactContext.getCurrentInstance().getDataContext();

        if (dataContext.isTransactionActive()) {
            throw new IntactException("To start the update the transaction must NOT be active. Commit any existing transaction first");
        }

        dataContext.beginTransaction();

        Query query = dataContext.getDaoFactory().getExperimentDao().getSession()
                .createQuery("select exp.ac from Experiment exp where exp.shortLabel like :label order by exp.created");
        query.setString("label", expLabelPattern);

        List<String> experimentAcs = query.list();

        try {
            dataContext.commitTransaction();
        } catch (IntactTransactionException e) {
            throw new IntactException(e);
        }

        List<UpdateSingleExperimentReport> reports = new LinkedList<UpdateSingleExperimentReport>();

        for (String ac : experimentAcs) {
            dataContext.beginTransaction();

            // get the experiment
            Experiment experiment = dataContext.getDaoFactory().getExperimentDao().getByAc(ac);

            UpdateSingleExperimentReport report = updateExperiment(experiment, printStream, dryRun);

            try {
                dataContext.commitTransaction();
            } catch (IntactTransactionException e) {
                throw new IntactException(e);
            }

            reports.add(report);
        }

        return reports;
    }

    /**
     * Prints update report to System.out.
     *
     * @param report
     */
    private static void printReport( UpdateExperimentAnnotationsFromPudmed.UpdateReport report, PrintStream printStream ) {
        if ( report.isAuthorListUpdated() ) {
            printStream.println( "author list updated" );
        }

        if ( report.isAuthorEmailUpdated() ) {
            printStream.println( "author email updated" );
        }

        if ( report.isContactUpdated() ) {
            printStream.println( "contact updated" );
        }

        if ( report.isJournalUpdated() ) {
            printStream.println( "journal updated" );
        }

        if ( report.isYearUpdated() ) {
            printStream.println( "year of publication updated" );
        }
    }

}