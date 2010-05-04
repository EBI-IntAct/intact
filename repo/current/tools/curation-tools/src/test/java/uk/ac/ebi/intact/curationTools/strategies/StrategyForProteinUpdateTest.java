package uk.ac.ebi.intact.curationTools.strategies;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.UpdateContext;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.model.BioSource;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30-Apr-2010</pre>
 */

public class StrategyForProteinUpdateTest {

    private StrategyForProteinUpdate strategy;

    @Before
    public void createStrategy(){
        this.strategy = new StrategyForProteinUpdate();
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
    public void test_Sequence_Mouse_BasicBlastProcessSuccessful(){
        String sequence = "METSVSEIQVETKDEKGPVAASPQKERQERKTATLCFKRRKKANKTKPKAGSRTAEETKKHPPEAGGSGQRQPAGAWASIKGLVTHRKRSEPAKKQKPPEAEVQPEDGALPKKKAKSRLKFPCLRFSRGAKRSRHSKLTEDSGYVRVQGEADDLEIKAQTQPDDQAIQAGSTQGLQEGVLVRDGKKSQESHISNSVTSGENVIAIELELENKSSAIQMGTPELEKETKVITEKPSVQTQRASLLESSAAGSPRSVTSAAPPSPATTHQHSLEEPSNGIRESAPSGKDDRRKTAAEEKKSGETALGQAEEAAVGQADKRALSQAGEATAGHPEEATVIQAESQAKEGKLSQAEETTVAQAKETVLSQAKEGELSQAKKATVGQAEEATIDHTEKVTVDQAEETTVGQAEEATVGQAGEAILSQAKEATVVGQAEEATVDRAEEATVGQAEEATVGHTEKVTVDQAEEATVGQAEEATVGQAEEATVDWAEKPTVGQAEEATVGQAEEATVGHTEKVTVDQAEEATVGQAEEATVGHTEKVTVDHAEEATVGQAEEATVGQAEKVTVDHAEEATVGQAEEATVGQAEKVTVDHAEEATVGQAEEATVGQAEKVTVDQAEEPTVDQAEEAISSHAPDLKENGIDTEKPRSEESKRMEPIAIIITDTEISEFDVKKSKNVPKQFLISMENEQVGVFANDSDFEGRTSEQYETLLIETASSLVKNAIELSVEQLVNEMVSEDNQINTLFQ";
        BioSource organism = createBiosource("mouse", "Mus musculus", "10090");

        UpdateContext context = new UpdateContext();
        context.setSequence(sequence);
        context.setOrganism(organism);

        this.strategy.setBasicBlastProcessRequired(true);

        IdentificationResults result = null;
        try {
            result = this.strategy.identifyProtein(context);

            Assert.assertNotNull(result);

            for (ActionReport r : result.getListOfActions()){
                System.out.println("Label : " + r.getStatus().getLabel().toString() + ": Description : " + r.getStatus().getDescription());
            }

            Assert.assertNull(result.getUniprotId());
            Assert.assertEquals(true, result.getLastAction() instanceof BlastReport);
            Assert.assertEquals(StatusLabel.TO_BE_REVIEWED, result.getLastAction().getStatus().getLabel());
        } catch (StrategyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
