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
        IntactInteractionEvidence reloaded = getIntactDao().getEntityManager().merge(interaction);
        Collection<Xref> xrefs = reloaded.getDbXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseInteractionAnnotations(IntactInteractionEvidence interaction) {
        // reload IntactInteractionEvidence without flushing changes
        IntactInteractionEvidence reloaded = getIntactDao().getEntityManager().merge(interaction);
        Collection<Annotation> annotations = reloaded.getDbAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseInteractionParameters(IntactInteractionEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactInteractionEvidence reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
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
    public IntactInteractionEvidence initialiseInteractionConfidences(IntactInteractionEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactInteractionEvidence reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
        Collection<Confidence> dets = reloaded.getConfidences();
        for (Confidence det : dets){
            initialiseConfidence(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractionEvidence initialiseParticipants(IntactInteractionEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactInteractionEvidence reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
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
    public IntactInteractionEvidence reloadFullyInitialisedInteraction(IntactInteractionEvidence interactor) {
        IntactInteractionEvidence reloaded = getIntactDao().getEntityManager().merge(interactor);

        // initialise experiment
        initialiseExperiment((IntactExperiment)reloaded.getExperiment());
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
            initialiseCv(reloaded.getInteractionType());
        }

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractor reloadFullyInitialisedInteractor(IntactInteractor interactor) {
        IntactInteractor reloaded = getIntactDao().getEntityManager().merge(interactor);

        // initialise xrefs because of identifiers
        initialiseXrefs(reloaded.getDbXrefs());

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    private void initialiseXrefs(Collection<Xref> xrefs) {
        for (Xref ref : xrefs){
            Hibernate.initialize(((IntactCvTerm)ref.getDatabase()).getDbAnnotations());
            Hibernate.initialize(((IntactCvTerm)ref.getDatabase()).getDbXrefs());
            if (ref.getQualifier() != null){
                Hibernate.initialize(((IntactCvTerm)ref.getQualifier()).getDbXrefs());
            }
        }
    }

    private void initialiseAnnotations(Collection<Annotation> annotations) {
        for (Annotation annot : annotations){
            Hibernate.initialize(((IntactCvTerm)annot.getTopic()).getDbAnnotations());
            Hibernate.initialize(((IntactCvTerm)annot.getTopic()).getDbXrefs());
        }
    }

    private void initialiseCv(CvTerm cv) {
        initialiseAnnotations(((IntactCvTerm)cv).getDbAnnotations());
        initialiseXrefs(((IntactCvTerm)cv).getDbXrefs());
    }

    private void initialiseAliases(Collection<Alias> aliases) {
        for (Alias alias : aliases){
            if (alias.getType() != null){
                Hibernate.initialize(((IntactCvTerm)alias.getType()).getDbXrefs());
            }
        }
    }

    private void initialiseInteractorMembers(Collection<Interactor> interactors) {
         for (Interactor interactor : interactors){
             initialiseXrefs(((IntactInteractor)interactor).getDbXrefs());
         }
    }

    private void initialiseSequence(IntactPolymer interactor) {
         interactor.getSequence();
    }

    private void initialiseParameters(Collection<Parameter> parameters) {
        for (Parameter parameter : parameters){
            Hibernate.initialize(((IntactCvTerm)parameter.getType()).getDbXrefs());

            if (parameter.getUnit() != null){
                Hibernate.initialize(((IntactCvTerm)parameter.getUnit()).getDbXrefs());
            }
        }
    }

    private void initialiseConfidence(Confidence det) {
        Hibernate.initialize(((IntactCvTerm) det.getType()).getDbXrefs());
    }

    private void initialiseParticipant(ParticipantEvidence det) {
        IntactInteractor interactor = (IntactInteractor)det.getInteractor();
        initialiseXrefs(interactor.getDbXrefs());
        initialiseAnnotations(interactor.getDbAnnotations());
        if (interactor instanceof Polymer){
            ((Polymer)interactor).getSequence();
        }

        initialiseCv(det.getExperimentalRole());
        initialiseCv(det.getBiologicalRole());
        for (FeatureEvidence f : det.getFeatures()){
           initialiseFeature(f);
        }
    }

    private void initialiseFeature(Feature det) {
        for (Object obj : det.getRanges()){
            Range range = (Range)obj;
            initialiseCv(range.getStart().getStatus());
            initialiseCv(range.getEnd().getStatus());
        }

        for (Object linked : det.getLinkedFeatures()){
            ((Feature)linked).getLinkedFeatures();
        }
    }

    private void initialiseExperiment(IntactExperiment experiment) {
        if (experiment.getParticipantIdentificationMethod() != null){
            initialiseCv(experiment.getParticipantIdentificationMethod());
        }
    }

    private void initialiseVariableParameters(Collection<VariableParameterValueSet> parameters) {
        for (VariableParameterValueSet set : parameters){
            for (VariableParameterValue value : set){
                Hibernate.initialize(((IntactVariableParameterValue)value).getInteractionParameterValues());
            }
        }
    }
}
