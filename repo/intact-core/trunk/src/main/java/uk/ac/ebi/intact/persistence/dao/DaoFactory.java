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
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.impl.*;
import uk.ac.ebi.intact.persistence.util.HibernateUtil;

import java.sql.Connection;

/**
 * Factory for all the intact DAOs using Hibernate
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class DaoFactory
{
    private static final Log log = LogFactory.getLog(DaoFactory.class);

    private DaoFactory(){}

    public static AliasDao getAliasDao()
    {
        return new AliasDaoImpl(getCurrentSession());
    }

    public static AnnotatedObjectDao<AnnotatedObject> getAnnotatedObjectDao()
    {
       return new AnnotatedObjectDaoImpl<AnnotatedObject>(AnnotatedObject.class, getCurrentSession());
    }

    public static <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao(Class<T> entityType)
    {
        HibernateBaseDaoImpl.validateEntity(entityType);

        return new AnnotatedObjectDaoImpl<T>(entityType, getCurrentSession());
    }

    public static AnnotationDao getAnnotationDao()
    {
        return new AnnotationDaoImpl(getCurrentSession());
    }

    public static BaseDao getBaseDao()
    {
        return new ExperimentDaoImpl(getCurrentSession());
    }

    public static BioSourceDao getBioSourceDao()
    {
        return new BioSourceDaoImpl(getCurrentSession());
    }

    public static ComponentDao getComponentDao()
    {
        return new ComponentDaoImpl(getCurrentSession());
    }

    public static CvObjectDao<CvObject> getCvObjectDao()
    {
        return new CvObjectDaoImpl<CvObject>(CvObject.class, getCurrentSession());
    }

    public static <T extends CvObject> CvObjectDao<T> getCvObjectDao(Class<T> entityType)
    {
        return new CvObjectDaoImpl<T>(entityType, getCurrentSession());
    }

    public static ExperimentDao getExperimentDao()
    {
        return new ExperimentDaoImpl(getCurrentSession());
    }

    public static FeatureDao getFeatureDao()
    {
        return new FeatureDaoImpl(getCurrentSession());
    }

    public static InstitutionDao getInstitutionDao()
    {
        return new InstitutionDaoImpl(getCurrentSession());
    }

    public static IntactObjectDao<IntactObject> getIntactObjectDao()
    {
        return new IntactObjectDaoImpl<IntactObject>(IntactObject.class, getCurrentSession());
    }

    public static <T extends IntactObject> IntactObjectDao<T> getIntactObjectDao(Class<T> entityType)
    {
        HibernateBaseDaoImpl.validateEntity(entityType);

        return new IntactObjectDaoImpl<T>(entityType, getCurrentSession());
    }

    public static InteractionDao getInteractionDao()
    {
        return new InteractionDaoImpl(getCurrentSession());
    }

    public static InteractorDao<InteractorImpl> getInteractorDao()
    {
        return new InteractorDaoImpl<InteractorImpl>(InteractorImpl.class, getCurrentSession());
    }

    public static ProteinDao getProteinDao()
    {
        return new ProteinDaoImpl(getCurrentSession());
    }

    public static RangeDao getRangeDao()
    {
        return new RangeDaoImpl(getCurrentSession());
    }

    public static SearchItemDao getSearchItemDao()
    {
        return new SearchItemDaoImpl(getCurrentSession());
    }

    public static XrefDao<Xref> getXrefDao()
    {
        return new XrefDaoImpl<Xref>(Xref.class, getCurrentSession());
    }

    public static  <T extends Xref> XrefDao<T> getXrefDao(Class<T> xrefClass)
    {
        return new XrefDaoImpl<T>(xrefClass, getCurrentSession());
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
        //checkStatus();

        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

}
