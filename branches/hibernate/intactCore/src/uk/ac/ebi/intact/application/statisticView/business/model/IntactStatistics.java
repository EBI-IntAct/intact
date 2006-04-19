/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.statisticView.business.model;

import java.sql.Timestamp;

/**
 * This class represents the Statistics table in the database.
 * <p/>
 * The Script sql/[oracle|postgres]/insert_count_statistics.sql
 * should be run before to use of this class.
 * <p/>
 * The corresponding mapping between the both JAVA object and the SQL table
 * is described in the repository_user.xml
 *
 * @author shuet (shuet@ebi.ac.uk), Samuel Kerrien (skerrien:ebi.ac.uk), Michael Kleen (mkleen@ebi.ac.uk)
 * @version : $Id$
 */
public class IntactStatistics implements Comparable {

    private int ac;
    private Timestamp timestamp;
    private int proteinNumber;
    private int interactionNumber;
    private int binaryInteractions;
    private int complexInteractions;
    private int experimentNumber;
    private int termNumber;

    public IntactStatistics() {
        this.timestamp = new java.sql.Timestamp(System.currentTimeMillis());
    }

    public int getAc() {
        return (this.ac);
    }

    public void setAc(final int ac) {
        this.ac = ac;
    }

    public Timestamp getTimestamp() {
        return (this.timestamp);
    }

    public void setTimestamp(final Timestamp timeStamp) {
        this.timestamp = timeStamp;
    }

    public int getNumberOfProteins() {
        return (this.proteinNumber);
    }

    public void setNumberOfProteins(final int proteinNumb) {
        this.proteinNumber = proteinNumb;
    }

    public int getNumberOfInteractions() {
        return (this.interactionNumber);
    }

    public void setNumberOfInteractions(final int interactionNumb) {
        this.interactionNumber = interactionNumb;
    }

    public int getNumberOfBinaryInteractions() {
        return (this.binaryInteractions);
    }

    public void setNumberOfBinaryInteractions(final int binaryInteraction) {
        this.binaryInteractions = binaryInteraction;
    }

    public int getNumberOfComplexInteractions() {
        return (this.complexInteractions);
    }

    public void setNumberOfComplexInteractions(final int complexInteraction) {
        this.complexInteractions = complexInteraction;
    }

    public int getNumberOfExperiments() {
        return (this.experimentNumber);
    }

    public void setNumberOfExperiments(final int experimentNumb) {
        this.experimentNumber = experimentNumb;
    }

    public int getNumberOfCvTerms() {
        return (this.termNumber);
    }

    public void setNumberOfCvTerms(final int termNumb) {
        this.termNumber = termNumb;
    }

    public String toString() {
        return " Timestamp: " + this.getTimestamp()
                + "; Number of proteins: " + this.getNumberOfProteins()
                + "; Number of interactions: " + this.getNumberOfInteractions()
                + " of which " + this.getNumberOfBinaryInteractions() + " with 2 interactors "
                + " and " + this.getNumberOfComplexInteractions() + "with more than 2 interactors"
                + "; Number of experiments: " + this.getNumberOfExperiments()
                + "; Number of terms in Go: " + this.getNumberOfProteins()
                + "\n";
    }

    public int compareTo(final Object o) {

        final Timestamp t = ((IntactStatistics) o).getTimestamp();
        return timestamp.compareTo(t);
    }
}
