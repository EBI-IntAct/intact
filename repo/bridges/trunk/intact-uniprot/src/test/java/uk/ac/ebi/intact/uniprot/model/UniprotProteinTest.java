package uk.ac.ebi.intact.uniprot.model;

import static org.junit.Assert.*;
import org.junit.Test;

import java.text.SimpleDateFormat;

/**
 * UniprotProtein Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class UniprotProteinTest {

    @Test
    public void Constructor() {
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

    @Test
    public void Id() throws Exception {
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

    @Test
    public void Sequence() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getSequence() );
        protein.setSequence( "ABCD" );
        assertEquals( "ABCD", protein.getSequence() );
        protein.setSequence( "ACBEDFG" );
        assertEquals( "ACBEDFG", protein.getSequence() );
    }

    @Test
    public void Organism() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertEquals( new Organism( 1 ), protein.getOrganism() );

        try {
            protein.setOrganism( null );
            fail();
        } catch ( Exception e ) {
            // ok
        }
    }

    @Test
    public void SetGetPrimaryAc() throws Exception {
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

    @Test
    public void GetSecondaryAcs() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getSecondaryAcs() );
        assertEquals( 0, protein.getSecondaryAcs().size() );
        protein.getSecondaryAcs().add( "Q99999" );
        assertEquals( 1, protein.getSecondaryAcs().size() );
        assertTrue( protein.getSecondaryAcs().contains( "Q99999" ) );
    }

    @Test
    public void SetGetDescription() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getDescription() );
        assertEquals( "desc", protein.getDescription() );
        protein.setDescription( "a note" );
        assertEquals( "a note", protein.getDescription() );
    }

    @Test
    public void GetGenes() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getGenes() );
        assertEquals( 0, protein.getGenes().size() );
        protein.getGenes().add( "gene" );
        assertEquals( 1, protein.getGenes().size() );
        assertTrue( protein.getGenes().contains( "gene" ) );
    }

    @Test
    public void GetOrfs() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getOrfs() );
        assertEquals( 0, protein.getOrfs().size() );
        protein.getOrfs().add( "orf" );
        assertEquals( 1, protein.getOrfs().size() );
        assertTrue( protein.getOrfs().contains( "orf" ) );
    }

    @Test
    public void GetSynomyms() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getSynomyms() );
        assertEquals( 0, protein.getSynomyms().size() );
        protein.getSynomyms().add( "syn" );
        assertEquals( 1, protein.getSynomyms().size() );
        assertTrue( protein.getSynomyms().contains( "syn" ) );
    }

    @Test
    public void GetLocuses() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getLocuses() );
        assertEquals( 0, protein.getLocuses().size() );
        protein.getLocuses().add( "locus" );
        assertEquals( 1, protein.getLocuses().size() );
        assertTrue( protein.getLocuses().contains( "locus" ) );
    }

    @Test
    public void GetKeywords() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getKeywords() );
        assertEquals( 0, protein.getKeywords().size() );
        protein.getKeywords().add( "kw" );
        assertEquals( 1, protein.getKeywords().size() );
        assertTrue( protein.getKeywords().contains( "kw" ) );
    }

    @Test
    public void GetFunctions() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getFunctions() );
        assertEquals( 0, protein.getFunctions().size() );
        protein.getFunctions().add( "function" );
        assertEquals( 1, protein.getFunctions().size() );
        assertTrue( protein.getFunctions().contains( "function" ) );
    }

    @Test
    public void GetCrossReferences() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getCrossReferences() );
        protein.getCrossReferences().add( new UniprotXref( "SAM:1", "sam" ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "SAM:1", "sam" ) ) );
    }

    @Test
    public void GetSpliceVariants() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getSpliceVariants() );
        protein.getSpliceVariants().add( new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" ) );
        assertEquals( 1, protein.getSpliceVariants().size() );
        assertTrue( protein.getSpliceVariants().contains( new UniprotSpliceVariant( "P12345-1", new Organism( 1 ), "ABCD" ) ) );
    }

    @Test
    public void GetFeatureChains() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNotNull( protein.getFeatureChains() );
        protein.getFeatureChains().add( new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" ) );
        assertEquals( 1, protein.getFeatureChains().size() );
        assertTrue( protein.getFeatureChains().contains( new UniprotFeatureChain( "PRO_123", new Organism( 1 ), "ABCD" ) ) );
    }

    @Test
    public void SetGetCrc64() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getCrc64() );
        protein.setCrc64( "LFLASIFNLIAFN1298437" );
        assertEquals( "LFLASIFNLIAFN1298437", protein.getCrc64() );
    }

    @Test
    public void SetGetSequence() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getSequence() );
        protein.setSequence( "ABCDEFGHABCDEFGH" );
        assertEquals( "ABCDEFGHABCDEFGH", protein.getSequence() );
    }

    @Test
    public void SetGetSequenceLength() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertEquals( 0, protein.getSequenceLength() );
        protein.setSequenceLength( 5 );
        assertEquals( 5, protein.getSequenceLength() );
    }

    @Test
    public void SetGetReleaseVersion() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getReleaseVersion() );
        protein.setReleaseVersion( "v1" );
        assertEquals( "v1", protein.getReleaseVersion() );
    }

    @Test
    public void SetGetLastAnnotationUpdate() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getLastAnnotationUpdate() );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MMM-dd" );
        protein.setLastAnnotationUpdate( sdf.parse( "2006-NOV-01" ) );
        assertEquals( sdf.parse( "2006-NOV-01" ), protein.getLastAnnotationUpdate() );
    }

    @Test
    public void SetGetLastSequenceUpdate() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getLastSequenceUpdate() );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MMM-dd" );
        protein.setLastSequenceUpdate( sdf.parse( "2006-NOV-01" ) );
        assertEquals( sdf.parse( "2006-NOV-01" ), protein.getLastSequenceUpdate() );
    }

    @Test
    public void SetGetSource() throws Exception {
        UniprotProtein protein = new UniprotProtein( "P12345_HUMAN", "P12345", new Organism( 1 ), "desc" );
        assertNull( protein.getSource() );
        protein.setSource( UniprotProteinType.SWISSPROT );
        assertEquals( UniprotProteinType.SWISSPROT, protein.getSource() );
    }
}