/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.graphic;

import uk.ac.ebi.intact.application.statisticView.business.StatGraphConstants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Allows to draw evolution of the quantity of interactions stored into IntAct.
 *
 * This class is particular because it needs to draw 2 curves in the same graphic:
 *      one curve represents the binary interactions evolution
 *      the other represents the complex interactions evolution
 *
 * That's why the implementation is a little bit different
 * from the other inherited class of the GraphSkeleton class
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class GraphInteraction extends GraphSkeleton {

    //---------- CONSTANTS -------------------//

    private final static int DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_RED   = 219;
    private final static int DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_GREEN = 67;
    private final static int DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_BLUE  = 90;

    private final static int DEFAULT_COLOR_FILLCURVE_COMPLEXES_RED   = 232;
    private final static int DEFAULT_COLOR_FILLCURVE_COMPLEXES_GREEN = 143;
    private final static int DEFAULT_COLOR_FILLCURVE_COMPLEXES_BLUE  = 157;

    private final static int COLUMN_BININT_NUMBER_STAT_TABLE = 3;
    private final static int COLUMN_COMPLEXINT_NUMBER_STAT_TABLE = 4;

    // The both collection needed to draw 2 curves
    private Collection binaryInteractionsList = null;
    private Collection complexInteractionsList = null;



    //---------- CONSTRUCTOR -------------------//

    public GraphInteraction() {
        super();
        titleString = StatGraphConstants.TITLE_GRAPH_INTERACTION;
        // statisticListFromDatabase = DataManagement.getProteinNumberList();
    }


    //---------- ABSTRACT METHODS IMPLEMENTATION -------------------//

    protected Collection getStatistics () {
        ArrayList list =  new ArrayList (2);

        binaryInteractionsList  = getSelectedStatistics(COLUMN_BININT_NUMBER_STAT_TABLE);
        complexInteractionsList = getSelectedStatistics(COLUMN_COMPLEXINT_NUMBER_STAT_TABLE);

        list.add (binaryInteractionsList);
        list.add (complexInteractionsList);

        return list;
    }

    // in this case, 2 curves to draw
    protected void drawCurves() {

        Color theBinColor = new Color (DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_RED,
                                       DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_GREEN,
                                       DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_BLUE);

        this.drawCurve (binaryInteractionsList, theGreatestYaxis, theLowestYaxis, theBinColor);


        Color theCompColor = new Color (DEFAULT_COLOR_FILLCURVE_COMPLEXES_RED,
                                        DEFAULT_COLOR_FILLCURVE_COMPLEXES_GREEN,
                                        DEFAULT_COLOR_FILLCURVE_COMPLEXES_BLUE);

        this.drawCurve (complexInteractionsList, theGreatestYaxis, theLowestYaxis, theCompColor);
    }

}
