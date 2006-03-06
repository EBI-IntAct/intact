/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.model;

import java.sql.Timestamp;


/**
 * @author Michael Kleen
 * @version ExperimentStatistics.java Date: Feb 18, 2005 Time: 1:06:55 PM
 */
public class ExperimentStatistics implements Comparable {

    private Timestamp timestamp;
    private int experimentNumber;
    private int binaryInteractions;
    private int ac;

    public ExperimentStatistics() {
    }

    public int getExperimentNumber() {
        return experimentNumber;
    }

    public void setExperimentNumber(int experimentNumber) {
        this.experimentNumber = experimentNumber;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public int getBinaryInteractions() {
        return binaryInteractions;
    }

    public void setBinaryInteractions(int binaryInteractions) {
        this.binaryInteractions = binaryInteractions;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int compareTo(Object o) {
        final Integer i = new Integer(((BioSourceStatistics) o).getAc());
        Integer toCompare = new Integer(ac);
        return toCompare.compareTo(i);
    }
}
