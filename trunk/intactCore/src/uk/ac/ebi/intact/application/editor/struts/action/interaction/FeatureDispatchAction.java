/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ComponentBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * The action class to handle events related to add/edit and delete of a
 * Feature from an Interaction editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureDispatchAction extends AbstractEditorAction {

    /**
     * This method dispatches calls to various methods using the 'dispatch' parameter.
     *
     * @param mapping  - The <code>ActionMapping</code> used to select this instance
     * @param form     - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request  - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     * @return - represents a destination to which the action servlet,
     *         <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     *         or HttpServletResponse.sendRedirect() to, as a result of processing
     *         activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The command associated with the dispatch
        String cmd = intform.getDispatchFeature();

//        System.out.println("Command is: " + cmd);
        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

//        if (cmd.equals(msgres.getMessage("int.proteins.button.feature.add"))) {
//            return add(mapping, intform, request, response);
//        }
        if (cmd.equals(msgres.getMessage("int.proteins.button.feature.edit"))) {
            return edit(mapping, intform, request, response);
        }
        // default is delete feature.
        return delete(mapping, intform, request, response);
    }

//    private ActionForward add(ActionMapping mapping,
//                              InteractionActionForm form,
//                              HttpServletRequest request,
//                              HttpServletResponse response)
//            throws Exception {
//        // Handler to the Intact User.
//        EditUserI user = getIntactUser(request);
//
//        // Still with the interaction view.
//        InteractionViewBean intView = (InteractionViewBean) user.getView();
//
//        // Save the interaction ac to get back to.
//        String ac = intView.getAc();
//
//        // Set the selected topic as other operation use it for various tasks.
//        user.setSelectedTopic("Feature");
//
//        // Set the new object as the current edit object.
//        user.setView(Feature.class);
//
//        // The feature view bean.
//        FeatureViewBean featureView = (FeatureViewBean) user.getView();
//
//        // Set the parent as the Feature doesn't exist in its own.
//        String protAc = form.getSelectedComponent().getAc();
////        Protein protein = (Protein) user.getObjectByAc(Protein.class, protAc);
////        featureView.setParent(protein);
//        // The component, this feature belongs to.
//        featureView.setComponent(intView.getComponent(protAc));
//
//        // The interaction to get back.
//        featureView.setSourceInteractionAc(ac);
//
//        return mapping.findForward(FEATURE);
//    }

    private ActionForward edit(ActionMapping mapping,
                               InteractionActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Still with the interaction view.
        InteractionViewBean intView = (InteractionViewBean) user.getView();

        // Save the interaction ac to get back to.
//        String ac = intView.getAc();

        // Set the selected topic as other operation use it for various tasks.
        user.setSelectedTopic("Feature");

        // The feature we are about to edit.
        Feature feature = (Feature) user.getObjectByAc(
                Feature.class, form.getSelectedFeature().getAc());

        // Set the new object as the current edit object.
        user.setView(feature);
        LOGGER.debug("Selected bean is: " + feature.getAc());

        // The feature view bean.
        FeatureViewBean featureView = (FeatureViewBean) user.getView();

        // Set the parent for the feature view.
        featureView.setParentView(intView);

        // The interaction to get back.
//        ((FeatureViewBean) user.getView()).setSourceInteractionAc(ac);

        return mapping.findForward(FEATURE);
    }

    public ActionForward delete(ActionMapping mapping,
                                InteractionActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
//        System.out.println("I am in the DELETE feature");
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The feature bean we are about to delete.
        Feature feature = (Feature) user.getObjectByAc(
                Feature.class, form.getSelectedFeature().getAc());

        // Delete the feature.
        ((InteractionViewBean) user.getView()).deleteFeature(feature);

        // Reset the dispatch feature.
//        form.resetDispatchFeature();
//        LOGGER.debug("At the end of delete feature action");
//        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
//            String para = (String) e.nextElement();
//            LOGGER.debug("parameter: " + para + " - " + request.getParameter(para));
//        }
//
//        LOGGER.debug("Deleted " + feature.getAc());
        return mapping.getInputForward();
    }
}