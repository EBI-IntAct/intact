/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * This class qualifies the association between an AnnotatedObject and a
 * Reference.
 *
 *@author hhe
 */
public class ReferenceQualifier {

    ///////////////////////////////////////
    // associations


    //attributes used for mapping BasicObjects - project synchron
    public String referenceAc;
    public String cvReferenceQualifierAc;

    /**
     *
     */
    public Reference reference;
    /**
     *
     */
    public CvReferenceQualifier cvReferenceQualifier;


    ///////////////////////////////////////
    // access methods for associations

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }
    public CvReferenceQualifier getCvReferenceQualifier() {
        return cvReferenceQualifier;
    }

    public void setCvReferenceQualifier(CvReferenceQualifier cvReferenceQualifier) {
        this.cvReferenceQualifier = cvReferenceQualifier;
    }

    //attributes used for mapping BasicObjects - project synchron
    public String getReferenceAc() {
        return referenceAc;
    }
    public void setReferenceAc(String ac) {
        this.referenceAc = ac;
    }

    public String getCvReferenceQualifierAc() {
        return cvReferenceQualifierAc;
    }
    public void setCvReferenceQualifierAc(String ac) {
        this.cvReferenceQualifierAc = ac;
    }

} // end ReferenceQualifier




