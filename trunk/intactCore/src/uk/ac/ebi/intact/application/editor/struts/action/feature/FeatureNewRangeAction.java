/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.feature;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.feature.RangeBean;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Range;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

/**
 * This action is invoked when the user wants to add a new range to a feature.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureNewRangeAction extends CommonDispatchAction {

    /**
     * Overrides the super to add addFeature method for pressing the add range
     * button.
     *
     * @return Resource key / method name map.
     */
    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("feature.range.button.add", "addRange");
        return map;
    }

    public ActionForward addRange(ActionMapping mapping,
                                    ActionForm form,
                                    HttpServletRequest request,
                                    HttpServletResponse response)
            throws Exception {
        // The form to extract values.
        FeatureActionForm featureForm = ((FeatureActionForm) form);

        // Handler to the current user.
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        FeatureViewBean view = (FeatureViewBean) user.getView();

        // The feature must exist before adding a new range.
//        if (view.getAnnotatedObject() == null) {
//            // Save & Continue. Analyze the forward path.
//            ActionForward forward = save(mapping, form, request, response);
//
//            // Return the forward for any non success.
//            if (!forward.getPath().equals(mapping.findForward(SUCCESS).getPath())) {
//                return forward;
//            }
//            // Feature is persisted.
//        }
        // Can we create a Range instance from the user input? validate
        // method only confirms ranges are valid.
        RangeBean rbnew = featureForm.getNewRange();

        // Does the range exist in the current ranges?
        if (view.rangeExists(rbnew)) {
            ActionErrors errors = new ActionErrors();
            errors.add("new.range",
                    new ActionError("error.feature.range.exists"));
            saveErrors(request, errors);
            // Incorrect values for ranges. Display the error in the input page.
            return mapping.getInputForward();
        }

        // The range to construct from the bean.
        Range range = rbnew.makeRange(user);

        // Wraps the range around a bean.
        RangeBean rb = new RangeBean(range);

        // Add the new range
        view.addRange(rb);

        // Update the existing defined feature.
//        view.updateDefinedFeature(rb);

        // Back to the input form.
        return mapping.getInputForward();
    }
}