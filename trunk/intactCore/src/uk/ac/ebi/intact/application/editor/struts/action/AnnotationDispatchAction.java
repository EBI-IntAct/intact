/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.EditorActionForm;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.AbstractEditBean;
import uk.ac.ebi.intact.application.editor.struts.view.CommentBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dispatcher action which dispatches according to the button title. This
 * class handles events for edit, save and delete of an annotation.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotationDispatchAction extends AbstractEditorAction {

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
        // The editor form.
        EditorActionForm editorForm = (EditorActionForm) form;

        // The command associated with the dispatch
        String cmd = editorForm.getDispatch();

        // Message resources to access button labels.
        MessageResources msgres = getResources(request);

        if (cmd.equals(msgres.getMessage("annotations.button.edit"))) {
            return edit(mapping, form, request, response);
        }
        if (cmd.equals(msgres.getMessage("annotations.button.save"))) {
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
        // The editor form.
        EditorActionForm editorForm = (EditorActionForm) form;

        // The annotation we are editing at the moment.
        CommentBean cb = editorForm.getSelectedAnnotation();

        // Must save this bean.
        cb.setEditState(AbstractEditBean.SAVE);

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
        // The edit form.
        EditorActionForm editorForm = (EditorActionForm) form;

        // The annotation we are about to delete.
        CommentBean cb = editorForm.getSelectedAnnotation();

        // The current view of the edit session.
        AbstractEditViewBean view = getIntactUser(request).getView();
        view.delAnnotation(cb);

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
        // The edit form.
        EditorActionForm editorForm = (EditorActionForm) form;

        // The annotation we are editing at the moment.
        CommentBean cb = editorForm.getSelectedAnnotation();

        // Handler to the EditUserI.
        EditUserI user = getIntactUser(request);

        // The current view of the edit session.
        AbstractEditViewBean view = user.getView();

        // The updated annotation bean.
        CommentBean cbup = new CommentBean(cb.getAnnotation(user), cb.getKey());

        // Save the bean in the view.
        view.saveComment(cb, cbup);

        // Back to the view mode.
        cb.setEditState(AbstractEditBean.VIEW);

        // Back to the edit form.
        return mapping.getInputForward();
    }
}
