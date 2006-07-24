package uk.ac.ebi.intact.context;

import java.sql.Connection;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UserContext
{

    private String userId = null;
    private String userMail;
    private String userPassword;

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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
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
