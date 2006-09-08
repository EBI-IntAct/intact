/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.search.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.SessionFactory;

import java.util.*;
import java.io.IOException;

import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.impl.WebServiceSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
public class SearchService
{

    private static final Log log = LogFactory.getLog(SearchService.class);

    private DaoFactory daoFactory;

    public SearchService()
    {
        log.debug("Initializing SearchService...");

        IntactSession intactSession = new WebServiceSession();
        IntactConfigurator.initIntact(intactSession);
        daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    /**
     * Finds all interaction partners for given list of protein IDs (only UniProt IDs are supported at present)
     *
     * @param proteinIds
     * @return
     */
    public PartnerResult[] findPartnersUsingUniprotIds(String[] proteinIds)  {
        if (log.isDebugEnabled())
        {
            if (proteinIds.length == 1)
            {
                log.debug("Finding partners for: "+proteinIds[0]);
            }
            else
            {
                log.debug("Finding partners for an array of "+proteinIds.length+" protein IDs");
            }
        }

        List<PartnerResult> results = new ArrayList<PartnerResult>(proteinIds.length);

        int totalFound = 0;

        for (String uniprotId : proteinIds)
        {
            ProteinImpl prot = daoFactory.getProteinDao().getByXref(uniprotId);

            List<String> protIds = daoFactory.getProteinDao().getPartnersUniprotIdsByProteinAc(prot.getAc());

            totalFound = totalFound+protIds.size();

            results.add(new PartnerResult(uniprotId, protIds.toArray(new String[protIds.size()])));
        }

        IntactContext.getCurrentInstance().getDataContext().commitAllActiveTransactions();

        if (log.isDebugEnabled())
        {
            log.debug("Total partners found: "+totalFound);
        }

        return results.toArray(new PartnerResult[results.size()]);
    }

    public String getVersion()
    {
        return version();
    }

    public static String version()
    {
        // Version
        Properties properties = new Properties();
        try
        {
            properties.load(SearchService.class.getResourceAsStream("/uk/ac/ebi/intact/search/ws/BuildInfo.properties"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String version = properties.getProperty("build.version");

        return version;
    }

}
