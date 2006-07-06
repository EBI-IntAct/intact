/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Represents ...
 * 
 * @see OtherClasses
 * @author Henning Hermjakob
 */
public class JournalRef extends Reference {

  ///////////////////////////////////////
  //attributes


/**
 * Represents ...
 */
    protected Integer pubmidId;

/**
 * Represents ...
 */
    protected String firstpage;

   ///////////////////////////////////////
   // associations

/**
 * 
 */
    public CvJournal cvJournal;


  ///////////////////////////////////////
  //access methods for attributes

    public Integer getPubmidId() {
        return pubmidId;
    }
    public void setPubmidId(Integer pubmidId) {
        this.pubmidId = pubmidId;
    }
    public String getFirstpage() {
        return firstpage;
    }
    public void setFirstpage(String firstpage) {
        this.firstpage = firstpage;
    }

   ///////////////////////////////////////
   // access methods for associations

    public CvJournal getCvJournal() {
        return cvJournal;
    }

    public void setCvJournal(CvJournal cvJournal) {
        this.cvJournal = cvJournal;
    } 

} // end JournalRef




