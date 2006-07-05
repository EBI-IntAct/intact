/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.search3.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.search3.util.UrlCheckerThread;

/**
 * UrlCheckerThread Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/07/2006</pre>
 */
public class UrlCheckerThreadTest extends TestCase {

    public UrlCheckerThreadTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( UrlCheckerThreadTest.class );
    }

    ////////////////////////////
    // Utility

    private boolean performTestManyTimes( final String url, final boolean correct ) {

        for ( int run = 10; run > 0; run-- ) {
            try {
                UrlCheckerThread urlCheckerThread = new UrlCheckerThread( url );
                urlCheckerThread.start();
                int max = 20;
                while ( max > 0 & ( ! urlCheckerThread.hasFinished( 300 ) ) ) {
                    max--;
                }

                // once finished or used all 10 try, we should get the answer.
                assertEquals( correct, urlCheckerThread.isValidUrl() );

            } catch ( Exception e ) {
                fail();
                e.printStackTrace();
            }
        } // for

        return true;
    }

    /////////////////////
    // Tests

    public void testRunUrlCheck() {
        // this one does not work as Google seems to use redirection for its home page.
//        assertTrue( performTestManyTimes( "http://www.google.com", true ) );
        assertTrue( performTestManyTimes( "http://www.g_o_o_g_l_e.com", false ) );

        assertTrue( performTestManyTimes( "ftp://ftp.ebi.ac.uk/pub/databases/intact/current/psi1/pmid/2004/10052460.xml", true ) );
        assertTrue( performTestManyTimes( "ftp://ftp.ebi.ac.uk/pub/databases/intact/current/psi1/pmid/2004/10052460.zip", true ) );
        assertTrue( performTestManyTimes( "ftp://ftp.ebi.ac.uk/pub/databases/intact/current/xml/ant__ma_small.xml", false ) );

        assertTrue( performTestManyTimes( "http://www.ebi.ac.uk/~skerrien/intact/current/psi1/pmid/2005/1715582.zip", true ) );
        assertTrue( performTestManyTimes( "http://www.ebi.ac.uk/~skerrien/intact/current/psi1/pmid/2005/1715__582.zip", false ) );

        // ftp://ftp.ebi.ac.uk/pub/databases/intact/current/xml/ant__ma_small.xml
    }

    public void testRunSimultaneousCheck() {

        final int MAX_THREAD = 10;

        UrlCheckerThread[] array = new UrlCheckerThread[ MAX_THREAD ];
        for ( int i = 0; i < array.length; i++ ) {
            array[ i ] = new UrlCheckerThread( "http://www.ebi.ac.uk" );
            array[ i ].start();
        }

        int notFinishedCount = 1;
        int count = 0;
        while ( notFinishedCount > 0 ) {

            count++;
//            System.out.println( "Checking if all threads have finished (" + count + ")..." );
            notFinishedCount = array.length;

            for ( int i = 0; i < array.length; i++ ) {
                UrlCheckerThread urlCheckerThread = array[ i ];
                boolean finished = urlCheckerThread.hasFinished();
//                System.out.println( i + ") finished = " + finished );
                if ( finished ) {
                    notFinishedCount--;
                }
            }

            try {
                Thread.currentThread().sleep( 10 ); // be a bit patient ...
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }

        for ( int i = 0; i < array.length; i++ ) {
            UrlCheckerThread urlCheckerThread = array[ i ];
            assertTrue( urlCheckerThread.isValidUrl() );
        }
    }

    public void testWait() {
        UrlCheckerThread urlCheckerThread = new UrlCheckerThread( "http://www.ebi.ac.uk" );
        urlCheckerThread.start();
        long start = System.currentTimeMillis();
        final int wait = 30 * 1000;
        urlCheckerThread.hasFinished( wait );
        long stop = System.currentTimeMillis();
        assertTrue( ( start - stop ) < wait );
    }
}