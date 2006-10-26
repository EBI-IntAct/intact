package uk.ac.ebi.intact.bridge.model;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

import java.text.SimpleDateFormat;

/**
 * UniprotProtein Tester.
 *
 * @author <Authors name>
 * @since <pre>10/23/2006</pre>
 * @version 1.0
 */
public class UniprotProteinTest extends TestCase {
    public UniprotProteinTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite(UniprotProteinTest.class);
    }

    ////////////////////
    // Tests

    public void testConstructor() {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein );

        try {
            new UniprotProtein( null, "P12345", new Organism( 1 ), "desc" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotProtein( "", "P12345", new Organism( 1 ), "desc" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            new UniprotProtein( "P12345_HUMAN", "P12345", null, "desc" );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testId() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertEquals( "P12345_HUMAN", protein.getId() );

        protein.setId( "X" );
        assertEquals( "X", protein.getId() );

        try {
            protein.setId( "" );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            protein.setId( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testSequence() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getSequence() );
        protein.setSequence( "ABCD" );
        assertEquals( "ABCD", protein.getSequence() );
        protein.setSequence( "ACBEDFG" );
        assertEquals( "ACBEDFG", protein.getSequence() );
    }

    public void testOrganism() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertEquals( new Organism( 1 ), protein.getOrganism() );

        try {
            protein.setOrganism( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testSetGetPrimaryAc() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertEquals( "P12345", protein.getPrimaryAc() );

        try {
            protein.setPrimaryAc( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }

        try {
            protein.setPrimaryAc( "" );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    public void testGetSecondaryAcs() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getSecondaryAcs() );
        assertEquals( 0, protein.getSecondaryAcs().size() );
        protein.getSecondaryAcs().add( "Q99999" );
        assertEquals( 1, protein.getSecondaryAcs().size() );
        assertTrue( protein.getSecondaryAcs().contains( "Q99999" ) );
    }

    public void testSetGetDescription() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getDescription() );
        assertEquals( "desc", protein.getDescription() );
        protein.setDescription( "a note" );
        assertEquals( "a note", protein.getDescription() );
    }

    public void testGetGenes() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getGenes() );
        assertEquals( 0, protein.getGenes().size() );
        protein.getGenes().add( "gene" );
        assertEquals( 1, protein.getGenes().size() );
        assertTrue( protein.getGenes().contains( "gene" ) );
    }

    public void testGetOrfs() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getOrfs() );
        assertEquals( 0, protein.getOrfs().size() );
        protein.getOrfs().add( "orf" );
        assertEquals( 1, protein.getOrfs().size() );
        assertTrue( protein.getOrfs().contains( "orf" ) );
    }

    public void testGetSynomyms() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getSynomyms() );
        assertEquals( 0, protein.getSynomyms().size() );
        protein.getSynomyms().add( "syn" );
        assertEquals( 1, protein.getSynomyms().size() );
        assertTrue( protein.getSynomyms().contains( "syn" ) );
    }

    public void testGetLocuses() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getLocuses() );
        assertEquals( 0, protein.getLocuses().size() );
        protein.getLocuses().add( "locus" );
        assertEquals( 1, protein.getLocuses().size() );
        assertTrue( protein.getLocuses().contains( "locus" ) );
    }

    public void testGetKeywords() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getKeywords() );
        assertEquals( 0, protein.getKeywords().size() );
        protein.getKeywords().add( "kw" );
        assertEquals( 1, protein.getKeywords().size() );
        assertTrue( protein.getKeywords().contains( "kw" ) );
    }

    public void testGetFunctions() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getFunctions() );
        assertEquals( 0, protein.getFunctions().size() );
        protein.getFunctions().add( "function" );
        assertEquals( 1, protein.getFunctions().size() );
        assertTrue( protein.getFunctions().contains( "function" ) );
    }

    public void testGetCrossReferences() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getCrossReferences() );
        protein.getCrossReferences().add( new UniprotXref( "SAM:1", "sam" ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "SAM:1", "sam" ) ) );
    }

    public void testGetSpliceVariants() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getSpliceVariants() );
        protein.getSpliceVariants().add( new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" ) );
        assertEquals( 1, protein.getSpliceVariants().size() );
        assertTrue( protein.getSpliceVariants().contains( new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" ) ) );
    }

    public void testGetFeatureChains() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getFeatureChains() );
        protein.getFeatureChains().add( new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" ) );
        assertEquals( 1, protein.getFeatureChains().size() );
        assertTrue( protein.getFeatureChains().contains( new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" ) ) );
    }

    public void testSetGetCrc64() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getCrc64() );
        protein.setCrc64( "LFLASIFNLIAFN1298437" );
        assertEquals( "LFLASIFNLIAFN1298437", protein.getCrc64() );
    }

    public void testSetGetSequence() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getSequence() );
        protein.setSequence( "ABCDEFGHABCDEFGH" );
        assertEquals( "ABCDEFGHABCDEFGH", protein.getSequence() );
    }

    public void testSetGetSequenceLength() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertEquals( 0, protein.getSequenceLength() );
        protein.setSequenceLength( 5 );
        assertEquals( 5, protein.getSequenceLength() );
    }

    public void testSetGetReleaseVersion() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getReleaseVersion() );
        protein.setReleaseVersion( "v1" );
        assertEquals( "v1", protein.getReleaseVersion() );
    }

    public void testSetGetLastAnnotationUpdate() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getLastAnnotationUpdate() );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MMM-dd" );
        protein.setLastAnnotationUpdate( sdf.parse( "2006-NOV-01" ) );
        assertEquals( sdf.parse( "2006-NOV-01" ), protein.getLastAnnotationUpdate() );
    }

    public void testSetGetLastSequenceUpdate() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getLastSequenceUpdate() );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MMM-dd" );
        protein.setLastSequenceUpdate( sdf.parse( "2006-NOV-01" ) );
        assertEquals( sdf.parse( "2006-NOV-01" ), protein.getLastSequenceUpdate() );
    }
}