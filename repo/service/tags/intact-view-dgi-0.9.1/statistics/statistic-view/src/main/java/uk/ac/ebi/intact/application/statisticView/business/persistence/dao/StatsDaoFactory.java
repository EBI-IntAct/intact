/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.statisticView.business.persistence.dao;

import org.hibernate.ejb.HibernateEntityManager;
import uk.ac.ebi.intact.application.statisticView.business.model.StatsBase;
import uk.ac.ebi.intact.application.statisticView.business.persistence.dao.impl.StatsBaseDaoImpl;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.context.IntactContext;

import javax.persistence.EntityManager;
import java.sql.Connection;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17-Jul-2006</pre>
 */
public class StatsDaoFactory {

    public static <T extends StatsBase> StatsBaseDao getStatsBaseDao( Class<T> stats ) {
        return new StatsBaseDaoImpl<T>( stats, getEntityManager(), IntactContext.getCurrentInstance().getSession() );
    }

    public static Connection connection() {
        return ((HibernateEntityManager)getEntityManager()).getSession().connection();
    }

    private static EntityManager getEntityManager() {
        DataConfig config = IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig();
        return config.getEntityManagerFactory().createEntityManager();
    }

}
