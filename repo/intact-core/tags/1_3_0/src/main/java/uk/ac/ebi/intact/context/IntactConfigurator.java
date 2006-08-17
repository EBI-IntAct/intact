/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.context.impl.IntactContextWrapper;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

import java.sql.SQLException;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class IntactConfigurator
{
    private static final Log log = LogFactory.getLog(IntactConfigurator.class);

    public static final String DATA_CONFIG_PARAM_NAME = "uk.ac.ebi.intact.DATA_CONFIG";

    public static final String INSTITUTION_LABEL = "uk.ac.ebi.intact.INSTITUTION_LABEL";
    public static final String INSTITUTION_FULL_NAME = "uk.ac.ebi.intact.INSTITUTION_FULL_NAME";
    public static final String INSTITUTION_POSTAL_ADDRESS = "uk.ac.ebi.intact.INSTITUTION_POSTAL_ADDRESS";
    public static final String INSTITUTION_URL = "uk.ac.ebi.intact.INSTITUTION_URL";

    public static final String AC_PREFIX_PARAM_NAME = "uk.ac.ebi.intact.AC_PREFIX";
    public static final String PRELOAD_COMMON_CVS_PARAM_NAME = "uk.ac.ebi.intact.PRELOAD_COMMON_CVOBJECTS";

    private static final String DEFAULT_INSTITUTION_LABEL = "ebi";
    private static final String DEFAULT_INSTITUTION_FULL_NAME = "European Bioinformatics Institute";
    private static final String DEFAULT_INSTITUTION_POSTAL_ADDRESS = "European Bioinformatics Institute \n" +
                                                                    "Wellcome Trust Genome Campus\n" +
                                                                    "Hinxton, Cambridge\n" +
                                                                    "CB10 1SD\n" +
                                                                    "United Kingdom";
    private static final String DEFAULT_INSTITUTION_URL = "http://www.ebi.ac.uk/";

    private static final String DEFAULT_AC_PREFIX = "UNK";

    /**
     * Path of the configuration file which allow to retrieve the inforamtion related to the IntAct node we are running
     * on.
     */
    private static final String INSTITUTION_CONFIG_FILE = "/config/Institution.properties";
    

    public static void initIntact(IntactSession session)
    {
        log.info("Initializing intact-core...");

        RuntimeConfig config = RuntimeConfig.getCurrentInstance(session);

        if (config.getDefaultDataConfig() == null)
        {
            // add the core model data config
            log.info("Registering standard data-config");
            StandardCoreDataConfig stdDataConfig = new StandardCoreDataConfig();
            stdDataConfig.initialize();
            config.addDataConfig(stdDataConfig, true);
        }

        // load the data configs
        if (session.containsInitParam(DATA_CONFIG_PARAM_NAME))
        {
            String dataConfigValue = session.getInitParam(DATA_CONFIG_PARAM_NAME);

            String[] dataConfigs = dataConfigValue.split(",");

            for (String dataConfigClass : dataConfigs)
            {
                dataConfigClass = dataConfigClass.trim();
                log.info("Registering data-config: " + dataConfigClass);


                try
                {
                    DataConfig dataConfig = (DataConfig) (Class.forName(dataConfigClass).newInstance());
                    dataConfig.initialize();
                    config.addDataConfig(dataConfig);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        // load the default institution
        DaoFactory daoFactory = DaoFactory.getCurrentInstance(config.getDefaultDataConfig());

        IntactTransaction tx = daoFactory.beginTransaction();
        Institution institution = loadInstitution(daoFactory, session);
        tx.commit();
        
        config.setInstitution(institution);

        log.info("Institution: "+institution.getFullName());

        // load the default prefix for generated ACs
        String prefix = getInitParamValue(session, AC_PREFIX_PARAM_NAME, DEFAULT_AC_PREFIX);
        config.setAcPrefix(prefix);


        // preload the most common CvObjects
        boolean preloadCommonCvs = Boolean.valueOf(getInitParamValue(session, PRELOAD_COMMON_CVS_PARAM_NAME, String.valueOf(Boolean.FALSE)));
        if (preloadCommonCvs)
        {
            log.info("Preloading common CvObjects");
            CvContext.getCurrentInstance(session).loadCommonCvObjects();
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

            initParamValue = (String) defaultValue;
        }

        return initParamValue;
    }

    public static IntactContext createIntactContext(IntactSession session)
    {
        String defaultUser = null;

        if (log.isInfoEnabled())
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

        log.debug("Creating data context...");
        DataContext dataContext = new DataContext(session);

        // start a context
        log.info("Creating IntactContext...");
        return new IntactContextWrapper(userContext, dataContext, session);
    }

    /**
     * Allow the user not to know about the it's Institution, it has to be configured once in the properties file:
     * ${INTACTCORE_HOME}/config/Institution.properties and then when calling that method, the Institution is either
     * retreived or created according to its shortlabel.
     *
     * @return the Institution to which all created object will be linked.
     */
    private static Institution loadInstitution(DaoFactory daoFactory, IntactSession session) 
    {

        String institutionLabel = getInitParamValue(session, INSTITUTION_LABEL, null, "institution");

        if (institutionLabel == null)
        {
            if (session.isWebapp())
            {
                throw new IntactException("A institution label is mandatory. " +
                        "Provide it by setting the init parameter "+INSTITUTION_LABEL+" " +
                        "in the web.xml file");
            }
            else
            {
                throw new IntactException("A institution label is mandatory. " +
                        "Provide it by setting the environment variable 'institution'" +
                        " when executing the java command. (e.g. java ... -Dinstitution=yourInstitution)." +
                        " You can also pass the init parameter "+INSTITUTION_LABEL+" to the IntactSession Object " +
                        "when calling the IntactContext.getCurrentInstance(IntactSession)");
            }
        }

        Institution institution = daoFactory.getInstitutionDao().getByShortLabel( institutionLabel );

        if ( institution == null ) {
            // doesn't exist, create it
            institution = new Institution( institutionLabel );

            String fullName = getInitParamValue(session, INSTITUTION_FULL_NAME, DEFAULT_INSTITUTION_FULL_NAME);
            String postalAddress = getInitParamValue(session, INSTITUTION_POSTAL_ADDRESS, DEFAULT_INSTITUTION_POSTAL_ADDRESS);
            String url = getInitParamValue(session, INSTITUTION_URL, DEFAULT_INSTITUTION_POSTAL_ADDRESS);

            institution.setFullName(fullName);
            institution.setPostalAddress(postalAddress);
            institution.setUrl(url);

            log.info("Inserting institution information in the database");
            daoFactory.getInstitutionDao().persist( institution );

        }

        return institution;
    }

}
