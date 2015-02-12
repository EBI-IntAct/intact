/**
 * Copyright 2010 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.editor.services.curate.interaction;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.util.Collection;

/**
 */
@Service
public class InteractionEditorService extends AbstractEditorService {

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countXrefs(IntactInteractionEvidence interaction) {
        return getIntactDao().getInteractionDao().countXrefsForInteraction(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAnnotations(IntactInteractionEvidence interaction) {
        return getIntactDao().getInteractionDao().countAnnotationsForInteraction(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countParticipants(IntactInteractionEvidence interaction) {
        return getIntactDao().getInteractionDao().countParticipantsForInteraction(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countConfidences(IntactInteractionEvidence interaction) {
        return getIntactDao().getInteractionDao().countConfidencesForInteraction(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countParameters(IntactInteractionEvidence interaction) {
        return getIntactDao().getInteractionDao().countParametersForInteraction(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countVariableParameterValues(IntactInteractionEvidence interaction) {
        return getIntactDao().getInteractionDao().countVariableParameterValuesSetsForInteraction(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseInteractionXrefs(IntactInteractionEvidence interaction) {
        // reload IntactInteractionEvidence without flushing changes
        IntactInteractionEvidence reloaded = reattachIntactObjectIfTransient(interaction, getIntactDao().getInteractionDao());
        Collection<Xref> xrefs = reloaded.getDbXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseInteractionAnnotations(IntactInteractionEvidence interaction) {
        // reload IntactInteractionEvidence without flushing changes
        IntactInteractionEvidence reloaded = reattachIntactObjectIfTransient(interaction, getIntactDao().getInteractionDao());
        Collection<Annotation> annotations = reloaded.getDbAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseInteractionParameters(IntactInteractionEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactInteractionEvidence reloaded = reattachIntactObjectIfTransient(participantEvidence, getIntactDao().getInteractionDao());
        Collection<Parameter> parameters = reloaded.getParameters();
        initialiseParameters(parameters);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseInteractionVariableParameterValues(IntactInteractionEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactInteractionEvidence reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
        Collection<VariableParameterValueSet> parameters = reloaded.getVariableParameterValues();
        initialiseVariableParameters(parameters);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseInteractionConfidences(IntactInteractionEvidence interaction) {
        // reload feature without flushing changes
        IntactInteractionEvidence reloaded = reattachIntactObjectIfTransient(interaction, getIntactDao().getInteractionDao());
        Collection<Confidence> dets = reloaded.getConfidences();
        for (Confidence det : dets){
            initialiseConfidence(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseParticipants(IntactInteractionEvidence interaction) {
        // reload feature without flushing changes
        IntactInteractionEvidence reloaded = reattachIntactObjectIfTransient(interaction, getIntactDao().getInteractionDao());
        Collection<ParticipantEvidence> dets = reloaded.getParticipants();
        for (ParticipantEvidence det : dets){
            initialiseParticipant(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence loadInteractionByAc(String ac) {
        IntactInteractionEvidence interaction = getIntactDao().getEntityManager().find(IntactInteractionEvidence.class, ac);

        if (interaction != null){
            // initialise experiment
            initialiseExperiment((IntactExperiment)interaction.getExperiment());
            // initialise annotations because needs caution
            initialiseAnnotations(interaction.getDbAnnotations());
            // initialise xrefs because of imex
            initialiseXrefs(interaction.getDbXrefs());
            // initialise participants
            Collection<ParticipantEvidence> dets = interaction.getParticipants();
            for (ParticipantEvidence det : dets){
                initialiseParticipant(det);
            }

            // load base types
            if (interaction.getInteractionType() != null) {
                initialiseCv(interaction.getInteractionType());
            }
        }

        return interaction;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactExperiment loadExperimentByAcOrLabel(String ac) {
        IntactExperiment experiment = getIntactDao().getEntityManager().find(IntactExperiment.class, ac);

        if (experiment == null){
            experiment = getIntactDao().getExperimentDao().getByShortLabel(ac);
        }

        if (experiment != null){
            initialiseExperiment(experiment);
        }

        return experiment;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence reloadFullyInitialisedInteraction(IntactInteractionEvidence interaction) {
        IntactInteractionEvidence reloaded = reattachIntactObjectIfTransient(interaction, getIntactDao().getInteractionDao());

        // initialise experiment
        initialiseExperiment((IntactExperiment) reloaded.getExperiment());
        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getDbAnnotations());
        // initialise xrefs because of imex
        initialiseXrefs(reloaded.getDbXrefs());
        // initialise participants
        Collection<ParticipantEvidence> dets = reloaded.getParticipants();
        for (ParticipantEvidence det : dets){
            initialiseParticipant(det);
        }

        // load base types
        if (reloaded.getInteractionType() != null) {
            CvTerm cv = initialiseCv(reloaded.getInteractionType());
            if (cv != reloaded.getInteractionType()){
                reloaded.setInteractionType(cv);
            }
        }

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractor reloadFullyInitialisedInteractor(IntactInteractor interactor) {
        IntactInteractor reloaded = reattachIntactObjectIfTransient(interactor, getIntactDao().getInteractorDao(IntactInteractor.class));

        // initialise xrefs because of identifiers
        initialiseXrefs(reloaded.getDbXrefs());

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public String computesShortLabel(IntactInteractionEvidence evidence) {
        IntactInteractionEvidence reloaded = reattachIntactObjectIfTransient(evidence, getIntactDao().getInteractionDao());

        String shortLabel = IntactUtils.generateAutomaticInteractionEvidenceShortlabelFor(reloaded, IntactUtils.MAX_SHORT_LABEL_LEN);

        getIntactDao().getEntityManager().detach(reloaded);

        return shortLabel;
    }

    private void initialiseParticipant(ParticipantEvidence det) {
        IntactInteractor interactor = (IntactInteractor)det.getInteractor();
        if (!getIntactDao().getEntityManager().contains(interactor)){
            interactor = getIntactDao().getEntityManager().merge(interactor);
            det.setInteractor(interactor);
        }
        if (interactor.getAc() != null){
            initialiseXrefs(interactor.getDbXrefs());
            initialiseAnnotations(interactor.getDbAnnotations());
            initialiseAliases(interactor.getDbAliases());
        }
        if (interactor instanceof Polymer){
            ((Polymer)interactor).getSequence();
        }

        CvTerm expRole = initialiseCv(det.getExperimentalRole());
        if (expRole != det.getExperimentalRole()){
            det.setExperimentalRole(expRole);
        }
        CvTerm bioRole = initialiseCv(det.getBiologicalRole());
        if (bioRole != det.getBiologicalRole()){
            det.setBiologicalRole(bioRole);
        }
        for (FeatureEvidence f : det.getFeatures()){
           initialiseFeature(f);
        }
    }

    private void initialiseFeature(Feature det) {
        for (Object obj : det.getRanges()){
            Range range = (Range)obj;
            initialisePosition(range.getStart());
            initialisePosition(range.getEnd());
        }

        for (Object linked : det.getLinkedFeatures()){
            ((Feature)linked).getLinkedFeatures();
        }
    }

    private void initialiseExperiment(IntactExperiment experiment) {
        if (experiment.getParticipantIdentificationMethod() != null){
            CvTerm cv = initialiseCv(experiment.getParticipantIdentificationMethod());
            if (cv != experiment.getParticipantIdentificationMethod()){
                experiment.setParticipantIdentificationMethod(cv);
            }
        }
    }

    private void initialiseVariableParameters(Collection<VariableParameterValueSet> parameters) {
        for (VariableParameterValueSet set : parameters){
            for (VariableParameterValue value : set){
                if (((IntactVariableParameterValue)value).getId() != null){
                    Hibernate.initialize(((IntactVariableParameterValue)value).getInteractionParameterValues());
                }
            }
        }
    }
}
