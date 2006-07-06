/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * An alternative name for the object.
 * 
 * @author hhe
 */
public class Alias extends BasicObject {

  ///////////////////////////////////////
  //attributes


/**
 * Alternative name for the object.
 */
    protected String name;

   ///////////////////////////////////////
   // associations

/**
 * 
 */
    public CvAliasType cvAliasType;
/**
 * The reference object(s) this alias refers to.
 */
    public Collection referenceObject = new Vector();


  ///////////////////////////////////////
  //access methods for attributes

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

   ///////////////////////////////////////
   // access methods for associations

    public CvAliasType getCvAliasType() {
        return cvAliasType;
    }

    public void setCvAliasType(CvAliasType cvAliasType) {
        this.cvAliasType = cvAliasType;
    } 
    public Collection getReferenceObject() {
        return referenceObject;
    }
    public void addReferenceObject(AnnotatedObject annotatedObject) {
        if (! this.referenceObject.contains(annotatedObject)) this.referenceObject.add(annotatedObject);  
    }
    public void removeReferenceObject(AnnotatedObject annotatedObject) {    
        this.referenceObject.remove(annotatedObject);        
    }

} // end Alias




