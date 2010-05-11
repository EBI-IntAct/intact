package uk.ac.ebi.intact.curationTools.strategies;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.model.BioSource;

import java.io.*;
import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Apr-2010</pre>
 */

public class StrategyWithIdentifierTest {

    private StrategyWithIdentifier strategy;

    @Before
    public void createStrategy(){
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
                context.setOrganism(organism);

                IdentificationResults result = this.strategy.identifyProtein(context);

                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getUniprotId());
                Assert.assertEquals(ac_toFind, result.getUniprotId());
                Assert.assertEquals(true, result.getLastAction() instanceof PICRReport);
                Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
                Assert.assertEquals(true, ((PICRReport) result.getLastAction()).isAswissprotEntry());

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
        // 4 identifiers to test
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
                context.setOrganism(organism);

                IdentificationResults result = this.strategy.identifyProtein(context);

                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getUniprotId());
                Assert.assertEquals(true, result.getLastAction() instanceof BlastReport);
                Assert.assertEquals(StatusLabel.COMPLETED, result.getListOfActions().get(0).getStatus().getLabel());

                for (ActionReport r : result.getListOfActions()){
                    System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
                }

                if (result.getLastAction().getStatus().getLabel().equals(StatusLabel.COMPLETED)){
                    System.out.println("Remapping done");
                    Assert.assertEquals(ac_toFind, result.getUniprotId());
                }
                else {
                    System.out.println("Remapping to be reviewed");
                    Assert.assertEquals(StatusLabel.COMPLETED, result.getListOfActions().get(0).getStatus().getLabel());
                    Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, result.getLastAction().getStatus().getLabel());

                    ArrayList<String> accessions = new ArrayList<String>();
                    for (BlastProtein p : ((BlastReport)result.getLastAction()).getBlastMatchingProteins()){
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
        context.setOrganism(organism);

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getUniprotId());
            Assert.assertEquals(ac_to_find, result.getUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof PICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, ((PICRReport) result.getLastAction()).isAswissprotEntry());
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

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);
            Assert.assertNotNull(result.getUniprotId());
            Assert.assertEquals(ac_to_find, result.getUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof PICRReport);
            Assert.assertEquals(StatusLabel.COMPLETED, result.getLastAction().getStatus().getLabel());
            Assert.assertEquals(true, ((PICRReport) result.getLastAction()).isAswissprotEntry());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
