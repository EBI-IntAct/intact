/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.*;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ProteinBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action class for editing a Protein.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinDispatchAction extends AbstractEditorAction {

    /**
     * This method dispatches calls to various methods using the 'dispatch' parameter.
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
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the xref.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The command associated with the index.
        String cmd = ((String[]) dynaform.get("protCmd"))[idx];

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        if (cmd.equals(msgres.getMessage("int.proteins.button.edit"))) {
            return edit(mapping, form, request, response);
        }
        if (cmd.equals(msgres.getMessage("int.proteins.button.save"))) {
            return save(mapping, form, request, response);
        }
        // default is delete.
        return delete(mapping, form, request, response);
    }

    public ActionForward edit(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the annotation.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The protein we are editing at the moment.
        ProteinBean pb = ((ProteinBean[]) dynaform.get("proteins"))[idx];

        // We must have the protein bean.
        assert pb != null;

        // Must save this bean.
        pb.setEditState(AbstractEditBean.SAVE);

        // Update the form.
        return mapping.findForward(SUCCESS);
    }

    public ActionForward save(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the annotation.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The current view of the edit session.
        InteractionViewBean viewbean =
                (InteractionViewBean) getIntactUser(request).getView();

        // The protein we are editing at the moment.
        ProteinBean pb = ((ProteinBean[]) dynaform.get("proteins"))[idx];

        // We must have the protein bean.
        assert pb != null;

        // Must define a role for the Protein.
        if (pb.getRole() == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.int.protein.edit.role"));
            saveErrors(request, errors);
            pb.setEditState(ProteinBean.ERROR);
            return mapping.findForward(FAILURE);
        }
        // The protein to update.
        viewbean.addProteinToUpdate(pb);

        // Back to the view mode.
        pb.setEditState(AbstractEditBean.VIEW);

        // Remove the unsaved proteins.
        viewbean.removeUnsavedProteins();
        
        // Update the form.
        return mapping.findForward(SUCCESS);
    }

    public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the annotation.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The current view of the edit session.
        InteractionViewBean view =
                (InteractionViewBean) getIntactUser(request).getView();

        // The protein we are editing at the moment.
        ProteinBean pb = ((ProteinBean[]) dynaform.get("proteins"))[idx];

        // We must have the protein bean.
        assert pb != null;

        // Delete this Protein from the view.
        view.delProtein(idx);

        // Update the form.
        return mapping.findForward(SUCCESS);
    }
}