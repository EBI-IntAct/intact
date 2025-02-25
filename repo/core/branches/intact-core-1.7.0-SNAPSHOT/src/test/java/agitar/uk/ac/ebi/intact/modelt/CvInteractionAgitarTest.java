/**
 * Generated by Agitar build: Agitator Version 1.0.4.000276 (Build date: Mar 27, 2007) [1.0.4.000276]
 * JDK Version: 1.5.0_09
 *
 * Generated on 04-Apr-2007 08:22:33
 * Time to generate: 00:07.820 seconds
 *
 */

package agitar.uk.ac.ebi.intact.modelt;

import uk.ac.ebi.intact.model.*;

import com.agitar.lib.junit.AgitarTestCase;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

public class CvInteractionAgitarTest extends AgitarTestCase {

    static Class TARGET_CLASS = CvInteraction.class;

    public void testConstructor() throws Throwable {
        Institution owner = new Institution( "testCvInteractionShortLabel" );
        CvInteraction cvInteraction = new CvInteraction( owner, "testCvInteractionShortLabel" );
        assertEquals( "cvInteraction.xrefs.size()", 0, cvInteraction.xrefs.size() );
        assertEquals( "cvInteraction.getAliases().size()", 0, cvInteraction.getAliases().size() );
        assertEquals( "cvInteraction.getEvidences().size()", 0, cvInteraction.getEvidences().size() );
        assertEquals( "cvInteraction.shortLabel", "testCvInteractionSho", cvInteraction.getShortLabel() );
        assertEquals( "cvInteraction.getChildren().size()", 0, cvInteraction.getChildren().size() );
        assertEquals( "cvInteraction.getLeftBound()", -1L, cvInteraction.getLeftBound() );
        assertEquals( "cvInteraction.annotations.size()", 0, cvInteraction.annotations.size() );
        assertEquals( "cvInteraction.getParents().size()", 0, cvInteraction.getParents().size() );
        assertSame( "cvInteraction.getOwner()", owner, cvInteraction.getOwner() );
        assertEquals( "cvInteraction.references.size()", 0, cvInteraction.references.size() );
        assertEquals( "cvInteraction.getRightBound()", -1L, cvInteraction.getRightBound() );
    }

    public void testConstructorThrowsIllegalArgumentException() throws Throwable {
        try {
            new CvInteraction( new Institution( "testCvInteractionShortLabel" ), "" );
            fail( "Expected IllegalArgumentException to be thrown" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "ex.getMessage()", "Must define a non empty short label", ex.getMessage() );
            assertThrownBy( AnnotatedObjectUtils.class, ex );
        }
    }

    public void testConstructorThrowsNullPointerException() throws Throwable {
        try {
            new CvInteraction( new Institution( "testCvInteractionShortLabel" ), null );
            fail( "Expected NullPointerException to be thrown" );
        } catch ( NullPointerException ex ) {
            assertEquals( "ex.getMessage()", "Must define a non null short label", ex.getMessage() );
            assertThrownBy( AnnotatedObjectUtils.class, ex );
        }
    }
}

