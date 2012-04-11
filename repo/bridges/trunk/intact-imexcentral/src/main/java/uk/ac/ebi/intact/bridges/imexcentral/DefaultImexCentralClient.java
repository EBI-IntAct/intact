package uk.ac.ebi.intact.bridges.imexcentral;

import edu.ucla.mbi.imex.central.ws.v20.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Default IMEx Central Client.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class DefaultImexCentralClient implements ImexCentralClient {

    private static final Log log = LogFactory.getLog( DefaultImexCentralClient.class );

    public static final String IC_TEST = "https://imexcentral.org/icentraltest/ws-v20";
    public static final String IC_BETA = "https://imexcentral.org/icentralbeta/ws-v20";
    public static final String IC_PROD = IC_BETA;

    private IcentralService service;
    private IcentralPort port;
    private String endPoint;

    private static Pattern pubmed_regexp = Pattern.compile("\\d+");

    public final static int USER_NOT_AUTHORIZED = 2;
    public final static int OPERATION_NOT_VALID = 3;
    public final static int IDENTIFIER_MISSING = 4;
    public final static int IDENTIFIER_UNKNOWN = 5;
    public final static int NO_RECORD = 6;
    public final static int NO_RECORD_CREATED = 7;
    public final static int STATUS_UNKNOWN = 8;
    public final static int NO_IMEX_ID = 9;
    public final static int UNKNOWN_USER = 10;
    public final static int UNKNOWN_GROUP = 11;
    public final static int OPERATION_NOT_SUPPORTED = 98;
    public final static int INTERNAL_SERVER_ERROR = 99;

    public DefaultImexCentralClient( String username, String password, String endPoint ) throws ImexCentralException {

        this.endPoint = endPoint;

        try {
            URL url = new URL( endPoint + "?wsdl" );
            log.debug( "WSDL: " + endPoint + "?wsdl" );
            QName qn = new QName( "http://imex.mbi.ucla.edu/icentral/ws", "ics20" );
            service = new IcentralService( url, qn );
            port = service.getIcp20();

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

    public String getEndpoint() {
        return endPoint;
    }

    /**
     *
     * @param identifier
     * @return the publication or null if not found.
     * @throws ImexCentralException
     */
    public Publication getPublicationById( String identifier ) throws ImexCentralException {
        try {
            final Publication pub = port.getPublicationById(buildIdentifier(identifier));
            return pub;
        } catch ( IcentralFault f ) {
            switch( f.getFaultInfo().getFaultCode() ) {
                case NO_RECORD:
                    // simply no data found, return null
                    return null;
            }

            throw new ImexCentralException("Impossible to find the publication " + identifier, f);
        }
    }

    public List<Publication> getPublicationByOwner( String owner, int first, int max) throws ImexCentralException {
        try {
            // create holders for publication and last record
            Holder<PublicationList> pubList = new Holder<PublicationList>();
            Holder<Long> number = new Holder<Long>();

            port.getPublicationByOwner( owner, first, max, pubList, number );
            if( pubList.value != null) {
                return pubList.value.getPublication();
            }
            return Collections.EMPTY_LIST;
        } catch ( IcentralFault f ) {
            switch( f.getFaultInfo().getFaultCode() ) {
                case NO_RECORD:
                    // simply no data found, return empty list
                    return Collections.EMPTY_LIST;
            }

            throw new ImexCentralException( "Error while getting publications by owner: " + owner, f );
        }
    }

    public List<Publication> getPublicationByStatus( String status, int first, int max) throws ImexCentralException {

        try {
            // create holders for publication and last record
            Holder<PublicationList> pubList = new Holder<PublicationList>();
            Holder<Long> number = new Holder<Long>();

            port.getPublicationByStatus( status, first, max, pubList, number );

            if( pubList.value != null) {
                return pubList.value.getPublication();
            }
            return Collections.EMPTY_LIST;

        } catch ( IcentralFault f ) {
            switch( f.getFaultInfo().getFaultCode() ) {
                case NO_RECORD:
                    // simply no data found, return empty list
                    return Collections.EMPTY_LIST;
            }

            throw new ImexCentralException( "Error while getting publications by status: " + status, f );
        }
    }

    /**
     *
     * @param identifier
     * @param status
     * @return an updated publication, null if not found.
     * @throws ImexCentralException
     */
    public Publication updatePublicationStatus( String identifier, PublicationStatus status) throws ImexCentralException {
        try {
            return port.updatePublicationStatus( buildIdentifier( identifier ), status.toString(), null );
        } catch ( IcentralFault f ) {

            throw new ImexCentralException( "Error while attempting to update a publication status: " +
                    identifier + "/" + status, f );
        }
    }

    public Publication updatePublicationAdminGroup( String identifier, Operation operation, String group ) throws ImexCentralException {
        try {
            return port.updatePublicationAdminGroup( buildIdentifier(identifier ), operation.toString(), group );
        } catch ( IcentralFault f ) {

            throw new ImexCentralException( "Error while attempting to upate the admin group to '"+
                    group +"' for publication " + identifier, f );
        }
    }

    public Publication updatePublicationAdminUser( String identifier, Operation operation, String user ) throws ImexCentralException {
        try {
            return port.updatePublicationAdminUser( buildIdentifier(identifier ), operation.toString(), user );
        } catch ( IcentralFault f ) {

            throw new ImexCentralException( "Error while attempting to upate the admin user to '"+
                    user +"' for publication " + identifier, f );
        }
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

        try {
            return port.updatePublicationIdentifier( buildIdentifier(oldIdentifier ), buildIdentifier(newIdentifier) );
        } catch ( IcentralFault f ) {

            throw new ImexCentralException( "Error while attempting to upate the identifier to '"+
                    newIdentifier +"' for publication " + oldIdentifier, f );
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
        String localTrustStorePwd = System.getProperty( "javax.net.ssl.keyStorePassword" );
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

        // test 19360080
        Publication publication;
        try {
            System.out.println( "Searching by PMID ..." );
            publication = client.getPublicationById( "19360080" );
            print( publication );

            System.out.println( "Searching by IMEx ID ..." );
            publication = client.getPublicationById( "IM-12212" );
            print( publication );
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
        System.out.println( "-- " + p.getIdentifier().size() + " identifiers ------------------------" );
        System.out.println( "Author: " + p.getAuthor() );
        System.out.println( "Title: " + p.getTitle() );
        System.out.println( "IMEx id: " + p.getImexAccession() );
        System.out.println( "Owner: " + p.getOwner() );
        System.out.println( "Status: " + p.getStatus() );
        System.out.println( "Release date: " + p.getReleaseDate() );
    }
}
