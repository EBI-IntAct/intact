/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.util;

import org.apache.log4j.PropertyConfigurator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;

import uk.ac.ebi.intact.core.CvContext;
import uk.ac.ebi.intact.core.ExternalContext;

/**
 * Inititialize the Logger with the log4J properties file.
 * <p/>
 * Created by Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class LoggingInitServlet extends HttpServlet {

    /**
     * You should include the folowing lines in your web.xml file to load that servlet on Tomcat startup.
     * <p/>
     * <p/>
     * &lt;servlet&gt;<br> &lt;servlet-name>logging-init&lt;/servlet-name&gt;<br> &lt;servlet-class&gt;uk.ac.ebi.intact.application.application.commons.logging.LoggingInitServlet&lt;/servlet-class&gt;
     * <br> &lt;init-param&gt;<br> &lt;param-name>log4j-init-file&lt;/param-name&gt;<br>
     * &lt;param-value&gt;/WEB-INF/classes/config/log4j.properties&lt;/param-value&gt;<br> &lt;/init-param&gt;<br> <br>
     * &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;<br> &lt;/servlet&gt;<br> </p>
     */
    public void init() {

        /* Get parameter's value in the web.xml file */
        String configFile = getInitParameter( "log4j-init-file" );
        URL configUrl = null;

        if ( configFile != null ) {
            try {
                configUrl = getServletContext().getResource( configFile );
            } catch ( MalformedURLException e ) {
                System.out.println( "LOGGING INIT: Couldn't get the logging file path from resource " + configFile );
                return;
            }
        } else {
            System.out.println( "LOGGING INIT: configuration file could not be found (" + configFile + ")." );
            return;
        }

        /* Load the configuration file */
        if ( configUrl != null ) {
            System.out.println( "LOGGING INIT: load logging properties file " + configUrl.toString() );
            PropertyConfigurator.configure( configUrl );
        } else {
            System.out.println( "LOGGING INIT: could not load logging properties file " + configFile );
        }

        // configure ExternalContext
        ExternalContext.newInstance(getServletContext());
        // initialize the CvContext, which contains the constant CvObjects
        CvContext.getCurrentInstance();

        /* For eventual later use, allow to reload the config at regular interval of time */
        // configureAndWatch(String configFilename, long delay_in_milliseconds)
    }

    public void doGet( HttpServletRequest req, HttpServletResponse res ) {
    }
}
