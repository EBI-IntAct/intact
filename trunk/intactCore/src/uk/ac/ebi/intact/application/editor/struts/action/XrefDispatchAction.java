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
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.XreferenceBean;
import uk.ac.ebi.intact.model.Xref;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dispatcher action which dispatches according to 'dispatch' parameter. This
 * class handles events for edit, save and delete of an Xref.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class XrefDispatchAction extends AbstractEditorAction {

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
        String cmd = ((String[]) dynaform.get("xrefCmd"))[idx];

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        if (cmd.equals(msgres.getMessage("xrefs.button.edit"))) {
            return edit(mapping, form, request, response);
        }
        if (cmd.equals(msgres.getMessage("xrefs.button.save"))) {
            return save(mapping, form, request, response);
        }
        // default is delete.
        return delete(mapping, form, request, response);
    }

    /**
     * Action for editing the selected annotation.
     *
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
    public ActionForward edit(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the xref.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The xref we are editing at the moment.
        XreferenceBean xb = ((XreferenceBean[]) dynaform.get("xrefs"))[idx];

        // Must save this bean.
        xb.setEditState(AbstractEditBean.SAVE);

        // Back to the edit form.
        return mapping.getInputForward();
    }

    /**
     * Action for deleting the selected annotation.
     *
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in deleting the CV object; search
     * mapping if the delete is successful and the previous search has only one
     * result; results mapping if the delete is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the annotation.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The xref we are about to delete.
        XreferenceBean xb = ((XreferenceBean[]) dynaform.get("xrefs"))[idx];

        // The current view of the edit session.
        AbstractEditViewBean view = getIntactUser(request).getView();

        // Delete from the view.
        view.delXref(xb);

        // Back to the edit form.
        return mapping.getInputForward();
    }

    /**
     * Action for saving an edited annotation.
     *
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in cancelling the CV object; search
     * mapping if the cancel is successful and the previous search has only one
     * result; results mapping if the cancel is successful and the previous
     * search has produced multiple results.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward save(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The dyna form.
        DynaActionForm dynaform = (DynaActionForm) form;

        // The index position of the annotation.
        int idx = ((Integer) dynaform.get("idx")).intValue();

        // The xref we are about to delete.
        XreferenceBean xb = ((XreferenceBean[]) dynaform.get("xrefs"))[idx];

        // Handler to the EditUserI.
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        AbstractEditViewBean view = user.getView();

        // Only add to the update list if this isn't a new xref.
        if (view.isNewXref(xb)) {
            // Remove the existing 'new' xref.
            view.removeNewXref(xb);
            // Add this 'updated' as a new xref.
            Xref xref = user.getXref(xb);
            view.addXref(new XreferenceBean(xref, xb.getKey()));
        }
        else {
            // Saving an existing annotation.
            view.addXrefToUpdate(xb);
        }
        // Back to the view mode again.
        xb.setEditState(AbstractEditBean.VIEW);

        // Back to the edit form.
        return mapping.getInputForward();
    }
}
