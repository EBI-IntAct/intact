/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.Date;
import java.util.*;

/**
 * Represents ...
 *
 * @author Henning Hermjakob
 */
public class SubmissionRef extends Reference {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    protected String referenceAc;

    /**
     * Represents ...
     */
    protected Date holdDate;

    ///////////////////////////////////////
    // associations

    /**
     *
     */
    public Reference reference;


    ///////////////////////////////////////
    //access methods for attributes

    public Date getHoldDate() {
        return holdDate;
    }
    public void setHoldDate(Date holdDate) {
        this.holdDate = holdDate;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        if (this.reference != reference) {
            this.reference = reference;
            if (reference != null) reference.setSubmissionRef(this);
        }
    }


    //attributes used for mapping BasicObjects - project synchron
    public String getReferenceAc() {
        return referenceAc;
    }

    public void setReferenceAc(String ac) {
        this.referenceAc = ac;
    }

} // end SubmissionRef




