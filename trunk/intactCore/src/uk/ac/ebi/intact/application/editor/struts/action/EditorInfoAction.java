/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.exception.SearchException;
import uk.ac.ebi.intact.model.AnnotatedObject;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Action class when the use presses Save button to save short label and full
 * name.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorInfoAction extends AbstractEditorAction {

    /**
     * Action for submitting the CV info form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in searching the database; refresh
     * mapping if the screen needs to be updated (this will only happen if the
     * short label on the screen is different to the short label returned by
     * getUnqiueShortLabel method. For all other instances, success mapping is
     * returned.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Handler to the current user.
        EditUserI user = super.getIntactUser(request);

        // The view of the current object we are editing at the moment.
        AbstractEditViewBean viewbean = user.getView();

        // The form to access input data.
        DynaActionForm theForm = (DynaActionForm) form;

        // Extract the short label for us to cheque for its uniqueness.
        String formlabel = (String) theForm.get("shortLabel");

        // Validate the short label.
        if (!validateShortLabel(user, formlabel, request)) {
            // Display the errors in the input page.
            return inputForward(mapping);
        }
        String newlabel = user.getUniqueShortLabel(formlabel);

        // Update the view with new values.
        viewbean.setShortLabel(newlabel);
        viewbean.setFullName((String) theForm.get("fullName"));

        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }

    /**
     * Validates the short label.
     * @param user the user to search the database.
     * @param label the label to validate.
     * @param request the Http request to save errors
     * @return true if <code>label</code> doesn't exist in the database.
     * @exception SearchException for errors in acccessing the database.
     */
    private boolean validateShortLabel(EditUserI user,
                                       String label,
                                       HttpServletRequest request)
            throws SearchException {
        // The object we are editing at the moment.
        AnnotatedObject annobj = user.getView().getAnnotatedObject();
        Class clazz = annobj.getClass();

        // Holds the result from the search.
        Collection results = user.search(clazz.getName(), "shortLabel", label);
        if (results.isEmpty()) {
            // Don't have this short label on the database.
            return true;
        }
        // If we found a single record then it could be the current record.
        if (results.size() == 1) {
            // Found an object with similar short label; is it as same as the
            // current record?
            AbstractEditViewBean view = user.getView();
            String currentAc = view.getAnnotatedObject().getAc();
            // ac is null until a record is persisted; current ac is null
            // for a new object.
            if (currentAc != null) {
                // Eediting an existing record.
                String resultAc = ((AnnotatedObject) results.iterator().next()).getAc();
                if (currentAc.equals(resultAc)) {
                    // We have retrieved the same record from the DB.
                    return true;
                }
            }
        }
        // Found more than one entry with the same short label.
        ActionErrors errors = new ActionErrors();
        errors.add("cvinfo.label", new ActionError("error.cvinfo.label", label));
        saveErrors(request, errors);
        return false;
    }
}
