/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.search.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Transaction;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.impl.WebServiceSession;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.dao.IntactTransaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

    //private DaoFactory daoFactory;

    public SearchService()
    {
        log.debug("Initializing SearchService...");

        IntactSession intactSession = new WebServiceSession();
        IntactConfigurator.initIntact(intactSession);
        //daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
    }

    public InteractionInfo[] getInteractionInfoUsingUniprotIds(String uniprotId1, String uniprotId2)
    {
        DaoFactory daoFactory = getDataContext().getDaoFactory();

        List<ProteinImpl> protsForId1 = daoFactory.getProteinDao().getByUniprotId(uniprotId1);
        List<ProteinImpl> protsForId2 = daoFactory.getProteinDao().getByUniprotId(uniprotId2);

        List<InteractionInfo> interInfos = new ArrayList<InteractionInfo>();

        for (Protein prot1 : protsForId1)
        {
             for (Protein prot2 : protsForId2)
             {
                 InteractionInfo[] results = getInteractionInfoUsingIntactIds(prot1.getAc(), prot2.getAc());
                 for (InteractionInfo result : results)
                 {
                     interInfos.add(result);
                 }
             }
        }

        getDataContext().commitTransaction();

        return interInfos.toArray(new InteractionInfo[interInfos.size()]);
    }

    public InteractionInfo[] getInteractionInfoUsingIntactIds(String id1, String id2)
    {
        DaoFactory daoFactory = getDataContext().getDaoFactory();

        List<Interaction> interactions = daoFactory.getInteractionDao().getInteractionsForProtPair(id1,id2);

        List<InteractionInfo> interInfos = new ArrayList<InteractionInfo>();

        for (Interaction inter : interactions)
        {
            String intactAc = inter.getAc();
            String shortName = inter.getShortLabel();
            String fullName = inter.getFullName();

            CvInteractionType type = inter.getCvInteractionType();
            String interactionType = type.getShortLabel();
            String description = type.getFullName();

            // annotation definition
            String definition = null;

            for (Annotation annotation : type.getAnnotations())
            {
                if (annotation.getCvTopic().getShortLabel().equals(CvTopic.DEFINITION))
                {
                    definition = annotation.getAnnotationText();
                    break;
                }
            }

            InteractionInfo interInfo = new InteractionInfo(intactAc, shortName, fullName, interactionType, description, definition);
            interInfos.add(interInfo);
        }

        getDataContext().commitTransaction();

        return interInfos.toArray(new InteractionInfo[interInfos.size()]);
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

        DaoFactory daoFactory = getDataContext().getDaoFactory();

        for (String uniprotId : proteinIds)
        {
            ProteinImpl prot = daoFactory.getProteinDao().getByXref(uniprotId);

            List<String> protIds = daoFactory.getProteinDao().getPartnersUniprotIdsByProteinAc(prot.getAc());

            totalFound = totalFound+protIds.size();

            results.add(new PartnerResult(uniprotId, protIds.toArray(new String[protIds.size()])));
        }

        getDataContext().commitTransaction();

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

    private static DataContext getDataContext()
    {
        return IntactContext.getCurrentInstance().getDataContext();
    }

}
