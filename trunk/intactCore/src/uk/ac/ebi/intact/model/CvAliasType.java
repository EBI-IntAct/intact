/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * The type of the alias.
 * @intact.example "common name"
 * @intact.example "misspelling"
 *
 * @author hhe
 */
public class CvAliasType extends CvObject {

    /**
     * no-arg constructor which will hopefully be removed later...
     */
    public CvAliasType() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid CvAliasType instance. Requires at least a shortLabel and an
     * owner to be specified.
     * @param shortLabel The memorable label to identify this CvAliasType
     * @param owner The Institution which owns this CvAliasType
     * @exception NullPointerException thrown if either parameters are not specified
     */
    public CvAliasType(String shortLabel, Institution owner) {
        //super call sets up a valid CvObject
        super(shortLabel, owner);
    }


} // end CvAliasType




