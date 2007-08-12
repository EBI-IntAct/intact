package uk.ac.ebi.intact.sanity.check;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.standard.CvObjectPersister;
import uk.ac.ebi.intact.core.persister.standard.InteractorPersister;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.CvExperimentalRole;
import uk.ac.ebi.intact.model.Protein;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

import java.util.List;
import java.util.Map;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class SanityCheckerTest extends AbstractSanityCheckTest
{


    @Test
    @IntactUnitDataset ( dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
    public void executeSanityCheck_default() throws Exception {

        // create missing CvExperimentalRole
        CvExperimentalRole inhibitedRole = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvExperimentalRole.class, CvExperimentalRole.INHIBITED_PSI_REF, CvExperimentalRole.INHIBITED);

        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution());
        Protein prot = mockBuilder.createProteinRandom();

        beginTransaction();

        CvObjectPersister.getInstance().saveOrUpdate(inhibitedRole);
        CvObjectPersister.getInstance().commit();
        InteractorPersister.getInstance().saveOrUpdate(prot);
        InteractorPersister.getInstance().commit();

        commitTransaction();

        beginTransaction();

        SimpleAdminReport report = SanityChecker.executeSanityCheck(super.getSanityCheckConfig());

        //printReport(report);
        Assert.assertEquals(1, report.getTopicsWithMessages().size());
        Assert.assertEquals(287, report.getMessagesByTopic(ReportTopic.XREF_WITH_NON_VALID_PRIMARYID).size());

        commitTransaction();
    }

    private static void printReport(SimpleAdminReport report) {
        for (Map.Entry<ReportTopic, List<ReportMessage>> entry :  report.getMessages().entrySet()) {
            System.out.println("=====================================");
            System.out.println(entry.getKey().getTitle()+":");
            System.out.println("=====================================");
            System.out.println(report.getHeaderByTopic(entry.getKey()));
            System.out.println("-----------------------------------------------------");
            for (ReportMessage message : entry.getValue()) {
                System.out.println("\t"+message);
            }
        }
    }
}
