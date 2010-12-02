package uk.ac.ebi.intact.bridges.taxonomy;

import org.junit.Test;
import sun.jvm.hotspot.utilities.Assert;

import static org.junit.Assert.*;

/**
 * UniprotTaxonomyService Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.1.4
 */
public class UniprotTaxonomyServiceTest {

    @Test
    public void human() throws Exception {
        TaxonomyService taxonomy = new UniprotTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( 9606 );
        assertNotNull( term );
//        System.out.println(term);
        assertEquals( term.getTaxid(), 9606 );
        assertEquals( term.getMnemonic(), "HUMAN" );
        assertEquals( term.getCommonName(), "Human" );
        assertEquals( term.getScientificName(), "Homo sapiens" );
        assertTrue( term.getSynonyms().isEmpty() );
    }

    @Test
    public void dog() throws Exception {
        TaxonomyService taxonomy = new UniprotTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( 9615 );
        assertNotNull( term );
//        System.out.println(term);
        assertEquals( term.getTaxid(), 9615 );
        assertEquals( term.getMnemonic(), "CANFA" );
        assertEquals( term.getCommonName(), "Dog" );
        assertEquals( term.getScientificName(), "Canis familiaris" );
        assertEquals( 1, term.getSynonyms().size() );
        assertEquals( "Canis lupus familiaris", term.getSynonyms().iterator().next() );
    }

    @Test
    public void yeast() throws Exception {
        TaxonomyService taxonomy = new UniprotTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( 4932 );
        assertNotNull( term );
//        System.out.println(term);
        assertEquals( term.getTaxid(), 4932 );
        assertEquals( term.getMnemonic(), "YEAST" );
        assertEquals( term.getCommonName(), "Baker's yeast" );
        assertEquals( term.getScientificName(), "Saccharomyces cerevisiae" );
        assertEquals( 0, term.getSynonyms().size() );
    }
}
