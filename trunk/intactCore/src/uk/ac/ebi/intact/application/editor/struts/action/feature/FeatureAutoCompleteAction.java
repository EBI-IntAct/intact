/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.feature;

import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.util.LockManager;
import uk.ac.ebi.intact.model.Interaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action is invoked when Auto-Complete button is pressed.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureAutoCompleteAction extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The form to extract values.
        FeatureActionForm featureForm = ((FeatureActionForm) form);

        // The command associated with the dispatch
        String cmd = featureForm.getDispatch();

        System.out.println("The command is: " + cmd + " selected feature is: " + featureForm.getDefinedFeature());
        // Message resources to access button labels.
//        MessageResources msgres = getResources(request);
//
//        if (cmd.equals(msgres.getMessage("int.proteins.button.feature.add"))) {
//            return mapping.findForward("addFeature");
//        }
//        if (cmd.equals(msgres.getMessage("int.proteins.button.save"))) {
//            return save(mapping, form, request, response);
//        }

//        // The AC of the interaction we are about to edit.
//        String intAc = (String) ((ExperimentActionForm) form).getIntac();//dynaform.get("intac");
//
//        // The interaction we are about to edit.
//        Interaction inter = (Interaction) user.getObjectByAc(
//                Interaction.class, intAc);
//
//        // We must have this Interaction.
//        assert inter != null;
//
//        // Try to acuire the lock.
//        if (!lmr.acquire(intAc, user.getUserName())) {
//            ActionErrors errors = new ActionErrors();
//            // The owner of the lock (not the current user).
//            errors.add(ActionErrors.GLOBAL_ERROR,
//                    new ActionError("error.lock", intAc, lmr.getOwner(intAc)));
//            saveErrors(request, errors);
//            // Show the errors in the input page.
//            return mapping.getInputForward();
//        }
//        // Set the topic.
//        user.setSelectedTopic(getService().getTopic(Interaction.class));
//
//        // Set the interaction as the new view.
//        user.setView(inter);
//
//        // Set the experiment AC, so we can come back to this experiment again.
//        ((InteractionViewBean) user.getView()).setSourceExperimentAc(expAc);

        // Update the form.
        return mapping.findForward(SUCCESS);
    }


}