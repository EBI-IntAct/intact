/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import org.apache.struts.tiles.actions.TilesAction;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.CvEditConstants;
import uk.ac.ebi.intact.application.cvedit.exception.SessionExpiredException;

/**
 * This action class is responsible for appending the class type to the header.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class TilesHeaderAction extends TilesAction {

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it),
     * with provision for handling exceptions thrown by the business logic.
     * Override this method to provide functionality
     *
     * @param context the current Tile context, containing Tile attributes.
     * @param mapping the ActionMapping used to select this instance.
     * @param form the optional ActionForm bean for this request (if any).
     * @param request the HTTP request we are processing.
     * @param response the HTTP response we are creating
     * @return null because there is no forward associated with this action.
     * @throws Exception if the application business logic throws an exception.
     */
    public ActionForward execute(ComponentContext context,
                             ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
                      throws Exception {
        // The existing title; the attribute name must match with the title
        // given in the tiles definition file.
        String oldtitle = (String) context.getAttribute("header.title");

        // Don't create a new session.
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new SessionExpiredException();
        }
        IntactUserIF user = (IntactUserIF)
            session.getAttribute(CvEditConstants.INTACT_USER);
        // Only change the header if we are editing; need to check for null
        // here because 'user' is null when the logout action was selected.
        if ((user != null) && user.isEditing()) {
            // Append the topic to the existing title.
            String newtitle = oldtitle + " - " + user.getSelectedTopic();
            context.putAttribute("header.title", newtitle);
        }
        return null;
    }
}
