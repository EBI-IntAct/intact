package uk.ac.ebi.intact.application.commons.context;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactContext
{

    private UserContext userContext;

    private IntactContext(UserContext userContext)
    {
        this.userContext = userContext;
    }

    public static IntactContext getCurrentInstance()
    {
       return currentInstance.get();
    }

    private static ThreadLocal<IntactContext> currentInstance = new ThreadLocal<IntactContext>()
    {
        protected IntactContext initialValue()
        {
            UserContext userContext = new UserContext();
            return new IntactContext(userContext);
        }
    };


    public UserContext getUserContext()
    {
        return userContext;
    }
}
