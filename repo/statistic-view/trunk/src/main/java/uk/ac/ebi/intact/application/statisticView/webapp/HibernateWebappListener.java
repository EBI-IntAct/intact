/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.statisticView.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.WebappSession;
import uk.ac.ebi.intact.application.statisticView.business.model.IntactStatistics;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Listener used to start hibernate and add the package with the model classes
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>18-Jul-2006</pre>
 */
public class HibernateWebappListener implements ServletContextListener
{

    private static final Log log = LogFactory.getLog(HibernateWebappListener.class);

    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        log.info("Adding package with model entities and Initializing hibernate");

        IntactSession session = new WebappSession(servletContextEvent.getServletContext(), null, null);
        StandardCoreDataConfig dataConfig = new StandardCoreDataConfig(session);

        dataConfig.addPackageWithEntities(IntactStatistics.class.getPackage().getName());
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {

    }
}
