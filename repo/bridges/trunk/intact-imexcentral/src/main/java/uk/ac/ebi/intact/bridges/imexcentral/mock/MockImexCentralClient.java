package uk.ac.ebi.intact.bridges.imexcentral.mock;

import edu.ucla.mbi.imex.central.ws.v20.*;
import uk.ac.ebi.intact.bridges.imexcentral.ImexCentralClient;
import uk.ac.ebi.intact.bridges.imexcentral.ImexCentralException;
import uk.ac.ebi.intact.bridges.imexcentral.Operation;
import uk.ac.ebi.intact.bridges.imexcentral.PublicationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Dummy service that one can use to write test against.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.2
 */
public class MockImexCentralClient implements ImexCentralClient {

    public MockImexCentralClient() {
        allPublications = new ArrayList<Publication>( );
    }

    private int imexIdSequence = 1;
    List<Publication> allPublications;
    private static Pattern PUBMED_REGEXP = Pattern.compile("\\d+");

    private String INTACT_GROUP = "INTACT";
    private String MATRIXDB_GROUP = "MATRIXDB";
    private String intact_user = "intact";
    private String phantom_user = "phantom";

    private static Pattern pubmed_regexp = Pattern.compile("\\d+");

    /////////////////////////////
    // Service initialization

    public void initImexSequence( int sequence ) {
        if( sequence < 1 ) {
            throw new IllegalArgumentException( "You must give a positive sequence: " + sequence );
        }
        imexIdSequence = sequence;
    }

    public void initPublications( List<Publication> allPublications ) {
        if ( allPublications == null ) {
            throw new IllegalArgumentException( "You must give a non null allPublications" );
        }
        this.allPublications = allPublications;
    }

    public int getNextSequenceValue() {
        return imexIdSequence;
    }

    public void addPublication( Publication p ) {
        if ( p == null ) {
            throw new IllegalArgumentException( "You must give a non null publication" );
        }
        allPublications.add( p );
    }

    public void addPublication( String identifier, String imexAccession, String status, String owner ) {
        Publication p = new Publication();
        final Identifier id = new Identifier();
        id.setAc( identifier );
        p.getIdentifier().add(id);
        p.setImexAccession( ( imexAccession == null ? "N/A" : imexAccession ) );
        p.setStatus( status );
        p.setOwner( owner );
        allPublications.add( p );
    }

    ///////////////////////
    // ImexCentralClient

    public String getEndpoint() {
        return null;
    }

    public Publication getPublicationById( String identifier ) throws ImexCentralException {

        for ( Publication p : allPublications ) {
            for (Identifier i : p.getIdentifier()){
                if( identifier.equals( i.getAc() ) ) {
                    return p;
                }
            }

            if (p.getImexAccession() != null && p.getImexAccession().equalsIgnoreCase(identifier)){
                return p;
            }
        }

        return null;
    }

    public Publication getPublicationByPubmedId( String identifier ) throws ImexCentralException {

        for ( Publication p : allPublications ) {
            for (Identifier i : p.getIdentifier()){
                if( "pmid".equalsIgnoreCase(i.getNs()) && identifier.equals( i.getAc() ) ) {
                    return p;
                }
            }

            if (p.getImexAccession() != null && p.getImexAccession().equalsIgnoreCase(identifier)){
                return p;
            }
        }

        return null;
    }

    public List<Publication> getPublicationByOwner( String owner, int first, int max) throws ImexCentralException {
        List<Publication> publications = new ArrayList<Publication>( );
        for ( int i = first; i <= max; i++ ) {
            Publication p = allPublications.get(i);
            if( owner.equals(p.getOwner()) ) {
                publications.add( p );
            }
        }
        return publications;
    }

    public List<Publication> getPublicationByStatus( String status, int first, int max) {
        List<Publication> publications = new ArrayList<Publication>( );
        for ( int j = first; j <= max; j++ ) {
            Publication p = allPublications.get(j);

            if (p.getStatus().equalsIgnoreCase(status)){
                publications.add( p );
            }
        }
        return publications;
    }

    public Publication updatePublicationStatus( String identifier, PublicationStatus status) throws ImexCentralException {
        final Publication p = getPublicationByPubmedId( identifier );

        if (!status.toString().equals("NEW") && !status.toString().equals("DISCARDED") && !status.toString().equals("RESERVED")
                && !status.toString().equals("PROCESSED") && !status.toString().equals("RELEASED") && !status.toString().equals("INPROGRESS") &&
                !status.toString().equals("INCOMPLETE")){
            ImexCentralFault imexFault = new ImexCentralFault();
            imexFault.setFaultCode(8);

            IcentralFault fault = new IcentralFault("Status not recognized", imexFault);
            throw new ImexCentralException(fault);
        }

        if( p != null ) {
            p.setStatus( status.toString() );
        }
        else {
            ImexCentralFault imexFault = new ImexCentralFault();
            imexFault.setFaultCode(6);

            IcentralFault fault = new IcentralFault("No publication record found", imexFault);
            throw new ImexCentralException(fault);
        }
        return p;
    }

    public Publication updatePublicationAdminGroup( String identifier, Operation operation, String group ) throws ImexCentralException {
        Publication p = getPublicationByPubmedId( identifier );

        if (p != null){
            if (group != null && (group.equalsIgnoreCase(INTACT_GROUP) || group.equalsIgnoreCase(MATRIXDB_GROUP))){
                if (p.getAdminGroupList() == null){
                    p.setAdminGroupList(new Publication.AdminGroupList());
                }
                p.getAdminGroupList().getGroup().add(group.toUpperCase());
            }
            // group not recognized, throw exception as in the real webservice
            else {
                ImexCentralFault imexFault = new ImexCentralFault();
                imexFault.setFaultCode(11);

                IcentralFault fault = new IcentralFault("Group not recognized", imexFault);
                throw new ImexCentralException(fault);
            }
        }
        else {
            ImexCentralFault imexFault = new ImexCentralFault();
            imexFault.setFaultCode(6);

            IcentralFault fault = new IcentralFault("No publication record found", imexFault);
            throw new ImexCentralException(fault);
        }

        return p;
    }

    public Publication updatePublicationAdminUser( String identifier, Operation operation, String user ) throws ImexCentralException {
        Publication p = getPublicationByPubmedId( identifier );

        if (p != null){
            if (user != null && (user.equalsIgnoreCase(intact_user) || user.equalsIgnoreCase(phantom_user))){
                if (p.getAdminUserList() == null){
                    p.setAdminUserList(new Publication.AdminUserList());
                }
                p.getAdminUserList().getUser().add(user.toLowerCase());
            }
            // user not recognized, throw exception as in the real webservice
            else {
                ImexCentralFault imexFault = new ImexCentralFault();
                imexFault.setFaultCode(10);

                IcentralFault fault = new IcentralFault("User not recognized", imexFault);
                throw new ImexCentralException(fault);
            }
        }
        else {
            ImexCentralFault imexFault = new ImexCentralFault();
            imexFault.setFaultCode(6);

            IcentralFault fault = new IcentralFault("No publication record found", imexFault);
            throw new ImexCentralException(fault);
        }
        
        return p;
    }

    @Override
    public Publication updatePublicationIdentifier(String oldIdentifier, String newIdentifier) throws ImexCentralException {
        Publication existingPub = getPublicationById(newIdentifier);

        // if the new identifier is already in IMEx central, we don't update anything
        if (existingPub != null){
            ImexCentralFault imexFault = new ImexCentralFault();
            imexFault.setFaultCode(3);

            IcentralFault fault = new IcentralFault("New publication identifier " + newIdentifier + "already used in IMEx central.", imexFault);
            throw new ImexCentralException( "Impossible to update the identifier of " + oldIdentifier, fault );
        }
        
        final Publication p = getPublicationById( oldIdentifier );
        if( p != null ) {
            Identifier newId = buildIdentifier(newIdentifier);

            boolean updated = false;
            for (Identifier id : p.getIdentifier()){
                // update the proper identifier in IMEx central
                if (newId.getNs().equals(id.getNs())){
                    id.setAc(id.getAc());
                    updated = true;
                }
            }

            if (!updated){
                p.getIdentifier().add(newId);
            }
        }
        else {
            ImexCentralFault imexFault = new ImexCentralFault();
            imexFault.setFaultCode(6);

            IcentralFault fault = new IcentralFault("No publication record found", imexFault);
            throw new ImexCentralException(fault);
        }
        
        return p;
    }

    public void createPublication( Publication publication ) throws ImexCentralException {
        Publication existingP = null;

        for ( Publication p : allPublications ) {
            for (Identifier i : p.getIdentifier()){
                if( !publication.getIdentifier().isEmpty() && publication.getIdentifier().iterator().next().getAc().equals( i.getAc() ) ) {
                    existingP = p;
                    break;
                }
            }
        }

        if (existingP == null){
            allPublications.add(publication);
        }
        else {
            allPublications.remove(existingP);
            allPublications.add(publication);
        }
    }

    public Publication createPublicationById( String identifier ) throws ImexCentralException {

        Publication p = new Publication();
        final Identifier i = new Identifier();
        i.setAc( identifier );
        i.setNs("pmid");
        p.getIdentifier().add(i);
        p.setImexAccession( "N/A" );
        allPublications.add( p );
        return p;
    }

    public Publication getPublicationImexAccession( String identifier, boolean create ) throws ImexCentralException {
        final Publication p = getPublicationByPubmedId( identifier );
        
        if (p != null){
            if( create ) {
                if( p.getImexAccession() != null && !p.getImexAccession().equals("N/A")) {
                    throw new IllegalStateException( "Publication already has an IMEx id: " + p.getImexAccession() );
                }

                // assigning new IMEx ID
                p.setImexAccession( "IM-" + imexIdSequence );
                imexIdSequence++;
            }
        }
        else {
            ImexCentralFault imexFault = new ImexCentralFault();
            imexFault.setFaultCode(6);

            IcentralFault fault = new IcentralFault("No publication record found", imexFault);
            throw new ImexCentralException(fault);
        }

        return p;
    }

    protected static Identifier buildIdentifier( String identifier ) {
        final Identifier id = new ObjectFactory().createIdentifier();
        id.setAc( identifier );
        // IMEx identifier
        if( identifier.startsWith( "IM-" ) ) {
            // this will enable searching for publication by IMEx id ... not obvious but it works ...
            id.setNs( "imex" );
        }
        // valid pubmed identifier
        else if (Pattern.matches(pubmed_regexp.toString(), identifier)) {
            // fallback namespace
            id.setNs( "pmid" );
        }
        // unassigned publication = internal identifier
        else if (identifier.startsWith("unassigned")){
            id.setNs( "jint" );
        }
        // doi number
        else {
            id.setNs("doi");
        }
        return id;
    }
}
