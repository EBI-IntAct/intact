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
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.AbstractHibernateDataConfig;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.RuntimeConfig;
import uk.ac.ebi.intact.model.*;
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

    @Autowired DbInfoDao dbInfoDao;
    @Autowired ExperimentDao experimentDao;
    @Autowired InstitutionDao institutionDao;
    @Autowired InteractionDao interactionDao;
    @Autowired ProteinDao proteinDao;
    @Autowired PublicationDao publicationDao;
    @Autowired SearchableDao searchableDao;

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
        return new AliasDaoImpl( Alias.class, getEntityManager(), intactSession );
    }

    public <T extends Alias> AliasDao<T> getAliasDao( Class<T> aliasType ) {
        return new AliasDaoImpl<T>( aliasType, getEntityManager(), intactSession );
    }

    public AnnotatedObjectDao<AnnotatedObject> getAnnotatedObjectDao() {
        return new AnnotatedObjectDaoImpl<AnnotatedObject>( AnnotatedObject.class, getEntityManager(), intactSession );
    }

    public <T extends AnnotatedObject> AnnotatedObjectDao<T> getAnnotatedObjectDao( Class<T> entityType ) {
        entityType = CgLibUtil.removeCglibEnhanced(entityType);
        HibernateBaseDaoImpl.validateEntity( entityType );

        return new AnnotatedObjectDaoImpl<T>( entityType, getEntityManager(), intactSession );
    }

    public AnnotationDao getAnnotationDao() {
        return new AnnotationDaoImpl( getEntityManager(), intactSession );
    }

    public BaseDao getBaseDao() {
        // It is returning an ExperimentDaoImpl because HibernateBaseDaoImpl is an abstract class, and ExperimentDaoImpl
        // implement all HibernateBaseDaoImpl anyway.
        return experimentDao;
    }

    public BioSourceDao getBioSourceDao() {
        return new BioSourceDaoImpl( getEntityManager(), intactSession );
    }

    public ComponentDao getComponentDao() {
        return new ComponentDaoImpl( getEntityManager(), intactSession );
    }

    public CvObjectDao<CvObject> getCvObjectDao() {
        return new CvObjectDaoImpl<CvObject>( CvObject.class, getEntityManager(), intactSession );
    }

    public <T extends CvObject> CvObjectDao<T> getCvObjectDao( Class<T> entityType ) {
        return new CvObjectDaoImpl<T>( entityType, getEntityManager(), intactSession );
    }

    public DbInfoDao getDbInfoDao() {
        return dbInfoDao;
    }

    public ExperimentDao getExperimentDao() {
        return experimentDao;
    }

    public FeatureDao getFeatureDao() {
        return new FeatureDaoImpl( getEntityManager(), intactSession );
    }

    /**
     * @since 1.7.2
     */
    public ImexImportDao getImexImportDao() {
        return new ImexImportDaoImpl(getEntityManager(), intactSession);
    }

    /**
     * @since 1.7.2
     */
    public ImexImportPublicationDao getImexImportPublicationDao() {
        return new ImexImportPublicationDaoImpl(getEntityManager(), intactSession);
    }

    public InstitutionDao getInstitutionDao() {
        return institutionDao;
    }

    public IntactObjectDao<IntactObject> getIntactObjectDao() {
        return new IntactObjectDaoImpl<IntactObject>( IntactObject.class, getEntityManager(), intactSession );
    }

    public <T extends IntactObject> IntactObjectDao<T> getIntactObjectDao( Class<T> entityType ) {
        HibernateBaseDaoImpl.validateEntity( entityType );

        return new IntactObjectDaoImpl<T>( entityType, getEntityManager(), intactSession );
    }

    public InteractionDao getInteractionDao() {
        return interactionDao;
    }

    public <T extends InteractorImpl> InteractorDao<T> getInteractorDao( Class<T> entityType ) {
        return new InteractorDaoImpl<T>( entityType, getEntityManager(), intactSession );
    }

    public InteractorDao<InteractorImpl> getInteractorDao() {
        return new InteractorDaoImpl<InteractorImpl>( InteractorImpl.class, getEntityManager(), intactSession );
    }

    /**
     * @since 1.5
     */
    public MineInteractionDao getMineInteractionDao() {
        return new MineInteractionDaoImpl( getEntityManager(), intactSession );
    }

    public PolymerDao<PolymerImpl> getPolymerDao() {
        return new PolymerDaoImpl<PolymerImpl>( PolymerImpl.class, getEntityManager(), intactSession );
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
        return new RangeDaoImpl( getEntityManager(), intactSession );
    }

    public ConfidenceDao getConfidenceDao(){
        return new ConfidenceDaoImpl(getEntityManager(), intactSession);
    }
    
    public InteractionParameterDao getInteractionParameterDao(){
        return new InteractionParameterDaoImpl(getEntityManager(), intactSession);
    }
    
    public ComponentParameterDao getComponentParameterDao(){
        return new ComponentParameterDaoImpl(getEntityManager(), intactSession);
    }

    public SearchableDao getSearchableDao() {
        return searchableDao;
    }

    public SearchItemDao getSearchItemDao() {
        return new SearchItemDaoImpl( getEntityManager(), intactSession );
    }

    public XrefDao<Xref> getXrefDao() {
        return new XrefDaoImpl<Xref>( Xref.class, getEntityManager(), intactSession );
    }

    public <T extends Xref> XrefDao<T> getXrefDao( Class<T> xrefClass ) {
        return new XrefDaoImpl<T>( xrefClass, getEntityManager(), intactSession );
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
