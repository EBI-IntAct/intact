/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import uk.ac.ebi.intact.util.GoServerProxy;

import java.io.IOException;

public class GoServerProxyTest extends TestCase {


    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     * @param name the name of the test.
     */
    public GoServerProxyTest(String name) {
        super(name);
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite(GoServerProxyTest.class);
    }

    public void testGetGoResponse() {
        try {
            getGoResponse();
        }
        catch (IOException ex) {
            // Uncomment this line to find out where it went wrong!
            ex.printStackTrace();
            fail(ex.getMessage());
        }
    }

    private void getGoResponse() throws IOException {
        // The server to connect to.
        GoServerProxy server = new GoServerProxy();
        GoServerProxy.GoResponse response = null;

        // Test 1: existing GO term.
        try {
            response = server.query( "GO:0000074" );

            assertEquals(response.getGoId(),     "GO:0000074");
            assertEquals(response.getName(),     "regulation of cell cycle");
            assertEquals(response.getCategory(), "process");
        }
        catch (IOException ex) {
            fail( "Could not connect to the GO server !" );
        }
        catch (GoServerProxy.GoIdNotFoundException ex) {
            fail( "Could not find GO:0000074" );
        }

        // Test 2: existing GO term.
        try {
            response = server.query( "GO:0008645" );

            assertEquals(response.getGoId(),     "GO:0008645");
            assertEquals(response.getName(),     "hexose transport");
            assertEquals(response.getCategory(), "process");
        }
        catch (IOException ex) {
            fail( "Could not connect to the GO server !" );
        }
        catch (GoServerProxy.GoIdNotFoundException ex) {
            fail( "Could not find GO:0008645" );
        }

        // Test 3: no matches.
        try {
            response = server.query( "GO:0000000" );
            // We shouldn't get here as there are no matches for this.
            fail( "Found GO:0000000 where it should have crashed !" );
        }
        catch (IOException ex) {
            fail( "Could not connect to the GO server !" );
        }
        catch (GoServerProxy.GoIdNotFoundException ex) {
            assertTrue( true );
        }

        // Test 4: wrong URL.
        String url = "http://www.ebi.ac.uk/ego/DisplayGoTermXYZ";
        server = new GoServerProxy( url );
        try {
            response = server.query( "GO:0000074" );
            // Invalid URL.
            fail( "Succeded to parse the URL: " + url + " where it should have crashed !" );
        }
        catch (IOException ex) {
            assertTrue( true );
        }
        catch (GoServerProxy.GoIdNotFoundException ex) {
            fail( "Succeded to parse the URL: " + url + " where it should have crashed !" );
        }
    }
}
