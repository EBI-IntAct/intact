/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * The role of the specific substrate in the
 * interaction.
 * 
 * example bait
 * example prey
 * @author hhe
 * @version $Id$
 */
public class CvComponentRole extends CvObject implements Editable {

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     * @deprecated Use the full constructor instead
     */
    private CvComponentRole() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvComponentRole instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvComponentRole
     * @param owner The Institution which owns this CvComponentRole
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvComponentRole(Institution owner, String shortLabel) {

        //super call sets up a valid CvObject
        super(owner, shortLabel);
    }

} // end CvComponentRole




