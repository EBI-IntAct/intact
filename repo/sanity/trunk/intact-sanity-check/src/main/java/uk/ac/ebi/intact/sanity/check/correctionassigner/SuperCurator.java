/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.sanity.check.correctionassigner;

import uk.ac.ebi.intact.sanity.check.model.ComparableExperimentBean;

import java.util.ArrayList;
import java.util.Collection;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id: SuperCurator.java,v 1.1 2006/04/05 16:02:35 catherineleroy Exp $
 */
public class SuperCurator {

    /**
     * Percentage of pubmed this SuperCurator should correct on the total of pubmed to be corrected.
     */
    private int percentage;

    /**
     * List of ComparableExperimentBean corresponding to the experiments this SuperCurator has to correct.
     */
    Collection<ComparableExperimentBean> experiments = new ArrayList<ComparableExperimentBean>();

    /**
     * Curator name (should be the name of the user in the database).
     */
    private String name;

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
    public SuperCurator(int percentage, String name) {
        this.percentage = percentage;
        this.name = name;
    }

    /**
     * Getter for the Collection of PubmedIds this superCurator has to correct.
     * @return the Collection pubmedIds.
     */
    public Collection<String> getPubmedIds() {
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

    public Collection<ComparableExperimentBean> getExperiments() {
        return experiments;
    }

    public void addExperiment(ComparableExperimentBean exp){
        experiments.add(exp);
    }

    public void addExperiments(Collection<ComparableExperimentBean> exps){
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
                ", name='" + name + '\'' +
                '}';
    }
}
