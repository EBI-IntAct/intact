/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**

 */
public class Evidence {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    protected String cvEvidenceTypeAc;
    /**

     */
    protected String parameters;

    ///////////////////////////////////////
    // associations

    /**
     *
     */
    public CvEvidenceType cvEvidenceType;


    ///////////////////////////////////////
    //access methods for attributes

    public String getParameters() {
        return parameters;
    }
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvEvidenceType getCvEvidenceType() {
        return cvEvidenceType;
    }

    public void setCvEvidenceType(CvEvidenceType cvEvidenceType) {
        this.cvEvidenceType = cvEvidenceType;
    }

    //attributes used for mapping BasicObjects - project synchron
    public String getCvEvidenceTypeAc() {
        return this.cvEvidenceTypeAc;
    }
    public void setCvEvidenceTypeAc(String ac) {
        this.cvEvidenceTypeAc = ac;
    }

} // end Evidence




