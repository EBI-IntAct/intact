/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search2.struts.controller;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import uk.ac.ebi.intact.application.search2.business.IntactUserIF;
import uk.ac.ebi.intact.application.search2.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search2.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search2.struts.view.AbstractViewBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

/**
 * Perform the common behaviour of the result Action.
 *
 * @author IntAct Team
 * @version $Id$
 */
public abstract class AbstractResulltAction extends IntactBaseAction {

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
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws IOException, ServletException {

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if( user == null ) {
            //browser page caching screwed up the session - need to
            //get a user object created again by forwarding to welcome action...
            return mapping.findForward(SearchConstants.FORWARD_SESSION_LOST);
        }

        //get the search results from the request
        Collection results = (Collection) request.getAttribute( SearchConstants.SEARCH_RESULTS );

        AbstractViewBean bean = getAbstractViewBean( results, user, request.getContextPath());

        if ( bean == null ) {
            super.log("No bean instanciated for empty collection");
            super.addError("error.search", "No data collected in the database.");
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        super.log( bean.getClass() + " created" );
        session.setAttribute( SearchConstants.VIEW_BEAN, bean );
        return mapping.findForward( SearchConstants.FORWARD_RESULTS );
    }



    //////////////////////
    // Abstract methods
    //////////////////////

    protected abstract AbstractViewBean getAbstractViewBean (Collection results, IntactUserIF user, String contextPath );
}
