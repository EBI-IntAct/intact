/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

import java.sql.Timestamp;

/**
 * This class represents the Statistics table in the database.
 *
 *  * ----------------------- FIRST NEEDS --------------------------------
 * The Scripts
 *                  create_table_statistics.sql
 *            and   insert_count_statistics.sql
 * should be run before the run of this class.
 *
 * Then check the repository_user.xml : mapping of the IA_Statistics table
 *
 *------------------------------------------------------------------------
 *
 * The corresponding mapping between the both JAVA object and the SQL table
 * is described in the repository_user.xml
 *
 *
 *
 * @author shuet (shuet@ebi.ac.uk)
 * @version : $Id$
 */
public class IntactStatistics {

    ///////////////////////////////////////
    //attributes:
    //8 items which represent the 8 "Statistics" table's columns
    //no need to return the autoincremented ac (primary key)

    /**
     * Specify the ac of the field retrieved.
     * Need to be declared there because it is a field in the repository_user.xml file
     *
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

    /*public IntactStatistics (int proteins,
                             int interactions,
                             int binaryInteractions,
                             int complexInteractions,
                             int experiments,
                             int terms) {
        this.proteinNumber = proteins;
        this.interactionNumber = interactions;
        this.binaryInteractions = binaryInteractions;
        this.complexInteractions = complexInteractions;
        this.experimentNumber = experiments;
        this.termNumber = terms;
    } */


    ///////////////////////////////////////
    //access methods for attributes
    // = properties to get and set attributes

    /**
     * returns the timestamp
     * @return Timestamp
     *
     */
    public int getAc() {
        return (this.ac);
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

     /**
     * returns the timestamp
     * @return Timestamp
     *
     */
    public Timestamp getTimestamp() {
        return (this.timestamp);
    }

    public void setTimestamp(Timestamp timeStamp) {
        this.timestamp = timeStamp;
    }

    /**
     * returns the number of proteins now available in the IntAct Database
     * @return int
     *
     */
    public int getNumberOfProteins() {
        return (this.proteinNumber);
    }

    public void setNumberOfProteins(int proteinNumb) {
        this.proteinNumber = proteinNumb;
    }

    /**
     * returns the number of interactions now available in the IntAct Database
     * @return int
     *
     */
    public int getNumberOfInteractions() {
        return (this.interactionNumber);
    }

    public void setNumberOfInteractions(int interactionNumb) {
        this.interactionNumber = interactionNumb;
    }


    /**
     * returns the number of interactions with two interactors, now available in the IntAct Database
     * @return int
     *
     */
    public int getNumberOfBinaryInteractions() {
        return (this.binaryInteractions);
    }

    public void setNumberOfBinaryInteractions(int binaryInteraction) {
        this.binaryInteractions = binaryInteraction;
    }

    /**
     * returns the number of interactions with more than two interactors, now available in the IntAct Database
     * @return int
     *
     */
    public int getNumberOfComplexInteractions() {
        return (this.complexInteractions);
    }

    public void setNumberOfComplexInteractions(int complexInteraction) {
        this.complexInteractions = complexInteraction;
    }

    /**
     * returns the number of experiments now available in the IntAct Database
     * @return int
     *
     */
    public int getNumberOfExperiments() {
        return (this.experimentNumber);
    }

    public void setNumberOfExperiments(int experimentNumb) {
        this.experimentNumber = experimentNumb;
    }


    /**
     * returns the number of terms in the Controlled Vocabulary table now available in the IntAct Database
     * @return int
     *
     */
    public int getNumberOfGoTerms() {
        return (this.termNumber);
    }

    public void setNumberOfGoTerms(int termNumb) {
        this.termNumber = termNumb;
    }



    public String toString(){
        return " Timestamp: " + this.getTimestamp()
                + "; Number of proteins: " + this.getNumberOfProteins()
                + "; Number of interactions: " + this.getNumberOfInteractions()
                + " of which " + this.getNumberOfBinaryInteractions() + " with 2 interactors "
                + " and " + this.getNumberOfComplexInteractions() + "with more than 2 interactors"
                + "; Number of experiments: "+ this.getNumberOfExperiments()
                + "; Number of terms in Go: " + this.getNumberOfProteins()
                + "\n";
    }
}// end IntactStatistics
