/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.graphic;

import uk.ac.ebi.intact.application.statisticView.business.StatGraphConstants;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
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

    private final static int COLUMN_INTERACTIONS_NUMBER_STAT_TABLE = 2;
    private final static int COLUMN_BININT_NUMBER_STAT_TABLE = 3;
    private final static int COLUMN_COMPLEXINT_NUMBER_STAT_TABLE = 4;

        // The both collection needed to draw 2 curves
    private Collection binaryInteractionsList = null;
    private Collection complexInteractionsList = null;



        //---------- CONSTRUCTOR -------------------//

    public GraphInteraction() {
        titleString = StatGraphConstants.TITLE_GRAPH_INTERACTION;
        // statisticListFromDatabase = DataManagement.getProteinNumberList();
    }

        //---------- ABSTRACT METHODS IMPLEMENTATION -------------------//


    protected void setTitle() {

            g2d.setColor(DEFAULT_COLOR_TITLE);
            String graphTitle = titleString;
            g2d.drawString(graphTitle, DEFAULT_TITLE_X, DEFAULT_TITLE_Y);
        }


    protected Collection fullCollectionWithGoodField(int theInterField) {

        ArrayList theIntList = new ArrayList();
        theIntList = (ArrayList)getOneStatisticCollection(theInterField);

        return theIntList;

    }


    protected void getCollectionAndDesignYAxis(int sizeToSet) {

            // after, this method will be called in the drawCurve() method.
            // A method to retrieve the greatest and the lowest numbers will be implemented
        //this.getStatisticCollection();
        //statisticListFromDatabase = this.fullCollectionWithGoodField(COLUMN_INTERACTIONS_NUMBER_STAT_TABLE);

        binaryInteractionsList = this.fullCollectionWithGoodField(COLUMN_BININT_NUMBER_STAT_TABLE);


        complexInteractionsList = this.fullCollectionWithGoodField(COLUMN_COMPLEXINT_NUMBER_STAT_TABLE);

        //this.getMinMaxAmountFromList();

        int theGreatestBin = 0;
        int theLowestBin = 100;

            // to get the maximum.
        for (Iterator iterator1 = this.binaryInteractionsList.iterator(); iterator1.hasNext();) {
            Integer theQuantityNum = (Integer)iterator1.next();
            int quantNum = theQuantityNum.intValue();
            if (quantNum > theGreatestBin) {
                theGreatestBin = quantNum;
            }
            else
                continue;
        }

            // to get the minimum.
        for (Iterator iterator2 = this.binaryInteractionsList.iterator(); iterator2.hasNext();) {
            Integer theSameQuantityNum = (Integer)iterator2.next();
            int valueNum = theSameQuantityNum.intValue();
            if (valueNum < theLowestBin) {
                theLowestBin = valueNum;
            }
            else
                continue;
        }

        int theGreatestComp = 0;
        int theLowestComp = 100;

           // to get the maximum.
        for (Iterator iterator1 = this.complexInteractionsList.iterator(); iterator1.hasNext();) {
            Integer theQuantityNum = (Integer)iterator1.next();
            int quantNum = theQuantityNum.intValue();
            if (quantNum > theGreatestComp) {
                theGreatestComp = quantNum;
            }
            else
                continue;
        }

            // to get the minimum.
        for (Iterator iterator2 = this.complexInteractionsList.iterator(); iterator2.hasNext();) {
            Integer theSameQuantityNum = (Integer)iterator2.next();
            int valueNum = theSameQuantityNum.intValue();
            if (valueNum < theLowestComp) {
                theLowestComp = valueNum;
            }
            else
                continue;
        }

        this.theGreatestYaxis = theGreatestComp;
        if (theGreatestBin > theGreatestComp)
            theGreatestYaxis = theGreatestBin;

        this.theLowestYaxis = theLowestBin;
        if (theLowestComp < theGreatestBin)
            theLowestYaxis = theLowestComp;

            // end y axis fitting
        int plus = sizeToSet - (theGreatestYaxis % sizeToSet);
        theGreatestYaxis = theGreatestYaxis + plus;

            // start y axis fitting
        int moins = theLowestYaxis % sizeToSet;
        theLowestYaxis = theLowestYaxis - moins;

        g2d.drawString(""+theGreatestYaxis, (int)origine.getX()-30, (int)ordinateEnd.getY());
        g2d.drawString(""+theLowestYaxis, (int)origine.getX()-30, (int)origine.getY());


    }


        // in case of 2 curves
    protected void drawEveryCurve() {

        Color theBinColor = this.specifyColorFillCurve(DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_RED,
                                                            DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_GREEN,
                                                                DEFAULT_COLOR_FILLCURVE_BIN_INTERACTIONS_BLUE);
        //this.drawCurve(binaryInteractionsList, theGreatestYaxis, theLowestYaxis, theBinColor);

        // to resize the x axis caption -- needed only once
        int statisticListSize = binaryInteractionsList.size();
        int xAxisLength = (int)abscisseEnd.getX() - (int)origine.getX();
        int spaceTimeCaption = 0;
        if (statisticListSize > 1)
            spaceTimeCaption = xAxisLength/(statisticListSize-1);

        this.drawOneOfTheCurves(binaryInteractionsList, spaceTimeCaption, theBinColor);


        Color theCompColor = this.specifyColorFillCurve(DEFAULT_COLOR_FILLCURVE_COMPLEXES_RED,
                                                            DEFAULT_COLOR_FILLCURVE_COMPLEXES_GREEN,
                                                                DEFAULT_COLOR_FILLCURVE_COMPLEXES_BLUE);
        //this.drawCurve(complexInteractionsList, theGreatestYaxis, theLowestYaxis, theCompColor);

        this.drawOneOfTheCurves(complexInteractionsList, spaceTimeCaption, theCompColor);

        // Once each curve is drawn, dispose context
        g2d.dispose();

    }


    protected void drawOneOfTheCurves (Collection theInterList, int xAxisSpace, Color theColor) {

        float convertedY = 0;

        if (theGreatestYaxis - theLowestYaxis > 0) {

                //the first point need to be retrieved separately.
            Point2D.Double prot1;
            prot1 = new Point2D.Double();
            for (Iterator iterator = theInterList.iterator(); iterator.hasNext();) {

                Integer theFirstItem = (Integer)iterator.next();
                int first = theFirstItem.intValue();

                convertedY = ( ((float)(theGreatestYaxis - first)/(float)(theGreatestYaxis - theLowestYaxis))
                                 * ((int)origine.getY()-(int)ordinateEnd.getY())) + (int)ordinateEnd.getY();

                prot1.setLocation((int)origine.getX(), convertedY);

                break;
            }

                // spaces between two points.
            int nextSpaceTimeCaption = xAxisSpace;

            ArrayList other = new ArrayList();
            for (Iterator iterator = theInterList.iterator(); iterator.hasNext();) {

                    // the spot value to put into the graph..
                Integer theProtNum = (Integer)iterator.next();
                int protNum = theProtNum.intValue();

                    // the first point has already been retrieved, just take the next one.
                other.add(theProtNum);
                if (other.size() == 1) {
                    theProtNum = (Integer)iterator.next();
                    protNum = theProtNum.intValue();
                }

                    // y axis conversion.
                convertedY = (
                                    ((float)(theGreatestYaxis-protNum)/(float)(theGreatestYaxis-theLowestYaxis))
                                                * ((int)origine.getY()-(int)ordinateEnd.getY())
                              ) +
                                    (int)ordinateEnd.getY();


                Point2D.Double prot2;
                prot2 = new Point2D.Double();
                prot2.setLocation((int)origine.getX()+nextSpaceTimeCaption, convertedY);

                    // drawing a polygone between the two closest points.
                Polygon poly1 = new Polygon();
                poly1.addPoint((int)prot1.getX(), (int)origine.getY());
                poly1.addPoint((int)prot1.getX(), (int)prot1.getY());
                poly1.addPoint((int)prot2.getX(), (int)prot2.getY());
                poly1.addPoint((int)prot2.getX(), (int)origine.getY());

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.setColor(theColor);
                g2d.drawPolygon(poly1);
                g2d.fillPolygon(poly1);

                prot1.setLocation(prot2);

                // X axis caption
                g2d.setColor(Color.black);
                g2d.drawLine((int)origine.getX()+nextSpaceTimeCaption, (int)abscisseEnd.getY()-2,
                                    (int)origine.getX()+nextSpaceTimeCaption, (int)abscisseEnd.getY()+3);

                if ( (convertedY != theGreatestYaxis) && (convertedY != theLowestYaxis) ) {
                    g2d.drawString(""+protNum, (int)prot2.getX(), (int)prot2.getY());
                    //g2d.drawString(""+protNum, (int)origine.getX()-30, convertedY);
                }

                nextSpaceTimeCaption = nextSpaceTimeCaption + xAxisSpace;
            }
            other.clear();
        }

    }


    /**
     *
     * Let each curve choosing its color.
     *
     * @param red
     * @param green
     * @param blue
     * @return Color which fills all polygones components of the curve
     */
    protected Color specifyColorFillCurve(int red, int green, int blue) {

        Color fillCurve    = new Color(red, green, blue);

        return fillCurve;
    }



}
