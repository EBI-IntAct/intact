/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.editor.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;

/**
 * When an HTTP request has to be handled, a new Session and database transaction will begin.
 * Right before the response is send to the client, and after all the work has been done,
 * the transaction will be committed, and the Session will be closed.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class EditorHibernateFilter implements Filter
{

    private static Log log = LogFactory.getLog(EditorHibernateFilter.class);

    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException
    {

        HttpSession session = ((HttpServletRequest)request).getSession();
        Connection connection = null; // get your connection here somehow

        EditorHibernateFilter.log.debug("Starting a database transaction");

        IntactTransaction tx = null;

        if (connection == null)
        {
             tx = DaoFactory.beginTransaction();
        }
        else
        {
             tx = DaoFactory.beginTransaction(connection);
        }

        // Call the next filter (continue request processing)
        chain.doFilter(request, response);

        tx.commit();

    }

    public void init(FilterConfig filterConfig) throws ServletException {
        EditorHibernateFilter.log.debug("Initializing filter...");
    }

    public void destroy() {}

}
