/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.interaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.InteractionViewBean;
import uk.ac.ebi.intact.application.editor.struts.view.interaction.ProteinEditForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action to populate the form with proteins.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class FillProteinFormAction extends AbstractEditorAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The view of the current object we are editing at the moment.
        InteractionViewBean view = (InteractionViewBean)
                getIntactUser(request).getView();

        // This is essentially a workaround to avoid array index
        // out of bounds exception for deleting an unsaved Protein. Had
        // we removed the bean in ProteinEditAction, struts tries to populate
        // the form with inputs. For example, had we removed protein[5] from the
        // view, the struts tries to poluate the form with protein[5] values.
        // Since we had already removed protein[5] from the view bean, this will
        // cause array index exception. What is happening here is that we let
        // struts to initialize the form and then remove it before initializing
        // the form.
        ProteinEditForm protform = (ProteinEditForm) form;
        int pos = protform.getDelProteinPos();
//        System.out.println("The position: " + pos);
        if (pos != -1) {
            view.delProtein(pos);
        }
        // Populate the form.
        protform.setProteins(view.getProteins());

        return mapping.findForward(FORWARD_SUCCESS);
    }
}
