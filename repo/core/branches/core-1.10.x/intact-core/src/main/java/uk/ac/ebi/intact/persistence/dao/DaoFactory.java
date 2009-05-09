/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.persistence.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.RuntimeConfig;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.persistence.dao.impl.*;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;

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

    private static final String DAO_FACTORY_ATT_NAME = DaoFactory.class.getName();

    @PersistenceContext
    private EntityManager currentEntityManager;
    
    @Autowired
    private RuntimeConfig runtimeConfig;

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

    private IntactSession intactSession;

    public DaoFactory() {

    }

    protected DaoFactory( DataConfig dataConfig, IntactSession intactSession ) {
        //this.dataConfig = dataConfig;
        this.intactSession = intactSession;
    }

    public static DaoFactory getCurrentInstance( IntactContext context ) {
        return getCurrentInstance( context.getSession(), context.getConfig().getDefaultDataConfig() );
    }

    public static DaoFactory getCurrentInstance( IntactContext context, String dataConfigName ) {
        return getCurrentInstance( context.getSession(), context.getConfig().getDataConfig( dataConfigName ) );
    }

    public static DaoFactory getCurrentInstance( IntactSession session, DataConfig dataConfig ) {
        String attName = DAO_FACTORY_ATT_NAME + "-" + dataConfig.getName();

        // when an application starts (with IntactConfigurator) the request is not yet available
        // the we store the daoFactory in application scope
        if ( !session.isRequestAvailable() ) {
            log.debug( "Getting DaoFactory from application, because request is not available at this point" +
                       " (probably the application is initializing)" );
            if ( session.getApplicationAttribute( attName ) != null ) {
                return ( DaoFactory ) session.getApplicationAttribute( attName );
            }

            DaoFactory daoFactory = new DaoFactory( dataConfig, session );
            session.setApplicationAttribute( attName, daoFactory );

            return daoFactory;
        }

        if ( session.getRequestAttribute( attName ) != null ) {
            return ( DaoFactory ) session.getRequestAttribute( attName );
        }

        DaoFactory daoFactory = new DaoFactory( dataConfig, session );
        session.setRequestAttribute( attName, daoFactory );

        return daoFactory;
    }

    public static DaoFactory getCurrentInstance( IntactSession session, DataConfig dataConfig, boolean forceCreationOfFactory ) {
        if ( forceCreationOfFactory ) {
            return new DaoFactory( dataConfig, session );
        }

        String attName = DAO_FACTORY_ATT_NAME + "-" + dataConfig.getName();

        if ( session.getRequestAttribute( attName ) != null ) {
            return ( DaoFactory ) session.getRequestAttribute( attName );
        }

        DaoFactory daoFactory = new DaoFactory( dataConfig, session );
        session.setRequestAttribute( attName, daoFactory );

        return daoFactory;
    }

    public AliasDao<Alias> getAliasDao() {
        return aliasDao;
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
            return cvObjectDao;
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
        return cvObjectDao;
    }

    public <T extends CvObject> CvObjectDao<T> getCvObjectDao( Class<T> entityType ) {
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
        return interactorDao;
    }

    public InteractorDao<InteractorImpl> getInteractorDao() {
        return interactorDao;
    }

    /**
     * @since 1.5
     */
    public MineInteractionDao getMineInteractionDao() {
        return mineInteractionDao;
    }

    public PolymerDao<PolymerImpl> getPolymerDao() {
        return polymerDao;
    }

    public <T extends PolymerImpl> PolymerDao<T> getPolymerDao( Class<T> clazz ) {
        return new PolymerDaoImpl<T>( clazz, getEntityManager(), intactSession );
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
        return xrefDao;
    }

    public <T extends Xref> XrefDao<T> getXrefDao( Class<T> xrefClass ) {
        // TODO check if this is really safe
        xrefDao.setEntityClass(xrefClass);
        return xrefDao;
    }

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
        Session session = (Session) getEntityManager().getDelegate();

        //Session session = getSessionFromSessionFactory(dataConfig);

        if (!runtimeConfig.getDataConfig().isAutoFlush()) {
            session.setFlushMode(FlushMode.MANUAL);
        }

        if ( !session.isOpen() ) {
            // this only should happen for hibernate data configs
            if ( log.isDebugEnabled() ) {
                log.debug( "Opening new session because the current is closed" );
            }
            session = ((AbstractHibernateDataConfig)getDataConfig()).getSessionFactory().openSession();
        }

        return session;
    }


    protected Session getSessionFromSessionFactory(DataConfig dataConfig) {
        return ((HibernateEntityManager)runtimeConfig.getDataConfig().getEntityManagerFactory().createEntityManager()).getSession();
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

    public DataConfig getDataConfig() {
        return runtimeConfig.getDataConfig();
    }
}
