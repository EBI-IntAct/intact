/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.action;

import uk.ac.ebi.intact.application.editor.struts.framework.AbstractEditorAction;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.struts.framework.util.ROViewBeanFactory;
import uk.ac.ebi.intact.application.editor.business.EditUserI;
import uk.ac.ebi.intact.model.AnnotatedObject;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This action is invoked when the user clicks on a link to get a read only view
 * of an object. The topic to view and its short label are passed as request
 * parameters.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class DisplayAction extends AbstractEditorAction {

    /**
     * Construts a read only view of an edit object. The object to display is
     * retreived using request parameters.
     * @param mapping the <code>ActionMapping</code> used to select this instance
     * @param form the optional <code>ActionForm</code> bean for this request
     * (if any).
     * @param request the HTTP request we are processing; this contains the
     * topic name (e.g., CvDatabase) and the short label (e.g., GO).
     * @param response the HTTP response we are creating
     * @return failure mapping for any errors in searching the CV database;
     * no matches if the search failed to find any records; success if the
     * a read only version is constructed.
     * @throws Exception for any uncaught errors.
     */
    public ActionForward execute(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
            throws Exception {
        // The topic and the short label.
        String topic = request.getParameter("topic");
        String label = request.getParameter("shortLabel");
        LOGGER.info("Topic is: " + topic + " label: " + label);

        // The user to do the search.
        EditUserI user = getIntactUser(request);

        // The class name associated with the topic.
        String classname = super.getService().getClassName(topic);

        request.setAttribute("view",
                user.getReadOnlyView(Class.forName(classname), label));
        return mapping.findForward(EditorConstants.FORWARD_SUCCESS);
    }
}
