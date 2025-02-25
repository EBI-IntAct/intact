/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import java.io.PrintStream;
import java.util.Properties;

/**
 * Setup the HTTP proxy setting in the current JVM.
 *
 * <p>
 * When called, the static method <code>setup()</code> looks up in the
 * classpath for the file:
 * <pre>config/proxy.properties</pre>
 * in order to grab the information related to the HTTP proxy setup.<br>
 * If the property <code>proxy.enable</code> is set to <code>true</code>,
 * we setup the current JVM to use the described configuration.
 * </p>
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 *
 * @see uk.ac.ebi.intact.util.PropertyLoader
 */
public class HttpProxyManager {

    public static final String PROXY_PROPERTIES_FILE = "/config/proxy.properties";

    private HttpProxyManager()
    {
        // never instantiated
    }

    public static class ProxyConfigurationNotFound extends Exception {
        public ProxyConfigurationNotFound ( String message ) {
            super ( message );
        }
    }


    private static Properties proxyProperties;

    private static void loadProxyProperties() throws ProxyConfigurationNotFound {
        if ( proxyProperties == null )
            proxyProperties = PropertyLoader.load (PROXY_PROPERTIES_FILE);

        if (proxyProperties == null) {
             throw new ProxyConfigurationNotFound( "Unable to load the file: " + PROXY_PROPERTIES_FILE );
        }
    }

    /**
     * Setup the HTTP proxy.
     *
     * @param out where to display logging messages. <code>null</code> if you don't want any.
     * @throws ProxyConfigurationNotFound if the proxy.properties file is not found
     */
    public static void setup( PrintStream out ) throws ProxyConfigurationNotFound {
        loadProxyProperties();

        String proxyEnable = proxyProperties.getProperty("proxy.enable", "false");

        if ( proxyEnable.equalsIgnoreCase("true") ) {

            String hostname = proxyProperties.getProperty( "proxy.host" );
            String port     = proxyProperties.getProperty( "proxy.port" );

            System.setProperty( "http.proxySet", "true" );
            System.setProperty( "http.proxyHost", hostname );
            System.setProperty( "http.proxyPort", port );

            if (out != null) out.println ( "Using HTTP proxy setting: " );
            if (out != null) out.println ( "hostname: " + hostname );
            if (out != null) out.println ( "port: " + port );
        } else {

            System.setProperty( "http.proxySet", "false" );

            if (out != null) out.println ( "No proxy setting requested." );
        }
    }

    /**
     * Setup the HTTP proxy.
     * Displays the logging on STDOUT.
     *
     * @throws ProxyConfigurationNotFound if the proxy.properties file is not found
     */
    public static void setup( ) throws ProxyConfigurationNotFound {
        setup(System.out);
    }


}
