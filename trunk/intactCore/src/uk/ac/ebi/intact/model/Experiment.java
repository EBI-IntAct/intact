/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.util.*;

/**
 * Represents one experiment. Describes the conditions in which
 * the experiment has been performed. The information should allow to
 * classify experiments and make them comparable.
 * The Experiment object does not
 * aim to contain enough information to redo the experiment,
 * it refers to the original publication for this purpose.
 *
 * @author hhe
 */
public class Experiment extends AnnotatedObject implements Editable {

    ///////////////////////////////////////
    // associations

    //attributes used for mapping BasicObjects - project synchron
    private String detectionMethodAc;
    private String identMethodAc;
    private String relatedExperimentAc;
    private String cvIdentificationAc;
    private String cvInteractionAc;
    private String bioSourceAc;

    /**
     *
     */
    public Collection interaction = new Vector();
    /**
     * One experiment should group all interactions from a publication
     * which have been performed under the same conditions.
     * However, one experiment might explicitely involve different
     * conditions, for example a time series, or before and after a stimulus.
     * This association can establish this relation.
     *
     * This might be extended into an association class
     * which could state the type of relationship.
     */
    public Experiment relatedExperiment;
    /**
     *
     */
    public CvIdentification cvIdentification;
    /**
     *
     */
    public CvInteraction cvInteraction;
    /**
     *
     */
    public BioSource bioSource;


    ///////////////////////////////////////
    // access methods for associations

    public void setInteraction(Collection someInteraction) {
        this.interaction = someInteraction;
    }
    public Collection getInteraction() {
        return interaction;
    }
    public void addInteraction(Interaction interaction) {
        if (! this.interaction.contains(interaction)) {
            this.interaction.add(interaction);
            interaction.addExperiment(this);
        }
    }
    public void removeInteraction(Interaction interaction) {
        boolean removed = this.interaction.remove(interaction);
        if (removed) interaction.removeExperiment(this);
    }
    public Experiment getRelatedExperiment() {
        return relatedExperiment;
    }

    public void setRelatedExperiment(Experiment experiment) {
        this.relatedExperiment = experiment;
    }
    public CvIdentification getCvIdentification() {
        return cvIdentification;
    }

    public void setCvIdentification(CvIdentification cvIdentification) {
        this.cvIdentification = cvIdentification;
    }
    public CvInteraction getCvInteraction() {
        return cvInteraction;
    }

    public void setCvInteraction(CvInteraction cvInteraction) {
        this.cvInteraction = cvInteraction;
    }
    public BioSource getBioSource() {
        return bioSource;
    }

    public void setBioSource(BioSource bioSource) {
        this.bioSource = bioSource;
    }

    //attributes used for mapping BasicObjects - project synchron
    public String getRelatedExperimentAc(){
        return this.relatedExperimentAc;
    }
    public void setRelatedExperimentAc(String ac){
        this.relatedExperimentAc = ac;
    }
    public String getCvIdentificationAc(){
        return this.cvIdentificationAc;
    }
    public void setCvIdentificationAc(String ac){
        this.cvIdentificationAc = ac;
    }
    public String getCvInteractionAc(){
        return this.cvInteractionAc;
    }
    public void setCvInteractionAc(String ac){
        this.cvInteractionAc = ac;
    }
    public String getBioSourceAc(){
        return this.bioSourceAc;
    }
    public void setBioSourceAc(String ac){
        this.bioSourceAc = ac;
    }


    ///////////////////////////////////////
    // instance methods

    public String toString(){
        String result;
        Iterator i;

        result = "Experiment: " + this.getAc() + " " + this.shortLabel + " [ \n";
        if (null != this.getInteraction()){
            i = this.getInteraction().iterator();
            while(i.hasNext()){
                result = result + i.next();
            }
        }

        return result + "] Experiment\n";
    }

} // end Experiment




