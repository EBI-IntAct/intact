package uk.ac.ebi.intact.search.wsclient;

import junit.framework.TestCase;

import java.rmi.RemoteException;

public class SearchClientTest extends TestCase
{

    public void testClient()
    {
       SearchServiceClient client = new SearchServiceClient("http://localhost:8080/search-ws/services/SearchService");

        try
        {
            String[] uniprotIds = client.findPartnersUsingUniprotIds("Q9VZ59");

            for (String id : uniprotIds)
            {
                System.out.println(id);
            }
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }


    }

}
