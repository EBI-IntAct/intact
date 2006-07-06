/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.image;

// intact
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.business.graph.InteractionNetwork;
import uk.ac.ebi.intact.simpleGraph.EdgeI;
import uk.ac.ebi.intact.simpleGraph.Node;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * Purpose :
 * -------
 *            This class allows to tranform a graph to an image (binary content)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */


public class DrawGraph {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);


    /********** CONSTANTS ************/
    private final static String DEFAULT_BORDER_SIZE  = "5";
    private final static String DEFAULT_IMAGE_LENGTH = "300";
    private final static String DEFAULT_IMAGE_HEIGHT = "300";
    private final static String DEFAULT_MAP_NAME     = "networkMap";

    private final static int DEFAULT_COLOR_BACKGROUND_RED   = 255;
    private final static int DEFAULT_COLOR_BACKGROUND_GREEN = 255;
    private final static int DEFAULT_COLOR_BACKGROUND_BLUE  = 255;
    private final static String DEFAULT_COLOR_BACKGROUND    = DEFAULT_COLOR_BACKGROUND_RED + "," +
            DEFAULT_COLOR_BACKGROUND_GREEN + "," +
            DEFAULT_COLOR_BACKGROUND_BLUE;


    private final static int DEFAULT_COLOR_NODE_DEFAULT_RED = 75;
    private final static int DEFAULT_COLOR_NODE_DEFAULT_GREEN = 158;
    private final static int DEFAULT_COLOR_NODE_DEFAULT_BLUE = 179;
    private final static String DEFAULT_COLOR_NODE_DEFAULT = DEFAULT_COLOR_NODE_DEFAULT_RED + "," +
            DEFAULT_COLOR_NODE_DEFAULT_GREEN + "," +
            DEFAULT_COLOR_NODE_DEFAULT_BLUE;

    private final static int DEFAULT_COLOR_EDGE_DEFAULT_RED = 75;
    private final static int DEFAULT_COLOR_EDGE_DEFAULT_GREEN = 158;
    private final static int DEFAULT_COLOR_EDGE_DEFAULT_BLUE = 179;
    private final static String DEFAULT_COLOR_EDGE_DEFAULT = DEFAULT_COLOR_EDGE_DEFAULT_RED + "," +
            DEFAULT_COLOR_EDGE_DEFAULT_GREEN + "," +
            DEFAULT_COLOR_EDGE_DEFAULT_BLUE;

    private final static String DEFAULT_SHAPE_STATE     = "disable";
    private final static String DEFAULT_BORDER_STATE    = "enable";
    private final static int DEFAULT_COLOR_BORDER_RED   = 255;
    private final static int DEFAULT_COLOR_BORDER_GREEN = 255;
    private final static int DEFAULT_COLOR_BORDER_BLUE  = 255;
//    private final static String DEFAULT_COLOR_BORDER    = DEFAULT_COLOR_BORDER_RED + "," +
//            DEFAULT_COLOR_BORDER_GREEN + "," +
//            DEFAULT_COLOR_BORDER_BLUE;

    private final static String DEFAULT_LABEL_FONT_NAME  = "Arial";
    private final static String DEFAULT_LABEL_FONT_SIZE  = "10";

    private final static String DEFAULT_TEXT_ANTIALIASED = "disable";
    private final static String DEFAULT_NODE_ANTIALIASED = "disable";
    private final static String DEFAULT_EDGE_ANTIALIASED = "disable";

    private final static String DEFAULT_EDGE_THICKNESS = "1.0";

    private final static String DEFAULT_INTERNAL_TOP_MARGIN    = "2";
    private final static String DEFAULT_INTERNAL_BOTTOM_MARGIN = "2";
    private final static String DEFAULT_INTERNAL_LEFT_MARGIN   = "5";
    private final static String DEFAULT_INTERNAL_RIGHT_MARGIN  = "5";

    /********** INSTANCE VARIABLES ************/

    /**
     * Allows to store the HTML MAP code we will use with the image we generate
     */
    private StringBuffer  mapCode = null;

    /**
     * where we will write the content of the image
     */
    private BufferedImage bufferedImage;

    /**
     * The name of the HTML MAP
     */
    private String mapName;

    /**
     * allows to know what nodes have been drawn
     */
    private boolean[] drawnNode;

    /**
     * The Interaction Network graph which will allow to create the image
     */
    private InteractionNetwork graph = null;

    /**
     * Size of the image
     */
    private int imageSizex;
    private int imageSizey;

    /**
     *  allow to apply a resizing of the image components
     */
    private float dimensionRateX;
    private float dimensionRateY;

    /**
     *
     */
//    private float[] marginColor;

    /**
     *
     */
    private int borderSize;

    /**
     * Internal margin of a node
     */
    private int internalTopMargin,
    internalBottomMargin,
    internalLeftMargin,
    internalRightMargin;

    /**
     * The used font to write the label
     */
    private Font fontLabel;
    private Font boldFontLabel;

    /**
     * Properties file content
     */
    private Properties properties;

    private Color  backgroundColor;
    private Color  nodeDefaultColor;
    private Color  edgeDefaultColor;
    private Color  borderColor;
    private String borderEnable;

    private int imageLength, imageHeight;

    private String shapeEnable;

    private String edgeAntialiased;
    private String textAntialiased;
    private String nodeAntialiased;

    private float edgeThickness;
    private String applicationPath;

    private String currentTime;


    /**
     * Constructor
     */
    public DrawGraph (InteractionNetwork in, String applicationPath) {
        this.graph = in;
        this.applicationPath = applicationPath;

        // Initialization of mapCode container
        this.mapCode = new StringBuffer();

        // read the ApplicationResource.properties file
        readPropertyFile ();

        // Compute the size of the final image
        updateProteinData (in);

        // Initialization of drawnNode
        int numberOfProtein = in.sizeNodes();
        drawnNode = new boolean [numberOfProtein];

        for(int k = 0 ; k < numberOfProtein; k++){
            drawnNode[k] = false;
        }

        ImageDimension dimension = in.getImageDimension();

        this.dimensionRateX  = dimension.length() / imageLength;
        this.dimensionRateY  = dimension.height() / imageHeight;
        this.imageSizex      = imageLength + borderSize * 2;
        this.imageSizey      = imageHeight + borderSize * 2;
    }



    /**
     * Compute the size of the image according to proteins data (coordinates, size).
     *
     * @param in the interaction network we build the SVG DOM  from
     */
    private void updateProteinData (InteractionNetwork in) {

        FontMetrics fontMetrics = null;

        ArrayList listOfProtein     = graph.getOrderedNodes();
        int numberOfProtein = in.sizeNodes();
        Node protein;

        // that's a fake to precalculate the image size
        bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

        g.setFont (fontLabel);
        fontMetrics = g.getFontMetrics ();

        ImageDimension dimension = in.getImageDimension();
        int j;

        // Update the image dimension according to the proteins coordinates
        for (j = 0; j < numberOfProtein; j++) {
            protein = (Node) listOfProtein.get(j);

            if (((Boolean) protein.get(Constants.ATTRIBUTE_VISIBLE)).booleanValue() == true) {

                // get Tulip coordinate
                float proteinX = ((Float) protein.get(Constants.ATTRIBUTE_COORDINATE_X)).floatValue();
                float proteinY = ((Float) protein.get(Constants.ATTRIBUTE_COORDINATE_Y)).floatValue();

                // update the image dimension
                dimension.adjust (proteinX, proteinY);
            }
        } // for


        // get the central protein in order to apply a different font
        ArrayList centrals = graph.getCentralProteins();

        // update the image dimension according to the proteins coordinates and their size's label
        for (j = 0; j < numberOfProtein; j++) {
            protein = (Node) listOfProtein.get(j);

            if (((Boolean) protein.get(Constants.ATTRIBUTE_VISIBLE)).booleanValue() == true) {
                // get the protein label
                String proteinLabel = protein.getLabel();

                // get Tulip coordinate
                float proteinX = ((Float) protein.get(Constants.ATTRIBUTE_COORDINATE_X)).floatValue();
                float proteinY = ((Float) protein.get(Constants.ATTRIBUTE_COORDINATE_Y)).floatValue();

                if (centrals.contains(protein)) {
                    g.setFont (boldFontLabel);
                } else {
                    g.setFont (fontLabel);
                }

                fontMetrics = g.getFontMetrics ();

                // calculate heigth and width
                float height = fontMetrics.getHeight() +
                               this.internalTopMargin +
                               this.internalBottomMargin;
                float length = fontMetrics.stringWidth(proteinLabel) +
                               this.internalLeftMargin +
                               this.internalRightMargin;

                // The dimension rate depends of the size of the picture.
                // so, we have to calculate at each iteration to keep a right value.
                this.dimensionRateX = dimension.length() / imageLength;
                this.dimensionRateY = dimension.height() / imageHeight;

                // update data in the protein
                protein.put (Constants.ATTRIBUTE_LENGTH, new Float (length));
                protein.put (Constants.ATTRIBUTE_HEIGHT, new Float (height));

                // update the image dimension according to the protein label size
                dimension.adjustCadre (length * dimensionRateX,
                                       height * dimensionRateY,
                                       proteinX,
                                       proteinY);
            }
        } //  for

        g.dispose();
        bufferedImage = null;
    } // updateProteinData



    /**
     * Read all the needed properties and store the values int instance variable.
     * it allows to avoid to scatter this code int the drawing code.
     */
    private void readPropertyFile () {

        this.properties = IntactUserI.GRAPH_PROPERTIES;

        // read the background and border color in the property file
        String stringBgColor     = null;
        String stringBorderColor = null;
        String stringNodeColorDefault = null;
        String stringEdgeColorDefault = null;

        String border       = null;
        String xSize        = null;
        String ySize        = null;
        String fontName     = null;
        String fontSize     = null;
        int    intFontSize  = 0;
        String thicknessStr = null;

        String internalTopMarginStr    = null;
        String internalBottomMarginStr = null;
        String internalLeftMarginStr   = null;
        String internalRightMarginStr  = null;
        this.internalTopMargin = 0;
        this.internalBottomMargin = 0;
        this.internalLeftMargin = 0;
        this.internalRightMargin = 0;

        this.borderEnable    = null;
        this.nodeAntialiased = null;
        this.mapName         = null;
        this.edgeAntialiased = null;
        this.textAntialiased = null;
        this.shapeEnable     = null;

        // read all the needed properties in the file
        if (null != this.properties) {
            stringBgColor             = properties.getProperty ("hierarchView.image.color.default.background");
            stringBorderColor         = properties.getProperty ("hierarchView.image.color.default.border");
            stringNodeColorDefault    = properties.getProperty ("hierarchView.image.color.default.node");
            stringEdgeColorDefault    = properties.getProperty ("hierarchView.image.color.default.edge");
            this.borderEnable         = properties.getProperty ("hierarchView.image.border");
            border                    = properties.getProperty ("hierarchView.image.size.default.border");
            xSize                     = properties.getProperty ("hierarchView.image.size.default.image.length");
            ySize                     = properties.getProperty ("hierarchView.image.size.default.image.height");
            fontName                  = properties.getProperty ("hierarchView.image.font.name.label");
            fontSize                  = properties.getProperty ("hierarchView.image.font.size.label");
            this.mapName              = properties.getProperty ("hierarchView.image.map.name");
            this.edgeAntialiased      = properties.getProperty ("hierarchView.image.edge.antialiased");
            this.textAntialiased      = properties.getProperty ("hierarchView.image.font.antialiased");
            thicknessStr              = properties.getProperty ("hierarchView.image.edge.thickness");
            this.nodeAntialiased      = properties.getProperty ("hierarchView.image.node.antialiased");
            this.shapeEnable          = properties.getProperty ("hierarchView.image.shape");
            internalTopMarginStr      = properties.getProperty ("hierarchView.image.node.internalTopMargin");
            internalBottomMarginStr   = properties.getProperty ("hierarchView.image.node.internalBottomMargin");
            internalLeftMarginStr     = properties.getProperty ("hierarchView.image.node.internalLeftMargin");
            internalRightMarginStr    = properties.getProperty ("hierarchView.image.node.internalRightMargin");
        }

        if (null == stringBgColor) {
            stringBgColor = DEFAULT_COLOR_BACKGROUND;
        }

        this.backgroundColor = Utilities.parseColor (stringBgColor,
                DEFAULT_COLOR_BACKGROUND_RED,
                DEFAULT_COLOR_BACKGROUND_GREEN,
                DEFAULT_COLOR_BACKGROUND_BLUE);

        if (null == stringNodeColorDefault) {
            stringNodeColorDefault = DEFAULT_COLOR_NODE_DEFAULT;
        }

        this.nodeDefaultColor = Utilities.parseColor (stringNodeColorDefault,
                DEFAULT_COLOR_NODE_DEFAULT_RED,
                DEFAULT_COLOR_NODE_DEFAULT_GREEN,
                DEFAULT_COLOR_NODE_DEFAULT_BLUE);

        if (null == stringEdgeColorDefault) {
            stringEdgeColorDefault = DEFAULT_COLOR_EDGE_DEFAULT;
        }

        this.edgeDefaultColor = Utilities.parseColor (stringEdgeColorDefault,
                DEFAULT_COLOR_EDGE_DEFAULT_RED,
                DEFAULT_COLOR_EDGE_DEFAULT_GREEN,
                DEFAULT_COLOR_EDGE_DEFAULT_BLUE);



        if (null == this.borderEnable) {
            this.borderEnable = DEFAULT_BORDER_STATE;
        }

        this.borderColor = Utilities.parseColor (stringBorderColor,
                DEFAULT_COLOR_BORDER_RED,
                DEFAULT_COLOR_BORDER_GREEN,
                DEFAULT_COLOR_BORDER_BLUE);
        if (null == border) {
            border = DEFAULT_BORDER_SIZE;
            /*error log*/
        }

        if (null == xSize) {
            xSize = DEFAULT_IMAGE_LENGTH;
            /*error log*/
        }

        if (null == ySize) {
            ySize = DEFAULT_IMAGE_HEIGHT;
            /*error log*/
        }

        if (null == this.mapName) {
            mapName = DEFAULT_MAP_NAME;
            /*error log*/
        }

        if (null == fontName) {
            fontName = DEFAULT_LABEL_FONT_NAME;
            /*error log*/
        }

        if (null == fontSize) {
            fontSize = DEFAULT_LABEL_FONT_SIZE;
            /*error log*/
        } else {
            try {
                intFontSize = Integer.parseInt(fontSize);
            } catch (NumberFormatException nfe) {
                // parse the default value
                intFontSize = Integer.parseInt(DEFAULT_LABEL_FONT_SIZE);
                /*error log*/
            }
        }

        // make all local fonts available
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        env.getAvailableFontFamilyNames();

        this.fontLabel     = new Font (fontName, Font.PLAIN, intFontSize);
        this.boldFontLabel = new Font (fontName, Font.BOLD, intFontSize+1);
        this.borderSize    = (new Integer(border)).intValue();
        this.imageLength     = (new Integer(xSize)).intValue();
        this.imageHeight     = (new Integer(ySize)).intValue();

        if (null == this.edgeAntialiased) {
            /* Log error */
            this.edgeAntialiased = DEFAULT_EDGE_ANTIALIASED;
        }

        if (null == this.textAntialiased) {
            /* Log error */
            this.textAntialiased = DEFAULT_TEXT_ANTIALIASED;
        }

        if (null == thicknessStr) {
            /* Log error */
            thicknessStr = DEFAULT_EDGE_THICKNESS;
        }

        try {
            this.edgeThickness = Float.parseFloat (thicknessStr);
        } catch (NumberFormatException nfe) {
            this.edgeThickness = Float.parseFloat (DEFAULT_EDGE_THICKNESS);
        }

        if (null == this.nodeAntialiased) {
            /* Log error */
            this.nodeAntialiased = DEFAULT_NODE_ANTIALIASED;
        }

        if (null == this.shapeEnable) {
            /* Log error */
            this.shapeEnable = DEFAULT_SHAPE_STATE;
        }

        if (null == internalTopMarginStr) {
            /* Log error */
            this.internalTopMargin = Integer.parseInt (DEFAULT_INTERNAL_TOP_MARGIN);
        } else {
            try {
                this.internalTopMargin = Integer.parseInt (internalTopMarginStr);
            } catch (NumberFormatException nfe) {
                this.internalTopMargin = Integer.parseInt (DEFAULT_INTERNAL_TOP_MARGIN);
            }
        }

        if (null == internalBottomMarginStr) {
            /* Log error */
            this.internalBottomMargin = Integer.parseInt (DEFAULT_INTERNAL_BOTTOM_MARGIN);
        } else {
            try {
                this.internalBottomMargin = Integer.parseInt (internalBottomMarginStr);
            } catch (NumberFormatException nfe) {
                this.internalBottomMargin = Integer.parseInt (DEFAULT_INTERNAL_BOTTOM_MARGIN);
            }
        }

        if (null == internalLeftMarginStr) {
            /* Log error */
            this.internalLeftMargin = Integer.parseInt (DEFAULT_INTERNAL_LEFT_MARGIN);
        } else {
            try {
                this.internalLeftMargin = Integer.parseInt (internalLeftMarginStr);
            } catch (NumberFormatException nfe) {
                this.internalLeftMargin = Integer.parseInt (DEFAULT_INTERNAL_LEFT_MARGIN);
            }
        }

        if (null == internalRightMarginStr) {
            /* Log error */
            this.internalRightMargin = Integer.parseInt (DEFAULT_INTERNAL_RIGHT_MARGIN);
        } else {
            try {
                this.internalRightMargin = Integer.parseInt (internalRightMarginStr);
            } catch (NumberFormatException nfe) {
                this.internalRightMargin = Integer.parseInt (DEFAULT_INTERNAL_RIGHT_MARGIN);
            }
        }

    } // readPropertyFile


    /**
     * Return the element "dimensionRate"
     *
     * @return the dimension rate
     */
    private float getDimensionRateX(){
        return dimensionRateX;
    }

    private float getDimensionRateY(){
        return dimensionRateY;
    }


    /**
     * Modify the coordinate for an edge.
     * i.e. Convert the Tulip coordinate to the image coordinate (the one we will draw).
     * The coordinate we want to use is in the middle of the node.
     * This modification allows to create a border in the image by applying a shift to the coordinate.
     * @param old The tulip coordinate
     * @param min The coordinate minimal in the graph
     * @param rate The rate
     *
     * @return float
     */
    private float newCoordinateEdge(float old, float min, float rate){
        return (old - min) / rate + borderSize;
    }



    /**
     * Modify the coordinate for a node.
     * i.e. Convert the Tulip coordinate to the image coordinate (the one we will draw).
     * The coordinate we want to use is the upper left corner of the node.
     * This modification allows to create a border in the image by applying a shift to the coordinate.
     *
     * @param old The tulip coordinate
     * @param min The coordinate minimal in the graph
     * @param length The length
     * @param rate The rate
     *
     * @return the new coordinate
     */
    private float newCoordinateNode (float old, float min, float length, float rate) {
        return (old - min) / rate - length/2 + borderSize;
    }


    /**
     * Allows to draw an element "node" in the image
     *
     * @param protein The protein to draw
     * @param g The graphic where we draw
     * @param labelFont the Font with which to draw the label
     */
    private void drawNode (Node protein, Graphics2D g, Font labelFont) {

        String proteinLabel  = protein.getLabel();

        float proteinLength  = ((Float) protein.get(Constants.ATTRIBUTE_LENGTH)).floatValue();
        float proteinHeight  = ((Float) protein.get(Constants.ATTRIBUTE_HEIGHT)).floatValue();
        float proteinX       = ((Float) protein.get(Constants.ATTRIBUTE_COORDINATE_X)).floatValue();
        float proteinY       = ((Float) protein.get(Constants.ATTRIBUTE_COORDINATE_Y)).floatValue();

        ImageDimension dimension = graph.getImageDimension();

        // Convert coordinates from Tulip space to hierarchView space
        float x1 = newCoordinateNode (proteinX,
                                      dimension.xmin(),
                                      proteinLength ,
                                      getDimensionRateX());
        float y1 = newCoordinateNode (proteinY,
                                      dimension.ymin(),
                                      proteinHeight,
                                      getDimensionRateY());

        int x2 = (int) x1 + ((int) proteinLength);
        int y2 = (int) y1 + ((int) proteinHeight);

        if (this.nodeAntialiased.equalsIgnoreCase ("enable")) {
            // Enable antialiasing for shape
            g.setRenderingHint (RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
        }

        if (this.shapeEnable.equalsIgnoreCase ("enable")) {
            Color colorNode = (Color) protein.get(Constants.ATTRIBUTE_COLOR_NODE);
            g.setColor(colorNode);
        } else {
            // create a transparent color to allow to see edges
            Color bg = new Color ( this.backgroundColor.getRed (),
                                   this.backgroundColor.getGreen (),
                                   this.backgroundColor.getBlue (),
                                   180); // opacity : 0=transparent, 255=opaque

            // dont display the node but clear edges to display label
            g.setColor(bg);
        }

        g.fillOval ((int) x1,
                    (int) y1,
                    (int) proteinLength,
                    (int) proteinHeight);

        // In anycase turn the shape antialiasing off.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        // Write the map
        mapCode.append("<AREA SHAPE=\"RECT\" HREF=\""+ applicationPath +"/click.do?AC=" + protein.getAc() +
                currentTime + "\" COORDS=" + (int)x1 + "," + (int)y1 + "," + x2 + "," + y2 + ">");


        // Write label
        g.setFont (labelFont);
        Color colorLabel = (Color) protein.get(Constants.ATTRIBUTE_COLOR_LABEL);
        g.setColor (colorLabel);

        g.drawString (proteinLabel,
                      (int) x1 + this.internalLeftMargin,
                      (int) (y1 + proteinHeight / 2) + this.internalTopMargin);

        // set the protein as drawn
        ArrayList listOfProteins = graph.getOrderedNodes();

        int z = listOfProteins.indexOf(protein);
        if (z != -1)
           drawnNode[z] = true;
    } // drawNode



    /**
     * Allows to draw an element "edge" in the image
     *
     * @param interaction The interaction to draw
     * @param g The graphic where we draw
     */
    private void drawEdge(EdgeI interaction, Graphics2D g) {
        Node proteinR, proteinL;
        float xline1, xline2, yline1, yline2;

        float proteinRx, proteinRy, proteinLx, proteinLy;

        ImageDimension dimension = graph.getImageDimension();

        // proteinRight
        proteinR           = (Node) interaction.getNode1();
        proteinRx          = ((Float) proteinR.get(Constants.ATTRIBUTE_COORDINATE_X)).floatValue();
        proteinRy          = ((Float) proteinR.get(Constants.ATTRIBUTE_COORDINATE_Y)).floatValue();

        // proteinLeft
        proteinL           = (Node) interaction.getNode2();
        proteinLx          = ((Float) proteinL.get(Constants.ATTRIBUTE_COORDINATE_X)).floatValue();
        proteinLy          = ((Float) proteinL.get(Constants.ATTRIBUTE_COORDINATE_Y)).floatValue();

        // calcul
        xline1 = newCoordinateEdge(proteinRx,
                dimension.xmin(),
                getDimensionRateX());

        xline2 = newCoordinateEdge(proteinLx,
                dimension.xmin(),
                getDimensionRateX());

        yline1 = newCoordinateEdge(proteinRy,
                dimension.ymin(),
                getDimensionRateY());

        yline2 = newCoordinateEdge(proteinLy,
                dimension.ymin(),
                getDimensionRateY());

        if (this.edgeAntialiased.equalsIgnoreCase ("enable")) {
            // Enable antialiasing for shape
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g.setColor (this.edgeDefaultColor);

        // draw the edge
        g.drawLine((int)xline1,
                   (int)yline1,
                   (int)xline2,
                   (int)yline2);

        // In anycase turn the antialiasing off.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
    }// drawEdge


    /**
     * drawing process, call methods to draw nodes, edges ...
     *
     */
    public void draw(){
        int i;
        int numberOfProtein     = graph.sizeNodes();
        int numberOfInteraction = graph.sizeEdges();

        currentTime = "&now=" + System.currentTimeMillis(); // this will be added at each link of the graph

        ArrayList listOfProtein  = graph.getOrderedNodes();
        ArrayList listOfInteraction = (ArrayList) graph.getEdges();

        EdgeI interaction;
        Node proteinR, proteinL;

        bufferedImage = new BufferedImage (imageSizex, imageSizey, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();

        // Display the background
        g.setColor (this.backgroundColor);
        g.fillRect (0,
                    0,
                    imageSizex,
                    imageSizey);

        if (textAntialiased.equalsIgnoreCase ("enable")) {
            // Enable antialiasing for text
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            // Disable antialiasing for text
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        Stroke defaultStroke = g.getStroke ();
        BasicStroke stroke   = new BasicStroke (this.edgeThickness);
        g.setStroke (stroke);

        // We draw edges whose nodes are visible
        for(i = 0; i < numberOfInteraction; i++) {
            interaction = (EdgeI) listOfInteraction.get(i);

            proteinR    = (Node) interaction.getNode1();
            proteinL    = (Node) interaction.getNode2();

            // draw edge only if both nodes are visible
            if (((Boolean) proteinR.get(Constants.ATTRIBUTE_VISIBLE)).booleanValue() == true &&
                ((Boolean) proteinL.get(Constants.ATTRIBUTE_VISIBLE)).booleanValue() == true) {
                drawEdge(interaction, g);
            }
        } // for

        // restore the default Stroke (thickness)
        g.setStroke (defaultStroke);

        Node tmp;

        // We draw all visible nodes
        mapCode.append("<MAP NAME=\"" + mapName + "\">");

        // set the central protein as drawn to draw only all others,
        // we'll draw the central protein after
        ArrayList listOfProteins = graph.getOrderedNodes();

        ArrayList centrals = graph.getCentralProteins();
        int max = centrals.size();
        for (int x = 0; x<max; x++) {
            // don't draw central protein now
            Node centralProtein = (Node) centrals.get(x);
            if (centralProtein == null) {
                continue;
            }

            drawnNode[listOfProteins.indexOf(centralProtein)] = true;
        }

        for(int j = 0; j < numberOfProtein; j++) {
            tmp = (Node) listOfProtein.get(j);
            if (drawnNode[j] == false &&
                ((Boolean) tmp.get(Constants.ATTRIBUTE_VISIBLE)).booleanValue() == true) {
                drawNode(tmp, g, fontLabel);
            }
        }

        // Draw the central node with a bold font to be sure it is visible and distinguishable.
        for (int x = 0; x<max; x++) {
            // draw visible central protein over the rest, with bold font.
            Node centralProtein = (Node) centrals.get(x);
            if ( centralProtein == null ) continue;
            if (((Boolean) centralProtein.get(Constants.ATTRIBUTE_VISIBLE)).booleanValue() == true) {
                drawNode(centralProtein, g, boldFontLabel);
            }
        }

        mapCode.append("</MAP>");

        if (this.borderEnable.equals("enable")) {
            g.setColor (borderColor);
            g.drawRect (0,
                        0,
                        imageSizex - 1,
                        imageSizey - 1);
        }

        // release
        g.dispose();
    } // draw


    /**
     * Create an Container with the image and the HTML MAP code.
     *
     * @return the bean initialized with the SVG DOM and the MAP code.
     */
    public ImageBean getImageBean () {

        ImageBean ib = new ImageBean ();
        ib.setMapCode(mapCode.toString());
        ib.setImageHeight(imageSizey);
        ib.setImageWidth(imageSizex);
        ib.setImageData (bufferedImage);

        return ib;
    }
}










