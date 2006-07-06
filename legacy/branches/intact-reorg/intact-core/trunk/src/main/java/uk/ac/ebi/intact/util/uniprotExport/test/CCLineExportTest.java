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

        ccLines.add( new CcLine( "blablabla", "abCDef", "" ) );
        ccLines.add( new CcLine( "blablabla", "abcdef", "" ) );
        ccLines.add( new CcLine( "blablabla", "Self", "" ) );
        ccLines.add( new CcLine( "blablabla", "fedcba", "" ) );
        ccLines.add( new CcLine( "blablabla", "aBcdEf", "" ) );
        ccLines.add( new CcLine( "blablabla", "aBCdef", "" ) );

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

        ccLines.add( new CcLine( "blablabla", "abCDef", "" ) );
        ccLines.add( new CcLine( "blablabla", "abcdef", "" ) );
        ccLines.add( new CcLine( "blablabla", "fedcba", "" ) );
        ccLines.add( new CcLine( "blablabla", "aBcdEf", "" ) );
        ccLines.add( new CcLine( "blablabla", "aBCdef", "" ) );

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

    public void testCCLinesOrdering_3() {

        // create a collection of CC Lines to order
        List ccLines = new LinkedList();


        // Note: ASCII( 'a' )=65, and ASCII( 'A' )=97

//        AC   P24343
//        CC   -!- INTERACTION:
//        CC       Q90888:-; NbExp=1; IntAct=EBI-445651, EBI-445772;
//        CC       Q92171:-; NbExp=2; IntAct=EBI-445651, EBI-445622;
//        CC       P18870:JUN; NbExp=1; IntAct=EBI-445651, EBI-445826;
//        CC       Q90595:MAFF; NbExp=1; IntAct=EBI-445651, EBI-445786;
//        CC       Q90889:MAFG; NbExp=1; IntAct=EBI-445651, EBI-445799;
//        CC       Q90596:MAFK; NbExp=1; IntAct=EBI-445651, EBI-445812;
//        CC       P23091:V-MAF (xeno); NbExp=1; IntAct=EBI-445651, EBI-445752;


        ccLines.add( new CcLine( "blablabla", "V-MAF", "P23091" ) );
        ccLines.add( new CcLine( "blablabla", "MAFF", "Q90595" ) );
        ccLines.add( new CcLine( "blablabla", "JUN", "P18870" ) );
        ccLines.add( new CcLine( "blablabla", "-", "Q90888" ) );
        ccLines.add( new CcLine( "blablabla", "MAFK", "Q90596" ) );
        ccLines.add( new CcLine( "blablabla", "MAFG", "Q90889" ) );
        ccLines.add( new CcLine( "blablabla", "-", "Q92171" ) );

        assertEquals( 7, ccLines.size() );

        System.out.println( "Before:" );
        for( Iterator iterator = ccLines.iterator(); iterator.hasNext(); ) {
            CcLine ccLine = (CcLine) iterator.next();
            System.out.println( ccLine.getGeneName() );
        }

        Collections.sort( ccLines );

        assertEquals( 7, ccLines.size() );

        System.out.println( "After:" );
        for( Iterator iterator = ccLines.iterator(); iterator.hasNext(); ) {
            CcLine ccLine = (CcLine) iterator.next();
            System.out.println( ccLine.getGeneName() );
        }

        // check the ordering
        assertEquals( "-", ( (CcLine) ccLines.get( 0 ) ).getGeneName() );
        assertEquals( "-", ( (CcLine) ccLines.get( 1 ) ).getGeneName() );
        assertEquals( "JUN", ( (CcLine) ccLines.get( 2 ) ).getGeneName() );
        assertEquals( "MAFF", ( (CcLine) ccLines.get( 3 ) ).getGeneName() );
        assertEquals( "MAFG", ( (CcLine) ccLines.get( 4 ) ).getGeneName() );
        assertEquals( "MAFK", ( (CcLine) ccLines.get( 5 ) ).getGeneName() );
        assertEquals( "V-MAF", ( (CcLine) ccLines.get( 6 ) ).getGeneName() );
    }
}