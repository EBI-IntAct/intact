/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.commons.logging;

import org.apache.log4j.PropertyConfigurator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Inititialize the Logger with the log4J properties file.
 *
 * Created by Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class LoggingInitServlet extends HttpServlet {

    /**
     * You should include the folowing lines in your web.xml file to load that
     * servlet on Tomcat startup.
     *
     * <p>
     * &lt;servlet&gt;<br>
     *   &lt;servlet-name>logging-init&lt;/servlet-name&gt;<br>
     *   &lt;servlet-class&gt;uk.ac.ebi.intact.application.application.commons.logging.LoggingInitServlet&lt;/servlet-class&gt;
     *   <br>
     *   &lt;init-param&gt;<br>
     *     &lt;param-name>log4j-init-file&lt;/param-name&gt;<br>
     *     &lt;param-value&gt;WEB-INF/classes/config/log4j.properties&lt;/param-value&gt;<br>
     *   &lt;/init-param&gt;<br>
     *   <br>
     *   &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;<br>
     * &lt;/servlet&gt;<br>
     * </p>
     */
    public void init() {
        String prefix =  getServletContext().getRealPath("/");
        String configFile = getInitParameter("log4j-init-file");

        // if the logging-init-file is not set (cf. web.xml), then no point in trying
        if (null != configFile) {
            PropertyConfigurator.configure (prefix + configFile);
            // configureAndWatch(String configFilename, long delay_in_milliseconds)
        }
    } // init

    public void doGet(HttpServletRequest req, HttpServletResponse res) {
    }
} // LoggingInit

