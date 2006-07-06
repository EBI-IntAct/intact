/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.highlightment.behaviour;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.hierarchView.business.Constants;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.business.image.Utilities;
import uk.ac.ebi.intact.simpleGraph.Node;

import java.awt.*;
import java.util.Properties;

/**
 * Behaviour class allowing to change the color of highlighted proteins.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class ColorHighlightmentBehaviour extends HighlightmentBehaviour {

    static Logger logger = Logger.getLogger (Constants.LOGGER_NAME);

    /********** CONSTANTS ************/
    private final static int DEFAULT_COLOR_HIGHLIGHTING_RED   = 255;
    private final static int DEFAULT_COLOR_HIGHLIGHTING_GREEN = 0;
    private final static int DEFAULT_COLOR_HIGHLIGHTING_BLUE  = 0;
    private final static String DEFAULT_COLOR_HIGHLIGHTING    = DEFAULT_COLOR_HIGHLIGHTING_RED + "," +
                                                                DEFAULT_COLOR_HIGHLIGHTING_GREEN + "," +
                                                                DEFAULT_COLOR_HIGHLIGHTING_BLUE;


    /**
     * Apply the implemented behaviour to the specific Node of the graph.
     * Here, we change the color of the highlighted node.
     *
     * @param aProtein the node on which we want to apply the behaviour
     */
    public void applyBehaviour (Node aProtein) {

        // read the Graph.proterties file
        Properties properties = IntactUserI.GRAPH_PROPERTIES;

        String colorString = null;

        if (null != properties) {
            colorString = properties.getProperty ("hierarchView.color.highlighting");
        }

        if (null == colorString) {
            colorString = DEFAULT_COLOR_HIGHLIGHTING;
            logger.warn ("Unable to find the property hierarchView.color.highlighting in " + Constants.PROPERTY_FILE);
        }


        Color color = Utilities.parseColor (colorString,
                                            DEFAULT_COLOR_HIGHLIGHTING_RED,
                                            DEFAULT_COLOR_HIGHLIGHTING_GREEN,
                                            DEFAULT_COLOR_HIGHLIGHTING_BLUE);

        aProtein.put(Constants.ATTRIBUTE_COLOR_LABEL, color);

    }
}