/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.commons.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

import javax.servlet.*;
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
public class HibernateSessionRequestFilter implements Filter {

    private static Log log = LogFactory.getLog(HibernateSessionRequestFilter.class);

    private static final String TX_TOKEN = IntactTransaction.class.getName();

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        // checks if the transaction has been opened before for this request
        if (request.getAttribute(TX_TOKEN) != null)
        {
            chain.doFilter(request, response);
            return;
        }

        log.debug("Starting a database transaction");

        // initializes the transaction
        IntactTransaction tx = DaoFactory.beginTransaction();

        // puts the token in the request to indicate that this request has already started
        // a transaction
        request.setAttribute(TX_TOKEN, true);

        try
        {
            // Call the next filter (continue request processing)
            chain.doFilter(request, response);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ServletException e)
        {
            e.printStackTrace();
        }
        finally
        {
            log.debug("Committing transaction");

            // commits the transaction
            tx.commit();
        }

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("Initializing filter...");
    }

    public void destroy() {}

}
