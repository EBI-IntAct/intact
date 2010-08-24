package uk.ac.ebi.intact.bridges.imexcentral;

import edu.ucla.mbi.imex.central.ws.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Default IMEx Central Client.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class DefaultImexCentralClient implements ImexCentralClient {

    private static final Log log = LogFactory.getLog( DefaultImexCentralClient.class );

    public static final String IC_TEST = "https://imexcentral.org/icentraltest/ws";
    public static final String IC_BETA = "https://imexcentral.org/icentralbeta/ws";
    public static final String IC_PROD = IC_BETA;

    private IcentralService service;
    private IcentralPort port;
    private String endPoint;

    public DefaultImexCentralClient( String username, String password, String endPoint ) throws ImexCentralException {

        this.endPoint = endPoint;

        try {
            URL url = new URL( endPoint + "?wsdl" );
            log.debug( "WSDL: " + endPoint + "?wsdl" );
            QName qn = new QName( "http://imex.mbi.ucla.edu/icentral/ws", "ImexCentralService" );
            service = new IcentralService( url, qn );
            port = service.getImexCentralPort();

            ( ( BindingProvider ) port ).getRequestContext().put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPoint );
            ( ( BindingProvider ) port ).getRequestContext().put( BindingProvider.USERNAME_PROPERTY, username );
            ( ( BindingProvider ) port ).getRequestContext().put( BindingProvider.PASSWORD_PROPERTY, password );
        } catch ( MalformedURLException e ) {
            throw new ImexCentralException( "Error while initializing IMEx Central Client", e );
        }
    }

    /////////////
    // Utilities

    protected static Identifier buildIdentifier( String identifier ) {
        final Identifier id = new ObjectFactory().createIdentifier();
        id.setAc( identifier );
        id.setNs( "pmid" );     
        return id;
    }

    private List<Identifier> buildIdentifierList( List<String> identifiers ) {
        List<Identifier> ids = new ArrayList<Identifier>( );
        for ( String id : identifiers ) {
            ids.add( buildIdentifier( id ));
        }
        return ids;
    }

    private String showPublication( Publication p ) {
        StringBuilder sb = new StringBuilder( 128 );
        sb.append( "Publication[" );
        sb.append( "Identifier: " ).append( p.getIdentifier() ).append( ", " );
        sb.append( "Owner: " ).append( p.getOwner() );
        sb.append( "]" );
        return sb.toString();
    }

    ///////////////////////
    // IMEx Central

    public List<Publication> getPublicationById( List<String> identifiers ) throws ImexCentralException {
        try {
            final PublicationList list = port.getPublicationById( buildIdentifierList( identifiers ) );
            if( list != null ) {
                return list.getPublication();
            }
            return null;
        } catch ( IcentralFault f ) {
            throw new ImexCentralException( "Error while getting a publication by id: " + identifiers, f );
        }
    }

    /**
     *
     * @param identifier
     * @return the publication or null if not found.
     * @throws ImexCentralException
     */
    public Publication getPublicationById( String identifier ) throws ImexCentralException {
        try {
            final PublicationList list = port.getPublicationById( buildIdentifierList( Arrays.asList( identifier ) ) );
            if( list != null ) {
                switch( list.getPublication().size() ) {
                    case 0:
                        return null;
                    case 1:
                        return list.getPublication().iterator().next();
                    default:
                        throw new ImexCentralException( list.getPublication().size() + " publications returned for " +
                                                        "identifiers '"+identifier+"' when only one at most was " +
                                                        "expected." );
                }
            }
        } catch ( IcentralFault f ) {
            switch( f.getFaultInfo().getFaultCode() ) {
                case 6:
                    // simply no data found, return null
                    return null;
            }

            throw new ImexCentralException( "Error while getting a publication by id: " + identifier, f );
        }
        return null;
    }

    public List<Publication> getPublicationByOwner( List<String> owners ) throws ImexCentralException {
        try {
            final PublicationList publicationList = port.getPublicationByOwner( owners );
            if( publicationList != null ) {
                return publicationList.getPublication();
            }
            return null;
        } catch ( IcentralFault f ) {
            throw new ImexCentralException( "Error while getting publications by owners: " + owners, f );
        }
    }

    public List<Publication> getPublicationByStatus( PublicationStatus... statuses ) throws ImexCentralException {

        List<String> statusList = new ArrayList<String>();
        for ( PublicationStatus s : statuses ) {
            statusList.add( s.toString() );
        }

        try {
            final PublicationList publicationList = port.getPublicationByStatus( statusList );
            if( publicationList != null ) {
                return publicationList.getPublication();
            }
            return null;

        } catch ( IcentralFault f ) {
            throw new ImexCentralException( "Error while getting publications by status: " + statusList, f );
        }
    }

    /**
     *
     * @param identifier
     * @param status
     * @return an updated publication, null if not found.
     * @throws ImexCentralException
     */
    public Publication updatePublicationStatus( String identifier, PublicationStatus status, String message ) throws ImexCentralException {
        try {
            return port.updatePublicationStatus( buildIdentifier( identifier ), status.toString(), message );
        } catch ( IcentralFault f ) {

            switch( f.getFaultInfo().getFaultCode() ) {
                case 6:
                    //  no data found
                    throw new ImexCentralException( "Could not find publication '"+ identifier +
                                                    "' on which we were attempting to upate the status to '"+
                                                    status +"'", f );
            }

            throw new ImexCentralException( "Error while attempting to update a publication status: " +
                                            identifier + "/" + status, f );
        }
    }

    public void updatePublicationAdminGroup( String identifier, Operation operation, String group ) throws ImexCentralException {
        try {
            port.updatePublicationAdminGroup( buildIdentifier(identifier ), operation.toString(), group );
        } catch ( IcentralFault f ) {

            switch( f.getFaultInfo().getFaultCode() ) {
                case 6:
                    //  no data found
                    throw new ImexCentralException( "Could not find publication '"+ identifier +
                                                    "' on which we were attempting to upate the admin group to '"+
                                                    group +"'", f );
            }

            final String message = f.getFaultInfo().getMessage();
            final int code = f.getFaultInfo().getFaultCode();
            throw new ImexCentralException( "["+ code+" - "+ message +
                                            "] Error while attempting to update a publication admin group: " +
                                            identifier + "/" + group, f );
        }
    }

    public void updatePublicationAdminUser( String identifier, Operation operation, String user ) throws ImexCentralException {
        try {
            port.updatePublicationAdminUser( buildIdentifier(identifier ), operation.toString(), user );
        } catch ( IcentralFault f ) {

            switch( f.getFaultInfo().getFaultCode() ) {
                case 6:
                    //  no data found
                    throw new ImexCentralException( "Could not find publication '"+ identifier +
                                                    "' on which we were attempting to upate the admin user to '"+
                                                    user +"'", f );
            }

            final String message = f.getFaultInfo().getMessage();
            final int code = f.getFaultInfo().getFaultCode();

            throw new ImexCentralException( "["+ code+" - "+ message +
                                            "] Error while attempting to update a publication admin user: " +
                                            identifier + "/" + user, f );
        }
    }

    public void createPublication( Publication publication ) throws ImexCentralException {
        try {
            port.createPublication( new Holder( publication ) );
        } catch ( IcentralFault f ) {
            throw new ImexCentralException( "Error while creating a publication: " + showPublication(publication), f);
        }
    }

    public Publication createPublicationById( String identifier ) throws ImexCentralException {
        try {
            return port.createPublicationById( buildIdentifier( identifier ) );
        } catch ( IcentralFault f ) {
            throw new ImexCentralException( "Error while creating a publication by id: " + identifier, f );
        }
    }

    public Publication getPublicationImexAccession( String identifier, boolean aBoolean ) throws ImexCentralException {
        try {
            return port.getPublicationImexAccession( buildIdentifier( identifier ), aBoolean );
        } catch ( IcentralFault f ) {
            throw new ImexCentralException("Error while getting an IMEx id by publication by id: " + identifier, f);
        }
    }

    /////////////////
    // D E M O

    public static void main( String[] args ) throws ImexCentralException {

        if( args.length != 2 ) {
            System.err.println( "Usage: DefaultImexCentralClient <username> <password>" );
        }

        String localTrustStore = System.getProperty( "javax.net.ssl.trustStore" );
        String localTrustStorePwd = System.getProperty( "Djavax.net.ssl.keyStorePassword" );
        if(localTrustStore==null) {
            System.out.println( "It appears you haven't setup a local trust store (other than the one embedded in the JDK)." +
                                "\nShould you want to specify one, use: -Djavax.net.ssl.trustStore=<path.to.keystore> " +
                                "\nAnd if it is password protected, use: -Djavax.net.ssl.keyStorePassword=<password>" );
        } else {
            System.out.println( "Using local trust store: " + localTrustStore + (localTrustStorePwd == null ? " (no password set)" : " (with password set)" ) );
        }

        String username = args[0];
        String password = args[1];

        String endPoint = IC_TEST;

        DefaultImexCentralClient client = new DefaultImexCentralClient( username, password, endPoint );

//        final List<Publication> releasedPublications = client.getPublicationByStatus( PublicationStatus.RELEASED );
//        System.out.println( "Released publication: " + releasedPublications.size() );

        // test 19360080
        final List<Publication> publications;
        try {
            publications = client.getPublicationById( Arrays.asList( "19249676" ) );
        } catch ( ImexCentralException e ) {
            e.printStackTrace(  );

            final int code = ( ( IcentralFault ) e.getCause() ).getFaultInfo().getFaultCode();
            final String msg = ( ( IcentralFault ) e.getCause() ).getFaultInfo().getMessage();
            System.out.println( "\n\n" + code + " - " + msg );
        }
//        for ( Publication p : publications ) {
//            print( p );

//            if( p.getImexAccession().equals( "N/A" ) ) {
//                System.out.println( "\nUpdating publication without IMEx ID ..." );
//                final Publication publication = client.getPublicationImexAccession( "19360080", true );
//                print( publication );
//            }
//        }
    }

    private static void print( Publication p ) {
        System.out.println( "-- " + p.getIdentifier().getAc() + " ------------------------" );
        System.out.println( "Author: " + p.getAuthor() );
        System.out.println( "IMEx id: " + p.getImexAccession() );
        System.out.println( "Owner: " + p.getOwner() );
        System.out.println( "Status: " + p.getStatus() );
        System.out.println( "Release date: " + p.getReleaseDate() );
    }
}
