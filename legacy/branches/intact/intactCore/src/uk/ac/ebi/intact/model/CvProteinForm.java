/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**

 */
public class CvProteinForm extends CvObject {

   ///////////////////////////////////////
   // associations

/**
 * 
 */
    public CvObject cvObject;


   ///////////////////////////////////////
   // access methods for associations

    public CvObject getCvObject() {
        return cvObject;
    }

    public void setCvObject(CvObject cvObject) {
        if (this.cvObject != cvObject) {
            this.cvObject = cvObject;
            if (cvObject != null) cvObject.setCvProteinForm(this);  
        }      
    } 

} // end CvProteinForm




