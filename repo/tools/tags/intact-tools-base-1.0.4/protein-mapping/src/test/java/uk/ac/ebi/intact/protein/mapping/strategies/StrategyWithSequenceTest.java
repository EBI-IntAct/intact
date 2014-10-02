package uk.ac.ebi.intact.protein.mapping.strategies;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.intact.commons.util.Crc64;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultIntactCrc64Report;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultPICRReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

/**
 * Unit test for StrategyWithSequence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Apr-2010</pre>
 */
@ContextConfiguration(locations = {"classpath*:/META-INF/jpa.test.spring.xml"})
public class StrategyWithSequenceTest  extends IntactBasicTestCase {

    private StrategyWithSequence strategy;
    private IntactContext intactContext;
    private String acToFind;

    @Before
    public void createProcess(){
        this.strategy = new StrategyWithSequence();
        this.strategy.enableIsoforms(false);
        this.intactContext = IntactContext.getCurrentInstance();

        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";

        Protein prot = getMockBuilder().createProtein("P12345", "test-protein");
        prot.setBioSource( createBiosource("xenla", "Xenopus laevis", "8355") );
        prot.setSequence(sequence);
        prot.setCrc64(Crc64.getCrc64(sequence));

        this.intactContext.getCorePersister().saveOrUpdate(prot);
        acToFind = prot.getAc();
    }

    private BioSource createBiosource(String shortLabel, String fullName, String taxId){
        BioSource bioSource = new BioSource();
        bioSource.setFullName(fullName);
        bioSource.setShortLabel(shortLabel);
        bioSource.setTaxId(taxId);

        return bioSource;
    }

    @Test
    public void test_Sequence_Swissprot_Successful(){
        String sequence = "MTTTVATDYDNIEIQQQYSDVNNRWDVDDWDNENSSARLFERSRIKALADEREAVQKKTF\n" +
                "TKWVNSHLARVSCRITDLYTDLRDGRMLIKLLEVLSGERLPKPTKGRMRIHCLENVDKAL\n" +
                "QFLKEQRVHLENMGSHDIVDGNHRLTLGLIWTIILRFQIQDISVETEDNKEKKSAKDALL\n" +
                "LWCQMKTAGYPNVNIHNFTTSWRDGMAFNALIHKHRPDLIDFDKLKKSNAHYNLQNAFNL\n" +
                "AEQHLGLTKLLDPEDISVDHPDEKSIITYVVTYYHYFSKMKALAVEGKRIGKVLDNAIET\n" +
                "EKMIEKYESLASDLLEWIEQTIIILNNRKFANSLVGVQQQLQAFNTYRTVEKPPKFTEKG\n" +
                "NLEVLLFTIQSKMRANNQKVYMPREGKLISDINKAWERLEKAEHERELALRNELIRQEKL\n" +
                "EQLARRFDRKAAMRETWLSENQRLVSQDNFGFDLPAVEAATKKHEAIETDIAAYEERVQA\n" +
                "VVAVARELEAENYHDIKRITARKDNVIRLWEYLLELLRARRQRLEMNLGLQKIFQEMLYI\n" +
                "MDWMDEMKVLVLSQDYGKHLLGVEDLLQKHTLVEADIGIQAERVRGVNASAQKFATDGEG\n" +
                "YKPCDPQVIRDRVAHMEFCYQELCQLAAERRARLEESRRLWKFFWEMAEEEGWIREKEKI\n" +
                "LSSDDYGKDLTSVMRLLSKHRAFEDEMSGRSGHFEQAIKEGEDMIAEEHFGSEKIRERII\n" +
                "YIREQWANLEQLSAIRKKRLEEASLLHQFQADADDIDAWMLDILKIVSSSDVGHDEYSTQ\n" +
                "SLVKKHKDVAEEIANYRPTLDTLHEQASALPQEHAESPDVRGRLSGIEERYKEVAELTRL\n" +
                "RKQALQDTLALYKMFSEADACELWIDEKEQWLNNMQIPEKLEDLEVIQHRFESLEPEMNN\n" +
                "QASRVAVVNQIARQLMHSGHPSEKEIKAQQDKLNTRWSQFRELVDRKKDALLSALSIQNY\n" +
                "HLECNETKSWIREKTKVIESTQDLGNDLAGVMALQRKLTGMERDLVAIEAKLSDLQKEAE\n" +
                "KLESEHPDQAQAILSRLAEISDVWEEMKTTLKNREASLGEASKLQQFLRDLDDFQSWLSR\n" +
                "TQTAIASEDMPNTLTEAEKLLTQHENIKNEIDNYEEDYQKMRDMGEMVTQGQTDAQYMFL\n" +
                "RQRLQALDTGWNELHKMWENRQNLLSQSHAYQQFLRDTKQAEAFLNNQEYVLAHTEMPTT\n" +
                "LEGAEAAIKKQEDFMTTMDANEEKINAVVETGRRLVSDGNINSDRIQEKVDSIDDRHRKN\n" +
                "RETASELLMRLKDNRDLQKFLQDCQELSLWINEKMLTAQDMSYDEARNLHSKWLKHQAFM\n" +
                "AELASNKEWLDKIEKEGMQLISEKPETEAVVKEKLTGLHKMWEVLESTTQTKAQRLFDAN\n" +
                "KAELFTQSCADLDKWLHGLESQIQSDDYGKDLTSVNILLKKQQMLENQMEVRKKEIEELQ\n" +
                "SQAQALSQEGKSTDEVDSKRLTVQTKFMELLEPLNERKHNLLASKEIHQFNRDVEDEILW\n" +
                "VGERMPLATSTDHGHNLQTVQLLIKKNQTLQKEIQGHQPRIDDIFERSQNIVTDSSSLSA\n" +
                "EAIRQRLADLKQLWGLLIEETEKRHRRLEEAHRAQQYYFDAAEAEAWMSEQELYMMSEEK\n" +
                "AKDEQSAVSMLKKHQILEQAVEDYAETVHQLSKTSRALVADSHPESERISMRQSKVDKLY\n" +
                "AGLKDLAEERRGKLDERHRLFQLNREVDDLEQWIAEREVVAGSHELGQDYEHVTMLQERF\n" +
                "REFARDTGNIGQERVDTVNHLADELINSGHSDAATIAEWKDGLNEAWADLLELIDTRTQI\n" +
                "LAASYELHKFYHDAKEIFGRIQDKHKKLPEELGRDQNTVETLQRMHTTFEHDIQALGTQV\n" +
                "RQLQEDAARLQAAYAGDKADDIQKRENEVLEAWKSLLDACESRRVRLVDTGDKFRFFSMV\n" +
                "RDLMLWMEDVIRQIEAQEKPRDVSSVELLMNNHQGIKAEIDARNDSFTTCIELGKSLLAR\n" +
                "KHYASEEIKEKLLQLTEKRKEMIDKWEDRWEWLRLILEVHQFSRDASVAEAWLLGQEPYL\n" +
                "SSREIGQSVDEVEKLIKRHEAFEKSAATWDERFSALERLTTLELLEVRRQQEEEERKRRP\n" +
                "PSPEPSTKVSEEAESQQQWDTSKGEQVSQNGLPAEQGSPRMAETVDTSEMVNGATEQRTS\n" +
                "SKESSPIPSPTSDRKAKTALPAQSAATLPARTQETPSAQMEGFLNRKHEWEAHNKKASSR\n" +
                "SWHNVYCVINNQEMGFYKDAKTAASGIPYHSEVPVSLKEAVCEVALDYKKKKHVFKLRLN\n" +
                "DGNEYLFQAKDDEEMNTWIQAISSAISSDKHEVSASTQSTPASSRAQTLPTSVVTITSES\n" +
                "SPGKREKDKEKDKEKRFSLFGKKK";

        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        String ac_to_find = "Q01082";

        IdentificationContext context = new IdentificationContext();
        context.setSequence(sequence);
        context.setOrganism(organism);

        IdentificationResults<MappingReport> result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);

            for (MappingReport r : result.getListOfActions()){
                System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
            }

            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals(ac_to_find, result.getFinalUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof DefaultPICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isASwissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_Sequence_PICR_Swissprot_Remapping_Successful(){
        String sequence = "MAGNFDSEERSSWYWGRLSRQEAVALLQGQRHGVFLVRDSSTSPGDYVLSVSENSRVSHY\n" +
                "IINSSGPRPPVPPSPAQPPPGVSPSRLRIGDQEFDSLPALLEFYKIHYLDTTTLIEPVSR\n" +
                "SRQGSGVILRQEEAEYVRALFDFNGNDEEDLPFKKGDILRIRDKPEEQWWNAEDSEGKRG\n" +
                "MIPVPYVEKYRPASASVSALIGGR";

        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        String ac_to_find = "P46108";

        IdentificationContext context = new IdentificationContext();
        context.setSequence(sequence);
        context.setOrganism(organism);

        IdentificationResults<MappingReport> result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);

            for (MappingReport r : result.getListOfActions()){
                System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
            }

            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals(1, result.getListOfActions().size());
            Assert.assertEquals(ac_to_find, result.getFinalUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof DefaultPICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            //Assert.assertEquals(true, ((DefaultBlastReport) result.getLastAction()).getBlastMatchingProteins().size() > 0);
            //Assert.assertEquals(true, ((DefaultBlastReport) result.getLastAction()).getBlastMatchingProteins().iterator().next().getTremblAccession() != null);
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_SwissprotIdentifier_IsoformExcluded(){
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        String sequence = "MTDSKYFTTNKKGEIFELKAELNNEKKEKRKEAVKKVIAAMTVGKDVSSLFPDVVNCMQT\n" +
                "DNLELKKLVYLYLMNYAKSQPDMAIMAVNSFVKDCEDPNPLIRALAVRTMGCIRVDKITE\n" +
                "YLCEPLRKCLKDEDPYVRKTAAVCVAKLHDINAQMVEDQGFLDSLRDLIADSNPMVVANA\n" +
                "VAALSEISESHPNSNLLDLNPQNINKLLTALNECTEWGQIFILDCLSNYNPKDDREAQSI\n" +
                "CERVTPRLSHANSAVVLSAVKVLMKFLELLPKDSDYYNMLLKKLAPPLVTLLSGEPEVQY\n" +
                "VALRNINLIVQKRPEILKQEIKVFFVKYNDPIYVKLEKLDIMIRLASQANIAQVLAELKE\n" +
                "YATEVDVDFVRKAVRAIGRCAIKVEQSAERCVSTLLDLIQTKVNYVVQEAIVVIRDIFRK\n" +
                "YPNKYESIIATLCENLDSLDEPDARAAMIWIVGEYAERIDNADELLESFLEGFHDESTQV\n" +
                "QLTLLTAIVKLFLKKPSETQELVQQVLSLATQDSDNPDLRDRGYIYWRLLSTDPVTAKEV\n" +
                "VLSEKPLISEETDLIEPTLLDELICHIGSLASVYHKPPNAFVEGSHGIHRKHLPIHHGST\n" +
                "DAGDSPVGTTTATNLEQPQVIPSQGDLLGDLLNLDLGPPVNVPQVSSMQMGAVDLLGGGL\n" +
                "DSLLGSDLGGGIGGSPAVGQSFIPSSVPATFAPSPTPAVVSSGLNDLFELSTGIGMAPGG\n" +
                "YVAPKAVWLPAVKAKGLEISGTFTHRQGHIYMEMNFTNKALQHMTDFAIQFNKNSFGVIP\n" +
                "STPLAIHTPLMPNQSIDVSLPLNTLGPVMKMEPLNNLQVAVKNNIDVFYFSCLIPLNVLF\n" +
                "VEDGKMERQVFLATWKDIPNENELQFQIKECHLNADTVSSKLQNNNVYTIAKRNVEGQDM\n" +
                "LYQSLKLTNGIWILAELRIQPGNPNYTLSLKCRAPEVSQYIYQVYDSILKN";

        String ac_to_find = "P63010";

        IdentificationContext context = new IdentificationContext();
        context.setSequence(sequence);
        context.setOrganism(organism);

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals(ac_to_find, result.getFinalUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof DefaultPICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isASwissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void test_SwissprotIdentifier_Isoform_NotExcluded(){
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        String sequence = "MTDSKYFTTNKKGEIFELKAELNNEKKEKRKEAVKKVIAAMTVGKDVSSLFPDVVNCMQT\n" +
                "DNLELKKLVYLYLMNYAKSQPDMAIMAVNSFVKDCEDPNPLIRALAVRTMGCIRVDKITE\n" +
                "YLCEPLRKCLKDEDPYVRKTAAVCVAKLHDINAQMVEDQGFLDSLRDLIADSNPMVVANA\n" +
                "VAALSEISESHPNSNLLDLNPQNINKLLTALNECTEWGQIFILDCLSNYNPKDDREAQSI\n" +
                "CERVTPRLSHANSAVVLSAVKVLMKFLELLPKDSDYYNMLLKKLAPPLVTLLSGEPEVQY\n" +
                "VALRNINLIVQKRPEILKQEIKVFFVKYNDPIYVKLEKLDIMIRLASQANIAQVLAELKE\n" +
                "YATEVDVDFVRKAVRAIGRCAIKVEQSAERCVSTLLDLIQTKVNYVVQEAIVVIRDIFRK\n" +
                "YPNKYESIIATLCENLDSLDEPDARAAMIWIVGEYAERIDNADELLESFLEGFHDESTQV\n" +
                "QLTLLTAIVKLFLKKPSETQELVQQVLSLATQDSDNPDLRDRGYIYWRLLSTDPVTAKEV\n" +
                "VLSEKPLISEETDLIEPTLLDELICHIGSLASVYHKPPNAFVEGSHGIHRKHLPIHHGST\n" +
                "DAGDSPVGTTTATNLEQPQVIPSQGDLLGDLLNLDLGPPVNVPQVSSMQMGAVDLLGGGL\n" +
                "DSLLGSDLGGGIGGSPAVGQSFIPSSVPATFAPSPTPAVVSSGLNDLFELSTGIGMAPGG\n" +
                "YVAPKAVWLPAVKAKGLEISGTFTHRQGHIYMEMNFTNKALQHMTDFAIQFNKNSFGVIP\n" +
                "STPLAIHTPLMPNQSIDVSLPLNTLGPVMKMEPLNNLQVAVKNNIDVFYFSCLIPLNVLF\n" +
                "VEDGKMERQVFLATWKDIPNENELQFQIKECHLNADTVSSKLQNNNVYTIAKRNVEGQDM\n" +
                "LYQSLKLTNGIWILAELRIQPGNPNYTLSLKCRAPEVSQYIYQVYDSILKN";
        String ac_to_find = "P63010-2";

        this.strategy.enableIsoforms(true);

        IdentificationContext context = new IdentificationContext();
        context.setSequence(sequence);
        context.setOrganism(organism);

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals(ac_to_find, result.getFinalUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof DefaultPICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isASwissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void test_PICR_Unsuccessful_Intact_Successful(){

        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";
        BioSource bioSource = createBiosource("xenla", "Xenopus laevis", "8355");

        IdentificationContext context = new IdentificationContext();
        context.setSequence(sequence);
        context.setOrganism(bioSource);

        this.strategy.setEnableIntactSearch(true);

        try {
            IdentificationResults<MappingReport> result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);

            for (MappingReport r : result.getListOfActions()){
                System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
            }

            Assert.assertNull(result.getFinalUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof DefaultIntactCrc64Report);
            Assert.assertEquals(acToFind, ((DefaultIntactCrc64Report) result.getLastAction()).getIntactAc());
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());

        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_PICR_Unsuccessful_Intact_Successful_No_Organism(){

        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";

        IdentificationContext context = new IdentificationContext();
        context.setSequence(sequence);
        context.setOrganism(null);

        this.strategy.setEnableIntactSearch(true);

        try {
            IdentificationResults<MappingReport> result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);

            for (MappingReport r : result.getListOfActions()){
                System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());

                for (String warn : r.getWarnings()){
                    System.out.println(warn);
                }
            }

            Assert.assertNull(result.getFinalUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof DefaultIntactCrc64Report);
            Assert.assertEquals(acToFind, ((DefaultIntactCrc64Report) result.getLastAction()).getIntactAc());
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());

        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
