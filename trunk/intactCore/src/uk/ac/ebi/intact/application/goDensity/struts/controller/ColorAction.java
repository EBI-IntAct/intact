/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.goDensity.struts.controller;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.hierarchView.exception.SessionExpiredException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * If user wants to change the state of color-settings
 * of the figure for the session,
 * this Action is called by two radio-buttons (images!).
 *
 * @author Markus Brosch (markus @ brosch.cc)
 * @version $Id$
 */

public final class ColorAction extends Action {

    /**
     * Action execute
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward execute (ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
            throws IOException, ServletException, SessionExpiredException {

        // try to get new state for color settings by radio-buttons.
        // if it fails, by default the settings are set to color.
        String radio = request.getParameter ("radio");
        if (radio == null)
            request.getSession().setAttribute("radio", "color");
        else
            request.getSession().setAttribute("radio", radio);

        // if there is no image yet rendered, forward back to input
        if (request.getSession().getAttribute("gogodensity") == null)
            return mapping.findForward("input");
        // if a image was already rendered, forward back to the image
        else
            return mapping.findForward("success");

    }
}
