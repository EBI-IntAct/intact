/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.graph;

/**
 * Give a specific behaviour to the generic graph definition
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.commons.search.CriteriaBean;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageDimension;
import uk.ac.ebi.intact.application.hierarchView.business.image.Utilities;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.TulipClient;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.simpleGraph.*;
import uk.ac.ebi.intact.util.Chrono;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.*;


public class InteractionNetwork extends Graph {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    public static final int DEFAULT_MAX_CENTRAL_PROTEIN = 7;

    /*********************************************************************** StrutsConstants */
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

    /*********************************************************************** Instance variables */

    /**
     * Allow to record nodes and keep their order
     */
    private ArrayList nodeList;
    private boolean isInitialized;

    /**
     * Properties of the final image (size ...)
     */
    private ImageDimension dimension;

    /**
     * Protein from which has been created the interaction network.
     */
    private Interactor centralProtein;
    private String centralProteinAC; // avoid numerous call to interactor.getAc()

    /**
     * Stores a set of central nodes and interactor. There is one by fusioned interaction network
     * We need to store both because in the case of an interaction, no node are stored.
     */
    private ArrayList centralNodes;
    private ArrayList centralInteractors;

    /**
     * Describe how the interaction network has been built,
     * from which query strings and what is the associated target
     * e.g. the [ShortLabel ABC] and [Xref DEF]
     * That collection contains String[2] (0:queryString, 1:target)
     */
    private ArrayList criteriaList;


    /**
     * Constructor
     */
    public InteractionNetwork (Interactor aCentralProtein) {
        Collection xrefs = aCentralProtein.getXrefs();
        logger.info("Create an Interaction Network with centralProtein:" + aCentralProtein.getAc() + " #xref=" + (xrefs==null?0:xrefs.size()));
        centralProtein     = aCentralProtein;
        centralProteinAC   = aCentralProtein.getAc();
        criteriaList       = new ArrayList();
        centralNodes       = new ArrayList();
        centralInteractors = new ArrayList();
        centralInteractors.add( aCentralProtein ); // can be an Interaction or a Protein

        // wait the user to add some node to reference the central one
        dimension     = new ImageDimension();
        isInitialized = false;
    }

    private static String maxCentralProtein = null;
    public static int getMaxCentralProtein() {

        if ( maxCentralProtein == null ) {
            maxCentralProtein = IntactUserI.GRAPH_PROPERTIES.getProperty( "hierarchView.graph.max.cental.protein" );
        }

        try {
            return Integer.parseInt( maxCentralProtein );
        } catch ( NumberFormatException e ) {
            return DEFAULT_MAX_CENTRAL_PROTEIN;
        }
    }

    public int getCurrentCentralProteinCount(){
        return centralNodes.size();
    }

    public String getCentralProteinAC() {
        return centralProteinAC;
    }

    public Interactor getCentralProtein() {
        return centralProtein;
    }

    public void addCentralProtein (Node node) {
        if ((! centralNodes.contains(node)) && (node != null))
            centralNodes.add (node);
    }

    public ArrayList getCentralProteins () {
        return centralNodes;
    }

    public ArrayList getCentralInteractors () {
        return centralInteractors;
    }

    public ArrayList getCriteria() {
        return criteriaList;
    }

    /**
     * Add a new criteria to the interaction network<br>
     * @param aCriteria the criteria to add if it doesn't exist in the collection already
     */
    public void addCriteria ( CriteriaBean aCriteria ) {
        if ( ! criteriaList.contains( aCriteria ) )
            criteriaList.add( aCriteria );
    }

    /**
     * remove all existing criteria from the interaction network
     */
    public void resetCriteria () {
        criteriaList.clear();
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
        if ( null != aNode ) {
            if( anInteractor.equals( centralProtein ) ) {
                addCentralProtein( aNode );
                // TODO: could add the interactor in a Collection ... would solve the problem of an interaction
            }
            aNode.put (Constants.ATTRIBUTE_LABEL, anInteractor.getAc ());
            initNodeDisplay (aNode);
        }

        isInitialized = false;
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
    public void initNodes() {
        Node aNode;

        HashMap  someNodes = super.getNodes();
        Set keys = someNodes.keySet();
        Iterator iterator = keys.iterator();

        while (iterator.hasNext ()) {
            aNode = (Node) someNodes.get(iterator.next());
            initNodeDisplay (aNode);
        }
    } // initNodes

    /**
     * initialisation of one Node about its color, its visible attribute
     *
     * @param aNode the node to update
     */
    public void initNodeDisplay (NodeI aNode) {

        // read the Graph.proterties file
        Properties properties = IntactUserI.GRAPH_PROPERTIES;

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

        Color colorLabel = Utilities.parseColor (stringColorLabel,
                                                 DEFAULT_COLOR_LABEL_RED,
                                                 DEFAULT_COLOR_LABEL_GREEN,
                                                 DEFAULT_COLOR_LABEL_BLUE);

        ((Node) aNode).put (Constants.ATTRIBUTE_COLOR_LABEL, colorLabel);
        ((Node) aNode).put (Constants.ATTRIBUTE_VISIBLE, new Boolean (true));
    }

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
        /*
         * TODO : could be possible to optimize the size of the string buffer
         *        to avoid as much as possible to extend the buffer size.
         */

        EdgeI edge;
        StringBuffer out = new StringBuffer();
        String separator = System.getProperty ("line.separator");
        int i, max;

        if (false == this.isInitialized)
            this.init();

        out.append("(nodes ");

        max = nodeList.size();
        for (i = 1; i <= max; i++)
            out.append(i + " ");

        out.append(")" + separator);

        ArrayList myEdges = (ArrayList) super.getEdges();

        max = sizeEdges();
        for (i = 1; i <= max; i++) {
            edge = (EdgeI) myEdges.get (i - 1);
            out.append("(edge "+ i + " "  +
                      (nodeList.indexOf (edge.getNode1 ()) + 1) + " " +
                      (nodeList.indexOf (edge.getNode2 ()) + 1) + ")" +
                      separator);
        }

        return out.toString();
    } // exportTlp

    /**
     * Create a String giving informations for the bioLayout EMBL software
     * the informations are just pairwise of protein label.
     *
     * @return an object String
     */
    public String exportBioLayout () {

        EdgeI edge;
        StringBuffer out        = new StringBuffer();
        String separator = System.getProperty ("line.separator");
        int i, max;

        if (false == this.isInitialized)
            this.init();

        Vector  myEdges = (Vector)  super.getEdges();
        max = sizeEdges();
        for (i = 1; i <= max; i++) {
            edge = (EdgeI) myEdges.get (i - 1);
            String label1 = ((Node) edge.getNode1 ()).getLabel () ;
            String label2 = ((Node) edge.getNode2 ()).getLabel () ;

            out.append(label1 + "\t" + label2 + separator);
        }

        return out.toString();
    } // exportBioLayout

    /**
     * Send a String to Tulip to calculate coordinates
     * Enter the obtained coordinates in the graph.
     *
     * @param dataTlp The obtained String by the exportTlp() method
     * @return an array of error message or <b>null</b> if no error occurs.
     */
    public String[] importDataToImage (String dataTlp)
          throws RemoteException {

        if (false == this.isInitialized)
            this.init();

        ProteinCoordinate[] result;
        TulipClient client  = new TulipClient();

        // Call Tulip Web Service : get
        try {
            result = client.getComputedTlpContent(dataTlp);
        } catch (RemoteException e) {
            logger.error("couldn't get coodinate from the TLP content", e);
            throw e;
        }

        if (null == result) {
            // throw new IOException ("Tulip send back no data.");
            String[] errors = client.getErrorMessages (true);
            logger.warn (errors.length + " error(s) returned by the Tulip web service");
            return errors;
        } else {
            // update protein coordinates
            ProteinCoordinate p = null;
            Float x, y;
            Node protein;

            for (int i = 0; i < result.length; i++) {
                p = result[i];

                x  = new Float (p.getX());
                y  = new Float (p.getY());

                // nodes are labelled from 1 to n int the tlp file and from 0 to n-1 int the collection.
                protein = (Node) this.nodeList.get (p.getId() - 1);

                // Store coordinates in the protein
                protein.put (Constants.ATTRIBUTE_COORDINATE_X, x);
                protein.put (Constants.ATTRIBUTE_COORDINATE_Y, y);
            } // for

            logger.info ("Protein coordinates updated");
        } // else

        return null;
    } //importDataToImage

    /**
     * Fusion a interaction network to the current one.<br>
     *
     * For each edge of the new network we check if it exists in the current one.<br>
     * If the edge already exists : continue.<br>
     * If not, we check if the two Nodes of the edge already exists in the current network :
     * <blockquote>
     *     if the node exists, update the edge with its reference<br>
     *     if not add it to the current network
     * </blockquote>
     * finally we add the up-to-date edge to the current network<br>
     *
     * @param network the interaction network we want to fusioned to the current one.
     */
    public void fusion( InteractionNetwork network ) {

        logger.info ("BEGIN fusion");
        Chrono chrono = new Chrono ();
        chrono.start();

        Collection newEdges = network.getEdges();
        Collection edges    = getEdges();
        HashMap    nodes    = getNodes();
        Edge aNewEdge;
        NodeI aNode;
        String ACNode;

        Iterator iterator = newEdges.iterator();
        while (iterator.hasNext()) {
            aNewEdge = (Edge) iterator.next();

            // (!) see also the equals method of Edge
            if (false == edges.contains (aNewEdge)) {
                // check if both nodes are present
                aNode = aNewEdge.getNode1 ();
                ACNode = aNode.getAc();
                // see also the equals method of Node
                if (false == nodes.containsKey(ACNode)) {
                    nodes.put (ACNode, aNode);
                    logger.info ("fusion: add node " + ACNode);
                } else {
                    aNewEdge.setNode1 ((Node) nodes.get(ACNode));
                }

                aNode = aNewEdge.getNode2 ();
                ACNode = aNode.getAc();
                if (false == nodes.containsKey(ACNode)) {
                    nodes.put (ACNode, aNode);
                    logger.info ("fusion: add node " + ACNode);
                } else {
                    aNewEdge.setNode2 ((Node) nodes.get(ACNode));
                }

                edges.add (aNewEdge);
                logger.info ("fusion: add edge " + aNewEdge.getNode1().getAc() + "<->"
                             + aNewEdge.getNode2().getAc());
            }
        }

        // update internal reference
        init();
        initNodes();

        // fusion central proteins
        ArrayList _centralNodes = network.getCentralProteins();
        int max = _centralNodes.size();
        for (int i=0; i<max; i++) {
            Node node = (Node) _centralNodes.get(i);
            /*
             * We get the reference of that node (from the AC)
             * in the current network to keep consistancy.
             */
            Node node2 = (Node) nodes.get(node.getAc());
            // TODO: does it needs to check the non redondancy ?!
            centralNodes.add( node2 );
        }

        // fusion search criteria using the addCriteria method to avois duplicates (addAll would not !)
        Collection criterias = network.getCriteria();
        for ( Iterator iterator2 = criterias.iterator (); iterator2.hasNext (); ) {
            CriteriaBean criteriaBean = (CriteriaBean) iterator2.next ();
            addCriteria( criteriaBean );
        }

        // fusion interactor list
        // TODO: does it needs to check the non redondancy ?!
        centralInteractors.addAll (network.getCentralInteractors());

        chrono.stop();
        String msg = "Network Fusion took " + chrono;
        logger.info( msg );

        logger.info ("END fusion");
    }

} // InteractionNetwork




