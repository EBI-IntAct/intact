package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ejb.Ejb3Configuration;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.SchemaVersion;
import uk.ac.ebi.intact.context.IntactSession;

import javax.persistence.EntityManagerFactory;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id: AbstractJpaDataConfig.java 10058 2007-10-18 21:04:26Z baranda $
 */
public abstract class AbstractJpaDataConfig extends DataConfig<EntityManagerFactory, Ejb3Configuration>{

    private static final Log log = LogFactory.getLog(AbstractJpaDataConfig.class);

    private EntityManagerFactory entityManagerFactory;

    public AbstractJpaDataConfig(IntactSession session) {
        super(session);
    }

    public String getName() {
        return AbstractJpaDataConfig.class.getName();
    }

    public void initialize() {
        if (entityManagerFactory == null) {
            entityManagerFactory = getEntityManagerFactory();
        }
    }

    public boolean isInitialized() {
        return true;
    }

    @Deprecated
    public EntityManagerFactory getSessionFactory() {
        return getEntityManagerFactory();
    }

    public void closeSessionFactory() {
        entityManagerFactory.close();
    }

    public abstract Ejb3Configuration getConfiguration();

    public void flushSession() {

    }

    public boolean isConfigurable() {
        return false;
    }

    public SchemaVersion getMinimumRequiredVersion() {
        return new SchemaVersion(DEFAULT_REQUIRED_VERSION_MAJOR, 
                DEFAULT_REQUIRED_VERSION_MINOR,
                DEFAULT_REQUIERD_VERSION_BUILD);
    }
}
