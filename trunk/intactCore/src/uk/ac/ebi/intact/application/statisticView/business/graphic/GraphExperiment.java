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
public class GraphExperiment extends GraphSkeleton {

    //---------- CONSTANTS -------------------//

    private final static int DEFAULT_COLOR_FILLCURVE_EXP_RED   = 141;
    private final static int DEFAULT_COLOR_FILLCURVE_EXP_GREEN = 172;
    private final static int DEFAULT_COLOR_FILLCURVE_EXP_BLUE  = 214;

    private final static int COLUMN_EXP_NUMBER_STAT_TABLE = 5;

    private Collection statistics = null;


    //---------- CONSTRUCTOR -------------------//

    public GraphExperiment() {
        super();
        titleString = StatGraphConstants.TITLE_GRAPH_EXPERIMENT;
    }


    //---------- ABSTRACT METHODS IMPLEMENTATION -------------------//

    protected Collection getStatistics () {
        ArrayList list =  new ArrayList (1);
        statistics = getSelectedStatistics(COLUMN_EXP_NUMBER_STAT_TABLE);
        list.add (statistics);
        return list;
    }

    protected void drawCurves() {

        Color theExpColor = new Color (DEFAULT_COLOR_FILLCURVE_EXP_RED,
                                       DEFAULT_COLOR_FILLCURVE_EXP_GREEN,
                                       DEFAULT_COLOR_FILLCURVE_EXP_BLUE);

        this.drawCurve(statistics, theGreatestYaxis, theLowestYaxis, theExpColor);
    }

}
