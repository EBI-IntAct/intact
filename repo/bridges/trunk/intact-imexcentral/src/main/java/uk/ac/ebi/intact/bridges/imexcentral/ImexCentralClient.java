package uk.ac.ebi.intact.bridges.imexcentral;

import edu.ucla.mbi.imex.central.ws.Publication;

import java.util.List;

/**
 * IMEx Central Client.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public interface ImexCentralClient {

    String getEndpoint();

    List<Publication> getPublicationById( List<String> identifiers ) throws ImexCentralException;

    Publication getPublicationById( String identifier ) throws ImexCentralException;

    List<Publication> getPublicationByOwner( List<String> owners ) throws ImexCentralException;

    List<Publication> getPublicationByStatus( PublicationStatus... statuses ) throws ImexCentralException;

    Publication updatePublicationStatus( String identifier, PublicationStatus status, String message ) throws ImexCentralException;

    public void updatePublicationAdminGroup( String identifier, Operation operation, String group ) throws ImexCentralException;

    public void updatePublicationAdminUser( String identifier, Operation operation, String user ) throws ImexCentralException;

    void createPublication( Publication publication ) throws ImexCentralException;

    Publication createPublicationById( String identifier ) throws ImexCentralException;

    Publication getPublicationImexAccession( String identifier, boolean aBoolean ) throws ImexCentralException;
}
