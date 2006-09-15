/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.Collection;

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

    private IntactSession session;

    public DataContext(IntactSession session)
    {
          this.session = session;
    }

    public void beginTransaction()
    {
        beginTransaction(getDefaultDataConfig().getName());
    }

    public void beginTransaction(String dataConfigName)
    {
        DaoFactory daoFactory = getDaoFactory(dataConfigName);

        if (!daoFactory.isTransactionActive())
        {
            log.debug("Creating new transaction for: "+dataConfigName);
            daoFactory.beginTransaction();
        }
        else
        {
            log.debug("Using existing transaction for: "+dataConfigName);
        }
    }

    public boolean isTransactionActive()
    {
        return isTransactionActive(getDefaultDataConfig().getName());
    }

    public boolean isTransactionActive(String dataConfigName)
    {
        return getDaoFactory(dataConfigName).isTransactionActive();
    }

    public void commitTransaction()
    {
         commitTransaction(getDefaultDataConfig().getName());
    }

    public void commitTransaction(String dataConfigName)
    {
        DaoFactory daoFactory = getDaoFactory(dataConfigName);

        if (daoFactory.isTransactionActive())
        {
            daoFactory.getCurrentTransaction().commit();
        }
    }

    public void commitAllActiveTransactions()
    {
        Collection<DataConfig> dataConfigs = RuntimeConfig.getCurrentInstance(session).getDataConfigs();

        for (DataConfig dataConfig : dataConfigs)
        {
            DaoFactory daoFactory = getDaoFactory(dataConfig);

            if (daoFactory.isTransactionActive())
            {
                daoFactory.getCurrentTransaction().commit();
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

    public boolean isReadOnly()
    {
        return RuntimeConfig.getCurrentInstance(session).isReadOnlyApp();
    }

    private DaoFactory getDaoFactory(DataConfig dataConfig)
    {
        DaoFactory daoFactory = DaoFactory.getCurrentInstance(session, dataConfig);

        if (!daoFactory.isTransactionActive())
        {
            daoFactory.beginTransaction(); // starts or uses an existing transaction
        }

        return daoFactory;
    }

    private DataConfig getDefaultDataConfig()
    {
        return RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig();
    }

}
