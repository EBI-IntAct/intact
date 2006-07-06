/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.model;

import java.sql.Timestamp;


/**
 * @author Michael Kleen
 * @version identificationMethods.java Date: Feb 18, 2005 Time: 1:06:33 PM
 */
public class IdentificationMethodStatistics implements Comparable {

    private Timestamp timestamp;
    private String detectionName;
    private int numberInteractions;
    private int ac;

    public IdentificationMethodStatistics() {
    }

    public String getDetectionName() {
        return detectionName;
    }

    public void setDetectionName(String detectionName) {
        this.detectionName = detectionName;
    }

    public int getNumberInteractions() {
        return numberInteractions;
    }

    public void setNumberInteractions(int numberInteractions) {
        this.numberInteractions = numberInteractions;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int compareTo(Object o) {
        final String detectionName = ((IdentificationMethodStatistics) o).getDetectionName();
        return detectionName.compareTo(detectionName);
    }


}
