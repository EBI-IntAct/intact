package uk.ac.ebi.intact.application.graph2MIF;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.Constants;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.persistence.DAOFactory;
import uk.ac.ebi.intact.persistence.DAOSource;
import uk.ac.ebi.intact.simpleGraph.Graph;
import uk.ac.ebi.intact.application.graph2MIF.Graph2MIF;
import uk.ac.ebi.intact.application.graph2MIF.GraphNotConvertableException;
import uk.ac.ebi.intact.application.graph2MIF.NoGraphRetrievedException;
import uk.ac.ebi.intact.application.graph2MIF.NoInteractorFoundException;
import uk.ac.ebi.intact.application.graph2MIF.MIFSerializeException;
import uk.ac.ebi.intact.persistence.DataSourceException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  Graph2MIFWSService Implementation
 *
 *  This is the implementation of The graph2MIF-WebService.
 *  Interface defined in Graph2MIFWS.java
 *
 *  @author Henning Mersch <hmersch@ebi.ac.uk>
 */
public class Graph2MIFWSService implements Graph2MIFWS {

    //The IntactHelper for retrieving information from IntAct Database.
	private IntactHelper helper;
	//Contructor creates the IntactHelper
	public Graph2MIFWSService()  throws DataSourceException, IntactException {
	       helper = new IntactHelper();
		   System.out.println("Helper created");
	}

    /**
	 * getMIF is the only method which is necessary for access.
	 * @param ac String ac in IntAct
	 * @param depth Integer of the depth the graph should be expanded
	 * @return String including a XML-Document in PSI-MIF-Format
	 * @exception IntactException thrown if search for interactor failed
	 * @exception GraphNotConvertableException thrown if Graph failed requirements of MIF.
	 * @exception DataSourceException thrown if could not retrieve graph from interactor
	 * @exception NoGraphRetrievedException thrown if DOM-Object could not be serialized
	 * @exception MIFSerializeException thrown if IntactHelper could not be created
	 * @exception NoInteractorFoundException thrown if no Interactor found for ac
	 */
    public String getMIF(String ac, Integer depth, Boolean strictmif)
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
	  graph = graph2mifwsservice.getInteractionNetwork(ac, depth); //NoGraphRetrievedExceptioni, IntactException and NoInteractorFoundException possible
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
	  if (interactors.size() == 1 ) {
	  interactor = (Interactor)interactors.toArray()[0]; // just take the 1st element - there should be no 2nd if searche for an ac !
	  } else { //No Interactor found
		System.out.println("msg: No Interactor found");
	    throw new NoInteractorFoundException();
	  }
	  // retrieve the graph with interactor as root element and given depth
	  Graph graph = new Graph();
      if(graph == null) {
          System.out.println("retrieved graph == null");
          throw new NoGraphRetrievedException();
      }
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
