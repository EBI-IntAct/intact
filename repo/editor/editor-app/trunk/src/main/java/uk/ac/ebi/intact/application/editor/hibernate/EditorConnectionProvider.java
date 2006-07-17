package uk.ac.ebi.intact.application.editor.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.connection.ConnectionProvider;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.persistence.util.HibernateUtil;

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
        String currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId();

        log.debug("Getting connection for user: "+currentUser);

        if (!driverLoaded)
        {
            String driverClass = HibernateUtil.getConfiguration().getProperty(Environment.DRIVER);
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

        Connection connection = IntactContext.getCurrentInstance().getUserContext().getConnection();

        if (connection == null)
        {
            log.debug("Using default connection");
            String url = HibernateUtil.getConfiguration().getProperty(Environment.URL);
            String name = HibernateUtil.getConfiguration().getProperty(Environment.USER);
            String password = HibernateUtil.getConfiguration().getProperty(Environment.PASS);

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
