/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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

    /**
     * Sets the anchor in the form if an anchor exists.
     *
     * @param request the HTTP request to get anchor.
     * @param form the form to set the anchor and also to extract the dispath
     * event.
     *
     * @see EditorService#getAnchor(Map, HttpServletRequest, String)
     */
    protected void setAnchor(HttpServletRequest request, EditorActionForm form) {
        // The map containing anchors.
        Map map = (Map) getApplicationObject(EditorConstants.ANCHOR_MAP);

        // Any anchors to set?
        String anchor = EditorService.getInstance().getAnchor(map, request,
                form.getDispatch());

        // Set the anchor only if it is set.
        if (anchor != null) {
            form.setAnchor(anchor);
        }
    }
}
