package uk.ac.ebi.intact.protein.mapping.strategies;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

import java.util.List;

/**
 * Unit test for StrategyWithName
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Apr-2010</pre>
 */

public class StrategyWithNameTest {

    private StrategyWithName strategy;

    public StrategyWithNameTest(){
        this.strategy = new StrategyWithName();
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
    public void test_GeneSearch_Successfull(){
        String geneName = "crk";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        IdentificationContext context = new IdentificationContext();
        context.setGene_name(geneName);
        context.setOrganism(organism);

        try {
            IdentificationResults result = this.strategy.identifyProtein(context);
            List<MappingReport> reports = result.getListOfActions();

            Assert.assertEquals(1, reports.size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals("P46108", result.getFinalUniprotId());

        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_ProteinSearch_Successfull(){
        String proteinName = "crk";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        IdentificationContext context = new IdentificationContext();

        context.setProtein_name(proteinName);
        context.setOrganism(organism);

        try {
            IdentificationResults result = this.strategy.identifyProtein(context);
            List<MappingReport> reports = result.getListOfActions();

            Assert.assertEquals(1, reports.size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(result);
            Assert.assertNull(result.getFinalUniprotId());
            Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, reports.get(0).getStatus().getLabel());
            Assert.assertEquals(true, reports.get(0).getPossibleAccessions().size() > 0);

            for (String p : reports.get(0).getPossibleAccessions()){
                 System.out.println("protein : " + p);
            }

        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_ProteinAndGeneNameSearch_Successfull(){
        String proteinName = "crk";
        String geneName = "crk";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        IdentificationContext context = new IdentificationContext();

        context.setProtein_name(proteinName);
        context.setGene_name(geneName);
        context.setOrganism(organism);

        try {
            IdentificationResults result = this.strategy.identifyProtein(context);
            List<MappingReport> reports = result.getListOfActions();
            Assert.assertEquals(1, reports.size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals("P46108", result.getFinalUniprotId());

        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_ProteinAndGeneNameSearch_Unsuccessfull(){
        String proteinName = "cls";
        String geneName = "crk";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        IdentificationContext context = new IdentificationContext();

       context.setProtein_name(proteinName);
        context.setGene_name(geneName);
        context.setOrganism(organism);

        try {
            IdentificationResults result = this.strategy.identifyProtein(context);
            List<MappingReport> reports = result.getListOfActions();
            for (String warn : reports.get(2).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(2).getStatus().getLabel() + " " + reports.get(2).getStatus().getDescription());

            Assert.assertNotNull(result);            
            Assert.assertNull(result.getFinalUniprotId());
            Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, reports.get(2).getStatus().getLabel());
            Assert.assertEquals(true, reports.get(2).getPossibleAccessions().size() > 0);

        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_GlobalSearch_Unsuccessfull(){
        String globalName = "IPI0000340";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        IdentificationContext context = new IdentificationContext();

        context.setGlobalName(globalName);
        context.setOrganism(organism);

        try {
            IdentificationResults result = this.strategy.identifyProtein(context);
            List<MappingReport> reports = result.getListOfActions();
            for (String warn : reports.get(1).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(1).getStatus().getLabel() + " " + reports.get(1).getStatus().getDescription());

            Assert.assertNotNull(result);
            Assert.assertNull(result.getFinalUniprotId());
            Assert.assertEquals(StatusLabel.FAILED, reports.get(1).getStatus().getLabel());
            Assert.assertEquals(true, reports.get(1).getPossibleAccessions().isEmpty());

        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_GlobalSearch_Successful(){
        String globalName = "cls";
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        IdentificationContext context = new IdentificationContext();

        context.setGlobalName(globalName);
        context.setOrganism(organism);

        try {
            IdentificationResults result = this.strategy.identifyProtein(context);
            List<MappingReport> reports = result.getListOfActions();
            Assert.assertEquals(1, reports.size());
            for (String warn : reports.get(0).getWarnings()){
                System.out.println(warn);
            }

            System.out.println(reports.get(0).getStatus().getLabel() + " " + reports.get(0).getStatus().getDescription());

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals(StatusLabel.COMPLETED, reports.get(0).getStatus().getLabel());
            Assert.assertEquals("Q9UJA2", result.getFinalUniprotId());

        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
