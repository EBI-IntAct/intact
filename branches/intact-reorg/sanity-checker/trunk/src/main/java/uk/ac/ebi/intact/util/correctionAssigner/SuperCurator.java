/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.correctionAssigner;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class SuperCurator {

    /**
     * Percentage of pubmed this SuperCurator should correct on the total of pubmed to be corrected.
     */
    private int percentage;

    /**
     * List of ComparableExperimentBean corresponding to the experiments this SuperCurator has to correct.
     */
    Collection experiments = new ArrayList();

    /**
     * Curator name (should be the name of the user in the database).
     */
    private String name;

    /**
     * Collection of pubmed Ids the super curator has to correct.
     */
    private Collection pubmedIds = new ArrayList();

    /**
     * Empty constructor.
     */
    public SuperCurator() {
    }

    /**
     * Constructor taking as argument percentage and name to set the variable percentage and name variable.
     * @param percentage
     * @param name
     */
    public SuperCurator(int percentage, String name) {
        this.percentage = percentage;
        this.name = name;
    }

    /**
     * Getter for the Collection of PubmedIds this superCurator has to correct.
     * @return the Collection pubmedIds.
     */
    public Collection getPubmedIds() {
        return pubmedIds;
    }

    /**
     * Getter for the percentage variable.
     * @return an int representing the percentage variable.
     */
    public int getPercentage() {
        return percentage;
    }

    /**
     * Set the perceantage.
     * @param percentage
     */
    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection getExperiments() {
        return experiments;
    }

    public void addExperiment(ComparableExperimentBean exp){
        experiments.add(exp);
    }

    public void addExperiment(Collection exps){
        experiments.add(exps);
    }

//    public void setExperiments (Collection experiments) {

//        this.experiments = experiments;

//    }

    public void addPubmedId(String pubmed){
        pubmedIds.add(pubmed);
    }

    public String toString() {
        return "SuperCurator{" +
                "percentage=" + percentage +
                ", name='" + name + '\'' +
                '}';
    }
}
