package uk.ac.ebi.intact.application.commons.context;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UserContext
{

    private String userId;
    private String userMail;

    public UserContext()
    {

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
        return userMail;
    }

    public void setUserMail(String userMail)
    {
        this.userMail = userMail;
    }
}
