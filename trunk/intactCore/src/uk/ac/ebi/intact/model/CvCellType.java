/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**

 */
public class CvCellType extends CvObject {

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    public CvCellType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvCellType instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvCellType
     * @param owner The Institution which owns this CvCellType
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvCellType(String shortLabel, Institution owner) {

        //super call sets up a valid CvObject
        super(shortLabel, owner);
    }


} // end CvCellType




