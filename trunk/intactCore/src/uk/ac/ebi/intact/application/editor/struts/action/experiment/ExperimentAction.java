/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action.experiment;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.view.experiment.ExperimentViewBean;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action class when the use presses Save button to save Experiment related
 * information.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentAction extends AbstractEditorAction {

    /**
     * Action for submitting the CV info form.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in searching the database; refresh
     * mapping if the screen needs to be updated (this will only happen if the
     * short label on the screen is different to the short label returned by
     * getUnqiueShortLabel method. For all other instances, success mapping is
     * returned.
     * @throws java.lang.Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // The form to access input data.
        DynaActionForm theForm = (DynaActionForm) form;

        // Handler to the current user.
        EditUserI user = super.getIntactUser(request);

        // This shouldn't crash the application as we had
        // already created the correct editor view bean.
        ExperimentViewBean view = (ExperimentViewBean) user.getView();

        String organism = (String) theForm.get("organism");

        // These two items need to be normalized.
        String interaction = view.getNormalizedInter(
                (String) theForm.get("inter"));
        String identification = view.getNormalizedIdent(
                (String) theForm.get("ident"));

        // Set the view bean with the new values.
        view.setOrganism(organism);
        view.setInter(interaction);
        view.setIdent(identification);

        return mapping.findForward(FORWARD_SUCCESS);
    }
}
