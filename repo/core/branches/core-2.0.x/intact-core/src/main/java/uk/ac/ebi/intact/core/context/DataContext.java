/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;

import java.io.Serializable;

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

    @Deprecated
    public void beginTransactionManualFlush() {
        //getDefaultDataConfig().setAutoFlush(true);
        beginTransaction(  );
    }

    @Deprecated
    public void beginTransaction(  ) {
        daoFactory.beginTransaction();
    }

    @Deprecated
    public void commitTransaction(  ) throws IntactTransactionException {
        try {
            DaoFactory daoFactory = getDaoFactory();

            if (log.isDebugEnabled()) {
               log.debug( "Committing transaction. "  );
            }

            daoFactory.commitTransaction();

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
