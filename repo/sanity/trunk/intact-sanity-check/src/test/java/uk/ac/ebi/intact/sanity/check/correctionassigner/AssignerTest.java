package uk.ac.ebi.intact.sanity.check.correctionassigner;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.core.persister.standard.InteractionPersister;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnitDataset;
import uk.ac.ebi.intact.model.Experiment;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.sanity.check.AbstractSanityCheckTest;
import uk.ac.ebi.intact.sanity.check.config.SanityCheckConfig;
import uk.ac.ebi.intact.sanity.check.config.SanityConfigurationException;
import uk.ac.ebi.intact.sanity.check.config.SuperCurator;
import uk.ac.ebi.intact.unitdataset.PsiTestDatasetProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@IntactUnitDataset( dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
public class AssignerTest extends AbstractSanityCheckTest
{

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

        SanityCheckConfig config = super.getSanityCheckConfig();
        config.setDisableAdminMails(true);
        config.setDisableUserMails(true);

        Assigner assigner = new Assigner(config, false);
        assigner.assign();

        Assert.assertEquals(5, config.getSuperCurator("John").getExperiments().size());

        commitTransaction();
    }

    @Test (expected = SanityConfigurationException.class )
    public void assign_wrongPercentages() throws Exception {
        List<SuperCurator> curators = new ArrayList<SuperCurator>(2);
        curators.add(new SuperCurator(60, "Peter"));
        curators.add(new SuperCurator(50, "Anne"));

        beginTransaction();

        SanityCheckConfig config = new SanityCheckConfig(curators);
        config.setDisableAdminMails(true);
        config.setDisableUserMails(true);


        Assigner assigner = new Assigner(config, false);
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

        List<SuperCurator> curators = new ArrayList<SuperCurator>(2);
        curators.add(new SuperCurator(50, "Peter"));
        curators.add(new SuperCurator(50, "Anne"));

        beginTransaction();

        SanityCheckConfig config = new SanityCheckConfig(curators);
        config.setDisableAdminMails(true);
        config.setDisableUserMails(true);

        Assigner assigner = new Assigner(config, false);
        assigner.assign();

        Assert.assertEquals(3, config.getSuperCurator("Peter").getExperiments().size());
        Assert.assertEquals(2, config.getSuperCurator("Anne").getExperiments().size());

        commitTransaction();
    }

    private void persistExperiment(Experiment experiment) throws PersisterException
    {
        for (Interaction interaction : experiment.getInteractions()) {
            InteractionPersister.getInstance().saveOrUpdate(interaction);
        }
        InteractionPersister.getInstance().commit();
    }
}