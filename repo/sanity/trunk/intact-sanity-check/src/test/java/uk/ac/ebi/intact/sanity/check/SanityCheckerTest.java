package uk.ac.ebi.intact.sanity.check;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.standard.InteractorPersister;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.sanity.commons.rules.RuleRunReport;

import java.util.Arrays;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckerTest extends IntactAbstractTestCase
{
    @Before
    public void prepare() throws Exception {
        RuleRunReport.getInstance().clear();
    }

    @Test
    @Ignore
    public void executeSanityCheck_default() throws Exception {

        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution());
        Protein prot = mockBuilder.createProteinRandom();

        beginTransaction();
        InteractorPersister.getInstance().saveOrUpdate(prot);
        InteractorPersister.getInstance().commit();

        commitTransaction();

        //RuleRunReport report = SanityChecker.executeSanityCheck(super.getSanityCheckConfig());
        beginTransaction();
        SanityChecker.checkAllCvObjects();
        commitTransaction();

        System.out.println("Messages: "+RuleRunReport.getInstance().getMessages());

        //printReport(report);
        //Assert.assertEquals(1, report.getTopicsWithMessages().size());
        //Assert.assertEquals(287, report.getMessagesByTopic(ReportTopic.XREF_WITH_NON_VALID_PRIMARYID).size());

    }

    @Test
    public void checkCvObjects_noXrefs() throws Exception {
        CvDatabase cvPubmed = getMockBuilder().createCvObject(CvDatabase.class, CvDatabase.PUBMED_MI_REF, CvDatabase.PUBMED);
        cvPubmed.getXrefs().clear();

        RuleRunReport report = SanityChecker.checkCvObjects(Arrays.asList(cvPubmed));

        Assert.assertEquals(1, report.getMessages().size());
    }


}
