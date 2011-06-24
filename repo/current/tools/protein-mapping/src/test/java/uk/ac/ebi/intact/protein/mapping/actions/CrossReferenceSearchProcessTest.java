package uk.ac.ebi.intact.protein.mapping.actions;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.factories.impl.DefaultReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

/**
 * Unit test for CrossReferenceSearchProcess
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12-May-2010</pre>
 */

public class CrossReferenceSearchProcessTest {

    private CrossReferenceSearchProcess process;
    private IdentificationContext context;

    public CrossReferenceSearchProcessTest(){
        this.process = new CrossReferenceSearchProcess(new DefaultReportsFactory());
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
    public void test_GeneIdSearch_Successfull_Mouse(){
        String identifier = "212483";
        BioSource organism = createBiosource("mouse", "Mus Musculus", "10090");
        String acToFind = "Q3U2K0";
        String geneIdDatabase = "MI:0477";

        this.context.setIdentifier(identifier);
        this.context.setOrganism(organism);
        context.setDatabaseForIdentifier(geneIdDatabase);

        try {
            String ac = this.process.runAction(context);

            for (MappingReport r : process.getListOfActionReports()){
                System.out.println("name = " + r.getName() + " " + r.getStatus().getLabel() + " " + r.getStatus().getDescription());
                for (String warn : r.getWarnings()){
                    System.out.println(warn);
                }
            }

            Assert.assertNotNull(ac);
            Assert.assertEquals(acToFind, ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_GeneIdSearch_Successfull_WithoutOrganism(){
        String identifier = "212483";
        String acToFind = "Q3U2K0";
        String geneIdDatabase = "MI:0477";

        this.context.setIdentifier(identifier);
        this.context.setOrganism(null);
        context.setDatabaseForIdentifier(geneIdDatabase);        

        try {
            String ac = this.process.runAction(context);

            for (MappingReport r : process.getListOfActionReports()){
                System.out.println("name = " + r.getName() + " " + r.getStatus().getLabel() + " " + r.getStatus().getDescription());
                for (String warn : r.getWarnings()){
                    System.out.println(warn);
                }
            }

            Assert.assertNotNull(ac);
            Assert.assertEquals(acToFind, ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_GISearch_UnSuccessfull_WithoutOrganism(){
        String identifier = "4506587";
        String acToFind = "P35244";
        String geneBankDatabase = "MI:0860";

        this.context.setIdentifier(identifier);
        this.context.setOrganism(null);
        context.setDatabaseForIdentifier(geneBankDatabase);


        try {
            String ac = this.process.runAction(context);

            for (MappingReport r : process.getListOfActionReports()){
                System.out.println("name = " + r.getName() + " " + r.getStatus().getLabel() + " " + r.getStatus().getDescription());
                for (String warn : r.getWarnings()){
                    System.out.println(warn);
                }
            }

            Assert.assertNull(ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_GISearch_UnSuccessfull_WithOrganism(){
        String identifier = "UPI0000D61C21";
        //String acToFind = "P35244";
        BioSource organism = createBiosource("human", "Homo Sapiens", "9606");


        this.context.setIdentifier(identifier);
        this.context.setOrganism(organism);

        try {
            String ac = this.process.runAction(context);

            MappingReport lastReport = process.getListOfActionReports().get(process.getListOfActionReports().size() - 1);
            for (String warn : lastReport.getWarnings()){
                System.out.println(warn);
            }

            System.out.println(lastReport.getStatus().getLabel() + " " + lastReport.getStatus().getDescription());

            Assert.assertNull(ac);

        } catch (ActionProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
