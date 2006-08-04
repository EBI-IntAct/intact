/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context.impl;

import uk.ac.ebi.intact.context.IntactSession;

import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import java.io.Serializable;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class WebappSession extends IntactSession
{

    private HttpSession session;
    private ServletContext servletContext;

    public WebappSession(ServletContext servletContext, HttpSession session)
    {
        this.session = session;
        this.servletContext = servletContext;
    }


    public Object getApplicationAttribute(String name)
    {
        return servletContext.getAttribute(name);
    }

    public void setApplicationAttribute(String name, Object attribute)
    {
        servletContext.setAttribute(name, attribute);
    }

    public Serializable getAttribute(String name)
    {
        return (Serializable) session.getAttribute(name);
    }

    public void setAttribute(String name, Serializable attribute)
    {
        session.setAttribute(name, attribute);
    }

    public boolean containsInitParam(String name)
    {
        return (servletContext.getInitParameter(name) != null);
    }

    public String getInitParam(String name)
    {
        return servletContext.getInitParameter(name);
    }

    public boolean isWebapp()
    {
        return true;
    }
}
