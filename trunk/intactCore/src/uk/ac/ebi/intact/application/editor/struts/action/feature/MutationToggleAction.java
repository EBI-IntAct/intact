/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.feature;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.CvFeatureType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action is invoked when Mutation Toggle button is selected.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class MutationToggleAction extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The user.
        EditUserI user = getIntactUser(request);

        // The current view.
        FeatureViewBean view = (FeatureViewBean) user.getView();

        // Toggle between modes.
        view.toggleEditMode();

        // Preset the CvFeature type.
        CvFeatureType featureType = (CvFeatureType) user.getObjectByLabel(
                CvFeatureType.class, "hotspot");

        // We shouldn't be getting a null object. But.... just in case.
        if (featureType != null) {
            view.setCvFeatureType("hotspot");
        }
        // Update the form and display.
        return mapping.findForward(SUCCESS);
    }
}