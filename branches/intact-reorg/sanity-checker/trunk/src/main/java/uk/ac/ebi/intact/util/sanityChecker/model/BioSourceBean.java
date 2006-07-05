/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.sanityChecker.model;

//import uk.ac.ebi.intact.application.commons.util.CvFilterRessources;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class BioSourceBean extends AnnotatedBean {

    private String bioSourceXref;

    private String tissue_ac;

    private String celltype_ac;

    public String getTissue_ac() {
        return tissue_ac;
    }

    public void setTissue_ac(String tissue_ac) {
        this.tissue_ac = tissue_ac;
    }

    public String getCelltype_ac() {
        return celltype_ac;
    }

    public void setCelltype_ac(String celltype_ac) {
        this.celltype_ac = celltype_ac;
    }

    public String getBioSourceXref() {
        return bioSourceXref;
    }

    public void setBioSourceXref(String bioSourceXref) {
        this.bioSourceXref = bioSourceXref;
    }

    /*public void setBioSourceXref() {
        CvFilterRessources cvFilterRessources=new CvFilterRessources();
        this.bioSourceXref = cvFilterRessources.getBiosourceXref().toString();
    }*/



    private String taxid;


    public BioSourceBean( ) {
    }

    public String getTaxid() {
        return taxid;
    }

    public void setTaxid( String taxid ) {
        this.taxid = taxid;
    }


    public String toString() {
        return "BioSourceBean{" +
                "taxid='" + taxid + "'" +
                ", shortlabel='" + super.getShortlabel() + "'" +
                ", fullname='" + super.getFullname() + "'" +
                "}";
    }


}