package uk.ac.ebi.intact.search.wsclient;

import junit.framework.TestCase;
import uk.ac.ebi.intact.search.wsclient.generated.Search;
import uk.ac.ebi.intact.search.wsclient.generated.SearchService;
import uk.ac.ebi.intact.search.wsclient.generated.InteractionInfo;

import java.util.List;


public class SearchClientTest extends TestCase
{

    private static final String LOCALHOST_URL = "http://localhost:8080/search-ws/search?wsdl";

    public void testFindPartnersUsingUniprotIds() throws Exception
    {
        SearchServiceClient client = new SearchServiceClient();
        List<String> uniprotIds = client.findPartnersUsingUniprotIds("Q9VZ59");

        for (String id : uniprotIds)
        {
            System.out.println(id);
        }
    }

    public void testGetInteractionInfoUsingIntactIds() throws Exception
    {
        SearchServiceClient client = new SearchServiceClient();
        List<InteractionInfo> interInfos = client.getInteractionInfoUsingIntactIds("EBI-1004115","EBI-710997");

        for (InteractionInfo interInfo : interInfos)
        {
            System.out.println(interInfo.getIntactAc());
        }

    }

    public void testGetInteractionInfoUsingUniprotIds() throws Exception
    {
        SearchServiceClient client = new SearchServiceClient();
        List<InteractionInfo> interInfos = client.getInteractionInfoUsingUniprotIds("Q15691","P54274");

        for (InteractionInfo interInfo : interInfos)
        {
            System.out.println(interInfo.getIntactAc());
        }

    }
     

    public void testServiceVersion() throws Exception
    {
        SearchServiceClient client = new SearchServiceClient();
        System.out.println(client.getServiceVersion());
    }
}
