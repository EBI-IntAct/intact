/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.core.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Connection;

/**
 * Factory for all the intact DAOs using Hibernate
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
@org.springframework.stereotype.Component
public class DaoFactory implements Serializable {

    private static final Log log = LogFactory.getLog( DaoFactory.class );

    @PersistenceContext
    private EntityManager currentEntityManager;

    @Autowired AliasDao aliasDao;
    @Autowired AnnotationDao annotationDao;
    @Autowired BioSourceDao bioSourceDao;
    @Autowired ComponentDao componentDao;
    @Autowired ComponentParameterDao componentParameterDao;
    @Autowired ConfidenceDao confidenceDao;
    @Autowired CvObjectDao cvObjectDao;
    @Autowired DbInfoDao dbInfoDao;
    @Autowired ExperimentDao experimentDao;
    @Autowired FeatureDao featureDao;
    @Autowired ImexImportDao imexImportDao;
    @Autowired ImexImportPublicationDao imexImportPublicationDao;
    @Autowired InstitutionDao institutionDao;
    @Autowired InteractionDao interactionDao;
    @Autowired InteractionParameterDao interactionParameterDao;
    @Autowired @Qualifier("interactorDaoImpl") InteractorDao interactorDao;
    @Autowired MineInteractionDao mineInteractionDao;
    @Autowired @Qualifier("polymerDaoImpl") PolymerDao polymerDao;
    @Autowired ProteinDao proteinDao;
    @Autowired PublicationDao publicationDao;
    @Autowired RangeDao rangeDao;
    @Autowired SearchableDao searchableDao;
    @Autowired XrefDao xrefDao;

    public DaoFactory() {

    }

    public static DaoFactory getCurrentInstance( IntactContext context ) {
        return context.getDataContext().getDaoFactory();
    }

    public AliasDao<Alias> getAliasDao() {
        return getAliasDao(Alias.class);
    }

    public <T extends Alias> AliasDao<T> getAliasDao( Class<T> aliasType ) {
        aliasDao.setEntityClass(aliasType);
        return aliasDao;
    }

    public <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao( Class<T> entityType ) {
        if (entityType.isAssignableFrom(Institution.class)) {
            return (AnnotatedObjectDao<T>)institutionDao;
        } else if (Publication.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)publicationDao;
        } else if (CvObject.class.isAssignableFrom(entityType)) {
            return getCvObjectDao((Class)entityType);
        } else if (Experiment.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)experimentDao;
        } else if (Interaction.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)interactionDao;
        } else if (Interactor.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)interactorDao;
        } else if (BioSource.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)bioSourceDao;
        } else if (Component.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)componentDao;
        } else if (Feature.class.isAssignableFrom(entityType)) {
            return (AnnotatedObjectDao<T>)featureDao;
        } else {
            throw new IllegalArgumentException( "No Dao for entity type: "+entityType.getClass().getName());
        }
    }

    public AnnotationDao getAnnotationDao() {
        return annotationDao;
    }

    public BaseDao getBaseDao() {
        // It is returning an ExperimentDaoImpl because HibernateBaseDaoImpl is an abstract class, and ExperimentDaoImpl
        // implement all HibernateBaseDaoImpl anyway.
        return experimentDao;
    }

    public BioSourceDao getBioSourceDao() {
        return bioSourceDao;
    }

    public ComponentDao getComponentDao() {
        return componentDao;
    }

    public CvObjectDao<CvObject> getCvObjectDao() {
        return getCvObjectDao(CvObject.class);
    }

    public <T extends CvObject> CvObjectDao<T> getCvObjectDao( Class<T> entityType ) {
        cvObjectDao.setEntityClass(entityType);
        return cvObjectDao;
    }

    public DbInfoDao getDbInfoDao() {
        return dbInfoDao;
    }

    public ExperimentDao getExperimentDao() {
        return experimentDao;
    }

    public FeatureDao getFeatureDao() {
        return featureDao;
    }

    /**
     * @since 1.7.2
     */
    public ImexImportDao getImexImportDao() {
        return imexImportDao;
    }

    /**
     * @since 1.7.2
     */
    public ImexImportPublicationDao getImexImportPublicationDao() {
        return imexImportPublicationDao;
    }

    public InstitutionDao getInstitutionDao() {
        return institutionDao;
    }

    public IntactObjectDao<? extends IntactObject> getIntactObjectDao() {
        return experimentDao;
    }

    public InteractionDao getInteractionDao() {
        return interactionDao;
    }

    public <T extends InteractorImpl> InteractorDao<T> getInteractorDao( Class<T> entityType ) {
        interactorDao.setEntityClass(entityType);
        return interactorDao;
    }

    public InteractorDao<InteractorImpl> getInteractorDao() {
        return getInteractorDao((Class) InteractorImpl.class);
    }

    /**
     * @since 1.5
     */
    public MineInteractionDao getMineInteractionDao() {
        return mineInteractionDao;
    }

    public PolymerDao<PolymerImpl> getPolymerDao() {
        return getPolymerDao(PolymerImpl.class);
    }

    public <T extends PolymerImpl> PolymerDao<T> getPolymerDao( Class<T> clazz ) {
        polymerDao.setEntityClass(clazz);
        return polymerDao;
    }

    public ProteinDao getProteinDao() {
        return proteinDao;
    }

    public PublicationDao getPublicationDao() {
        return publicationDao;
    }

    public RangeDao getRangeDao() {
        return rangeDao;
    }

    public ConfidenceDao getConfidenceDao(){
        return confidenceDao;
    }
    
    public InteractionParameterDao getInteractionParameterDao(){
        return interactionParameterDao;
    }
    
    public ComponentParameterDao getComponentParameterDao(){
        return componentParameterDao;
    }

    public SearchableDao getSearchableDao() {
        return searchableDao;
    }

    public XrefDao<Xref> getXrefDao() {
        return getXrefDao(Xref.class);
    }

    public <T extends Xref> XrefDao<T> getXrefDao( Class<T> xrefClass ) {
        // TODO check if this is really safe
        XrefDao dao = (XrefDao) IntactContext.getCurrentInstance().getSpringContext()
                .getBean("xrefDaoImpl");
        dao.setEntityClass(xrefClass);
        return dao;
    }

    @Deprecated
    public Connection connection() {
        return getCurrentSession().connection();
    }

    public EntityTransaction beginTransaction() {
        /*
        log.debug("Starting transaction...");
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return transaction;
        */
        return null;
    }

    public void commitTransaction() {
        getEntityManager().flush();
//        if (currentEntityManager.getTransaction().isActive()) {
//            if (log.isDebugEnabled()) log.debug("Committing transaction");
//
//            //if (getEntityManager().getFlushMode() == FlushModeType.COMMIT) {
//                getEntityManager().flush();
//            //}
//
//            currentEntityManager.getTransaction().commit();
//            currentEntityManager.close();
//
//        } else {
//            if (log.isWarnEnabled()) log.warn("Attempted commit on a transaction that was not active");
//        }

//        JpaTransactionManager transactionManager = (JpaTransactionManager) getApplicationContext().getBean("transactionManager");
//        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_MANDATORY));
//        System.out.println("Is new: "+status.isNewTransaction());
//        transactionManager.commit(status);
//        System.out.println("Completed: "+status.isCompleted());
    }

    public EntityManager getEntityManager() {
//        if (currentEntityManager == null || !currentEntityManager.isOpen()) {
//            if (log.isDebugEnabled()) log.debug("Creating new EntityManager");
//
//            if (runtimeConfig.getDataConfig() == null) {
//                throw new IllegalStateException("No DataConfig found");
//            }
//
//            EntityManagerFactory entityManagerFactory = runtimeConfig.getDataConfig().getEntityManagerFactory();
//
//            if (entityManagerFactory == null) {
//                throw new IllegalStateException("Null EntityManagerFactory for DataConfig with name: " + runtimeConfig.getDataConfig().getName());
//            }
//
//            currentEntityManager = entityManagerFactory.createEntityManager();
//
//        }
//
//        if (!runtimeConfig.getDataConfig().isAutoFlush()) {
//            if (log.isDebugEnabled())
//                log.debug("Data-config is not autoflush. Using flush mode: " + FlushModeType.COMMIT);
//            currentEntityManager.setFlushMode(FlushModeType.COMMIT);
//        }

        return currentEntityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        currentEntityManager = entityManager;
    }

    @Deprecated
    public synchronized Session getCurrentSession() {
        return (Session) getEntityManager().getDelegate();
    }

    public boolean isTransactionActive() {
//        if (currentEntityManager == null) {
//            if (log.isWarnEnabled()) log.warn("Cannot check if the transaction is active as the current EntityManager is null");
//            return false;
//        }
//
//        EntityTransaction currentTransaction = currentEntityManager.getTransaction();
//        boolean active = currentTransaction.isActive();
//        if( log.isDebugEnabled() ) {
//            log.debug( "Current transaction is " + ( active ? "active" : "committed" ) );
//        }
//        return active;
        return false;
    }

    public EntityTransaction getCurrentTransaction() {
        return currentEntityManager.getTransaction();
    }
}
