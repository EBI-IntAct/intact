package uk.ac.ebi.intact.bridges.unisave;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * UnisaveService Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.3
 */
public class UnisaveServiceTest {


    /*
         Primary (citable) accession number: Q96IZ0
         Secondary accession number(s): O75796, Q6FHY9, Q8N700
                         isSecondary  isNotSecondary
         primary id          ex            49       (Q96IZ0)
         secondary id        36            18       (Q6FHY9)
     */

    @Test
    public void getVersions_2() throws Exception {
        UnisaveService service = new UnisaveService();
        final List<Integer> versions = service.getVersions( "Q98753", false );
        Assert.assertNotNull( versions );
    }


    @Test
    public void getFastaSequence_1() throws Exception {
        UnisaveService service = new UnisaveService();
        String id = "Q98753";
        service.getFastaSequence( id , 7);
    }

    @Test
    public void getFastaSequence_2() throws Exception {
        UnisaveService service = new UnisaveService();
        String id = "Q00001";
        service.getFastaSequence( id , 7);
    }

    @Test
    public void getLastSequenceReleased() throws Exception {
        UnisaveService service = new UnisaveService();

        String sequence = service.getLastSequenceAtTheDate("Q98753", false, new Date(System.currentTimeMillis()));

        Assert.assertNotNull( sequence );
        Assert.assertEquals("VPFLSKAVRCGPVIPFVIHHFNFRRVTTTKRRRNKYVLVPGYGWVLQDDYLVNSVKMTGE" +
                "NDLPPNQLPHDDDLLFTYAKILLYDYISYFPKFRHNNPDLLDHKTELELFPLKADSAARN" +
                "KANFYARTLWNDTITDKSAFKPGTYNDTVAGLLLWQQCALMWSLPKSVINRTISGVCDAL" +
                "TNRTSLTLLKRISDWLKQLGLACSPIHRLFIELPTLLGRGAIPGDADKDIKHRLAFDPSI" +
                "TVDVPKEQLHLLIYRLLSRNLNITKVNSFEHHLEERLLWSKSGSHYYPDDKINELLPPQP" +
                "TRKEFLDVVTTEYIKECKPQVFIRQSRKLEHGKERFIYNCDTVSYVYFDFILKLFETGWQ" +
                "DSEAILSPGDYTSERLHAKISSYKYKAMLDYTDFNSQHTIQSMRLIFETMKELLPPEATF" +
                "ALDWCIASFDNMQTSDGLKWMATLPSGHRATTFINTVLNWCYTQMVGLKFDSFMCAGDDV" +
                "ILMSQQPISLAPILTSHFKFNPSKQSTGTRGEFLRKHYSEAGVFAYPCRAIASLVSGNWL" +
                "SQSLRENTPILVPIQNGIDRLRSRAGLLGVPWKLGLSELIEREAIPKEVGMALLNSHAAG" +
                "PGLITRDYSSFTVTPKPPKLSSTLEYTATRYGLQDLSKHVPWKQLTTVESDKLSRQIKKI" +
                "SYRHCSQAKITYNCTYEVFKPRGLPTVLSGSSQPSLSMLWWQAMLKQAIQDDSTKKIDAR" +
                "MFAANACTSSVSGDAFLRANASMAGVLITSLITSSS", sequence);
    }

    @Test
    public void getLastSequenceReleased_P51875() throws Exception {
        UnisaveService service = new UnisaveService();

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String sequence = service.getLastSequenceAtTheDate("P51875", false, format.parse("2007/09/17"));

        Assert.assertNotNull( sequence );
        Assert.assertEquals("MGCTMSQEERAALERSRMIEKNLKEDGMQAAKDIKLLLLGAGESGKSTIVKQMKIIHESGFTAEDYKQYKPVVYSNTVQSLVAILRAMSNLGVSFGSADREVDA" +
                "KLVMDVVARMEDTEPFSEELLSSMKRLWGDAGVQDCFSRSNEYQLNDSAKYFLDDLERLGEAIYQPTEQDILRTRVKTTGIVEVHFTFKNLNFKLFDVGGQRSERKKWIHCFEDVTA" +
                "IIFCVAMSEYDQVLHEDETTNRMHESLKLFDSICNNKWFTDTSIILFLNKKDLFEEKIKKSPLTICFPEYSGRQDYHEASAYIQAQFEAKNKSANKEIYCHMTCATDTTNIQFVFDA" +
                "VTDVIIANNLRGCGLY", sequence);
        Assert.assertEquals(354, sequence.length());

    }

    @Test
    public void getSequenceVersion(){
        String sequence = "MNKLAILAIIAMVLFSANAFRFQSRIRSNVEAKTETRDLCEQSALQCNEQGCHNFCSPEDKPGCLGMVWNPELVP";
        String uniprotAc = "P12350";
        UnisaveService service = new UnisaveService();

        try {
            int sequenceVersion = service.getSequenceVersion(uniprotAc, false, sequence);

            Assert.assertEquals(2, sequenceVersion);
        } catch (UnisaveServiceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSequenceForSequenceVersion(){
        String sequence = "MNKLAILAIIAMVLFSANAFRFQSRIRSNVEAKTETRDLCEQSALQCNEQGCHNFCSPEDKPGCLGMVWNPELVP";
        String uniprotAc = "P12350";
        UnisaveService service = new UnisaveService();

        try {
            String sequenceFromUnisave = service.getSequenceFor(uniprotAc, false, 2);

            Assert.assertEquals(sequence, sequenceFromUnisave);
        } catch (UnisaveServiceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getSequenceVersion_sequence_not_found(){
        String sequence = "MNKLAI";
        String uniprotAc = "P12350";
        UnisaveService service = new UnisaveService();

        try {
            int sequenceVersion = service.getSequenceVersion(uniprotAc, false, sequence);

            Assert.assertEquals(-1, sequenceVersion);
        } catch (UnisaveServiceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLastSequenceReleased_P51875_2() throws Exception {
        UnisaveService service = new UnisaveService();

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String sequence = service.getLastSequenceAtTheDate("P51875", false, format.parse("2006/09/01"));

        Assert.assertNotNull( sequence );
        Assert.assertEquals("GCTMSQEERAALERSRMIEKNLKEDGMQAAKDIKLLLLGAGESGKSTIVKQMKIIHESGF" +
                "TAEDYKQYKPVVYSNTVQSLVAILRAMSNLGVSFGSADREVDAKLVMDVVARMEDTEPFS" +
                "EELLSSMKRLWGDAGVQDCFSRSNEYQLNDSAKYFLDDLERLGEAIYQPTEQDILRTRVK" +
                "TTGIVEVHFTFKNLNFKLFDVGGQRSERKKWIHCFEDVTAIIFCVAMSEYDQVLHEDETT" +
                "NRMHESLKLFDSICNNKWFTDTSIILFLNKKDLFEEKIKKSPLTICFPEYSGRQDYHEAS" +
                "AYIQAQFEAKNKSANKEIYCHMTCATDTTNIQFVFDAVTDVIIANNLRGCGLY", sequence);
        Assert.assertEquals(353, sequence.length());

    }

    @Test
    public void getAllPreviousSequenceReleased_P51875_2() throws Exception {
        UnisaveService service = new UnisaveService();

        DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Map<Integer, String> sequences = service.getAllSequencesBeforeDate("P51875", false, format.parse("2006/09/01"));

        Assert.assertEquals(2, sequences.size());

    }

    @Test
    public void getAvailableSequenceUpdate_P12345() throws Exception {

        // example of a sequence that doesn't have any update through its history

        UnisaveService service = new UnisaveService();
        final List<SequenceVersion> updates = service.getAvailableSequenceUpdate( "P12345", false, "SSWWAHVEMGPPDPILGVTEAYKRDTNSKK" );
        Assert.assertNotNull( updates );
        Assert.assertEquals( 1, updates.size() );
        final SequenceVersion sv = updates.iterator().next();
        Assert.assertNotNull( sv );
        Assert.assertNotNull( sv.getSequence() );
        Assert.assertEquals( "MALLHSARVLSGVASAFHPGLAAAASARASSWWAHVEMGPPDPILGVTEAYKRDTNSKKMNLGVGAYRDDNGKPYVLPSVRKAEAQIAAKGLDKEYLPIGGLAEFCRASAELALGENSEVVKSGRFVTVQTISGTGALRIGASFLQRFFKFSRDVFLPKPSWGNHTPIFRDAGMQLQSYRYYDPKTCGFDFTGALEDISKIPEQSVLLLHACAHNPTGVDPRPEQWKEIATVVKKRNLFAFFDMAYQGFASGDGDKDAWAVRHFIEQGINVCLCQSYAKNMGLYGERVGAFTVICKDADEAKRVESQLKILIRPMYSNPPIHGARIASTILTSPDLRKQWLQEVKGMADRIIGMRTQLVSNLKKEGSTHSWQHITDQIGMFCFTGLKPEQVERLTKEFSIYMTKDGRISVAGVTSGNVGYLAHAIHQVTK", sv.getSequence().getSequence() );
        Assert.assertEquals( "FT   TRANSIT       1     29       Mitochondrion.\n" +
                "FT   CHAIN        30    430       Aspartate aminotransferase,\n" +
                "FT                                mitochondrial.\n" +
                "FT                                /FTId=PRO_0000123886.\n" +
                "FT   BINDING      65     65       Substrate; via amide nitrogen (By\n" +
                "FT                                similarity).\n" +
                "FT   BINDING     162    162       Substrate (By similarity).\n" +
                "FT   BINDING     215    215       Substrate (By similarity).\n" +
                "FT   BINDING     407    407       Substrate (By similarity).\n" +
                "FT   MOD_RES      59     59       N6-acetyllysine (By similarity).\n" +
                "FT   MOD_RES      73     73       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES      73     73       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES      82     82       N6-acetyllysine (By similarity).\n" +
                "FT   MOD_RES      90     90       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES      90     90       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES      96     96       Nitrated tyrosine (By similarity).\n" +
                "FT   MOD_RES     122    122       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     122    122       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     159    159       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     159    159       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     185    185       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     185    185       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     227    227       N6-succinyllysine (By similarity).\n" +
                "FT   MOD_RES     234    234       N6-acetyllysine (By similarity).\n" +
                "FT   MOD_RES     279    279       N6-(pyridoxal phosphate)lysine; alternate\n" +
                "FT                                (By similarity).\n" +
                "FT   MOD_RES     279    279       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     296    296       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     296    296       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     302    302       N6-acetyllysine (By similarity).\n" +
                "FT   MOD_RES     309    309       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     309    309       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     338    338       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     338    338       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     345    345       N6-acetyllysine (By similarity).\n" +
                "FT   MOD_RES     363    363       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     363    363       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     364    364       N6-acetyllysine (By similarity).\n" +
                "FT   MOD_RES     387    387       N6-acetyllysine (By similarity).\n" +
                "FT   MOD_RES     396    396       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     396    396       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     404    404       N6-acetyllysine; alternate (By\n" +
                "FT                                similarity).\n" +
                "FT   MOD_RES     404    404       N6-succinyllysine; alternate (By\n" +
                "FT                                similarity).", sv.getSequence().getHeader() );
        Assert.assertEquals( 2, sv.getVersion() );
    }

    @Test
    public void getAvailableSequenceUpdate_Q98753_version1() throws Exception {

        // example of a sequence that has multiple updates (1 and 2) through its history, we are searching with version 1

        UnisaveService service = new UnisaveService();
        final List<SequenceVersion> updates = service.getAvailableSequenceUpdate( "Q98753", false, "XXX" );
        Assert.assertNotNull( updates );
        Assert.assertEquals( 2, updates.size() );
        SequenceVersion sv;
        final Iterator<SequenceVersion> updateIterator = updates.iterator();

        sv = updateIterator.next();
        Assert.assertNotNull( sv );
        Assert.assertNotNull( sv.getSequence() );
        Assert.assertEquals( "VPFLSKAVRCGPVIPFVIHHFNFRRVTTTKRRRNKYVLVPGYGWVLQDDYLVNSVKMTGENDLPPNQLPHDDDLLFTYAKILLYDYISYFPKFRHNNPDLLDHKTELELFPLKADSAARNKANFYARTLWNDTITDKSAFKPGTYNDTVAGLLLWQQCALMWSLPKSVINRTISGVCDALTNRTSLTLLKRISDWLKQLGLACSPIHRLFIELPTLLGRGAIPGDADKDIKHRLAFDPSITVDVPKEQLHLLIYRLLSRNLNITKVNSFEHHLEERLLWSKSGSHYYPDDKINELLPPQPTRKEFLDVVTTEYIKECKPQVFIRQSRKLEHGKERFIYNCDTVSYVYFDFILKLFETGWQDSEAILSPGDYTSERLHAKISSYKYKAMLDYTDFNSQHTIQSMRLIFETMKELLPPEATFALDWCIASFDNMQTSDGLKWMATLPSGHRATTFINTVLNWCYTQMVGLKFDSFMCAGDDVILMSQQPISLAPILTSHFKFNPSKQSTGTRGEFLRKHYSEAGVFAYPCRAIASLVSGNWLSQSLRENTPILVPIQNGIDRLRSRAGLLGVPWKLGLSELIEREAIPKEVGMALLNSHAAGPGLITRDYSSFTVTPKPPKLSSTLEYTATRYGLQDLSKHVPWKQLTTVESDKLSRQIKKISYRHCSQAKITYNCTYEVFKPRGLPTVLSGSSQPSLSMLWWQAMLKQAIQDDSTKKIDARMFAANACTSSVSGDAFLRANASMAGVLITSLITSSS", sv.getSequence().getSequence() );
        Assert.assertEquals( "FT   NON_TER       1      1", sv.getSequence().getHeader() );
        Assert.assertEquals( 2, sv.getVersion() );

        sv = updateIterator.next();
        Assert.assertNotNull( sv );
        Assert.assertNotNull( sv.getSequence() );
        Assert.assertEquals( "XPFLSKAVRCGPVIPFVIHHFNFRRVTTTKRRRNKYVLVPGYGWVLQDDYLVNSVKMTGENDLPPNQLPHDDDLLFTYAKILLYDYISYFPKFRHNNPDLLDHKTELELFPLKADSAARNKANFYARTLWNDTITDKSAFKPGTYNDTVAGLLLWQQCALMWSLPKSVINRTISGVCDALTNRTSLTLLKRISDWLKQLGLACSPIHRLFIELPTLLGRGAIPGDADKDIKHRLAFDPSITVDVPKEQLHLLIYRLLSRNLNITKVNSFEHHLEERLLWSKSGSHYYPDDKINELLPPQPTRKEFLDVVTTEYIKECKPQVFIRQSRKLEHGKERFIYNCDTVSYVYFDFILKLFETGWQDSEAILSPGDYTSERLHAKISSYKYKAMLDYTDFNSQHTIQSMRLIFETMKELLPPEATFALDWCIASFDNMQTSDGLKWMATLPSGHRATTFINTVLNWCYTQMVGLKFDSFMCAGDDVILMSQQPISLAPILTSHFKFNPSKQSTGTRGEFLRKHYSEAGVFAYPCRAIASLVSGNWLSQSLRENTPILVPIQNGIDRLRSRAGLLGVPWKLGLSELIEREAIPKEVGMALLNSHAAGPGLITRDYSSFTVTPKPPKLSSTLEYTATRYGLQDLSKHVPWKQLTTVESDKLSRQIKKISYRHCSQAKITYNCTYEVFKPRGLPTVLSGSSQPSLSMLWWQAMLKQAIQDDSTKKIDARMFAANACTSSVSGDAFLRANASMAGVLITSLITSSS", sv.getSequence().getSequence() );
        Assert.assertEquals( null, sv.getSequence().getHeader() );
        Assert.assertEquals( 1, sv.getVersion() );

    }

    @Test
    public void getAvailableSequenceUpdate_sequenceMismatch() throws Exception {

        // example of a sequence that doesn't have any update through its history

        UnisaveService service = new UnisaveService();
        final List<SequenceVersion> updates = service.getAvailableSequenceUpdate( "P12345", false, "SSWWAHVEMGPPDPILGVTEAYKRDTNSKK" );
        Assert.assertNotNull( updates );
        Assert.assertEquals( 1, updates.size() );
    }
}
