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
import uk.ac.ebi.intact.application.search2.struts.view.ViewBeanFactory;
import uk.ac.ebi.intact.model.AnnotatedObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * This class provides the actions required to generate information to be displayed in
 * a 'single object view'. Examples are single Protein views, CvObject views, etc. Typically this class
 * will not be accessed directly - after a search the <code>SearchAction</code>
 * class will forward to this Action class if appropriate.
 *
 * @author Chris Lewington (clewing@ebi.ac.uk)
 * @version $Id$
 */
public class FreeTextSearchAction extends IntactBaseAction {

    /**
     * Process the specified HTTP request, and create the corresponding
     * HTTP response (or forward to another web component that will create
     * it). Return an ActionForward instance describing where and how
     * control should be forwarded, or null if the response has
     * already been completed.
     *
     * @param mapping - The <code>ActionMapping</code> used to select this instance
     * @param form - The optional <code>ActionForm</code> bean for this request (if any)
     * @param request - The HTTP request we are processing
     * @param response - The HTTP response we are creating
     *
     * @return - represents a destination to which the controller servlet,
     * <code>ActionServlet</code>, might be directed to perform a RequestDispatcher.forward()
     * or HttpServletResponse.sendRedirect() to, as a result of processing
     * activities of an <code>Action</code> class
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws  Exception {

        // Clear any previous errors.
        super.clearErrors();

        // The help link for the html builder.
        String relativeHelpLink =
                getServlet().getServletContext().getInitParameter("helpLink");

        //build the help link out of the context path - strip off the 'search' bit...
        String ctxtPath = request.getContextPath();
        String relativePath = ctxtPath.substring(0, ctxtPath.lastIndexOf("search"));
        String helpLink = relativePath.concat(relativeHelpLink);

        // Session to access various session objects.
        HttpSession session = super.getSession(request);

        // Handler to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if(user == null) {
            //browser page caching screwed up the session - need to
            //get a user object created again by forwarding to welcome action...
            return mapping.findForward(SearchConstants.FORWARD_SESSION_LOST);

        }

        //get the search results from the request
        Collection results = (Collection)request.getAttribute(SearchConstants.SEARCH_RESULTS);
        if(results == null) {
            super.log("Can't display search details - no results saved!");
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        super.log("Free text search action: building view beans...");

        AbstractViewBean bean = ViewBeanFactory.getInstance().getViewBean(
                results, helpLink );
        if ( bean == null ) {
            if (results.size() > 0) {
                AnnotatedObject ao = (AnnotatedObject) results.iterator().next();
                super.log("No bean instanciated for " + ao.getClass());
                super.addError("error.search", ao.getClass() + " not supported");
            } else {
                super.log("No bean instanciated for empty collection");
                super.addError("error.search", "No data collected in the database.");
            }
            super.saveErrors(request);
            return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        super.log( bean.getClass() + " created" );
        session.setAttribute(SearchConstants.VIEW_BEAN, bean);
        return mapping.findForward(SearchConstants.FORWARD_RESULTS);
    }

//-------------------------- private helper methods -----------------------------------
}
