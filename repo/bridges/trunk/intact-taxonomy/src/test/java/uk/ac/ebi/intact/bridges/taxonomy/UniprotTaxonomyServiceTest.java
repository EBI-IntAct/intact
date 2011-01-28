package uk.ac.ebi.intact.bridges.taxonomy;

import org.junit.Test;

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
        assertEquals( 9606, term.getTaxid() );
        assertEquals( "HUMAN", term.getMnemonic() );
        assertEquals( "Human", term.getCommonName() );
        assertEquals( "Homo sapiens", term.getScientificName() );
        assertTrue( term.getSynonyms().isEmpty() );
    }

    @Test
    public void dog() throws Exception {
        TaxonomyService taxonomy = new UniprotTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( 9615 );
        assertNotNull( term );
//        System.out.println(term);
        assertEquals( 9615, term.getTaxid() );
        assertEquals( "CANFA", term.getMnemonic() );
        assertEquals( "Dog", term.getCommonName() );
        assertEquals( "Canis familiaris", term.getScientificName() );
        assertEquals( 1, term.getSynonyms().size() );
        assertEquals( "Canis lupus familiaris", term.getSynonyms().iterator().next() );
    }

    @Test
    public void yeast() throws Exception {
        TaxonomyService taxonomy = new UniprotTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( 4932 );
        assertNotNull( term );
//        System.out.println(term);
        assertEquals( 4932, term.getTaxid() );
        assertEquals( "YEAST", term.getMnemonic() );
        assertEquals( "Baker's yeast", term.getCommonName() );
        assertEquals( "Saccharomyces cerevisiae", term.getScientificName() );
        assertEquals( 0, term.getSynonyms().size() );
    }

    @Test
    public void musvi() throws Exception {
        TaxonomyService taxonomy = new UniprotTaxonomyService();
        TaxonomyTerm term = taxonomy.getTaxonomyTerm( 9667 );
        assertNotNull( term );
//        System.out.println(term);
        assertEquals( 452646, term.getTaxid() );
        assertEquals( "MUSVI", term.getMnemonic() );
        assertEquals( "American mink", term.getCommonName() );
        assertEquals( "Mustela vison", term.getScientificName() );
        assertEquals( 1, term.getSynonyms().size() );
        assertTrue( term.hasObsoleteTaxid() );
        assertEquals( 9667, term.getObsoleteTaxid() );
    }
}
