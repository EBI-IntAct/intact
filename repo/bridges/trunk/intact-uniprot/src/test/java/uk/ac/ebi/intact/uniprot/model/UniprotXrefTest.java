package uk.ac.ebi.intact.uniprot.model;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * UniprotXref Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class UniprotXrefTest {

    @Test
    public void SetGetAccession() throws Exception {
        UniprotXref x = new UniprotXref( "SAM:1", "sam" );
        assertNotNull( x );
        assertEquals( "SAM:1", x.getAccession() );
        assertEquals( "sam", x.getDatabase() );
        assertNull( x.getDescription() );

        try {
            new UniprotXref( "", "sam" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotXref( null, "sam" );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    @Test
    public void SetGetDatabase() throws Exception {
        UniprotXref x = new UniprotXref( "SAM:1", "sam" );
        assertNotNull( x );
        assertEquals( "SAM:1", x.getAccession() );
        assertEquals( "sam", x.getDatabase() );
        assertNull( x.getDescription() );

        try {
            new UniprotXref( "SAM:1", "" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotXref( "SAM:1", null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    @Test
    public void SetGetDescription() throws Exception {
        UniprotXref x = new UniprotXref( "SAM:1", "sam", "blabla" );
        assertNotNull( x );
        assertEquals( "SAM:1", x.getAccession() );
        assertEquals( "sam", x.getDatabase() );
        assertEquals( "blabla", x.getDescription() );

        x.setDescription( "desc" );
        assertEquals( "desc", x.getDescription() );
    }
}