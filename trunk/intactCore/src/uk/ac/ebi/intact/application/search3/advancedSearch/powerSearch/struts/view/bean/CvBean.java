/*
* Copyright (c) 2002 The European Bioinformatics Institute, and others.
* All rights reserved. Please see the file LICENSE
* in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.search3.advancedSearch.powerSearch.struts.view.bean;

/**
 * this class is used to store information about cv objects.
 * It is used to store the shortlabels of the CVs, that are shown in the drop down lists.
 *
 * @author Anja Friedrichsen
 * @version $id$
 */
public class CvBean {


    private String ac = null;

    private String shortlabel = null;

    private String fullname = null;

    public CvBean(String ac, String shortlabel, String fullname) {
        this.ac = ac;
        this.shortlabel = shortlabel;
        this.fullname = fullname;
    }

    public String getAc() {
        return ac;
    }

    public void setAc(String ac) {
        this.ac = ac;
    }

    public String getShortlabel() {
        return shortlabel;
    }

    public void setShortlabel(String shortlabel) {
        this.shortlabel = shortlabel;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


}
