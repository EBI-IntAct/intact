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
 * @intact.example bait
 * @intact.example prey
 * @author hhe
 */
public class CvComponentRole extends CvObject implements Editable {

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    public CvComponentRole() {
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
    public CvComponentRole(String shortLabel, Institution owner) {

        //super call sets up a valid CvObject
        super(shortLabel, owner);
    }


} // end CvComponentRole




