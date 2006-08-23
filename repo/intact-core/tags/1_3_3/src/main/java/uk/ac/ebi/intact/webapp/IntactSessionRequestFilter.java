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

    private static Log log = LogFactory.getLog(IntactSessionRequestFilter.class);

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpSession session = ((HttpServletRequest)request).getSession();

        log.debug("Creating IntactContext");
        IntactSession intactSession = new WebappSession(session.getServletContext(), session, (HttpServletRequest) request);
        IntactContext context = IntactConfigurator.createIntactContext(intactSession);

        //try
        //{
            // Call the next filter (continue request processing)
            chain.doFilter(request, response);
        //}
        //catch (IOException e)
        //{
         //   e.printStackTrace();
        //}
        //catch (ServletException e)
        //{
         //   e.printStackTrace();
        //}
        //finally
        //{
            log.debug("Committing active transactions");
            try
            {
                context.getDataContext().commitAllActiveTransactions();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        //}

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("Initializing filter...");
    }

    public void destroy() {}

}
