package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.impl.StandaloneSession;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.config.DataConfig;
import uk.ac.ebi.intact.config.impl.StandardCoreDataConfig;

import java.io.Serializable;

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
    private DataContext dataContext;


    protected IntactContext(UserContext userContext, DataContext dataContext, IntactSession session)
    {
        this.userContext = userContext;
        this.session = session;
        this.dataContext = dataContext;
    }

    public static IntactContext getCurrentInstance()
    {
        if (currentInstance.get() == null)
        {
            log.warn("Current instance of IntactContext is null. Initializing with StandaloneSession," +
                    "because probably this application is not a web application");
            initStandaloneContext(null);
        }

       return currentInstance.get();
    }

    public static void initStandaloneContext(DataConfig standardDataConfig)
    {
        if (standardDataConfig == null)
        {
            standardDataConfig = new StandardCoreDataConfig();
        }

       IntactSession session = new StandaloneSession();
       RuntimeConfig.getCurrentInstance(session).setDefaultDataConfig(standardDataConfig);
       IntactConfigurator.initIntact(session);
       IntactConfigurator.createIntactContext(session);
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
                defaultUser = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getBaseDao().getDbUserName();
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


    public IntactSession getSession()
    {
        return session;
    }

    public DataContext getDataContext()
    {
        return dataContext;
    }
}
