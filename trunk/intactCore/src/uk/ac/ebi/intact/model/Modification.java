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
 * @intact.example phosphorylation
 * @intact.todo further specification
 * @author hhe
 */
public class Modification extends BasicObject {

    ///////////////////////////////////////
    // associations


    //attributes used for mapping BasicObjects - project synchron
    public String rangeAc;
    public String cvModificationTypeAc;

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

    //attributes used for mapping BasicObjects - project synchron
    public String getRangeAc() {
        return this.rangeAc;
    }
    public void setRangeAc(String ac) {
        this.rangeAc = ac;
    }

    public String getCvModificationTypeAc() {
        return this.cvModificationTypeAc;
    }
    public void setCvModificationTypeAc(String ac) {
        this.cvModificationTypeAc = ac;
    }

} // end Modification




