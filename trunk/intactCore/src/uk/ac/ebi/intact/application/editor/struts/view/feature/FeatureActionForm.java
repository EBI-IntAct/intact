/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.feature;

import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;

/**
 * The action form for the Feature editor.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureActionForm extends EditorActionForm {

    /**
     * The parent ac
     */
    private String myParentAc;

    /**
     * The parent short label.
     */
    private String myParentShortLabel;

    /**
     * The parent fullname.
     */
    private String myParentFullName;

    /**
     * The selected defined feature (hidden field attribute).
     */
    private String mySelectedDefinedFeature;

    /**
     * The feature type.
     */
    private String myFeatureType;

    /**
     * The feature identification.
     */
    private String myFeatureIdent;

    /**
     * List of ranges for the feature.
     */
    private List myRanges;

    /**
     * The new range as a bean.
     */
    private RangeBean myNewRange = new RangeBean();

    // Getter/Setter methods.

    public String getParentAc() {
        return myParentAc;
    }

    public void setParentAc(String ac) {
        myParentAc = ac;
    }

    public String getParentShortLabel() {
        return myParentShortLabel;
    }

    public void setParentShortLabel(String label) {
        myParentShortLabel = label;
    }

    public String getParentFullName() {
        return myParentFullName;
    }

    public void setParentFullName(String fullname) {
        myParentFullName = fullname;
    }

    public void setDefinedFeature(String label) {
        mySelectedDefinedFeature = label;
    }

    public String getDefinedFeature() {
        return mySelectedDefinedFeature;
    }

    public String getFeatureType() {
        return myFeatureType;
    }

    public void setFeatureType(String type) {
        myFeatureType = type;
    }

    public String getFeatureIdent() {
        return myFeatureIdent;
    }

    public void setFeatureIdent(String ident) {
        myFeatureIdent = ident;
    }

    /**
     * Sets the dispatch with index of the button. This was called for selecting
     * Edit/Delete range buttons.
     *
     * @param index index of the button.
     * @param value the button label.
     */
    public void setRangeCmd(int index, String value) {
        setDispatch(index, value);
    }

    /**
     * Sets ranges for the feature.
     *
     * @param ranges a list of ranges for a <code>Feature</code>.
     * <p/>
     * <pre>
     * pre:  forall(obj : Object | obj.oclIsTypeOf(RangeBean))
     * </pre>
     */
    public void setRanges(List ranges) {
        myRanges = ranges;
    }

    public List getRanges() {
        return myRanges;
    }

    public void setNewRange(RangeBean rb) {
        myNewRange = rb;
    }

    public RangeBean getNewRange() {
        return myNewRange;
    }

    /**
     * Override the super method to reset the new range bean.
     */
//    public void resetNewBeans() {
//        super.resetNewBeans();
//        myNewRange.reset();
//    }

    /**
     * Returns the selected range.
     *
     * @return the selected range as indicated by the dispatch index.
     */
    public RangeBean getSelectedRange() {
        return (RangeBean) myRanges.get(getDispatchIndex());
    }

    /**
     * Validate the properties that have been set from the HTTP request.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     *         no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     *         object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);

        // Only proceed if super method does not find any errors.
        if ((errors != null) && !errors.isEmpty()) {
            return errors;
        }
        // The dispatch parameter to find out which button was pressed.
        String dispatch = getDispatch();

        // No errors if the form wasn't dispatched.
        if (dispatch == null) {
            return null;
        }
        // Message resources to access button labels.
        MessageResources msgres =
                ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY));

        // Trap errors for adding a new range.
        if (dispatch.equals(msgres.getMessage("feature.range.button.add"))) {
            // Validate from and to ranges.
            errors = getNewRange().validate("new");
        }
        // Trap errors for saving a range
        else if (dispatch.equals(msgres.getMessage("feature.range.button.save"))) {
            // Validate from and to ranges.
            errors = getSelectedRange().validate("edit");
        }
        // Trap errors for Saving/Submitting the feature.
        else if (dispatch.equals(msgres.getMessage("button.submit"))
                || dispatch.equals(msgres.getMessage("button.save.continue"))) {
            // Must have ranges.
            if (myRanges.isEmpty()) {
                errors = new ActionErrors();
                errors.add("feature.range.empty", new ActionError("error.feature.range.empty"));
            }
            else {
                // Check for unsaved ranges.
                errors = checkUnsavedRanges();
            }
        }
        return errors;
    }

    /**
     * Checks for unsaved ranges. A range not in a view state is flagged as an error.
     *
     * @return null if no errors found.
     */
    private ActionErrors checkUnsavedRanges() {
        // The errors to return.
        ActionErrors errors = null;

        // Look for any unsaved or error proteins.
        for (Iterator iter = myRanges.iterator(); iter.hasNext();) {
            RangeBean rb = (RangeBean) iter.next();
            // They all must be in view mode. Flag an error if not.
            if (!rb.getEditState().equals(AbstractEditBean.VIEW)) {
                errors = new ActionErrors();
                errors.add("feature.range.unsaved", new ActionError("error.feature.range.unsaved"));
                break;
            }
        }
        return errors;
    }
}
