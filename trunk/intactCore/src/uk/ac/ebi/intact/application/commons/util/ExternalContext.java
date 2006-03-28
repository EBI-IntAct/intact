/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.commons.util;

import javax.servlet.ServletContext;

/**
 * This "singleton" (with ThreadLocal pattern) contains information about the application context. It is
 * a convenience class to get the ServletContext from any class, without having to use the session or passing
 * the class around
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Mar-2006</pre>
 */
public class ExternalContext
{

    private ServletContext servletContext;

    protected ExternalContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    private static ThreadLocal<ExternalContext> currentInstance = new ThreadLocal()
    {
        // the initial value of the CvContext is null
        protected ExternalContext initialValue()
        {
            return null;
        }

    };

    public static ExternalContext newInstance(ServletContext servletContext)
    {
        ExternalContext externalContext = new ExternalContext(servletContext);
        currentInstance.set(externalContext);

        return externalContext;
    }

    public static ExternalContext getCurrentInstance()
    {
        ExternalContext context = currentInstance.get();

        if (context == null)
        {
            throw new NullPointerException("No current instance of ExternalContext");
        }
        return context;
    }

    public ServletContext getServletContext()
    {
        return servletContext;
    }
}
