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
            // Uncomment this line to find out where it went wrong!
            //ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private void getNewResponse() throws IOException {
        URL url = new URL("http://www.ebi.ac.uk/newt/display");
        // The server to connect to.
        NewtServerProxy server = new NewtServerProxy(url);
        NewtServerProxy.NewtResponse response = null;

        // Test: has full name but no short label.
        try {
            response = server.query(45009);
            // No short label for 45009.
            assertTrue(!response.hasShortLabel());
            assertEquals(response.getFullName(), "unidentified algae B0m10");
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            fail();
        }

        // Test: has full name but no short label (another one).
        try {
            response = server.query(48509);
            // No short label for 48509.
            assertTrue(!response.hasShortLabel());
            assertEquals(response.getFullName(), "environmental samples");
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            fail();
        }

        // Test: has full name and short label.
        try {
            response = server.query(9606);
            assertEquals(response.getShortLabel(), "HUMAN");
            assertEquals(response.getFullName(), "Homo sapiens");
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            fail();
        }

        // Test: has full name but no short label.
        try {
            response = server.query(41057);
            // short label has to be replaced by the taxid
            assertEquals(response.getShortLabel(), "41057");
            assertEquals(response.getFullName(), "Aspergillus unilateralis");
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            fail();
        }

        // Test: no matches.
        try {
            response = server.query(8);
            // We shouldn't get here as there are no matches for this.
            fail();
        }
        catch (NewtServerProxy.TaxIdNotFoundException e) {
            assertTrue(true);
        }

        // Test: For obsolete tax id
        try {
            response = server.query(38081);
            assertEquals(response.getTaxId(), 4754);
            assertEquals(response.getShortLabel(), "PNECA");
            assertEquals(response.getFullName(), "Pneumocystis carinii");
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            fail();
        }

        // Test: another test for obsolete tax id.
        try {
            response = server.query(5072);
            assertEquals(response.getTaxId(), 162425);
            assertEquals(response.getShortLabel(), "EMENI");
            assertEquals(response.getFullName(), "Emericella nidulans");
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            fail();
        }

        // Test: wrong URL.
        url = new URL("http://web7-node1.ebi.ac.uk:9120/newt/display1");
        server = new NewtServerProxy(url);
        try {
            response = server.query(45009);
            // Invalid URL.
            fail();
        }
        catch (IOException ex) {
            assertTrue(true);
        }
        catch (NewtServerProxy.TaxIdNotFoundException ex) {
            fail();
        }
    }
}
