/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.cvedit.struts.framework;

import org.apache.struts.action.ActionForm;

import javax.servlet.ServletContext;
import java.util.Collection;

import uk.ac.ebi.intact.application.cvedit.business.IntactUserIF;
import uk.ac.ebi.intact.application.cvedit.struts.framework.util.WebIntactConstants;

/**
 * The super form for all Intact forms.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactBaseForm extends ActionForm {

    /**
     * The user choice.
     */
    private String myAction;

    /**
     * Returns the list for given list name. Just a wrapper
     * to <code>getList(String)</code> method of <code>IntactService</code>.
     *
     * @see uk.ac.ebi.intact.application.cvedit.business.IntactService#getListName(String)
     */
//    public Collection getList(String name) {
//        if (myUser == null) {
//            ServletContext ctx = super.getServlet().getServletContext();
//            myUser = (IntactUser) ctx.getAttribute(
//                WebIntactConstants.SERVICE_INTERFACE);
//        }
//        return myService.getList(name);
//    }

    /**
     * Sets the action taken by the user.
     *
     * @param action the user action.
     */
    public void setAction(String action) {
        myAction = action;
    }

    /**
     * True if the form is submitted.
     *
     * <pre>
     * post: return = true if myAction = 'Submit'
     * </pre>
     */
    public boolean isSubmitted() {
        return myAction.equals("Submit");
    }

    /**
     * True if the form is cancelled.
     *
     * <pre>
     * post: return = true if myAction = 'Cancel'
     * </pre>
     */
    public boolean isCancelled() {
        return myAction.equals("Cancel");
    }

    // Protected Methods for sub classes.

    /**
     * Allows access to user action; for checking an action other than
     * 'Submit' or 'Cancel'.
     */
    protected String getAction() {
        return myAction;
    }
}
