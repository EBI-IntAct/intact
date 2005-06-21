// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.persistence.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.business.IntactHelper;
import uk.ac.ebi.intact.model.CvFuzzyType;
import uk.ac.ebi.intact.model.Institution;
import uk.ac.ebi.intact.model.Range;

/**
 * The test class for the Range class.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public class RangeTest extends TestCase {

    /**
     * Constructs an instance with the specified name.
     *
     * @param name the name of the test.
     */
    public RangeTest( String name ) {
        super( name );
    }

    /**
     * Sets up the test fixture. Called before every test case method.
     */
    protected void setUp() {
        // Write setting up code for each test.
    }

    /**
     * Tears down the test fixture. Called after every test case method.
     */
    protected void tearDown() {
        // Release resources for after running a test.
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( uk.ac.ebi.intact.model.test.RangeTest.class );
    }

    /**
     * Tests the constructor.
     */
    public void testToString() {
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            doTestToString( helper );
        } catch ( Exception ex ) {
            ex.printStackTrace();
            fail( ex.getMessage() );
        } finally {
            if( helper != null ) {
                try {
                    helper.closeStore();
                } catch ( IntactException e ) {
                }
            }
        }
    }

    public void testSetSequenceRaw() {
        IntactHelper helper = null;
        try {
            helper = new IntactHelper();
            doTestSetSequenceRaw( helper );
        } catch ( Exception ex ) {
            ex.printStackTrace();
            fail( ex.getMessage() );
        } finally {
            if( helper != null ) {
                try {
                    helper.closeStore();
                } catch ( IntactException e ) {
                }
            }
        }
    }

    // Helper methods

    private void doTestToString( IntactHelper helper ) throws IntactException {

        CvFuzzyType lessThan = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.LESS_THAN );
        CvFuzzyType greaterThan = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.GREATER_THAN );
        CvFuzzyType undetermined = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.UNDETERMINED );
        CvFuzzyType range = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.RANGE );
        CvFuzzyType ct = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.C_TERMINAL );
        CvFuzzyType nt = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.N_TERMINAL );

        Range testRange = null;

        // Cache the institution.
        Institution inst = helper.getInstitution();

        // No fuzzy type
        testRange = new Range( inst, 2, 2, 3, 3, null );
        assertEquals( testRange.toString(), "2-3" );

        // No fuzzy type associated.
        assertNull( testRange.getFromCvFuzzyType() );
        assertNull( testRange.getToCvFuzzyType() );

        // < fuzzy type for from range
        testRange = new Range( inst, 2, 2, 3, 3, null );
        testRange.setFromCvFuzzyType( lessThan );

        assertEquals( testRange.toString(), "<2-3" );
        // Fuzzy type is greater for from type.
        assertEquals( testRange.getFromCvFuzzyType(), lessThan );
        // Fuzzy type is null for to type.
        assertNull( testRange.getToCvFuzzyType() );

        // > fuzzy type for from range
        testRange = new Range( inst, 2, 2, 3, 3, null );
        testRange.setFromCvFuzzyType( greaterThan );
        assertEquals( testRange.toString(), ">2-3" );
        // Fuzzy type is greater for from type.
        assertEquals( testRange.getFromCvFuzzyType(), greaterThan );
        // Fuzzy type is null for to type.
        assertNull( testRange.getToCvFuzzyType() );

        // < for both ranges
        testRange = new Range( inst, 2, 2, 3, 3, null );
        testRange.setFromCvFuzzyType( lessThan );
        testRange.setToCvFuzzyType( lessThan );
        assertEquals( testRange.toString(), "<2-<3" );
        // Fuzzy type is greater for from type.
        assertEquals( testRange.getFromCvFuzzyType(), lessThan );
        // Fuzzy type is greater for to type.
        assertEquals( testRange.getToCvFuzzyType(), lessThan );

        // > for both ranges
        testRange = new Range( inst, 2, 2, 3, 3, null );
        testRange.setFromCvFuzzyType( greaterThan );
        testRange.setToCvFuzzyType( greaterThan );
        assertEquals( testRange.toString(), ">2->3" );
        // Fuzzy type is greater for from type.
        assertEquals( testRange.getFromCvFuzzyType(), greaterThan );
        // Fuzzy type is greater for to type.
        assertEquals( testRange.getToCvFuzzyType(), greaterThan );

        // > for from and < for to ranges
        testRange = new Range( inst, 2, 2, 3, 3, null );
        testRange.setFromCvFuzzyType( greaterThan );
        testRange.setToCvFuzzyType( lessThan );
        assertEquals( testRange.toString(), ">2-<3" );
        // Fuzzy type is greater for from type.
        assertEquals( testRange.getFromCvFuzzyType(), greaterThan );
        // Fuzzy type is less than for to type.
        assertEquals( testRange.getToCvFuzzyType(), lessThan );

        // < for from and > for to ranges
        testRange = new Range( inst, 2, 2, 3, 3, null );
        testRange.setFromCvFuzzyType( lessThan );
        testRange.setToCvFuzzyType( greaterThan );
        assertEquals( testRange.toString(), "<2->3" );
        // Fuzzy type is less than for from type.
        assertEquals( testRange.getFromCvFuzzyType(), lessThan );
        // Fuzzy type is greater for to type.
        assertEquals( testRange.getToCvFuzzyType(), greaterThan );

        // fuzzy type is undetermined
        testRange = new Range( inst, 0, 0, 0, 0, null );
        testRange.setFromCvFuzzyType( undetermined );
        testRange.setToCvFuzzyType( undetermined );
        assertEquals( testRange.toString(), "?-?" );
        // Fuzzy type is undetermined for from type.
        assertEquals( testRange.getFromCvFuzzyType(), undetermined );
        // Fuzzy type is undetermined for to type.
        assertEquals( testRange.getToCvFuzzyType(), undetermined );

        // from fuzzy type is range
        testRange = new Range( inst, 1, 2, 2, 2, null );
        testRange.setFromCvFuzzyType( range );
        assertEquals( testRange.toString(), "1..2-2" );
        // Fuzzy type is range for from type.
        assertEquals( testRange.getFromCvFuzzyType(), range );
        // Fuzzy type is unknown for to type.
        assertNull( testRange.getToCvFuzzyType() );

        // to fuzzy type is range
        testRange = new Range( inst, 1, 1, 2, 3, null );
        testRange.setToCvFuzzyType( range );
        assertEquals( testRange.toString(), "1-2..3" );
        // Fuzzy type is unknown for from type.
        assertNull( testRange.getFromCvFuzzyType() );
        // Fuzzy type is range for to type.
        assertEquals( testRange.getToCvFuzzyType(), range );

        // from and to fuzzy types are ranges
        testRange = new Range( inst, 1, 2, 2, 3, null );
        testRange.setFromCvFuzzyType( range );
        testRange.setToCvFuzzyType( range );
        assertEquals( testRange.toString(), "1..2-2..3" );
        // Fuzzy type is range for from type.
        assertEquals( testRange.getFromCvFuzzyType(), range );
        // Fuzzy type is range for to type.
        assertEquals( testRange.getToCvFuzzyType(), range );

        // c for for range
        testRange = new Range( inst, 0, 0, 3, 3, null );
        testRange.setFromCvFuzzyType( ct );
        assertEquals( testRange.toString(), "c-3" );
        // Fuzzy type is c-terminal for from type.
        assertEquals( testRange.getFromCvFuzzyType(), ct );
        // Fuzzy type is null for to type.
        assertNull( testRange.getToCvFuzzyType() );

        // c for for both ranges
        testRange = new Range( inst, 0, 0, 0, 0, null );
        testRange.setFromCvFuzzyType( ct );
        testRange.setToCvFuzzyType( ct );
        assertEquals( testRange.toString(), "c-c" );
        // Fuzzy type is c-terminal for from type.
        assertEquals( testRange.getFromCvFuzzyType(), ct );
        // Fuzzy type is c-terminal for to type.
        assertEquals( testRange.getToCvFuzzyType(), ct );

        // c for from and n for to
        testRange = new Range( inst, 0, 0, 0, 0, null );
        testRange.setFromCvFuzzyType( ct );
        testRange.setToCvFuzzyType( nt );
        assertEquals( testRange.toString(), "c-n" );
        // Fuzzy type is c-terminal for from type.
        assertEquals( testRange.getFromCvFuzzyType(), ct );
        // Fuzzy type is n-terminal for to type.
        assertEquals( testRange.getToCvFuzzyType(), nt );

        // n for for from and n for to range
        testRange = new Range( inst, 0, 0, 3, 3, null );
        testRange.setFromCvFuzzyType( nt );
        assertEquals( testRange.toString(), "n-3" );
        // Fuzzy type is n-terminal for from type.
        assertEquals( testRange.getFromCvFuzzyType(), nt );
        // Fuzzy type is null for to type.
        assertNull( testRange.getToCvFuzzyType() );

        // n for for range
        testRange = new Range( inst, 0, 0, 0, 0, null );
        testRange.setFromCvFuzzyType( nt );
        testRange.setToCvFuzzyType( nt );
        assertEquals( testRange.toString(), "n-n" );
        // Fuzzy type is n-terminal for from type.
        assertEquals( testRange.getFromCvFuzzyType(), nt );
        // Fuzzy type is n-terminal for to type.
        assertEquals( testRange.getToCvFuzzyType(), nt );
    }

    private void doTestSetSequenceRaw( IntactHelper helper ) throws IntactException {
        CvFuzzyType lessThan = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.LESS_THAN );
        CvFuzzyType greaterThan = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.GREATER_THAN );
        CvFuzzyType undetermined = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.UNDETERMINED );
        CvFuzzyType rangeType = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.RANGE );
        CvFuzzyType ct = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.C_TERMINAL );
        CvFuzzyType nt = (CvFuzzyType) helper.getObjectByLabel( CvFuzzyType.class, CvFuzzyType.N_TERMINAL );

        // Cache the institution.
        Institution inst = helper.getInstitution();

        Range range = null;

        // Set the maximum size for the tests.
        Range.setMaxSequenceSize( 5 );

        range = new Range( inst, 0, 2, 3, 3, null );

        // fuzzy type set to C-Terminal
        range.setFromCvFuzzyType( ct );
        checkSequence( range, "0123456789", "56789" );
        checkSequence( range, "01234", "01234" );
        checkSequence( range, "0123", "0123" );
        checkSequence( range, "0", "0" );
        checkSequence( range, "", "" );
        checkSequence( range, null, null );

        // Set it back N-terminal.
        range.setFromCvFuzzyType( nt );
        checkSequence( range, "0123456789", "01234" );
        checkSequence( range, "01234", "01234" );
        checkSequence( range, "0123", "0123" );
        checkSequence( range, "0", "0" );
        checkSequence( range, "", "" );
        checkSequence( range, null, null );

        // Set it undetermined type.
        range.setFromCvFuzzyType( undetermined );
        checkSequence( range, "0123456789", "01234" );
        checkSequence( range, "01234", "01234" );
        checkSequence( range, "0123", "0123" );
        checkSequence( range, "0", "0" );
        checkSequence( range, "", "" );
        checkSequence( range, null, null );

        // Fuzzy type is none.
        testOtherSetSequenceType( null, inst );
        // Fuzzy type is <
        testOtherSetSequenceType( lessThan, inst );
        // Fuzzy type is >
        testOtherSetSequenceType( greaterThan, inst );
        // Fuzzy type range
        testOtherSetSequenceType( rangeType, inst );
    }

    private void checkSequence( Range range, String sequence, String expected ) {
        range.setSequence( sequence );
        if( sequence == null ) {
            assertNull( range.getSequence() );
        } else {
            assertEquals( range.getSequence(), expected );
        }
    }

    private void testOtherSetSequenceType( CvFuzzyType type, Institution inst ) {
        // From range is 0
        Range range = new Range( inst, 0, 0, 2, 2, null );
        range.setFromCvFuzzyType( type );
        checkSequence( range, "0123456789", "01234" );
        checkSequence( range, "01234", "01234" );
        checkSequence( range, "0123", "0123" );
        checkSequence( range, "0", "0" );
        checkSequence( range, "", "" );
        checkSequence( range, null, null );

        // From range is 1
        range = new Range( inst, 1, 1, 2, 2, null );
        range.setFromCvFuzzyType( type );
        checkSequence( range, "0123456789", "12345" );
        checkSequence( range, "01234", "1234" );
        checkSequence( range, "0123", "123" );
        checkSequence( range, "0", "" );
        checkSequence( range, "", null );
        checkSequence( range, null, null );

        // From range is 2
        range = new Range( inst, 2, 2, 2, 2, null );
        range.setFromCvFuzzyType( type );
        checkSequence( range, "0123456789", "23456" );
        checkSequence( range, "01234", "234" );
        checkSequence( range, "0123", "23" );
        checkSequence( range, "0", null );
        checkSequence( range, "", null );
        checkSequence( range, null, null );

        // From range is 5
        range = new Range( inst, 5, 5, 5, 5, null );
        range.setFromCvFuzzyType( type );
        checkSequence( range, "0123456789", "56789" );
        checkSequence( range, "01234", "" );
        checkSequence( range, "0123", null );

        // From range is 5 but actual sequence is less than from + max
        range = new Range( inst, 5, 5, 5, 5, null );
        range.setFromCvFuzzyType( type );
        checkSequence( range, "01234", "" );
        checkSequence( range, "012345", "5" );
        checkSequence( range, "0123456", "56" );
        checkSequence( range, "012345678901", "56789" );
    }
}