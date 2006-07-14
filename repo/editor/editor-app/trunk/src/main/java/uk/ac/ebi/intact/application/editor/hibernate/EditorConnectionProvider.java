package uk.ac.ebi.intact.application.editor.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;
import uk.ac.ebi.intact.application.commons.context.IntactContext;

import java.sql.Connection;
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

    public void configure(Properties properties) throws HibernateException
    {

    }

    public Connection getConnection() throws SQLException
    {
        String currentUser = IntactContext.getCurrentInstance().getUserContext().getUserId();

        log.debug("Getting connection for user: "+currentUser);

        Connection connection = IntactContext.getCurrentInstance().getUserContext().getConnection();

        if (connection == null)
        {
            throw new SQLException("User with id '"+currentUser+"' is not associated to a connection in the UserContext");
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
