/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public class DataContext
{

    private static final Log log = LogFactory.getLog(DataContext.class);

    private Map<String,IntactTransaction> txMap;
    private IntactSession session;

    private DaoFactory daoFactory;

    public DataContext(IntactSession session)
    {
        txMap = new HashMap<String,IntactTransaction>();
        this.session = session;
    }

    protected IntactTransaction beginTransaction()
    {
        return beginTransaction(StandardCoreDataConfig.NAME);
    }

    protected IntactTransaction beginTransaction(String dataConfigName)
    {
        IntactTransaction tx = txMap.get(dataConfigName);

        if (!isTransactionActive(tx))
        {
            log.debug("Creating new transaction");

            tx = daoFactory.beginTransaction();
            txMap.put(dataConfigName, tx);
        }
        else
        {
            log.debug("Using existing transaction");
        }

        return tx;
    }

    public static boolean isTransactionActive(IntactTransaction tx)
    {
        return (tx != null && !tx.wasCommitted());
    }

    public void commitTransaction()
    {
         commitTransaction(StandardCoreDataConfig.NAME);
    }

    public void commitTransaction(String dataConfigName)
    {
        IntactTransaction tx = txMap.get(dataConfigName);

        if (tx != null && !tx.wasCommitted())
        {
            tx.commit();
        }
    }

    public void commitAllActiveTransactions()
    {
        for (IntactTransaction tx : txMap.values())
        {
            if (isTransactionActive(tx))
            {
                tx.commit();
            }
        }
    }

    public DaoFactory getDaoFactory()
    {
        DataConfig dataConfig = RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig();
        return getDaoFactory(dataConfig);
    }

    public DaoFactory getDaoFactory(String dataConfigName)
    {
        DataConfig dataConfig = RuntimeConfig.getCurrentInstance(session).getDataConfig(dataConfigName);
        return getDaoFactory(dataConfig);
    }

    private DaoFactory getDaoFactory(DataConfig dataConfig)
    {
        daoFactory = DaoFactory.getCurrentInstance(session, dataConfig);
        beginTransaction(); // starts or uses an existing transaction

        return daoFactory;
    }

}
