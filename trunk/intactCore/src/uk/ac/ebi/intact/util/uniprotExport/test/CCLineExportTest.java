/*
Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/

package uk.ac.ebi.intact.util.uniprotExport.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.uniprotExport.CcLine;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CCLineExportTest extends TestCase {

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( CCLineExportTest.class );
    }

    /////////////////////////////////////////
    // Check on the ordering of the CC lines

    public void testCCLinesOrdering() {

        // create a collection of CC Lines to order
        List ccLines = new LinkedList();


        // Note: ASCII( 'a' )=65, and ASCII( 'A' )=97

        ccLines.add( new CcLine( "blablabla", "abCDef" ) );
        ccLines.add( new CcLine( "blablabla", "abcdef" ) );
        ccLines.add( new CcLine( "blablabla", "Self" ) );
        ccLines.add( new CcLine( "blablabla", "fedcba" ) );
        ccLines.add( new CcLine( "blablabla", "aBcdEf" ) );
        ccLines.add( new CcLine( "blablabla", "aBCdef" ) );

        assertEquals( 6, ccLines.size() );

        System.out.println( "Before:" );
        for( Iterator iterator = ccLines.iterator(); iterator.hasNext(); ) {
            CcLine ccLine = (CcLine) iterator.next();
            System.out.println( ccLine.getGeneName() );
        }

        Collections.sort( ccLines );

        assertEquals( 6, ccLines.size() );

        System.out.println( "After:" );
        for( Iterator iterator = ccLines.iterator(); iterator.hasNext(); ) {
            CcLine ccLine = (CcLine) iterator.next();
            System.out.println( ccLine.getGeneName() );
        }

        // check the ordering
        assertEquals( "Self", ( (CcLine) ccLines.get( 0 ) ).getGeneName() );
        assertEquals( "abCDef", ( (CcLine) ccLines.get( 1 ) ).getGeneName() );
        assertEquals( "abcdef", ( (CcLine) ccLines.get( 2 ) ).getGeneName() );
        assertEquals( "aBcdEf", ( (CcLine) ccLines.get( 3 ) ).getGeneName() );
        assertEquals( "aBCdef", ( (CcLine) ccLines.get( 4 ) ).getGeneName() );
        assertEquals( "fedcba", ( (CcLine) ccLines.get( 5 ) ).getGeneName() );
    }


    public void testCCLinesOrdering_2() {

        // create a collection of CC Lines to order
        List ccLines = new LinkedList();


        // Note: ASCII( 'a' )=65, and ASCII( 'A' )=97

        ccLines.add( new CcLine( "blablabla", "abCDef" ) );
        ccLines.add( new CcLine( "blablabla", "abcdef" ) );
        ccLines.add( new CcLine( "blablabla", "fedcba" ) );
        ccLines.add( new CcLine( "blablabla", "aBcdEf" ) );
        ccLines.add( new CcLine( "blablabla", "aBCdef" ) );

        assertEquals( 5, ccLines.size() );

        System.out.println( "Before:" );
        for( Iterator iterator = ccLines.iterator(); iterator.hasNext(); ) {
            CcLine ccLine = (CcLine) iterator.next();
            System.out.println( ccLine.getGeneName() );
        }

        Collections.sort( ccLines );

        assertEquals( 5, ccLines.size() );

        System.out.println( "After:" );
        for( Iterator iterator = ccLines.iterator(); iterator.hasNext(); ) {
            CcLine ccLine = (CcLine) iterator.next();
            System.out.println( ccLine.getGeneName() );
        }

        // check the ordering
        assertEquals( "abCDef", ( (CcLine) ccLines.get( 0 ) ).getGeneName() );
        assertEquals( "abcdef", ( (CcLine) ccLines.get( 1 ) ).getGeneName() );
        assertEquals( "aBcdEf", ( (CcLine) ccLines.get( 2 ) ).getGeneName() );
        assertEquals( "aBCdef", ( (CcLine) ccLines.get( 3 ) ).getGeneName() );
        assertEquals( "fedcba", ( (CcLine) ccLines.get( 4 ) ).getGeneName() );
    }
}