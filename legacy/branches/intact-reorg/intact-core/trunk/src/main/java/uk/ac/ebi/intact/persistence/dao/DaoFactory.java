/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.hibernate.Session;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvObject;
import uk.ac.ebi.intact.model.IntactObject;
import uk.ac.ebi.intact.model.InteractorImpl;
import uk.ac.ebi.intact.persistence.dao.impl.AliasDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.AnnotatedObjectDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.AnnotationDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.BioSourceDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.ComponentDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.CvObjectDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.ExperimentDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.FeatureDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.HibernateBaseDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.InstitutionDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.IntactObjectDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.InteractionDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.InteractorDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.ProteinDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.RangeDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.SearchItemDaoImpl;
import uk.ac.ebi.intact.persistence.dao.impl.XrefDaoImpl;
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
    private DaoFactory(){}

    public static AliasDao getAliasDao()
    {
        return new AliasDaoImpl(getCurrentSession());
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

    public static XrefDao getXrefDao()
    {
        return new XrefDaoImpl(getCurrentSession());
    }

    private static Session getCurrentSession()
    {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    public static Connection connection()
    {
        return getCurrentSession().connection();
    }

}
