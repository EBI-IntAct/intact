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
import uk.ac.ebi.intact.model.Range;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

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
        // Add a copy of the new range
        view.addRange(rbnew.copy());

        // Back to the input form.
        return mapping.getInputForward();
    }
}