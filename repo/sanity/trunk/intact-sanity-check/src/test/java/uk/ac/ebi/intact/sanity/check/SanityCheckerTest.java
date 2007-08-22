package uk.ac.ebi.intact.sanity.check;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.standard.ExperimentPersister;
import uk.ac.ebi.intact.core.persister.standard.InteractorPersister;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.commons.SanityReport;
import uk.ac.ebi.intact.sanity.commons.rules.report.HtmlReportWriter;
import uk.ac.ebi.intact.sanity.commons.rules.report.ReportWriter;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckerTest extends AbstractSanityCheckTest
{

    @Test
    public void executeSanityCheck_default() throws Exception {

        // Add some random data
         for (int i=0; i<5; i++) {
            Experiment exp = getMockBuilder().createExperimentRandom(2);
            exp.getXrefs().clear();

            beginTransaction();
            ExperimentPersister.getInstance().saveOrUpdate(exp);
            InteractorPersister.getInstance().commit();
            commitTransaction();
        }

        SanityCheckConfig sanityConfig = getSanityCheckConfig();
        sanityConfig.setEnableAdminMails(true);
        sanityConfig.setEnableUserMails(true);

        SanityReport report = SanityChecker.executeSanityCheck(sanityConfig);

        SanityReportMailer mailer = new SanityReportMailer(sanityConfig);
        mailer.mailReports(report);

    }

    @Test
    public void checkAnnotatedObjects_cvObjects_noXrefs() throws Exception {
        CvDatabase cvPubmed = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.PUBMED_MI_REF, CvDatabase.PUBMED);
        cvPubmed.getXrefs().clear();
        populateAuditable(cvPubmed);

        SanityReport report = SanityChecker.checkAnnotatedObjects(Arrays.asList(cvPubmed));

        Assert.assertEquals(1, report.getSanityResult().size());
    }

    @Test
    public void checkAnnotatedObjects_interactions() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.setExperiments(Collections.EMPTY_LIST);
        populateAuditable(interaction);
        
        SanityReport report = SanityChecker.checkAnnotatedObjects(Arrays.asList(interaction));

        Assert.assertEquals(1, report.getSanityResult().size());
    }

    @Test
    public void checkAnnotatedObjects_interactors_interaction() throws Exception {
        Interaction interaction = getMockBuilder().createInteractionRandomBinary();
        interaction.setExperiments(Collections.EMPTY_LIST);
        populateAuditable(interaction);

        SanityReport report = SanityChecker.checkAnnotatedObjects(Arrays.asList(interaction));

        Assert.assertEquals(1, report.getSanityResult().size());
    }

    @Test
    public void checkAnnotatedObjects_interactors_protein() throws Exception {
        Protein protein = getMockBuilder().createProteinRandom();
        populateAuditable(protein);

        SanityReport report = SanityChecker.checkAnnotatedObjects(Arrays.asList(protein));

        Assert.assertEquals(0, report.getSanityResult().size());
    }

    protected void populateAuditable(Auditable auditable) {
        auditable.setCreated(new Date(new Random().nextLong()));
        auditable.setUpdated(new Date(new Random().nextLong()));
        auditable.setCreator("peter");
        auditable.setUpdator("anne");
    }

}
