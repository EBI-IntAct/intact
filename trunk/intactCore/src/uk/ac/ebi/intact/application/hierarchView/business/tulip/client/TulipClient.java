package uk.ac.ebi.intact.application.hierarchView.business.tulip.client;

import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccess;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessService;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.TulipAccessServiceLocator;
import uk.ac.ebi.intact.application.hierarchView.struts.Constants;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;


/**
 * 
 * Provide an access to the Tulip web service.
 * Wrap methods declared in the web service.
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */


public class TulipClient {

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

            Properties properties = PropertyLoader.load (Constants.WEB_SERVICE_PROPERTY_FILE);
            String tulipAdress = null;
            if (null != properties) {
                tulipAdress = properties.getProperty ("webService.adress");
                System.out.println (" adress=" + tulipAdress);
            } else {
                tulip = null;
                return;
            }

            URL tulipUrl = new URL (tulipAdress);

            // Now use the service to get a stub
            tulip = service.getTulip (tulipUrl);

        } catch (MalformedURLException e) {
            tulip = null;
        }
        catch (javax.xml.rpc.ServiceException se) {
            tulip = null;
        }

    } // constructor


    /**
     * allows to know if the service is ready or not.
     * BUG : that method isn't reliable !
     *
     * @return is the webService is rubbubg
     */
    public boolean isReady () {
        if (null == tulip) {
            return false;
        }
        return true;
    } // isReady


    /**
     * allows to compute a tlp content
     *
     * @param tlpContent the tlp content to compute
     * @return the collection of protein coordinates
     */
    public ProteinCoordinate[] getComputedTlpContent (String tlpContent) {

        ProteinCoordinate[] pc = null;
        String mask = "0";

        // - LOG -
        System.out.println (tlpContent);

        if (null != tulip) {
            try {
                pc = tulip.getComputedTlpContent (tlpContent, mask);
            } catch (java.rmi.RemoteException se) {
                System.out.println ("Exception during retreiving proteins' coordinate");
                se.printStackTrace ();
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
            return tulip.getErrorMessages (hasToBeCleaned);
        } catch (java.rmi.RemoteException se) {
            // create an error message to display
            String[] errors = new String[1];
            errors[0] = "\n\nError while checking errors.";

            // log exception
            se.printStackTrace ();
            return errors;
        }
    } // getErrorMessages


    /**
     * get the line separator string
     *
     */
    public String getLineSeparator () {
        String result = null;

        //    if (null != tulip) {
//       try {
// 	result = tulip.getLineSeparator();
//       } catch (java.rmi.RemoteException se) {}
//     }

        return result;
    } // getLineSeparator




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

        proteins = client.getComputedTlpContent (content);

        if (null == proteins) {
            System.out.println ("Error during retreiving of proteins coordinates (null).");
            System.exit (1);
        }

        if (0 == proteins.length) {
            System.out.println ("No protein retrived.");
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

