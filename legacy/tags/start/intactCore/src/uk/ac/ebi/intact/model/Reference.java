/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**

 */
public class Reference extends BasicObject {

  ///////////////////////////////////////
  //attributes


/**
 * Represents ...
 */
    protected String title;

/**
 * Represents ...
 */
    protected String authors;

   ///////////////////////////////////////
   // associations

/**
 * 
 */
    public Collection annotatedObject = new Vector();
/**
 * 
 */
    public SubmissionRef submissionRef;
/**
 * 
 */
    public Xref xref;


  ///////////////////////////////////////
  //access methods for attributes

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthors() {
        return authors;
    }
    public void setAuthors(String authors) {
        this.authors = authors;
    }

   ///////////////////////////////////////
   // access methods for associations

    public Collection getAnnotatedObject() {
        return annotatedObject;
    }
    public void addAnnotatedObject(AnnotatedObject annotatedObject) {
        if (! this.annotatedObject.contains(annotatedObject)) {     
            this.annotatedObject.add(annotatedObject);  
            annotatedObject.addReference(this);
        }
    }
    public void removeAnnotatedObject(AnnotatedObject annotatedObject) {
        boolean removed = this.annotatedObject.remove(annotatedObject);
        if (removed) annotatedObject.removeReference(this);
    }
    public SubmissionRef getSubmissionRef() {
        return submissionRef;
    }

    public void setSubmissionRef(SubmissionRef submissionRef) {
        if (this.submissionRef != submissionRef) {
            this.submissionRef = submissionRef;
            if (submissionRef != null) submissionRef.setReference(this);  
        }      
    } 


} // end Reference




