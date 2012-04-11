package uk.ac.ebi.intact.bridges.imexcentral;

import edu.ucla.mbi.imex.central.ws.v20.Publication;

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
     * Retrieve a publication record in IMEx central using pubmed, doi, imex or internal identifier
     * @param identifier : pubmed, doi, unassigned or imex
     * @return the publication in IMEx central if it exists, null otherwise
     * @throws ImexCentralException
     */
    public Publication getPublicationById( String identifier ) throws ImexCentralException;

    /**
     * Collect a list of publications associated with a specific owner. Selects first and last results
     * @param owner : login of owner
     * @param first : first result
     * @param max : last result
     * @return list of publications associated with this owner, empty list if no results
     * @throws ImexCentralException
     */
    public List<Publication> getPublicationByOwner( String owner, int first, int max) throws ImexCentralException;

    /**
     * Collect a list of publications having a specific status. Selects first and last results
     * @param status : the status of publication in IMEx central
     * @param first : first result
     * @param max : last result
     * @return list of publications associated with this status, empty list if no results
     * @throws ImexCentralException
     */
    public List<Publication> getPublicationByStatus( String status, int first, int max) throws ImexCentralException;

    /**
     * Update the status of a publication associated with a valid pubmed identifier already registered in IMEx central
     * @param identifier : a valid pubmed identifier
     * @param status : new status
     * @return the updated record in IMEx central
     * @throws ImexCentralException if no record found in IMEx central, no pubmed identifier or invalid status
     */
    public Publication updatePublicationStatus( String identifier, PublicationStatus status ) throws ImexCentralException;

    /**
     * Update the publication admin group given a valid pubmed identifier and a valid operator. The publication must be registered in IMEx central
     * @param identifier : valid pubmed id
     * @param operation : DROP or ADD
     * @param group : the name of the admin group
     * @return the updated record in IMEx central
     * @throws ImexCentralException if no record found in IMEX central, no pubmed identifiier or unknown group
     */
    public Publication updatePublicationAdminGroup( String identifier, Operation operation, String group ) throws ImexCentralException;

    /**
     * Update the publication admin user given a valid pubmed identifier and a valid operator. The publication must be registered in IMEx central
     * @param identifier
     * @param operation
     * @param user
     * @return the updated record in IMEx central
     * @throws ImexCentralException if no record found in IMEX central, no pubmed identifiier or unknown user
     */
    public Publication updatePublicationAdminUser( String identifier, Operation operation, String user ) throws ImexCentralException;

    /**
     * Update publication pubmed identifier, DOI number or internal identifier of an existing record in IMEx central. The new publication identifier should not be already registered in IMEx central
     * @param oldIdentifier : can be pubmed, imex, doi or internal identifier
     * @param newIdentifier : can be pubmed, doi or internal identifier
     * @throws ImexCentralException if no record found in IMEX central, identifier not recognized or new identifier is associated with another publication in IMEx central
     */
    public Publication updatePublicationIdentifier(String oldIdentifier, String newIdentifier) throws ImexCentralException;

    /**
     * Create a new publication record in IMEx central (not implemented yet in production but can be used in test with the MockImexCentralClient)
     * @param publication : the publication to create in IMEx central
     * @throws ImexCentralException
     */
    public void createPublication( Publication publication ) throws ImexCentralException;

    /**
     * Creates a publication given a valid pubmed id.
     * @param identifier : valid pubmed id
     * @return the newly created record
     * @throws ImexCentralException if identifier is not valid pubmed or if the pubmed id is already registered in IMEx central
     */
    public Publication createPublicationById( String identifier ) throws ImexCentralException;

    /**
     * Create a new IMEx id if requested for a given pubmed id
     * @param identifier: a valid pubmed identifier
     * @param aBoolean : true if we want to assign a new IMEx id, false otherwise
     * @return the updated record in IMEx central
     * @throws ImexCentralException if not a valid pubmed id, not existing record and cannot generate a new IMEx id
     */
    public Publication getPublicationImexAccession( String identifier, boolean aBoolean ) throws ImexCentralException;
}
