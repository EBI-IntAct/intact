package uk.ac.ebi.intact.application.search3.struts.controller;

import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Michael Kleen
 * @version SearchBaseAction.java Date: Feb 17, 2005 Time: 4:13:44 PM
 *          <p/>
 *          SearchBaseAction is the superclass of all search actions. It provides some basic methods to make life easier.
 */
public abstract class SearchBaseAction extends IntactBaseAction {

    private HttpServletRequest myRequest;

    public void setRequest(final HttpServletRequest request) {
        this.myRequest = request;
    }

    public IntactUserIF getIntactUser() {

        // Session to access various session objects. This will create
        //a new session if one does not exist.
        HttpSession session = super.getSession(myRequest);

        // Handle to the Intact User.
        IntactUserIF user = super.getIntactUser(session);
        if (user == null) {
            //just set up a new user for the session
            user = super.setupUser(myRequest);
        }
        return user;
    }

    public String getSearchURL() {
        String contextPath = myRequest.getContextPath();
        String appPath = getServlet().getServletContext().getInitParameter("searchLink");
        String searchURL = contextPath.concat(appPath);
        return searchURL;
    }

}
