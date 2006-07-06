/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.framework;

import javax.servlet.ServletException;
import javax.servlet.ServletContext;

import org.apache.struts.action.ActionServlet;

import uk.ac.ebi.intact.struts.service.IntactService;
import uk.ac.ebi.intact.struts.service.IntactServiceImpl;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.framework.exceptions.MissingIntactTypesException;
import uk.ac.ebi.intact.persistence.DataSourceException;
import uk.ac.ebi.intact.model.Constants;

import java.util.ResourceBundle;
import java.util.Map;
import java.beans.IntrospectionException;

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

        // Name of the mapping file and data source.
        String mapping = ctx.getInitParameter(Constants.MAPPING_FILE_KEY);
        String ds = ctx.getInitParameter(WebIntactConstants.DATA_SOURCE);

        // Create an instance of IntactService.
        IntactService service = null;
        try {
            service = new IntactServiceImpl(mapping, ds);
        }
        catch (DataSourceException de) {
            // Unable to get a data source...can't proceed
            super.log("failed to obtain a data source: " + de.getNestedMessage());
            throw new ServletException();
        }

        // The Intact Types file.
        String types = ctx.getInitParameter(WebIntactConstants.INTACT_TYPES_FILE);
        try {
            // Load the Intact Types respources.
            service.setIntactTypes(types);
        }
        catch (MissingIntactTypesException mite) {
            super.log("error with loading intact types resource file");
            throw new ServletException();
        }
        catch (ClassNotFoundException cnfe) {
            super.log("failed to find intact object on loading", cnfe);
            // Carry on; may cause problems later on.
        }
        catch (IntrospectionException ie) {
            super.log("failed to get reflection data on object", ie);
            // Carry on; may cause problems later on.
        }

        // Store the service into the application scope.
        super.getServletContext().setAttribute(
            WebIntactConstants.SERVICE_INTERFACE, service);
    }
}
