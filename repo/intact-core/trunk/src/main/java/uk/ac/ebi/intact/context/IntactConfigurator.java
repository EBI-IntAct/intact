/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.SchemaVersion;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.context.impl.IntactContextWrapper;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.meta.DbInfo;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to initialize IntAct, and to initialize IntactContexts
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class IntactConfigurator
{
    private static final Log log = LogFactory.getLog(IntactConfigurator.class);

    private static final String DEFAULT_INSTITUTION_LABEL = "ebi";
    private static final String DEFAULT_INSTITUTION_FULL_NAME = "European Bioinformatics Institute";
    private static final String DEFAULT_INSTITUTION_POSTAL_ADDRESS = "European Bioinformatics Institute; " +
                                                                    "Wellcome Trust Genome Campus; " +
                                                                    "Hinxton, Cambridge; " +
                                                                    "CB10 1SD; " +
                                                                    "United Kingdom";
    private static final String DEFAULT_INSTITUTION_URL = "http://www.ebi.ac.uk/";

    private static final String DEFAULT_AC_PREFIX = "UNK";
    
    private static final String DEFAULT_READ_ONLY_APP = Boolean.TRUE.toString();

    private static final String INSTITUTION_TO_BE_PERSISTED_FLAG = "uk.ac.ebi.intact.internal.INSTITUTION_TO_BE_PERSISTED";
    private static final String SCHEMA_VERSION_TO_BE_PERSISTED_FLAG = "uk.ac.ebi.intact.internal.SCHEMA_VERSION_TO_BE_PERSISTED";

    private static final String INITIALIZED_APP_ATT_NAME = IntactConfigurator.class+"_INITIALIZED";

    private static final String INTACT_CONTEXT_SESS_ATT_NAME = IntactConfigurator.class+"_INTACT_CONTEXT";

    /**
     * Initializes the fundamental intact parameters, such as data configs, the default prefix,  and default data
     * from the database. Note that it only performs only database read-only, as to write to the database
     * is not allowed at this point if using the IntactIdGenerator.
     * @param session the IntactSession to use
     * @throws IntactInitializationError if something unexpected happends during loading or a check fails
     */
    public static void initIntact(IntactSession session) throws IntactInitializationError
    {
        log.info("Initializing intact-core with session of class: "+session.getClass());

        if (isInitialized(session))
        {
            throw new IntactInitializationError("IntAct already initialized");
        }

        // when logging info, print init params
        if (log.isInfoEnabled())
        {
            List<String> initParams = new ArrayList<String>();

            for (String initParam : session.getInitParamNames())
            {
                 initParams.add(initParam+"="+session.getInitParam(initParam));
            }

            log.info("Init Params: "+initParams);
        }

        RuntimeConfig config = RuntimeConfig.getCurrentInstance(session);

        if (config.getDefaultDataConfig() == null)
        {
            // add the core model data config
            log.info("Registering standard data-config");
            StandardCoreDataConfig stdDataConfig = new StandardCoreDataConfig(session);
            stdDataConfig.getSessionFactory();
            config.addDataConfig(stdDataConfig, true);
        }

        // load the data configs
        if (session.containsInitParam(IntactEnvironment.DATA_CONFIG_PARAM_NAME))
        {
            String dataConfigValue = session.getInitParam(IntactEnvironment.DATA_CONFIG_PARAM_NAME);

            String[] dataConfigs = dataConfigValue.split(",");

            for (String dataConfigClass : dataConfigs)
            {
                dataConfigClass = dataConfigClass.trim();
                log.info("Registering data-config: " + dataConfigClass);

                try
                {
                    DataConfig dataConfig = (DataConfig) (Class.forName(dataConfigClass).newInstance());
                    dataConfig.getSessionFactory();
                    config.addDataConfig(dataConfig);
                }
                catch (Exception e)
                {
                    throw new IntactInitializationError("Error initializing data configs", e);
                }
            }
        }

        // load the default prefix for generated ACs
        String prefix = getInitParamValue(session, IntactEnvironment.AC_PREFIX_PARAM_NAME, DEFAULT_AC_PREFIX);
        config.setAcPrefix(prefix);

        if (prefix.equals(DEFAULT_AC_PREFIX))
        {
            log.warn("The default prefix '"+DEFAULT_AC_PREFIX+"' will be used when persisting data. Usually." +
                    "this is not the desired functionality if the application is persisting data.");
        }

        // check the schema Version
        checkSchemaCompatibility(session);

        // read only
        String strReadOnly = getInitParamValue(session, IntactEnvironment.READ_ONLY_APP, DEFAULT_READ_ONLY_APP);
        boolean readOnly = Boolean.parseBoolean(strReadOnly);
        config.setReadOnlyApp(readOnly);
        log.debug("Application is read-only: "+readOnly);

        // load the institution
        loadInstitution(session);

        // preload the most common CvObjects
        boolean preloadCommonCvs = Boolean.valueOf(getInitParamValue(session, IntactEnvironment.PRELOAD_COMMON_CVS_PARAM_NAME, String.valueOf(Boolean.FALSE)));
        if (preloadCommonCvs)
        {
            log.info("Preloading common CvObjects");
            IntactTransaction tx = DaoFactory.getCurrentInstance(session, RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig()).beginTransaction();
            CvContext.getCurrentInstance(session).loadCommonCvObjects();
            tx.commit();
        }

        setInitialized(session, true);
    }

    public static IntactContext createIntactContext(IntactSession session)
    {
        if (RuntimeConfig.getCurrentInstance(session).getDataConfigs().isEmpty())
        {
            log.warn("No data configs found. Re-initializing IntAct");
            initIntact(session);
        }

        IntactContext context = getIntactContextFromSession(session);

        if (context != null)
        {
            return context;
        }

        String defaultUser = null;
        String defaultPassword = ((Configuration)RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig().getConfiguration())
                .getProperty(Environment.PASS);

        if (log.isDebugEnabled())
        {
            log.debug("Data Configs registered: "+RuntimeConfig.getCurrentInstance(session).getDataConfigs());
        }

        DaoFactory daoFactory = DaoFactory.getCurrentInstance(session,
                RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig());

        try
        {
            IntactTransaction tx = daoFactory.beginTransaction();
            defaultUser = daoFactory.getBaseDao().getDbUserName();
            tx.commit();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        log.debug("Creating user context, for user: "+defaultUser);
        UserContext userContext = new UserContext(defaultUser);
        userContext.setUserPassword(defaultPassword);

        log.debug("Creating data context...");
        DataContext dataContext = new DataContext(session);

        // start a context
        log.info("Creating IntactContext...");
        context = new IntactContextWrapper(userContext, dataContext, session);
        persistInstitutionIfNecessary(context);
        persistSchemaVersionIfNecessary(context);

        putIntactContextInSession(context, session);

        return context;
    }

    private static IntactContext getIntactContextFromSession(IntactSession session)
    {
        if (!session.isRequestAvailable())
        {
            return null;
        }

        return (IntactContext) session.getAttribute(INTACT_CONTEXT_SESS_ATT_NAME);
    }

    private static void putIntactContextInSession(IntactContext context, IntactSession session)
    {
        session.setAttribute(INTACT_CONTEXT_SESS_ATT_NAME, context);
    }

    private static void checkSchemaCompatibility(IntactSession session)
    {
        SchemaVersion requiredVersion = SchemaVersion.minimumVersion();

        DaoFactory daoFactory = DaoFactory.getCurrentInstance(session, RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig());

        IntactTransaction tx = daoFactory.beginTransaction();
        DbInfo dbInfoSchemaVersion = daoFactory.getDbInfoDao().get(DbInfo.SCHEMA_VERSION);
        tx.commit();

        SchemaVersion schemaVersion;

        if (dbInfoSchemaVersion == null)
        {
            log.warn("Schema version does not exist. Will be created");
            setSchemaVersionToBePersisted(session);
            return;
        }

        try
        {
            schemaVersion = SchemaVersion.parse(dbInfoSchemaVersion.getValue());
        }
        catch (Exception e)
        {
            throw new IntactInitializationError("Error getting schema version", e);
        }

        log.info("Schema Version: "+schemaVersion);

        if (!schemaVersion.isCompatibleWith(requiredVersion))
        {
            throw new IntactInitializationError("Database schema version "+requiredVersion+" is required" +
                    " to use this version of intact-core. Schema version found: "+schemaVersion);
        }
    }

    private static String getInitParamValue(IntactSession session, String initParamName, String defaultValue )
    {
        return getInitParamValue(session,initParamName,defaultValue,null);
    }

    private static String getInitParamValue(IntactSession session, String initParamName, String defaultValue, String systemPropertyDefault )
    {
        String initParamValue;

        if (session.containsInitParam(initParamName))
        {
            initParamValue = session.getInitParam(initParamName);
            log.debug(initParamName+": "+initParamValue);
        }
        else
        {
            if (session.isWebapp())
            {
                log.warn("Init-Param missing in web.xml: "+initParamName);
            }
            else
            {
                if (systemPropertyDefault != null)
                {
                    String propValue = System.getProperty(systemPropertyDefault);

                    if (propValue != null)
                    {
                        log.debug("Found environment property for default value: "+propValue);
                        defaultValue = propValue;
                    }
                }
            }
            log.debug("Using default value for param "+initParamName+": "+defaultValue);

            initParamValue = defaultValue;
        }

        return initParamValue;
    }

    private static void loadInstitution(IntactSession session)
    {
        String institutionLabel = getInitParamValue(session, IntactEnvironment.INSTITUTION_LABEL, null, "institution");

        if (institutionLabel == null)
        {
            if (session.isWebapp())
            {
                throw new IntactException("A institution label is mandatory. " +
                        "Provide it by setting the init parameter "+IntactEnvironment.INSTITUTION_LABEL+" " +
                        "in the web.xml file");
            }
            else
            {
                throw new IntactException("A institution label is mandatory. " +
                        "Provide it by setting the environment variable 'institution'" +
                        " when executing the java command. (e.g. java ... -Dinstitution=yourInstitution)." +
                        " You can also set the init parameter "+IntactEnvironment.INSTITUTION_LABEL+" in the IntactSession Object. ");
            }
        }

        DaoFactory daoFactory = DaoFactory.getCurrentInstance(session, RuntimeConfig.getCurrentInstance(session).getDefaultDataConfig());

        IntactTransaction tx = daoFactory.beginTransaction();
        Institution institution = daoFactory
                .getInstitutionDao().getByShortLabel( institutionLabel );
        tx.commit();

        if (institution == null)
        {
            // doesn't exist, create it
            institution = new Institution( institutionLabel );

            String fullName;
            String postalAddress;
            String url;

            if (institutionLabel.equalsIgnoreCase(DEFAULT_INSTITUTION_LABEL))
            {
                fullName = getInitParamValue(session, IntactEnvironment.INSTITUTION_FULL_NAME, DEFAULT_INSTITUTION_FULL_NAME);
                postalAddress = getInitParamValue(session, IntactEnvironment.INSTITUTION_POSTAL_ADDRESS, DEFAULT_INSTITUTION_POSTAL_ADDRESS);
                url = getInitParamValue(session, IntactEnvironment.INSTITUTION_URL, DEFAULT_INSTITUTION_URL);
            }
            else
            {
                fullName = getInitParamValue(session, IntactEnvironment.INSTITUTION_FULL_NAME, "");
                postalAddress = getInitParamValue(session, IntactEnvironment.INSTITUTION_POSTAL_ADDRESS, "");
                url = getInitParamValue(session, IntactEnvironment.INSTITUTION_URL, "");
            }

            institution.setFullName(fullName);
            institution.setPostalAddress(postalAddress);
            institution.setUrl(url);

            log.warn("Institution does not exist. Will be created, overriding any read-only attribute");
            setInstitutionToBePersisted(session);
        }

        RuntimeConfig.getCurrentInstance(session).setInstitution(institution);
        log.debug("Institution: "+institution.getShortLabel());
    }

    private static void persistInstitutionIfNecessary(IntactContext context)
    {
        IntactSession session = context.getSession();

        Object obj = session.getApplicationAttribute(INSTITUTION_TO_BE_PERSISTED_FLAG);

        if (obj == null)
        {
            return;
        }

        boolean needsToBePersisted = (Boolean) obj;

        if (needsToBePersisted)
        {
            Institution institution = context.getConfig().getInstitution();

            log.debug("Persisting institution");
            context.getDataContext().getDaoFactory().getInstitutionDao().persist( institution );
            context.getDataContext().commitTransaction();

            session.setApplicationAttribute(INSTITUTION_TO_BE_PERSISTED_FLAG, Boolean.FALSE);
        }
    }

    private static void persistSchemaVersionIfNecessary(IntactContext context)
    {
        IntactSession session = context.getSession();

        Object obj = session.getApplicationAttribute(SCHEMA_VERSION_TO_BE_PERSISTED_FLAG);

        if (obj == null)
        {
            return;
        }

        boolean needsToBePersisted = (Boolean) obj;

        if (needsToBePersisted)
        {
            DbInfo dbInfo = new DbInfo(DbInfo.SCHEMA_VERSION, SchemaVersion.minimumVersion().toString());

            log.debug("Persisting schema version");
            context.getDataContext().getDaoFactory().getDbInfoDao().persist( dbInfo );
            context.getDataContext().commitTransaction();

            session.setApplicationAttribute(SCHEMA_VERSION_TO_BE_PERSISTED_FLAG, Boolean.FALSE);
        }
    }

    private static void setInstitutionToBePersisted(IntactSession session)
    {
        session.setApplicationAttribute(INSTITUTION_TO_BE_PERSISTED_FLAG, Boolean.TRUE);
    }

    private static void setSchemaVersionToBePersisted(IntactSession session)
    {
        session.setApplicationAttribute(SCHEMA_VERSION_TO_BE_PERSISTED_FLAG, Boolean.TRUE);
    }

    private static void setInitialized(IntactSession session, boolean initialized)
    {
        session.setApplicationAttribute(INITIALIZED_APP_ATT_NAME, initialized);
    }

    public static boolean isInitialized(IntactSession session)
    {
        Object obj = session.getApplicationAttribute(INITIALIZED_APP_ATT_NAME);

        if (obj == null)
        {
            return false;
        }

        return (Boolean) obj;
    }

}
