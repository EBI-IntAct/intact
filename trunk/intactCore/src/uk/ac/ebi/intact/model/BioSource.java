/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * Represents a biological source.
 *
 * @author hhe
 */
public class BioSource extends AnnotatedObject implements Editable {

    ///////////////////////////////////////
    //attributes


    //attributes used for mapping BasicObjects - project synchron
    public String cvCellCycleAc;
    public String cvDevelopmentalStageAc;
    public String cvTissueAc;
    public String cvCellTypeAc;
    public String cvCompartmentAc;


    /**
     * The NCBI tax id.
     */
    protected String taxId;

    ///////////////////////////////////////
    // associations

    /**
     *
     */
    public CvCellCycle cvCellCycle;
    /**
     *
     */
    public CvDevelopmentalStage cvDevelopmentalStage;
    /**
     *
     */
    public CvTissue cvTissue;
    /**
     *
     */
    public CvCellType cvCellType;
    /**
     *
     */
    public CvCompartment cvCompartment;

    /**
     * no-arg constructor. Hope to replace with a private one as it should
     * not be used by applications because it will result in objects with invalid
     * states.
     */
    public BioSource() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid BioSource (ie a source organism) instance. A valid instance must have at least
     * a non-null shortLabel specified. A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time.
     * @param shortLabel The label used to identify this instance
     * @param taxId the NCBI taxId, which must be unique if defined (may be null)
     * @param owner The <code>Institution</code> which 'owns' this BioSource
     * @exception NullPointerException thrown if either no shortLabel or Institution specified.
     */
    public BioSource(String shortLabel, String taxId, Institution owner) {

        //super call sets up a valid AnnotatedObject
        super(shortLabel, owner);
        //Q: taxId must be unique (but apparently may be null)- how to validate this??
        this.taxId = taxId;

    }


    ///////////////////////////////////////
    //access methods for attributes

    public String getTaxId() {
        return taxId;

    }
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvCellCycle getCvCellCycle() {
        return cvCellCycle;
    }
    public void setCvCellCycle(CvCellCycle cvCellCycle) {
        this.cvCellCycle = cvCellCycle;
    }

    public CvDevelopmentalStage getCvDevelopmentalStage() {
        return cvDevelopmentalStage;
    }
    public void setCvDevelopmentalStage(CvDevelopmentalStage cvDevelopmentalStage) {
        this.cvDevelopmentalStage = cvDevelopmentalStage;
    }

    public CvTissue getCvTissue() {
        return cvTissue;
    }
    public void setCvTissue(CvTissue cvTissue) {
        this.cvTissue = cvTissue;
    }

    public CvCellType getCvCellType() {
        return cvCellType;
    }
    public void setCvCellType(CvCellType cvCellType) {
        this.cvCellType = cvCellType;
    }

    public CvCompartment getCvCompartment() {
        return cvCompartment;
    }
    public void setCvCompartment(CvCompartment cvCompartment) {
        this.cvCompartment = cvCompartment;
    }



    //attributes used for mapping BasicObjects - project synchron
    public String getCvCompartmentAc() {
        return cvCompartmentAc;
    }
    public void setCvCompartmentAc(String ac) {
        this.cvCompartmentAc = ac;
    }
    public String getCvCellCycleAc() {
        return cvCellCycleAc;
    }
    public void setCvCellCycleAc(String ac) {
        this.cvCellCycleAc = ac;
    }
    public String getCvCellTypeAc() {
        return cvCellTypeAc;
    }
    public void setCvCellTypeAc(String ac) {
        this.cvCellTypeAc = ac;
    }
    public String getCvTissueAc() {
        return cvTissueAc;
    }
    public void setCvTissueAc(String ac) {
        this.cvTissueAc = ac;
    }
    public String getCvDevelopmentalStageAc() {
        return cvDevelopmentalStageAc;
    }
    public void setCvDevelopmentalStageAc(String ac) {
        this.cvDevelopmentalStageAc = ac;
    }


    /**
     * Currently the equality rely only on the taxId.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BioSource)) return false;
        if (!super.equals(o)) return false;

        final BioSource bioSource = (BioSource) o;

        if (taxId != null ? !taxId.equals(bioSource.taxId) : bioSource.taxId != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (taxId != null ? taxId.hashCode() : 0);
        return result;
    }

} // end BioSource




