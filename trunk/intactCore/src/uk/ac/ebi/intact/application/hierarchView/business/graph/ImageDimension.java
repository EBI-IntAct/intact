/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.graph;

import java.io.Serializable;


public class ImageDimension implements Serializable {
    // ---------------------------------------------------------------- StrutsConstants
    public static float DEFAULT_BORDER = 5f;

    // ---------------------------------------------------------------- Instance Variables
    private float xmin;
    private float xmax;
    private float ymin;
    private float ymax;
    private float border;

    // ---------------------------------------------------------------- Constructors
    /**
     * Create an ImageDimension with a specified border
     *
     * @param aBorder the border
     */
    public ImageDimension (float aBorder) {
        xmin = xmax = ymin = ymax = 0;
        border = aBorder;
    }

    public ImageDimension() {
        this (DEFAULT_BORDER);
    }

    // ---------------------------------------------------------------- Accessors
    public float length () { return xmax - xmin; }

    public float height () { return ymax - ymin; }

    public float xmin ()   { return xmin; }

    public float ymin ()   { return ymin; }

    public float border ()  { return border; }


    // ---------------------------------------------------------------- Other methods
    /**
     * Widen the size if the new coordinate is out of the usable space.
     * After adding a set of points we should have obtain something like below.<br>
     *     +-----------------------4-----+<br>
     *     +                             +<br>
     *     +        1 (x1,y1)            +<br>
     *     +                             +<br>
     *     +                             +<br>
     *     +                             +<br>
     *     +                             +<br>
     *     5                             +<br>
     *     +              6              2 (x2,y2)<br>
     *     +                             +<br>
     *     +                             +<br>
     *     +                             +<br>
     *     +------3----------------------+<br>
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    public void adjust (float x, float y) {
        if (x < xmin) xmin = x;
        if (y < ymin) ymin = y;
        if (x > xmax) xmax = x;
        if (y > ymax) ymax = y;
    } // adjust


    /**
     * Adjust width and height according to components size.
     * This is efficient only if node have already been set.<br>
     * <br>
     *     +------------------+    ^ <br>
     *     +                  +    | <br>
     *     +        * (x,y)   +    | height <br>
     *     +                  +    | <br>
     *     +------------------+    - <br>
     *  <br>
     *     <----- width ------> <br>
     *
     * @param width width of the conponent
     * @param height heigth of the component
     * @param x the X coordinate
     * @param y the Y coordinate
     */
    public void adjustCadre (float width, float height, float x, float y) {
        float tmp = 0;
        if ((tmp = x  - width/2) < xmin)
            xmin = tmp;
        if ((tmp = x  + width/2) > xmax)
            xmax = tmp;
        if ((tmp = y  - height/2) < ymin)
            ymin = tmp;
        if ((tmp = y  + height/2) > ymax)
            ymax = tmp;
    } // adjustCadre

} // ImageDimension
