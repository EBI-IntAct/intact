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
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

import java.io.IOException;
import java.io.InputStream;

public class GoServerProxyTest extends TestCase {


    private static final String NEW_LINE = System.getProperty( "line.separator" );


    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public GoServerProxyTest( String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( GoServerProxyTest.class );
    }


    private GoServerProxy server;

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {

        this.server = new GoServerProxy();
    }

    public static final String GO0000074 = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" + NEW_LINE +
                                           "<page>" + NEW_LINE +
                                           "  <name>" + NEW_LINE +
                                           "     regulation of cell cycle" + NEW_LINE +
                                           "  </name>" + NEW_LINE +
                                           "  <category>" + NEW_LINE +
                                           "     process" + NEW_LINE +
                                           "  </category>" + NEW_LINE +
                                           "</page>";

    public static final String GO0008645 = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" + NEW_LINE +
                                           "<page>" + NEW_LINE +
                                           "  <name>" + NEW_LINE +
                                           "     hexose transport" + NEW_LINE +
                                           "  </name>" + NEW_LINE +
                                           "  <category>" + NEW_LINE +
                                           "     process" + NEW_LINE +
                                           "  </category>" + NEW_LINE +
                                           "</page>";


    public void testGetGoResponseFromInputStream() {

        GoServerProxy.GoResponse response = null;

        try {
            MockInputStream is = new MockInputStream();
            is.setBuffer( GO0000074 );
            response = server.query( (InputStream) is, "GO:0000074" );

            assertEquals( response.getGoId(),     "GO:0000074" );
            assertEquals( response.getName(),     "regulation of cell cycle" );
            assertEquals( response.getCategory(), "process" );
        } catch( IOException ex ) {
            fail( "Could not connect to the GO server !" );
        } catch( GoServerProxy.GoIdNotFoundException ex ) {
            fail( "Could not find GO:0000074" );
        }

        // Test 2: existing GO term.
        try {
            response = server.query( "GO:0008645" );

            MockInputStream is = new MockInputStream();
            is.setBuffer( GO0008645 );
            response = server.query( (InputStream) is, "GO:0008645" );

            assertEquals( response.getGoId(),     "GO:0008645" );
            assertEquals( response.getName(),     "hexose transport" );
            assertEquals( response.getCategory(), "process" );
        } catch( IOException ex ) {
            fail( "Could not connect to the GO server !" );
        } catch( GoServerProxy.GoIdNotFoundException ex ) {
            fail( "Could not find GO:0008645" );
        }
    }



    public void testGetGoResponseWithUrl() {
        getGoResponse();
    }

    private void getGoResponse() {
        GoServerProxy.GoResponse response = null;

        // Test 1: existing GO term.
        try {
            response = server.query( "GO:0000074" );

            assertEquals( response.getGoId(),     "GO:0000074" );
            assertEquals( response.getName(),     "regulation of cell cycle" );
            assertEquals( response.getCategory(), "process" );
        } catch( IOException ex ) {
            fail( "Could not connect to the GO server !" );
        } catch( GoServerProxy.GoIdNotFoundException ex ) {
            fail( "Could not find GO:0000074" );
        }

        // Test 2: existing GO term.
        try {
            response = server.query( "GO:0008645" );

            assertEquals( response.getGoId(),     "GO:0008645" );
            assertEquals( response.getName(),     "hexose transport" );
            assertEquals( response.getCategory(), "process" );
        } catch( IOException ex ) {
            fail( "Could not connect to the GO server !" );
        } catch( GoServerProxy.GoIdNotFoundException ex ) {
            fail( "Could not find GO:0008645" );
        }

        // CA BUG ICI !!!!!!!!!!!!!

        // Test 3: no matches.
        try {
            response = server.query( "GO:0000000" );
            // We shouldn't get here as there are no matches for this.
            fail( "Found GO:0000000 where it should have crashed !" );
        } catch( IOException ex ) {
            fail( "Could not connect to the GO server !" );
        } catch( GoServerProxy.GoIdNotFoundException ex ) {
            assertTrue( true );
        }

        // Test 4: wrong URL.
        String url = "http://www.ebi.ac.uk/ego/DisplayGoTermXYZ";
        server = new GoServerProxy( url );
        try {
            response = server.query( "GO:0000074" );
            // Invalid URL.
            fail( "Succeded to parse the URL: " + url + " where it should have crashed !" );
        } catch( IOException ex ) {
            assertTrue( true );
        } catch( GoServerProxy.GoIdNotFoundException ex ) {
            fail( "Succeded to parse the URL: " + url + " where it should have crashed !" );
        }
    }
}
