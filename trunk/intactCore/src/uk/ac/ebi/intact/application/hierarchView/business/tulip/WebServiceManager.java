/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.hierarchView.business.tulip;

import org.apache.axis.utils.Options;
import org.apache.axis.client.AdminClient;

import java.io.InputStream;

/**
 * Allow to deploy and undeploy the web service
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */

public class WebServiceManager {

    private String deploymentFile;
    private String undeploymentFile;

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
     * Undeploy the web service according to collected data
     * (i.e. undeployment file name)
     */
    public void undeploy () throws Exception {

        processWsddFile (this.undeploymentFile);

    } // undeploy


    /**
     * deploy the web service according to collected data
     * (i.e. deployment file name)
     */
    public void deploy () throws Exception {

        processWsddFile (this.deploymentFile);

    } // deploy


    /**
     * process the WSDD file given in parameter
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
                    throw (new Exception ("Unable to process the WSDD file: " + wsddFile));
                }
            }
            catch (Exception e) {
                // REGISTRATION ERROR;
                throw (e);
            }
        }
        else
            throw (new Exception ("Unable to open the WSDD file: " + wsddFile));
    }  // processWsddFile

}  // WebServiceManager
