/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.model.Experiment;

import java.io.Serializable;

/**
 * Bean to store data for an Experiment (in an Interaction).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentBean extends AbstractEditBean implements Serializable {

    // Instance Data

    /**
     * Reference to the object this instance is created with.
     */
    private Experiment myExperiment;

    /**
     * Instantiate an object of this class from a Experiment instance.
     * @param experiment the <code>Experiment</code> object.
     */
    public ExperimentBean(Experiment experiment) {
        myExperiment = experiment;
    }

    // Read only properties.

    public Experiment getExperiment() {
        return myExperiment;
    }

    public String getAc() {
        return myExperiment.getAc();
    }

    public String getShortLabel() {
        return myExperiment.getShortLabel();
    }

    public String getFullName() {
        return myExperiment.getFullName();
    }

    /**
     * Returns the topic with a link to show its contents in a window.
     * @return the topic as a browsable link.
     */
    public String getShortLabelLink() {
        return getLink("Experiment", myExperiment.getShortLabel());
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Delegates the task to
     * {@link uk.ac.ebi.intact.model.Experiment#equals(Object)}.
     * @param obj the object to compare.
     * @return true only if <code>obj</code> is an instance of this class
     * and its wrapped Experiment equals to this object's Experiment. For all
     * other instances, false is returned.
     */
    public boolean equals(Object obj) {
        if (obj instanceof ExperimentBean) {
            return myExperiment.equals(((ExperimentBean) obj).myExperiment);
        }
        return false;
    }
}
