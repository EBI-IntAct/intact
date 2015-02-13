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
package uk.ac.ebi.intact.editor.services.curate.complex;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.editor.controller.curate.cloner.ComplexCloner;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.lifecycle.ComplexLifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import java.util.Collection;

/**
 */
@Service
public class ComplexEditorService extends AbstractEditorService {

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countXrefs(IntactComplex interaction) {
        return getIntactDao().getComplexDao().countXrefsForInteractor(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAnnotations(IntactComplex interaction) {
        return getIntactDao().getComplexDao().countAnnotationsForInteractor(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countParticipants(IntactComplex interaction) {
        return getIntactDao().getComplexDao().countParticipantsForComplex(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countConfidences(IntactComplex interaction) {
        return getIntactDao().getComplexDao().countConfidencesForComplex(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countParameters(IntactComplex interaction) {
        return getIntactDao().getComplexDao().countParametersForComplex(interaction.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex initialiseComplexXrefs(IntactComplex interaction) {
        // reload IntactInteractionEvidence without flushing changes
        IntactComplex reloaded = reattachIntactObjectIfTransient(interaction, getIntactDao().getComplexDao());
        Collection<Xref> xrefs = reloaded.getDbXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex initialiseComplexAliases(IntactComplex interaction) {
        // reload IntactInteractionEvidence without flushing changes
        IntactComplex reloaded = reattachIntactObjectIfTransient(interaction, getIntactDao().getComplexDao());
        Collection<Alias> alias = reloaded.getAliases();
        initialiseAliases(alias);

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex initialiseComplexAnnotations(IntactComplex interaction) {
        // reload IntactInteractionEvidence without flushing changes
        IntactComplex reloaded = reattachIntactObjectIfTransient(interaction, getIntactDao().getComplexDao());
        Collection<Annotation> annotations = reloaded.getDbAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex initialiseComplexParameters(IntactComplex participantEvidence) {
        // reload feature without flushing changes
        IntactComplex reloaded = reattachIntactObjectIfTransient(participantEvidence, getIntactDao().getComplexDao());
        Collection<ModelledParameter> parameters = reloaded.getModelledParameters();
        initialiseParameters(parameters);

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex initialiseComplexConfidences(IntactComplex participantEvidence) {
        // reload feature without flushing changes
        IntactComplex reloaded = reattachIntactObjectIfTransient(participantEvidence, getIntactDao().getComplexDao());
        Collection<ModelledConfidence> dets = reloaded.getModelledConfidences();
        for (ModelledConfidence det : dets){
            initialiseConfidence(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex initialiseParticipants(IntactComplex participantEvidence) {
        // reload feature without flushing changes
        IntactComplex reloaded = reattachIntactObjectIfTransient(participantEvidence, getIntactDao().getComplexDao());
        Collection<ModelledParticipant> dets = reloaded.getParticipants();
        for (ModelledParticipant det : dets){
            initialiseParticipant(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex initialiseLifeCycleEvents(IntactComplex publication) {
        // reload participant without flushing changes
        IntactComplex reloaded = reattachIntactObjectIfTransient(publication, getIntactDao().getComplexDao());
        Collection<LifeCycleEvent> evidences = reloaded.getLifecycleEvents();
        initialiseEvents(evidences);

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex loadComplexByAc(String ac) {
        IntactComplex interaction = getIntactDao().getEntityManager().find(IntactComplex.class, ac);

        if (interaction != null){
            // iniTransactionSynchtialise annotations because needs caution
            initialiseAnnotations(interaction.getDbAnnotations());
            // initialise aliases
            initialiseAliases(interaction.getAliases());
            // initialise lifecycle events
            initialiseEvents(interaction.getLifecycleEvents());
            // initialise participants
            Collection<ModelledParticipant> dets = interaction.getParticipants();
            for (ModelledParticipant det : dets){
                initialiseParticipant(det);
            }

            // load base types
            if (interaction.getInteractionType() != null) {
                initialiseCv(interaction.getInteractionType());
            }
            if (interaction.getInteractorType() != null) {
                initialiseCv(interaction.getInteractorType());
            }
            if (interaction.getEvidenceType() != null) {
                initialiseCv(interaction.getEvidenceType());
            }

            initialiseCv(interaction.getCvStatus());
        }

        return interaction;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactComplex reloadFullyInitialisedComplex(IntactComplex interactor) {
        IntactComplex reloaded = reattachIntactObjectIfTransient(interactor, getIntactDao().getComplexDao());

        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getDbAnnotations());
        // initialise aliases
        initialiseAliases(reloaded.getAliases());
        // initialise lifecycle events
        initialiseEvents(reloaded.getLifecycleEvents());
        // initialise participants
        Collection<ModelledParticipant> dets = reloaded.getParticipants();
        for (ModelledParticipant det : dets){
            initialiseParticipant(det);
        }

        // load base types
        if (reloaded.getInteractionType() != null) {
            CvTerm cv = initialiseCv(reloaded.getInteractionType());
            if (cv != reloaded.getInteractionType()){
                reloaded.setInteractionType(cv);
            }
        }
        if (reloaded.getInteractorType() != null) {
            CvTerm cv = initialiseCv(reloaded.getInteractorType());
            if (cv != reloaded.getInteractorType()){
                reloaded.setInteractorType(cv);
            }
        }
        if (reloaded.getEvidenceType() != null) {
            CvTerm cv = initialiseCv(reloaded.getEvidenceType());
            if (cv != reloaded.getEvidenceType()){
                reloaded.setEvidenceType(cv);
            }
        }

        // initialise status
        CvTerm cv = initialiseCv(reloaded.getCvStatus());
        if (cv != reloaded.getCvStatus()){
            reloaded.setCvStatus(cv);
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
    public IntactComplex cloneInteractionEvidence(IntactInteractionEvidence ao, ComplexCloner cloner) throws SynchronizerException,
            FinderException,PersisterException {


        IntactInteractionEvidence reloaded = reattachIntactObjectIfTransient(ao, getIntactDao().getInteractionDao());
        IntactComplex clone = null;
        try {
            clone = cloner.cloneFromEvidence(reloaded, getIntactDao());
            getIntactDao().getEntityManager().detach(reloaded);
            return clone;
        } catch (SynchronizerException e) {
            // clear cache
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().detach(reloaded);
            throw e;
        } catch (FinderException e) {
            // clear cache
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().detach(reloaded);
            throw e;
        } catch (PersisterException e) {
            // clear cache
            getIntactDao().getSynchronizerContext().clearCache();
            getIntactDao().getEntityManager().detach(reloaded);
            throw e;
        }
    }

    private void initialiseParticipant(ModelledParticipant det) {
        IntactInteractor interactor = (IntactInteractor)det.getInteractor();
        if (interactor.getAc() != null && !getIntactDao().getEntityManager().contains(interactor)){
            interactor = getIntactDao().getEntityManager().merge(interactor);
            det.setInteractor(interactor);
        }
        initialiseXrefs(interactor.getDbXrefs());
        initialiseAnnotations(interactor.getDbAnnotations());
        if (interactor instanceof Polymer){
            ((Polymer)interactor).getSequence();
        }

        CvTerm bioRole = initialiseCv(det.getBiologicalRole());
        if (bioRole != det.getBiologicalRole()){
           det.setBiologicalRole(bioRole);
        }
        for (ModelledFeature f : det.getFeatures()){
           initialiseFeature(f);
        }

        getIntactDao().getEntityManager().detach(interactor);
    }

    private void initialiseFeature(Feature det) {
        for (Object obj : det.getRanges()){
            Range range = (Range)obj;
            CvTerm startCv = initialiseCv(range.getStart().getStatus());
            if (startCv != range.getStart().getStatus()){
                ((IntactPosition)range.getStart()).setStatus(startCv);
            }
            CvTerm endCv = initialiseCv(range.getEnd().getStatus());
            if (endCv != range.getEnd().getStatus()){
                ((IntactPosition)range.getEnd()).setStatus(startCv);
            }
        }

        for (Object linked : det.getLinkedFeatures()){
            ((Feature)linked).getLinkedFeatures();
        }
    }

    private void initialiseEvents(Collection<LifeCycleEvent> evidences) {
        for (LifeCycleEvent evt : evidences){
            CvTerm cvEvent = initialiseCv(((ComplexLifeCycleEvent)evt).getCvEvent());
            if (cvEvent != ((ComplexLifeCycleEvent)evt).getCvEvent()){
                ((ComplexLifeCycleEvent)evt).setCvEvent(cvEvent);
            }
        }
    }
}
