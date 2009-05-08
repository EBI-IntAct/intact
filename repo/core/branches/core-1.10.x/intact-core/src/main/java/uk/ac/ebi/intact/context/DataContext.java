/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.business.IntactTransactionException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;
import uk.ac.ebi.intact.config.impl.AbstractJpaDataConfig;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collection;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
@Component
public class DataContext implements Serializable {

    private static final Log log = LogFactory.getLog( DataContext.class );

    @Autowired
    private DaoFactory daoFactory;
    
    @Autowired
    private RuntimeConfig runtimeConfig;

    public DataContext( ) {
    }

//    public void beginTransaction() {
//        beginTransaction(  );
//    }

    public void beginTransactionManualFlush() {
        //getDefaultDataConfig().setAutoFlush(true);
        beginTransaction(  );
    }

    public void beginTransaction(  ) {
        DaoFactory daoFactory = getDaoFactory( (String)null );

        if ( !daoFactory.isTransactionActive() ) {
            log.debug( "Creating new transaction" );
            daoFactory.beginTransaction();
        } else {
            log.debug( "Using existing transaction" );
        }
    }

    public boolean isTransactionActive(  ) {
        return getDaoFactory().isTransactionActive();
    }

    public void commitTransaction(  ) throws IntactTransactionException {
        try {
            DaoFactory daoFactory = getDaoFactory();

            if (log.isDebugEnabled()) {
               log.debug( "Committing transaction. "  );
            }

            daoFactory.commitTransaction();

            assert ( daoFactory.isTransactionActive() == false );

            // flush the CvContext in to avoid lazy initialization errors
            clearCvContext();
        } catch (Exception e) {
            throw new IntactTransactionException( e );
        }
    }

    private void clearCvContext() {
       //CvContext.getCurrentInstance(session).clearCache();
    }

    public Session getSession() {
        return getSessionFromSessionFactory(IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig());
    }

     protected Session getSessionFromSessionFactory(DataConfig dataConfig) {
        Session session;
        if (dataConfig instanceof AbstractHibernateDataConfig) {
            AbstractHibernateDataConfig hibernateDataConfig = (AbstractHibernateDataConfig) dataConfig;
            session = hibernateDataConfig.getSessionFactory().getCurrentSession();
        } else if (dataConfig instanceof AbstractJpaDataConfig) {
            AbstractJpaDataConfig jpaDataConfig = (AbstractJpaDataConfig) dataConfig;
            HibernateEntityManager hibernateEntityManager = (HibernateEntityManager) jpaDataConfig.getSessionFactory().createEntityManager();
            session = hibernateEntityManager.getSession();
        } else {
            throw new IllegalStateException("Wrong DataConfig found: "+dataConfig);
        }
        return session;
    }

    public void commitAllActiveTransactions() throws IntactTransactionException {
        Collection<DataConfig> dataConfigs = runtimeConfig.getDataConfigs();

        for ( DataConfig dataConfig : dataConfigs ) {
            DaoFactory daoFactory = getDaoFactory( dataConfig );

            if ( daoFactory.isTransactionActive() ) {
                daoFactory.commitTransaction();
            }
        }
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    public DaoFactory getDaoFactory( String dataConfigName ) {
        DataConfig dataConfig = runtimeConfig.getDataConfig( dataConfigName );
        return getDaoFactory( dataConfig );
    }

    public boolean isReadOnly() {
        return runtimeConfig.isReadOnlyApp();
    }

    public void flushSession() {
        DataConfig dataConfig = runtimeConfig.getDefaultDataConfig();
        getDaoFactory(dataConfig).getEntityManager().flush();

        // flush the CvContext in to avoid lazy initialization errors
        clearCvContext();
    }

    private DaoFactory getDaoFactory( DataConfig dataConfig ) {
        return daoFactory;
        //return DaoFactory.getCurrentInstance( session, dataConfig );
    }

//    private DataConfig getDefaultDataConfig() {
//        DataConfig dataConfig = runtimeConfig.getDefaultDataConfig();
//
////        if (dataConfig == null) {
////            dataConfig = IntactContext.calculateDefaultDataConfig( session );
////        }
//
//        return dataConfig;
//    }
}
