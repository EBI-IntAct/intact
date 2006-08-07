/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.SQLException;
import java.util.Properties;

import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.context.impl.IntactContextWrapper;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.PropertyLoader;

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

    private static final String INTACT_INIT_DONE
            = IntactConfigurator.class.getName() + ".INTACT_INIT_DONE";

    private static final String DATA_CONFIG_PARAM_NAME = "uk.ac.ebi.intact.DATA_CONFIG";
    private static final String AC_PREFIX_PARAM_NAME = "uk.ac.ebi.intact.AC_PREFIX";

    private static final String DEFAULT_AC_PREFIX = "UNK";

    /**
     * Path of the configuration file which allow to retrieve the inforamtion related to the IntAct node we are running
     * on.
     */
    private static final String INSTITUTION_CONFIG_FILE = "/config/Institution.properties";
    

    public static void initIntact(IntactSession session)
    {
        log.info("Initializing intact-core...");

        boolean initDone = (session.getApplicationAttribute(INTACT_INIT_DONE) != null);

        if (initDone)
        {
            return;
        }

        RuntimeConfig config = RuntimeConfig.getCurrentInstance(session);

        // add the core model data config
        log.info("Registering standard data-config");
        StandardCoreDataConfig stdDataConfig = new StandardCoreDataConfig();
        try
        {
            stdDataConfig.initialize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        config.addDataConfig(stdDataConfig);

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
        DaoFactory daoFactory = DaoFactory.getCurrentInstance(stdDataConfig);

        IntactTransaction tx = daoFactory.beginTransaction();
        Institution institution = loadInstitutionFromProperties(daoFactory);
        tx.commit();
        
        config.setInstitution(institution);

        log.info("Institution: "+institution.getFullName());

        // load the default prefix for generated ACs
        if (session.containsInitParam(AC_PREFIX_PARAM_NAME))
        {
            String prefix = session.getInitParam(AC_PREFIX_PARAM_NAME);
            log.debug("AC prefix: "+prefix);
            config.setAcPrefix(prefix);
        }
        else
        {
            if (session.isWebapp())
            {
                log.warn("Init-Param missing in web.xml: "+AC_PREFIX_PARAM_NAME);
            }
            log.debug("Using default AC prefix: "+DEFAULT_AC_PREFIX);
            config.setAcPrefix(DEFAULT_AC_PREFIX);
        }

        session.setApplicationAttribute(INTACT_INIT_DONE, true);

    }

    public static IntactContext createIntactContext(IntactSession session)
    {
        String defaultUser = null;

        DaoFactory daoFactory = DaoFactory.getCurrentInstance(session,
                RuntimeConfig.getCurrentInstance(session).getDataConfig(StandardCoreDataConfig.NAME));

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
    private static Institution loadInstitutionFromProperties(DaoFactory daoFactory) throws IntactException
    {
        Institution institution = null;

        Properties props = PropertyLoader.load( INSTITUTION_CONFIG_FILE );
        if ( props != null ) {
            String shortlabel = props.getProperty( "Institution.shortLabel" );
            if ( shortlabel == null || shortlabel.trim().equals( "" ) ) {
                throw new IntactException( "Your institution is not properly configured, check out the configuration file:" +
                                           INSTITUTION_CONFIG_FILE + " and set 'Institution.shortLabel' correctly" );
            }

            // search for it (force it for LC as short labels must be in LC).
            shortlabel = shortlabel.trim();
            institution = daoFactory.getInstitutionDao().getByShortLabel( shortlabel );

            if ( institution == null ) {
                // doesn't exist, create it
                institution = new Institution( shortlabel );

                String fullname = props.getProperty( "Institution.fullName" );
                if ( fullname != null ) {
                    fullname = fullname.trim();
                    if ( !fullname.equals( "" ) ) {
                        institution.setFullName( fullname );
                    }
                }


                String lineBreak = System.getProperty( "line.separator" );
                StringBuffer address = new StringBuffer( 128 );
                appendLineFromProperty( address, props, "Institution.postalAddress.line1" );
                appendLineFromProperty( address, props, "Institution.postalAddress.line2" );
                appendLineFromProperty( address, props, "Institution.postalAddress.line3" );
                appendLineFromProperty( address, props, "Institution.postalAddress.line4" );
                appendLineFromProperty( address, props, "Institution.postalAddress.line5" );

                if ( address.length() > 0 ) {
                    address.deleteCharAt( address.length() - 1 ); // delete the last line break;
                    institution.setPostalAddress( address.toString() );
                }

                String url = props.getProperty( "Institution.url" );
                if ( url != null ) {
                    url = url.trim();
                    if ( !url.equals( "" ) ) {
                        institution.setUrl( url );
                    }
                }

                daoFactory.getInstitutionDao().persist( institution );

            }

        } else {
            throw new IntactException( "Unable to read the properties from " + INSTITUTION_CONFIG_FILE );
        }

        return institution;
    }

    private static void appendLineFromProperty(StringBuffer sb, Properties props, String propertyName)
    {
        String lineBreak = System.getProperty("line.separator");

        String line = props.getProperty(propertyName);
        if (line != null)
        {
            line = line.trim();
        }
        if (!line.equals(""))
        {
            sb.append(line).append(lineBreak);
        }
    }


}
