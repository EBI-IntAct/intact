package uk.ac.ebi.intact.util.protein;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.util.newt.NewtTerm;
import uk.ac.ebi.intact.util.newt.NewtBridge;
import uk.ac.ebi.intact.util.newt.NewtUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * NewtBridge Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version 1.0
 * @since <pre>01/17/2007</pre>
 */
public class NewtBridgeTest extends TestCase {
    public NewtBridgeTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( NewtBridgeTest.class );
    }

    ////////////////////
    // Tests

    public void testGetNewtTerm() throws Exception {
        NewtBridge newt = new NewtBridge();
        NewtTerm term = newt.getNewtTerm( 9606 );
        assertNotNull( term );
        assertEquals( term.getTaxid(), 9606 );
    }

    public void testGetNewtTermChildren() throws Exception {
        NewtBridge newt = new NewtBridge();
        Collection<NewtTerm> children = newt.getNewtTermChildren( 9606 );
        assertEquals( 1, children.size() );
        assertEquals( 63221, children.iterator().next().getTaxid() );
    }

    public void testGetNewtTermParent() throws Exception {
        NewtBridge newt = new NewtBridge();
        Collection<NewtTerm> parents = newt.getNewtTermParent( 9606 );
        assertEquals( 1, parents.size() );
        assertEquals( 9605, parents.iterator().next().getTaxid() );
    }

    public void testRretreiveChildren() throws IOException {
        NewtBridge newt = new NewtBridge();
        NewtTerm term = newt.getNewtTerm( 562 );

        // Term with direct children
        newt.retreiveChildren( term, false );
        assertEquals( 53, NewtUtils.collectAllChildren( term ).size() );

        // Term with all children
        newt.retreiveChildren( term, true );
        assertEquals( 57, NewtUtils.collectAllChildren( term ).size() );
    }

    public void testRetreiveParents() throws IOException {
        NewtBridge newt = new NewtBridge();
        
        NewtTerm term = newt.getNewtTerm( 285006 );
        assertNotNull( term );

        // Term with direct parent
        newt.retreiveParents( term, false );
        assertEquals( 4932, term.getParents().iterator().next().getTaxid() );

        // Term with all parents
        newt.retreiveParents( term, true );

        NewtTerm t = null;
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

        assertTrue( t.getParents().isEmpty() );
    }
}
