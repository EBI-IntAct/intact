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

        } catch (Exception e) {
            throw new IntactTransactionException( e );
        }
    }

    public void commitAllActiveTransactions() throws IntactTransactionException {
          daoFactory.commitTransaction();
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    @Deprecated
    public boolean isReadOnly() {
        return false;
    }

    public void flushSession() {
        getDaoFactory().getEntityManager().flush();
    }

   
}
