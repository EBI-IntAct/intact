/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search3.struts.framework;

import org.apache.struts.action.ActionServlet;
import uk.ac.ebi.intact.application.search3.business.IntactServiceIF;
import uk.ac.ebi.intact.application.search3.business.IntactServiceImpl;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is Intact specific action servlet class. This class provides our own initialization.
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
        String configDir = ctx.getInitParameter( SearchConstants.CONFIG_DIR );
        // Create an instance of IntactService.
        IntactServiceIF service = null;

        // Load the Intact Types resources.
        service = new IntactServiceImpl( configDir );

        // Store the service into the session scope.
        ctx.setAttribute( SearchConstants.INTACT_SERVICE, service );

        //now set the map defining the maximum number of items that can be displayed
        //for given intact types (currently Protein, Experiment, Interaction
        //and CvObject)
        Map sizeMap = new HashMap();
        sizeMap.put( "Experiment", Integer.toString( SearchConstants.MAXIMUM_DISPLAYABLE_EXPERIMENTS ) );
        sizeMap.put( "Interaction", Integer.toString( SearchConstants.MAXIMUM_DISPLAYABLE_INTERACTION ) );
        sizeMap.put( "CvObject", Integer.toString( SearchConstants.MAXIMUM_DISPLAYABLE_CVOBJECTS ) );
        sizeMap.put( "Protein", Integer.toString( SearchConstants.MAXIMUM_DISPLAYABLE_PROTEIN ) );

        ctx.setAttribute( SearchConstants.MAX_ITEMS_MAP, sizeMap );
    }
}