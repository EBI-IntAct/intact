package uk.ac.ebi.intact.application.hierarchView.struts;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * Form bean for the main form of the view.jsp page.  
 * This form has the following fields, with default values in square brackets:
 * <ul>
 * <li><b>AC</b> - Entered AC value
 * <li><b>depth</b> - Entered depth value
 * <li><b>method</b> - Selected method value
 * </ul>
 *
 */

public final class VisualizeForm extends ActionForm {


    // --------------------------------------------------- Instance Variables


    /**
     * AC.
     */
    private String AC = null;


    /**
     * The depth.
     */
    private String depth = null;


    /**
     * The noDepthLimit flag.
     */
    private boolean hasNoDepthLimit = false;


    /**
     * The method.
     */
    private String method = null;

    // ----------------------------------------------------------- Properties


    /**
     * Return the AC value.
     */
    public String getAC() {
	return (this.AC);
    }


    /**
     * Set the AC value.
     *
     * @param AC The new AC value
     */
    public void setAC(String AC) {
        this.AC = AC;
    }


    /**
     * Return the depth.
     */
    public String getDepth() {
	return (this.depth);
    }


    /**
     * Set the depth.
     *
     * @param depth The new depth
     */
    public void setDepth (String depth) {
        this.depth = depth;
    }


    /**
     * Return the hasNoDepthLimit flag.
     */
    public boolean getHasNoDepthLimit() {
	return (this.hasNoDepthLimit);
    }


    /**
     * Set the hasNoDepthLimit.
     *
     * @param depth The new flag
     */
    public void setHasNoDepthLimit (boolean flag) {
        this.hasNoDepthLimit = flag;
    }


    /**
     * Return the method.
     */
    public String getMethod() {
	return (this.method);
    }

    /**
     * Set the method.
     *
     * @param method The new method
     */
    public void setMethod (String method) {
        this.method = method;
    }


    // --------------------------------------------------------- Public Methods


    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        this.AC     = null;
        this.depth  = null;
	this.method = null;
	this.hasNoDepthLimit = false;

	// clean the session
	HttpSession session = request.getSession();
	session.invalidate();
    } // reset


    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();

        if ((AC == null) || (AC.length() < 1)) {
	  errors.add("AC", new ActionError("error.AC.required"));	  
	}

        if ((depth == null) || (depth.length() < 1)) {
	  if (false == hasNoDepthLimit) {
	    // the user want to have a limited depth
	    errors.add("depth", new ActionError("error.depth.required"));
	  } else {
	    // don't need to store the depth
	    depth = null;
	  }
	}
	else {
	  try {
	    int i = Integer.parseInt (depth);
	    if (i < 0) {
	      errors.add("depth", new ActionError("error.depth.negative"));
	    }
	  } catch (NumberFormatException e) {
	    errors.add("depth", new ActionError("error.depth.malformed"));
	  }
	}

        if ((method == null) || (method.length() < 1))
            errors.add("method", new ActionError("error.method.required"));

	if (false == errors.empty()) {
	  // delete properties of the bean, so can't be saved in the session.
	  reset(mapping, request);
	}

        return errors;

    } // validate


  public String toString () {

    StringBuffer sb = new StringBuffer("VisualizeForm[AC=");
    sb.append(AC);
    if (depth != null) {
      sb.append(", depth=");
      sb.append(depth);
    }
    if (method != null) {
      sb.append(", method=");
      sb.append(method);
    }
    sb.append(", noDepthLimit=");
    sb.append(hasNoDepthLimit);
    sb.append("]");
    return (sb.toString());

  } // toString

} // VisualizeForm
