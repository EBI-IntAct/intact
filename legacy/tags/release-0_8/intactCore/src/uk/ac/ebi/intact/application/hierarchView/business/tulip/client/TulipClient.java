/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.tulip.client;

import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessService;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessServiceLocator;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.util.Chrono;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;

import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * 
 * Provide an access to the Tulip web service.
 * Wrap methods declared in the web service.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */


public class TulipClient {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /* --------------------------------------------------- Instance variable
    /**
    * Stub to handle the tulip web service
    */
    private TulipAccess tulip;


    /* --------------------------------------------------- Methods
    /**
    * Prepare the web service.
    *
    * @return
    */
    public TulipClient () {
        try {
            // Make a service locator (allow to find the service)
            TulipAccessServiceLocator serviceLocator = new TulipAccessServiceLocator();

            // Ask the service to maintain the session in order to keep values between two call
            serviceLocator.setMaintainSession (true);

            // get a service object
            TulipAccessService service = serviceLocator;

            // Look in the property file where is the web service
            Properties properties = IntactUserI.WEB_SERVICE_PROPERTIES;
            String tulipAdress = null;
            if (null != properties) {
                tulipAdress = properties.getProperty ("webService.adress");
            } else {
                logger.error ("Unable to get WebService properties ... so can't use the web service.");
                tulip = null;
                return;
            }

            if (null == tulipAdress) {
                logger.error ("Unable to find the 'webService.adress' property in " +
                              StrutsConstants.WEB_SERVICE_PROPERTY_FILE);
                tulip = null;
                return;
            } else logger.debug ("Tulip web service URL: " + tulipAdress);

            URL tulipUrl = new URL (tulipAdress);

            // Now use the service to get a stub
            tulip = service.getTulip (tulipUrl);

        } catch (MalformedURLException e) {
            logger.error("The Tulip web service URL is malformed", e);
            tulip = null;
        }
        catch (javax.xml.rpc.ServiceException se) {
            logger.error("Unable to create the Tulip web service", se);
            tulip = null;
        }
    } // constructor


    /**
     * allows to compute a tlp content
     *
     * @param tlpContent the tlp content to compute
     * @return the collection of protein coordinates
     */
    public ProteinCoordinate[] getComputedTlpContent (String tlpContent)
           throws RemoteException {

        ProteinCoordinate[] pc = null;
        String mask = "0";

        logger.info ("TLP content: \n" + tlpContent);

        if (null != tulip) {
            try {
                Chrono chrono = new Chrono ();
                chrono.start();
                pc = tulip.getComputedTlpContent (tlpContent, mask);
                chrono.stop();
                String msg = "Time for computing coordinates " + chrono;
                logger.info(msg);
            } catch (RemoteException re) {
                logger.error ("Unable to retreive proteins' coordinates", re);
                throw re;
            }
        }

        return pc;
    } // getComputedTlpContent


    /**
     * Get the list of messages produce in the web service
     *
     * @param hasToBeCleaned delete all messages after sended them back to the client
     */
    public String[] getErrorMessages (boolean hasToBeCleaned) {
        try {
            logger.error ("Try to retreive from tulip web service error messages");
            return tulip.getErrorMessages (hasToBeCleaned);
        } catch (RemoteException re) {
            // create an error message to display
            String[] errors = new String[1];
            errors[0] = "\n\nError while checking errors.";

            logger.error ("Unable to get error messages", re);
            return errors;
        }
    } // getErrorMessages



    /**********************
     *     TEST METHOD    *
     **********************/
    public static void main (String args[]) {

        TulipClient client = new TulipClient();

        String content = "(nodes 1 2 3 4 5 6 7 8 9 )\n" +
                "(edge 1 7 2)\n" +
                "(edge 2 7 8)\n" +
                "(edge 3 7 9)\n" +
                "(edge 4 7 5)\n" +
                "(edge 5 7 1)\n" +
                "(edge 6 7 3)\n" +
                "(edge 7 7 4)\n" +
                "(edge 8 7 3)\n" +
                "(edge 9 7 6)";

        ProteinCoordinate[] proteins = null;

        try {
            System.out.print ("Check if the web service is running ... ");
            proteins = client.getComputedTlpContent (content);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (null == proteins) {
            System.out.println ("Error during retreiving of proteins coordinates (null).");

            System.out.print ("Try to get service messages ... ");
            String[] msg = client.getErrorMessages (true);
            for (int i = 0; i < msg.length; i++) {
                // display coordinates
                System.out.println ("message "+ i + ") " + msg[i]);
            } // for

            System.exit (1);
        }

        if (0 == proteins.length) {
            System.out.println ("No protein retreived.");
        } else {
            for (int i = 0; i < proteins.length; i++) {
                // display coordinates
                System.out.println (proteins[i].getId() +
                        " \t X=" + proteins[i].getX() +
                        " \t Y=" + proteins[i].getY()
                );
            } // for
        } // else

    } // main







} // TulipClient

