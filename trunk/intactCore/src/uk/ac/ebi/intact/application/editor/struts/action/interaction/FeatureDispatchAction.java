/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ComponentBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.model.Feature;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * The action class to handle events related to add/edit and delete of a
 * Feature from an Interaction editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureDispatchAction extends CommonDispatchAction {

    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("int.proteins.button.feature.edit", "edit");
        map.put("int.proteins.button.feature.delete", "delete");
        map.put("int.proteins.button.feature.add", "add");
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
        ActionForward forward = save(mapping, form, request, response);

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

        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The feature we are about to edit.
        FeatureBean fb = intform.getSelectedFeature();
        Feature feature = fb.getFeature();

        // Unselect the bean or else it will be selected all the time.
        fb.unselect();

        // Set the new object as the current edit object.
        user.setView(feature);

        // The feature view bean.
        FeatureViewBean featureView = (FeatureViewBean) user.getView();

        // Set the parent for the feature view.
        featureView.setParentView(intView);

        return forward;
    }

    /**
     * Handles when Delete Protein button is pressed.
     */
    public ActionForward delete(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The feature bean we are about to delete.
        FeatureBean bean = intform.getSelectedFeature();

        // Delete the feature.
        ((InteractionViewBean) user.getView()).deleteFeature(bean);

        return mapping.getInputForward();
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
        ActionForward forward = save(mapping, form, request, response);

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

    // Override the super method to get the anchor for dispatch feature.

//    protected String getAnchor(HttpServletRequest request, EditorActionForm form) {
//        String anchor = super.getAnchor(request, form);
//
//        if (anchor != null) {
//            return anchor;
//        }
//        // Start searching feature dispatch.
//        String dispatch = ((InteractionActionForm) form).getDispatchFeature();
//
//        // The map containing anchors.
//        Map anchorMap = (Map) getApplicationObject(EditorConstants.ANCHOR_MAP);
//
//        // Now go for the non error anchor using the dispatch value.
//        if (anchorMap.containsKey(dispatch)) {
//            return (String) anchorMap.get(dispatch);
//        }
//        return null;
//    }
}