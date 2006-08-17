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
import uk.ac.ebi.intact.context.DataContext;
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
    private DataContext dataContext;

    private IntactTransaction currentTransaction;

    private static DaoFactory instance;

    private DaoFactory(DataConfig dataConfig)
    {
        this.dataConfig = (AbstractHibernateDataConfig) dataConfig;
    }

    public static DaoFactory getCurrentInstance(IntactContext context)
    {
        return getCurrentInstance(context.getConfig().getDefaultDataConfig());
    }

    public static DaoFactory getCurrentInstance(IntactContext context, String dataConfigName)
    {
        return getCurrentInstance(context.getSession(), context.getConfig().getDataConfig(dataConfigName));
    }

    public static DaoFactory getCurrentInstance(DataConfig dataConfig)
    {
        DaoFactory daoFactory = new DaoFactory(dataConfig);
        return daoFactory;
    }

    public static DaoFactory getCurrentInstance(IntactSession session, DataConfig dataConfig)
    {
        String attName = DAO_FACTORY_ATT_NAME+"-"+dataConfig.getName();

        if (session.getRequestAttribute(attName) != null)
        {
            return (DaoFactory) session.getRequestAttribute(attName);
        }

        DaoFactory daoFactory = new DaoFactory(dataConfig);
        session.setRequestAttribute(attName, daoFactory);

        return daoFactory;
    }

    public AliasDao getAliasDao()
    {
        return new AliasDaoImpl(getCurrentSession());
    }

    public AnnotatedObjectDao<AnnotatedObject> getAnnotatedObjectDao()
    {
        return new AnnotatedObjectDaoImpl<AnnotatedObject>(AnnotatedObject.class, getCurrentSession());
    }

    public <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao(Class<T> entityType)
    {
        HibernateBaseDaoImpl.validateEntity(entityType);

        return new AnnotatedObjectDaoImpl<T>(entityType, getCurrentSession());
    }

    public AnnotationDao getAnnotationDao()
    {
        return new AnnotationDaoImpl(getCurrentSession());
    }

    public BaseDao getBaseDao()
    {
        return new ExperimentDaoImpl(getCurrentSession());
    }

    public BioSourceDao getBioSourceDao()
    {
        return new BioSourceDaoImpl(getCurrentSession());
    }

    public ComponentDao getComponentDao()
    {
        return new ComponentDaoImpl(getCurrentSession());
    }

    public CvObjectDao<CvObject> getCvObjectDao()
    {
        return new CvObjectDaoImpl<CvObject>(CvObject.class, getCurrentSession());
    }

    public <T extends CvObject> CvObjectDao<T> getCvObjectDao(Class<T> entityType)
    {
        return new CvObjectDaoImpl<T>(entityType, getCurrentSession());
    }

    public ExperimentDao getExperimentDao()
    {
        return new ExperimentDaoImpl(getCurrentSession());
    }

    public FeatureDao getFeatureDao()
    {
        return new FeatureDaoImpl(getCurrentSession());
    }

    public InstitutionDao getInstitutionDao()
    {
        return new InstitutionDaoImpl(getCurrentSession());
    }

    public IntactObjectDao<IntactObject> getIntactObjectDao()
    {
        return new IntactObjectDaoImpl<IntactObject>(IntactObject.class, getCurrentSession());
    }

    public <T extends IntactObject> IntactObjectDao<T> getIntactObjectDao(Class<T> entityType)
    {
        HibernateBaseDaoImpl.validateEntity(entityType);

        return new IntactObjectDaoImpl<T>(entityType, getCurrentSession());
    }

    public InteractionDao getInteractionDao()
    {
        return new InteractionDaoImpl(getCurrentSession());
    }

    public InteractorDao<InteractorImpl> getInteractorDao()
    {
        return new InteractorDaoImpl<InteractorImpl>(InteractorImpl.class, getCurrentSession());
    }

    public ProteinDao getProteinDao()
    {
        return new ProteinDaoImpl(getCurrentSession());
    }

    public PublicationDao getPublicationDao()
    {
        return new PublicationDaoImpl(getCurrentSession());
    }

    public RangeDao getRangeDao()
    {
        return new RangeDaoImpl(getCurrentSession());
    }

    public SearchItemDao getSearchItemDao()
    {
        return new SearchItemDaoImpl(getCurrentSession());
    }

    public XrefDao<Xref> getXrefDao()
    {
        return new XrefDaoImpl<Xref>(Xref.class, getCurrentSession());
    }

    public <T extends Xref> XrefDao<T> getXrefDao(Class<T> xrefClass)
    {
        return new XrefDaoImpl<T>(xrefClass, getCurrentSession());
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
        //checkStatus();

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
