/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.experiment;

import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.validation.ValidationException;
import uk.ac.ebi.intact.application.editor.exception.validation.ExperimentException;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.business.IntactException;
import org.apache.struts.tiles.ComponentContext;

import java.util.Map;

/**
 * Experiment edit view bean.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentViewBean extends AbstractEditViewBean {

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
            setOrganism(biosrc.getShortLabel());
        }
        CvInteraction inter = exp.getCvInteraction();
        if (inter != null) {
            setInteraction(exp.getCvInteraction().getShortLabel());
        }
        CvIdentification ident = exp.getCvIdentification();
        if (ident != null) {
            setIdentification(exp.getCvIdentification().getShortLabel());
        }
    }

    // Override the super method to this bean's info.
    public void persist(EditUserI user) throws IntactException, SearchException {
        // The order is important! update super last as it does
        // the update of the object.
        Experiment exp = (Experiment) getAnnotatedObject();

        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteraction interaction = (CvInteraction) user.getObjectByLabel(
                CvInteraction.class, myInteraction);
        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
                CvIdentification.class, myIdentification);

        // Update the experiment object.
        exp.setBioSource(biosource);
//        exp.setBioSourceAc(biosource.getAc());
        exp.setCvInteraction(interaction);
//        exp.setCvInteractionAc(interaction.getAc());
        exp.setCvIdentification(ident);
//        exp.setCvIdentificationAc(ident.getAc());
        super.persist(user);
    }

    // Ovverride to provide Experiment layout.
    public void setLayout(ComponentContext context) {
        context.putAttribute("content", "edit.exp.layout");
    }

    // Null for any of these values will throw an exception.
    public void validate(EditUserI user) throws ValidationException,
            SearchException {
        super.validate(user);
        // Get the objects using their short label.
        BioSource biosource = (BioSource) user.getObjectByLabel(
                BioSource.class, myOrganism);
        CvInteraction interaction = (CvInteraction) user.getObjectByLabel(
                CvInteraction.class, myInteraction);
        CvIdentification ident = (CvIdentification) user.getObjectByLabel(
                CvIdentification.class, myIdentification);
        // We should have these proper objects.
        if ((biosource == null) || (interaction == null) || (ident == null)) {
            throw new ExperimentException();
        }
    }

    public Map getEditorMenus() throws SearchException {
        // The map to return.
        Map map = null;
        // The object we are editing at the moment.
        Experiment exp = (Experiment) getAnnotatedObject();
        if (exp.getBioSource() == null) {
            // Adding a new Experiment.
            map = getMenuFactory().getExperimentMenus(1);
        }
        else {
            // Editing an existing experiment.
            map = getMenuFactory().getExperimentMenus(0);
        }
        return map;
    }

    // Getter/Setter methods for Organism.
    public String getOrganism() {
        return myOrganism;
    }

    public void setOrganism(String organism) {
        myOrganism = organism;
    }

    // Getter/Setter methods for Interaction.
    public String getInteraction() {
        return myInteraction;
    }

    public void setInteraction(String interaction) {
        myInteraction = interaction;
    }

    // Getter/Setter methods for Identification.
    public String getIdentification() {
        return myIdentification;
    }

    public void setIdentification(String identification) {
        myIdentification = identification;
    }
}
