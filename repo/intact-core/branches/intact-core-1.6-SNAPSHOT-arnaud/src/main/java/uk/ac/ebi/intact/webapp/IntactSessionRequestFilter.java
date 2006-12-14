/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.WebappSession;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * When an HTTP request has to be handled, a new Session and database transaction will begin.
 * Right before the response is send to the client, and after all the work has been done,
 * the transaction will be committed, and the Session will be closed.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class IntactSessionRequestFilter implements Filter {

    private static final Log log = LogFactory.getLog(IntactSessionRequestFilter.class);

    private static final String FILTERED_PARAM_NAME = "uk.ac.ebi.intact.filter.EXCLUDED_EXTENSIONS";

    private static final String[] DEFAULT_EXCLUDED_EXTENSIONS = new String[] { ".js" };

    private List<String> excludedExtensions;

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)request;
        HttpSession session = req.getSession();

        String requestUrl = req.getRequestURL().toString();

        // if the the url end matches with a filtered extensions do not start IntactContext
        for (String ext : excludedExtensions)
        {
            if (requestUrl.toLowerCase().endsWith(ext.toLowerCase()))
            {
                log.debug("Context not created for (excluded): "+requestUrl);
                chain.doFilter(request, response);
                return;
            }
        }

        log.debug("Creating IntactContext, for request url: "+requestUrl);
        IntactSession intactSession = new WebappSession(session.getServletContext(), session, req);
        IntactContext context = IntactConfigurator.createIntactContext(intactSession);

        try
        {
            // Call the next filter (continue request processing)
            chain.doFilter(request, response);
        }
        catch (IOException e)
        {
//           e.printStackTrace();
        	// use the lograther than standard output or some errors could be raised 
        	log.error("intact session: ", e);
        }
        catch (ServletException e)
        {
//           e.printStackTrace();
        	log.error("intact session: ", e);
        }
        finally
        {
            log.debug("Committing active transactions");
            try
            {
                context.getDataContext().commitAllActiveTransactions();
                log.debug("Committed");
            }
            catch (Exception e)
            {
//                e.printStackTrace();
            	log.error("intact session in finally block: ", e);
            }
        }

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("Initializing filter...");

        excludedExtensions = new ArrayList<String>();

        for (String defaultNotFilterExt : DEFAULT_EXCLUDED_EXTENSIONS)
        {
            excludedExtensions.add(defaultNotFilterExt);
        }

        String paramValue = filterConfig.getInitParameter(FILTERED_PARAM_NAME);

        if (paramValue != null)
        {
            String[] fexts = paramValue.split(",");

            for (String fext : fexts)
            {
                fext = fext.trim();

                if (fext.startsWith("*"))
                {
                    fext = fext.substring(1, fext.length());
                }

                excludedExtensions.add(fext);
            }
        }

        log.debug("Will not create IntactContexts for requests URL ending with: "+ excludedExtensions);
    }

    public void destroy() {}

}
