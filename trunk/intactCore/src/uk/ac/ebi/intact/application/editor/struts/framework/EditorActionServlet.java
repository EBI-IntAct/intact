/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.struts.framework;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;

import org.apache.struts.action.ActionServlet;
import org.apache.commons.lang.exception.ExceptionUtils;

import uk.ac.ebi.intact.application.editor.business.EditorService;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.application.editor.exception.EmptyTopicsException;

import java.util.MissingResourceException;
import java.net.MalformedURLException;

/**
 * This is Intact editor specific action servlet class. This class is
 * responsible for initializing application wide resources.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class EditorActionServlet extends ActionServlet {

    public void init() throws ServletException {
        // Make sure to call super;s init().
        super.init();

        // Save the context to avoid repeat calls.
        ServletContext ctx = super.getServletContext();

        // Create an instance of EditorService.
        EditorService service = new EditorService();
        try {
            // Load resources.
            service.loadTopicProperties(ctx.getInitParameter("topics"));
            service.initNewtServer(ctx.getInitParameter("newt"));
        }
        catch (MissingResourceException mre) {
            // Unable to find the resource file.
            super.log(ExceptionUtils.getStackTrace(mre));
            throw new ServletException();
        }
        catch (EmptyTopicsException mite) {
            // An empty topic resource file.
            super.log(ExceptionUtils.getStackTrace(mite));
            throw new ServletException();
        }
        catch (MalformedURLException murle) {
            // Incorrect URL for the Newt server.
            super.log(ExceptionUtils.getStackTrace(murle));
            // Log the error and carry on - we should able to use the rest of
            // editor functionalities.
        }
        // Make them accessible for any servlets within the server.
        ctx.setAttribute(EditorConstants.EDITOR_SERVICE, service);
        ctx.setAttribute(EditorConstants.EDITOR_TOPICS, service.getIntactTypes());
    }
}
