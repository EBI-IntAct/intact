package uk.ac.ebi.intact.protein.mapping.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.factories.impl.DefaultReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultIntactReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import java.util.List;

/**
 * Unit test for IntactNameSearchProcess
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Apr-2010</pre>
 */
@ContextConfiguration(locations = {"classpath*:/META-INF/jpa.test.spring.xml"})
public class IntactNameSearchProcessTest  extends IntactBasicTestCase {

    private IntactNameSearchProcess process;
    private IdentificationContext context;
    private IntactContext intactContext;
    private String acToFind;

    @Before
    public void createBlastProcess(){

        this.process = new IntactNameSearchProcess(new DefaultReportsFactory());
        this.context = new IdentificationContext();

        this.intactContext = IntactContext.getCurrentInstance();

        Protein prot = getMockBuilder().createProtein("P12345", "intf_ecoli");
        prot.setFullName("Putative prophage CP4-6 integrase");
        prot.setBioSource( createBiosource("ecoli-2", "Escherichia coli (K12)", "83333") );

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
    public void test_IntactSearch_Successful_withoutOrganism(){
        String name = "intf_ecoli";

        this.context.setGlobalName(name);
        this.context.setOrganism(null);
        try {
            String id = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(0) instanceof DefaultIntactReport);
            Assert.assertNotNull(((DefaultIntactReport)reports.get(0)).getIntactAc());
            Assert.assertEquals(acToFind, ((DefaultIntactReport)reports.get(0)).getIntactAc());
        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_IntactSearch_Successful_withoutOrganismInShortLabel(){
        String name = "intf";

        this.context.setGlobalName(name);
        this.context.setOrganism(null);
        try {
            String id = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(1) instanceof DefaultIntactReport);
            Assert.assertNotNull(((DefaultIntactReport)reports.get(1)).getIntactAc());
            Assert.assertEquals(acToFind, ((DefaultIntactReport)reports.get(1)).getIntactAc());
        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_IntactSearch_Successful_withOrganism(){
        String name = "intf_ecoli";
        BioSource organism = createBiosource("ecoli-2", "Escherichia coli (K12)", "83333");

        this.context.setGlobalName(name);
        this.context.setOrganism(organism);
        try {
            String id = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(0) instanceof DefaultIntactReport);
            Assert.assertNotNull(((DefaultIntactReport)reports.get(0)).getIntactAc());
            Assert.assertEquals(acToFind, ((DefaultIntactReport)reports.get(0)).getIntactAc());
        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_IntactSearch_Unsuccessful_withOrganism(){
        String name = "casa";
        BioSource organism = createBiosource("ecoli-2", "Escherichia coli (K12)", "83333");

        this.context.setGlobalName(name);
        this.context.setOrganism(organism);
        try {
            String id = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(1) instanceof DefaultIntactReport);
            Assert.assertNull(((DefaultIntactReport)reports.get(1)).getIntactAc());
        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_IntactSearch_Successful_withoutOrganismAndWithFullName(){
        String name = "prophage";

        this.context.setGlobalName(name);
        this.context.setOrganism(null);
        try {
            String id = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();

            for (MappingReport report : reports){
                for (String warn : report.getWarnings()){
                    System.out.println(warn);
                }
                System.out.println(report.getStatus().getLabel() + " " + report.getStatus().getDescription());
            }

            Assert.assertNull(id);
            Assert.assertEquals(true, reports.get(reports.size() - 1) instanceof DefaultIntactReport);
            Assert.assertNotNull(((DefaultIntactReport)reports.get(reports.size() - 1)).getIntactAc());
            Assert.assertTrue(((DefaultIntactReport)reports.get(reports.size() - 1)).getPossibleIntactIds().isEmpty());
        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}