/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.model.Interaction;

import java.io.Serializable;

/**
 * Bean to store data for an Interaction (in an Experiment).
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionBean extends AbstractEditBean implements Serializable {

    // Instance Data

    /**
     * Reference to the object this instance is created with.
     */
    private Interaction myInteraction;

    /**
     * Instantiate an object of this class from an Interaction instance.
     * @param interaction the <code>Interaction</code> object.
     */
    public InteractionBean(Interaction interaction) {
        myInteraction = interaction;
    }

    // Read only properties.

    public Interaction getInteraction() {
        return myInteraction;
    }

    public String getAc() {
        return myInteraction.getAc();
    }

    public String getShortLabel() {
        return myInteraction.getShortLabel();
    }

    public String getFullName() {
        return myInteraction.getFullName();
    }

    /**
     * Returns the topic with a link to show its contents in a window.
     * @return the topic as a browsable link.
     */
    public String getShortLabelLink() {
        return getLink("Interaction", myInteraction.getShortLabel());
    }

    // Override Objects's equal method.

    /**
     * Compares <code>obj</code> with this object according to
     * Java's equals() contract. Delegates the task to
     * {@link uk.ac.ebi.intact.model.Interaction#equals(Object)}.
     * @param obj the object to compare.
     * @return true only if <code>obj</code> is an instance of this class
     * and its wrapped Interaction equals to this object's Interaction. For all
     * other instances, false is returned.
     */
    public boolean equals(Object obj) {
        if (obj instanceof InteractionBean) {
            return myInteraction.equals(((InteractionBean) obj).myInteraction);
        }
        return false;
    }
}
