/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.struts.controller;

import uk.ac.ebi.intact.application.hierarchView.business.PropertyLoader;
import uk.ac.ebi.intact.application.hierarchView.business.IntactUserI;
import uk.ac.ebi.intact.application.hierarchView.struts.StrutsConstants;
import uk.ac.ebi.intact.application.hierarchView.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.hierarchView.struts.view.SearchForm;
import uk.ac.ebi.intact.application.hierarchView.exception.SessionExpiredException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Properties;

/**
 * Implementation of <strong>Action</strong> that validates a visualize submisson.
 *
 * @author Samuel Kerrien
 * @version $Id$
 */

public final class SearchAction extends IntactBaseAction {

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
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

        // Clear any previous errors.
        clearErrors();

        // get the current session
        HttpSession session = getSession(request);

        // retreive user fron the session
        IntactUserI user = getIntactUser(session);

        String AC    = null;
        String methodLabel = null;
        String methodClass = null;
        String behaviourDefault = null;

        if (null != form) {
            // read form values from the bean
            AC          = ((SearchForm) form).getAC ();
            methodLabel = ((SearchForm) form).getMethod ();

            // read the highlighting.proterties file
            Properties properties = PropertyLoader.load (StrutsConstants.HIGHLIGHTING_PROPERTY_FILE);

            if (null != properties) {
                methodClass = properties.getProperty ("highlightment.source." + methodLabel + ".class");
                behaviourDefault = properties.getProperty ("highlighting.behaviour.default.class");
            }
        }

        // store the bean (by taking care of the scope)
        if ("request".equals(mapping.getScope()))
            request.setAttribute(mapping.getAttribute(), form);
        else
            session.setAttribute(mapping.getAttribute(), form);

        if (false == isErrorsEmpty()) {
            // Report any errors we have discovered back to the original form
            saveErrors(request);
            return (mapping.findForward("error"));
        } else {
            // Save user's data
            user.setAC (AC);
            user.setDepthToDefault();
            user.resetSourceURL();
            user.setMethodLabel (methodLabel);
            user.setMethodClass (methodClass);
            user.setBehaviour (behaviourDefault);

            // Creation of the graph and the image
            // that method fill the ActionError in case of trouble, so a check is necessary then.
            produceInteractionNetworkImage (user);

            if (false == isErrorsEmpty()) {
                // Report any errors we have discovered during the interaction network producing
                saveErrors(request);
                return (mapping.findForward("error"));
            }
        }

        logger.info ("SearchAction: AC=" + AC +
                     " methodLabel=" + methodLabel +
                     " methodClass=" + methodClass);

        // Forward control to the specified success URI
        return (mapping.findForward("success"));
    }
}




