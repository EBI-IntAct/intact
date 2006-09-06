package uk.ac.ebi.intact.application.editor.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Configuration;
import org.hibernate.connection.ConnectionProvider;
import uk.ac.ebi.intact.context.IntactContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class EditorConnectionProvider implements ConnectionProvider
{
    private static final Log log = LogFactory.getLog(EditorConnectionProvider.class);

    private boolean driverLoaded;

    public void configure(Properties properties) throws HibernateException
    {

    }

    public Connection getConnection() throws SQLException
    {
        Configuration configuration = (Configuration) IntactContext.getCurrentInstance().getConfig().getDefaultDataConfig().getConfiguration();
                
        String currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId();
        String currentUserPassword = IntactContext.getCurrentInstance().getUserContext().getUserPassword();
        String url = configuration.getProperty(Environment.URL);

        log.debug("Getting connection for user: " + currentUser);
        log.debug("CurrentUser: " + currentUser);
        log.debug("CurrentUserPassword: " + currentUserPassword);


        if (!driverLoaded)
        {
            String driverClass = configuration.getProperty(Environment.DRIVER);
            try
            {
                Class.forName(driverClass);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            driverLoaded = true;
        }
        Connection connection;
        if(currentUser != null){
            connection = DriverManager.getConnection(url, currentUser, currentUserPassword);
        }
        else{
            log.debug("Using default connection");
            String name = configuration.getProperty(Environment.USER);
            String password = configuration.getProperty(Environment.PASS);

            connection = DriverManager.getConnection(url,name,password);
        }

        return connection;
    }

    public void closeConnection(Connection connection) throws SQLException
    {
         // no connections are closed
    }

    public void close() throws HibernateException
    {
    }

    public boolean supportsAggressiveRelease()
    {
        return false;
    }
}
