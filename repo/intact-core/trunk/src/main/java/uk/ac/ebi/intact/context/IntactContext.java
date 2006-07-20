package uk.ac.ebi.intact.context;

import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactContext
{

    private UserContext userContext;
    private Institution institution;

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
            UserContext userContext = new UserContext(null);
            return new IntactContext(userContext);
        }
    };


    public UserContext getUserContext()
    {
        return userContext;
    }

    public Institution getInstitution()
    {
        if (institution == null)
        {
            institution = DaoFactory.getInstitutionDao().getInstitution();
        }

        return institution;
    }
}
