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
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sets up the editor form type using the selected editor topic.
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
        // The topic to choose where to go.
        String topic = getIntactUser(request).getSelectedTopic();
        if (topic.equals("BioSource")) {
            return mapping.findForward("biosource");
        }
        if (topic.equals("Experiment")) {
            return mapping.findForward("experiment");
        }
        if (topic.equals("Interaction")) {
            return mapping.findForward("interaction");
        }
        // Straight to the editor.
        return mapping.findForward(EditorConstants.FORWARD_EDITOR);
    }
}
