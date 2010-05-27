package uk.ac.ebi.intact.bridges.imexcentral;

import edu.ucla.mbi.imex.central.ws.Publication;

import java.util.List;

/**
 * TODO document this !
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since TODO add POM version
 */
public interface ImexCentralClient {
    List<Publication> getPublicationById( List<String> identifiers ) throws ImexCentralException;

    Publication getPublicationById( String identifier ) throws ImexCentralException;

    List<Publication> getPublicationByOwner( List<String> owners ) throws ImexCentralException;

    List<Publication> getPublicationByStatus( PublicationStatus... statuses ) throws ImexCentralException;

    Publication updatePublicationStatus( String identifier, PublicationStatus status ) throws ImexCentralException;

    void createPublication( Publication publication ) throws ImexCentralException;

    Publication createPublicationById( String identifier ) throws ImexCentralException;

    Publication getPublicationImexAccession( String identifier, boolean aBoolean ) throws ImexCentralException;
}
