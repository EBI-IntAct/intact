/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.graphic;

import uk.ac.ebi.intact.application.statisticView.business.StatGraphConstants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

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


        //---------- CONSTRUCTOR -------------------//

    public GraphExperiment() {
        titleString = StatGraphConstants.TITLE_GRAPH_EXPERIMENT;
        // statisticListFromDatabase = DataManagement.getProteinNumberList();
    }

        //---------- ABSTRACT METHODS IMPLEMENTATION -------------------//


    protected void setTitle() {

            g2d.setColor(DEFAULT_COLOR_TITLE);
            String graphTitle = titleString;
            g2d.drawString(graphTitle, DEFAULT_TITLE_X, DEFAULT_TITLE_Y);
        }


    protected Collection fullCollectionWithGoodField(int theExpField) {

        ArrayList theExpList = new ArrayList();
        theExpList = (ArrayList)getOneStatisticCollection(theExpField);

        return theExpList;

    }



    protected void getCollectionAndDesignYAxis(int sizeToSet) {

            // after, this method will be called in the drawCurve() method.
            // A method to retrieve the greatest and the lowest numbers will be implemented
        //this.getStatisticCollection();
        statisticListFromDatabase = this.fullCollectionWithGoodField(COLUMN_EXP_NUMBER_STAT_TABLE);
        this.getMinMaxAmountFromList();

            // end y axis fitting
        int plus = sizeToSet - (theGreatestYaxis % sizeToSet);
        theGreatestYaxis = theGreatestYaxis + plus;

            // start y axis fitting
        int moins = theLowestYaxis % sizeToSet;
        theLowestYaxis = theLowestYaxis - moins;

        g2d.drawString(""+theGreatestYaxis, (int)origine.getX()-30, (int)ordinateEnd.getY());
        g2d.drawString(""+theLowestYaxis, (int)origine.getX()-30, (int)origine.getY());

        /*
        int quantSize = theGreatestYaxis - theLowestYaxis;
        int yAxisLength = (int)origine.getY() - (int)ordinateEnd.getY();

        if (quantSize>0 & yAxisLength>0) {

            int spaceQuantCaption = quantSize/yAxisLength;

            if (spaceQuantCaption > 0) {
                int spaceToSubstractFromTheOrigin = yAxisLength/spaceQuantCaption;

                int spaceWhereToCome = (int)origine.getY()-spaceToSubstractFromTheOrigin;


                for (int i = 1; i < spaceQuantCaption; i++) {
                    g2d.drawLine((int)ordinateEnd.getX()-2, spaceWhereToCome,
                                        (int)ordinateEnd.getX()+3, spaceWhereToCome);

                    spaceWhereToCome = spaceWhereToCome - spaceToSubstractFromTheOrigin;
                }
            }
        }*/
    }


    protected void drawEveryCurve() {

        Color theExpColor = this.specifyColorFillCurve(DEFAULT_COLOR_FILLCURVE_EXP_RED,
                                                            DEFAULT_COLOR_FILLCURVE_EXP_GREEN,
                                                                  DEFAULT_COLOR_FILLCURVE_EXP_BLUE);
        this.drawCurve(statisticListFromDatabase, theGreatestYaxis, theLowestYaxis, theExpColor);

    }

     protected Color specifyColorFillCurve(int red, int green, int blue) {

        Color fillCurve    = new Color(red, green, blue);

        return fillCurve;
    }



}
