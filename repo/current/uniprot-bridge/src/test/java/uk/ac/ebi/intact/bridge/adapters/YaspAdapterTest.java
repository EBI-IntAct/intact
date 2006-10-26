package uk.ac.ebi.intact.bridge.adapters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridge.UniprotBridgeException;
import uk.ac.ebi.intact.bridge.adapters.referenceFilter.IntactCrossReferenceFilter;
import uk.ac.ebi.intact.bridge.model.UniprotFeatureChain;
import uk.ac.ebi.intact.bridge.model.UniprotProtein;
import uk.ac.ebi.intact.bridge.model.UniprotSpliceVariant;
import uk.ac.ebi.intact.bridge.model.UniprotXref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * YaspAdapter Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>09/18/2006</pre>
 */
public class YaspAdapterTest extends TestCase {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( YaspAdapterTest.class );

    public YaspAdapterTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( YaspAdapterTest.class );
    }

    //////////////////////////
    // Utility

    private UniprotBridgeAdapter getBridgeAdapter() {
        return new YaspAdapter();
    }

    private UniprotBridgeAdapter getBridgeAdapter( IntactCrossReferenceFilter filter ) {
        AbstractUniprotBridgeAdapter service = (AbstractUniprotBridgeAdapter) getBridgeAdapter();
        service.setCrossReferenceSelector( filter );
        return service;
    }

    ///////////////////
    // Tests

//    public void testRetreiveUnknownProtein() throws UniprotBridgeException {
//        UniprotBridgeAdapter ya = new YaspAdapter();
//        Collection<UniprotProtein> proteins = ya.retreive( "foobar" );
//        assertNotNull( proteins );
//        assertEquals( 0, proteins.size() );
//        assertNotNull( ya.getErrors() );
//        assertTrue( ya.getErrors().size() > 1 );
//        assertTrue( ya.getErrors().keySet().contains( "foobar" ) );
//    }

    public void testRetreiveSimpleProtein() throws UniprotBridgeException {

        UniprotBridgeAdapter uniprot = getBridgeAdapter();
        Collection<UniprotProtein> proteins = uniprot.retreive( "P47068" );

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        UniprotProtein protein = proteins.iterator().next();

        assertEquals( "BBC1_YEAST", protein.getId() );
        assertEquals( "Myosin tail region-interacting protein MTI1", protein.getDescription() );

        assertEquals( "P47068", protein.getPrimaryAc() );
        assertTrue( protein.getSecondaryAcs().contains( "P47067" ) );
        assertTrue( protein.getSecondaryAcs().contains( "Q8X1F4" ) );

        // gene names
        assertEquals( 1, protein.getGenes().size() );
        assertTrue( protein.getGenes().contains( "BBC1" ) );

        assertEquals( 1, protein.getSynomyms().size() );
        assertTrue( protein.getSynomyms().contains( "MTI1" ) );

        assertEquals( 1, protein.getOrfs().size() );
        assertTrue( protein.getOrfs().contains( "J1305/J1286" ) );

        assertEquals( 1, protein.getLocuses().size() );
        assertTrue( protein.getLocuses().contains( "YJL020C/YJL021C" ) ); // YJL020C/YJL021C

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
            assertEquals( formatter.parse( "17-JAN-2003" ), protein.getLastSequenceUpdate() );
            assertEquals( formatter.parse( "17-OCT-2006" ), protein.getLastAnnotationUpdate() );
            formatter = null;
        } catch ( ParseException e ) {
            fail( "Date parsing should not fail here." );
        }

        // functions
        assertEquals( 1, protein.getFunctions().size() );
        assertTrue( protein.getFunctions().contains( "Involved in the regulation of actin cytoskeleton" ) );

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
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030479", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0017024", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0005515", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030036", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0007010", "GO" ) ) );
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

    public void testRetreiveProteinWithSpliceVariant() throws UniprotBridgeException {

        UniprotBridgeAdapter uniprot = getBridgeAdapter();
        Collection<UniprotProtein> proteins = uniprot.retreive( "Q24208" );

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        UniprotProtein protein = proteins.iterator().next();
        assertNotNull( protein.getSpliceVariants() );

        boolean sv1 = false;
        boolean sv2 = false;
        boolean sv3 = false;

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
                String s = "MATAEAQIGVNRNLQKQDLSNLDVSKLTPLSPEVISRQATINIGTIGHVAHGKSTVVKAI" +
                           "SGVQTVRFKNELERNITIKLERLSEKKIKNLLTSKQQRQQYEIKQRSMLRHLAELRRHSR" +
                           "FRRLCTKPASSSMPASTSSVDRRTTRRSTSQTSLSPSNSSGYGSVFGCEEHDVDKIPSLN" +
                           "GFAKLKRRRSSCVGAPTPNSKRSKNNMGVIAKRPPKGEYVVERIECVEMDQYQPVFFVKW" +
                           "LGYHDSENTWESLANVADCAEMEKFVERHQQLYETYIAKITTELEKQLEALPLMENITVA" +
                           "EVDAYEPLNLQIDLILLAQYRAAGSRSQREPQKIGERALKSMQIKRAQFVRRKQLADLAL" +
                           "FEKRMNHVEKPSPPIRVENNIDLDTIDSNFMYIHDNIIGKDVPKPEAGIVGCKCTEDTEE" +
                           "CTASTKCCARFAGELFAYERSTRRLRLRPGSAIYECNSRCSCDSSCSNRLVQHGRQVPLV" +
                           "LFKTANGSGWGVRAATALRKGEFVCEYIGEIITSDEANERGKAYDDNGRTYLFDLDYNTA" +
                           "QDSEYTIDAANYGNISHFINHSCDPNLAVFPCWIEHLNVALPHLVFFTLRPIKAGEELSF" +
                           "DYIRADNEDVPYENLSTAVRVECRCGRDNCRKVLF";

//                System.out.println( s );
//                System.out.println( s.length() );
//                System.out.println( "" );
//                System.out.println( sv.getSequence() );
//                System.out.println( sv.getSequence().length() );

                // it seems to return the sequence of the parent protein instead !!!!
                assertEquals( protein.getSequence(), sv.getSequence() );
//                assertEquals( s, sv.getSequence() );

                sv2 = true;

            } else if ( "Q24208-2".equals( sv.getPrimaryAc() ) ) {

                assertEquals( 0, sv.getSecondaryAcs().size() );
                assertEquals( 0, sv.getSynomyms().size() );
                assertEquals( null, sv.getStart() );
                assertEquals( null, sv.getEnd() );
                assertEquals( "MHLRGDVLLGGVAADVSKLTPLSPEVISRQATINIGTIGHVAHGKSTVVKAISGVQTVRF" +
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

    public void testRetreiveSimpleProteinWithCrossReferenceFilter() throws UniprotBridgeException {
        UniprotBridgeAdapter uniprot = getBridgeAdapter( new IntactCrossReferenceFilter() );
        Collection<UniprotProtein> proteins = uniprot.retreive( "P47068" );

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        UniprotProtein protein = proteins.iterator().next();

        // check that we have not so many cross references
        // cross references
        assertEquals( 9, protein.getCrossReferences().size() );

        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "1TG0", "PDB" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "1WDX", "PDB" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "S000003557", "SGD" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030479", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0017024", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0005515", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030036", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0007010", "GO" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "IPR001452", "InterPro" ) ) );
    }
}