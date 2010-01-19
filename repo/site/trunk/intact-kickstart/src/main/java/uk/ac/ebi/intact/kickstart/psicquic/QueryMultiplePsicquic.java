package uk.ac.ebi.intact.kickstart.psicquic;

import org.hupo.psi.mi.psicquic.wsclient.UniversalPsicquicClient;
import psidev.psi.mi.search.SearchResult;
import psidev.psi.mi.tab.model.BinaryInteraction;
import psidev.psi.mi.tab.model.CrossReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This example shows how to query multiple PSICQUIC service found in the registry.
 */
public class QueryMultiplePsicquic {

    public static void main( String[] args ) throws Exception {

        // Use REST URL to find active PSICQUIC services in txt format.
        // More information on the registry available: http://code.google.com/p/psicquic/wiki/Registry
        final URL url =
                new URL( "http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=ACTIVE&format=txt" );

        BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
        String str;
        Map<String, String> psiqcuicServices = new HashMap<String, String>();
        while ( ( str = in.readLine() ) != null ) {
            final int idx = str.indexOf( '=' );
            psiqcuicServices.put( str.substring( 0, idx ), str.substring( idx + 1, str.length() ));
        }
        in.close();

        // Query services
        System.out.println( "Found " + psiqcuicServices.size() + " active service(s)." );
        for ( Object o : psiqcuicServices.keySet() ) {
            String serviceName = ( String ) o;
            String serviceUrl = psiqcuicServices.get( serviceName );
            System.out.println( serviceName + " -> " + serviceUrl );

            // change the endpoint address as needed
            UniversalPsicquicClient client = new UniversalPsicquicClient( serviceUrl );

            SearchResult<?> result = client.getByInteractor( "brca2", 0, 50 );

            // Print the results in the console
            System.out.println( "Interactions found: " + result.getTotalCount() );

            for ( BinaryInteraction binaryInteraction : result.getData() ) {
                String interactorIdA = binaryInteraction.getInteractorA().getIdentifiers().iterator().next().getIdentifier();
                String interactorIdB = binaryInteraction.getInteractorB().getIdentifiers().iterator().next().getIdentifier();

                String interactionAc = "-";
                if( ! binaryInteraction.getInteractionAcs().isEmpty() ) {
                    CrossReference cr = ( CrossReference ) binaryInteraction.getInteractionAcs().iterator().next();
                    interactionAc = cr.getIdentifier();
                }

                System.out.println( "\tInteraction (" + interactionAc + "): " + interactorIdA + " interacts with " + interactorIdB );
            }
        }
    }
}