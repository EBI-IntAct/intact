/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.framework;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;

import org.apache.struts.action.ActionServlet;
import org.apache.commons.lang.exception.ExceptionUtils;

import uk.ac.ebi.intact.application.cvedit.business.IntactServiceImpl;
import uk.ac.ebi.intact.application.cvedit.business.IntactServiceIF;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.exception.MissingIntactTypesException;

/**
 * This is Intact specific action servlet class. This class provides our own
 * initialization.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactActionServlet extends ActionServlet {

    public void init() throws ServletException {
        // Make sure to call super;s init().
        super.init();

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServletContext();

        // Create an instance of IntactService.
        IntactServiceIF service = null;
        try {
            // Load the Intact Types resources.
            service = new IntactServiceImpl(ctx.getInitParameter("intacttypesfile"));
        }
        catch (MissingIntactTypesException mite) {
            // Unable to find the resource file.
            super.log(ExceptionUtils.getStackTrace(mite));
            throw new ServletException();
        }
        // Make them accessible for any servlets within the server.
        ctx.setAttribute(WebIntactConstants.INTACT_SERVICE, service);
        ctx.setAttribute(WebIntactConstants.INTACT_TYPES, service.getIntactTypes());
    }
}
