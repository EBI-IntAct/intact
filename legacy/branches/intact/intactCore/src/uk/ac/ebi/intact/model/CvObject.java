/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Represents a controlled vocabulary object. CvObject is derived from
  * AnnotatedObject to allow to store annotation of the term within the
  * object itself, thus allowing to build an integrated dictionary.
  *
  *
  * @author Henning Hermjakob
 */
public abstract class CvObject extends AnnotatedObject {

   ///////////////////////////////////////
   // associations

    private String ojbConcreteClass;
/**
 * 
 */
    public CvProteinForm cvProteinForm;


   ///////////////////////////////////////
   // access methods for associations

    public CvProteinForm getCvProteinForm() {
        return cvProteinForm;
    }

    public void setCvProteinForm(CvProteinForm cvProteinForm) {
        if (this.cvProteinForm != cvProteinForm) {
            this.cvProteinForm = cvProteinForm;
            if (cvProteinForm != null) cvProteinForm.setCvObject(this);  
        }      
    } 

} // end CvObject




