/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Represents a biological source.
 *
 * @author hhe
 */
public class BioSource extends AnnotatedObject {

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

    /**
     * The scientific name. This is for human readability and text based searching only, the object is defined by the taxId.
     */
    protected String scientificName;

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


    ///////////////////////////////////////
    //access methods for attributes

    public String getTaxId() {
        return taxId;

    }
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
    public String getScientificName() {
        return scientificName;
    }
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
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
} // end BioSource




