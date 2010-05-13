package uk.ac.ebi.intact.curationTools.strategies;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.SwissprotRemappingReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.model.BioSource;

/**
 * Unit test for StrategyWithSequence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Apr-2010</pre>
 */

public class StrategyWithSequenceTest {

    private StrategyWithSequence strategy;

    @Before
    public void createStrategy(){
        this.strategy = new StrategyWithSequence();
        this.strategy.enableIsoforms(false);
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

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);

            for (ActionReport r : result.getListOfActions()){
                System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
            }

            Assert.assertNotNull(result.getUniprotId());
            Assert.assertEquals(ac_to_find, result.getUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof PICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isAswissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_Sequence_Swissprot_Remapping_Successful(){
        String sequence = "MAGNFDSEERSSWYWGRLSRQEAVALLQGQRHGVFLVRDSSTSPGDYVLSVSENSRVSHY\n" +
                "IINSSGPRPPVPPSPAQPPPGVSPSRLRIGDQEFDSLPALLEFYKIHYLDTTTLIEPVSR\n" +
                "SRQGSGVILRQEEAEYVRALFDFNGNDEEDLPFKKGDILRIRDKPEEQWWNAEDSEGKRG\n" +
                "MIPVPYVEKYRPASASVSALIGGR";

        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        String ac_to_find = "P46108";

        IdentificationContext context = new IdentificationContext();
        context.setSequence(sequence);
        context.setOrganism(organism);

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);

            for (ActionReport r : result.getListOfActions()){
                System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
            }

            Assert.assertNotNull(result.getUniprotId());
            Assert.assertEquals(3, result.getListOfActions().size());
            Assert.assertEquals(ac_to_find, result.getUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof SwissprotRemappingReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, ((SwissprotRemappingReport) result.getLastAction()).getTremblAccession() != null);
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
            Assert.assertNotNull(result.getUniprotId());
            Assert.assertEquals(ac_to_find, result.getUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof PICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isAswissprotEntry());
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
            Assert.assertNotNull(result.getUniprotId());
            Assert.assertEquals(ac_to_find, result.getUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof PICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isAswissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
