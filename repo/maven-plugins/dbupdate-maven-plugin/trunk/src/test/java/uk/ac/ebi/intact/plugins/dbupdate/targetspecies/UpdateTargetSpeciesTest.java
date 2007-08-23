/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.plugins.dbupdate.targetspecies;

import junitx.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.core.persister.standard.CvObjectPersister;
import uk.ac.ebi.intact.core.persister.standard.ExperimentPersister;
import uk.ac.ebi.intact.core.unit.IntactBasicTestCase;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.unit.IntactUnit;
import uk.ac.ebi.intact.model.*;

import java.util.*;

//@IntactUnitDataset(  dataset = PsiTestDatasetProvider.ALL_CVS, provider = PsiTestDatasetProvider.class )
public class UpdateTargetSpeciesTest extends IntactBasicTestCase {


    private CvTopic noUniprotUpdate;
    private CvDatabase newt;
    private CvXrefQualifier targetSpeciesQual;

    @Before
    public void prepareCvs() throws Exception {
        new IntactUnit().resetSchema();

        IntactMockBuilder mockBuilder = new IntactMockBuilder();

        newt = mockBuilder.createCvObject(CvDatabase.class, CvDatabase.NEWT_MI_REF, CvDatabase.NEWT);
        targetSpeciesQual = mockBuilder.createCvObject(CvXrefQualifier.class, "IA:0001", CvXrefQualifier.TARGET_SPECIES);

        beginTransaction();
        CvObjectPersister.getInstance().saveOrUpdate(newt);
        CvObjectPersister.getInstance().saveOrUpdate(targetSpeciesQual);
        CvObjectPersister.getInstance().commit();
        commitTransaction();
    }

    @After
    public void endTest() throws Exception {
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

        List<ExperimentXref> targetSpeciesXrefs = updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp);
        Collections.sort(targetSpeciesXrefs, new ExperimentXrefComparator());
        Assert.assertEquals(2, targetSpeciesXrefs.size());
        Assert.assertEquals("7227", targetSpeciesXrefs.get(0).getPrimaryId());
        Assert.assertEquals("drome", targetSpeciesXrefs.get(0).getSecondaryId());
        Assert.assertEquals("9606", targetSpeciesXrefs.get(1).getPrimaryId());
        Assert.assertEquals("human", targetSpeciesXrefs.get(1).getSecondaryId());

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

        List<ExperimentXref> targetSpeciesXrefs = updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp);
        Collections.sort(targetSpeciesXrefs, new ExperimentXrefComparator());
        Assert.assertEquals(2, targetSpeciesXrefs.size());
        Assert.assertEquals("7227", targetSpeciesXrefs.get(0).getPrimaryId());
        Assert.assertEquals("drome", targetSpeciesXrefs.get(0).getSecondaryId());
        Assert.assertEquals("9606", targetSpeciesXrefs.get(1).getPrimaryId());
        Assert.assertEquals("human", targetSpeciesXrefs.get(1).getSecondaryId());

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

        List<ExperimentXref> targetSpeciesXrefs = updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp);
        Collections.sort(targetSpeciesXrefs, new ExperimentXrefComparator());
        Assert.assertEquals(2, targetSpeciesXrefs.size());
        Assert.assertEquals("7227", targetSpeciesXrefs.get(0).getPrimaryId());
        Assert.assertEquals("drome", targetSpeciesXrefs.get(0).getSecondaryId());
        Assert.assertEquals("9606", targetSpeciesXrefs.get(1).getPrimaryId());
        Assert.assertEquals("human", targetSpeciesXrefs.get(1).getSecondaryId());

        commitTransaction();
    }

    @Test
    public void update_existingLegacyTarget2() throws Exception {
        BioSource human = getMockBuilder().createBioSource(9606, "human");
        BioSource drome = getMockBuilder().createBioSource(7227, "drome");

        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        ExperimentXref xref = getMockBuilder().createXref(experiment, "44545", targetSpeciesQual, newt);
        xref.setSecondaryId("unknown");
        experiment.addXref(xref);
        ExperimentXref xref2 = getMockBuilder().createXref(experiment, "9606", targetSpeciesQual, newt);
        xref2.setSecondaryId("human");
        experiment.addXref(xref2);

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

        List<ExperimentXref> targetSpeciesXrefs = updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp);
        Collections.sort(targetSpeciesXrefs, new ExperimentXrefComparator());
        Assert.assertEquals(2, targetSpeciesXrefs.size());
        Assert.assertEquals("7227", targetSpeciesXrefs.get(0).getPrimaryId());
        Assert.assertEquals("drome", targetSpeciesXrefs.get(0).getSecondaryId());
        Assert.assertEquals("9606", targetSpeciesXrefs.get(1).getPrimaryId());
        Assert.assertEquals("human", targetSpeciesXrefs.get(1).getSecondaryId());

        commitTransaction();
    }

    @Test
    public void update_existingLegacyTarget2_ThreeInteractors() throws Exception {
        BioSource human = getMockBuilder().createBioSource(9606, "human");
        BioSource drome = getMockBuilder().createBioSource(7227, "drome");

        Experiment experiment = getMockBuilder().createExperimentRandom(1);
        ExperimentXref xref = getMockBuilder().createXref(experiment, "44545", targetSpeciesQual, newt);
        xref.setSecondaryId("unknown");
        experiment.addXref(xref);
        ExperimentXref xref2 = getMockBuilder().createXref(experiment, "9606", targetSpeciesQual, newt);
        xref2.setSecondaryId("human");
        experiment.addXref(xref2);

        List<Interactor> interactors = new ArrayList<Interactor>();

        for (Component comp : experiment.getInteractions().iterator().next().getComponents()) {
            interactors.add(comp.getInteractor());
        }

        interactors.get(0).setBioSource(human);
        interactors.get(1).setBioSource(drome);

        Protein interactor3 = getMockBuilder().createProteinRandom();
        interactor3.setBioSource(human);
        interactors.add(interactor3);

        persistExperiment(experiment);

        UpdateTargetSpecies updateTargetSpecies = new UpdateTargetSpecies();
        updateTargetSpecies.updateExperiment(experiment);

        beginTransaction();

        Experiment reloadedExp = getDaoFactory().getExperimentDao().getByShortLabel(experiment.getShortLabel());

        Assert.assertEquals(3, reloadedExp.getXrefs().size());

        List<ExperimentXref> targetSpeciesXrefs = updateTargetSpecies.getTargetSpeciesXrefs(reloadedExp);
        Collections.sort(targetSpeciesXrefs, new ExperimentXrefComparator());
        Assert.assertEquals(2, targetSpeciesXrefs.size());
        Assert.assertEquals("7227", targetSpeciesXrefs.get(0).getPrimaryId());
        Assert.assertEquals("drome", targetSpeciesXrefs.get(0).getSecondaryId());
        Assert.assertEquals("9606", targetSpeciesXrefs.get(1).getPrimaryId());
        Assert.assertEquals("human", targetSpeciesXrefs.get(1).getSecondaryId());

        commitTransaction();
    }

    protected void persistExperiment(Experiment experiment) throws Exception {
         beginTransaction();
        ExperimentPersister.getInstance().saveOrUpdate(experiment);
        ExperimentPersister.getInstance().commit();
        commitTransaction();
    }

    private class ExperimentXrefComparator implements Comparator<ExperimentXref> {
        public int compare(ExperimentXref experimentXref, ExperimentXref experimentXref1) {
            return experimentXref.getPrimaryId().compareTo(experimentXref1.getPrimaryId());
        }
    }
}

