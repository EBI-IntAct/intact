package uk.ac.ebi.intact.bridges.taxonomy;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 * OLS Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class OlsTaxonomyServiceTest {

    @Test
    @Ignore
    public void getTerm_mouse() throws Exception {
        TaxonomyService taxonomy = new OLSTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( 10090 );
        assertNotNull( term );
        assertEquals( term.getTaxid(), 10090 );
        assertEquals( term.getCommonName(), "Mouse" );
        assertEquals( term.getScientificName(), "Mus musculus" );
    }

    @Test
    public void getTerm_human() throws Exception {
        TaxonomyService taxonomy = new OLSTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( 9606 );
        assertNotNull( term );
        assertEquals( term.getTaxid(), 9606 );
        assertEquals( term.getCommonName(), "Human" );
        assertEquals( term.getScientificName(), "Homo sapiens" );
    }

    @Test
    public void getTerm_special() throws Exception {
        TaxonomyService taxonomy = new OLSTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( -1 );
        assertNotNull( term );
        assertEquals( term.getTaxid(), -1 );
        assertEquals( term.getCommonName(), "In vitro" );
        assertEquals( term.getScientificName(), "In vitro" );
    }
}