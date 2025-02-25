/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.RuntimeConfig;
import uk.ac.ebi.intact.context.impl.WebappSession;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class BasicStartupIntactListener {

    private static final Log log = LogFactory.getLog( BasicStartupIntactListener.class );

    public void contextInitialized( ServletContextEvent servletContextEvent ) {
        IntactSession intactSession = new WebappSession( servletContextEvent.getServletContext(), null, null );

        log.info( "Starting application" );

        // start the intact application (e.g. load Institution, etc)
        IntactConfigurator.initIntact( intactSession );

    }

    public void sessionCreated( HttpSessionEvent httpSessionEvent ) {
        HttpSession session = httpSessionEvent.getSession();

        if (log.isDebugEnabled()) {
            log.debug( "Session started: " + session.getId() );
        }

    }

    public void contextDestroyed( ServletContextEvent servletContextEvent ) {
        if (log.isDebugEnabled()) log.debug( "LogFactory.release and destroying application" );
        LogFactory.release( Thread.currentThread().getContextClassLoader() );

        if (log.isDebugEnabled()) log.debug( "Closing SessionFactory" );
        IntactSession intactSession = new WebappSession( servletContextEvent.getServletContext(), null, null );
        RuntimeConfig.getCurrentInstance( intactSession ).getDefaultDataConfig().getEntityManagerFactory().close();
    }

    public void sessionDestroyed( HttpSessionEvent httpSessionEvent ) {
        if (log.isDebugEnabled()) log.debug( "Session destroyed: " + httpSessionEvent.getSession().getId() );
    }
}