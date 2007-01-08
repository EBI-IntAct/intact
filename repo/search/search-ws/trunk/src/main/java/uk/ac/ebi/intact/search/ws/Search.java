/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.search.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.context.DataContext;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.WebServiceSession;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.svc.impl.SimpleSearchService;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
@WebService(name="Search", targetNamespace = "http://ebi.ac.uk/intact/search/wsclient/generated")
public class Search
{

    private static final Log log = LogFactory.getLog(Search.class);


    public Search()
    {
        log.debug("Initializing Search...");

        IntactSession intactSession = new WebServiceSession();
        IntactConfigurator.initIntact(intactSession);
    }
    
    @WebMethod()
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

    @WebMethod()
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
    @WebMethod()
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
        int intactProtsFound = 0;

        DaoFactory daoFactory = getDataContext().getDaoFactory();

        for (String uniprotId : proteinIds)
        {
            List<ProteinImpl> protsWithThisUniprotId = daoFactory.getProteinDao().getByUniprotId(uniprotId);

            intactProtsFound = protsWithThisUniprotId.size();

            for (ProteinImpl prot : protsWithThisUniprotId)
            {
                List<String> protIds;

                if (prot != null)
                {
                    protIds = daoFactory.getProteinDao().getPartnersUniprotIdsByProteinAc(prot.getAc());
                }
                else
                {
                    protIds = Collections.EMPTY_LIST;
                }

                totalFound = totalFound+protIds.size();

                results.add(new PartnerResult(uniprotId, protIds.toArray(new String[protIds.size()])));
            }
        }

        getDataContext().commitTransaction();

        if (log.isDebugEnabled())
        {
            log.debug("Total IntAct prots found: "+intactProtsFound);
            log.debug("Total partners found: "+totalFound);
        }

        return results.toArray(new PartnerResult[results.size()]);
    }

    @WebMethod
    public int countExperimentsUsingIntactQuery(String query)
    {
        return new SimpleSearchService().count(Experiment.class, query);
    }

    @WebMethod
    public int countProteinsUsingIntactQuery(String query)
    {
        return new SimpleSearchService().count(ProteinImpl.class, query);
    }

    @WebMethod
    public int countNucleicAcidsUsingIntactQuery(String query)
    {
        return new SimpleSearchService().count(NucleicAcidImpl.class, query);
    }

    @WebMethod
    public int countSmallMoleculesUsingIntactQuery(String query)
    {
        return new SimpleSearchService().count(SmallMoleculeImpl.class, query);
    }

    @WebMethod
    public int countInteractionsUsingIntactQuery(String query)
    {
        return new SimpleSearchService().count(InteractionImpl.class, query);
    }

    @WebMethod
    public int countCvObjectsUsingIntactQuery(String query)
    {
        return new SimpleSearchService().count(CvObject.class, query);
    }

    @WebMethod
    public int countAllBinaryInteractions()
    {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        int componentCount = daoFactory.getComponentDao().countAll();
        int interactionCount = daoFactory.getInteractionDao().countAll();

        return (componentCount - interactionCount);
    }

    @WebMethod
    public List<SimpleResult> searchExperimentsUsingQuery(String query, Integer firstResult, Integer maxResults)
    {
        return searchUsingQuery(query, new Class[] {Experiment.class}, firstResult, maxResults);
    }

    @WebMethod
    public List<SimpleResult> searchProteinsUsingQuery(String query, Integer firstResult, Integer maxResults)
    {
        return searchUsingQuery(query, new Class[] {ProteinImpl.class}, firstResult, maxResults);
    }

    @WebMethod
    public List<SimpleResult> searchNucleicAcidsUsingQuery(String query, Integer firstResult, Integer maxResults)
    {
        return searchUsingQuery(query, new Class[] {NucleicAcidImpl.class}, firstResult, maxResults);
    }

    @WebMethod
    public List<SimpleResult> searchSmallMoleculesUsingQuery(String query, Integer firstResult, Integer maxResults)
    {
        return searchUsingQuery(query, new Class[] {SmallMoleculeImpl.class}, firstResult, maxResults);
    }

    @WebMethod
    public List<SimpleResult> searchInteractionsUsingQuery(String query, Integer firstResult, Integer maxResults)
    {
        return searchUsingQuery(query, new Class[] {InteractionImpl.class}, firstResult, maxResults);
    }

    @WebMethod
    public List<SimpleResult> searchCvObjectsUsingQuery(String query, Integer firstResult, Integer maxResults)
    {
        return searchUsingQuery(query, new Class[] {CvObject.class}, firstResult, maxResults);
    }

    @WebMethod
    public List<SimpleResult> searchUsingQuery(String query, String[] searchableTypes, Integer firstResult, Integer maxResults)
    {
        Class<? extends Searchable>[] searchables = new Class[searchableTypes.length];

        for (int i=0; i<searchableTypes.length; i++)
        {
            try
            {
                searchables[i] = (Class<? extends Searchable>) Class.forName(searchableTypes[i]);
            }
            catch (ClassNotFoundException e)
            {
                throw new IntactException(e);
            }
        }

        return searchUsingQuery(query, searchables, firstResult, maxResults);
    }

    private List<SimpleResult> searchUsingQuery(String query, Class<? extends Searchable>[] searchables, Integer firstResult, Integer maxResults)
    {
        List<SimpleResult> results = new ArrayList<SimpleResult>();
        if (firstResult == null) firstResult = 0;
        if (maxResults == null) maxResults = Integer.MAX_VALUE;

        int currentResultsNum = 0;

        for (Class<? extends Searchable> searchable : searchables)
        {
            results.addAll(searchUsingQuery(query, searchable, firstResult, maxResults-currentResultsNum));
            currentResultsNum = currentResultsNum + results.size();

            if (currentResultsNum == maxResults)
            {
                break;
            }
        }

        return results;
    }

    private List<SimpleResult> searchUsingQuery(String query, Class<? extends Searchable> searchable, Integer firstResult, Integer maxResults) 
    {
        List<SimpleResult> results = new ArrayList<SimpleResult>();

        List res = new SimpleSearchService().search(searchable, query, firstResult, maxResults);

        for (Object result : res)
        {
            AnnotatedObject ao = (AnnotatedObject) result;
            results.add(new SimpleResult(ao.getAc(), ao.getShortLabel(), ao.getFullName(), ao.getClass().getName()));
        }

        return results;
    }

    @WebMethod()
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
            properties.load(Search.class.getResourceAsStream("/uk/ac/ebi/intact/search/ws/BuildInfo.properties"));
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
