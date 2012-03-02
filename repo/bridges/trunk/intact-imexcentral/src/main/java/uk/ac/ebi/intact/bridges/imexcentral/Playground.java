package uk.ac.ebi.intact.bridges.imexcentral;

import edu.ucla.mbi.imex.central.ws.v20.Publication;

/**
 * Sample script to play with the client.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.1
 */
public class Playground {
    public static void main( String[] args ) throws Exception {

        if ( args.length < 3 ) {
            System.err.println( "Usage: java " +
                                "-Djavax.net.ssl.trustStore=<path.to.keystore> " +
                                "-Djavax.net.ssl.keyStorePassword=<password> " +
                                "Playground <imexcentral.username> <imexcentral.password> <pmid>" );
            System.exit( 1 );
        }

        final String icUsername = args[0];
        final String icPassword = args[1];
        final String pmid = args[2];

        ImexCentralClient client = new DefaultImexCentralClient( icUsername, icPassword, DefaultImexCentralClient.IC_TEST );

        final Publication publication = client.getPublicationById( pmid );

        if( publication == null ) {
            System.err.println( "Could not find this publication in IMExCentral." );
        } else {
            System.out.println( "Identifier: " + publication.getIdentifier() );
            System.out.println( "Title: " + publication.getTitle() );
            System.out.println( "IMEx ID: " + publication.getImexAccession() );
            System.out.println( "Owner: " + publication.getOwner() );
            System.out.println( "Status: " + publication.getStatus() );
        }
    }
}
