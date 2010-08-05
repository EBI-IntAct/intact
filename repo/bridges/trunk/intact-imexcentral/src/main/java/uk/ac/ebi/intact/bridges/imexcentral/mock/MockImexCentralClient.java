package uk.ac.ebi.intact.bridges.imexcentral.mock;

import edu.ucla.mbi.imex.central.ws.Identifier;
import edu.ucla.mbi.imex.central.ws.Publication;
import uk.ac.ebi.intact.bridges.imexcentral.ImexCentralClient;
import uk.ac.ebi.intact.bridges.imexcentral.ImexCentralException;
import uk.ac.ebi.intact.bridges.imexcentral.Operation;
import uk.ac.ebi.intact.bridges.imexcentral.PublicationStatus;

import java.util.ArrayList;
import java.util.List;

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
        p.setIdentifier( id );
        p.setImexAccession( ( imexAccession == null ? "N/A" : imexAccession ) );
        p.setStatus( status );
        p.setOwner( owner );
        allPublications.add( p );
    }

    ///////////////////////
    // ImexCentralClient

    public List<Publication> getPublicationById( List<String> identifiers ) throws ImexCentralException {
        List<Publication> publications = new ArrayList<Publication>( );
        for ( Publication p : allPublications ) {
           if( identifiers.contains( p.getIdentifier().getAc() ) ) {
               publications.add( p );
           }
        }
        return publications;
    }

    public Publication getPublicationById( String identifier ) throws ImexCentralException {
        for ( Publication p : allPublications ) {
           if( identifier.equals( p.getIdentifier().getAc() ) ) {
               return p;
           }
        }
        return null;
    }

    public List<Publication> getPublicationByOwner( List<String> owners ) throws ImexCentralException {
        List<Publication> publications = new ArrayList<Publication>( );
        for ( Publication p : allPublications ) {
           if( owners.contains( p.getOwner() ) ) {
               publications.add( p );
           }
        }
        return publications;
    }

    public List<Publication> getPublicationByStatus( PublicationStatus... statuses ) throws ImexCentralException {
        List<Publication> publications = new ArrayList<Publication>( );
        for ( Publication p : allPublications ) {
            boolean stop = false;
            for ( int i = 0; i < statuses.length && ! stop; i++ ) {
                PublicationStatus status = statuses[i];
                if( status.toString().equals( p.getStatus() ) ) {
                    publications.add( p );
                    stop = true;
                }
            }
        }
        return publications;
    }

    public Publication updatePublicationStatus( String identifier, PublicationStatus status, String message ) throws ImexCentralException {
        final Publication p = getPublicationById( identifier );
        if( p != null ) {
            p.setStatus( status.toString() );
        }
        return p;
    }

    public void updatePublicationAdminGroup( String identifier, Operation operation, String group ) throws ImexCentralException {
        throw new UnsupportedOperationException( );
    }

    public void updatePublicationAdminUser( String identifier, Operation operation, String user ) throws ImexCentralException {
        throw new UnsupportedOperationException( );
    }

    public void createPublication( Publication publication ) throws ImexCentralException {
        allPublications.add( publication );
    }

    public Publication createPublicationById( String identifier ) throws ImexCentralException {
        Publication p = new Publication();
        final Identifier i = new Identifier();
        i.setAc( identifier );
        p.setIdentifier( i );
        p.setImexAccession( "N/A" );
        allPublications.add( p );
        return p;
    }

    public Publication getPublicationImexAccession( String identifier, boolean create ) throws ImexCentralException {
        final Publication p = getPublicationById( identifier );
        if( create ) {
            if( ! p.getImexAccession().equals("N/A")) {
                throw new IllegalStateException( "Publication already has an IMEx id: " + p.getImexAccession() );
            }

            // assigning new IMEx ID
            p.setImexAccession( "IM-" + imexIdSequence );
            imexIdSequence++;
        }

        return p;
    }
}
