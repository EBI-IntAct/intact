/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.NewtServerProxy;
import uk.ac.ebi.intact.persistence.SearchException;

import java.net.URL;
import java.io.IOException;

public class NewtServerProxyTest extends TestCase {


    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     * @param name the name of the test.
     */
    public NewtServerProxyTest(String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(NewtServerProxyTest.class);
    }

    public void testGetNewtResponse() {
        try {
            getNewResponse();
        }
        catch (IOException ex) {
            fail(ex.getMessage());
        }
        catch (SearchException ex) {
            fail(ex.getMessage());
        }
    }

    private void getNewResponse() throws IOException, SearchException {
        URL url = new URL("http://web7-node1.ebi.ac.uk:9120/newt/display");
        // The server to connect to.
        NewtServerProxy server = new NewtServerProxy(url);
        NewtServerProxy.NewtResponse response = server.query(45009);
        // No short label for 45009.
        assertTrue(!response.hasShortLabel());
        assertEquals(response.getFullName(), "unidentified algae B0m10");

        response = server.query(48509);
        // No short label for 48509.
        assertTrue(!response.hasShortLabel());
        assertEquals(response.getFullName(), "environmental samples");

        response = server.query(9606);
        assertEquals(response.getShortLabel(), "HUMAN");
        assertEquals(response.getFullName(), "Homo sapiens");

        try {
            response = server.query(8);
            // We shouldn't get here as there are no matches for this.
            assertFalse(true);
        }
        catch (SearchException ex) {
            // This should throw an exception.
            assertTrue(true);
        }
        // With the wrong URL.
        url = new URL("http://web7-node1.ebi.ac.uk:9120/newt/display1");
        server = new NewtServerProxy(url);
        try {
            response = server.query(45009);
            // Invalid URL.
        }
        catch (IOException ex) {
            assertTrue(true);
        }
    }
}
