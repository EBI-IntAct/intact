/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.feature;

import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ComponentBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.feature.RangeBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action class for editing a Range.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeDispatchAction extends AbstractEditorAction {

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
        FeatureActionForm featureForm = (FeatureActionForm) form;

        // Handler to the EditUserI.
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        FeatureViewBean view =
                (FeatureViewBean) getIntactUser(request).getView();

        // The range we are editing at the moment.
        RangeBean bean = featureForm.getSelectedRange();
//        System.out.println("Selected range: " + bean.getFromRange() + " - " + bean.getToRange());

        // The command associated with the dispatch
        String cmd = featureForm.getDispatch();

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        if (cmd.equals(msgres.getMessage("feature.range.button.edit"))) {
//            return edit(mapping, featureForm, request, response);
            // Set the state to save this bean.
            bean.setEditState(AbstractEditBean.SAVE);
        }

        // Handle Save event.
        else if (cmd.equals(msgres.getMessage("feature.range.button.save"))) {
            // The updated range bean.
            Feature feature = (Feature) view.getAnnotatedObject();
            RangeBean updated = new RangeBean(bean.getRange(feature, user),
                    bean.getKey());

            System.out.println("Updated range: " + updated.getFromRange() + " - " + updated.getToRange());
            // Save the bean in the view.
            view.saveRange(bean, updated);

            // Back to the view mode.
            bean.setEditState(AbstractEditBean.VIEW);
        }
        else {
            // Default is to delete a range. Delete the selected Range
            //  from the view.
            view.delRange(featureForm.getDispatchIndex());
        }
        // Update the form.
        return mapping.getInputForward();
//        return delete(mapping, form, request, response);
    }

//    public ActionForward edit(ActionMapping mapping,
//                              FeatureActionForm form,
//                              HttpServletRequest request,
//                              HttpServletResponse response)
//            throws Exception {
//        // The range we are editing at the moment.
//        RangeBean rb = form.getSelectedRange();
//
//        // We must have the range bean.
//        assert rb != null;
//
//        // Must save this bean.
//        rb.setEditState(AbstractEditBean.SAVE);
//
//        // Update the form.
//        return mapping.findForward(SUCCESS);
//    }

//    public ActionForward save(ActionMapping mapping,
//                              FeatureViewBean view,
//                              RangeBean bean,
//                              EditUserI user)
//            throws Exception {
//        // The current view of the edit session.
////        FeatureViewBean viewbean =
////                (FeatureViewBean) getIntactUser(request).getView();
//
//        // We must have the range bean.
////        assert rb != null;
//
//        // Handler to the EditUserI.
////        EditUserI user = getIntactUser(request);
//
//        // The updated range bean.
//        RangeBean updated = new RangeBean(bean.getRange(user), bean.getKey());
//
//        // Save the bean in the view.
//        view.saveRange(bean, updated);
//
//        // Back to the view mode.
//        bean.setEditState(AbstractEditBean.VIEW);
//
//        // Update the form.
//        return mapping.findForward(SUCCESS);
//    }
//
//    public ActionForward delete(ActionMapping mapping,
//                                FeatureViewBean view,
//                                RangeBean bean,
//                                HttpServletRequest request,
//                                HttpServletResponse response)
//            throws Exception {
//        // The form.
//        // The current view of the edit session.
////        InteractionViewBean view =
////                (InteractionViewBean) getIntactUser(request).getView();
////
//        // The protein we are editing at the moment.
////        ComponentBean pb = intform.getSelectedProtein();
//
//        // We must have the protein bean.
////        assert pb != null;
//
//        // Delete this Protein from the view.
//        view.delRange(intform.getDispatchIndex());
//
//        // Update the form.
//        return mapping.findForward(SUCCESS);
//    }
}