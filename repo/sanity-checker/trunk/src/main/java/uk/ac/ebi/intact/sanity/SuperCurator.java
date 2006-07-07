/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity;

import uk.ac.ebi.intact.util.correctionAssigner.ComparableExperimentBean;

import java.util.Collection;
import java.util.ArrayList;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class SuperCurator extends Curator {

    /**
     * Percentage of pubmed this SuperCurator should correct on the total of pubmed to be corrected.
     */
    private int percentage;

    /**
     * List of ComparableExperimentBean corresponding to the experiments this SuperCurator has to correct.
     */
    Collection<ComparableExperimentBean> experiments = new ArrayList<ComparableExperimentBean>();

    /**
     * Collection of pubmed Ids the super curator has to correct.
     */
    private Collection<String> pubmedIds = new ArrayList<String>();

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
    public SuperCurator(int percentage, String id) {
        this.percentage = percentage;
        super.setId(id);
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

    public Collection<ComparableExperimentBean> getExperiments() {
        return experiments;
    }

    public void addExperiment(ComparableExperimentBean exp){
        experiments.add(exp);
    }

    public void addExperiment(Collection<ComparableExperimentBean> exps){
        experiments.addAll(exps);
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
                ", id='" + getId() + '\'' +
                '}';
    }
}
