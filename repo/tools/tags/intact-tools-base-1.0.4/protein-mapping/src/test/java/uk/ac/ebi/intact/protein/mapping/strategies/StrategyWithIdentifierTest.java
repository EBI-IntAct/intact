package uk.ac.ebi.intact.protein.mapping.strategies;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.model.BioSource;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultBlastReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultMappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.impl.DefaultPICRReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

import java.io.*;
import java.util.ArrayList;

/**
 * Unit test for StrategyWithIdentifier
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Apr-2010</pre>
 */

public class StrategyWithIdentifierTest {

    private StrategyWithIdentifier strategy;

    public StrategyWithIdentifierTest(){
        this.strategy = new StrategyWithIdentifier();
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
    public void test_PICR_Swissprot_Successfull(){
        // 46 identifiers to test
        File file = new File(getClass().getResource("/Identifiers_PICR_Swissprot.csv").getFile());
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();
            while (line != null){
                String [] protein = line.split("\t");
                String identifier = protein[0];
                String ac_toFind = protein[1];

                System.out.println("identifier " + identifier);

                IdentificationContext context = new IdentificationContext();
                context.setIdentifier(identifier);
                context.setDatabaseForIdentifier("ipi");
                context.setOrganism(organism);

                IdentificationResults result = this.strategy.identifyProtein(context);

                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getFinalUniprotId());
                Assert.assertEquals(ac_toFind, result.getFinalUniprotId());
                Assert.assertEquals(true, result.getLastAction() instanceof DefaultPICRReport);
                Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
                Assert.assertEquals(true, result.getLastAction().isASwissprotEntry());

                line = reader.readLine();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_Swissprot_Remapping_Successfull(){
        // 3 identifiers to test
        File file = new File(getClass().getResource("/SwissprotRemapping.csv").getFile());
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();
            while (line != null){
                String [] protein = line.split("\t");
                String identifier = protein[0];
                String ac_toFind = protein[1];

                System.out.println("identifier " + identifier);

                IdentificationContext context = new IdentificationContext();
                context.setIdentifier(identifier);
                context.setDatabaseForIdentifier("ipi");
                context.setOrganism(organism);

                IdentificationResults<DefaultMappingReport> result = this.strategy.identifyProtein(context);

                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getFinalUniprotId());
                Assert.assertEquals(true, result.getLastAction() instanceof DefaultBlastReport);
                Assert.assertEquals(StatusLabel.COMPLETED, result.getListOfActions().get(0).getStatus().getLabel());

                for (MappingReport r : result.getListOfActions()){
                    System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
                }

                if (result.getLastAction().getStatus().getLabel().equals(StatusLabel.COMPLETED)){
                    System.out.println("Remapping done");
                    Assert.assertEquals(ac_toFind, result.getFinalUniprotId());
                }
                else {
                    System.out.println("Remapping to be reviewed");
                    Assert.assertEquals(StatusLabel.COMPLETED, result.getListOfActions().get(0).getStatus().getLabel());
                    Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, result.getLastAction().getStatus().getLabel());

                    ArrayList<String> accessions = new ArrayList<String>();
                    for (BlastResults p : ((DefaultBlastReport)result.getLastAction()).getBlastMatchingProteins()){
                        accessions.add(p.getAccession());
                    }
                    Assert.assertTrue(accessions.contains(ac_toFind));
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Test
    public void test_SwissprotIdentifier_IsoformExcluded(){
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        String identifier = "IPI00220991";
        String ac_to_find = "P63010";

        IdentificationContext context = new IdentificationContext();
        context.setIdentifier(identifier);
        context.setDatabaseForIdentifier("ipi");
        context.setOrganism(organism);

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals(ac_to_find, result.getFinalUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof DefaultPICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isASwissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void test_GeneIDIdentifier_UniprotCrossReference(){
        BioSource organism = createBiosource("mouse", "Mus Musculus", "10090");
        String identifier = "212483";
        String ac_to_find = "Q3U2K0";

        this.strategy.enableIsoforms(true);

        IdentificationContext context = new IdentificationContext();
        context.setIdentifier(identifier);
        context.setOrganism(organism);
        context.setDatabaseForIdentifier("MI:0477"); // database = ENTREZ

        IdentificationResults<DefaultMappingReport> result = null;
        try {
            result = this.strategy.identifyProtein(context);

            for (MappingReport r : result.getListOfActions()){
                System.out.println("name " + r.getName().toString() + " Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
            }

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals(ac_to_find, result.getFinalUniprotId());
            Assert.assertEquals(false, result.getLastAction() instanceof DefaultPICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isASwissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    @Ignore
    public void test_SwissprotIdentifier_Isoform_NotExcluded(){
        BioSource organism = createBiosource("human", "Homo sapiens", "9606");
        String identifier = "IPI00220991";
        String ac_to_find = "P63010-2";

        this.strategy.enableIsoforms(true);

        IdentificationContext context = new IdentificationContext();
        context.setIdentifier(identifier);
        context.setOrganism(organism);
        context.setDatabaseForIdentifier("ipi");

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getFinalUniprotId());
            Assert.assertEquals(ac_to_find, result.getFinalUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof DefaultPICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, result.getLastAction().isASwissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
