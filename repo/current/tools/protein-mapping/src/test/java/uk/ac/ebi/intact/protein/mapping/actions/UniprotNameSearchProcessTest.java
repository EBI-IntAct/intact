package uk.ac.ebi.intact.protein.mapping.actions;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.impl.DefaultReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import java.util.List;

/**
 * Unit test for UniprotNameSearchProcess
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Apr-2010</pre>
 */

public class UniprotNameSearchProcessTest {

    private UniprotNameSearchProcess process;
    private IdentificationContext context;

    public UniprotNameSearchProcessTest(){
        this.process = new UniprotNameSearchProcess(new DefaultReportsFactory());
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
    public void test_GeneSearch_Successfull(){
        String geneName = "crk";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setGene_name(geneName);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(1, reports.size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals("P46108", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_ProteinSearch_Successfull(){
        String proteinName = "crk";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setProtein_name(proteinName);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(1, reports.size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, reports.get(0).getStatus().getLabel());
            Assert.assertEquals(true, reports.get(0).getPossibleAccessions().size() > 0);

            for (String p : reports.get(0).getPossibleAccessions()){
                 System.out.println("protein : " + p);
            }

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_ProteinAndGeneNameSearch_Successfull(){
        String proteinName = "crk";
        String geneName = "crk";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setProtein_name(proteinName);
        this.context.setGene_name(geneName);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(1, reports.size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals("P46108", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_ProteinAndGeneNameSearch_Unsuccessfull(){
        String proteinName = "cls";
        String geneName = "crk";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setProtein_name(proteinName);
        this.context.setGene_name(geneName);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();
            for (String warn : reports.get(2).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(2).getStatus().getLabel() + " " + reports.get(2).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, reports.get(2).getStatus().getLabel());
            Assert.assertEquals(true, reports.get(2).getPossibleAccessions().size() > 0);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_GlobalSearch_Unsuccessfull(){
        String globalName = "IPI0000340";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setGlobalName(globalName);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();
            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNull(ac);
            Assert.assertEquals(StatusLabel.FAILED, reports.get(1).getStatus().getLabel());
            Assert.assertEquals(true, reports.get(1).getPossibleAccessions().isEmpty());

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_GlobalSearch_Successful(){
        String globalName = "cls";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        this.context.setGlobalName(globalName);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);
            List<MappingReport> reports = this.process.getListOfActionReports();
            Assert.assertEquals(1, reports.size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(ac);
            Assert.assertEquals(StatusLabel.COMPLETED, reports.get(0).getStatus().getLabel());
            Assert.assertEquals("Q9UJA2", ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
