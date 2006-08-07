/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.context.IntactContext;

import java.util.*;

/**
 * Module used to collect information from CitExplore in order to prefill an Experiment (shortlabel, fullname, Xref,
 * Annotation).
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Aug-2005</pre>
 */
public class ExperimentAutoFill {

    private static final Log log = LogFactory.getLog(ExperimentAutoFill.class);

    /////////////////////////
    // Inner Class

    private Comparator creationDateComparator = new Comparator() {
        public int compare( Object o1, Object o2 ) {

            if ( !( o1 instanceof Experiment ) ) {
                throw new ClassCastException();
            }

            if ( !( o2 instanceof Experiment ) ) {
                throw new ClassCastException();
            }

            Experiment experiment1 = (Experiment) o1;
            Experiment experiment2 = (Experiment) o2;

            return experiment1.getCreated().compareTo( experiment2.getCreated() );
        }
    };

    ////////////////////
    // Constants

    // set to true will display debugging message on STDOUT.
    private static final boolean _DEBUG_ = false;

    ////////////////////////
    // Instance variable

    private String pubmedID = null;

    //////////////////////
    // Constructor

    public ExperimentAutoFill( String pubmedID ) throws PublicationNotFoundException,
                                                        UnexpectedException {

        if ( pubmedID == null ) {
            throw new NullPointerException();
        }

        try {
            // check that it is an Integer
            Integer.parseInt( pubmedID );
        } catch ( NumberFormatException e ) {
            throw e;
        }

        this.pubmedID = pubmedID;

        try {

            citation = loadCitation( pubmedID );

        } catch ( PublicationNotFoundException e ) {
            throw e;
        } catch ( Exception e ) {
            throw new UnexpectedException( "An unexpected error occured (ie. " + e.getMessage() + ")", e );
        }
    }

    ////////////////////////////
    // Private methods

    private IntactCitation citation = null;

    /**
     * Retreive citation details from CitExplore.
     *
     * @param pubmedID the pubmed ID of the publication.
     *
     * @return the citation's details.
     *
     * @throws UnexpectedException          if an unexpected error occured.
     * @throws PublicationNotFoundException if the pulication could not be found.
     */
    private IntactCitation loadCitation( String pubmedID ) throws UnexpectedException,
                                                                  PublicationNotFoundException {
        return IntactCitationFactory.getInstance().buildCitation( pubmedID );
    }

    /**
     * Retreive a pubmed ID from an IntAct experience. <br> That information should be found in Xref( CvDatabase( pubmed
     * ), CvXrefQualifier( primary-reference ) ).
     *
     * @param experiment the experiment from which we try to retreive the pubmedId.
     *
     * @return the pubmedId as a String or null if none were found.
     */
    private String getPubmedId( Experiment experiment ) {

        if ( experiment == null ) {
            return null;
        }

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

    ////////////////////////////
    // Getters

    /**
     * autogenerates a shortlabel for an experiment based on the given pubmed ID. <br> We need to take into account the
     * current content of the database, so we proceed as follow:
     * <pre>
     * (1) load all experimentXrefs having that same pubmed ID
     *     Note: this is in case some shortlabel would have been created not following the required format.
     * (2) load all experiment matching the pattern 'author-year-%'
     * (3) sort them by creation date
     * (4) add then to the Suffix generator as context
     * (5) get the suffix for the new experiment
     * </pre>
     *
     * @return the generated shortlabel.
     *
     * @throws IntactException              if an error occured when accessing IntAct.
     * @throws UnexpectedException          If some unexpected error occured
     * @throws PublicationNotFoundException if the given pubmed ID could not be found or retreived from CitExplore.
     */
    public String getShortlabel() throws IntactException,
                                                              UnexpectedException,
                                                              PublicationNotFoundException {

        CvDatabase pubmed = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(CvDatabase.class).getByXref( CvDatabase.PUBMED_MI_REF );
        CvXrefQualifier primaryRef = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getCvObjectDao(CvXrefQualifier.class).getByXref(CvXrefQualifier.PRIMARY_REFERENCE_MI_REF );

        // Load, from IntAct, all existing experiment having that same pudmed ID.

        // (1) load all experimentXrefs having that same pubmed ID
        Collection experimentXrefs = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().getByXrefLike(pubmedID);

        if ( _DEBUG_ ) {
            log.debug( "Found " + experimentXrefs.size() + " experiment(s) by PubMed( " + pubmedID + " )" );
            for ( Iterator iterator = experimentXrefs.iterator(); iterator.hasNext(); ) {
                Experiment experiment = (Experiment) iterator.next();
                log.debug( experiment.getShortLabel() );
            }
        }

        // (2) load all experiment matching the pattern 'author-year-%'
        String authorLastName = citation.getAuthorLastName();

        int year = citation.getYear();
        String prefix = authorLastName + "-" + year;

        Collection experimentLLabels = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getExperimentDao().getByShortLabelLike(prefix+"%");

        if ( _DEBUG_ ) {
            log.debug( "Found " + experimentLLabels.size() + " experiment(s) by prefix( " + prefix + " )" );
            for ( Iterator iterator = experimentLLabels.iterator(); iterator.hasNext(); ) {
                Experiment experiment = (Experiment) iterator.next();
                log.debug( experiment.getShortLabel() );
            }
        }

        // (3) sort them by creation date
        Set _allExperiments = new HashSet( experimentLLabels.size() + experimentXrefs.size() );
        _allExperiments.addAll( experimentXrefs );
        _allExperiments.addAll( experimentLLabels );

        // get a distinct set of Experiment
        List allExperiments = new ArrayList( _allExperiments );

        Collections.sort( allExperiments, creationDateComparator );

        // (4) add then to the Suffix generator as context
        if ( _DEBUG_ ) {
            log.debug( "Initialise the current context" );
        }
        ExperimentShortlabelGenerator suffixGenerator = new ExperimentShortlabelGenerator();
        for ( Iterator iterator = allExperiments.iterator(); iterator.hasNext(); ) {
            Experiment experiment = (Experiment) iterator.next();
            if ( _DEBUG_ ) {
                System.out.print( experiment.getShortLabel() + " " + experiment.getCreated() );
                System.out.flush();
            }
            String pmid = getPubmedId( experiment );
            IntactCitation citation = loadCitation( pmid );

            String s = suffixGenerator.getSuffix( citation.getAuthorLastName(), citation.getYear(), pmid );
            if ( _DEBUG_ ) {
                log.debug( "   --->   " + s );
            }
        }

        // (5) get the suffix for the new experiment
        String suffix = suffixGenerator.getSuffix( authorLastName, year, pubmedID );

        // Build the shortlabel
        // Here we don't care (yet) about the suffixes ... but keeping a list of all already generated
        // shortlabel in the scope of the experimentList should allow us to generate it easily.
        String experimentShortlabel = authorLastName + "-" + year + suffix;

        return experimentShortlabel;
    }

    /**
     * return a well sized fullname. <br> IntAct's experiment have constraint on the size of their fullname.
     *
     * @return the experiment fullname.
     */
    public String getFullname() {
        return citation.getTitle();
    }

    public boolean hasAuthorEmail() {
        return citation.hasEmail();
    }

    public String getAuthorEmail() {
        return citation.getEmail();
    }

    public String getAuthorList() {
        return citation.getAuthorList();
    }

    public String getJournal() {
        return citation.getJournal();
    }

    public int getYear() {
        return citation.getYear();
    }

    /////////////////////////
    // D E M O

    public static void main( String[] args ) throws Exception {

        // working cases
//        ExperimentAutoFill eaf = new ExperimentAutoFill( "12130660" ); // with more than one pub by year
//        ExperimentAutoFill eaf = new ExperimentAutoFill( "16104060" );   // not in IntAct, neither in CitExplore
//        ExperimentAutoFill eaf = new ExperimentAutoFill( "12029088" );   // not in IntAct, neither in CitExplore
//        ExperimentAutoFill eaf = new ExperimentAutoFill( "14517332" );   // not in IntAct, neither in CitExplore
//        ExperimentAutoFill eaf = new ExperimentAutoFill( "12244133" );   // not in IntAct, neither in CitExplore
        ExperimentAutoFill eaf = new ExperimentAutoFill( "9010225" );   // not in IntAct, neither in CitExplore

        // error case
//        ExperimentAutoFill eaf = new ExperimentAutoFill( "16104058" );   // not in IntAct, neither in CitExplore
//        ExperimentAutoFill eaf = new ExperimentAutoFill( "-1" );   // invalid pubmed id
//        ExperimentAutoFill eaf = new ExperimentAutoFill( "blabla" );   // invalid pubmed id


        log.info( eaf.getShortlabel( ) );

        log.info( eaf.getFullname() );
        log.info( eaf.getAuthorList() );
        if ( eaf.hasAuthorEmail() ) {
            log.info( eaf.getAuthorEmail() );
        } else {
            log.info( "No email" );
        }

        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();
    }
}