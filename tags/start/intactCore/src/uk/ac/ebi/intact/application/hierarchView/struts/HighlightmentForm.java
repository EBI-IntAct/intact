package uk.ac.ebi.intact.application.hierarchView.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * Form bean for the highlightment form of the view.jsp page.  
 * This form has the following fields, with default values in square brackets:
 * <ul>
 * <li><b>behaviour</b> - Entered behaviour value
 * </ul>
 * 
 */

public final class HighlightmentForm extends ActionForm {


    // --------------------------------------------------- Instance Variables


    /**
     * behaviour.
     */
    private String behaviour = null;


    // ----------------------------------------------------------- Properties


    /**
     * Return the behaviour value.
     */
    public String getBehaviour() {
	return (this.behaviour);
    }


    /**
     * Set the behaviour value.
     *
     * @param behaviour The new behaviour value
     */
    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }



    // --------------------------------------------------------- Public Methods


    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        this.behaviour = null;

	// clean the session	
	HttpSession session = request.getSession();
	session.removeAttribute(Constants.ATTRIBUTE_BEHAVIOUR);

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

        if ((behaviour == null) || (behaviour.length() < 1))
            errors.add("behaviour", new ActionError("error.behaviour.required"));

	try {

	  // try to load the specified behaviour
	  Class cls = Class.forName (behaviour);

	} catch (ClassNotFoundException e) {
	  errors.add("behaviour", new ActionError("error.behaviour.unknown", behaviour));
	}
	catch (Exception e) {
	  errors.add("behaviour", new ActionError("error.behaviour.unexpected", e.getMessage() ) ) ;
	}

	if (false == errors.empty()) {
	  // delete properties of the bean, so can't be saved int the session.
	  reset(mapping, request);
	}

        return errors;

    } // validate


} // HighlightmentForm
