/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.AnnotatedObject;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The action when the the user selects an entry from a list to edit an object.
 * Short label is passed as a request paramter.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ResultAction extends AbstractEditorAction {

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
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {
        // Handler to the Intact User.
        EditUserI user = super.getIntactUser(request);

        // The class name to search.
        String className = user.getSearchClass();

        // Strip the package bit from the name.
        int lastPos = className.lastIndexOf('.');
        user.setSelectedTopic(className.substring(lastPos + 1));

        // The short label to search
        String shortLabel = request.getParameter("shortLabel");

        LOGGER.info("Label: " + shortLabel + " class: " + className);

        // The selected Annotated object.
        AnnotatedObject annobj =
                (AnnotatedObject) user.getObjectByLabel(className, shortLabel);
        // The object we are editing presently.
        user.updateView(annobj);

        LOGGER.info("Numner of annotations: " + annobj.getAnnotation().size());
        LOGGER.info("Number of xrefs: " + annobj.getXref().size());

        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }
}
