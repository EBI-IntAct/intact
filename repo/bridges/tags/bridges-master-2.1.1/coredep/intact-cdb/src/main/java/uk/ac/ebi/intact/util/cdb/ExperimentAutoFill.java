/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.util.ExperimentUtils;

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

    private String pubmedID;

    //////////////////////
    // Constructor

    public ExperimentAutoFill( String pubmedID ) throws PublicationNotFoundException,
                                                        UnexpectedException {

        PubmedIdChecker.ensureValidFormat(pubmedID);

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
        IntactCitationFactory intactCitationFactory = IntactCitationFactory.getInstance();
        return intactCitationFactory.buildCitation( pubmedID );
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
        return getShortlabel(true);
    }

    /**
     * autogenerates a shortlabel for an experiment based on the given pubmed ID. <br>
     * If accessDatabase is true, we need to take into account the
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
     * @param accessDatabase if true, access the database to calculate the prefix of the experiment
     */
    public String getShortlabel(boolean accessDatabase) throws IntactException,
                                                              UnexpectedException,
                                                              PublicationNotFoundException {

        String authorLastName = citation.getAuthorLastName();

        int year = citation.getYear();
        String experimentShortlabel = authorLastName + "-" + year;

        if (accessDatabase) {
            experimentShortlabel = ExperimentUtils.syncShortLabelWithDb(experimentShortlabel, pubmedID);
        }

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

}