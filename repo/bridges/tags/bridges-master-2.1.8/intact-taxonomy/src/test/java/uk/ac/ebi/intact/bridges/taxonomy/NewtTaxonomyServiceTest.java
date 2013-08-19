package uk.ac.ebi.intact.bridges.taxonomy;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import static org.junit.Assert.*;

import java.util.Collection;

/**
 * Newt Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.0
 */
public class NewtTaxonomyServiceTest {

    @Test
    @Ignore
    public void GetNewtTerm() throws Exception {
        NewtTaxonomyService newt = new NewtTaxonomyService();
        TaxonomyTerm term = newt.getTaxonomyTerm( 9606 );
        assertNotNull( term );
        assertEquals( term.getTaxid(), 9606 );
    }

    @Test
    @Ignore
    public void GetNewtTermChildren() throws Exception {
        NewtTaxonomyService newt = new NewtTaxonomyService();
        Collection<TaxonomyTerm> children = newt.getTermChildren( 9606 );
        assertEquals( 1, children.size() );
        assertEquals( 63221, children.iterator().next().getTaxid() );
    }

    @Test
    @Ignore
    public void GetNewtTermParent() throws Exception {
        NewtTaxonomyService newt = new NewtTaxonomyService();
        Collection<TaxonomyTerm> parents = newt.getTermParent( 9606 );
        assertEquals( 1, parents.size() );
        assertEquals( 9605, parents.iterator().next().getTaxid() );
    }

    @Test
    @Ignore
    public void RetrieveChildren() throws TaxonomyServiceException {
        NewtTaxonomyService newt = new NewtTaxonomyService();
        TaxonomyTerm term = newt.getTaxonomyTerm( 562 );

        // Term with direct children
        newt.retrieveChildren( term, false );
        assertTrue( TaxonomyTermUtils.collectAllChildren( term ).size() > 74);

        // Term with all children
        newt.retrieveChildren( term, true );
        assertTrue( TaxonomyTermUtils.collectAllChildren( term ).size() > 86);
    }

    @Test
    @Ignore
    public void RetrieveParents() throws TaxonomyServiceException {
        NewtTaxonomyService newt = new NewtTaxonomyService();

        TaxonomyTerm term = newt.getTaxonomyTerm( 285006 );
        assertNotNull( term );

        // Term with direct parent
        newt.retrieveParents( term, false );
        assertEquals( 4932, term.getParents().iterator().next().getTaxid() );

        // Term with all parents
        newt.retrieveParents( term, true );

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
        assertEquals( 451864, t.getTaxid() );

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

    @Test
    @Ignore
    public void Cache() throws TaxonomyServiceException {

        // when the caching is enabled, we do not have duplication of terms, they are reused.
        NewtTaxonomyService newt = new NewtTaxonomyService( true );

        TaxonomyTerm eukariota = newt.getTaxonomyTerm( 2759 );
        TaxonomyTerm bacteria = newt.getTaxonomyTerm( 2 );

        newt.retrieveParents( eukariota, false );
        newt.retrieveParents( bacteria, false );

        TaxonomyTerm eukariotaParent = eukariota.getParents().iterator().next();
        TaxonomyTerm bacteriaParent = bacteria.getParents().iterator().next();

        assertTrue( eukariotaParent == bacteriaParent );
    }

    @Test
    @Ignore
    public void NoCache() throws TaxonomyServiceException {

        // when the caching is disabled, we have duplication of terms
        NewtTaxonomyService newt = new NewtTaxonomyService( false );

        TaxonomyTerm eukariota = newt.getTaxonomyTerm( 2759 );
        TaxonomyTerm bacteria = newt.getTaxonomyTerm( 2 );

        newt.retrieveParents( eukariota, false );
        newt.retrieveParents( bacteria, false );

        TaxonomyTerm eukariotaParent = eukariota.getParents().iterator().next();
        TaxonomyTerm bacteriaParent = bacteria.getParents().iterator().next();

        assertFalse( eukariotaParent == bacteriaParent );
        assertEquals( eukariotaParent, bacteriaParent );
    }
}
