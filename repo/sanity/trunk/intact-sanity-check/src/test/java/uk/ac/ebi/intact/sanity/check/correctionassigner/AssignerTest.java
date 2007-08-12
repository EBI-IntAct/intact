package uk.ac.ebi.intact.sanity.check.correctionassigner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.standard.InteractionPersister;
import uk.ac.ebi.intact.core.unit.IntactAbstractTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.check.MessageSender;
import uk.ac.ebi.intact.sanity.check.ReportMessage;
import uk.ac.ebi.intact.sanity.check.ReportTopic;
import uk.ac.ebi.intact.sanity.check.SimpleAdminReport;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IntactUnitDataset( dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
public class AssignerTest extends IntactAbstractTestCase
{
    private Properties sanityCheckTestProps;
    private List<SuperCurator> curators;

    @Before
    public void prepare() throws Exception {
        sanityCheckTestProps = new Properties();
        sanityCheckTestProps.load(AssignerTest.class.getResourceAsStream("/sanityCheck.test.properties"));

        SuperCurator curator = new SuperCurator(100, "John");

        curators = new ArrayList<SuperCurator>();
        curators.add(curator);
    }

    @Test
    public void assign_default() throws Exception {
        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution());
        
        for (int i=0; i<5; i++) {
            Experiment experiment = mockBuilder.createExperimentRandom(5);

            beginTransaction();
            persistExperiment(experiment);
            commitTransaction();
        }

        beginTransaction();

        Assigner assigner = new Assigner(curators, new MessageSender(sanityCheckTestProps), false);
        assigner.assign();

        Assert.assertEquals(5, assigner.getSuperCurator("John").getExperiments().size());

        commitTransaction();
    }

    @Test (expected = AssignerConfigurationException.class )
    public void assign_wrongPercentages() throws Exception {
        curators = new ArrayList<SuperCurator>(2);
        curators.add(new SuperCurator(60, "Peter"));
        curators.add(new SuperCurator(50, "Anne"));

        beginTransaction();

        Assigner assigner = new Assigner(curators, new MessageSender(sanityCheckTestProps), false);
        assigner.assign();

        commitTransaction();
    }

    @Test
    public void assign_twoCurators() throws Exception {
        // Add some random data
        IntactMockBuilder mockBuilder = new IntactMockBuilder(IntactContext.getCurrentInstance().getInstitution());

        beginTransaction();

        for (int i=0; i<5; i++) {
            Experiment experiment = mockBuilder.createExperimentRandom(5);


            persistExperiment(experiment);

        }
        commitTransaction();

        beginTransaction();

        curators = new ArrayList<SuperCurator>(2);
        curators.add(new SuperCurator(50, "Peter"));
        curators.add(new SuperCurator(50, "Anne"));

        Assigner assigner = new Assigner(curators, new MessageSender(sanityCheckTestProps), false);
        assigner.assign();

        Assert.assertEquals(3, assigner.getSuperCurator("Peter").getExperiments().size());
        Assert.assertEquals(2, assigner.getSuperCurator("Anne").getExperiments().size());

        commitTransaction();
    }

    private void persistExperiment(Experiment experiment) throws PersisterException
    {
        for (Interaction interaction : experiment.getInteractions()) {
            InteractionPersister.getInstance().saveOrUpdate(interaction);
        }
        InteractionPersister.getInstance().commit();
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