/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import uk.ac.ebi.intact.application.editor.struts.action.FillCvFormAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ExperimentBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ProteinBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Populates Interaction form for display.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FillInteractionFormAction extends FillCvFormAction {

    // Override Actions's execure method.
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Fill the form with common info.
        super.execute(mapping, form, request, response);

        // The view of the current object we are editing at the moment.
        InteractionViewBean view = (InteractionViewBean)
                getIntactUser(request).getView();

        // Poplulate with experiment data; first try the form first, so it
        // will save the last typed values.
        DynaActionForm dynaform = (DynaActionForm) form;

        // Assumption: We are creating a new record if the current view has no
        // short label. Reset the previous values.
        if (view.getShortLabel() == null) {
            dynaform.set("kD", Float.valueOf("0.0"));
            dynaform.set("organism", null);
            dynaform.set("interactionType", null);

            // Clear the search forms as well.
            dynaform.set("expSearchLabel", null);
            dynaform.set("expSearchAC", null);
            dynaform.set("protSearchLabel", null);
            dynaform.set("protSearchSpAC", null);
            dynaform.set("protSearchAC", null);
        }
        else {
            dynaform.set("kD", view.getKD());
            dynaform.set("organism", view.getOrganism());
            dynaform.set("interactionType", view.getInteractionType());
        }
        // Populate with the experiments for the interaction.
        List exps = view.getExperiments();
        dynaform.set("exps", exps.toArray(new ExperimentBean[0]));
        dynaform.set("expCmd", new String[exps.size()]);

        // Populate with experiments on hold.
        List expshold = view.getHoldExperiments();
        dynaform.set("expshold", expshold.toArray(new ExperimentBean[0]));
        dynaform.set("expsholdCmd", new String[expshold.size()]);

        // Populate with Proteins
        List proteins = view.getProteins();
        dynaform.set("proteins", proteins.toArray(new ProteinBean[0]));
        dynaform.set("protCmd", new String[proteins.size()]);

        return mapping.findForward(SUCCESS);
    }
}
