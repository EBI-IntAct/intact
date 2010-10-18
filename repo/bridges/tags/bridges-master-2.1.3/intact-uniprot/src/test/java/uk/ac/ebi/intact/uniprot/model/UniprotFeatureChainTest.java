package uk.ac.ebi.intact.uniprot.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * UniprotFeatureChain Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class UniprotFeatureChainTest {

    @Test
    public void Id() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        assertEquals( "PRO_123", ufc.getPrimaryAc() );

        try {
            new UniprotFeatureChain( "", new Organism( 1 ), "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotFeatureChain( null, new Organism( 1 ), "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    @Test
    public void Sequence() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        assertEquals( "ABCD", ufc.getSequence() );
        ufc.setSequence( "ACBEDFG" );
        assertEquals( "ACBEDFG", ufc.getSequence() );
    }

    @Test
    public void SetGetOrganism() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        assertEquals( new Organism( 1 ), ufc.getOrganism() );

        try {
            new UniprotFeatureChain( "PRO_123", null, "ABCD" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            ufc.setOrganism( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    @Test
    public void Start() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        ufc.setStart( 2 );
        assertEquals( 2, (long) ufc.getStart() );
        try {
            ufc.setStart( -2 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
        try {
            ufc.setEnd( 4 );
            ufc.setStart( 5 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    @Test
    public void End() throws Exception {
        UniprotFeatureChain ufc = new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" );
        ufc.setEnd( 5 );
        assertEquals( 5, (long) ufc.getEnd() );
        try {
            ufc.setStart( 3 );
            ufc.setEnd( 2 );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }
}
