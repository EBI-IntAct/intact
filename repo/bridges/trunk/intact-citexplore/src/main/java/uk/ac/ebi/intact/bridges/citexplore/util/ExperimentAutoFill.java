package uk.ac.ebi.intact.bridges.citexplore.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.citexplore.exceptions.PublicationNotFoundException;
import uk.ac.ebi.intact.bridges.citexplore.exceptions.UnexpectedException;

/**
 * Module used to collect information from CitExplore in order to prefill an Experiment (shortlabel, fullname, Xref,
 * Annotation).
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id: ExperimentAutoFill.java 13233 2009-06-08 13:36:49Z baranda $
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
     * autogenerates a shortlabel for an experiment based on the given pubmed ID.
     *
     * @return the generated shortlabel.
     *
     * @throws UnexpectedException          If some unexpected error occured
     * @throws PublicationNotFoundException if the given pubmed ID could not be found or retreived from CitExplore.
     */
    public String getShortlabel() throws UnexpectedException,
            PublicationNotFoundException {
        String authorLastName = citation.getAuthorLastName();

        int year = citation.getYear();
        String experimentShortlabel = authorLastName + "-" + year;

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
