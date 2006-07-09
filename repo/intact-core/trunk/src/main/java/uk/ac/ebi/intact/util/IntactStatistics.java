/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

/**
 * This class represents the Statistics table in the database.
 *
 * The Script sql/[oracle|postgres]/insert_count_statistics.sql
 * should be run before to use of this class.
 *
 * The corresponding mapping between the both JAVA object and the SQL table
 * is described in the repository_user.xml
 *
 * @author shuet (shuet@ebi.ac.uk), Samuel Kerrien (skerrien:ebi.ac.uk)
 * @version : $Id$
 */
public class IntactStatistics implements Comparable {

    private static final Log log = LogFactory.getLog(IntactStatistics.class);

    ///////////////////////////////////////
    //attributes:
    //8 items which represent the 8 "Statistics" table's columns
    //no need to return the autoincremented ac (primary key)

    /**
     * Specify the ac of the field retrieved.
     * Need to be declared there because it is a field in the repository_user.xml file
     */
    protected int ac;

    /**
     * Specify the date of the object storing.
     * The type is java.sql.Date, not java.util.Data,
     * for database compatibility.
     */
    protected Timestamp timestamp;

    /*
     * other attributes which describe the amount of data in IntAct
     */
    protected int proteinNumber;
    protected int interactionNumber;
    protected int binaryInteractions;
    protected int complexInteractions;
    protected int experimentNumber;
    protected int termNumber;


    ///////////////////////////////////////
    // constructors
    public IntactStatistics () {
        this.timestamp = new java.sql.Timestamp(System.currentTimeMillis());
    }


    ///////////////////////////////////////
    //access methods for attributes
    ///////////////////////////////////////

    /**
     * returns the timestamp
     * @return Timestamp
     *
     */
    public final int getAc() {
        return (this.ac);
    }

    public final void setAc(final int ac) {
        this.ac = ac;
    }

     /**
     * returns the timestamp
     * @return Timestamp
     *
     */
    public final Timestamp getTimestamp() {
        return (this.timestamp);
    }

    public final void setTimestamp(final Timestamp timeStamp) {
        this.timestamp = timeStamp;
    }

    /**
     * returns the number of proteins now available in the IntAct Database
     * @return int
     *
     */
    public final int getNumberOfProteins() {
        return (this.proteinNumber);
    }

    public final void setNumberOfProteins(final int proteinNumb) {
        this.proteinNumber = proteinNumb;
    }

    /**
     * returns the number of interactions now available in the IntAct Database
     * @return int
     *
     */
    public final int getNumberOfInteractions() {
        return (this.interactionNumber);
    }

    public final void setNumberOfInteractions(final int interactionNumb) {
        this.interactionNumber = interactionNumb;
    }

    /**
     * returns the number of interactions with two interactors, now available in the IntAct Database
     * @return int
     *
     */
    public final int getNumberOfBinaryInteractions() {
        return (this.binaryInteractions);
    }

    public final void setNumberOfBinaryInteractions(final int binaryInteraction) {
        this.binaryInteractions = binaryInteraction;
    }

    /**
     * returns the number of interactions with more than two interactors, now available in the IntAct Database
     * @return int
     *
     */
    public final int getNumberOfComplexInteractions() {
        return (this.complexInteractions);
    }

    public final void setNumberOfComplexInteractions(final int complexInteraction) {
        this.complexInteractions = complexInteraction;
    }

    /**
     * returns the number of experiments now available in the IntAct Database
     * @return int
     *
     */
    public final int getNumberOfExperiments() {
        return (this.experimentNumber);
    }

    public final void setNumberOfExperiments(final int experimentNumb) {
        this.experimentNumber = experimentNumb;
    }

    /**
     * returns the number of terms in the Controlled Vocabulary table now available in the IntAct Database
     * @return int
     *
     */
    public final int getNumberOfGoTerms() {
        return (this.termNumber);
    }

    public final void setNumberOfGoTerms(final int termNumb) {
        this.termNumber = termNumb;
    }

    @Override
    public final String toString(){
        return " Timestamp: " + this.getTimestamp()
                + "; Number of proteins: " + this.getNumberOfProteins()
                + "; Number of interactions: " + this.getNumberOfInteractions()
                + " of which " + this.getNumberOfBinaryInteractions() + " with 2 interactors "
                + " and " + this.getNumberOfComplexInteractions() + "with more than 2 interactors"
                + "; Number of experiments: "+ this.getNumberOfExperiments()
                + "; Number of terms in Go: " + this.getNumberOfProteins()
                + "\n";
    }


    ////////////////////////////////////////////////
    // Implementation of the Comparable interface
    ////////////////////////////////////////////////

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     *
     * @param   o the Object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		    is less than, equal to, or greater than the specified object.
     *
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this Object.
     */
    public final int compareTo( final Object o ) {

        final Timestamp t = ( (IntactStatistics) o ).getTimestamp();
        return timestamp.compareTo( t );
    }
}
