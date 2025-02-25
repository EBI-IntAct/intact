package uk.ac.ebi.intact.application.hierarchView.struts;


import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.util.MessageResources;

import  uk.ac.ebi.intact.application.hierarchView.highlightment.source.*;
import  uk.ac.ebi.intact.application.hierarchView.highlightment.behaviour.*;


/**
 * Implementation of <strong>Action</strong> that validates an highlightment submisson.
 *
 * @author Samuel Kerrien
 * @version 
 */

public final class HighlightmentAction extends Action {


    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward perform(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws IOException, ServletException {

	// Extract attributes we will need
	Locale locale = getLocale(request);
	MessageResources messages = getResources();
	ActionErrors errors = new ActionErrors();
	HttpSession session = request.getSession();

	String behaviour = null;
 
	if (null != form) {
	  behaviour = ((HighlightmentForm) form).getBehaviour ();

	  // get the class method name to create an instance
	  String source = (String) session.getAttribute (Constants.ATTRIBUTE_METHOD_CLASS);

	  // update the option (given in thise request) of the source in the session
	  HighlightmentSource highlightmentSource = HighlightmentSource.getHighlightmentSource(source);
	  if (null != highlightmentSource) {
	    highlightmentSource.parseRequest (request, session);
	  } else {
	    errors.add ("HighlightmentSource", new ActionError("error.HighlightmentSource.unknown", source));
	  }
	}

	// Report any errors we have discovered back to the original form
	if (!errors.empty()) {
	    saveErrors (request, errors);
	    return (new ActionForward(mapping.getInput()));
	}

	// Save our data in the session
	session.setAttribute (Constants.ATTRIBUTE_BEHAVIOUR, behaviour);

	// Print debug in the log file
	if (servlet.getDebug() >= 1)
	    servlet.log("HighlightmentAction: behaviour=" + behaviour +
	                "\nlogged on in session " + session.getId());

        // Remove the obsolete form bean
	if (mapping.getAttribute() != null) {
            if ("request".equals(mapping.getScope()))
                request.removeAttribute(mapping.getAttribute());
            else
                session.removeAttribute(mapping.getAttribute());
        }

	// Forward control to the specified success URI
	return (mapping.findForward("success"));

    }

} // HighlightmentAction
