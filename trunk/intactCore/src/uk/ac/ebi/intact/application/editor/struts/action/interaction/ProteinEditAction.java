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
        EditForm theform = (EditForm) form;

        // The current view of the edit session.
        InteractionViewBean viewbean =
                (InteractionViewBean) getIntactUser(request).getView();

        // The bean associated with the current action.
        ProteinBean pb = (ProteinBean) theform.getSelectedBean();

        // We must have the protein bean.
        assert pb != null;

        if (theform.editPressed()) {
            // Must save this bean.
            pb.setEditState(EditBean.SAVE);
        }
        else if (theform.savePressed()) {
            if (viewbean.hasDuplicates(pb)) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR,
                        new ActionError("error.int.protein.edit.dup",
                                pb.getShortLabel(), pb.getRole()));
                saveErrors(request, errors);
                pb.setEditState(ProteinBean.ERROR);
//                pb.setError(pb.getShortLabel(), pb.getRole());
//                System.out.println("mapping is " + inputForward(mapping));
                return mapping.getInputForward();
//                return inputForward(mapping);
//                return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
            }
//            else {
                // The protein to update.
                viewbean.addProteinToUpdate(pb);
                // Back to the view mode.
                pb.setEditState(EditBean.VIEW);
//            }
        }
        else if (theform.deletePressed()) {
            // Delete is pressed.
            viewbean.delProtein(pb, theform.getIndex());
        }
        else {
            // Unknown operation; should never get here.
            assert false;
        }
        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }
}