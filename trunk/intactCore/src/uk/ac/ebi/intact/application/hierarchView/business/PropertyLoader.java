/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.hierarchView.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;


/**
 * Allow to create a set of properties from a file.
 *
 *   <b> example : </b><br>
 *   Properties props = PropertyLoader.load ("config/file.properties");
 *   if (props != null) {
 *     String login = props.getProperty ("database.login");
 *     // ...
 *   } else {
 *     System.err.println ("Unable to open the properties file.");
 *   }
 *
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */

public class PropertyLoader {

    // can't instanciate outside this class
    private PropertyLoader () {}

    /**
     * Allows to get a set of property or null from a property file.
     *
     * @param propName the name of the property file
     * @return a set of property or null
     */
    public static Properties load (String propName) {

        try {
            InputStream is = PropertyLoader.class.getResourceAsStream (propName);
            if (is != null) {
                Properties properties = new Properties ();
                properties.load (is);
                return properties;
            }
        } catch (IOException ioe) {}

        return null;
    }


    /**
     * get the content of the property file int a String
     *
     * @param propName the name of the property file
     * @param lineSeparator line separator between two properties
     * @return the content of the property file
     */
    public static String getContent (String propName, String lineSeparator) {
        Properties properties = load (propName);
        if (null == properties) return null;

        StringBuffer sb = new StringBuffer ();

        // display properties content
        Enumeration e = properties.propertyNames();
        for (; e.hasMoreElements() ;) {
            String key = (String) e.nextElement();
            sb.append (key + "=" + properties.getProperty(key) + lineSeparator);
        }

        return sb.toString ();
    }
}
