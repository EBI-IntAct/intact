/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.statisticView.business.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import uk.ac.ebi.intact.application.statisticView.business.model.StatsBase;
import uk.ac.ebi.intact.application.statisticView.business.persistence.dao.impl.StatsBaseDaoImpl;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;

import java.sql.Connection;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Jul-2006</pre>
 */
public class StatsDaoFactory
{
    private static final Log log = LogFactory.getLog(StatsDaoFactory.class);

    public static <T extends StatsBase> StatsBaseDao getStatsBaseDao(Class<T> stats)
    {
        return new StatsBaseDaoImpl<T>(stats, getCurrentSession(), IntactContext.getCurrentInstance().getSession());
    }

    public static Connection connection()
    {
        return getCurrentSession().connection();
    }

    public static IntactTransaction beginTransaction()
    {
        Transaction transaction = getCurrentSession().beginTransaction();

        // wrap it
        return new IntactTransaction(transaction);
    }

    private static Session getCurrentSession()
    {
        AbstractHibernateDataConfig config = (AbstractHibernateDataConfig) IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig();
        return config.getSessionFactory().getCurrentSession(); 
    }

}
