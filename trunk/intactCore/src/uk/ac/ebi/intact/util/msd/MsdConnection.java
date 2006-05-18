package uk.ac.ebi.intact.util.msd;

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
final class MsdConnection  {

    private static  String DRIVER_NAME;
    private static  String DB_NAME;
    private static  String USER_NAME;
    private static  String USER_PASSWORD;

    /**
     * Enable the connection to MSD database using the config file.
     * @return
     * @throws IntactException  if the config file is not found  or if one of the properties
     * is not found by MsdPropertyLoader.
     */
    static Connection getMsdConnection() throws IntactException {

        MsdPropertyLoader msdPropertyLoader = new MsdPropertyLoader();
        DRIVER_NAME =msdPropertyLoader.getDriverName();
        DB_NAME =msdPropertyLoader.getDbName();
        USER_NAME =msdPropertyLoader.getUserName();
        USER_PASSWORD =msdPropertyLoader.getUserPassword();

        Connection result = null;
        try{
            Class.forName(DRIVER_NAME).newInstance();
        }
        catch (Exception ex){
            System.out.println("Check claspath. Cannot load db driver: "+ DRIVER_NAME);
        }
        try{
            result = DriverManager.getConnection(DB_NAME, USER_NAME, USER_PASSWORD);
        }
        catch (SQLException ex){
            System.err.println("Driver loaded, but cannot connect to db:" + DB_NAME);
        }
        return result;
}

}
