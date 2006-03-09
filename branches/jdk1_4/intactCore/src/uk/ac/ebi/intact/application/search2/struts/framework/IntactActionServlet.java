/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.framework;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts.action.ActionServlet;
import uk.ac.ebi.intact.application.search2.business.IntactServiceIF;
import uk.ac.ebi.intact.application.search2.business.IntactServiceImpl;
import uk.ac.ebi.intact.application.search2.struts.framework.util.SearchConstants;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;

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

        // The configuration dir.
        String configDir = ctx.getInitParameter(SearchConstants.CONFIG_DIR);
        // Create an instance of IntactService.
        IntactServiceIF service = null;
        try {
            // Load the Intact Types resources.
            service = new IntactServiceImpl(configDir);
        }
        catch (IOException ioe) {
            // Unable to load the properties file.
            super.log(ExceptionUtils.getStackTrace(ioe));
            throw new ServletException();
        }
        // Store the service into the session scope.
        ctx.setAttribute(SearchConstants.INTACT_SERVICE, service);
    }
}
