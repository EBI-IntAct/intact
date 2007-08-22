package uk.ac.ebi.intact.plugins.targetspecies;


import junitx.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.standard.CvObjectPersister;
import uk.ac.ebi.intact.core.persister.standard.ExperimentPersister;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//@IntactUnitDataset(  dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
public class UpdateTargetSpeciesTest extends IntactBasicTestCase {


    private CvTopic noUniprotUpdate;
    private CvDatabase newt;
    private CvXrefQualifier targetSpeciesQual;

    @Before
    public void prepareCvs() throws Exception {
        new IntactUnit().resetSchema();

        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        noUniprotUpdate = mockBuilder.createCvObject(CvTopic.class, "IA:0000", CvTopic.NON_UNIPROT);
        newt = mockBuilder.createCvObject(CvDatabase.class, CvDatabase.NEWT_MI_REF, CvDatabase.NEWT);
        targetSpeciesQual = mockBuilder.createCvObject(CvXrefQualifier.class, "IA:0001", CvXrefQualifier.TARGET_SPECIES);

        beginTransaction();
        CvObjectPersister.getInstance().saveOrUpdate(noUniprotUpdate);
        CvObjectPersister.getInstance().saveOrUpdate(newt);
        CvObjectPersister.getInstance().saveOrUpdate(targetSpeciesQual);
        CvObjectPersister.getInstance().commit();
        commitTransaction();
    }

    @Test
    public void getTargetSpeciesXrefs_noneFound() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);

        persistExperiment(experiment);

        UpdateTargetSpecies updateTargetSpecies = new UpdateTargetSpecies();

        Collection<ExperimentXref> targetSpeciesXrefs = updateTargetSpecies.getTargetSpeciesXrefs(experiment);
        Assert.assertEquals(0, targetSpeciesXrefs.size());
    }

    @Test
    public void getTargetSpeciesXrefs_containsOne() throws Exception {
        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        ExperimentXref xref = getMockBuilder().createXref(experiment, "9606", targetSpeciesQual, newt);
        experiment.addXref(xref);

        persistExperiment(experiment);

        UpdateTargetSpecies updateTargetSpecies = new UpdateTargetSpecies();

        beginTransaction();
        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel(experiment.getShortLabel());

        Collection<ExperimentXref> targetSpeciesXrefs = updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp);
        Assert.assertEquals(1, targetSpeciesXrefs.size());

        commitTransaction();
    }

    @Test
    public void update_default() throws Exception {
        BioSource human = getMockBuilder().createBioSource(9606, "human");
        BioSource drome = getMockBuilder().createBioSource(7227, "drome");

        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        List<Interactor> interactors = new ArrayList<Interactor>();

        for (Component comp : experiment.getInteractions().iterator().next().getComponents()) {
            interactors.add(comp.getInteractor());
        }

        interactors.get(0).setBioSource(human);
        interactors.get(1).setBioSource(drome);

        persistExperiment(experiment);

        UpdateTargetSpecies updateTargetSpecies = new UpdateTargetSpecies();
        updateTargetSpecies.updateExperiment(experiment);

        beginTransaction();

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel(experiment.getShortLabel());

        Assert.assertEquals(3, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp).size());

        commitTransaction();
    }

    @Test
    public void update_existingTarget() throws Exception {
        BioSource human = getMockBuilder().createBioSource(9606, "human");
        BioSource drome = getMockBuilder().createBioSource(7227, "drome");

        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        ExperimentXref xref = getMockBuilder().createXref(experiment, "9606", targetSpeciesQual, newt);
        xref.setSecondaryId("human");
        experiment.addXref(xref);

        List<Interactor> interactors = new ArrayList<Interactor>();

        for (Component comp : experiment.getInteractions().iterator().next().getComponents()) {
            interactors.add(comp.getInteractor());
        }

        interactors.get(0).setBioSource(human);
        interactors.get(1).setBioSource(drome);

        persistExperiment(experiment);

        UpdateTargetSpecies updateTargetSpecies = new UpdateTargetSpecies();
        updateTargetSpecies.updateExperiment(experiment);

        beginTransaction();

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel(experiment.getShortLabel());

        Assert.assertEquals(3, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp).size());

        commitTransaction();
    }

    @Test
    public void update_existingLegacyTarget() throws Exception {
        BioSource human = getMockBuilder().createBioSource(9606, "human");
        BioSource drome = getMockBuilder().createBioSource(7227, "drome");

        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        ExperimentXref xref = getMockBuilder().createXref(experiment, "44545", targetSpeciesQual, newt);
        xref.setSecondaryId("unknown");
        experiment.addXref(xref);

        List<Interactor> interactors = new ArrayList<Interactor>();

        for (Component comp : experiment.getInteractions().iterator().next().getComponents()) {
            interactors.add(comp.getInteractor());
        }

        interactors.get(0).setBioSource(human);
        interactors.get(1).setBioSource(drome);

        persistExperiment(experiment);

        UpdateTargetSpecies updateTargetSpecies = new UpdateTargetSpecies();
        updateTargetSpecies.updateExperiment(experiment);

        beginTransaction();

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel(experiment.getShortLabel());

        Assert.assertEquals(3, reloadedExp.getXrefs().size());
        Assert.assertEquals(2, updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp).size());

        commitTransaction();
    }

    protected void persistExperiment(Experiment experiment) throws Exception {
         beginTransaction();
        ExperimentPersister.getInstance().saveOrUpdate(experiment);
        ExperimentPersister.getInstance().commit();
        commitTransaction();
    }



}

