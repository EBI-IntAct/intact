/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.search.struts.controller;

import uk.ac.ebi.intact.application.search.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search.struts.framework.util.SearchConstants;
import uk.ac.ebi.intact.application.search.struts.framework.util.TreeViewAction;
import uk.ac.ebi.intact.application.search.struts.view.IntactViewBean;
import uk.ac.ebi.intact.application.search.struts.view.ViewForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;

import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Process the user submitted search form.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ViewAction extends IntactBaseAction {

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
                                  HttpServletResponse response) throws Exception {
        // The collection of beans to process.
        Map idToView = (Map) super.getSession(request).getAttribute(
                SearchConstants.FORWARD_MATCHES);

        // The user action.
        TreeViewAction action = TreeViewAction.factory((ViewForm) form);

        // Save the parameters from the view page.
        Map map = request.getParameterMap();

        // Only search for the check boxes if the number of parameters in a
        // request exceed 1 (one parameter is always returned for buttons).
        if (map.size() > 1) {

            // Search through the parameter lists (check boxes).
            for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = (String) entry.getKey();

                // Ignore the non tbl parameters.
                if (!key.startsWith("tbl_")) {
                    continue;
                }
                // Extract the bean id and the AC.
                StringTokenizer stk = new StringTokenizer(key, "_");
                String ignore = stk.nextToken();
                String beanId = stk.nextToken();
                String ac = stk.nextToken();

                // The view bean associated with the id.
                IntactViewBean bean = (IntactViewBean) idToView.get(beanId);

                // Change the status for the ac.
                bean.setTreeStatus(action, ac);
            }
        }
//        else {
//            // No check boxes were selected. Assume all is wanted.
//            for (Iterator iter = idToView.values().iterator(); iter.hasNext(); ) {
//                IntactViewBean bean = (IntactViewBean) iter.next();
//                bean.setTreeStatus(action);
//            }
//        }
        // Move to the results page.
        return mapping.findForward(SearchConstants.FORWARD_RESULTS);
    }
}
