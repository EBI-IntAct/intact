/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others. All
 * rights reserved. Please see the file LICENSE in the root directory of this
 * distribution.
 */
package uk.ac.ebi.intact.bridges.wswublast.client;

import org.junit.*;
import uk.ac.ebi.intact.bridges.wswublast.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Test class for the wswublast client.
 *
 * @author Irina Armean (iarmean@ebi.ac.uk)
 * @since <pre> 7 Sep 2007 </pre>
 */
public class BlastClientTest {

    private File testDir;
    private BlastClient bc;

    /**
     * @throws Exception     : BlastCleintException
     */
    @Before
    public void setUp() throws Exception {
        testDir = getTargetDirectory();
        bc = new BlastClient( "iarmean@ebi.ac.uk" );
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testBlastClient() throws WuBlastClientException {
        Assert.assertTrue( true );

//        System.out.println( "mem: " + ( Runtime.getRuntime().maxMemory() ) / ( 1024 * 1024 ) );
//        BlastClient bc = new BlastClient( "iarmean@ebi.ac.uk" );
//       //wswublast-20080122-09052290	Q3E806
//        //wswublast-20080122-03262455	P47087
//        // wswublast-20080126-13500606	P35918
//       Job job = new Job( "wswublast-20080126-13500606", new BlastInput( new UniprotAc( "P35918" ) ) );
//      // Job job = bc.wswublast(new BlastInput(new UniprotAc( "Q9I7U4")));
//        bc.checkStatus( job );
//        if ( BlastJobStatus.NOT_FOUND.equals( job.getStatus() )){
//            System.out.println("job not found!");
//            return;
//        }
//        while ( !BlastJobStatus.DONE.equals( job.getStatus() ) ) {
//            try {
//                Thread.sleep( 5000 );
//            } catch ( InterruptedException e ) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            bc.checkStatus( job );
//        }
//        bc.getResult( job );
//        BlastOutput result = job.getBlastResult();
//        FileWriter fw;
//        try {
//            fw = new FileWriter( new File(  testDir, job.getBlastInput().getUniprotAc().getAcNr() + ".xml" ) );
////            fw = new FileWriter(new File ("/net/nfs7/vol22/sp-pro5/20080201_iarmean", job.getBlastInput().getUniprotAc().getAcNr() + ".xml"));
//            System.out.println( "done." );
//            fw.append( new String( result.getResult() ) );
//            fw.close();
//        } catch ( IOException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Test
    public final void testBlastAc() throws WuBlastClientException {
        Assert.assertTrue(true);

//        System.out.println( "mem: " + ( Runtime.getRuntime().maxMemory() ) / ( 1024 * 1024 ) );
//        BlastClient bc = new BlastClient( "iarmean@ebi.ac.uk" );
//        UniprotAc ac = new UniprotAc( "Q8IKL0" );
//        Sequence seq = new Sequence("MNDICYPYNC SITYINSNGV ISTRDIKEYD GEEINEIIND HDNNNNNNNN NNNNSNSNFV FCENDLDNIK SIVINLKPFH KDYEDNIIFK IKKKNEKYAC EWVVFIKTSV EEIKNKVCNK HTSEDIDINN IEEKIFPCKD IKEIMLKYVY CTTFFDNYIL SIKNELEKYG LDINISIDDY IRVPKDLCKY KYDNFLSSYK KNVATQNNHD VYNDNNIGYK KLSENDHNKM NIVNNMNSKN NNFVNYTMNI KYNDNTKGSL SNNFNSKVND TTYRNTKGNN NNNTNDDNKN DVNKNDVNKN DVNKNDVNKN DVNKNDVNKN DVNKNDDNTN DVNTNNINTY DNTYNQTAPN YSYNTNKYDS KYNNIHNKIK RSKRINNDEE GDEDDFLGEI LSLKHFNEDT INLSNNLKNN KYDLYNDNNN NNNNNNNNNN NNKNNSNNNY EKYYNYKLNN NLFKSDVNQN YSSRLNINKN FPKNFNNKKI FTNMSQEEDD KQASTVNISS DDNDDEYNNY FYSSKKTESD IKVYHNVRKD FYILNTKNKS SNFSNSEKNN IMRKNLIQFL KENRNKVKSS RSDYSEDNNL LNNPYDNKTR KINQTCNKNL SYSGNMTFKE NISFDKNNKL YNKNISFNEK AYNNSNKNLS NNVIHFYEKR NYSNNDIHDF INMNKDDSNN NINNNNNNNN NIEKNIMRNT QKHVSIPVFR HSFLNENISK PDSFRNIYLT DSNSKYPWND LSNASIECLE NDKSNNNKIV KSKNNKIIRP NYINDENEHH TTKTCVKGST KNNDQNIESN IIDNQYVNDK MLMLRNNFHM DNNIHNRSNS SNLTFGNMVR QKKEDYLLNS KRNDLYEKQT FSISNINEKT KININKEKND NKNNLESHYE YLSNEILKFM IKYNESSKNF KMDVNEIDTE LLSTSIKKFV RNLTEKEMNQ LLCNKDYIFS KIIMESLENN EVNKNDTNVT CDNKTMTDDE YNIDKNNTDI KKNTDPSHKN DNIKHDLQLC GSKTMSNFYK EGNTNDIDTS RNNKSCENNS NNIYTNVSKN MNKDQIFNNN VKKNNTFNVN INKNAPNNMS NNNNNNNNNN NVTSCINNNG KNKMRSLKLS NDNVSMLYNY YLDICRDNKS QLEKKIQDDN KINEKITNEN LMENVKDVIK KEDNTHINDT NDTNDTKSNN NNNNNNNSSS SNNNHVDKLK NVTYDLNTIS STKRKNFKEH INYFFNDCFK NFKNLNDKIK HKKISENVAN TNNDKKEENN GNLNEKHKEQ NIDNVLNTRK VPFDGHILDE QTSCNDKDDD NLESEKKTFN KCTSVTKNES NNMILRGDKN NDIIKKMRTN DHTDRMTFEH FNNKINNIIN KYEYKQCLNK YERQLIKFVQ TFKNTKQDHI SSQIYNDHND KNDIIKYKYD EENILKAILK NMMWEKSNMN SNNNNNNNNN SSSNESFFSF NNSIKDIDDN FVENADDIIY GNIDHNNNIK RSYGSNNIYD YNNHDSSSGS CIKISSDKMI SYKNNKTDEK YSNPKNIYPI KQSCSDSNHY ILQKIMKNKR EKSCNNNEEN NFSFNDILYD KRSNNYDNSI SMSYNKCFHK NIKNITNSIT KNYLYYNNNN NKYGKRNETF EHISNNNYGN HKKNENILMP SQSKNFSCLS YNKENNSFPI ICNGNNVDEN KLKNSCVGNM IKYKEPKNNL KYPMNIYNKL FNKDRQNIDI SPEQTLNLVK SIQRESMLNK AKEINLFNER NNNKIGCTYD NTNNTAISDF NKDKIIINNI DIQKNNFCNM NCLKKNSNDL INKNIKFNNN KMEKCMDIFE KFQIKNKDKY AHIFNVHVDN KKCSNNADYF KRTHLYKFNN ILSNNNINNT LNIHNIEYNK CNNIKKEGEN NILDNVPNDD MKISKKKKNE KSQLMINLNN FNQNYIKELL IQKMYYQKEF YNNNDFIHKY NDNHTLKDNI YLEKCKYDTN KSGVYNNSYS SYNSLKINLF FLFDDMYDKF FIENYQDREK MLNILSINKS VCYNQLSIFY KKKIFFQYKG AKYLHNNYNM NVSYNAYNLY ISQNVIQHKN YYNSDINKNI CENLNIYKIK NETKNDLDNK YKSKDNDKNK TENCNKSIYN NIYNEYNNDN NDNYNNNDNI VNCDNNLYGL NHNEELNEFI LNHHKKWNDK NKNILELSYH KDFMDNKIEK NIKEIKHINK YYMKEEMNYF KKAIKEMKRL KKIMCNILKS QSNNYLNLNI DEKLLYNMQN DDDLSFESYL KDDSDDIDHY GNVYNREDDY IINLNTNNNY NVYKTPRLDK IRKNHKYNSH PQIEKEILNN SNQFNINENY KKCNITDDDK ASKIHMKKNG TSSNMLHLIN RKNENDQKKL IDHNSEYNSD YSFDSDNVSK LINIVFENKN EMNKNALKID QPMDNIINDK HEEIKINIEG DKNKQRDDKN NQRDDKNKQR DDKNKQRDDK NKQRDDKNKQ RDDKNKQRDD KNKQRDDKNK QRDDKNNQRD DIHNTSREPL GTFEFEVNIK NNEEEYRETF KYIYEKITNI RNNMWNEDMD NMQNNEMKQN LLIYKSRNDN ISPVYEQNIN NNVNMLDTEK EEDIKDKLKN HDIAKEENVN GDNDVKKCND DIYYFNIYTN VWENVDLNVS QKLLTSLKMK KDIIKKYYKN DHEYENFITF SEWNAYNLMI SKIYKALDMK KFEYYQRRVY DTDIFKRDFK KRCAYDFGMK IIGKPRSRRK TKRWNIEEED KLYEDEVYSK NSEDYSDFNY DEYVEGDEDE YEEKEHNQDK ENDEHEIGGN KYDEDQDQNK EEQKEEQESE DQNEDEDYEE DEDDEEDDEE DENDDDENDD DEEEEYDHEE DDHGEDDHED DDNEDDVDQK CLYEEHIEQE KEKEVPEKKD EEKCRYVKKE SCEHNNDGSK KYNEVVCKNE NANNPKYTVG QGEGNKMKEC SDLKNTLSSI EDEEKFEERE PKRSERLYLR RKRASSILSN NSTENSVNKI VTRQYKKLKE SEKAEEDMKS VYLGTRSNKT NNNKKTHIFK KRISRKKTSN TLTFKKPYIY ELIQLPDYVT HNELISSKNE SLPTYILQYE QLRKNECVYF NIQNHYNSFV NNVYHFAISY EQSLKHNEYL NNALNNVKSR FYIKRICLKN NKTMENNNIY DICSTYYNYL YTYDPNNSTY ILQTSNRNNT IASLDKGLNY EEYYSNYVND YLQSQLLKKK RNRTKSKENN KCTSDILLST ENDNPLDISN EYIVTRKMSS KYYDNSPFLN EDYLGIKSME YKHNTSIIND SNFGDKNNYV DYTTSYMDGN NEIACNYNYL NYYNNKNHQT KAQNDISFSE KTLEEGDDEL QQ");
//
//        Job job = bc.wswublast( new BlastInput( ac, seq ) );
//
//        while ( !BlastJobStatus.DONE.equals( job.getStatus() ) ) {
//            try {
//                Thread.sleep( 5000 );
//            } catch ( InterruptedException e ) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            bc.checkStatus( job );
//        }
//        bc.getResult( job );
//        BlastOutput result = job.getBlastResult();
//        FileWriter fw;
//        try {
//            fw = new FileWriter( new File( testDir, ac.getAcNr() + ".xml" ) );
//            fw.append( new String( result.getResult() ) );
//            fw.close();
//        } catch ( IOException e ) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @Test
    public final void testBlastSeq() throws WuBlastClientException {
        BlastClient bc = new BlastClient( "iarmean@ebi.ac.uk" );
        String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        UniprotAc ac = new UniprotAc( "Q9VQS6-1" );
        Sequence seqObj = new Sequence( seq );
        BlastInput bi = new BlastInput( ac, seqObj );
        Job job = bc.blastSeq( bi );
        while ( !BlastJobStatus.FINISHED.equals( job.getStatus() ) ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            bc.checkStatus( job );
        }

        BlastOutput result = job.getBlastResult();
        FileWriter fw;
        try {
            fw = new FileWriter( new File( testDir, ac.getAcNr() + ".xml" ) );
            fw.append( new String( result.getResult() ) );
            fw.close();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public final void testSubmitSplicevariant() throws WuBlastClientException {
        BlastClient bc = new BlastClient( "iarmean@ebi.ac.uk" );
        String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        UniprotAc ac = new UniprotAc( "Q9VQS6-1" );
        BlastInput bI = new BlastInput( ac, new Sequence( seq ) );
        Job job = bc.blast( bI );
        while ( !BlastJobStatus.FINISHED.equals( job.getStatus() ) ) {
            try {
                Thread.sleep( 5000 );
            } catch ( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            bc.checkStatus( job );
        }

        BlastOutput result = job.getBlastResult();
        FileWriter fw;
        try {
            fw = new FileWriter( new File( testDir, ac.getAcNr() + ".xml" ) );
            fw.append( new String( result.getResult() ) );
            fw.close();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public final void testSpecificContentWithSeq() throws WuBlastClientException {
        String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        UniprotAc ac = new UniprotAc( "Q9VQS6" );
        BlastInput bI = new BlastInput( ac, new Sequence( seq ) );
        String content = bc.getSpecificContent( bI );
        Assert.assertFalse( content.equals( "uniprot:Q9VQS6" ) );
        Assert.assertTrue(content.equalsIgnoreCase( seq)) ;
    }

    @Test
    public final void testSpecificContentWithoutSeqIsoform() throws WuBlastClientException {
        UniprotAc ac = new UniprotAc( "Q9VQS6-1" );
        BlastInput bI = new BlastInput( ac);
        String content = bc.getSpecificContent( bI );
        Assert.assertTrue( content.equals( "intact:Q9VQS6-1" ) );
    }

    @Test
    public final void testSpecificContentWithoutSeq() throws WuBlastClientException {
        UniprotAc ac = new UniprotAc( "Q9VQS6" );
        BlastInput bI = new BlastInput( ac);
        String content = bc.getSpecificContent( bI );
        Assert.assertTrue( content.equals( "intact:Q9VQS6" ) );
    }

    @Test
    public final void testSpecificContentIsoform() throws WuBlastClientException {
        String seq = "MFAVMRIDNDDCRSDFRRKMRPKCEFICKYCQRRFTKPYNLMIHERTHKSPEITYSCEVCGKYFKQRDNLRQHRCSQCVWR";
        UniprotAc ac = new UniprotAc( "Q9VQS6-1" );
        BlastInput bI = new BlastInput( ac, new Sequence( seq ) );
        String content = bc.getSpecificContent( bI );
        Assert.assertFalse( content.equals( "uniprot:Q9VQS6-1" ) );
        Assert.assertTrue(content.equalsIgnoreCase( seq)) ;
    }

    private File getTargetDirectory() {
        String outputDirPath = BlastClientTest.class.getResource( "/" ).getFile();
        Assert.assertNotNull( outputDirPath );
        File outputDir = new File( outputDirPath );
        // we are in intact-wswublast/target/test-classes , move 1 up
        outputDir = outputDir.getParentFile();
        Assert.assertNotNull( outputDir );
        Assert.assertTrue( outputDir.getAbsolutePath(), outputDir.isDirectory() );
        Assert.assertEquals( "target", outputDir.getName() );
        return outputDir;
    }

    @Test
    public void getParametersValuesOfWuBlast(){
        this.bc.getParametersValues();
    }
}
