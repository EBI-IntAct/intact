/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**

 */
public class CvCellCycle extends CvObject implements Editable {

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    public CvCellCycle() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvCellCycle instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvCellCycle
     * @param owner The Institution which owns this CvCellCycle
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvCellCycle(String shortLabel, Institution owner) {

        //super call sets up a valid CvObject
        super(shortLabel, owner);
    }


} // end CvCellCycle




