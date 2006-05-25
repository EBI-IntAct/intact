/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util.msd.util;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.util.PropertyLoader;

import java.util.Properties;

/**
 * TODO comment it.
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 */
public class MsdPropertyLoader {

    /**
     * Path to the msd.properties file.
     */
    public static final String MSD_CONFIG_FILE = "/config/msd.properties";

    /**
     * String containing the property name of the driver Name property
     */
    private static final String DRIVER_NAME_PROPERTY = "driver.name";
    /**
     * String containing the property name of the Db url  property
     */
    private static final String DB_URL_PROPERTY = "db.url";
    /**
     * String containing the property name of the driver User Name property
     */
    private static final String USER_NAME_PROPERTY = "user.name";
    /**
     * String containing the property name of the driver User Password property
     */
    private static final String USER_PASSWORD_PROPERTY = "user.password";

    /**
     * String containing the property value of the driver Name property
     */
    private String driverName;
    /**
     * String containing the property value of the driver Db Url property
     */
    private String dbUrl;
    /**
     * String containing the property value of the driver User Name property
     */
    private String userName;
    /**
     * String containing the property value of the driver User Password property
     */
    private String userPassword;


    /**
     * Constructor of the MsdPropertyLoader class. When instantiating this class, it automatically load the properties
     * from the config file.
     *
     * @throws IntactException return an IntactException if the config file is not found or if one of the properties is
     *                         not found.
     */
    public MsdPropertyLoader() throws IntactException {
        loadPropertyFile();
    }

    /**
     * Method that does load the property file msd.properties.
     *
     * @throws IntactException eturn an IntactException if the config file is not found or if one of the properties is
     *                         not found.
     */
    private void loadPropertyFile() throws IntactException {
        Properties props = PropertyLoader.load( MSD_CONFIG_FILE );
        if ( props == null ) {
            throw new IntactException( "The config file " + MSD_CONFIG_FILE + " was not found" );
        }
        driverName = getProperty( props, DRIVER_NAME_PROPERTY );
        dbUrl = getProperty( props, DB_URL_PROPERTY );
        userName = getProperty( props, USER_NAME_PROPERTY );
        userPassword = getProperty( props, USER_PASSWORD_PROPERTY );
    }

    /**
     * Given a Property object and the name of a property it returns a String containing the value of the property.
     *
     * @param props        Properties object corresponding to the msd.properties file.
     * @param propertyName Name or the property you want to get the value.
     *
     * @return
     *
     * @throws IntactException return an IntactException if the property is not found in the file.
     */
    private String getProperty( Properties props, String propertyName ) throws IntactException {
        String property = props.getProperty( propertyName );
        if ( property == null ) {
            throw new IntactException( "The property " + propertyName + " could not be found in the config file"
                                       + MSD_CONFIG_FILE + "." );
        }
        return property;
    }

    /**
     * Getter of the driverName String.
     *
     * @return the driverName global String variables
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * Getter of the dbUrl String.
     *
     * @return the dbUrl global String variables
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * Getter of the userName String.
     *
     * @return the userName global String variables
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Getter of the userPassword String.
     *
     * @return the userPassword global String variables
     */
    public String getUserPassword() {
        return userPassword;
    }

    public static void main( String[] args ) throws IntactException {
        MsdPropertyLoader msdPropertyLoader = new MsdPropertyLoader();
        System.out.println( "msdPropertyLoader.getDbUrl() = " + msdPropertyLoader.getDbUrl() );
        System.out.println( "msdPropertyLoader.getDriverName() = " + msdPropertyLoader.getDriverName() );
        System.out.println( "msdPropertyLoader.getUserName() = " + msdPropertyLoader.getUserName() );
        System.out.println( "msdPropertyLoader.getUserPassword() = " + msdPropertyLoader.getUserPassword() );
    }
}
