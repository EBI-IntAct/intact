package uk.ac.ebi.intact.curationTools.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.model.BioSource;

import java.util.List;

/**
 * Unit test for BasicBlastProcess.
 */
public class BasicBlastProcessTest {

    private BasicBlastProcess blastProcess;
    private BlastContext context;

    @Before
    public void createBlastProcess(){
        this.blastProcess = new BasicBlastProcess();
        this.context = new BlastContext();
    }

    @Test
    public void test_UniprotBlast_successful_withoutOrganism(){
        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";

        initialiseContext(sequence, null);

        try {
            String id = this.blastProcess.runAction(context);
            List<ActionReport> reports = this.blastProcess.getListOfActionReports();

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(0) instanceof BlastReport);
            Assert.assertEquals(true, ((BlastReport)reports.get(0)).getBlastMatchingProteins().isEmpty());
            System.out.println(((BlastReport)reports.get(0)).getBlastMatchingProteins().size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_UniprotBlast_Unsuccessful_withOrganismBecauseOfLowIdentity(){
        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";
        BioSource bioSource = createBiosource("human", "Homo sapiens", "9606");

        initialiseContext(sequence, bioSource);

        try {
            String id = this.blastProcess.runAction(context);
            List<ActionReport> reports = this.blastProcess.getListOfActionReports();

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(0) instanceof BlastReport);
            Assert.assertEquals(0, ((BlastReport)reports.get(0)).getBlastMatchingProteins().size());

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_UniprotBlast_notSuccessful_withOrganism(){
        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";
        BioSource bioSource = createBiosource("Striped hairy-footed hamster", "Phodopus sungorus", "10044");

        initialiseContext(sequence, bioSource);

        try {
            String id = this.blastProcess.runAction(context);
            List<ActionReport> reports = this.blastProcess.getListOfActionReports();

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(0) instanceof BlastReport);
            Assert.assertEquals(StatusLabel.FAILED, reports.get(0).getStatus().getLabel());

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private BioSource createBiosource(String shortLabel, String fullName, String taxId){
        BioSource bioSource = new BioSource();
        bioSource.setFullName(fullName);
        bioSource.setShortLabel(shortLabel);
        bioSource.setTaxId(taxId);

        return bioSource;
    }

    private void initialiseContext(String sequence, BioSource organism){
        this.context.setSequence(sequence);

        this.context.setOrganism(organism);
    }
}
