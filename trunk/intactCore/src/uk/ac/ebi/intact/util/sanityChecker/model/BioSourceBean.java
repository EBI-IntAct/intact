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

    public String getBioSourceXref() {
        return bioSourceXref;
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