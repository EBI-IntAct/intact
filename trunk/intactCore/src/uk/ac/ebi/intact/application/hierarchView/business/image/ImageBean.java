/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.image;


/**
 * ImageBean.java
 *
 * Allows to store the image and the associated HTML MAP.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 */
import org.w3c.dom.Document;

public class ImageBean  {

    /*************************************************** Properties */

    /**
     * Coded image.
     */
    private byte[] imageData;

    /**
     * Document
     */
    private Document document;

    /**
     * HTML MAP code.
     */
    private String mapCode;

    /*************************************************** Setter and getter */

    public void setImageData (byte[] someImageData) {
        imageData = someImageData;
    } // setImageData

    public byte[] getImageData () {
        return imageData;
    } // getImageData

    public void setDocument (Document aDocument) {
        document = aDocument;
    } // setDocument

    public Document getDocument () {
        return document;
    } // getDocument

    public void setMapCode (String aMapCode) {
        mapCode = aMapCode;
    } // setMapCode

    public String getMapCode () {
        return mapCode;
    } // getMapCode

} // ImageBean

