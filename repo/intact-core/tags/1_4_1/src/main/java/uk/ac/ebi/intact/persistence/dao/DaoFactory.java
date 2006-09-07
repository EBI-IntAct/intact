/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.impl.*;

import java.io.Serializable;
import java.sql.Connection;

/**
 * Factory for all the intact DAOs using Hibernate
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class DaoFactory implements Serializable
{
    private static final Log log = LogFactory.getLog(DaoFactory.class);

    private static final String DAO_FACTORY_ATT_NAME = DaoFactory.class.getName();

    private AbstractHibernateDataConfig dataConfig;
    private IntactSession intactSession;

    private IntactTransaction currentTransaction;

    private DaoFactory(DataConfig dataConfig, IntactSession intactSession)
    {
        this.dataConfig = (AbstractHibernateDataConfig) dataConfig;
        this.intactSession = intactSession;
    }

    public static DaoFactory getCurrentInstance(IntactContext context)
    {
        return getCurrentInstance(context.getSession(), context.getConfig().getDefaultDataConfig());
    }

    public static DaoFactory getCurrentInstance(IntactContext context, String dataConfigName)
    {
        return getCurrentInstance(context.getSession(), context.getConfig().getDataConfig(dataConfigName));
    }

    public static DaoFactory getCurrentInstance(IntactSession session, DataConfig dataConfig)
    {
        String attName = DAO_FACTORY_ATT_NAME+"-"+dataConfig.getName();

        // when an application starts (with IntactConfigurator) the request is not yet available
        // the we store the daoFactory in application scope
        if (!session.isRequestAvailable())
        {
            log.debug("Getting DaoFactory from application, because request is not available");
            if (session.getApplicationAttribute(attName) != null)
            {
                return (DaoFactory) session.getApplicationAttribute(attName);
            }

            DaoFactory daoFactory = new DaoFactory(dataConfig, session);
            session.setApplicationAttribute(attName, daoFactory);

            return daoFactory; 
        }

        if (session.getRequestAttribute(attName) != null)
        {
            return (DaoFactory) session.getRequestAttribute(attName);
        }

        DaoFactory daoFactory = new DaoFactory(dataConfig, session);
        session.setRequestAttribute(attName, daoFactory);

        return daoFactory;
    }

    public static DaoFactory getCurrentInstance(IntactSession session, DataConfig dataConfig, boolean forceCreationOfFactory)
    {
        if (forceCreationOfFactory)
        {
            return new DaoFactory(dataConfig, session);
        }

        String attName = DAO_FACTORY_ATT_NAME+"-"+dataConfig.getName();

        if (session.getRequestAttribute(attName) != null)
        {
            return (DaoFactory) session.getRequestAttribute(attName);
        }

        DaoFactory daoFactory = new DaoFactory(dataConfig, session);
        session.setRequestAttribute(attName, daoFactory);

        return daoFactory;
    }

    public AliasDao getAliasDao()
    {
        return new AliasDaoImpl(getCurrentSession(), intactSession);
    }

    public AnnotatedObjectDao<AnnotatedObject> getAnnotatedObjectDao()
    {
        return new AnnotatedObjectDaoImpl<AnnotatedObject>(AnnotatedObject.class, getCurrentSession(), intactSession);
    }

    public <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao(Class<T> entityType)
    {
        HibernateBaseDaoImpl.validateEntity(entityType);

        return new AnnotatedObjectDaoImpl<T>(entityType, getCurrentSession(), intactSession);
    }

    public AnnotationDao getAnnotationDao()
    {
        return new AnnotationDaoImpl(getCurrentSession(), intactSession);
    }

    public BaseDao getBaseDao()
    {
        return new ExperimentDaoImpl(getCurrentSession(), intactSession);
    }

    public BioSourceDao getBioSourceDao()
    {
        return new BioSourceDaoImpl(getCurrentSession(), intactSession);
    }

    public ComponentDao getComponentDao()
    {
        return new ComponentDaoImpl(getCurrentSession(), intactSession);
    }

    public CvObjectDao<CvObject> getCvObjectDao()
    {
        return new CvObjectDaoImpl<CvObject>(CvObject.class, getCurrentSession(), intactSession);
    }

    public <T extends CvObject> CvObjectDao<T> getCvObjectDao(Class<T> entityType)
    {
        return new CvObjectDaoImpl<T>(entityType, getCurrentSession(), intactSession);
    }

    public DbInfoDao getDbInfoDao()
    {
        return new DbInfoDaoImpl(getCurrentSession(), intactSession);
    }

    public ExperimentDao getExperimentDao()
    {
        return new ExperimentDaoImpl(getCurrentSession(), intactSession);
    }

    public FeatureDao getFeatureDao()
    {
        return new FeatureDaoImpl(getCurrentSession(), intactSession);
    }

    public InstitutionDao getInstitutionDao()
    {
        return new InstitutionDaoImpl(getCurrentSession(), intactSession);
    }

    public IntactObjectDao<IntactObject> getIntactObjectDao()
    {
        return new IntactObjectDaoImpl<IntactObject>(IntactObject.class, getCurrentSession(), intactSession);
    }

    public <T extends IntactObject> IntactObjectDao<T> getIntactObjectDao(Class<T> entityType)
    {
        HibernateBaseDaoImpl.validateEntity(entityType);

        return new IntactObjectDaoImpl<T>(entityType, getCurrentSession(), intactSession);
    }

    public InteractionDao getInteractionDao()
    {
        return new InteractionDaoImpl(getCurrentSession(), intactSession);
    }

    public InteractorDao<InteractorImpl> getInteractorDao()
    {
        return new InteractorDaoImpl<InteractorImpl>(InteractorImpl.class, getCurrentSession(), intactSession);
    }

    public ProteinDao getProteinDao()
    {
        return new ProteinDaoImpl(getCurrentSession(), intactSession);
    }

    public PublicationDao getPublicationDao()
    {
        return new PublicationDaoImpl(getCurrentSession(), intactSession);
    }

    public RangeDao getRangeDao()
    {
        return new RangeDaoImpl(getCurrentSession(), intactSession);
    }

    public SearchItemDao getSearchItemDao()
    {
        return new SearchItemDaoImpl(getCurrentSession(), intactSession);
    }

    public XrefDao<Xref> getXrefDao()
    {
        return new XrefDaoImpl<Xref>(Xref.class, getCurrentSession(), intactSession);
    }

    public <T extends Xref> XrefDao<T> getXrefDao(Class<T> xrefClass)
    {
        return new XrefDaoImpl<T>(xrefClass, getCurrentSession(), intactSession);
    }

    public Connection connection()
    {
        return getCurrentSession().connection();
    }

    public IntactTransaction beginTransaction()
    {
        Transaction transaction = getCurrentSession().beginTransaction();

        currentTransaction = new IntactTransaction(transaction);

        // wrap it
        return currentTransaction;
    }

    public Session getCurrentSession()
    {
        return dataConfig.getSessionFactory().getCurrentSession();
    }

    public boolean isTransactionActive()
    {
        return (currentTransaction != null && !currentTransaction.wasCommitted());
    }

    public IntactTransaction getCurrentTransaction()
    {
        return currentTransaction;
    }
}
