/*
 Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
 All rights reserved. Please see the file LICENSE
 in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.editor.struts.action.feature;

import org.apache.struts.action.*;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.application.editor.struts.action.CommonDispatchAction;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.feature.FeatureViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;

/**
 * This action class extends from common dispatch action class to override
 * submit action (Submit button) for Feature editor. Other submit actions such
 * as Save & Continue are not affected.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureSubmitAction extends CommonDispatchAction {

    /**
     * Overrides the super's submit action to handle Feature editor specific
     * behaviour.
     *
     * @param mapping the <code>ActionMapping</code> used to select this
     * instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     *         mapping if the update is successful and the previous search has only one
     *         result; results mapping if the update is successful and the previous
     *         search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward submit(ActionMapping mapping, ActionForm form,
                                HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        EditUserI user = getIntactUser(request);

        // The current view.
        FeatureViewBean view = ((FeatureViewBean) user.getView());

        // Stores mapping forwards.
        ActionForward forward;

        // The list of features (only valid in mutation mode).
        List features = null;

        // Check to see if this sumbitted Feature has mutations.
        if (view.isInMutationMode()) {
            try {
                // Persist mutations
                features = persistMutations(user);
                // It was a success.
                forward = mapping.findForward(SUCCESS);
            }
            catch (IntactException ie) {
                // Log the stack trace.
                LOGGER.error(ie);
                // Error with updating.
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.update",
                        ie.getRootCause().getMessage()));
                saveErrors(request, errors);
                forward = mapping.findForward(FAILURE);
            }
        }
        else {
            // Submit the form. Analyze the forward path.
            forward = submitForm(mapping, form, request, true);
        }
        // Return the forward for any non success.
        if (!forward.equals(mapping.findForward(SUCCESS))) {
            return forward;
        }
        // Set the topic.
        user.setSelectedTopic(getService().getTopic(Interaction.class));

        // The parent view of the current view.
        InteractionViewBean intView = view.getParentView();

        // The interaction we are going back to.
        user.setView(intView);

        // Update individual Features if in mutation mode.
        if (view.isInMutationMode()) {
            // features variable can never be null when in mutation mode. At least
            // it could contain a single entry
            for (Iterator iter = features.iterator(); iter.hasNext(); ) {
                intView.saveFeature((Feature) iter.next());
            }
        }
        else {
            // Update the feature in the interaction view (only for non mutation mode)
            intView.saveFeature((Feature) view.getAnnotatedObject());
        }
        // Turn off mutation mode (or else you will get mutation screen again)
        view.turnOffMutationMode();

        // Return to the interaction editor.
        return mapping.findForward(INT);
    }

    // Override the super method to allow duplicate short labels for feature.
    protected String getShortLabel(EditUserI user, Class editClass,
                                   String formlabel) throws SearchException {
        return formlabel;
    }

    private List persistMutations(EditUserI user) throws SearchException, IntactException {
        // The list of features to return.
        List features = new ArrayList();

        // The current view.
        FeatureViewBean view = ((FeatureViewBean) user.getView());

        // The owner for new Features.
        Institution owner = user.getInstitution();

        // CvFeature types.
        CvFeatureType featureType = (CvFeatureType) user.getObjectByLabel(
                CvFeatureType.class, view.getCvFeatureType());
        // CvFeatureIdent is optional.
        CvFeatureIdentification featureIdent = null;
        if (view.getCvFeatureIdentification() != null) {
            featureIdent = (CvFeatureIdentification) user.getObjectByLabel(
                    CvFeatureIdentification.class, view.getCvFeatureIdentification());
        }
        StringTokenizer stk1 = new StringTokenizer(view.getFullName(),
                getService().getResource("mutation.feature.sep"));

        // The mutation Feture to create.
        Feature feature;

        // The range separator.
        String sep = getService().getResource("mutation.range.sep");
        do {
            // Contains info for a single feature mutation
            String token = stk1.nextToken();

            // The next possible label for the new Feature.
            String nextSL = computeFeatureShortLabel(token, sep);
            feature = new Feature(owner, nextSL, view.getComponent(), featureType);
            if (featureIdent != null) {
                feature.setCvFeatureIdentification(featureIdent);
            }
            // Create a Feature in a separate transaction.
            user.begin();
            user.create(feature);
            user.endTransaction();

            // Feature is persisted, add it to the list.
            features.add(feature);

            StringTokenizer stk2 = new StringTokenizer(token, sep);

            user.begin();

            try {
                if (!stk2.hasMoreTokens()) {
                    // Only a single range specified.
                    int rangeValue = extractRange(token);
                    if (rangeValue != -1) {
                        Range range = new Range(owner, rangeValue, rangeValue, null);
                        user.create(range);
                        feature.addRange(range);
                    }
                    continue;
                }
                // Multiple ranges specified.
                do {
                    // Extract the range value to construct a range object.
                    int rangeValue = extractRange(stk2.nextToken());
                    Range range = new Range(owner, rangeValue, rangeValue, null);
                    user.create(range);
                    feature.addRange(range);
                }
                while (stk2.hasMoreTokens());
            }
            catch (IntactException ie) {
                // Delete the current Feature (it has already been created)
                user.delete(feature);
                // Rethrow it again for logging the exception.
                throw ie;
            }
            user.update(feature);
            user.endTransaction();
        }
        while (stk1.hasMoreTokens());
        return features;
    }

    /**
     * Computes the short label for a Feature
     * @param token this token may consist of multiple ranges
     * @param sep the range separtor
     * @return the computed short label. Each range is joined with '-' as long
     * as the string of the text is less than or equals tm max chars allowed for
     * a short label.
     */
    private String computeFeatureShortLabel(String token, String sep) {
        StringBuffer sb = new StringBuffer();
        StringTokenizer stk1 = new StringTokenizer(token, sep);
        while (stk1.hasMoreTokens()) {
            String range = stk1.nextToken().trim();
            if (sb.length() == 0) {
                sb.append(range);
                continue;
            }
            if ((sb.length() + range.length() + 1) <= AnnotatedObjectImpl.MAX_SHORT_LABEL_LEN) {
                sb.append("-");
                sb.append(range);
            }
            else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Extracts a range value from a string.
     * @param element the element to extract the range
     * @return an int value extracted from <code>element</code>
     */
    private int extractRange(String element) {
        Matcher matcher = FeatureActionForm.MUT_ITEM_REGX.matcher(element.trim());
        if (matcher.matches()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }
}