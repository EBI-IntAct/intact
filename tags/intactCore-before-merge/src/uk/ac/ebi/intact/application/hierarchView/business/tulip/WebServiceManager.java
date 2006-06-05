/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.hierarchView.business.tulip;

import org.apache.axis.client.AdminClient;
import org.apache.axis.utils.Options;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Allows to deploy and undeploy the web service.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class WebServiceManager implements ServletContextListener {

    private static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    private String deploymentFile   = null;
    private String undeploymentFile = null;

    private boolean started = false;


    public String getDeploymentFile() {
        return deploymentFile;
    }

    public void setDeploymentFile(String deploymentFile) {
        this.deploymentFile = deploymentFile;
    }

    public String getUndeploymentFile() {
        return undeploymentFile;
    }

    public void setUndeploymentFile(String undeploymentFile) {
        this.undeploymentFile = undeploymentFile;
    }


    /**
     * Tells is the Manager is properly initialized.
     * @return true is the manager is initialized, false in the other way round.
     */
    public boolean isInitialized () {
        return ((null != deploymentFile) && (null != undeploymentFile));
    }

    /**
     * Initialize the web service manager by reading the properties file
     * and setting needed files names.
     * @throws Exception
     */
    public void init () throws Exception {

        logger.info ("Loading web service's properties");
        Properties props = IntactUserI.WEB_SERVICE_PROPERTIES;

        if (null != props) {
            String deploymentFile   = props.getProperty ("webService.deployment");
            String undeploymentFile = props.getProperty ("webService.undeployment");

            logger.info ("Properties Loaded :" +
                         "\nwebService.deployment = "   + deploymentFile +
                         "\nwebService.undeployment = " + undeploymentFile);

            if ((null == deploymentFile) || (null == undeploymentFile)) {
                String msg = "Error, can't initialize WebServiceManager : unable to read properties file.";
                logger.error (msg);
                throw new Exception (msg);
            }

            this.setDeploymentFile (deploymentFile);
            this.setUndeploymentFile (undeploymentFile);
            logger.info ("Properties loaded.");
        } else {
            logger.warn ("Unable to load properties file: " + StrutsConstants.WEB_SERVICE_PROPERTY_FILE);
        }
    } // init


    /**
     * Tells is the web service is running.
     * @return true is the web service is running, or false in the other way round.
     */
    public boolean isRunning () {
        return started;
    }

    /**
     * Undeploy the web service according to collected data
     * (ie undeployment file name).
     */
    public void undeploy () throws Exception {

        try {
            if (false == isInitialized()) init();
            processWsddFile (this.undeploymentFile);
            // web service stopped
            started = false;
        } catch (Exception e) {
            throw e;
        }
    } // undeploy


    /**
     * deploy the web service according to collected data
     * (ie deployment file name).
     */
    public void deploy () throws Exception {

        try {
            if (false == isInitialized()) init();
            processWsddFile (this.deploymentFile);
            // web service started
            started = true;
        } catch (Exception e) {
            throw e;
        }
    } // deploy


    /**
     * process the WSDD file given in parameter.
     *
     * @param wsddFile can be a deployment or undeployment file
     * @throws Exception
     */
    public void processWsddFile (String wsddFile) throws Exception {
        String opts[] = new String[1];
        opts[0] = "";
        Options serviceOptions;

        try {
            serviceOptions = new Options(opts);
        }
        catch (Exception e) {
            serviceOptions = null;
        }

        AdminClient axisAdmin = new AdminClient();
        InputStream wsddStream = this.getClass().getResourceAsStream (wsddFile);

        if (wsddStream != null) {
            try {
                String reg = axisAdmin.process(serviceOptions, wsddStream);
                if (reg != null) {
                    // SUCCESS
                    return;
                }
                else {
                    // FAILURE
                    Exception e = new Exception ("Unable to process the WSDD file: " + wsddFile);
                    throw (e);
                }
            }
            catch (Exception e) {
                // REGISTRATION ERROR;
                throw (e);
            }
        } else {
            Exception e = new Exception ("Unable to open the WSDD file: " + wsddFile);
            throw (e);
        }

    }  // processWsddFile


    // ****************************************
    // Implementation of ServletContextListener
    public void contextInitialized (ServletContextEvent event) {
    }


    public void contextDestroyed (ServletContextEvent event) {

        // UNDEPLOY THE WEB SERVICE
        // Could throw an exception if Axis is running on the same Tomcat server,
        // seems that connection with Axis are already closed when we try to stop
        // the web service.
        try {
            this.undeploy();
            logger.info ("Tulip web service undeployed successfully");
        } catch (Exception e) {
            logger.error ("Unable to undeploy the web service", e);
        }
    } // contextDestroyed

}  // WebServiceManager
