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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Fills the form with values for display.
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

        // Set anchor if necessary.
        setAnchor(request, editorForm);

        // The view of the current object we are editing at the moment.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // Fill the form.
        view.copyPropertiesTo(editorForm);

        // Reset any new beans (such as annotations & xrefs).
        editorForm.clearNewBeans();

        // Reset the dispatch action.
        editorForm.resetDispatch();
        
        // To the editor.
        return mapping.findForward(SUCCESS);
    }
}
