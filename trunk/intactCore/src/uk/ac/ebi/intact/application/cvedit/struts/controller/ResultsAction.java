/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.controller;

import uk.ac.ebi.intact.application.cvedit.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;
import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.struts.view.CvViewBean;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.persistence.*;
import org.apache.struts.action.*;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The action class for the results.jsp. When clicked on a link it simply
 * passes the short lable as a request parameter to edit.jsp.
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
        // Save the user session.
        HttpSession session = super.getSession(request);

        // Handler to the Intact Usre.
        IntactUserIF user = super.getIntactUser(session);

        // The topic selected by the user.
        String topic = user.getSelectedTopic();

        // The class name to search.
        String className = super.getIntactService().getClassName(topic);

        // Clear any previous errors.
        super.clearErrors();

        // The short label to search
        String shortLabel = (String) request.getParameter("shortLabel");

        // The selected CV object.
        CvObject cvobj = null;

        try {
            // Needs the class object for the topic.
            Class clazz = Class.forName(className);

            super.log("Label: " + shortLabel + " class: " + clazz.getName());
            super.log("Just before search: TRAN " + user.isActive());

            cvobj = (CvObject) user.getObjectByLabel(clazz, shortLabel);
        }
        catch (ClassNotFoundException cnfe) {
            super.log(ExceptionUtils.getStackTrace(cnfe));
            // The errors to report back.
            super.addError("error.class", cnfe.getMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        catch (SearchException se) {
            super.log(ExceptionUtils.getStackTrace(se));
            // The errors to report back.
            super.addError("error.search", se.getNestedMessage());
            super.saveErrors(request);
            return mapping.findForward(WebIntactConstants.FORWARD_FAILURE);
        }
        // The object we are editing presently.
        user.setCurrentEditObject(cvobj);

        super.log("Just after search: TRAN " + user.isActive());
        super.log("Numbner of annotations: " + cvobj.getAnnotation().size());
        super.log("Numbner of xrefs: " + cvobj.getXref().size());

        // Construct the bean for JSP to display.
        CvViewBean viewbean = super.getViewBean(session);
        viewbean.initialise(cvobj);
        viewbean.setTopic(topic);
        session.setAttribute(WebIntactConstants.VIEW_BEAN, viewbean);
        return mapping.findForward(WebIntactConstants.FORWARD_SUCCESS);
    }
}
