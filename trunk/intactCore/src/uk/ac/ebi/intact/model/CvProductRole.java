/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * The role of the product.
 * 
 * @intact.example In an enzymatic reaction, one product could be the "modified protein", the other the "enzyme".
 * @author hhe
 */
public class CvProductRole extends CvObject implements Editable {

    /**
     * Cache a Vector of all shortLabels of the class, e.g. for menus.
     * This should not be here as it has no model functionality but is
     * related to eg user interfaces.
     */
    protected static Vector menuList = null;

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    public CvProductRole() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvProductRole instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvProductRole
     * @param owner The Institution which owns this CvProductRole
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvProductRole(String shortLabel, Institution owner) {

        //super call sets up a valid CvObject
        super(shortLabel, owner);
    }


} // end CvProductRole




