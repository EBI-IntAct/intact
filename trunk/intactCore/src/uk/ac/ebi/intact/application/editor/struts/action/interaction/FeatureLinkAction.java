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
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Protein;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * The action class to handle events related to link/unlink of a feature from
 * the Interaction editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureLinkAction extends AbstractEditorAction {

    /**
     * This method handles events related to link/unlink of a feature.
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

        // The dispatch command
        String cmd = intform.getDispatch();

//        System.out.println("Command is: " + cmd);
        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

//        if (cmd.equals(msgres.getMessage("int.proteins.button.feature.add"))) {
//            return add(mapping, intform, request, response);
//        }
        if (cmd.equals(msgres.getMessage("int.proteins.button.feature.link"))) {
            return link(mapping, intform, request, response);
        }
        // default is unlink.
        return unlink(mapping, intform, request, response);
    }

    private ActionForward link(ActionMapping mapping,
                               InteractionActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response)
            throws Exception {
//        System.out.println("In the LINK feature");
//        // The two ACs to interact.
        FeatureBean[] fbs = form.getFeatureACsForLink();

        // Update the domain.
        fbs[0].setBoundDomain(fbs[1].getShortLabel());
//
//        int idx = 0;
//        // Get the feature ACs for two linked items.
//        for (Iterator iter0 = form.getComponents().iterator();
//             iter0.hasNext() && acs[1] == null;) {
//            ComponentBean compBean = (ComponentBean) iter0.next();
//            for (Iterator iter1 = compBean.getFeatures().iterator();
//                 iter1.hasNext() && acs[1] == null;) {
//                FeatureBean featureBean = (FeatureBean) iter1.next();
//                if (featureBean.isLinked()) {
//                    acs[idx] = featureBean.getAc();
//                    ++idx;
//                }
//            }
//        }
//        // We should have those two links.
//        assert ((acs[0] == null) && (acs[1] == null));

//        System.out.println("Linking feature ac: " + acs[0] + " and " + acs[1]);
//        LOGGER.debug("Selected bean is: " + form.getSelectedFeatureAc());
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The first feature to interact with.
//        Feature feature1 = (Feature) user.getObjectByAc(Feature.class, acs[0]);
//
//        // The second feature to interact with.
//        Feature feature2 = (Feature) user.getObjectByAc(Feature.class, acs[1]);
//
//        feature1.setBoundDomain(feature2);
//
//        // Still with the interaction view.
        // The current view.
        InteractionViewBean view = (InteractionViewBean)
                getIntactUser(request).getView();
        view.addFeatureLink(fbs[0].getAc(), fbs[1].getAc());
//
//        // Save the interaction ac to get back to.
//        String ac = intView.getAc();
//
//        // Set the selected topic as other operation use it for various tasks.
//        user.setSelectedTopic("Feature");
//
//        // The feature we are about to edit.
//        Feature feature = (Feature) user.getObjectByAc(
//                Feature.class, form.getSelectedFeatureAc());
//
//        // Set the new object as the current edit object.
//        user.setView(feature);
        return mapping.getInputForward();
    }

    public ActionForward unlink(ActionMapping mapping,
                                InteractionActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        System.out.println("I am in the unlink feature");
//        // Handler to the Intact User.
//        EditUserI user = getIntactUser(request);
//
//        // The feature we are about to delete.
//        Feature feature = (Feature) user.getObjectByAc(
//                Feature.class, form.getSelectedFeatureAc());
//
//        // Delete the feature.
//        ((InteractionViewBean) user.getView()).deleteFeature(feature);
//
        return mapping.getInputForward();
    }
}