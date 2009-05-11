package uk.ac.ebi.intact.core.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.intact.core.IntactException;
import uk.ac.ebi.intact.core.config.IntactConfiguration;
import uk.ac.ebi.intact.core.config.ConfigurationException;
import uk.ac.ebi.intact.core.context.impl.StandaloneSession;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.core.persister.PersisterHelper;

import javax.persistence.EntityManagerFactory;
import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.io.File;
import java.io.Serializable;

/**
 * The {@code IntactContext} class is the central point of access to the IntAct Core API.  *
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
public class IntactContext implements Serializable, Closeable {

    private static final Log log = LogFactory.getLog( IntactContext.class );

    private static IntactContext instance;

    /**
     * Contains scoped data and variables
     */
    private transient IntactSession session;

    @Autowired
    private DataContext dataContext;

    @Autowired
    private PersisterHelper persisterHelper;

    @Autowired
    private IntactConfiguration config;

    @Autowired
    private UserContext userContext;

    @Autowired
    private ApplicationContext springContext;

    public IntactContext() {

    }

    @PostConstruct
    public void init() {

        //configurator.initIntact( new StandaloneSession() );
        instance = this;
    }

    /**
     * Gets the current (ThreadLocal) instance of {@code IntactContext}. If no such instance exist,
     * IntAct Core will be automatically initialized using JPA configurations in the classpath, configured
     * DataConfigs and, if these are not found, using a temporary database.
     * @return the IntactContext instance
     */
    public static IntactContext getCurrentInstance() {
        if ( !currentInstanceExists() ) {
            // stack trace element to know from where this method was called
            StackTraceElement ste = Thread.currentThread().getStackTrace()[3];

            log.debug( "Current instance of IntactContext is null. Initializing a context in memory.\nCalled at:\n\t" +
                      ste.toString() );

            initStandaloneContextInMemory();
        }

        return instance;
    }

    /**
     * Checks if an instance already exists.
     * @return True if an instance of IntactContext exist.
     */
    public static boolean currentInstanceExists() {
        return instance != null;
    }

    /**
     * Initializes a standalone {@code IntactContext} (not to use in web applications, where the initialization
     * might be controlled by other means). It will try to find a working configuration or start a temporary database otherwise.
     */
    public static void initStandaloneContext() {
        initContext( (String)null, new StandaloneSession() );
    }

    /**
     * Initializes a standalone {@code IntactContext} using the Hibernate configuration file provided
     * (not to use in web applications, where the initialization might be controlled by other means).
     * It will try to find a working configuration or start a temporary database otherwise.
     * @param hibernateFile The hibernate configuration file
     */
    @Deprecated
    public static void initStandaloneContext(File hibernateFile) {
        throw new UnsupportedOperationException();
    }

    /**
     * Initializes a standalone {@code IntactContext} using a temporary database. This is probably only useful
     * for testing.
     */
    public static void initStandaloneContextInMemory() {
        initContext();
    }

    /**
     * Initializes a standalone {@code IntactContext} using a persistence unit name and an {@code IntactSession} instance.
     * @param persistenceUnitName The name of the persistence unit. This is used to create a {@code DataConfig} of type {@code JpaCoreDataConfig}
     * @param session The IntactSession object. By default, this will be an instance of {@code StandaloneSession} for
     * standalone applications or a {@code WebappSession} for web applications. This value cannot be null.
     */
    @Deprecated
    public static void initContext( String persistenceUnitName, IntactSession session ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Initializes a standalone {@code IntactContext} using an {@code EntityManagerFactory} and an {@code IntactSession} instance.
     * @param emf An EntityManagerFactory configured to access the IntAct database. This is used to create a {@code DataConfig} of type {@code JpaEntityManagerFactoryDataConfig}
     * @param session The IntactSession object. By default, this will be an instance of {@code StandaloneSession} for
     * standalone applications or a {@code WebappSession} for web applications. This value cannot be null.
     */
    @Deprecated
    public static void initContext( EntityManagerFactory emf, IntactSession session ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Initializes a standalone {@code IntactContext} using a {@code DataConfig} instance and an {@code IntactSession} instance.
     * standalone applications or a {@code WebappSession} for web applications. This value cannot be null.
     */
    public static void initContext( ) {

        // init Spring
        ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(
                new String[] {"/META-INF/intact-base.spring.xml", "/META-INF/standalone/jpa-standalone.spring.xml", "/META-INF/standalone/intact-standalone.spring.xml"});

        instance = (IntactContext) springContext.getBean("intactContext");
    }


    /**
     * The {@UserContext contains user-specific information, such as the current user name}
     * @return The UserContext instance
     */
    public UserContext getUserContext() {
        if (userContext == null) {
            throw new ConfigurationException("No bean of type "+UserContext.class.getName()+" found. One is expected");
        }
        
        return userContext;
    }

    /**
     * Gets the institution from the RuntimeConfig object. In addition, tries to refresh
     * the instance from the database if it is detached.
     * @return
     * @throws IntactException
     */
    public Institution getInstitution() throws IntactException {
        Institution institution = config.getDefaultInstitution();

//        if (institution.getAc() != null && getDataContext().getDaoFactory().getInstitutionDao().isTransient(institution)) {
//            institution = getDataContext().getDaoFactory().getInstitutionDao().getByAc(institution.getAc());
//        }

        return institution;
    }

    public IntactConfiguration getConfig() {
        return config;
    }


    public IntactSession getSession() {
        return session;
    }

    public DataContext getDataContext() {
        return dataContext;
    }

    /**
     * Closes this instance of {@code IntactContext} and finalizes the data access, by closing the EntityManagerFactories
     * for all the registered DataConfigs. Other fields are set to null, as well as the current instance.     *
     */
    public void close() {
        getSpringContext().close();
        session = null;
        instance = null;
    }

    /**
     * Closes the current IntactContext.
     */
    public static void closeCurrentInstance() {
        if (currentInstanceExists()) {
            instance.close();
        } else {
            if (log.isDebugEnabled()) log.debug("No IntactContext found, so it didn't need to be closed");
        }
    }

    public PersisterHelper getPersisterHelper() {
        return persisterHelper;
    }

    public ConfigurableApplicationContext getSpringContext() {
        return (ConfigurableApplicationContext) springContext;
    }
}
