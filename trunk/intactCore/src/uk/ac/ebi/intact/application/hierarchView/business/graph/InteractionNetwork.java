package uk.ac.ebi.intact.application.hierarchView.business.graph;
 
// intact
import uk.ac.ebi.intact.simpleGraph.*;
import uk.ac.ebi.intact.model.*;

// hierarchView
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;

import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.TulipClient;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.*;
import uk.ac.ebi.intact.application.hierarchView.business.image.*;

// JDK
import java.util.*;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.awt.Color;
 
public class InteractionNetwork extends Graph {


  /*********************************************************************** Constants */
  private static int DEFAULT_COLOR_NODE_RED   = 0;
  private static int DEFAULT_COLOR_NODE_GREEN = 0;
  private static int DEFAULT_COLOR_NODE_BLUE  = 255;
  private static String DEFAULT_COLOR_NODE = DEFAULT_COLOR_NODE_RED + "," +
                                             DEFAULT_COLOR_NODE_GREEN + "," + 
                                             DEFAULT_COLOR_NODE_BLUE;

  private static int DEFAULT_COLOR_LABEL_RED   = 255;
  private static int DEFAULT_COLOR_LABEL_GREEN = 255;
  private static int DEFAULT_COLOR_LABEL_BLUE  = 255;
  private static String DEFAULT_COLOR_LABEL = DEFAULT_COLOR_LABEL_RED + "," +
                                              DEFAULT_COLOR_LABEL_GREEN + "," + 
                                              DEFAULT_COLOR_LABEL_BLUE;

  private static String DEFAULT_SIZE_LENGTH = "40";
  private static String DEFAULT_SIZE_HEIGHT = "40";



  /*********************************************************************** Instance variables */

  /**
   * Allow to record nodes and keep their order
   */
  private ArrayList nodeList;
  private boolean   isInitialized;

  /**
   * Properties of the final image (size ...)
   */
  private ImageDimension dimension;




  /*********************************************************************** Methods */
  /**
   * Constructor
   */
  public InteractionNetwork()
  {
    this.dimension     = new ImageDimension();
    this.isInitialized = false;
  }
  

  /**
   * Initialization of the interaction network
   * Record the nodes in a list which allows to keep an order ...
   */
  public void init () {
    HashMap myNodes = super.getNodes();

    // create a new collection (ordered) to allow a indexed access to the node list
    this.nodeList = new ArrayList (myNodes.values());
    this.isInitialized = true;
  }


  /**
   * add the initialization part to the super class method
   *
   * @param anInteractor the interactor to add in the graph
   * @return the created Node
   */
  public Node addNode(Interactor anInteractor){
    Node aNode = super.addNode (anInteractor);

    // initialization of the node
    if (null != aNode) {
      // String label = anInteractor.getShortLabel();
      String label = anInteractor.getAc ();
      aNode.put (Constants.ATTRIBUTE_LABEL,label);

      this.initNodeDisplay (aNode);
    }

    return aNode;
  } // addNode
  

  /**
   * return a list of nodes ordered 
   */
  public ArrayList getOrderedNodes () {
    return this.nodeList;
  }


  /**
   * Allow to put the default color and default visibility for each 
   * protein of the interaction network
   */
  public void initNodes()
  {
    NodeI aNode;
    HashMap  someNodes = super.getNodes();
    Iterator iterator = someNodes.values().iterator ();

    while (iterator.hasNext ()) {
      aNode = (NodeI) ((Entry) iterator.next ()).getValue();
      initNodeDisplay (aNode);
    }

  } // initNodes


  /**
   * initialisation of one Node about its color, its visible attribute
   *
   * @param aNode the node to update
   */
  public void initNodeDisplay (NodeI aNode) {

    // read the ApplicationResource.proterties file 
    Properties properties = PropertyLoader.load (Constants.PROPERTY_FILE);
    
    String stringColorNode  = null;
    String stringColorLabel = null; 
    
    if (null != properties) {
      stringColorNode  = properties.getProperty ("hierarchView.image.color.default.node");
      stringColorLabel = properties.getProperty ("hierarchView.image.color.default.label");
    }
    
    if (null == stringColorNode)  stringColorNode  = DEFAULT_COLOR_NODE;
    if (null == stringColorLabel) stringColorLabel = DEFAULT_COLOR_LABEL;
    
    Color colorNode = Utilities.parseColor(stringColorNode, 
					   DEFAULT_COLOR_NODE_RED, 
					   DEFAULT_COLOR_NODE_GREEN,
					   DEFAULT_COLOR_NODE_BLUE);
    
    ((Node) aNode).put (Constants.ATTRIBUTE_COLOR_NODE, colorNode);

    Color colorLabel = Utilities.parseColor(stringColorLabel, 
					    DEFAULT_COLOR_LABEL_RED, 
					    DEFAULT_COLOR_LABEL_GREEN,
					    DEFAULT_COLOR_LABEL_BLUE);
    
    ((Node) aNode).put (Constants.ATTRIBUTE_COLOR_LABEL, colorLabel);    
    ((Node) aNode).put (Constants.ATTRIBUTE_VISIBLE,new Boolean (true));

  } // initNode


  /**
   * Return the number of node
   *
   * @return the number of nodes
   */
  public int sizeNodes () {
    return super.getNodes().size();
  }

  
  /**
   * Return the number of edge 
   *
   * @return the number of edge
   */
  public int sizeEdges () {
    return super.getEdges().size();
  }


  /**
   * Return the object ImageDimension which correspond to the graph
   *
   * @return an object ImageDimension
   */
  public ImageDimension getImageDimension () {
    return this.dimension;
  }


  /**
   * Create a String giving informations for the Tulip treatment
   * the informations are :
   *       - the number of nodes,
   *       - the whole of the edges and nodes associeted for each edge,
   *       - the label of each node.
   *
   * @return an object String
   */
  public String exportTlp () {

    EdgeI edge;
    StringBuffer out        = new StringBuffer();
    String separator = System.getProperty ("line.separator");
    int i;

    if (false == this.isInitialized)
      this.init();

    out.append("(nodes ");
    
    for (i = 1; i <= this.nodeList.size(); i++)
      out.append(i + " ");
    
    out.append(")" + separator);
    

    Vector  myEdges = (Vector)  super.getEdges();

    for (i = 1; i <= sizeEdges(); i++) {

      edge = (EdgeI) myEdges.get (i - 1);
      out.append("(edge "+ i + " "  + 
		 (this.nodeList.indexOf (edge.getNode1 ()) + 1) + " " +
		 (this.nodeList.indexOf (edge.getNode2 ()) + 1) + ")" + 
		 separator);
    }

    return out.toString();
  } // exportTlp



  /**
   * Send a String to Tulip to calculate coordinates
   * Enter the obtained coordinates in the graph.
   * 
   * @param dataTlp The obtained String by the exportTlp() method 
   */
  public void importDataToImage (String dataTlp) throws IOException {

    ProteinCoordinate[] result;
    TulipClient client  = new TulipClient();
    
    // Call Tulip Web Service
    if (!client.isReady()) throw new IOException ("Unable to create Tulip Web service"); 
    
    // get coordinates
    result    = client.getComputedTlpContent(dataTlp);

    if (null == result) {
      throw new IOException ("Tulip send back no data.");
    } else {
      // update protein coordinates
      for (int i = 0; i < result.length; i++) {
	ProteinCoordinate p = result[i];
	
	Float x  = new Float (p.getX());
	Float y  = new Float (p.getY());
	
	// nodes are labelled from 1 to n int the tlp file and from 0 to n-1 int the collection.
	Node protein = (Node) this.nodeList.get (p.getId() - 1);
	
	// Store coordinates in the protein
	protein.put (Constants.ATTRIBUTE_COORDINATE_X, x);
	protein.put (Constants.ATTRIBUTE_COORDINATE_Y, y);
      } // for
    } // else
    
  } //importDataToImage

} // InteractionNetwork




