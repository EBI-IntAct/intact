package uk.ac.ebi.intact.util.protein;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.taxonomy.NewtTaxonomyService;
import uk.ac.ebi.intact.util.taxonomy.TaxonomyServiceException;
import uk.ac.ebi.intact.util.taxonomy.TaxonomyTerm;
import uk.ac.ebi.intact.util.taxonomy.TaxonomyTermUtils;

import java.util.Collection;

/**
 * NewtBridge Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>01/17/2007</pre>
 */
public class NewtTaxonomyServiceTest extends TestCase {
    public NewtTaxonomyServiceTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( NewtTaxonomyServiceTest.class );
    }

    ////////////////////
    // Tests

    public void testGetNewtTerm() throws Exception {
        NewtTaxonomyService newt = new NewtTaxonomyService();
        TaxonomyTerm term = newt.getTaxonomyTerm( 9606 );
        assertNotNull( term );
        assertEquals( term.getTaxid(), 9606 );
    }

    public void testGetNewtTermChildren() throws Exception {
        NewtTaxonomyService newt = new NewtTaxonomyService();
        Collection<TaxonomyTerm> children = newt.getTermChildren( 9606 );
        assertEquals( 1, children.size() );
        assertEquals( 63221, children.iterator().next().getTaxid() );
    }

    public void testGetNewtTermParent() throws Exception {
        NewtTaxonomyService newt = new NewtTaxonomyService();
        Collection<TaxonomyTerm> parents = newt.getTermParent( 9606 );
        assertEquals( 1, parents.size() );
        assertEquals( 9605, parents.iterator().next().getTaxid() );
    }

    public void testRretreiveChildren() throws TaxonomyServiceException {
        NewtTaxonomyService newt = new NewtTaxonomyService();
        TaxonomyTerm term = newt.getTaxonomyTerm( 562 );

        // Term with direct children
        newt.retreiveChildren( term, false );
        assertEquals( 55, TaxonomyTermUtils.collectAllChildren( term ).size() );

        // Term with all children
        newt.retreiveChildren( term, true );
        assertEquals( 59, TaxonomyTermUtils.collectAllChildren( term ).size() );
    }

    public void testRetreiveParents() throws TaxonomyServiceException {
        NewtTaxonomyService newt = new NewtTaxonomyService();

        TaxonomyTerm term = newt.getTaxonomyTerm( 285006 );
        assertNotNull( term );

        // Term with direct parent
        newt.retreiveParents( term, false );
        assertEquals( 4932, term.getParents().iterator().next().getTaxid() );

        // Term with all parents
        newt.retreiveParents( term, true );

        TaxonomyTerm t = null;
        t = term.getParents().iterator().next();
        assertEquals( 4932, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 4930, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 4893, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 4892, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 4891, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 147537, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 4890, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 4751, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 33154, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 2759, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 131567, t.getTaxid() );

        t = t.getParents().iterator().next();
        assertEquals( 1, t.getTaxid() );

        assertTrue( t.getParents().isEmpty() );
    }

    public void testCache() throws TaxonomyServiceException {

        // when the caching is enabled, we do not have duplication of terms, they are reused.
        NewtTaxonomyService newt = new NewtTaxonomyService( true );

        TaxonomyTerm eukariota = newt.getTaxonomyTerm( 2759 );
        TaxonomyTerm bacteria = newt.getTaxonomyTerm( 2 );

        newt.retreiveParents( eukariota, false );
        newt.retreiveParents( bacteria, false );

        TaxonomyTerm eukariotaParent = eukariota.getParents().iterator().next();
        TaxonomyTerm bacteriaParent = bacteria.getParents().iterator().next();

        assertTrue( eukariotaParent == bacteriaParent );
    }

    public void testNoCache() throws TaxonomyServiceException {

        // when the caching is disabled, we have duplication of terms
        NewtTaxonomyService newt = new NewtTaxonomyService( false );

        TaxonomyTerm eukariota = newt.getTaxonomyTerm( 2759 );
        TaxonomyTerm bacteria = newt.getTaxonomyTerm( 2 );

        newt.retreiveParents( eukariota, false );
        newt.retreiveParents( bacteria, false );

        TaxonomyTerm eukariotaParent = eukariota.getParents().iterator().next();
        TaxonomyTerm bacteriaParent = bacteria.getParents().iterator().next();

        assertFalse( eukariotaParent == bacteriaParent );
        assertEquals( eukariotaParent, bacteriaParent );
    }
}
