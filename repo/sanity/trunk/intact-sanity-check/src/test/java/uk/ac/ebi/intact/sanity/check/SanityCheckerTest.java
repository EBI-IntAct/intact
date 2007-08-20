package uk.ac.ebi.intact.sanity.check;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.standard.ExperimentPersister;
import uk.ac.ebi.intact.core.persister.standard.InteractorPersister;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.rules.RuleRunReport;

import java.util.Arrays;
import java.util.Collections;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckerTest extends IntactBasicTestCase
{
    @Before
    public void prepare() throws Exception {
        RuleRunReport.getInstance().clear();
    }

    @Test
    public void executeSanityCheck_default() throws Exception {

        // Add some random data
         for (int i=0; i<10; i++) {
            Experiment exp = getMockBuilder().createExperimentRandom(15);

            beginTransaction();
            ExperimentPersister.getInstance().saveOrUpdate(exp);
            InteractorPersister.getInstance().commit();
            commitTransaction();
        }

        //RuleRunReport report = SanityChecker.executeSanityCheck(super.getSanityCheckConfig());
        long start = System.currentTimeMillis();
        SanityChecker.executeSanityCheck(null);

        System.out.println("Messages: "+RuleRunReport.getInstance().getMessages().size());
        System.out.println("Elapsed time: "+(System.currentTimeMillis()-start)+"ms");

    }

    @Test
    public void checkAnnotatedObjects_cvObjects_noXrefs() throws Exception {
        CvDatabase cvPubmed = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.PUBMED_MI_REF, CvDatabase.PUBMED);
        cvPubmed.getXrefs().clear();

        RuleRunReport report = SanityChecker.checkAnnotatedObjects(Arrays.asList(cvPubmed));

        Assert.assertEquals(1, report.getMessages().size());
    }

    @Test
    public void checkAnnotatedObjects_interactions() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.setExperiments(Collections.EMPTY_LIST);
        
        RuleRunReport report = SanityChecker.checkAnnotatedObjects(Arrays.asList(interaction));

        Assert.assertEquals(1, report.getMessages().size());
    }

    @Test
    public void checkAnnotatedObjects_interactors_interaction() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.setExperiments(Collections.EMPTY_LIST);

        RuleRunReport report = SanityChecker.checkAnnotatedObjects(Arrays.asList(interaction));

        Assert.assertEquals(1, report.getMessages().size());
    }

    @Test
    public void checkAnnotatedObjects_interactors_protein() throws Exception {
        Protein protein = getMockBuilder().createProteinRandom();

        RuleRunReport report = SanityChecker.checkAnnotatedObjects(Arrays.asList(protein));

        Assert.assertEquals(0, report.getMessages().size());
    }


}
