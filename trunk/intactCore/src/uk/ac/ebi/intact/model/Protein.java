/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Represents a protein or peptide. The object should only contain
 * the minimum information relevant for IntAct, most information
 * should only be retrieved by the xref.
 *
 * @author hhe
 */
public class Protein extends Interactor {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    public String  cvProteinFormAc;


    private String polymerSeqAc;
    protected String formOfAc;

    /**
     * The protein sequence. If the protein is present in a public database,
     * the sequence should not be repeated.
     */
    protected String sequence;

    /**
     * Represents the CRC64 checksum. This checksum is used to
     * detect potential inconsistencies between the sequence the object
     * refers to and the external sequence object, for example when the external
     * object has been updated.
     */
    protected String crc64;

    ///////////////////////////////////////
    // associations

    /**

     */
    public Protein formOf;
    /**
     *
     */
    public CvProteinForm cvProteinForm;
    /**
     *
     */
    public Collection feature = new Vector();
    /**
     *
     */
    public Collection modification = new Vector();


    ///////////////////////////////////////
    //access methods for attributes

    public String getSequence() {
        return sequence;
    }
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    public String getCrc64() {
        return crc64;
    }
    public void setCrc64(String crc64) {
        this.crc64 = crc64;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Protein getFormOf() {
        return formOf;
    }

    public void setFormOf(Protein protein) {
        this.formOf = protein;
    }
    public CvProteinForm getCvProteinForm() {
        return cvProteinForm;
    }

    public void setCvProteinForm(CvProteinForm cvProteinForm) {
        this.cvProteinForm = cvProteinForm;
    }
    public void setFeature(Collection someFeature) {
        this.feature = someFeature;
    }
    public Collection getFeature() {
        return feature;
    }
    public void addFeature(Feature feature) {
        if (! this.feature.contains(feature)) {
            this.feature.add(feature);
            feature.setProtein(this);
        }
    }
    public void removeFeature(Feature feature) {
        boolean removed = this.feature.remove(feature);
        if (removed) feature.setProtein(null);
    }
    public void setModification(Collection someModification) {
        this.modification = someModification;
    }
    public Collection getModification() {
        return modification;
    }
    public void addModification(Modification modification) {
        if (! this.modification.contains(modification)) this.modification.add(modification);
    }
    public void removeModification(Modification modification) {
        this.modification.remove(modification);
    }


    //attributes used for mapping BasicObjects - project synchron
    public String  getCvProteinFormAc() {
        return cvProteinFormAc;
    }
    public void setCvProteinFormAc(String cvProteinFormAc) {
        this.cvProteinFormAc = cvProteinFormAc;
    }
    public String getFormOfAc() {
        return this.formOfAc;
    }
    public void setFormOfAc(String ac) {
        this.formOfAc = ac;
    }

} // end Protein




