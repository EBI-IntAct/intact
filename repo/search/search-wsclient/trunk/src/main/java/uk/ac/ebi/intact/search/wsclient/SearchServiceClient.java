/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.search.wsclient;

import uk.ac.ebi.intact.search.wsclient.generated.PartnerResult;
import uk.ac.ebi.intact.search.wsclient.generated.Search;
import uk.ac.ebi.intact.search.wsclient.generated.SearchAccessServiceLocator;
import uk.ac.ebi.intact.search.wsclient.generated.InteractionInfo;

import java.net.URL;
import java.rmi.RemoteException;

/**
 * Client for the Search Web Service
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Aug-2006</pre>
 */
public class SearchServiceClient
{

    private static final String DEFAULT_URL = "http://www.ebi.ac.uk/intact/search-ws/services/SearchService";

    /**
     * Stub to handle the search web service
     */
    private Search search;

    /**
     * Prepare the web service.
     */
    public SearchServiceClient()
    {

        try
        {
            SearchAccessServiceLocator serviceLocator = new SearchAccessServiceLocator();
            serviceLocator.setMaintainSession(true);

            this.search = serviceLocator.getSearchService(new URL(DEFAULT_URL));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    } // constructor

    public SearchServiceClient(String searchWsUrl)
    {

        try
        {
            SearchAccessServiceLocator serviceLocator = new SearchAccessServiceLocator();
            serviceLocator.setMaintainSession(true);

            this.search = serviceLocator.getSearchService(new URL(searchWsUrl));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    } // constructor

    /**
     * Finds the Uniprot IDs of the partners for the provided list of Uniprot IDs
     *
     * @param proteinIds The Uniprot IDs to look for
     * @return An array of ParnterResult, one for each provided proteinId. In the PartnerResult we
     *         can get the list of Uniprot IDs for the proteins that interact with the provided protein
     * @throws RemoteException
     */
    public PartnerResult[] findPartnersUsingUniprotIds(String[] proteinIds) throws RemoteException
    {
        return search.findPartnersUsingUniprotIds(proteinIds);
    }


    /**
     * Finds the Uniprot IDs of the partners (the proteins which the protein provided interact)
     * for the provided a Uniprot IDs
     *
     * @param proteinIds The Uniprot IDs to look for
     * @return The list of Uniprot IDs for the proteins that interact with the provided protein
     * @throws RemoteException
     */
    public String[] findPartnersUsingUniprotIds(String proteinIds) throws RemoteException
    {
        return search.findPartnersUsingUniprotIds(new String[]{proteinIds})[0].getPartnerUniprotAcs();
    }

    /**
     * Gets the information of the interactions that the pair of proteins provided have in common
     * @param id1 Uniprot ID for protein 1
     * @param id2 Uniprot ID for protein 2
     * @return Array of <code>Interaction</code> with interaction information
     * @throws RemoteException
     */
    public InteractionInfo[] getInteractionInfoUsingUniprotIds(String id1, String id2) throws RemoteException
    {
        return search.getInteractionInfoUsingUniprotIds(id1, id2);
    }

    /**
     * Gets the information of the interactions that the pair of proteins provided have in common
     * @param id1 IntAct AC for protein 1
     * @param id2 IntAct AC for protein 2
     * @return Array of <code>Interaction</code> with interaction information
     * @throws RemoteException
     */
    public InteractionInfo[] getInteractionInfoUsingIntactIds(String id1, String id2) throws RemoteException
    {
        return search.getInteractionInfoUsingIntactIds(id1, id2);
    }

    public String getServiceVersion() throws RemoteException
    {
        return search.getVersion();
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("IntAct Search Web Service Client\nWS Version: "+new SearchServiceClient().getServiceVersion());
    }

}
