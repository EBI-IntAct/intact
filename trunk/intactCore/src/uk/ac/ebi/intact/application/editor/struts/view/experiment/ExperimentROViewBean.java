/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractROViewBean;
import uk.ac.ebi.intact.model.*;

/**
 * Experiment read only view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentROViewBean extends AbstractROViewBean {

    /**
     * The host organism.
     */
    private String myOrganism;

    /**
     * The interaction with the experiment.
     */
    private String myInteraction;

    /**
     * The experiment identification.
     */
    private String myIdentification;

    // Override the super method to set the tax id.
    public void setAnnotatedObject(Experiment exp) {
        super.setAnnotatedObject(exp);
        // Only set the short labels if the experiment has the objects.
        BioSource biosrc = exp.getBioSource();
        if (biosrc != null) {
            myOrganism = biosrc.getShortLabel();
        }
        CvInteraction inter = exp.getCvInteraction();
        if (inter != null) {
            myInteraction = exp.getCvInteraction().getShortLabel();
        }
        CvIdentification ident = exp.getCvIdentification();
        if (ident != null) {
            myIdentification = exp.getCvIdentification().getShortLabel();
        }
    }

    // Getter/Setter methods for Organism.
    public String getOrganism() {
        return myOrganism;
    }

    // Getter/Setter methods for Interaction.
    public String getInteraction() {
        return myInteraction;
    }

    // Getter/Setter methods for Identification.
    public String getIdentification() {
        return myIdentification;
    }
}
