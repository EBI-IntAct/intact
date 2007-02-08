package uk.ac.ebi.intact.bridge.adapters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.bridge.UniprotBridgeException;
import uk.ac.ebi.intact.bridge.adapters.referenceFilter.IntactCrossReferenceFilter;
import uk.ac.ebi.intact.bridge.model.UniprotFeatureChain;
import uk.ac.ebi.intact.bridge.model.UniprotProtein;
import uk.ac.ebi.intact.bridge.model.UniprotSpliceVariant;
import uk.ac.ebi.intact.bridge.model.UniprotXref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

/**
 * UniprotRemoteServiceAdapter Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>10/24/2006</pre>
 */
public class UniprotRemoteServiceAdapterTest extends TestCase {
    
    public UniprotRemoteServiceAdapterTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( UniprotRemoteServiceAdapterTest.class );
    }

    //////////////////////
    // Utility

    private UniprotBridgeAdapter getBridgeAdapter() {
        return new UniprotRemoteServiceAdapter();
    }

    private UniprotBridgeAdapter getBridgeAdapter( IntactCrossReferenceFilter filter ) {
        if ( filter == null ) {
            fail( "you must give a non null filter !!" );
        }
        AbstractUniprotBridgeAdapter service = ( AbstractUniprotBridgeAdapter ) getBridgeAdapter();
        service.setCrossReferenceSelector( filter );
        return ( UniprotBridgeAdapter ) service;
    }

    ////////////////////
    // Tests

    public void testBuild() throws Exception {
        UniprotBridgeAdapter uniprot = getBridgeAdapter();

        Collection<UniprotProtein> proteins = uniprot.retreive( "P47068" );

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        UniprotProtein protein = proteins.iterator().next();

        assertEquals( "BBC1_YEAST", protein.getId() );
        assertEquals( "Myosin tail region-interacting protein MTI1", protein.getDescription() );

        assertEquals( "P47068", protein.getPrimaryAc() );
        assertEquals( "P47067", protein.getSecondaryAcs().get( 0 ) );
        assertEquals( "Q8X1F4", protein.getSecondaryAcs().get( 1 ) );

        // gene names
        assertEquals( 1, protein.getGenes().size() );
        assertEquals( "BBC1", protein.getGenes().iterator().next() );

        assertEquals( 1, protein.getSynomyms().size() );
        assertEquals( "MTI1", protein.getSynomyms().iterator().next() );

        assertEquals( 1, protein.getOrfs().size() );
        assertEquals( "J1305/J1286", protein.getOrfs().iterator().next() );

        assertEquals( 1, protein.getLocuses().size() );
        assertTrue( protein.getLocuses().contains( "YJL020C/YJL021C" ) );

        // sequence
        String sequence = "MSEPEVPFKVVAQFPYKSDYEDDLNFEKDQEIIVTSVEDAEWYFGEYQDSNGDVIEGIFP" +
                          "KSFVAVQGSEVGKEAESSPNTGSTEQRTIQPEVEQKDLPEPISPETKKETLSGPVPVPAA" +
                          "TVPVPAATVPVPAATAVSAQVQHDSSSGNGERKVPMDSPKLKARLSMFNQDITEQVPLPK" +
                          "STHLDLENIPVKKTIVADAPKYYVPPGIPTNDTSNLERKKSLKENEKKIVPEPINRAQVE" +
                          "SGRIETENDQLKKDLPQMSLKERIALLQEQQRLQAAREEELLRKKAKLEQEHERSAVNKN" +
                          "EPYTETEEAEENEKTEPKPEFTPETEHNEEPQMELLAHKEITKTSREADEGTNDIEKEQF" +
                          "LDEYTKENQKVEESQADEARGENVAEESEIGYGHEDREGDNDEEKEEEDSEENRRAALRE" +
                          "RMAKLSGASRFGAPVGFNPFGMASGVGNKPSEEPKKKQHKEKEEEEPEQLQELPRAIPVM" +
                          "PFVDPSSNPFFRKSNLSEKNQPTETKTLDPHATTEHEQKQEHGTHAYHNLAAVDNAHPEY" +
                          "SDHDSDEDTDDHEFEDANDGLRKHSMVEQAFQIGNNESENVNSGEKIYPQEPPISHRTAE" +
                          "VSHDIENSSQNTTGNVLPVSSPQTRVARNGSINSLTKSISGENRRKSINEYHDTVSTNSS" +
                          "ALTETAQDISMAAPAAPVLSKVSHPEDKVPPHPVPSAPSAPPVPSAPSVPSAPPVPPAPP" +
                          "ALSAPSVPPVPPVPPVSSAPPALSAPSIPPVPPTPPAPPAPPAPLALPKHNEVEEHVKSS" +
                          "APLPPVSEEYHPMPNTAPPLPRAPPVPPATFEFDSEPTATHSHTAPSPPPHQNVTASTPS" +
                          "MMSTQQRVPTSVLSGAEKESRTLPPHVPSLTNRPVDSFHESDTTPKVASIRRSTTHDVGE" +
                          "ISNNVKIEFNAQERWWINKSAPPAISNLKLNFLMEIDDHFISKRLHQKWVVRDFYFLFEN" +
                          "YSQLRFSLTFNSTSPEKTVTTLQERFPSPVETQSARILDEYAQRFNAKVVEKSHSLINSH" +
                          "IGAKNFVSQIVSEFKDEVIQPIGARTFGATILSYKPEEGIEQLMKSLQKIKPGDILVIRK" +
                          "AKFEAHKKIGKNEIINVGMDSAAPYSSVVTDYDFTKNKFRVIENHEGKIIQNSYKLSHMK" +
                          "SGKLKVFRIVARGYVGW";
        assertEquals( 1157, protein.getSequenceLength() );
        assertEquals( 1157, protein.getSequence().length() );
        assertEquals( sequence, protein.getSequence() );

        // DT lines
        try {
            SimpleDateFormat formatter = new SimpleDateFormat( "dd-MMM-yyyy" );

            Calendar calendar = Calendar.getInstance();
            calendar.setTime( protein.getLastSequenceUpdate() );
            calendar.set( Calendar.HOUR, 0 );
            calendar.set( Calendar.MINUTE, 0 );
            calendar.set( Calendar.SECOND, 0 );

            assertEquals( formatter.parse( "17-JAN-2003" ), calendar.getTime() );

            calendar = Calendar.getInstance();
            calendar.setTime( protein.getLastAnnotationUpdate() );
            calendar.set( Calendar.HOUR, 0 );
            calendar.set( Calendar.MINUTE, 0 );
            calendar.set( Calendar.SECOND, 0 );

            assertEquals( formatter.parse( "19-SEP-2006" ), calendar.getTime() );

            formatter = null;
        } catch ( ParseException e ) {
            fail( "Date parsing should not fail here." );
        }

        // functions
        assertEquals( 1, protein.getFunctions().size() );
        assertEquals( "Involved in the regulation of actin cytoskeleton", protein.getFunctions().iterator().next() );

        // keywords
        assertEquals( 5, protein.getKeywords().size() );

        // cross references
        assertEquals( 22, protein.getCrossReferences().size() );

        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "Z49295", "EMBL" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "Z49296", "EMBL", "CAA89312.1" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "AF373805", "EMBL", "AAL57239.1" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "1TG0", "PDB" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "1WDX", "PDB" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "P47068", "IntAct" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "141636", "GermOnline" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "YJL020C", "Ensembl" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "Y13136_GR", "GenomeReviews" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "S000003557", "SGD" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "SCER-S28-01:SCER-S28-01-003102-MONOMER", "BioCyc" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "P47068", "LinkHub" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030479", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0017024", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0005515", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030036", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0007010", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "IPR001452", "InterPro" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "PF00018", "Pfam" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "PD000066", "ProDom" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "SM00326", "SMART" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "PS50002", "PROSITE" ) ) );

        // splice variants
        assertEquals( 0, protein.getSpliceVariants().size() );

        // feature chain
        assertEquals( 1, protein.getFeatureChains().size() );
        UniprotFeatureChain featureChain = protein.getFeatureChains().iterator().next();
        assertEquals( "PRO_0000064839", featureChain.getId() );
        assertEquals( sequence, featureChain.getSequence() );
        assertEquals( 1, featureChain.getStart() );
        assertEquals( 1157, featureChain.getEnd() );
        assertEquals( protein.getOrganism(), featureChain.getOrganism() );
    }

    public void testSearchBySpliceVariant() throws UniprotBridgeException {

        // Q8NG31-1 has parent Q8NG31
        UniprotBridgeAdapter uniprot = getBridgeAdapter();

        Collection<UniprotProtein> proteins = uniprot.retreive( "Q8NG31-1" );

        assertEquals( 1, proteins.size() );
        UniprotProtein protein = proteins.iterator().next();
        // search for splice variant Q8NG31-1 in the protein
        boolean found = false;
        for ( UniprotSpliceVariant sv : protein.getSpliceVariants() ) {
            if ( "Q8NG31-1".equals( sv.getPrimaryAc() ) ) {
                found = true;
                break; // exits the loop
            }
        }

        if ( !found ) {
            fail( "Could not find Splice Variant: Q8NG31-1" );
        }
    }

    public void testSearchBySpliceVariantSecondaryId() throws UniprotBridgeException {

        // Q8NG31-1 has parent Q8NG31
        UniprotBridgeAdapter uniprot = getBridgeAdapter();

        // note: this splice variant is showing up in CDC42_CANFA, CDC42_HUMAN and CDC42_MOUSE
        Collection<UniprotProtein> proteins = uniprot.retreive( "P21181-1" );

        assertEquals( 3, proteins.size() );
        for ( UniprotProtein protein : proteins ) {

            // search for splice variant P21181-1 in the current protein
            boolean found = false;
            for ( UniprotSpliceVariant sv : protein.getSpliceVariants() ) {
                if ( sv.getSecondaryAcs().contains( "P21181-1" ) ) {
                    found = true;
                    System.out.println( "Found Splice Variant P21181-1 (secondary ac) in protein " + protein.getId() + "." );
                    break; // exits the splice variant loop
                }
            }

            if ( !found ) {
                fail( "Could not find Splice Variant P21181-1 (secondary ac) in protein " + protein.getId() + "." );
            }
        }
    }

    public void testRetreiveProteinWithSpliceVariant() throws UniprotBridgeException {

        UniprotBridgeAdapter uniprot = getBridgeAdapter();
        Collection<UniprotProtein> proteins = uniprot.retreive( "Q24208" );

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        UniprotProtein protein = proteins.iterator().next();
        assertNotNull( protein.getSpliceVariants() );
        assertEquals( 3, protein.getSpliceVariants().size() );

        boolean sv1 = false;
        boolean sv2 = false;
        boolean sv3 = false;

        System.out.println( "parent: " + protein.getSequence() );
        System.out.println( "parent's length: " + protein.getSequence().length() );

        for ( UniprotSpliceVariant sv : protein.getSpliceVariants() ) {

            assertEquals( sv.getPrimaryAc(), protein.getOrganism(), sv.getOrganism() );

            if ( "Q24208-1".equals( sv.getPrimaryAc() ) ) {

                assertEquals( 0, sv.getSecondaryAcs().size() );
                assertEquals( "eIF-2-gamma", sv.getSynomyms().iterator().next() );
                assertEquals( "MATAEAQIGVNRNLQKQDLSNLDVSKLTPLSPEVISRQATINIGTIGHVAHGKSTVVKAI" +
                              "SGVQTVRFKNELERNITIKLGYANAKIYKCDNPKCPRPASFVSDASSKDDSLPCTRLNCS" +
                              "GNFRLVRHVSFVDCPGHDILMATMLNGAAVMDAALLLIAGNESCPQPQTSEHLAAIEIMK" +
                              "LKQILILQNKIDLIKESQAKEQYEEITKFVQGTVAEGAPIIPISAQLKYNIDVLCEYIVN" +
                              "KIPVPPRDFNAPPRLIVIRSFDVNKPGCEVADLKGGVAGGSILSGVLKVGQEIEVRPGVV" +
                              "TKDSDGNITCRPIFSRIVSLFAEQNELQYAVPGGLIGVGTKIDPTLCRADRLVGQVLGAV" +
                              "GQLPDIYQELEISYYLLRRLLGVRTDGDKKGARVEKLQKNEILLVNIGSLSTGGRISATK" +
                              "GDLAKIVLTTPVCTEKGEKIALSRRVENHWRLIGWGQIFGGKTITPVLDSQVAKK", sv.getSequence() );
                sv1 = true;

            } else if ( "P45975-1".equals( sv.getPrimaryAc() ) ) {

                assertEquals( 0, sv.getSecondaryAcs().size() );
                assertEquals( 1, sv.getSynomyms().size() );
                assertEquals( "Su(var)3-9", sv.getSynomyms().iterator().next() );
                assertEquals( "MATAEAQIGVNRNLQKQDLSNLDVSKLTPLSPEVISRQATINIGTIGHVAHGKSTVVKAI" +
                              "SGVQTVRFKNELERNITIKLERLSEKKIKNLLTSKQQRQQYEIKQRSMLRHLAELRRHSR" +
                              "FRRLCTKPASSSMPASTSSVDRRTTRRSTSQTSLSPSNSSGYGSVFGCEEHDVDKIPSLN" +
                              "GFAKLKRRRSSCVGAPTPNSKRSKNNMGVIAKRPPKGEYVVERIECVEMDQYQPVFFVKW" +
                              "LGYHDSENTWESLANVADCAEMEKFVERHQQLYETYIAKITTELEKQLEALPLMENITVA" +
                              "EVDAYEPLNLQIDLILLAQYRAAGSRSQREPQKIGERALKSMQIKRAQFVRRKQLADLAL" +
                              "FEKRMNHVEKPSPPIRVENNIDLDTIDSNFMYIHDNIIGKDVPKPEAGIVGCKCTEDTEE" +
                              "CTASTKCCARFAGELFAYERSTRRLRLRPGSAIYECNSRCSCDSSCSNRLVQHGRQVPLV" +
                              "LFKTANGSGWGVRAATALRKGEFVCEYIGEIITSDEANERGKAYDDNGRTYLFDLDYNTA" +
                              "QDSEYTIDAANYGNISHFINHSCDPNLAVFPCWIEHLNVALPHLVFFTLRPIKAGEELSF" +
                              "DYIRADNEDVPYENLSTAVRVECRCGRDNCRKVLF", sv.getSequence() );
                sv2 = true;

            } else if ( "Q24208-2".equals( sv.getPrimaryAc() ) ) {

                assertEquals( 0, sv.getSecondaryAcs().size() );
                assertEquals( 0, sv.getSynomyms().size() );
                assertEquals( null, sv.getStart() );
                assertEquals( null, sv.getEnd() );

                String s = "MHLRGDVLLGGVAADVSKLTPLSPEVISRQATINIGTIGHVAHGKSTVVKAISGVQTVRF" +
                           "KNELERNITIKLGYANAKIYKCDNPKCPRPASFVSDASSKDDSLPCTRLNCSGNFRLVRH" +
                           "VSFVDCPGHDILMATMLNGAAVMDAALLLIAGNESCPQPQTSEHLAAIEIMKLKQILILQ" +
                           "NKIDLIKESQAKEQYEEITKFVQGTVAEGAPIIPISAQLKYNIDVLCEYIVNKIPVPPRD" +
                           "FNAPPRLIVIRSFDVNKPGCEVADLKGGVAGGSILSGVLKVGQEIEVRPGVVTKDSDGNI" +
                           "TCRPIFSRIVSLFAEQNELQYAVPGGLIGVGTKIDPTLCRADRLVGQVLGAVGQLPDIYQ" +
                           "ELEISYYLLRRLLGVRTDGDKKGARVEKLQKNEILLVNIGSLSTGGRISATKGDLAKIVL" +
                           "TTPVCTEKGEKIALSRRVENHWRLIGWGQIFGGKTITPVLDSQVAKK";

                System.out.println( s.length() );
                System.out.println( s + "\n" );
                System.out.println( sv.getSequence() );
                System.out.println( sv.getSequence().length() );

                assertEquals( "MMHLRGDVLLGGVAADVSKLTPLSPEVISRQATINIGTIGHVAHGKSTVVKAISGVQTVRF" +
                              "KNELERNITIKLGYANAKIYKCDNPKCPRPASFVSDASSKDDSLPCTRLNCSGNFRLVRH" +
                              "VSFVDCPGHDILMATMLNGAAVMDAALLLIAGNESCPQPQTSEHLAAIEIMKLKQILILQ" +
                              "NKIDLIKESQAKEQYEEITKFVQGTVAEGAPIIPISAQLKYNIDVLCEYIVNKIPVPPRD" +
                              "FNAPPRLIVIRSFDVNKPGCEVADLKGGVAGGSILSGVLKVGQEIEVRPGVVTKDSDGNI" +
                              "TCRPIFSRIVSLFAEQNELQYAVPGGLIGVGTKIDPTLCRADRLVGQVLGAVGQLPDIYQ" +
                              "ELEISYYLLRRLLGVRTDGDKKGARVEKLQKNEILLVNIGSLSTGGRISATKGDLAKIVL" +
                              "TTPVCTEKGEKIALSRRVENHWRLIGWGQIFGGKTITPVLDSQVAKK", sv.getSequence() );
                sv3 = true;

            } else {
                fail( "Unknown splice variant: " + sv.getPrimaryAc() );
            }
        } // for

        assertTrue( "Q24208-1 was missing from the splice variant list.", sv1 );
        assertTrue( "P45975-1 was missing from the splice variant list.", sv2 );
        assertTrue( "Q24208-2 was missing from the splice variant list.", sv3 );
    }

    public void testRetreiveMultipleProteins() throws UniprotBridgeException {
        UniprotBridgeAdapter uniprot = getBridgeAdapter( new IntactCrossReferenceFilter() );
        Collection<UniprotProtein> proteins = uniprot.retreive( "P21181" );

        assertNotNull( proteins );
        assertEquals( 3, proteins.size() );

        Collection<String> ids = new ArrayList<String>();
        ids.add( "CDC42_CANFA" );
        ids.add( "CDC42_HUMAN" );
        ids.add( "CDC42_MOUSE" );

        for ( UniprotProtein protein : proteins ) {
            assertTrue( ids.contains( protein.getId() ) );
        }
    }

    public void testRetreiveSimpleProteinWithCrossReferenceFilter() throws UniprotBridgeException {
        UniprotBridgeAdapter uniprot = getBridgeAdapter( new IntactCrossReferenceFilter() );
        Collection<UniprotProtein> proteins = uniprot.retreive( "P47068" );

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        UniprotProtein protein = proteins.iterator().next();

        // check that we have not so many cross references
        // cross references
        assertEquals( 7, protein.getCrossReferences().size() );

        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "1TG0", "PDB" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "1WDX", "PDB" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "S000003557", "SGD" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030479", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0017024", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0005515", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030036", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0007010", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "IPR001452", "InterPro" ) ) );
    }

    public void testRetreiveUnknownProtein() throws UniprotBridgeException {
        UniprotBridgeAdapter ya = new YaspAdapter();
        Collection<UniprotProtein> proteins = ya.retreive( "foobar" );
        assertNotNull( proteins );
        assertEquals( 0, proteins.size() );
        assertNotNull( ya.getErrors() );
        assertEquals( 1, ya.getErrors().size() );
        assertTrue( ya.getErrors().keySet().contains( "foobar" ) );
    }
}
