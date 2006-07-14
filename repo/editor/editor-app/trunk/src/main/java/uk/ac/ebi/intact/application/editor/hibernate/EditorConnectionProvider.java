package uk.ac.ebi.intact.application.editor.hibernate;

import org.hibernate.connection.ConnectionProvider;
import org.hibernate.HibernateException;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.SQLException;

import uk.ac.ebi.intact.application.commons.context.IntactContext;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class EditorConnectionProvider implements ConnectionProvider
{
    private Map<String,Connection> userConnections = new HashMap<String,Connection>();

    public void configure(Properties properties) throws HibernateException
    {

    }

    public Connection getConnection() throws SQLException
    {
        String currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId();

        if (userConnections.containsKey(currentUser))
        {
            return userConnections.get(currentUser);
        }

        // obtain the connection for that user
        Connection connection = null;

        return connection;
    }

    public void closeConnection(Connection connection) throws SQLException
    {
         // no connections are closed
    }

    public void close() throws HibernateException
    {
        userConnections.clear();
    }

    public boolean supportsAggressiveRelease()
    {
        return false;
    }
}
