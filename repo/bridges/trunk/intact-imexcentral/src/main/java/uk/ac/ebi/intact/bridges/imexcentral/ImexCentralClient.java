package uk.ac.ebi.intact.bridges.imexcentral;

import edu.ucla.mbi.imex.icentral.ws.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * IMEx Central Client.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.2.1
 */
public class ImexCentralClient {

    private static final Log log = LogFactory.getLog( ImexCentralClient.class );

    private static final String NS = "http://imex.mbi.ucla.edu/icentral/ws";

    private ImexCentralService imexCentralService;

    public ImexCentralClient() throws MalformedURLException {

        imexCentralService = new ImexCentralService();

//        imexCentralService = new ImexCentralService( new URL( "https://imexcentral.org/icentraltest/ws?wsdl" ),
//                                                     new QName( "http://imex.mbi.ucla.edu/icentral/ws",
//                                                                "ImexCentralService") );
        
        System.out.println( "WSDLDocumentLocation: " + imexCentralService.getWSDLDocumentLocation() );
        System.out.println( "ServiceName: " + imexCentralService.getServiceName() );
    }

    protected ImexCentralService getImexCentralService() {
        return imexCentralService;
    }

    protected static Identifier buildIdentifier( String identifier ) {
        final Identifier id = new Identifier();
        id.setAc( identifier );
        id.setNs( NS );
        return id;
    }

    ///////////////////////////
    // IMEx Central Methods

    public String getPublicationImexAccession( String publicationId ) throws ImexCentralFault_Exception {
        final ImexCentralPort port = imexCentralService.getImexCentralPort();

        final Publication publication = port.getPublicationImexAccession( buildIdentifier( "17474147" ), false );
        log.info( publication.getIdentifier().getAc() + ": " + publication.getImexAccession());

        return publication.getImexAccession();
    }

    public static void main( String[] args ) throws MalformedURLException, ImexCentralFault_Exception {
        ImexCentralClient client = new ImexCentralClient();

        client.getPublicationImexAccession( "17474147" );
    }
}
