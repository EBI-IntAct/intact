package uk.ac.ebi.intact.application.search3.struts.controller;

import uk.ac.ebi.intact.application.search3.business.IntactUserIF;
import uk.ac.ebi.intact.application.search3.struts.framework.IntactBaseAction;
import uk.ac.ebi.intact.application.search3.struts.util.SearchConstants;
import uk.ac.ebi.intact.application.commons.util.UrlUtil;
import uk.ac.ebi.intact.persistence.dao.query.SearchableQuery;
import uk.ac.ebi.intact.persistence.dao.SearchableDao;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.Searchable;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.io.IOException;

/**
 * Abstraction of the IntactBaseAction. Gives access to the IntActUser.
 *
 * @author Michael Kleen
 * @version IntactSearchAction.java Date: Feb 17, 2005 Time: 4:13:44 PM
 */
public abstract class IntactSearchAction extends IntactBaseAction {

    private HttpServletRequest myRequest;

    /**
     * Specifies the request value.
     *
     * @param request a HttpServletRequest object specifying the request value
     */
    public void setRequest( final HttpServletRequest request ) {
        this.myRequest = request;
    }

    /**
     * Returns the intact user value.
     *
     * @return an Intact User.
     */
    public IntactUserIF getIntactUser() {

        // Session to access various session objects. This will create
        //a new session if one does not exist.
        HttpSession session = super.getSession( myRequest );

        // Handle to the Intact User.
        IntactUserIF user = super.getIntactUser( session );
        if ( user == null ) {
            //just set up a new user for the session
            user = super.setupUser( myRequest );
        }
        return user;
    }

    /**
     * Returns the search URL value.
     *
     * @return a search URL.
     */
    public String getSearchURL() {
        String contextPath = myRequest.getContextPath();
        String appPath = getServlet().getServletContext().getInitParameter( "searchLink" );
        String searchURL = contextPath.concat( appPath );
        return searchURL;
    }

}