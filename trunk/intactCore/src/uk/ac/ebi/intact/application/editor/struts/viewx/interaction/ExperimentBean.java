/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.application.editor.struts.view.EditBean;

import java.io.Serializable;

/**
 * Bean to store data for an Experiment (in an Interaction).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentBean extends EditBean implements Serializable {

    // Instance Data

    /**
     * The AC.
     */
    private String myAc;

    /**
     * The short label.
     */
    private String myShortLabel;

    /**
     * The full name of the Protein.
     */
    private String myFullName;

    /**
     * Instantiate an object of this class from a Experiment instance.
     * @param experiment the <code>Experiment</code> object.
     */
    public ExperimentBean(Experiment experiment) {
        myAc = experiment.getAc();
        myShortLabel = experiment.getShortLabel();
        myFullName = experiment.getFullName();
    }

    // Read only properties.

    public String getAc() {
        return myAc;
    }

    public String getShortLabel() {
        return myShortLabel;
    }

    public String getFullName() {
        return myFullName;
    }

    /**
     * Returns the topic with a link to show its contents in a window.
     * @return the topic as a browsable link.
     */
    public String getShortLabelLink() {
        return getLink("Experiment", myShortLabel);
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Only returns <tt>true</tt> if the short labels
     * for both objects match.
     * @param obj the object to compare.
     */
    public boolean equals(Object obj) {
        // Identical to this?
        if (obj == this) {
            return true;
        }
        if ((obj != null) && (getClass() == obj.getClass())) {
            // Can safely cast it.
            return myShortLabel == ((ExperimentBean) obj).myShortLabel;
        }
        return false;
    }
}
