/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.Globals;
import org.apache.struts.action.*;
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
//        LOGGER.debug("At the beginning of fill form action");
//        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
//            String para = (String) e.nextElement();
//            LOGGER.debug("parameter: " + para + " - " + request.getParameter(para));
//        }

        // The editor form.
        EditorActionForm editorForm = (EditorActionForm) form;

        // Set anchor if necessary.
        setAnchor(request, editorForm);

        // The view of the current object we are editing at the moment.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // Fill the form.
        view.copyPropertiesTo(editorForm);

        // Reset any new beans (such as annotations & xrefs).
        editorForm.resetNewBeans();

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
     * @see #getAnchor(HttpServletRequest, EditorActionForm)
     */
    protected void setAnchor(HttpServletRequest request, EditorActionForm form) {
        // Any anchors to set?
        String anchor = getAnchor(request, form);

        // Set the anchor only if it is set.
        if (anchor != null) {
            form.setAnchor(anchor);
        }
    }

    /**
     * Returns an anchor name by (1). error message, (2) the dispatch event.
     * <code>null</code> is returned if no anchor was found for the above two.
     * This method is protected for a subclass to overide it. For example,
     * this allows an anchor to determine by analysing another method other than
     * default two methods.
     *
     * @param request the request holds the error message.
     * @param form the form to get the dispatch event.
     * @return anchor appropriate for (1) error in <code>request</code> or (2)
     * button. <code>null</code> if no none found. All anchors are stored in
     * a map by the initial servlet for the editor.
     */
    protected String getAnchor(HttpServletRequest request, EditorActionForm form) {
        // The map containing anchors.
        Map anchorMap = (Map) getApplicationObject(EditorConstants.ANCHOR_MAP);

		// Errors are stored under this key.
		String errorkey =  Globals.ERROR_KEY;

        // Any errors?
        if (request.getAttribute(errorkey) != null) {
            ActionErrors errors = (ActionErrors) request.getAttribute(errorkey);
            // Only interested in the first (or only) error.
            ActionError error = (ActionError) errors.get().next();

            // The key this error is stored.
            String key = error.getKey();

            // Check the map for the error key.
            if (anchorMap.containsKey(key)) {
                return (String) anchorMap.get(key);
            }
        }

        // Any messages? Messages are storde under this key.
//        String msgkey = Globals.MESSAGE_KEY;
//
//        if (request.getAttribute(msgkey) != null) {
//        	ActionMessages msgs = (ActionMessages) request.getAttribute(msgkey);
//			// Only interested in the first (or only) message.
//			ActionError msg = (ActionError) msgs.get().next();
//
//			// The key this error is stored.
//			String key = msg.getKey();
//
//			// Check the map for the msg key.
//			if (anchorMap.containsKey(key)) {
//				return (String) anchorMap.get(key);
//			}
//        }
        // Start searching the dispatch.
        String dispatch = form.getDispatch();

        // Now go for the non error anchor using the dispatch value.
        if (anchorMap.containsKey(dispatch)) {
            return (String) anchorMap.get(dispatch);
        }
        return null;
    }
}
