package uk.ac.ebi.intact.application.commons.context;

import java.sql.Connection;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UserContext
{

    private String userId;
    private String userMail;
    private Connection connection;

    public UserContext(String userId)
    {
        this.userId = userId;
    }


    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserMail()
    {
        if (userMail == null)
        {
            return userId+"@ebi.ac.uk";

        }
        return userMail;
    }

    public void setUserMail(String userMail)
    {
        this.userMail = userMail;
    }


    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }
}
