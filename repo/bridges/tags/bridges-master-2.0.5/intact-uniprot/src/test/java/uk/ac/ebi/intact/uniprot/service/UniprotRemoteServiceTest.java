package uk.ac.ebi.intact.uniprot.service;

import static junit.framework.Assert.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.Ignore;
import uk.ac.ebi.intact.uniprot.UniprotServiceException;
import uk.ac.ebi.intact.uniprot.data.MockUniProtEntries;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotProteinType;
import uk.ac.ebi.intact.uniprot.model.UniprotSpliceVariant;
import uk.ac.ebi.intact.uniprot.model.UniprotXref;
import uk.ac.ebi.intact.uniprot.service.referenceFilter.CrossReferenceFilter;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * UniprotRemoteServiceAdapter Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.0
 */
public class UniprotRemoteServiceTest {

    public static final Log log = LogFactory.getLog( UniprotRemoteServiceTest.class );

    /////////////////////
    // Utility

    private UniprotService getUniprotService() {
        return new UniprotRemoteService();
    }

    private UniprotService getUniprotService( CrossReferenceFilter filter ) {
        if ( filter == null ) {
            fail( "you must give a non null filter !!" );
        }
        AbstractUniprotService service = ( AbstractUniprotService ) getUniprotService();
        service.setCrossReferenceSelector( filter );
        return service;
    }

    ////////////////////
    // Tests

    @Test
    public void Convert_CDC42_CANFA() throws Exception {
        UniProtEntry entry = MockUniProtEntries.build_P60952();
        UniprotRemoteService service = new UniprotRemoteService();
        UniprotProtein protein = service.buildUniprotProtein( entry );

        assertEquals( 2, protein.getSpliceVariants().size() );

        UniprotSpliceVariant sv1 = searchSpliceVariantByIsoId( "P60952-1", protein );
        assertNotNull( sv1 );
        assertTrue( sv1.getSecondaryAcs().contains( "P21181-1" ) );
        assertEquals( "Has not been isolated in dog so far", sv1.getNote() );
        assertEquals( 1, sv1.getSynomyms().size() );
        assertEquals( "Brain", sv1.getSynomyms().iterator().next() );
        assertEquals( protein.getSequence(), sv1.getSequence() );

        UniprotSpliceVariant sv2 = searchSpliceVariantByIsoId( "P60952-2", protein );
        assertNotNull( sv2 );
        assertTrue( sv2.getSecondaryAcs().contains( "P21181-4" ) );
        assertEquals( "", sv2.getNote() );
        assertEquals( 1, sv2.getSynomyms().size() );
        assertEquals( "Placental", sv2.getSynomyms().iterator().next() );
    }

    @Test
    public void RetrieveByUniprotId() throws Exception {
        Collection<UniprotProtein> prots = getUniprotService().retrieve( "CDC2_HUMAN" );
        final UniprotProtein uniprotProtein = prots.iterator().next();
        assertEquals( "P06493", uniprotProtein.getPrimaryAc() );
        assertEquals( "Cell division control protein 2 homolog", uniprotProtein.getDescription());
        assertEquals(2, uniprotProtein.getSynomyms().size());
    }

    @Test
    public void RetrieveByUniprotId_noGeneNames() throws Exception {
        Collection<UniprotProtein> prots = getUniprotService().retrieve( "O58917" );

        UniprotProtein uniprotProtein = prots.iterator().next();
        assertTrue( uniprotProtein.getGenes().isEmpty() );
    }

    @Test
    public void RetrieveBySpliceVariantId() throws Exception {
        Collection<UniprotProtein> prots = getUniprotService().retrieve( "Q13535-1" );
        assertEquals( "ATR_HUMAN", prots.iterator().next().getId() );
    }

    @Test
    public void Convert_FAU_DROME() throws Exception {

        UniProtEntry entry = MockUniProtEntries.build_Q9VGX3();
        UniprotRemoteService service = new UniprotRemoteService();
        UniprotProtein protein = service.buildUniprotProtein( entry );

        assertTrue(entry.getProteinDescription().hasRecommendedName());

        assertEquals( UniprotProteinType.SWISSPROT, protein.getSource() );

        assertEquals( "FAU_DROME", protein.getId() );

        assertEquals( "Q9VGX3", protein.getPrimaryAc() );

        assertEquals( 4, protein.getSecondaryAcs().size() );
        assertTrue( protein.getSecondaryAcs().contains( "Q95S18" ) );
        assertTrue( protein.getSecondaryAcs().contains( "Q9VGX1" ) );
        assertTrue( protein.getSecondaryAcs().contains( "Q9VGX2" ) );
        assertTrue( protein.getSecondaryAcs().contains( "Q9Y0F9" ) );

        assertEquals( "Protein anoxia up-regulated", protein.getDescription() );

        assertEquals( 1, protein.getGenes().size() );
        assertTrue( protein.getGenes().contains( "fau" ) );

        assertEquals( 0, protein.getSynomyms().size() );

        assertEquals( 1, protein.getOrfs().size() );
        assertTrue( protein.getOrfs().contains( "CG6544" ) );

        assertEquals( 0, protein.getLocuses().size() );

        SimpleDateFormat sdf = new SimpleDateFormat( "dd-MMM-yyyy" );
        assertEquals( sdf.parse( "23-JAN-2007" ), protein.getLastAnnotationUpdate() );
        assertEquals( sdf.parse( "19-JUL-2004" ), protein.getLastSequenceUpdate() );
        assertEquals( "SP_41", protein.getReleaseVersion() );

        assertEquals( 0, protein.getKeywords().size() );

        assertEquals( 0, protein.getDiseases().size() );

        assertEquals( 7227, protein.getOrganism().getTaxid() );
        assertEquals( "Drosophila melanogaster", protein.getOrganism().getName() );

        assertEquals( "7DDCB26AD1AB9CEE", protein.getCrc64() );
        assertEquals( "MVYESGFTTRRTYSSRPVTTSYAVTYPSVEKVTRVYKSSYPIYSSYSVPRRVYGATRVVT" +
                      "SPIRVVTSPARVVSRVIHSPSPVRVVRTTTRVISSPERTTYSYTTPSTYYSPSYLPSTYT" +
                      "STYIPTSYTTYTPSYAYSPTTVTRVYAPRSSLSPLRITPSPVRVITSPVRSVPSYLKRLP" +
                      "PGYGARALTNYLNTEPFTTFSEETSRIRNRAQSLIRDLHTPVVRRARSCTPFPVTGYTYE" +
                      "PASQLALDAYVARVTNPVRHIAKEVHNISHYPRPAVKYVDAELDPNRPSRKFSAPRPLED" +
                      "PLDVEAKEKQRLRQERLLTVNEEALDEVDLEKKRAQKADEAKRREERALKEERDRLTAEA" +
                      "EKQAAAKAKKAAEEAAKIAAEEALLAEAAAQKAAEEAKALKAAEDAAQKAAEEARLAEEA" +
                      "AAQKVAEEAAQKAAEEARLAEEAAAQKAAEEAAQKAAEEAALKAAEEARLAEEAAQKAAE" +
                      "EAALKAVEEARAAEEAAQKAAEEARVAEEARLEEEQRVREQELERLAEIEKESEGELARQ" +
                      "AAELAEIARQESELAAQELQAIQKNENETSEPVVEEPVTPVEEQEPIIELGSNVTPTGGN" +
                      "SYEEDLDAEEEEDEEEEEE", protein.getSequence() );
        assertEquals( protein.getSequence().length(), protein.getSequenceLength() );

        assertEquals( 4, protein.getCrossReferences().size() );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0005515", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0006979", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "CG6544", "Ensembl" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "FBgn0020439", "FlyBase" ) ) );

        assertEquals( 2, protein.getSpliceVariants().size() );

        UniprotSpliceVariant sv1 = searchSpliceVariantByIsoId( "Q9VGX3-5", protein );
        assertNotNull( sv1 );
        assertEquals( protein.getSequence(), sv1.getSequence() );

        UniprotSpliceVariant sv2 = searchSpliceVariantByIsoId( "Q9VGX3-2", protein );
        assertNotNull( sv2 );
        assertEquals( "MVYESGFTTRRTYSSRPVTTSYAVTYPSVEKVTRVYKSSYPIYSSYSVPRRVYGATRVVTSPIRVVTSPARVVSRVIHSPSPVRVVRTTTRVISSPERTTYSYTTPSTYYSPSYLPSTYTSTYIPTSYTTYTPSYAYSPTTVTRVYAPRSSLSPLRITPSPVRVITSPVRSVPSYLKRLPPGYGARALTNYLNTEPFTTFSEETSRIRNRAQSLIRDLHTPVVRRARSCTPFPVTGYTYEPASQLALDAYVARVTNPVRHIAKEVHNISHYPRPAVKYVDAELDPNRPSRKFSAPRPLEDPLDVEAKEKQRLRQERLLTVNEEALDEVDLEKKRAQKADEAKRREERALKEERDRLTAEAEKQAAAKAKKAAEEAAKIAAEEALLAEAAAQKAAEEAKALKAAEDAAQKAAEEARLAEEAAAQKVAEEAAQKAAEEARLAEEAAAQKAAEEAAQKAAEEAALKAAEEARLAEEAAQKAAEEAALKAVEEARAAEEAAQKAAEEARVAEEARLEEEQRVREQELERLAEIEKESEGELARQAAELAEIARQESELAAQELQAIQKNENETSEPVVEEPVTPVEEQEPIIELGSNVTPTGGNSYEEDLDAEEEEDEEEEEE",
                      sv2.getSequence() );

        assertEquals( 0, protein.getFeatureChains().size() );
    }

    private UniprotSpliceVariant searchSpliceVariantByIsoId( String isoId, UniprotProtein protein ) {

        for ( UniprotSpliceVariant usv : protein.getSpliceVariants() ) {
            if ( usv.getPrimaryAc().equals( isoId ) ) {
                return usv;
            }
        }
        return null;
    }

    @Test
    public void Build() throws Exception {
        UniprotService uniprot = getUniprotService();

        Collection<UniprotProtein> proteins = uniprot.retrieve( "P47068" );

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

        assertEquals( 2, protein.getSynomyms().size() );
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

            formatter = null;
        } catch ( ParseException e ) {
            fail( "Date parsing should not fail here." );
        }

        // functions
        assertEquals( 1, protein.getFunctions().size() );
        assertEquals( "Involved in the regulation of actin cytoskeleton", protein.getFunctions().iterator().next() );

        // keywords
        assertEquals( 7, protein.getKeywords().size() );

        // cross references
        assertEquals( 34, protein.getCrossReferences().size() );

        // splice variants
        assertEquals( 0, protein.getSpliceVariants().size() );

        // feature chain
        assertEquals( 0, protein.getFeatureChains().size() );
    }

    @Test
    public void SearchBySpliceVariant() throws UniprotServiceException {

        // Q8NG31-1 has parent Q8NG31
        UniprotService uniprot = getUniprotService();

        Collection<UniprotProtein> proteins = uniprot.retrieve( "Q8NG31-1" );

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

    @Test
    public void SearchBySpliceVariantSecondaryId() throws UniprotServiceException {

        // Q8NG31-1 has parent Q8NG31
        UniprotService uniprot = getUniprotService();

        // note: this splice variant is showing up in CDC42_CANFA, CDC42_HUMAN and CDC42_MOUSE
        Collection<UniprotProtein> proteins = uniprot.retrieve( "P21181-1" );

        assertEquals( 3, proteins.size() );
        for ( UniprotProtein protein : proteins ) {

            // search for splice variant P21181-1 in the current protein
            boolean found = false;
            for ( UniprotSpliceVariant sv : protein.getSpliceVariants() ) {
                if ( sv.getSecondaryAcs().contains( "P21181-1" ) ) {
                    found = true;
                    log.debug( "Found Splice Variant P21181-1 (secondary ac) in protein " + protein.getId() + "." );
                    break; // exits the splice variant loop
                }
            }

            if ( !found ) {
                fail( "Could not find Splice Variant P21181-1 (secondary ac) in protein " + protein.getId() + "." );
            }
        }
    }

    @Test
    public void RetreiveProteinWithSpliceVariant() throws UniprotServiceException {

        UniprotService uniprot = getUniprotService();
        Collection<UniprotProtein> proteins = uniprot.retrieve( "Q24208" );

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );

        UniprotProtein protein = proteins.iterator().next();
        assertNotNull( protein.getSpliceVariants() );
        assertEquals( 3, protein.getSpliceVariants().size() );

        boolean sv1 = false;
        boolean sv2 = false;
        boolean sv3 = false;

        log.debug( "" );
        log.debug( "parent: " + protein.getSequence() );
        log.debug( "parent's length: " + protein.getSequence().length() );

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

                log.debug( s.length() );
                log.debug( s + "\n" );
                log.debug( sv.getSequence() );
                log.debug( sv.getSequence().length() );

                assertEquals( "MHLRGDVLLGGVAADVSKLTPLSPEVISRQATINIGTIGHVAHGKSTVVKAISGVQT" +
                              "VRFKNELERNITIKLGYANAKIYKCDNPKCPRPASFVSDASSKDDSLPCTRLNCSGN" +
                              "FRLVRHVSFVDCPGHDILMATMLNGAAVMDAALLLIAGNESCPQPQTSEHLAAIEIM" +
                              "KLKQILILQNKIDLIKESQAKEQYEEITKFVQGTVAEGAPIIPISAQLKYNIDVLCE" +
                              "YIVNKIPVPPRDFNAPPRLIVIRSFDVNKPGCEVADLKGGVAGGSILSGVLKVGQEI" +
                              "EVRPGVVTKDSDGNITCRPIFSRIVSLFAEQNELQYAVPGGLIGVGTKIDPTLCRAD" +
                              "RLVGQVLGAVGQLPDIYQELEISYYLLRRLLGVRTDGDKKGARVEKLQKNEILLVNI" +
                              "GSLSTGGRISATKGDLAKIVLTTPVCTEKGEKIALSRRVENHWRLIGWGQIFGGKTI" +
                              "TPVLDSQVAKK", sv.getSequence() );
                sv3 = true;

            } else {
                fail( "Unknown splice variant: " + sv.getPrimaryAc() );
            }
        } // for

        assertTrue( "Q24208-1 was missing from the splice variant list.", sv1 );
        assertTrue( "P45975-1 was missing from the splice variant list.", sv2 );
        assertTrue( "Q24208-2 was missing from the splice variant list.", sv3 );
    }

    @Test
    public void RetreiveMultipleProteins() throws UniprotServiceException {
        UniprotService uniprot = getUniprotService();
        Collection<UniprotProtein> proteins = uniprot.retrieve( "P21181" );

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

    @Test
    public void RetreiveSimpleProteinWithCrossReferenceFilter() throws UniprotServiceException {
        CrossReferenceFilter filter = new CrossReferenceFilter() {

            public boolean isSelected(String database) {
                return getFilteredDatabases().contains(database);
            }

            public List<String> getFilteredDatabases() {
                return Arrays.asList("PDB", "SGD", "Go", "InterPro");
            }

            public String getMi(String databaseName) {
                return null;
            }
        };

        UniprotService uniprot = getUniprotService( filter );
        Collection<UniprotProtein> proteins = uniprot.retrieve( "P47068" );

        assertNotNull( proteins );
        assertEquals( 1, proteins.size() );
        UniprotProtein protein = proteins.iterator().next();

        assertEquals( UniprotProteinType.SWISSPROT, protein.getSource() );

        // check that we have not so many cross references
        // cross references
        assertEquals( 8, protein.getCrossReferences().size() );

        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "1TG0", "PDB" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "1WDX", "PDB" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "S000003557", "SGD" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030479", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0017024", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "GO:0030036", "Go" ) ) );
        assertTrue( protein.getCrossReferences().contains( new UniprotXref( "IPR001452", "InterPro" ) ) );
    }
}
