/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import org.apache.struts.action.*;
import org.apache.commons.beanutils.DynaBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Sets up the editor form type using the selected editor topic. The common forms
 * for all the topics (such as annotations, xrefs) are populated.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class SetUpEditorAction  extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = super.getIntactUser(request);

        // The view of the current object we are editing at the moment.
        AbstractEditViewBean view = user.getView();

        String cvFormName = EditorConstants.FORM_CVINFO;
        DynaBean cvInfoBean = user.getDynaBean(cvFormName, request);
        cvInfoBean.set("ac", view.getAc());
        cvInfoBean.set("shortLabel", view.getShortLabel());
        cvInfoBean.set("fullName", view.getFullName());
        request.setAttribute(cvFormName, cvInfoBean);

        // The session to retrieve forms.
        HttpSession session = getSession(request);

        // The annotation form.
        EditForm commentForm =
                getEditForm(session, EditorConstants.FORM_COMMENT_EDIT);
        // Populate with annotations.
        commentForm.setItems(view.getAnnotations());

        // The xref form.
        EditForm xrefForm = getEditForm(session, EditorConstants.FORM_XREF_EDIT);
        // Populate with xrefs.
        xrefForm.setItems(view.getXrefs());
//        LOGGER.info("Annotation ITEMS: " + commentForm.getItems().length);
//        LOGGER.info("Xref ITEMS: " + xrefForm.getItems().length);

        String topic = user.getSelectedTopic();
        if (topic.equals("BioSource")) {
            return mapping.findForward("biosource");
        }
        if (topic.equals("Experiment")) {
            return mapping.findForward("experiment");
        }
        if (topic.equals("Interaction")) {
            return mapping.findForward("interaction");
        }
        // Strainght to the editor.
        return mapping.findForward(EditorConstants.FORWARD_EDITOR);
    }
}
