package uk.ac.ebi.intact.sanity.check;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.PersisterHelper;
import uk.ac.ebi.intact.model.Auditable;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.commons.SanityReport;
import uk.ac.ebi.intact.sanity.commons.rules.RuleRunnerReport;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckerTest extends AbstractSanityCheckTest
{

    @After
    public void prepare() throws Exception {
        RuleRunnerReport.getInstance().clear();
    }

    @Test
    public void executeSanityCheck_default() throws Exception {

        // Add some random data
         for (int i=0; i<5; i++) {
            Experiment exp = getMockBuilder().createExperimentRandom(2);
            exp.getXrefs().clear();

            PersisterHelper.saveOrUpdate(exp);
        }

        SanityCheckConfig sanityConfig = getSanityCheckConfig();

        SanityReport report = SanityChecker.executeSanityCheck(sanityConfig);

        Assert.assertEquals(4, report.getSanityResult().size());
    }

    @Test
    public void checkAnnotatedObjects_interactions() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.setExperiments(Collections.EMPTY_LIST);
        populateAuditable(interaction);
        
        SanityReport report = SanityChecker.executeSanityCheck(Arrays.asList(interaction));

        Assert.assertEquals(1, report.getSanityResult().size());
    }

    @Test
    public void checkAnnotatedObjects_interactors_interaction() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.setExperiments(Collections.EMPTY_LIST);
        populateAuditable(interaction);

        SanityReport report = SanityChecker.executeSanityCheck(Arrays.asList(interaction));

        Assert.assertEquals(1, report.getSanityResult().size());
    }

    @Test
    public void checkAnnotatedObjects_interactors_protein() throws Exception {
        Protein protein = getMockBuilder().createProteinRandom();
        populateAuditable(protein);

        SanityReport report = SanityChecker.executeSanityCheck(Arrays.asList(protein));

        Assert.assertEquals(1, report.getSanityResult().size());
    }

    protected void populateAuditable(Auditable auditable) {
        Calendar cal = Calendar.getInstance();
        cal.set(2007, 5, 15);
        auditable.setCreated(cal.getTime());
        cal.set(2007, 6, 30);
        auditable.setUpdated(cal.getTime());
        auditable.setCreator("peter");
        auditable.setUpdator("anne");
    }

}
