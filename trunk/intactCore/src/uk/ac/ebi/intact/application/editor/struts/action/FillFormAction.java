/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Fills the form with values for ac, short label and full name.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FillFormAction extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The editor form.
        EditorActionForm editorForm = (EditorActionForm) form;

        // The view of the current object we are editing at the moment.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // Fill the form.
        view.copyPropertiesTo(editorForm);

        // Reset annotation and xref forms.
        resetAddAnnotation(editorForm);
        resetAddXref(editorForm);

        // Straight to the editor.
        return mapping.findForward(SUCCESS);
    }

    /**
     * Resets the annotation add form.
     * @param editorForm the form to get/set the bean.
     */
    private void resetAddAnnotation(EditorActionForm editorForm) {
        CommentBean cb = editorForm.getNewAnnotation();
        if (cb != null) {
            // Resuse the existing bean.
            cb.reset();
        }
        else {
            editorForm.setNewAnnotation(new CommentBean());
        }
    }

    /**
     * Resets the xref add form.
     * @param editorForm the form to get/set the bean.
     */
    private void resetAddXref(EditorActionForm editorForm) {
        XreferenceBean xb = editorForm.getNewXref();
        if (xb != null) {
            // Resuse the existing bean.
            xb.reset();
        }
        else {
            editorForm.setNewXref(new XreferenceBean());
        }
    }
}
