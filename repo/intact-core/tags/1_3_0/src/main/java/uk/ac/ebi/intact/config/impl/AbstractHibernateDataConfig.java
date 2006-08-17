/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.config.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.persistence.util.ImportFromClasspathEntityResolver;
import uk.ac.ebi.intact.persistence.util.IntactAnnotator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07-Aug-2006</pre>
 */
public abstract class AbstractHibernateDataConfig extends DataConfig<SessionFactory, Configuration>
{

    private static final Log log = LogFactory.getLog(AbstractHibernateDataConfig.class);

    private static final String SESSION_FACTORY_NAME = "jndi/intact/sessionfactory";
    private static final String INTERCEPTOR_CLASS = "hibernate.util.interceptor_class";

    private Configuration configuration;

    private SessionFactory sessionFactory;

    private List<String> packagesWithEntities;

    public AbstractHibernateDataConfig()
    {
        this.packagesWithEntities = getPackagesWithEntities();
    }

    public void initialize()
    {
        File cfgFile = getConfigFile();

        // Create the initial SessionFactory from the default configuration files
        try
        {
            // Replace with Configuration() if you don't use annotations or JDK 5.0
            configuration = new AnnotationConfiguration();

            for (String packageName : getPackagesWithEntities())
            {
                log.debug("Processing package: " + packageName);

                if (!packageName.startsWith("/") && packageName.contains("."))
                {
                    packageName = "/"+packageName.replaceAll("\\.", "/");
                }

                for (Class clazz : IntactAnnotator.getAnnotatedClasses(packageName))
                {
                    log.debug("Adding annotated class to hibernate: " + clazz.getName());
                    ((AnnotationConfiguration) configuration).addAnnotatedClass(clazz);
                }
            }

            // This custom entity resolver supports entity placeholders in XML mapping files
            // and tries to resolve them on the classpath as a resource
            configuration.setEntityResolver(new ImportFromClasspathEntityResolver());

            // Read not only hibernate.properties, but also hibernate.cfg.xml
            if (cfgFile != null)
            {
                log.info("Reading from config file: " + cfgFile);
                configuration.configure(cfgFile);
            }
            else
            {
                log.info("Reading from default config file");
                configuration.configure();
            }

            // Set global interceptor from configuration
            setInterceptor(configuration, null);

            if (configuration.getProperty(Environment.SESSION_FACTORY_NAME) != null)
            {
                // Let Hibernate bind the factory to JNDI
                configuration.buildSessionFactory();
            }
            else
            {
                // or use static variable handling
                sessionFactory = configuration.buildSessionFactory();
            }

        }
        catch (HibernateException ex)
        {
            log.error("Building SessionFactory failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }

        setInitialized(true);
    }

    public SessionFactory getSessionFactory()
    {
        checkInitialization();

        if (sessionFactory != null)
        {
            return sessionFactory;
        }

        try
        {
            sessionFactory = (SessionFactory) new InitialContext().lookup(configuration.getProperty(Environment.SESSION_FACTORY_NAME));
        }
        catch (NamingException e)
        {
            throw new IntactException("SessionFactory could not be retrieved from JNDI: "+Environment.SESSION_FACTORY_NAME);
        }

        return sessionFactory;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public void addPackageWithEntities(String packageName)
    {
        if (isInitialized())
        {
            throw new IntactException("Cannot add package after the sessionFactory has been initialized");
        }

        packagesWithEntities.add(packageName);
    }

    protected abstract List<String> getPackagesWithEntities();

    protected abstract File getConfigFile();

    /**
     * Either sets the given interceptor on the configuration or looks
     * it up from configuration if null.
     */
    private void setInterceptor(Configuration configuration, Interceptor interceptor) {
        String interceptorName = configuration.getProperty(INTERCEPTOR_CLASS);
        if (interceptor == null && interceptorName != null) {
            try {
                Class interceptorClass =
                        AbstractHibernateDataConfig.class.getClassLoader().loadClass(interceptorName);
                interceptor = (Interceptor)interceptorClass.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Could not initialize interceptor: " + interceptorName, ex);
            }
        }
        if (interceptor != null) {
            configuration.setInterceptor(interceptor);
        } else {
            configuration.setInterceptor(EmptyInterceptor.INSTANCE);
        }
    }
}
