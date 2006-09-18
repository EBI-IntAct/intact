package uk.ac.ebi.intact.search.wsclient;

import junit.framework.TestCase;

import java.rmi.RemoteException;

import uk.ac.ebi.intact.search.wsclient.generated.InteractionInfo;

public class SearchClientTest extends TestCase
{

    public void testFindPartnersUsingUniprotIds() throws Exception
    {
        SearchServiceClient client = new SearchServiceClient();
        String[] uniprotIds = client.findPartnersUsingUniprotIds("Q9VZ59");

        for (String id : uniprotIds)
        {
            System.out.println(id);
        }
    }

    public void testGetInteractionInfoUsingIntactIds() throws Exception
    {
        SearchServiceClient client = new SearchServiceClient();
        InteractionInfo[] interInfos = client.getInteractionInfoUsingIntactIds("EBI-1004115","EBI-710997");

        for (InteractionInfo interInfo : interInfos)
        {
            System.out.println(interInfo.getIntactAc());
        }

    }

    public void testGetInteractionInfoUsingUniprotIds() throws Exception
    {
        SearchServiceClient client = new SearchServiceClient();
        InteractionInfo[] interInfos = client.getInteractionInfoUsingUniprotIds("Q15691","P54274");

        for (InteractionInfo interInfo : interInfos)
        {
            System.out.println(interInfo.getIntactAc());
        }

    }

}
