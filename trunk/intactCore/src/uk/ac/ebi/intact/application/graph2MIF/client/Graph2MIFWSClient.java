package uk.ac.ebi.intact.application.graph2MIF.client;

import org.apache.axis.client.*;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import uk.ac.ebi.intact.util.PropertyLoader;

/**
 * Graph2MIFWSClient
 *
 * This is an example implementation of accessing the WebService "Graph2MIFWS" for export of
 * the IntAct Database as PSI-MIF Format.
 * The resulting PSI-XML will simply writen to STDOUT while occuring errors ging to STDERR
 *
 * See http://psidev.sourceforge.net/ for further information on the PSI-MIF-Format
 *
 * @author Henning Mersch <hmersch@ebi.ac.uk>
 */

public class Graph2MIFWSClient {


    //line separator for giving out usage
    public static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * main Method, which will be called if the client is started.
     * Prints the PSI-XML data to STDOUT and errors to STDERR.
     *
     * @param args [0] ac: String of the Ac in IntAct Database
     * @param args [1] depth: Integer of the depth the graph should be expanded
     * @param args [2] strict: (true|false) if only strict MIF should be produce
     */
    public static void main(String[] args) {
        try {
            //loading properties
            Properties props = PropertyLoader.load("/graph2MIF.properties");
            if (props != null) {
                if (args.length <= 2) {
                    System.err.println("Graph2MIFWSClient usage:" + NEW_LINE +
                            "\tjava uk.ac.ebi.intact.application.graph2MIF.client.Graph2MIFWSClient ac depth strict" + NEW_LINE +
                            "\tac\tac in IntAct Database" + NEW_LINE +
                            "\tdepth\tInteger of depth the graph should be expanded" + NEW_LINE +
                            "\tstrict (true|false) if only strict MIF should be produced" + NEW_LINE +
                            " by Henning Mersch <hmersch@ebi.ac.uk>");

                    System.exit(1);
                }
                URL url = new URL(props.getProperty("webservice.location"));
                // prepare the call (the same for all called methods)
                Call call = (Call) new Service().createCall();
                call.setTargetEndpointAddress(url);
                // if no args are submitted give out usage.
                // call  getMIF  & give out with params to retrieve data
                String ac = args[0];
                Boolean strictmif;
                if (args[2].equals("true")) {
                    strictmif = new Boolean(true);
                } else {
                    strictmif = new Boolean(false);
                }
                try {
                    Integer depth = new Integer(args[1]);
                    call.setMaintainSession(false);
                    call.setOperationName("getMIF");
                    System.out.println(call.invoke(new Object[]{ac, depth, strictmif}));
                } catch (NumberFormatException e) {
                    System.err.println("depth sould be an integer");
                    System.exit(1);
                }
                // error handling with proper information for the user
            } else { //end probs!=null
                System.err.println("Unable to open the properties file.");
                System.exit(1);
            }

        } catch (RemoteException e) {
            //Using this kind of Webservice there is only one one field for giving back a
            //error message. When an axception occours, the client side of Axis will throw
            //an RemoteException which includes the class name of the thrown exception.
            //There is no way to get more information like the original stacktrace !!!
            if (e.toString().equals("uk.ac.ebi.intact.business.IntactException")) {
                System.err.println("ERROR: Search for interactor failed (" + e.toString() + ")");
                System.exit(1);
                    }
            if (e.toString().equals("uk.ac.ebi.intact.application.graph2MIF.GraphNotConvertableException")) {
                System.err.println("ERROR: Graph failed requirements of MIF. (" + e.toString() + ")");
                System.exit(1);
                    }
            if (e.toString().equals("uk.ac.ebi.intact.application.graph2MIF.NoGraphRetrievedException")) {
                System.err.println("ERROR: Could not retrieve graph from interactor (" + e.toString() + ")");
                System.exit(1);
                    }
            if (e.toString().equals("uk.ac.ebi.intact.application.graph2MIF.MIFSerializeException")) {
                System.err.println("ERROR: DOM-Object could not be serialized (" + e.toString() + ")");
                System.exit(1);
                    }
            if (e.toString().equals("uk.ac.ebi.intact.persistence.DataSourceException")) {
                System.err.println("ERROR: IntactHelper could not be created (" + e.toString() + ")");
                System.exit(1);
                    }
            if (e.toString().equals("uk.ac.ebi.intact.application.graph2MIF.NoInteractorFoundException")) {
                System.err.println("ERROR: No Interactor found for this ac (" + e.toString() + ")");
                System.exit(1);
                    }
        } catch (ServiceException e) {
            e.printStackTrace();
            System.exit(1);
                } catch (MalformedURLException e) {
            System.err.println("Wrong URL Format - change graph2MIF.properties");
            System.exit(1);
        }

    }
}
