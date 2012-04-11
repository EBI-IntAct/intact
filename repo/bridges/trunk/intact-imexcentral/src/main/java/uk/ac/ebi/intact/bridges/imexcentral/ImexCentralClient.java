package uk.ac.ebi.intact.bridges.imexcentral;

import edu.ucla.mbi.imex.central.ws.v20.Publication;
import edu.ucla.mbi.imex.central.ws.v20.PublicationList;

import javax.xml.ws.Holder;
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

    /**
     *
     * @param identifier : pubmed, doi, unassigned or imex
     * @return the publication in IMEx central if it exists
     * @throws ImexCentralException
     */
    Publication getPublicationById( String identifier ) throws ImexCentralException;

    public List<Publication> getPublicationByOwner( String owner, int first, int max, Holder<PublicationList> pubList, Holder<Long> number ) throws ImexCentralException;

    public List<Publication> getPublicationByStatus( String status, int first, int max, Holder<PublicationList> pubList, Holder<Long> number ) throws ImexCentralException;

    Publication updatePublicationStatus( String identifier, PublicationStatus status, String message ) throws ImexCentralException;

    public void updatePublicationAdminGroup( String identifier, Operation operation, String group ) throws ImexCentralException;

    public void updatePublicationAdminUser( String identifier, Operation operation, String user ) throws ImexCentralException;

    public void updatePublicationIdentifier(String oldIdentifier, String newIdentifier) throws ImexCentralException;

    void createPublication( Publication publication ) throws ImexCentralException;

    Publication createPublicationById( String identifier ) throws ImexCentralException;

    Publication getPublicationImexAccession( String identifier, boolean aBoolean ) throws ImexCentralException;
}
