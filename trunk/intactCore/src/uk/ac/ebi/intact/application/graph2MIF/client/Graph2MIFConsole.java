package uk.ac.ebi.intact.application.graph2MIF.client;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.simpleGraph.Graph;
import uk.ac.ebi.intact.application.graph2MIF.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

/**
 *  Graph2MIFConsole
 *
 *  This is a stand alone client of Graph2MIF (without WebService).
 *
 *  @author Henning Mersch <hmersch@ebi.ac.uk>
 */
public class Graph2MIFConsole {

    //line separator for ggivcing out usage
    public static final String NEW_LINE = System.getProperty("line.separator");
    //The IntactHelper for retrieving information from IntAct Database.
    private IntactHelper helper;
    //Contructor creates the IntactHelper
    public Graph2MIFConsole() throws IntactException, DataSourceException {
        helper = new IntactHelper();
    }

    /**
	* main Method, which will be called if the client is started.
	* Prints the PSI-XML data to STDOUT and errors to STDERR.
	*
	* @param args [0] ac: String of the Ac in IntAct Database
	* @param args [1] depth: Integer of the depth the graph should be expanded
    * @param args [2] strict: (true|false) if only strict MIF should be produce
	*/
    public static void main(String[] args) throws DataSourceException, IntactException {
        //give usage if not enough params.
        if (args.length <= 2) {
          System.err.println("Graph2MIFWSClient usage:" + NEW_LINE +
                            "\tjava uk.ac.ebi.intact.application.graph2MIF.client.Graph2MIFWSClient ac depth strict" + NEW_LINE +
                            "\tac\tac in IntAct Database" + NEW_LINE +
                            "\tdepth\tInteger of depth the graph should be expanded" +  NEW_LINE +
                            "\tstrict (true|false) if only strict MIF should be produced" + NEW_LINE +
                            " by Henning Mersch <hmersch@ebi.ac.uk>");
          System.exit(1);
        }
        // call  getMIF  & give out with params to retrieve data
        String ac = args[0];
        Integer depth = new Integer(args[1]);
        Boolean strictmif;
        if (args[2].equals("true")) {strictmif = new Boolean(true);}
        else {strictmif = new Boolean(false);};
        Graph2MIFConsole graph2MIF = new Graph2MIFConsole();
        try {
            //call getMIF to retrieve and convert
            String mif = graph2MIF.getMIF(ac,depth,strictmif);
            //giving result to STDOUT
            System.out.println(mif);
            //or get errors and give out.
        } catch (IntactException e) {
            System.err.println ("ERROR: Search for interactor failed (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (GraphNotConvertableException e) {
            System.err.println ("ERROR: Graph failed requirements of MIF. (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (NoGraphRetrievedException e) {
            System.err.println ("ERROR: Could not retrieve graph from interactor (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (MIFSerializeException e) {
            System.err.println ("ERROR: DOM-Object could not be serialized (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (DataSourceException e) {
            System.err.println ("ERROR: IntactHelper could not be created (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (NoInteractorFoundException e) {
            System.err.println ("ERROR: No Interactor found for this ac (" + e.toString() + ")");
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

    }

    /**
	 * getMIF is the only method which is necessary for access.
	 * @param ac String ac in IntAct
	 * @param depth Integer of the depth the graph should be expanded
	 * @return String including a XML-Document in PSI-MIF-Format
	 * @exception IntactException thrown if search for interactor failed
	 * @exception GraphNotConvertableException thrown if Graph failed requirements of MIF.
	 * @exception DataSourceException thrown if could not retrieve graph from interactor
	 * @exception uk.ac.ebi.intact.application.graph2MIF.NoGraphRetrievedException thrown if DOM-Object could not be serialized
	 * @exception uk.ac.ebi.intact.application.graph2MIF.MIFSerializeException thrown if IntactHelper could not be created
	 * @exception uk.ac.ebi.intact.application.graph2MIF.NoInteractorFoundException thrown if no Interactor found for ac
	 */
    private String getMIF(String ac, Integer depth, Boolean strictmif)
		  throws IntactException,
				 GraphNotConvertableException,
                 NoGraphRetrievedException,
            MIFSerializeException,
				 DataSourceException,
            NoInteractorFoundException {
	  // the serialised DOM-Object - so the returned string.
	  String mif = "";
	  System.out.println("here we go with AC:'"+ac+"', Depth:'"+depth+"'");
	  System.out.println("generating strict MIF: "+strictmif.booleanValue());
	  Graph graph = new Graph();
	  // retrieve Graph
	  Graph2MIFWSService graph2mifwsservice = new Graph2MIFWSService(); //DataSourceException possible
	  graph = getInteractionNetwork(ac, depth); //NoGraphRetrievedExceptioni, IntactException and NoInteractorFoundException possible
	  System.out.println("got graph:");
	  System.out.println(graph);
	  //convert graph to DOM Object
	  Graph2MIF convert = new Graph2MIF(strictmif);
      Document mifDOM = null;
	    mifDOM = convert.getMIF(graph); // GraphNotConvertableException possible
	   // serialize the DOMObject
	  StringWriter w = new StringWriter();
	  OutputFormat of = new OutputFormat(mifDOM, "UTF-8", true); //(true|false) for (un)formated  output
	  XMLSerializer xmls = new XMLSerializer(w, of);
	  try {
		xmls.serialize(mifDOM);
	  } catch (IOException e) {
		System.out.println("msg:"+e.getMessage());
        throw new MIFSerializeException();
	  }
	  mif = w.toString();
	  //return the PSI-MIF-XML
	  return mif;
    }



    /**
	 * getInteractionNetwork retrieves a interactionnetwork (graph) from a given ac and depth
	 * @param ac String ac in IntAct
	 * @param depth Integer of the depth the graph should be expanded
	 * @return graph of the ac with given depth
	 * @exception IntactException thrown if search for interactor failed
	 * @exception NoGraphRetrievedException thrown if DOM-Object could not be serialized
	 * @exception NoInteractorFoundException thrown if no Interactor found for ac
	 */
	private Graph getInteractionNetwork (String ac,Integer depth) throws IntactException, NoInteractorFoundException, NoGraphRetrievedException {
	  //for graph retrieval a interactor is necessary. So get the interactor of given ac.
	  Collection interactors = null;
	  try {
		  interactors = helper.search(Interactor.class.getName(), "ac", ac); //SNU66,
	  } catch (IntactException e) {
		System.out.println("msg:"+e.getMessage());
		throw new IntactException();
	  }
	  Interactor interactor = null;
	  try {
	  interactor = (Interactor)interactors.toArray()[0]; // just take the 1st element - there should be no 2nd if searche for an ac !
	  } catch (ArrayIndexOutOfBoundsException e) {
		System.out.println("msg:"+e.getMessage());
	    throw new NoInteractorFoundException();
	  }
	  // retrieve the graph with interactor as root element and given depth
	  Graph graph = new Graph();
      try {
		graph = helper.subGraph(interactor, depth.intValue(), null, Constants.EXPANSION_BAITPREY, graph);
	  } catch (IntactException e) {
		System.out.println("msg:"+e.getMessage());
        throw new NoGraphRetrievedException();
	  }
	  //return this graph
	  return graph;
	}




}