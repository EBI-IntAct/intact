/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.graphic;


import uk.ac.ebi.intact.application.statisticView.business.data.DataManagement;
import uk.ac.ebi.intact.application.statisticView.business.Constants;
import uk.ac.ebi.intact.business.IntactException;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * 
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public abstract class GraphSkeleton {

    static Logger logger = Logger.getLogger(Constants.LOGGER_NAME);

    /********** CONSTANTS ************/

    // these constants by default are set up as the same for each graphic.

    // background color
    private static final Color DEFAULT_COLOR_BACKGROUND     = Color.white;

    // background part colored.
    private static final int DEFAULT_BACKGROUND_COLOR_X = 0;
    private static final int DEFAULT_BACKGROUND_COLOR_Y = 0;

    // drawing axis...
    // color of axis
    private static final Color DEFAULT_COLOR_AXIS = Color.black;

    // origine point location.
    private static final int DEFAULT_ORIGINE_X = 40;
    private static final int DEFAULT_ORIGINE_Y = 200;

    // Y axis end point location.
    private static final int DEFAULT_YEND_X = DEFAULT_ORIGINE_X;
    private static final int DEFAULT_YEND_Y = 40;

    // X axis end point location.
    private static final int DEFAULT_XEND_X = 400;
    private static final int DEFAULT_XEND_Y = DEFAULT_ORIGINE_Y;


    // title color.
    protected static final Color DEFAULT_COLOR_TITLE     = Color.red;

    // title place above the graphic.
    protected static final int DEFAULT_TITLE_X = 200;
    protected static final int DEFAULT_TITLE_Y = 20;

    boolean notDone = true;
    int spaceTimeCaption = 0;


    //---------- INSTANCE VARIABLES -------------//

    /**
     * define the size of images created from an instance of a graphic class.
     */
    protected int widthImage;
    protected int heightImage;

    /**
     * generated image data in order to display the picture
     */
    protected BufferedImage image;

    protected Graphics2D g2d;

    protected Point2D.Double origine, abscisseEnd, ordinateEnd;

    protected String titleString = "";

    //protected Collection of statistics, one by curve to draw;
    protected Collection statisticListFromDatabase = null;

    protected int theGreatestYaxis = 0;
    protected int theLowestYaxis = 0;



    //---------- ABSTRACT METHODS -------------------//

    abstract protected Collection getStatistics ();

    abstract protected void drawCurves();


    //---------- PROTECTED METHODS -------------------//

    public GraphSkeleton () {
        // Create graphs axis
        origine = new Point2D.Double();
        abscisseEnd = new Point2D.Double();
        ordinateEnd = new Point2D.Double();

        // values to change if you need to translate the graph
        // don't touch anything else!
        origine.setLocation (DEFAULT_ORIGINE_X, DEFAULT_ORIGINE_Y);
        ordinateEnd.setLocation (DEFAULT_YEND_X, DEFAULT_YEND_Y);
        abscisseEnd.setLocation (DEFAULT_XEND_X, DEFAULT_XEND_Y);
    }

    protected void setTitle() {

        g2d.setColor(DEFAULT_COLOR_TITLE);
        String graphTitle = titleString;
        g2d.drawString(graphTitle, DEFAULT_TITLE_X, DEFAULT_TITLE_Y);
    }

    protected void designYAxis(int sizeToSet) {

        this.getMinMaxAmountFromList();

        // end y axis fitting
        int plus = sizeToSet - (theGreatestYaxis % sizeToSet);
        theGreatestYaxis = theGreatestYaxis + plus;

        // start y axis fitting
        int moins = theLowestYaxis % sizeToSet;
        theLowestYaxis = theLowestYaxis - moins;

        g2d.drawString ("" + theGreatestYaxis, (int) origine.getX()-30, (int) ordinateEnd.getY());
        g2d.drawString ("" + theLowestYaxis, (int) origine.getX()-30, (int) origine.getY());
    }

    /**
     * This method defines the color of the background and the part to be colored.
     * This definition should be common between all graphics.
     * This method will be overrided if the background is going to be different between graphics.
     *
     * @param colorBg defines the color of the backgroung, which should be white.
     * @param xBg
     * @param yBg Both describe the starting point where the background is going to be filled
     *            with the color defined above
     *
     */
    protected void backgroundFeatures(Color colorBg, int xBg, int yBg) {

        g2d.setBackground(colorBg);
        g2d.fillRect(xBg, yBg, image.getWidth(), image.getHeight());
    }


    /**
     * Draw the both axis of a graphic: X axis and Y axis, without the caption.
     * The caption is going to move according to the max and min numbers retrieved in IntAct.
     */
    protected void drawAxis() {
        // draw Line2D.Double
        g2d.setColor (DEFAULT_COLOR_AXIS);
        g2d.draw (new Line2D.Double (origine, abscisseEnd));
        g2d.draw (new Line2D.Double (origine, ordinateEnd));

        // write the label at the axis extremities.
        g2d.drawString("Time",     (int)abscisseEnd.getX()-5,  (int)abscisseEnd.getY()+15);
        g2d.drawString("Quantity", (int)ordinateEnd.getX()-10, (int)ordinateEnd.getY()-15);
    }


    protected Collection getSelectedStatistics (int fieldOfOneColumn) {

        DataManagement dat = null;
        Collection oneStatTableField = null;

        try {
            dat = new DataManagement();
            oneStatTableField = dat.getAllDataFromOneFieldStatistics(fieldOfOneColumn);

            logger.info("Column " + fieldOfOneColumn + "  SIZE:" + oneStatTableField.size()) ;
            dat.closeDataStore();

        } catch (IntactException e) {
            logger.error ("When trying to get the data, cause: " + e.getRootCause(), e);
        }

        return oneStatTableField;
    }


    /**
     *
     * This method informs about the size of the Y axis and the caption to applicate then.
     *
     * ArrayList contains two items: one is the minimum number retrieved in the list
     *                                      the other is the maximum number retrieved in the list
     *
     */
    protected void getMinMaxAmountFromList () {

        theGreatestYaxis = Integer.MIN_VALUE;
        theLowestYaxis   = Integer.MAX_VALUE;

        for (Iterator iterator = statisticListFromDatabase.iterator(); iterator.hasNext();) {
            Collection curve = (Collection) iterator.next();

            // to get the maximum and minimum.
            for (Iterator iterator1 = curve.iterator(); iterator1.hasNext();) {
                int stat = ((Integer)iterator1.next()).intValue();

                theGreatestYaxis = Math.max (theGreatestYaxis, stat);
                theLowestYaxis   = Math.min (theLowestYaxis, stat);
            }
        }
   }


    /**
     * After getting the content of the graph, which is defined in the IA_Statistics table of IntAct,
     * draw the curve according to these data.
     *
     *
     */
    protected void drawCurve (Collection theStatList, int theGreatest, int theLowest, Color theColor) {

        if (notDone) {
            // to resize the x axis caption
            int statisticListSize = theStatList.size();
            int xAxisLength = (int)abscisseEnd.getX() - (int)origine.getX();
            if (statisticListSize > 1)
                spaceTimeCaption = xAxisLength/(statisticListSize-1);

            notDone = false;
        }


        float convertedY = 0;

        if (theGreatest - theLowest > 0) {

            //the first point need to be retrieved separately.
            Point2D.Double prot1;
            prot1 = new Point2D.Double();

            Iterator iterator = theStatList.iterator();
            if (iterator.hasNext()) {
                Integer theFirstItem = (Integer)iterator.next();
                int first = theFirstItem.intValue();

                convertedY = ( ((float)(theGreatest - first)/(float)(theGreatest - theLowest))
                              * ((int)origine.getY()-(int)ordinateEnd.getY())) + (int)ordinateEnd.getY();

                prot1.setLocation((int)origine.getX(), convertedY);
            }

            // spaces between two points.
            int nextSpaceTimeCaption = spaceTimeCaption;

            ArrayList other = new ArrayList();
            for (iterator = theStatList.iterator(); iterator.hasNext();) {

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
                convertedY = (((float)(theGreatest-protNum)/(float)(theGreatest-theLowest))
                              * ((int)origine.getY()-(int)ordinateEnd.getY())
                             ) + (int)ordinateEnd.getY();


                Point2D.Double prot2;
                prot2 = new Point2D.Double();
                prot2.setLocation((int)origine.getX()+nextSpaceTimeCaption, convertedY);

                // drawing a polygone between the two closest points.
                Polygon poly1 = new Polygon();
                poly1.addPoint((int)prot1.getX(), (int)origine.getY());
                poly1.addPoint((int)prot1.getX(), (int)prot1.getY());
                poly1.addPoint((int)prot2.getX(), (int)prot2.getY());
                poly1.addPoint((int)prot2.getX(), (int)origine.getY());

                g2d.setComposite (AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.7f));
                g2d.setColor(theColor);
                g2d.drawPolygon(poly1);
                g2d.fillPolygon(poly1);

                prot1.setLocation(prot2);

                // X axis caption
                g2d.setColor(Color.black);
                g2d.drawLine ((int)origine.getX()+nextSpaceTimeCaption, (int)abscisseEnd.getY()-2,
                              (int)origine.getX()+nextSpaceTimeCaption, (int)abscisseEnd.getY()+3);

                if ( (convertedY != theGreatest) && (convertedY != theLowest) ) {
                    String s = ""+protNum;
                    int width = g2d.getFontMetrics().stringWidth (s) / 2;
                    g2d.drawString(s, (int)prot2.getX() - width, (int)prot2.getY());
                }

                nextSpaceTimeCaption = nextSpaceTimeCaption + spaceTimeCaption;
            }
            other.clear();
        }
    }


    //---------- PUBLIC METHODS -------------------//

    /*
    * It defines the size of the image in the web page.
    */
    public void setSizeImage(int width, int height) {
        this.widthImage = width;
        this.heightImage = height;
        if (DEFAULT_XEND_X > widthImage ) {
            this.widthImage = DEFAULT_XEND_X + 50;
        }
        if (DEFAULT_ORIGINE_Y > heightImage) {
            this.heightImage = DEFAULT_ORIGINE_Y + 50;
        }
        image = new BufferedImage (widthImage, heightImage, BufferedImage.TYPE_INT_RGB);
    }

    /*
    * This method is public. It generates the skeleton of the graph, calling the previous protected
    * method. This method must be called by the servlet.
    */
    public void drawSkeleton () {

        statisticListFromDatabase = this.getStatistics ();

        // Get drawing context
        Graphics g = image.getGraphics();
        g2d = (Graphics2D) g ;

        // Produce a better quality of image
        g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        this.backgroundFeatures (DEFAULT_COLOR_BACKGROUND, DEFAULT_BACKGROUND_COLOR_X, DEFAULT_BACKGROUND_COLOR_Y);
        this.designYAxis (10);

        this.drawCurves();

        // draw the last to overlap the curves
        this.setTitle();
        this.drawAxis();

        // Dispose context
        g2d.dispose();
    }


    public BufferedImage getImageData() {
        return image;
    }

}
