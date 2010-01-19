package uk.ac.ebi.intact.kickstart.psicquic;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Reads the registry to identify available PSICQUIC services.
 */
public class QueryRegistry {

    public static void main( String[] args ) throws Exception {

        // Use REST URL to find active PSICQUIC services in txt format.
        // More information on the registry available: http://code.google.com/p/psicquic/wiki/Registry
        final URL url =
                new URL( "http://www.ebi.ac.uk/Tools/webservices/psicquic/registry/registry?action=ACTIVE&format=txt" );

        // Read data
        final InputStream is = url.openStream();
        final Properties services = new Properties();
        services.load( is );
        is.close();

        // Print services
        System.out.println( "Found " + services.size() + " active service(s)."  );
        for ( Object o : services.keySet() ) {
            String key = (String ) o;
            System.out.println( key + " -> " + services.getProperty( key ));
        }
    }
}