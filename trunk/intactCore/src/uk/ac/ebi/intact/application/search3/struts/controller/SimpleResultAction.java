package uk.ac.ebi.intact.application.search3.struts.controller;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import java.util.Collection;
import java.util.Iterator;

import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.view.beans.SimpleViewBean;
import uk.ac.ebi.intact.model.*;

/**
 * This Action class performs the construction of view beans that will be used
 * by the simple.jsp page to display initial search results. Note that this Action
 * is different to all others in that it may have to construct more than a single
 * view bean since the results to be displayed may be of multiple types. Therefore
 * it may not be appropriate to extend from AbstractResultAction as in other cases -
 * TBD.
 *
 * @author Chris Lewington
 * @version $Id$
 */
public class SimpleResultAction extends IntactBaseAction {

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
     * @exception java.io.IOException if an input/output error occurs
     * @exception javax.servlet.ServletException if a servlet exception occurs
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws IOException, ServletException {

        // Session to access various session objects. This will create
        //a new session if one does not exist.
        HttpSession session = super.getSession(request);

        // Handle to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if(user == null) {
            //just set up a new user for the session - if it fails, need to give up!
            user = super.setupUser(request);
            if(user == null) return mapping.findForward(SearchConstants.FORWARD_FAILURE);
        }

        //get the search results from the request
        Collection results = (Collection) request.getAttribute(SearchConstants.SEARCH_RESULTS);

        logger.info("SimpleAction: Collection contains " + results.size() + " items.");

        String contextPath = request.getContextPath();
        //build the URL for searches and pass to the view beans
        //NB probably better to put this in the User object at some point instead...
        String appPath = getServlet().getServletContext().getInitParameter("searchLink");
        String searchURL = contextPath.concat(appPath);

        //we can build a List of sublists, partitioned by type, here inseatd of
        //in the JSP. That way the JSP only ever gets things to display, unless
        //there are too many items to display for a particular type...
        List expList = new ArrayList();
        List interactionList = new ArrayList();
        List proteinList = new ArrayList();
        List cvObjectList = new ArrayList();

        List partitionList = new ArrayList();   //this will hold the seperate lists as items

        for(Iterator it = results.iterator(); it.hasNext();) {

            AnnotatedObject obj = (AnnotatedObject)it.next();
            //now create a relevant view bean for each type in the result set...
            if(Experiment.class.isAssignableFrom(obj.getClass()))
                expList.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));

            if(Interaction.class.isAssignableFrom(obj.getClass()))
                interactionList.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));

            if(Protein.class.isAssignableFrom(obj.getClass())) {

                //have to check for and IGNORE 'orphan' Proteins...
                if(((Protein)obj).getActiveInstances().isEmpty()) continue;
                proteinList.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));
            }

            if(CvObject.class.isAssignableFrom(obj.getClass()))
                cvObjectList.add(new SimpleViewBean(obj, user.getHelpLink(), searchURL, contextPath));
        }

        //now add in the non-empty sublists to the overall viewbean List..
        if (!expList.isEmpty()) partitionList.add(expList);
        if (!interactionList.isEmpty()) partitionList.add(interactionList);
        if (!proteinList.isEmpty()) partitionList.add(proteinList);
        if (!cvObjectList.isEmpty()) partitionList.add(cvObjectList);

        //get the maximum size beans from the context for later use
        Map sizeMap = (Map)session.getServletContext().getAttribute(SearchConstants.MAX_ITEMS_MAP);

        //put the viewbeans in the request and send on to the view...
        request.setAttribute(SearchConstants.VIEW_BEAN_LIST, partitionList);
        return mapping.findForward("simpleResults");

    }

}
