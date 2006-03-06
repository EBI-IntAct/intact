/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.intSeq.struts.view.utils;

/**
 * Bean to display a result object from srs. This bean is used by the ProteinDecorator
 * to decorate the data before its display in the srsSearchResults.jsp page
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class SrsResultBean {

    /**
     * 2 items to full the two columns of the displayed table.
     */
    protected String acc = "";
    protected String url = "";
    protected String des = "";

    /**
     * constructor by default.
     */
    public SrsResultBean () {

    }

     /**
     * constructs an instance of this class for a given object, composed with 2 items.
     *
     * @param accNum the accession number from the SP-Tr database.
     * @param description the description field in SRS, to describe the protein retrieved.
     *
     */
    public SrsResultBean (String accNum, String url, String description) {
        this.acc = accNum;
        this.url = url;
        this.des = description;
    }


        // -------- PROPERTIES ------------//

    /**
     * Set the accession number field.
     * @param accNum
     *
     */
    public void setAcc (String accNum) {
        this.acc = accNum;
    }

    /**
     * returns the accession number
     * @return String
     *
     */
    public String getAcc () {
        return (this.acc);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

     /**
     * Set the description field.
     * @param description
     *
     */
     public void setDes (String description) {
        this.des = description;
    }

    /**
     * returns the description
     * @return String
     *
     */
    public String getDes () {
        return (this.des);
    }


}
