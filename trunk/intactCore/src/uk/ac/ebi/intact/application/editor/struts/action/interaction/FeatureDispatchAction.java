/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ComponentBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.model.Feature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * The action class to handle events related to add/edit a
 * Feature from an Interaction editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureDispatchAction extends CommonDispatchAction {

    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("int.proteins.button.feature.edit", "edit");
        map.put("int.proteins.button.feature.add", "add");
        map.put("int.proteins.button.feature.save", "save");
        return map;
    }

    /**
     * Handles when Edit Feature button is pressed.
     */
    public ActionForward edit(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        // Save the interaction first.
        ActionForward forward = super.save(mapping, form, request, response);

        // Don not proceed if the inteaction wasn't saved successfully first.
        if (!forward.equals(mapping.findForward(SUCCESS))) {
            return forward;
        }
        // Linking to the feature editor starts from here.

        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Still with the interaction view.
        InteractionViewBean intView = (InteractionViewBean) user.getView();

        // Set the selected topic as other operation use it for various tasks.
        user.setSelectedTopic("Feature");

        // The feature we are about to edit.
        FeatureBean fb = intView.getSelectedFeature();
        Feature feature = fb.getFeature();

        // Set the new object as the current edit object.
        user.setView(feature);

        // The feature view bean.
        FeatureViewBean featureView = (FeatureViewBean) user.getView();

        // Set the parent for the feature view.
        featureView.setParentView(intView);

        return forward;
    }

    /**
     * Handles when Adde Feature button is pressed.
     */
    public ActionForward add(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
            throws Exception {
        // Save the interaction first.
        ActionForward forward = super.save(mapping, form, request, response);

        // Don not proceed if the inteaction wasn't saved successfully first.
        if (!forward.equals(mapping.findForward(SUCCESS))) {
            return forward;
        }
        // Linking to the feature editor starts from here.

        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Still with the interaction view.
        InteractionViewBean intView = (InteractionViewBean) user.getView();

        // Set the selected topic as other operation use it for various tasks.
        user.setSelectedTopic("Feature");

        // Set the new object as the current edit object.
        user.setView(Feature.class);

        // The feature view bean.
        FeatureViewBean featureView = (FeatureViewBean) user.getView();

        // Set the parent for the feature view.
        featureView.setParentView(intView);

        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The selected component from the form.
        ComponentBean selectedComp = intform.getSelectedComponent();

        // The component for the feature.
        featureView.setComponent(selectedComp.getComponent(user));

        return mapping.findForward(SUCCESS);
    }

    /**
     * Handles when Save Feature button is pressed.
     */
    public ActionForward save(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Still with the interaction view.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // The feature we are about to save.
        FeatureBean fb = view.getSelectedFeature();

        // The short label user modified.
        String formLabel = fb.getShortLabel();

        // Does the short label exist?
        if (user.shortLabelExists(Feature.class, formLabel, fb.getAc())) {
            // Found more than one entry with the same short label.
            String link = "<a href=\"javascript:show('" + formLabel + "')\">here</a>";
            ActionErrors errors = new ActionErrors();
            errors.add("int.feature.shortlabel",
                    new ActionError("error.label", formLabel, link));
            saveErrors(request, errors);
        }
        else {
            // Remove the error flag from the bean.
            fb.setEditState(AbstractEditBean.VIEW);

            // Need to update the link if this feature is linked to another feature.
            if (fb.hasBoundDomain()) {
                FeatureBean target = view.getFeatureBean(fb.getBoundDomain());
                view.addFeatureLink(fb, target);
            }
        }
        // Update the input page.
        return mapping.getInputForward();
    }
}