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
 * The action class for adding a new feature for the selected component.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class AddFeatureAction extends CommonDispatchAction {

    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("int.proteins.button.feature.add", "add");
        return map;
    }

    /**
     * Handles when Edit Protein button is pressed.
     */     
    public ActionForward add(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Save the interaction first.
        ActionForward forward = save(mapping, form, request, response);

        // Don not proceed if the inteaction wasn'r saved successfully first.
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
}