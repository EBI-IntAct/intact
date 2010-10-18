package uk.ac.ebi.intact.bridges.taxonomy;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * NewtTerm Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since 1.0
 */
public class NewtTermTest {

    @Test
    public void testNewtTerm() {
        NewtTerm t = new NewtTerm( "123|common|sci|blabla" );
        assertNotNull( t );
        assertEquals( 123, t.getTaxid() );
        assertNotNull( t.getScientificName() );
        assertEquals( "sci", t.getScientificName() );

        assertNotNull( t.getCommonName() );
        assertEquals( "common", t.getCommonName() );

        assertTrue( t.getChildren().isEmpty() );
        assertTrue( t.getParents().isEmpty() );
    }
}