package uk.ac.ebi.intact.protein.mapping.actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.ActionReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.PICRReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import java.util.List;

/**
 * Unit test for PICRSearchProcessWithAccession.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28-Apr-2010</pre>
 */

public class PICRSearchProcessWithAccessionTest {

    private PICRSearchProcessWithAccession process;
    private IdentificationContext context;

    @Before
    public void createBlastProcess(){
        this.process = new PICRSearchProcessWithAccession();
        this.context = new IdentificationContext();
    }

    private BioSource createBiosource(String shortLabel, String fullName, String taxId){
        BioSource bioSource = new BioSource();
        bioSource.setFullName(fullName);
        bioSource.setShortLabel(shortLabel);
        bioSource.setTaxId(taxId);

        return bioSource;
    }

    @Test
    public void test_PICRProcess_successful_withoutOrganism(){
         String id = "IPI00022256";

        this.context.setIdentifier(id);
        this.context.setOrganism(null);

        try {
            String ac = this.process.runAction(context);
            List<ActionReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals(true, reports.get(0) instanceof PICRReport);
            Assert.assertEquals("P84092", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_PICRProcess_successful_withOrganism(){
         String id = "IPI00022256";
         BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setIdentifier(id);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<ActionReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals(true, reports.get(0) instanceof PICRReport);
            Assert.assertEquals("Q96CW1", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_PICRProcess_unSuccessful_withOrganism(){
         String id = "IPI00334775";
         BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setIdentifier(id);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<ActionReport> reports = this.process.getListOfActionReports();

            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(true, reports.get(0) instanceof PICRReport);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
