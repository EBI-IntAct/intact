/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.struts.view.EditBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ProteinBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;

import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action class for editing a Protein.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinEditAction extends AbstractEditorAction {

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
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        EditForm theForm = (EditForm) form;

        // The current view of the edit session.
        EditUserI user = super.getIntactUser(request);
        InteractionViewBean viewbean = (InteractionViewBean) user.getView();

        // The bean associated with the current action.
        int index = theForm.getIndex();
        ProteinBean pb = viewbean.getProtein(index);

        // We must have the protein bean.
        assert pb != null;

        if (theForm.editPressed()) {
            // Must save this bean.
            pb.setEditState(EditBean.SAVE);
        }
        else if (theForm.savePressed()) {
            // The protein to update.
            viewbean.addProteinToUpdate(pb);
            // Back to the view mode.
            pb.setEditState(EditBean.VIEW);
        }
        else if (theForm.deletePressed()) {
            // Delete is pressed.
            viewbean.delProtein(pb);
        }
        else {
            // Unknown operation; should never get here.
            assert false;
        }
        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }
}