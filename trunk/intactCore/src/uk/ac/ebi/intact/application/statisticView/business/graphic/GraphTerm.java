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
public class GraphTerm extends GraphSkeleton {

    //---------- CONSTANTS -------------------//

    private final static int DEFAULT_COLOR_FILLCURVETERM_RED   = 67;
    private final static int DEFAULT_COLOR_FILLCURVETERM_GREEN = 219;
    private final static int DEFAULT_COLOR_FILLCURVETERM_BLUE  = 209;

    private final static int COLUMN_TERM_NUMBER_STAT_TABLE = 6;

    private Collection statistics = null;

    //---------- CONSTRUCTOR -------------------//

    public GraphTerm() {
        super ();
        titleString = StatGraphConstants.TITLE_GRAPH_TERM;
    }


    //---------- ABSTRACT METHODS IMPLEMENTATION -------------------//


    protected Collection getStatistics () {
        ArrayList list =  new ArrayList (1);
        statistics = getSelectedStatistics(COLUMN_TERM_NUMBER_STAT_TABLE);
        list.add (statistics);
        return list;
    }

    protected void drawCurves() {

        Color theTermColor = new Color (DEFAULT_COLOR_FILLCURVETERM_RED,
                                        DEFAULT_COLOR_FILLCURVETERM_GREEN,
                                        DEFAULT_COLOR_FILLCURVETERM_BLUE);

        this.drawCurve(statistics, theGreatestYaxis, theLowestYaxis, theTermColor);
    }
}
