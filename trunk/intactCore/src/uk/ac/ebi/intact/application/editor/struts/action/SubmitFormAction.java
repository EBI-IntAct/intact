/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dispatcher action which dispatches to various submit actions.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SubmitFormAction extends AbstractEditorAction {

    /**
     * Action for submitting the edit form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in updating the CV object; search
     * mapping if the update is successful and the previous search has only one
     * result; results mapping if the update is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

//        System.out.println("At the beginning of form dispatch");
//        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
//            String para = (String) e.nextElement();
//            System.out.println("parameters: " + para + " - " + request.getParameter(para));
//        }

        // The current view.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // Set the fields; these will be validated later upon submit.
        view.setShortLabel((String) dynaform.get("shortLabel"));
        view.setFullName((String) dynaform.get("fullName"));

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        String dispatch = (String) dynaform.get("dispatch");
        if (dispatch != null) {
            if (submitPressed(dispatch, msgres)) {
                return mapping.findForward("submit");
            }
            if (cancelPressed(dispatch, msgres)) {
                return mapping.findForward("cancel");
            }
            if (deletePressed(dispatch, msgres)) {
                return mapping.findForward("delete");
            }
            // None of the above actions; Let the subclass handle this
            // dispatch action.
            return handleDispatch(mapping, form, request, dispatch);
        }
        // List of buttons for annotation action.
        String[] annotCmds = (String[]) dynaform.get("annotCmd");
        int annotIdx = getCurrentSelection(annotCmds);

        if (annotIdx != -1) {
            // Selected an annotation. Set the index.
            dynaform.set("idx", new Integer(annotIdx));
            return mapping.findForward("annotation");
        }
        // List of buttons for xref action.
        String[] xrefCmds = (String[]) dynaform.get("xrefCmd");
        int xrefIdx = getCurrentSelection(xrefCmds);

        // Is it for a xref?
        if (xrefIdx != -1) {
            dynaform.set("idx", new Integer(xrefIdx));
            return mapping.findForward("xref");
        }
        // None of the above events; Let the subclass handle this action.
        return handle(mapping, form, request);
    }

    /**
     * Subclass must override this to provide to handle the event.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param dispatch the dispatch parameter.
     * @return the next cause of action to take.
     * @throws Exception for any uncaught errors.
     */
    protected ActionForward handleDispatch(ActionMapping mapping,
                                           ActionForm form,
                                           HttpServletRequest request,
                                           String dispatch)
            throws Exception {
        return null;
    }

    /**
     * Subclass must override this to provide to handle the event.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @return the next cause of action to take.
     * @throws Exception for any uncaught errors.
     */
    protected ActionForward handle(ActionMapping mapping,
                                   ActionForm form,
                                   HttpServletRequest request)
            throws Exception {
        return null;
    }

    /**
     * @param cmds an array button commands.
     * @return the first non null position of <code>cmds</code>. This position
     * marks as the currently selected item.
     */
    protected int getCurrentSelection(String[] cmds) {
        for (int i = 0; i < cmds.length; i++) {
            if (cmds[i] != null) {
                return i;
            }
        }
        return -1;
    }

    // Helper methods

    /**
     * @param title the button title to compare.
     * @param msgres the message resource to get the button label.
     * @return true if one of the submit buttons
     * (Submit/Save/Adding annotation/xref) is pressed.
     */
    private boolean submitPressed(String title, MessageResources msgres) {
        if (title.equals(msgres.getMessage("button.submit"))
                || title.equals(msgres.getMessage("button.save.continue"))
                || title.equals(msgres.getMessage("annotations.button.add"))
                || title.equals(msgres.getMessage("xrefs.button.add"))) {
            return true;
        }
        return false;
    }

    /**
     * @param title the button title to compare.
     * @param msgres the message resource to get the button label.
     * @return true if cancel button (form) is pressed.
     */
    private boolean cancelPressed(String title, MessageResources msgres) {
        if (title.equals(msgres.getMessage("button.cancel"))) {
            return true;
        }
        return false;
    }

    /**
     * @param title the button title to compare.
     * @param msgres the message resource to get the button label.
     * @return true if delete button (form) is pressed.
     */
    private boolean deletePressed(String title, MessageResources msgres) {
        if (title.equals(msgres.getMessage("button.delete"))) {
            return true;
        }
        return false;
    }
}
