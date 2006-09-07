/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Contains the attributes stored by the IntAct applications. It has to be used to get and set
 * all the attributes used by the application.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public abstract class IntactSession
{
    private static final String DEFAULT_PROP_FILE = "/intact.properties";

    protected void readDefaultProperties()
    {
        Properties props = readFromClasspathProperties();
        initParametersWithProperties(props);
    }

    private Properties readFromClasspathProperties()
    {
        Properties properties = new Properties();

        URL intactPropertiesFilename = IntactSession.class.getResource(DEFAULT_PROP_FILE);

        if (intactPropertiesFilename != null)
        {
           File intactPropsFile = new File(intactPropertiesFilename.getFile());

            if (intactPropsFile.exists() && !intactPropsFile.isDirectory())
            {
                try {
                    properties.load(new FileInputStream(intactPropsFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return properties;
    }

    protected void initParametersWithProperties(Properties properties)
    {
        Enumeration<String> propNames = (Enumeration<String>) properties.propertyNames();

        while (propNames.hasMoreElements())
        {
           String propName = propNames.nextElement();
           setInitParam(propName, properties.getProperty(propName));
        }
    }

    public abstract Object getApplicationAttribute(String name);

    public abstract void setApplicationAttribute(String name, Object attribute);

    public abstract Serializable getAttribute(String name);

    public abstract void setAttribute(String name, Serializable attribute);

    public abstract Object getRequestAttribute(String name);

    public abstract void setRequestAttribute(String name, Object value);

    public abstract boolean containsInitParam(String name);

    public abstract String getInitParam(String name);

    public abstract void setInitParam(String name, String value);

    public abstract boolean isWebapp();

    public abstract boolean isRequestAvailable();
}
