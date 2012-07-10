package uk.ac.ebi.intact.bridges.ontology_manager.impl.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.ontology_manager.client.IntactFilterOlsClient;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Unit tester of IntactFilterOlsClient
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/11/11</pre>
 */

public class IntactFilterOlsClientTest {

    private IntactFilterOlsClient olsClient;

    @Before
    public void initializeOlsClient() throws MalformedURLException, ServiceException {
        olsClient = new IntactFilterOlsClient();
    }

    @Test
    public void test_filter_obsolete() throws RemoteException {
        Assert.assertTrue(olsClient.isObsolete("MOD:00001", "MI"));
    }

    @Test
    public void test_filter_children() throws RemoteException {
        Map children = olsClient.getTermChildren("MI:0252", "MI", 1, null);

        Assert.assertEquals(4, children.size());

        for (Object key : children.keySet()){
            String id = (String) key;

            if (!id.equals("MI:0117") && !id.equals("MI:0118") && !id.equals("MI:0828") && !id.equals("MI:1175") ){
                Assert.assertFalse(true);
            }
        }
    }

    @Test
    public void test_filter_term() throws RemoteException {
        Assert.assertNull(olsClient.getTermById("MOD:00001", "MI"));
    }

    @Test
    public void test_filter_metadata() throws RemoteException {
        Assert.assertTrue(olsClient.getTermMetadata("MOD:00001", "MI").isEmpty());
    }

    @Test
    public void test_filter_xrefs() throws RemoteException {
        Assert.assertNotNull(olsClient.getTermXrefs("MOD:00001", "MI").isEmpty());
    }
}
