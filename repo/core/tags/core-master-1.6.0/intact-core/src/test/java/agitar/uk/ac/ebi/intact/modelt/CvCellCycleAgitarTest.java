/**
 * Generated by Agitar build: Agitator Version 1.0.4.000276 (Build date: Mar 27, 2007) [1.0.4.000276]
 * JDK Version: 1.5.0_09
 *
 * Generated on 04-Apr-2007 08:21:42
 * Time to generate: 00:08.123 seconds
 *
 */

package agitar.uk.ac.ebi.intact.modelt;

import uk.ac.ebi.intact.model.*;

import com.agitar.lib.junit.AgitarTestCase;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

public class CvCellCycleAgitarTest extends AgitarTestCase {

    static Class TARGET_CLASS = CvCellCycle.class;

    public void testConstructor() throws Throwable {
        Institution owner = new Institution( "testCvCellCycleShortLabel" );
        CvCellCycle cvCellCycle = new CvCellCycle( owner, "testCvCellCycleShortLabel" );
        assertEquals( "cvCellCycle.xrefs.size()", 0, cvCellCycle.xrefs.size() );
        assertEquals( "cvCellCycle.getAliases().size()", 0, cvCellCycle.getAliases().size() );
        assertEquals( "cvCellCycle.getEvidences().size()", 0, cvCellCycle.getEvidences().size() );
        assertEquals( "cvCellCycle.shortLabel", "testCvCellCycleShort", cvCellCycle.getShortLabel() );
        assertEquals( "cvCellCycle.annotations.size()", 0, cvCellCycle.annotations.size() );
        assertSame( "cvCellCycle.getOwner()", owner, cvCellCycle.getOwner() );
        assertEquals( "cvCellCycle.references.size()", 0, cvCellCycle.references.size() );
    }

    public void testConstructorThrowsIllegalArgumentException() throws Throwable {
        try {
            new CvCellCycle( new Institution( "testCvCellCycleShortLabel" ), "" );
            fail( "Expected IllegalArgumentException to be thrown" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "ex.getMessage()", "Must define a non empty short label", ex.getMessage() );
            assertThrownBy( AnnotatedObjectUtils.class, ex );
        }
    }

    public void testConstructorThrowsNullPointerException() throws Throwable {
        try {
            new CvCellCycle( new Institution( "testCvCellCycleShortLabel" ), null );
            fail( "Expected NullPointerException to be thrown" );
        } catch ( NullPointerException ex ) {
            assertEquals( "ex.getMessage()", "Must define a non null short label", ex.getMessage() );
            assertThrownBy( AnnotatedObjectUtils.class, ex );
        }
    }
}

