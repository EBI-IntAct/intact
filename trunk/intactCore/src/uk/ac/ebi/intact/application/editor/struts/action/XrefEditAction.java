/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.EditBean;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action class is called when the user adds a new comment (annotation) to
 * the edit.jsp.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefEditAction extends AbstractEditorAction {

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping - The <code>ActionMapping</code> used to select this instance
     * @param form - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the action servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        EditForm editform = (EditForm) form;

        // The current view of the edit session.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // The bean associated with the current action.
        XreferenceBean xb = (XreferenceBean) editform.getSelectedBean();

        // We must have the bean.
        assert xb != null;

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        if (editform.hasPressed(msgres.getMessage("button.edit"))) {
            // Must save this bean.
            xb.setEditState(EditBean.SAVE);
        }
        else if (editform.hasPressed(msgres.getMessage("button.save"))) {
            // Save button pressed. The xref to update.
            view.addXrefToUpdate(xb);
            // Back to the view mode again.
            xb.setEditState(EditBean.VIEW);
        }
        else if (editform.hasPressed(msgres.getMessage("button.delete"))) {
            // Delete is pressed.
            view.delXref(xb);
        }
        else {
            // Unknown operation; should never get here.
            assert false;
        }
        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }
}