/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.interaction;

import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.commons.util.XrefHelper;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Xref;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

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
     * Returns the pubmed id as a link.
     * @return the pubmed id as a browsable link.
     */
    public String getPubMedLink() {
        for (Iterator iter = myExperiment.getXrefs().iterator(); iter.hasNext(); ) {
            Xref xref = (Xref) iter.next();
            if (xref.getCvXrefQualifier().getShortLabel().equals("primary-reference")) {
                String link = XrefHelper.getPrimaryIdLink(xref);
                // javascipt to display the link is only for a valid link.
                if (link.startsWith("http://")) {
                    return "<a href=\"" + "javascript:showXrefPId('" + link + "')\"" + ">"
                            + xref.getPrimaryId() + "</a>";
                }
            }
        }
        return null;
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
     * Java's equals() contract.
     * @param obj the object to compare.
     * @return true only if <code>obj</code> is an instance of this class
     * and its AC equals to this object's AC; for all other instances, false
     * is returned.
     */
    public boolean equals(Object obj) {
        if (obj instanceof ExperimentBean) {
            return getAc().equals(((ExperimentBean) obj).getAc());
        }
        return false;
    }
}
