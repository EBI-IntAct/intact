/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.AbstractEditViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.EditForm;
import uk.ac.ebi.intact.application.editor.struts.view.XrefEditForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sets up the editor form type using the selected editor topic. The common forms
 * for all the topics (such as annotations, xrefs) are populated.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FillXrefFormAction  extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        if (form == null) {
            System.out.println("Creating a new xref form");
            form = new XrefEditForm();
            if ("request".equals(mapping.getScope())) {
                request.setAttribute(mapping.getName(), form);
            }
            else {
                getSession(request).setAttribute(mapping.getName(), form);
            }
        }
        else {
            System.out.println("Using an existing xref form");
        }
        XrefEditForm editform = (XrefEditForm) form;
        // The view of the current object we are editing at the moment.
        AbstractEditViewBean view = getIntactUser(request).getView();
        editform.setItems(view.getXrefs());
        // Strainght to the editor.
        return mapping.findForward("success");
    }
}
