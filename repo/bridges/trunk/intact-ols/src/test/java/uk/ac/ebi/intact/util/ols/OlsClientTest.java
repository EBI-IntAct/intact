package uk.ac.ebi.intact.util.ols;

import junit.framework.TestCase;


public class OlsClientTest extends TestCase
{
    private OlsClient client;

    protected void tearDown() throws Exception
    {
        super.tearDown();
        client = null;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        client = new OlsClient();
    }

    
     


}
