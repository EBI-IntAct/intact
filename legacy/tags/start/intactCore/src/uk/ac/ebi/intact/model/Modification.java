/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * An amino acid modification.
 * 
 * @example phosphorylation
 * @todo further specification
 * @author hhe
 */
public class Modification extends BasicObject {

   ///////////////////////////////////////
   // associations

/**
 * 
 */
    public Range range;
/**
 * 
 */
    public CvModificationType cvModificationType;


   ///////////////////////////////////////
   // access methods for associations

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        if (this.range != range) {
            this.range = range;
            if (range != null) range.setModification(this);  
        }      
    } 
    public CvModificationType getCvModificationType() {
        return cvModificationType;
    }

    public void setCvModificationType(CvModificationType cvModificationType) {
        this.cvModificationType = cvModificationType;
    } 

} // end Modification




