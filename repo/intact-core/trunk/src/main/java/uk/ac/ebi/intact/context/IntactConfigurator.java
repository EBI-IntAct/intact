/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;
import uk.ac.ebi.intact.context.impl.IntactContextWrapper;
import uk.ac.ebi.intact.model.Institution;

/**
 * TODO: comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>04/08/2006</pre>
 */
public class IntactConfigurator
{
    private static final Log log = LogFactory.getLog(IntactConfigurator.class);

    private static final String AC_PREFIX_PARAM_NAME = "uk.ac.ebi.intact.AC_PREFIX";
    private static final String DEFAULT_AC_PREFIX = "UNK";

    public static void initIntact(IntactSession session)
    {
        log.info("Initializing intact-core...");
        IntactTransaction tx = DaoFactory.beginTransaction();

        RuntimeConfig config = RuntimeConfig.getCurrentInstance(session);

        // load the default institution
        Institution institution = config.getInstitution();

        log.info("Institution: "+institution.getFullName());

        // load the default prefix for generated ACs
        if (session.containsInitParam(AC_PREFIX_PARAM_NAME))
        {
            String prefix = session.getInitParam(AC_PREFIX_PARAM_NAME);
            log.debug("AC prefix: "+prefix);
            config.setAcPrefix(prefix);
        }
        else
        {
            if (session.isWebapp())
            {
                log.warn("Init-Param missing in web.xml: "+AC_PREFIX_PARAM_NAME);
            }
            log.debug("Using default AC prefix: "+DEFAULT_AC_PREFIX);
            config.setAcPrefix(DEFAULT_AC_PREFIX);
        }

        tx.commit();

    }

    public static void createIntactContext(IntactSession session)
    {
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

        // start a context
        log.debug("Created IntactContext");
        new IntactContextWrapper(userContext, session);
    }


}
