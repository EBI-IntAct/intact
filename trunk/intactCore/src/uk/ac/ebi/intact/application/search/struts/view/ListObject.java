/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.view;

import org.apache.taglibs.display.StringUtil;

import java.io.Serializable;

import uk.ac.ebi.intact.model.AnnotatedObject;

/**
 * This class returns columns of data that are useful for displaying them
 * onto a jsp form. This class forms part of the Decorator pattern required by
 * display tag library.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */

public class ListObject implements Serializable {

    /**
     * Reference to an intact (annotated) object.
     */
    private AnnotatedObject myObject;

    /**
     * Constructs with an AnnotatedObject.
     * @param anObj the object to extract information.
     */
    public ListObject(AnnotatedObject anObj) {
        myObject = anObj;
    }

    /**
     * Returns the AC as a string.
     *
     * @return accession number of the CvObject.
     */
    public String getAc() {
        return myObject.getAc();
    }

    /**
     * Returns the short label.
     *
     * @return short label.
     */
    public String getShortLabel() {
        return myObject.getShortLabel();
    }
}
