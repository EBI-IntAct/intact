/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**

 */
public class Range extends BasicObject {

  ///////////////////////////////////////
  //attributes


/**
 * Represents ...
 */
    protected int posFrom;

/**
 * Represents ...
 */
    protected int posTo;

   ///////////////////////////////////////
   // associations

/**
 * 
 */
    public Modification modification;


  ///////////////////////////////////////
  //access methods for attributes

    public int getPosFrom() {
        return posFrom;
    }
    public void setPosFrom(int posFrom) {
        this.posFrom = posFrom;
    }
    public int getPosTo() {
        return posTo;
    }
    public void setPosTo(int posTo) {
        this.posTo = posTo;
    }

   ///////////////////////////////////////
   // access methods for associations

    public Modification getModification() {
        return modification;
    }

    public void setModification(Modification modification) {
        if (this.modification != modification) {
            this.modification = modification;
            if (modification != null) modification.setRange(this);  
        }      
    } 

} // end Range




