/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.graph;

/**
 * Give a specific behaviour to the generic graph definition
 *
 * @author Samuel Kerrien
 */

// intact
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.image.Utilities;
import uk.ac.ebi.intact.application.hierarchView.business.image.ImageDimension;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.TulipClient;
import uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated.ProteinCoordinate;
import uk.ac.ebi.intact.model.Interactor;
import uk.ac.ebi.intact.simpleGraph.EdgeI;
import uk.ac.ebi.intact.simpleGraph.Graph;
import uk.ac.ebi.intact.simpleGraph.Node;
import uk.ac.ebi.intact.simpleGraph.NodeI;

import java.rmi.RemoteException;
import java.awt.Color;
import java.util.*;

import org.apache.log4j.Logger;


public class InteractionNetwork extends Graph {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

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
    private boolean   isInitialized;

    /**
     * Properties of the final image (size ...)
     */
    private ImageDimension dimension;

    /**
     * Protein from which has been created the interaction network.
     */
    private String centralProteinAC;
    private Node centralProtein;


    /*********************************************************************** Methods */
    /**
     * Constructor
     */
    public InteractionNetwork (String aCentralProteinAC) {
        centralProteinAC = aCentralProteinAC;
        // wait the user to add some node to reference the central one
        centralProtein   = null;
        dimension        = new ImageDimension();
        isInitialized    = false;
    }


    public String getCentralProteinAC() {
        return centralProteinAC;
    }

    public Node getCentralProtein() {
        return centralProtein;
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
            String label = anInteractor.getAc ();
            if (label.equals(centralProteinAC)) {
               centralProtein = aNode;
            }
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
    public void initNodes() {
        Node aNode;

        HashMap  someNodes = super.getNodes();
        Set keys = someNodes.keySet();
        Iterator iterator = keys.iterator();

        while (iterator.hasNext ()) {
            aNode = (Node) someNodes.get(iterator.next());
            this.initNodeDisplay (aNode);
        }
    } // initNodes


    /**
     * initialisation of one Node about its color, its visible attribute
     *
     * @param aNode the node to update
     */
    public void initNodeDisplay (NodeI aNode) {

        // read the Graph.proterties file
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

        Color colorLabel = Utilities.parseColor (stringColorLabel,
                                                 DEFAULT_COLOR_LABEL_RED,
                                                 DEFAULT_COLOR_LABEL_GREEN,
                                                 DEFAULT_COLOR_LABEL_BLUE);

        ((Node) aNode).put (Constants.ATTRIBUTE_COLOR_LABEL, colorLabel);
        ((Node) aNode).put (Constants.ATTRIBUTE_VISIBLE, new Boolean (true));
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
     * Create a String giving informations for the bioLayout EMBL software
     * the informations are just pairwise of protein label.
     *
     * @return an object String
     */
    public String exportBioLayout () {

        EdgeI edge;
        StringBuffer out        = new StringBuffer();
        String separator = System.getProperty ("line.separator");
        int i;

        if (false == this.isInitialized)
            this.init();

        Vector  myEdges = (Vector)  super.getEdges();

        for (i = 1; i <= sizeEdges(); i++) {

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

            logger.info ("Protein coordinates updated");
        } // else

        return null;
    } //importDataToImage

} // InteractionNetwork




