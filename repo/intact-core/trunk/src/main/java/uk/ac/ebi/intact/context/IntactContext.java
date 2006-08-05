package uk.ac.ebi.intact.context;

import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.util.PropertyLoader;
import uk.ac.ebi.intact.context.impl.StandaloneSession;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Properties;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactContext implements Serializable
{
    private static final Log log = LogFactory.getLog(IntactContext.class);

    public static String SESSION_CONTEXT_NAME = IntactContext.class.getName();

    private IntactSession session;

    private UserContext userContext;


    protected IntactContext(UserContext userContext, IntactSession session)
    {
        this.userContext = userContext;
        this.session = session;
    }

    public static IntactContext getCurrentInstance()
    {
        if (currentInstance.get() == null)
        {
            log.warn("Current instance of IntactContext is null. Initializing with StandaloneSession," +
                    "because probably this application is not a web application");
            IntactConfigurator.initIntact(new StandaloneSession());
        }

       return currentInstance.get();
    }

    private static ThreadLocal<IntactContext> currentInstance = new ThreadLocal<IntactContext>()
    {
        protected IntactContext initialValue()
        {
            return null;
            /*
            String defaultUser = null;

            try
            {
                defaultUser = DaoFactory.getBaseDao().getDbUserName();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

            UserContext userContext = new UserContext(defaultUser);

            return new IntactContext(userContext, null);  */
        }
    };

    protected static void setCurrentInstance(IntactContext context)
    {
        currentInstance.set(context);
    }


    public UserContext getUserContext()
    {
        return userContext;
    }

    public Institution getInstitution() throws IntactException
     {
        return getConfig().getInstitution();
    }

    public RuntimeConfig getConfig()
    {
        return RuntimeConfig.getCurrentInstance(session);
    }

   
}
