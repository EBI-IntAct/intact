/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.struts.controller;

import uk.ac.ebi.intact.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.struts.framework.IntactUserSession;
import uk.ac.ebi.intact.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.struts.service.IntactService;
import uk.ac.ebi.intact.struts.view.CvViewBean;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvObject;
import org.apache.struts.action.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspException;

/**
 * The action class for the results.jsp. This simply forwards to details.jsp.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class ResultsAction extends IntactBaseAction {

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
    public ActionForward perform (ActionMapping mapping, ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        // Need to get to the service handler saved in page context.
        IntactService service = super.getIntactService();

        // Save the user session.
        HttpSession session = super.getSession(request);

        // We also need the topic the user has selected as well.
        IntactUserSession userSession = (IntactUserSession) session.getAttribute(
            WebIntactConstants.USER_SESSION);

        // Save the selected topic as we need it to store in CvViewBean later.
        String topic = userSession.getSelectedTopic();

        // The class name to search.
        String className = service.getClassName(topic);

        try {
            // Needs the class object for the topic.
            Class clazz = Class.forName(className);

            // The helper to get the Intact CV object.
            IntactHelper helper =  service.getIntactHelper();

            // The short label to search
            String shortLabel = (String) request.getParameter("shortLabel");
            super.log("Label: " + shortLabel + " class: " + clazz.getName());
            CvObject cvobj = (CvObject) helper.getObjectByLabel(clazz, shortLabel);

            // The view bean for JSP to access; save it in a session for JSP.
            // Reuse an existing bean without creating a new bean for each call.
            CvViewBean viewbean = (CvViewBean) session.getAttribute("viewbean");
            if (viewbean == null) {
                viewbean = new CvViewBean();
            }
            viewbean.initialise(cvobj);
            viewbean.setTopic(topic);
            session.setAttribute("viewbean", viewbean);
        }
        catch (ClassNotFoundException cnfe) {
            //return to action servlet witha forward to error page command
            String msg = "unable to find the selected Intact object";
            super.log("results action: " + msg);

            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add("no.class.found", new ActionError("error.no.class"));
            super.saveErrors(request, errors);
            return (mapping.findForward(WebIntactConstants.FORWARD_FAILURE));
        }
        catch (IntactException ie) {
            //return to action servlet with forward to error page command
            String msg = "unable to find the selected Intact object";
            super.log("results action: " + msg);

            // The errors to report back.
            ActionErrors errors = new ActionErrors();
            errors.add("intact.error", new ActionError("error.intact"));
            super.saveErrors(request, errors);
            return (mapping.findForward(WebIntactConstants.FORWARD_FAILURE));
        }
        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }
}
