/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.model;

import java.sql.Timestamp;


/**
 * This class represents the Biosourcestatistics table in the database.
 * <p/>
 * The Script sql/[oracle|postgres]/insert_biosource.pl
 * should be run before to use of this class.
 * <p/>
 * The corresponding mapping between the both JAVA object and the SQL table
 * is described in the repository_user.xml
 *
 * @author Michael Kleen
 * @version BioSourceStatistics.java Date: Oct 12, 2004 Time: 3:57:09 PM
 */
public class BioSourceStatistics implements Comparable {

    private String taxId;
    private String shortlabel;
    private Timestamp timestamp;
    private int binaryInteractions;
    private int proteinNumber;
    private int ac;

    public String getShortlabel() {
        return shortlabel;
    }

    public void setShortlabel(String shortlabel) {
        this.shortlabel = shortlabel;
    }

    public final int getAc() {
        return ac;
    }

    public final void setAc(int ac) {
        this.ac = ac;
    }

    public final Timestamp getTimestamp() {
        return timestamp;
    }

    public final void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public final String getTaxId() {
        return taxId;
    }

    public final void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public final int getBinaryInteractions() {
        return binaryInteractions;
    }

    public final void setBinaryInteractions(int binaryInteractions) {
        this.binaryInteractions = binaryInteractions;
    }

    public final int getProteinNumber() {
        return proteinNumber;
    }

    public final void setProteinNumber(int proteinNumber) {
        this.proteinNumber = proteinNumber;
    }

    public final int compareTo(Object o) {
        //  final Timestamp t = ( (BioSourceStatistics) o ).getUpdated();

        BioSourceStatistics bs = null;
        if( o instanceof BioSourceStatistics ) {
            bs = (BioSourceStatistics) o;
        }

        // sort the stats by decreasing interaction count
        return bs.getBinaryInteractions() - binaryInteractions;
    }

    public String toString() {
        return "BioSourceStatistics[[ac]" + this.ac + ",[shortlabel] " + this.shortlabel +
                ", [taxid]" + this.taxId + ", [proteinNumbers] " + this.proteinNumber +
                ", [binaryInteractions]" + this.binaryInteractions;
    }
}