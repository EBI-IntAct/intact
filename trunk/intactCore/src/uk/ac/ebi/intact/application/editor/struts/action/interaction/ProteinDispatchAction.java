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
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action class for editing a Protein.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinDispatchAction extends AbstractEditorAction {

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
        String cmd = intform.getDispatch();

        System.out.println("Command is: " + cmd);
        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        if (cmd.equals(msgres.getMessage("int.proteins.button.edit"))) {
            return edit(mapping, form, request, response);
        }
        if (cmd.equals(msgres.getMessage("int.proteins.button.save"))) {
            return save(mapping, form, request, response);
        }
//        if (cmd.equals(msgres.getMessage("int.proteins.button.delete"))) {
//            return delete(mapping, form, request, response);
//        }
        if (cmd.equals(msgres.getMessage("int.proteins.button.feature.add"))) {
            return addFeature(mapping, intform, request, response);
        }
//        if (cmd.equals(msgres.getMessage("int.proteins.button.feature.edit"))) {
//            return editFeature(mapping, form, request, response);
//        }
        // default is delete protein.
        return delete(mapping, form, request, response);
    }

    public ActionForward edit(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The protein we are editing at the moment.
        ComponentBean pb = intform.getSelectedComponent();

        // We must have the protein bean.
        assert pb != null;

        // Must save this bean.
        pb.setEditState(AbstractEditBean.SAVE);

        // Update the form.
        return mapping.findForward(SUCCESS);
    }

    public ActionForward save(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The current view of the edit session.
        InteractionViewBean viewbean =
                (InteractionViewBean) getIntactUser(request).getView();

        // The component we are editing at the moment.
        ComponentBean cb = intform.getSelectedComponent();

        // We must have the protein bean.
        assert cb != null;

        // Must define a role for the Protein.
        if (cb.getRole() == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("int.prot.role",
                    new ActionError("error.int.protein.edit.role"));
            saveErrors(request, errors);
            cb.setEditState(ComponentBean.ERROR);
            return mapping.findForward(FAILURE);
        }
        // The protein to update.
        viewbean.addProteinToUpdate(cb);

        // Back to the view mode.
        cb.setEditState(AbstractEditBean.VIEW);

        // Remove the unsaved proteins.
        viewbean.removeUnsavedProteins();
        
        // Update the form.
        return mapping.findForward(SUCCESS);
    }

    public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The current view of the edit session.
        InteractionViewBean view =
                (InteractionViewBean) getIntactUser(request).getView();

        // Delete this Protein from the view.
        view.delProtein(intform.getDispatchIndex());

        // Update the form.
        return mapping.findForward(SUCCESS);
    }

    private ActionForward addFeature(ActionMapping mapping,
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

        // Set the new object as the current edit object.
        user.setView(Feature.class);

        // The feature view bean.
        FeatureViewBean featureView = (FeatureViewBean) user.getView();

        // Set the parent for the feature view.
        featureView.setParentView(intView);
        
        // The selected component from the form.
        ComponentBean selectedComp = form.getSelectedComponent();
        
        // The component for the feature.
        featureView.setComponent(selectedComp.getComponent(user));

        // The interaction to get back.
//        featureView.setSourceInteractionAc(ac);

        return mapping.findForward(FEATURE);
    }
//
//    private ActionForward editFeature(ActionMapping mapping,
//                                    ActionForm form,
//                                    HttpServletRequest request,
//                                    HttpServletResponse response) {
//
//        System.out.println("I am in the edit feature");
//        return mapping.findForward(SUCCESS);
//    }
//
//    public ActionForward deleteFeature(ActionMapping mapping,
//                              ActionForm form,
//                              HttpServletRequest request,
//                              HttpServletResponse response)
//            throws Exception {
//        System.out.println("I am in the DELETE feature");
//        return mapping.findForward(SUCCESS);
//    }
}