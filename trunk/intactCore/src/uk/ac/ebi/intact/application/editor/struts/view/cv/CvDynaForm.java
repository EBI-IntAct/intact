/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.view.cv;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.DynaValidatorForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorMenuFactory;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The form to edit bio experiment data.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class CvDynaForm extends DynaValidatorForm {

    /**
     * The pattern for a valid short label.
     */
    private static Pattern SHORT_LABEL_PAT =
            Pattern.compile("[a-z0-9\\-:_]+ ?[a-z0-9\\-:_]+$");

    /**
     * Validate the properties that have been set from the HTTP request.
     *
     * @param mapping the mapping used to select this instance
     * @param request the servlet request we are processing
     * @return <tt>ActionErrors</tt> object that contains validation errors. If
     * no errors are found, <tt>null</tt> or an empty <tt>ActionErrors</tt>
     * object is returned.
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        ActionErrors errors = null;

        // The short label to verify for syntax.
        String formlabel = (String) get("shortLabel");

        // Validate the short label
        Matcher matcher = SHORT_LABEL_PAT.matcher(formlabel);
        if (!matcher.matches()) {
            // Invalid syntax for a short label.
            errors = new ActionErrors();
            errors.add("shortLabel",
                    new ActionError("error.shortlabel.mask", formlabel));
            return errors;
        }
        // The dispatch parameter to find out which button was pressed.
        String dispatch = (String) get("dispatch");

        if (dispatch != null) {
            // Message resources to access button labels.
            MessageResources msgres =
                    ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY));

            // Adding an annotation?
            if (dispatch.equals(msgres.getMessage("annotations.button.add"))) {
                // The bean to extract the values.
                CommentBean cb = (CommentBean) get("annotation");
                if (cb.getTopic().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
                    errors = new ActionErrors();
                    errors.add(ActionErrors.GLOBAL_ERROR,
                            new ActionError("error.annotation.topic"));
                    return errors;
                }
            }
            // Adding an Xref?
            if (dispatch.equals(msgres.getMessage("xrefs.button.add"))) {
                // The bean to extract the values.
                XreferenceBean xb = (XreferenceBean) get("xref");
                if (xb.getDatabase().equals(EditorMenuFactory.SELECT_LIST_ITEM)) {
                    errors = new ActionErrors();
                    errors.add(ActionErrors.GLOBAL_ERROR,
                            new ActionError("error.xref.database"));
                    return errors;
                }
                // Primary id is required.
                if (AbstractEditorAction.isPropertyEmpty(xb.getPrimaryId())) {
                    errors = new ActionErrors();
                    errors.add(ActionErrors.GLOBAL_ERROR,
                            new ActionError("error.xref.pid"));
                }
            }
        }
        return errors;
    }
}
