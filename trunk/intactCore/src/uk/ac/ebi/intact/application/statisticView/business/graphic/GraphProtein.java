/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.graphic;

import uk.ac.ebi.intact.application.statisticView.business.StatGraphConstants;

import java.awt.*;
import java.util.Collection;
import java.util.ArrayList;

/**
 * 
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class GraphProtein extends GraphSkeleton {

    //---------- CONSTANTS -------------------//

    private final static int DEFAULT_COLOR_FILLCURVEPROT_RED   = 239;
    private final static int DEFAULT_COLOR_FILLCURVEPROT_GREEN = 134;
    private final static int DEFAULT_COLOR_FILLCURVEPROT_BLUE  = 21;

    private final static int COLUMN_PROT_NUMBER_STAT_TABLE = 1;

    private Collection statistics = null;

    //---------- CONSTRUCTOR -------------------//

    public GraphProtein() {
        super ();
        titleString = StatGraphConstants.TITLE_GRAPH_PROTEIN;
    }

    //---------- ABSTRACT METHODS IMPLEMENTATION -------------------//


    protected Collection getStatistics () {
        ArrayList list =  new ArrayList (1);
        statistics = getSelectedStatistics(COLUMN_PROT_NUMBER_STAT_TABLE);
        list.add (statistics);
        return list;
    }


    protected void drawCurves() {

        Color theProtColor = new Color (DEFAULT_COLOR_FILLCURVEPROT_RED,
                                        DEFAULT_COLOR_FILLCURVEPROT_GREEN,
                                        DEFAULT_COLOR_FILLCURVEPROT_BLUE);

        this.drawCurve(statistics, theGreatestYaxis, theLowestYaxis, theProtColor);
    }


}
