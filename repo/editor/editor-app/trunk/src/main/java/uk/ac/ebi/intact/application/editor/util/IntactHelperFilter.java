/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.application.editor.util;

import org.apache.log4j.Logger;
import uk.ac.ebi.intact.application.editor.struts.framework.util.EditorConstants;
import uk.ac.ebi.intact.business.IntactException;

import javax.servlet.*;
import java.io.IOException;

/**
 * This servlet filter to ensure that IntactHelper is closed before sending
 * out the response
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class IntactHelperFilter implements Filter {

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
        ourLog.debug("IntactHelperFilter has been initialised");
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
        if (IntactHelperUtil.getIntactHelper() != null) {
            String error = "An IntactHelper is already associated with this thread!";
            ourLog.error(error);
            throw new IllegalStateException(error);
        }
        try {
            ourLog.debug("About to call the next in d chain, IHFiler");
            chain.doFilter(req, res);
        }
        finally {
            ourLog.debug("Just about to close the helper");
            try {
                IntactHelperUtil.closeIntactHelper();
            }
            catch (IntactException ex) {
                ourLog.error("Error in closing IntactHelper", ex);
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
}
