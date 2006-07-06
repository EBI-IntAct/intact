/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.correctionAssigner;

import java.sql.Timestamp;
import java.sql.Date;

/**
 * Instead I could have used the ExperimentBean object Used by the util.sanityChecker.SanityChecker class but I needed
 * the ExperimentBean to implements Comparable, this was involving change on the ExperimentBean class and super class
 * (initializing global variable, adding compareTo method) that would have given to those bean a behavious that couldn't
 * feet for both SanityChecker and Assigner class.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class ComparableExperimentBean implements Comparable{

    /**
     * The ac of the Experiment.
     */
    String ac = new String();

    /**
     * The shortlabel of the Experiment.
     */
    String shortlabel = new String();

    /**
     * The name of the user who created the Experiment.
     */
    String created_user = new String();

    /**
     * The pubmedId corresponding to this experiment.
     */
    String pubmedId = new String();

    /**
     * The Timestamp corresponding to the creation time of the Experiment.
     */
    Date created = new Date(1);//= new Timestamp(1);

    /**
     * The name of the superCurator who is suppose to correct the Experiment.
     */
    String reviewer = new String();

    /**
     * Empty constructor.
     */
    public ComparableExperimentBean() {
    }

    /**
     * Ac getter.
     * @return the ac of the Experiment.
     */
    public String getAc() {
        return ac;
    }

    /**
     * Ac setter.
     * @param ac the String used to set the value of the ac.
     */
    public void setAc(String ac) {
        this.ac = ac;
    }

    /**
     * Shortlabel getter.
     * @return return the shortlabel of the Experiment.
     */
    public String getShortlabel() {
        return shortlabel;
    }

    /**
     * Shortlabel setter.
     * @param shortlabel the String used to set the value of the shortlabel.
     */
    public void setShortlabel(String shortlabel) {
        this.shortlabel = shortlabel;
    }

    /**
     * Created_user getter.
     * @return the name of the curator who created the experiment.
     */
    public String getCreated_user() {
        return created_user;
    }

    /**
     * created_user setter,
     * @param created_user the String used to set the value of the created_user.
     */
    public void setCreated_user(String created_user) {
        this.created_user = created_user;
    }

    /**
     * PubmedId setter.
     * @return the pubmedId corresponding to the Experiment. (the xref having as qualifier primary-reference and as
     * database pubmed.
     */
    public String getPubmedId() {
        return pubmedId;
    }

    /**
     * PubmedId setter.
     * @param pubmedId the String used to set the value of the pubmedId.
     */
    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }

    /**
     * Created getter.
     * @return the a Timestamp object representing the time of creation of the Experiment.
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Created setter.
     * @param created A timestamp object used to set the value of the created value.
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * Reviewer setter.
     * @return A String representing the name of the super-curator who is assigned to correct this experiment. This
     * information is stored in an Annotation to the Experiment having as cvTopic 'reviewer' and as description the name
     * ot the super-curator.
     */
    public String getReviewer() {
        return reviewer;
    }

    /**
     * Reviewer setter.
     * @param reviewer a string used to set the reviewer variable.
     */
    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    /**
     * Test if the current ComparableExperimentBean is equal to the Object o given in parameter.
     * If both objects are stored in the same memory space then they are equal.
     * If o is null or o is not the same class then the current ComparableExperimentBean, both objects are not equal.
     * Then if the ac, shortlabel, created_user, created, pubmedId and reviewer are equal, then both objects are equal.
     * @param o The object you want to check for equality to this ComparableExperimentBean.
     * @return true if o is equal to this, false otherwise.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ComparableExperimentBean that = (ComparableExperimentBean) o;

        if(!ac.equals(that.ac)) return false;
        if(!shortlabel.equals(that.shortlabel)) return false;
        if(!created_user.equals(that.created_user)) return false;
        if(!created.equals(that.created)) return false;
        if (!pubmedId.equals(that.pubmedId)) return false;
        if (!reviewer.equals(that.reviewer)) return false;

        return true;
    }

    /**
     * Return an int representing the object.
     * @return int representing the object.
     */
    public int hashCode() {
        int result;
        result = (ac != null ? ac.hashCode() : 0);
        result = 29 * result + (shortlabel != null ? shortlabel.hashCode() : 0);
        result = 29 * result + (created_user != null ? created_user.hashCode() : 0);
        result = 29 * result + (pubmedId != null ? pubmedId.hashCode() : 0);
        result = 29 * result + (created != null ? created.hashCode() : 0);
        result = 29 * result + (reviewer != null ? reviewer.hashCode() : 0);
        return result;
    }

    /**
     * The object ComparableExperimentBean implement the interface comparable, therefore it must implements the method
     * compareTo. As it implements this method you can for exemple sort a List of ComparableExperimentBean using the
     * static method Collections.sort(myListOfComparableExperimentBeans).
     * In this case, I decided that comparing the experimentBean will be equivalant to comparing the pubmedIds.
     *
     * @param o
     * @return return the int 0 if o.pubmed is equal to the current object pubmed.
     *         return -1 if o.pubmed is bigger then the current object pubmed.
     *         return 1 if o.pubmed is smaller then the current object pubmed.
     *         return 2 if the current pubmed object is null, or if o.pubmed is null
     */
    public int compareTo(Object o) {

        ComparableExperimentBean experiment = (ComparableExperimentBean) o;

        if(this.pubmedId == null || experiment.pubmedId == null){
            return 2;
        }
        else{
            if(Integer.parseInt(this.pubmedId) < Integer.parseInt(experiment.getPubmedId())){
                return -1;
            }else if(Integer.parseInt(this.pubmedId) == Integer.parseInt(experiment.getPubmedId())){
                return 0;
            }else return 1;
        }

    }


}
