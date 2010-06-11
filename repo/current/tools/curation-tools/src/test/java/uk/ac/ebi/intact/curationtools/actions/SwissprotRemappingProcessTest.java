package uk.ac.ebi.intact.curationtools.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.curationtools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationtools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationtools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationtools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationtools.model.contexts.BlastContext;
import uk.ac.ebi.intact.model.BioSource;

import java.util.List;

/**
 * Unit test for SwissprotRemappingProcess
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Apr-2010</pre>
 */

public class SwissprotRemappingProcessTest {

    private SwissprotRemappingProcess process;
    private BlastContext context;

    @Before
    public void createBlastProcess(){
        this.process = new SwissprotRemappingProcess();
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
    public void test_SwissprotRemapping_Successful(){
        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGII\n" +
                "PANYVQKREGVKAGTKLSLMPWFHGKITREQAERLLYPPETGLFLVRESTNYPGDYTLCV\n" +
                "SCDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQ\n" +
                "DEFYRSGWALNMKELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVM\n" +
                "TQLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFSLDVCE\n" +
                "AMEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASTQDTGKLPVKWTAPEALREK\n" +
                "KFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPRVEKGYKMDAPDGCPPAVYEVMKN\n" +
                "CWHLDAAMRPSFLQLREQLEHIKTHELHL";

        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setSequence(sequence);
        this.context.setOrganism(organism);
        this.context.setEnsemblGene("ENSG00000103653");

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
            Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, reports.get(1).getStatusLabel());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_SwissprotRemapping_Successful_WithoutOrganism(){
        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGII\n" +
                "PANYVQKREGVKAGTKLSLMPWFHGKITREQAERLLYPPETGLFLVRESTNYPGDYTLCV\n" +
                "SCDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQ\n" +
                "DEFYRSGWALNMKELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVM\n" +
                "TQLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFSLDVCE\n" +
                "AMEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASTQDTGKLPVKWTAPEALREK\n" +
                "KFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPRVEKGYKMDAPDGCPPAVYEVMKN\n" +
                "CWHLDAAMRPSFLQLREQLEHIKTHELHL";

        //BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setSequence(sequence);
        this.context.setOrganism(null);
        this.context.setEnsemblGene("ENSG00000103653");

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
            Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, reports.get(1).getStatusLabel());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_SwissprotRemapping_Unsuccessful_WithBadEnsemblGeneAndOrganism(){
        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGII\n" +
                "PANYVQKREGVKAGTKLSLMPWFHGKITREQAERLLYPPETGLFLVRESTNYPGDYTLCV\n" +
                "SCDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQ\n" +
                "DEFYRSGWALNMKELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVM\n" +
                "TQLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFSLDVCE\n" +
                "AMEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASTQDTGKLPVKWTAPEALREK\n" +
                "KFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPRVEKGYKMDAPDGCPPAVYEVMKN\n" +
                "CWHLDAAMRPSFLQLREQLEHIKTHELHL";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setSequence(sequence);
        this.context.setOrganism(organism);
        this.context.setEnsemblGene("ENSRNOG00000019374");

        try {
            String ac = this.process.runAction(context);
            List<ActionReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(3, reports.size());
            for (String warn : reports.get(2).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(2).getStatus().getLabel() + " " + reports.get(2).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(true, reports.get(2) instanceof BlastReport);
            Assert.assertEquals(true, ((BlastReport) reports.get(2)).getBlastMatchingProteins().size() == 0);
            Assert.assertEquals(StatusLabel.FAILED, reports.get(1).getStatus().getLabel());
            Assert.assertEquals(StatusLabel.FAILED, reports.get(2).getStatus().getLabel());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_SwissprotRemapping_to_be_reviewed_WithEnsemblGeneAndOrganism_LowIdentity(){
        String sequence = "MSAIQAAWPSGTECIAKYNFHGTAEQDLPFCKGDVLTIVAVTKDPNWYKAKNKVGREGII\n" +
                "PANYVQKREGVKAGTKLSLMPWFHGKITREQAERLLYPPETGLFLVRESTNYPGDYTLCV\n" +
                "SCDGKVEHYRIMYHASKLSIDEEVYFENLMQLVEHYTSDADGLCTRLIKPKVMEGTVAAQ\n" +
                "DEFYRSGWALNMKELKLLQTIGKGEFGDVMLGDYRGNKVAVKCIKNDATAQAFLAEASVM\n" +
                "TQLRHSNLVQLLGVIVEEKGGLYIVTEYMAKGSLVDYLRSRGRSVLGGDCLLKFSLDVCE\n" +
                "AMEYLEGNNFVHRDLAARNVLVSEDNVAKVSDFGLTKEASTQDTGKLPVKWTAPEALREK\n" +
                "KFSTKSDVWSFGILLWEIYSFGRVPYPRIPLKDVVPRVEKGYKMDAPDGCPPAVYEVMKN\n" +
                "CWHLDAAMRPSFLQLREQLEHIKTHELHL";
        BioSource organism = createBiosource("rat", "Rattus norvegicus", "10116");

        this.context.setSequence(sequence);
        this.context.setOrganism(organism);
        this.context.setEnsemblGene("ENSRNOG00000019374");

        try {
            String ac = this.process.runAction(context);
            List<ActionReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(3, reports.size());
            for (String warn : reports.get(2).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(2).getStatus().getLabel() + " " + reports.get(2).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(true, reports.get(2) instanceof BlastReport);
            Assert.assertEquals(true, ((BlastReport) reports.get(2)).getBlastMatchingProteins().size() > 0);
            Assert.assertEquals(StatusLabel.FAILED, reports.get(1).getStatus().getLabel());
            Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, reports.get(2).getStatus().getLabel());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
