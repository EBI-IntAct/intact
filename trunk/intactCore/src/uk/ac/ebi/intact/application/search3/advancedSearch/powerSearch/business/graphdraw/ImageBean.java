/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.business.graphdraw;

import java.awt.image.BufferedImage;

/**
 * A bean that holds the imagedata and the corresponding map
 *
 * @author Samuel Kerrien (Samuel kerrien), Anja Friedrichsen
 * @version $Id$
 */
public class ImageBean {

    /**
     * generated image data in order to display the picture
     */
    private transient BufferedImage imageData;

    /**
     * HTML map code
     */
    private String imageMap;

    /**
     * name of the CV to generate the image
     */
    private String cvName;


    //////////////////////////
    // Getters and Setters

    public BufferedImage getImageData() {
        return imageData;
    }

    public void setImageData( BufferedImage imageData ) {
        this.imageData = imageData;
    }

    public String getImageMap() {
        return imageMap;
    }

    public void setImageMap( String imageMap ) {
        this.imageMap = imageMap;
    }

    public String getCvName() {
        return cvName;
    }

    public void setCvName( String cvName ) {
        this.cvName = cvName;
    }
}
