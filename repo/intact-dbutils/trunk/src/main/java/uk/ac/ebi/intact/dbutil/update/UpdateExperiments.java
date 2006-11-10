/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.dbutil.update;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.util.HttpProxyManager;
import uk.ac.ebi.intact.util.SearchReplace;
import uk.ac.ebi.intact.util.cdb.ExperimentShortlabelGenerator;
import uk.ac.ebi.intact.util.cdb.IntactCitation;
import uk.ac.ebi.intact.util.cdb.IntactCitationFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
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
    private static final Log log = LogFactory.getLog(UpdateExperiments.class);

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
    public static void updateExperiment(  Experiment experiment, boolean dryRun ) {

        String dryRunMode = (dryRun)? " (DRY RUN MODE)" : "";

        log.debug( "=======================================================================================" );
        log.debug( "Updating experiment"+dryRunMode+": " + experiment.getAc() + " " + experiment.getShortLabel() );

        // find experiment pubmed id
        String pubmedId = getPubmedId( experiment );

        if ( pubmedId == null ) {
            log.error( experiment.getShortLabel() + " doesn't have a primary-reference pubmed id." );
            return;
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
                log.debug( "WARNING - the current shortlabel is " + current +
                                    " though we were expecting it to start with " + experimentShortlabel );
            }

            //////////////////////////////
            // update the experiment

            boolean updated = false;
            if ( ! experiment.getShortLabel().equals( experimentShortlabel ) ) {
                experiment.setShortLabel( experimentShortlabel );
                log.debug( "shortlabel updated." );
                updated = true;
            }

            String title = citation.getTitle();

            if ( ! title.equals( experiment.getFullName() ) ) {
                experiment.setFullName( title );
                log.debug( "Fullname updated" );
                updated = true;
            }

            printReport( UpdateExperimentAnnotationsFromPudmed.update( experiment, pubmedId, dryRun ) );

            ////////////////////////////////
            // Write report.
            log.debug( StringUtils.rightPad( experiment.getAc(), 15 ) +
                                StringUtils.rightPad( current + " / " + experimentShortlabel, 50 ) +
                                pubmedId + "   " + generateCitexploreUrl( pubmedId ) );


        } catch ( Exception e ) {

            log.error( "An exception was thrown diring the update process of:" );
            log.error( StringUtils.rightPad( experiment.getAc(), 15 ) +
                                StringUtils.rightPad( experiment.getShortLabel(), 23 ) +
                                pubmedId + "   " + generateCitexploreUrl( pubmedId ) );

            // display exception and causes (if any)
            Throwable t = (Throwable) e;
            while ( t != null ) {

                t.printStackTrace();

                t = t.getCause();
                if ( t != null ) {
                    log.error( "============================ CAUSED BY  ========================" );
                }
            }
        }
    }

    /**
     * Prints update report to System.out.
     *
     * @param report
     */
    private static void printReport( UpdateSingleExperimentReport report ) {
        if ( report.isAuthorListUpdated() ) {
            log.debug( "author list updated" );
        }

        if ( report.isContactUpdated() ) {
            log.debug( "contact updated" );
        }

        if ( report.isJournalUpdated() ) {
            log.debug( "journal updated" );
        }

        if ( report.isYearUpdated() ) {
            log.debug( "year of publication updated" );
        }
    }

    ////////////////////////
    // M A I N

    public static void main( String[] args ) throws IntactException, SQLException {

        try {
            // setup HTTP proxy, cf. intactCore/config/proxy.properties
            HttpProxyManager.setup();
        } catch ( HttpProxyManager.ProxyConfigurationNotFound e ) {
            log.error( e.getMessage() );
        }
        
            try {
                log.debug( "Helper created (User: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbUserName() + " " +
                                    "Database: " + IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbName() + ")" );
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            // retreive all experiment ACs
            System.out.print( "Loading experiments ... " );
            System.out.flush();
            Connection connection = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().connection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( "SELECT ac FROM ia_experiment ORDER BY created" );
            List experimentAcs = new ArrayList();
            while ( resultSet.next() ) {
                experimentAcs.add( resultSet.getString( 1 ) );
            }
            resultSet.close();
            statement.close();
            connection = null; // release the connection, don't close it, the helper is doing that for us.

            log.debug( experimentAcs.size() + " experiment's AC loaded." );

            for ( Iterator iterator = experimentAcs.iterator(); iterator.hasNext(); ) {
                String ac = (String) iterator.next();

                // get the experiment
                Experiment experiment = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().getByAc(ac);

                updateExperiment(  experiment, false );

                iterator.remove(); // empty the collection as we go
            }

    }
}