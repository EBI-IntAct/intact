/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.security;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This servlet filter intercepts all inbound requests to the editor application.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class LoginCheckFilter implements Filter {

    /**
     * For logging.
     */
    private static final Logger ourLog = Logger.getLogger(EditorConstants.LOGGER);

    /**
     * A filter configuration object used by the web container to pass
     * information to a filter during initialization
     */
    private FilterConfig myFilterConfig;

    /**
     * The servlet container calls the init method exactly once after
     * instantiating the filter (similar servlet's init method). The init method
     *
     * @param filterConfig the filter configuration.
     */
    public void init(FilterConfig filterConfig) {
        myFilterConfig = filterConfig;
        ourLog.debug("LoginCheckFilter has been initialised");
    }


    /**
     * The <code>doFilter()</code> method performs the actual filtering work.
     * In its doFilter() method, each filter receives the current request and
     * response, as well as a FilterChain containing the filters that still must be
     * processed.
     *
     * @param req Servlet request object
     * @param res Servlet response object
     * @param chain Filter chain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;

        // Don't create a new session here or else someone can SPAM the editor
        // with bogus sessions.
        HttpSession session = request.getSession(false);

        String uri = request.getRequestURI();
        ourLog.debug("Requested URI " + uri);

        // Lets struts handle when there is no session.
        if (session == null) {
            // Check for static HTML pages.
            if (uri.endsWith("html")) {
                ourLog.debug("Plain html request, lets server serve it");
                // Just a plain HTML page. Let's pass thru.
                chain.doFilter(req, res);
            }
            else {
                ourLog.debug("New session, let's forward to the welcome action");
                // Make sure to forward to the welcome action.
                getWelcomeDispatcher().forward(req, res);
            }
        }
        else {
            // Got a session; check the URI.
            if (uri.indexOf("/editor/do/secure/edit") == -1) {
                // Trying to access non secure part of the editor. Lets Struts take
                // care of it.
                ourLog.debug("Allow access to non secure area, lets Struts to handle this URL");
                chain.doFilter(req, res);
            }
            else {
                // Requested a secure area of the editor. Check the logged in status.
                if (session.getAttribute(EditorConstants.LOGGED_IN) != null) {
                    ourLog.debug("Found a logged in user - passing thru");
                    chain.doFilter(req, res);
                }
                else {
                    ourLog.debug("New user - let's forward to the welcome action");
                    getWelcomeDispatcher().forward(request, res);
                }
            }
        }
    }


    /**
     * Called by the web container to indicate to a filter that it is being taken
     * out of service. As with init() method, this method is only called once all
     * threads within the filter's doFilter method have exited or after a timeout
     * period has passed.
     */
    public void destroy() {
        myFilterConfig = null;
    }

    /**
     * Returns the welcome action request dispatcher.
     *
     * @return the welcome action RequestDispatcher object.
     */
    private RequestDispatcher getWelcomeDispatcher() {
        ServletContext ctx = myFilterConfig.getServletContext();
        return ctx.getRequestDispatcher("/do/welcome");
    }
}
