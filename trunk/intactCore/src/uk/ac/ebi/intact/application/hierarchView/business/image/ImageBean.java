/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business.image;

/**
 * Allows to store the image and the associated data :
 * - SVG DOM document,
 * - HTML MAP.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
import org.w3c.dom.Document;

public class ImageBean  {

    /*************************************************** Instance variable */

    /**
     * SVG DOM document
     */
    private Document document;

    /**
     * HTML MAP code.
     */
    private String mapCode;

    /*************************************************** Setters and getters */

    public void setDocument (Document aDocument) {
        document = aDocument;
    }

    public Document getDocument () {
        return document;
    }

    public void setMapCode (String aMapCode) {
        mapCode = aMapCode;
    }

    public String getMapCode () {
        return mapCode;
    }
}

