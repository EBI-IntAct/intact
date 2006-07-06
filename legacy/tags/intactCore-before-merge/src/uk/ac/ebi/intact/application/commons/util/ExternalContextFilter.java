/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.commons.util;

import javax.servlet.*;
import java.io.IOException;

/**
 * Filter to ensure loading of ExternalContext on each request
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Mar-2006</pre>
 */
public class ExternalContextFilter implements Filter
{
    private FilterConfig filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException
    {
         this.filterConfig = filterConfig;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        // creates a new instance of the external context
        ExternalContext.newInstance(filterConfig.getServletContext());

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy()
    {
       this.filterConfig = null;
    }
}
