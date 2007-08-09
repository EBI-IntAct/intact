package uk.ac.ebi.intact.util.msd.util;

import uk.ac.ebi.intact.business.IntactException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: krobbe
 * Date: 22-Mar-2006
 * Time: 17:37:04
 * To change this template use File | Settings | File Templates.
 */
final class MsdConnection {

    private static Connection conn;

    /**
     * String containing the driver class name.
     */
    private static String DRIVER_NAME;
    /**
     * String
     */
    private static String DB_URL;
    private static String USER_NAME;
    private static String USER_PASSWORD;

    /**
     * Enable the connection to MSD database using the config file.
     *
     * @return
     *
     * @throws IntactException if the config file is not found  or if one of the properties is not found by
     *                         MsdPropertyLoader.
     */
    public static Connection getMsdConnection() throws IntactException {

        if ( conn == null ) {

            MsdPropertyLoader msdPropertyLoader = new MsdPropertyLoader();
            DRIVER_NAME = msdPropertyLoader.getDriverName();
            DB_URL = msdPropertyLoader.getDbUrl();
            USER_NAME = msdPropertyLoader.getUserName();
            USER_PASSWORD = msdPropertyLoader.getUserPassword();


            try {
                Class.forName( DRIVER_NAME ).newInstance();
            }
            catch ( Exception ex ) {
                // We don't want to continue, if the connection to the database can't work so just throw an Exception.
                throw new IntactException( "Problem instanciating " + DRIVER_NAME + " the msd Connection can be created : ", ex );
            }
            try {
                conn = DriverManager.getConnection( DB_URL, USER_NAME, USER_PASSWORD );
            }
            catch ( SQLException ex ) {
                // We don't want to continue, if the connection to the database can't work so just throw an Exception.
                throw new IntactException( "Problem getting the connection to: " + DB_URL, ex );
            }
        }
        return conn;
    }

    public static void closeConnection() throws IntactException {
        if ( conn != null ) {
            try {
                conn.close();
            } catch ( SQLException e ) {
                throw new IntactException( "Problem trying to close the connection to " + DB_URL + " : ", e );
            }
            conn = null;
        }
    }

}
