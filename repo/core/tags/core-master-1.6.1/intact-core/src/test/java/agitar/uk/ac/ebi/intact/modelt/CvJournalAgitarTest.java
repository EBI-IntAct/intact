/**
 * Generated by Agitar build: Agitator Version 1.0.4.000276 (Build date: Mar 27, 2007) [1.0.4.000276]
 * JDK Version: 1.5.0_09
 *
 * Generated on 04-Apr-2007 08:28:32
 * Time to generate: 00:07.138 seconds
 *
 */

package agitar.uk.ac.ebi.intact.modelt; import uk.ac.ebi.intact.model.*;

import com.agitar.lib.junit.AgitarTestCase;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

public class CvJournalAgitarTest extends AgitarTestCase {

    static Class TARGET_CLASS = CvJournal.class;

    public void testConstructor() throws Throwable {
        Institution owner = new Institution( "testCvJournalShortLabel" );
        CvJournal cvJournal = new CvJournal( owner, "testCvJournalShortLabel" );
        assertEquals( "cvJournal.xrefs.size()", 0, cvJournal.xrefs.size() );
        assertEquals( "cvJournal.getAliases().size()", 0, cvJournal.getAliases().size() );
        assertEquals( "cvJournal.getEvidences().size()", 0, cvJournal.getEvidences().size() );
        assertEquals( "cvJournal.shortLabel", "testCvJournalShortLa", cvJournal.getShortLabel() );
        assertEquals( "cvJournal.annotations.size()", 0, cvJournal.annotations.size() );
        assertSame( "cvJournal.getOwner()", owner, cvJournal.getOwner() );
        assertEquals( "cvJournal.references.size()", 0, cvJournal.references.size() );
    }

    public void testConstructorThrowsIllegalArgumentException() throws Throwable {
        try {
            new CvJournal( new Institution( "testCvJournalShortLabel" ), "" );
            fail( "Expected IllegalArgumentException to be thrown" );
        } catch ( IllegalArgumentException ex ) {
            assertEquals( "ex.getMessage()", "Must define a non empty short label", ex.getMessage() );
            assertThrownBy( AnnotatedObjectUtils.class, ex );
        }
    }

    public void testConstructorThrowsNullPointerException() throws Throwable {
        try {
            new CvJournal( new Institution( "testCvJournalShortLabel" ), null );
            fail( "Expected NullPointerException to be thrown" );
        } catch ( NullPointerException ex ) {
            assertEquals( "ex.getMessage()", "Must define a non null short label", ex.getMessage() );
            assertThrownBy( AnnotatedObjectUtils.class, ex );
        }
    }
}

