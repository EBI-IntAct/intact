package uk.ac.ebi.intact.curationTools.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.model.BioSource;

import java.util.List;

/**
 * Unit test for UniprotIdentityBlastProcess
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Apr-2010</pre>
 */

public class UniprotIdentityBlastProcessTest {
    private UniprotIdentityBlastProcess process;
    private BlastContext context;

    @Before
    public void createBlastProcess(){
        this.process = new UniprotIdentityBlastProcess();
        this.context = new BlastContext();
    }

    private BioSource createBiosource(String shortLabel, String fullName, String taxId){
        BioSource bioSource = new BioSource();
        bioSource.setFullName(fullName);
        bioSource.setShortLabel(shortLabel);
        bioSource.setTaxId(taxId);

        return bioSource;
    }

    @Test
    public void test_Swissprot_Successful(){
        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGII" +
                "PANYVQKREGVKAGTKLSLMPWFHGKITREQAERLLYPPETGLFLVRESTNYPGDYTLCV" +
                "SCDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQ" +
                "DEFYRSGWALNMKELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVM" +
                "TQLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFSLDVCE" +
                "AMEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASSTQDTGKLPVKWTAPEALRE" +
                "KKFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPRVEKGYKMDAPDGCPPAVYEVMK" +
                "NCWHLDAAMRPSFLQLREQLEHIKTHELHL";

        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setSequence(sequence);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<ActionReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(2, reports.size());
            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals(true, reports.get(1) instanceof BlastReport);
            Assert.assertEquals("P41240", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_Swissprot_ToBeReviewed_WithBadOrganism(){
        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGII" +
                "PANYVQKREGVKAGTKLSLMPWFHGKITREQAERLLYPPETGLFLVRESTNYPGDYTLCV" +
                "SCDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQ" +
                "DEFYRSGWALNMKELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVM" +
                "TQLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFSLDVCE" +
                "AMEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASSTQDTGKLPVKWTAPEALRE" +
                "KKFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPRVEKGYKMDAPDGCPPAVYEVMK" +
                "NCWHLDAAMRPSFLQLREQLEHIKTHELHL";

        BioSource organism = createBiosource("rat", "Rattus norvegicus", "10116");

        this.context.setSequence(sequence);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<ActionReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(2, reports.size());
            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(true, reports.get(1) instanceof BlastReport);
            Assert.assertEquals(false, ((BlastReport)reports.get(1)).getBlastMatchingProteins().isEmpty());            

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_Swissprot_Unsuccessful(){
        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKI\n" +
                "AQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLIT\n" +
                "HKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSK\n" +
                "ICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSA\n" +
                "TS";

        BioSource organism = createBiosource("rat", "Rattus norvegicus", "10116");

        this.context.setSequence(sequence);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<ActionReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(2, reports.size());
            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(true, reports.get(1) instanceof BlastReport);
            Assert.assertEquals(true, ((BlastReport)reports.get(1)).getBlastMatchingProteins().isEmpty());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
