package uk.ac.ebi.intact.uniprot.service;

import org.junit.Test;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;

import java.util.Collection;

/**
 * CachedUniprotService Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class CachedUniprotServiceTest {

    @Test
    public void constructor() {
        UniprotService service = new CachedUniprotService( new DummyUniprotService() );
        Collection<UniprotProtein> proteins;

//        proteins = service.retreive( "P12345" );
//        assertNotNull( proteins );
//        assertEquals( 1, proteins.size() );
//        UniprotProtein p1 = proteins.iterator().next();
//
//        proteins = service.retreive( "P12345" );
//        assertNotNull( proteins );
//        assertEquals( 1, proteins.size() );
//        UniprotProtein p2 = proteins.iterator().next();
//
//        assertEquals( p1, p2);
//        assertSame( p1, p2 );
    }
}
