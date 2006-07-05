/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ComponentBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.business.IntactHelper;

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
 *
 * @struts.action
 *      path="/int/feature"
 *      name="intForm"
 *      input="edit.layout"
 *      scope="session"
 *      parameter="dispatchFeature"
 *
 * @struts.action-forward
 *      name="success"
 *      path="/do/feature/fill/form"
 */
public class FeatureDispatchAction extends CommonDispatchAction {

    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("int.proteins.button.feature.edit", "edit");
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
        ActionForward forward = super.save(mapping, form, request, response);

        // Don not proceed if the inteaction wasn't saved successfully first.
        if (!forward.equals(mapping.findForward(SUCCESS))) {
            return forward;
        }
        // Linking to the feature editor starts from here.

        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // Set the selected topic as other operation use it for various tasks.
        user.setSelectedTopic("Feature");

        // The feature we are about to edit.
        FeatureBean fb = ((InteractionViewBean) user.getView()).getSelectedFeature();
        IntactHelper helper = user.getIntactHelper();

        Feature feature = fb.getFeature();
        feature = helper.getObjectByAc(Feature.class, feature.getAc());

        // Set the new object as the current edit object, don't release the pre view
        user.setView(feature, false);

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

        // Set the selected topic as other operation use it for various tasks.
        user.setSelectedTopic("Feature");

        // Set the new object as the current edit object, don't release the pre view
        user.setView(Feature.class, false);

        // The feature view bean.
        FeatureViewBean featureView = (FeatureViewBean) user.getView();

        // The form.
        InteractionActionForm intform = (InteractionActionForm) form;

        // The selected component from the form.
        ComponentBean selectedComp = intform.getSelectedComponent();

        // The component for the feature.
        featureView.setComponent(selectedComp.getComponent());
        return mapping.findForward(SUCCESS);
    }
}