package uk.ac.ebi.intact.protein.mapping.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.intact.commons.util.Crc64;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.factories.impl.DefaultReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultIntactCrc64Report;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import java.util.List;

/**
 * Unit test for IntactCrc64SearchProcess
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Apr-2010</pre>
 */
@ContextConfiguration(locations = {"classpath*:/META-INF/jpa.test.spring.xml"})
public class IntactCrc64SearchProcessTest extends IntactBasicTestCase {

    private IntactCrc64SearchProcess process;
    private IdentificationContext context;
    private IntactContext intactContext;
    private String acToFind;

    @Before
    public void createProcess(){
        this.process = new IntactCrc64SearchProcess(new DefaultReportsFactory());
        this.context = new IdentificationContext();

        this.intactContext = IntactContext.getCurrentInstance();

        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";

        Protein prot = getMockBuilder().createProtein("P12345", "test-protein");
        prot.getXrefs().clear();
        prot.setBioSource( createBiosource("xenla", "Xenopus laevis", "8355") );
        prot.setSequence(sequence);
        prot.setCrc64(Crc64.getCrc64(sequence));

        this.intactContext.getCorePersister().saveOrUpdate(prot);
        acToFind = prot.getAc();

    }

    @Test
    public void test_IntactSearch_successful_withoutOrganism(){
        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";

        initialiseContext(sequence, null);

        try {
            String id = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(0) instanceof DefaultIntactCrc64Report);
            Assert.assertNotNull(((DefaultIntactCrc64Report)reports.get(0)).getIntactAc());
            Assert.assertEquals(acToFind, ((DefaultIntactCrc64Report)reports.get(0)).getIntactAc());

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }
            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_IntactSearch_successful_withOrganism(){
        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";
        BioSource bioSource = createBiosource("xenla", "Xenopus laevis", "8355");

        initialiseContext(sequence, bioSource);

        try {
            String id = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(0) instanceof DefaultIntactCrc64Report);
            Assert.assertNotNull(((DefaultIntactCrc64Report)reports.get(0)).getIntactAc());
            Assert.assertEquals(acToFind, ((DefaultIntactCrc64Report)reports.get(0)).getIntactAc());

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_IntactSearch_notSuccessful_withOrganism(){
        String sequence = "GTRASKHVFEKNLRPKALKLKNAEHCSIITKETARTVLTIQSYLQSISNPEWAAAIAHKIAQELPTGPDKIHALKFCLHLAEKWKKNVSSENDAHEKADVFIKKLSVQYQRSATENVLITHKLNTPELLKQIGKPANLIVSLYEHSSVEQRIRHPTGRDYPDIHTAAKQISEVNNLNMSKICTLLLEKWICPPAVPQADKNKDVFGDIHGDEDLRRVIYLLQPYPVDYSSRMLYAIATSATS";
        BioSource bioSource = createBiosource("human", "homo sapiens", "9606");

        initialiseContext(sequence, bioSource);

        try {
            String id = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(0) instanceof DefaultIntactCrc64Report);
            Assert.assertNull(((DefaultIntactCrc64Report)reports.get(0)).getIntactAc());

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