/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The action class to handle events related to delete/link/unlink of a feature.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureLinkAction extends AbstractEditorDispatchAction {

    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("int.proteins.button.feature.delete", "delete");
        map.put("int.proteins.button.feature.link", "link");
        map.put("int.proteins.button.feature.unlink", "unlink");
        return map;
    }

    /**
     * Handles when Delete Feature button is pressed.
     */
    public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = getIntactUser(request);

        // The current view to delete features.
        InteractionViewBean view = (InteractionViewBean) user.getView();

        // The features we are about to delete.
        List beans = view.getFeaturesToDelete();

        // Delete the features.
        for (Iterator iter = beans.iterator(); iter.hasNext();) {
            view.deleteFeature((FeatureBean) iter.next());
        }
        return updateForm(mapping, form, request);
    }

    /**
     * Handles when Link Feature button is pressed.
     */
    public ActionForward link(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        // The current view to link/unlink features.
        InteractionViewBean view =
                (InteractionViewBean) getIntactUser(request).getView();

        // The two Features to link.
        FeatureBean[] fbs = view.getFeaturesForLink();

        // Check if they already have bound domains.
        if ((fbs[0].getBoundDomain().length() != 0)
                || (fbs[1].getBoundDomain().length() != 0)) {
            ActionErrors errors = new ActionErrors();
            errors.add("feature.link",
                    new ActionError("error.int.feature.link.error"));
            saveErrors(request, errors);
        }
        else {
            // Link two features.
            view.addFeatureLink(fbs[0], fbs[1]);
        }
        return updateForm(mapping, form, request);
    }

    /**
     * Handles when Unlink Feature button is pressed.
     */
    public ActionForward unlink(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The current view to link/unlink features.
        InteractionViewBean view =
                (InteractionViewBean) getIntactUser(request).getView();

        // The Feature to unlink.
        FeatureBean fb = view.getFeatureForUnlink();

        // Check if they already have bound domains.
        if (fb.getBoundDomain().length() == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add("feature.link",
                    new ActionError("error.int.feature.unlink.error"));
            saveErrors(request, errors);
        }
        else {
            // This feature is linked.
            view.addFeatureToUnlink(fb);
        }
        return updateForm(mapping, form, request);
    }

    // Encapsulate common functionality
    private ActionForward updateForm(ActionMapping mapping,
                                     ActionForm form,
                                     HttpServletRequest request) {
        // Set the anchor if necessary.
        setAnchor(request, (EditorActionForm) form);

        // Update the form.
        return mapping.getInputForward();
    }
}