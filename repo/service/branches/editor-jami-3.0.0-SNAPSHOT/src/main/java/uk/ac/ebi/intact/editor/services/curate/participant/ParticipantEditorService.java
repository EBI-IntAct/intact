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
package uk.ac.ebi.intact.editor.services.curate.participant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.editor.services.curate.cvobject.CvObjectService;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.annotation.Resource;
import java.util.Collection;

/**
 */
@Service
public class ParticipantEditorService extends AbstractEditorService {

    @Resource(name = "cvObjectService")
    private CvObjectService cvObjectService;

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAnnotations(AbstractIntactParticipant participant) {
        return getIntactDao().getParticipantDao(participant.getClass()).countAnnotationsForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countXrefs(AbstractIntactParticipant participant) {
        return getIntactDao().getParticipantDao(participant.getClass()).countXrefsForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAliases(AbstractIntactParticipant participant) {
        return getIntactDao().getParticipantDao(participant.getClass()).countAliasesForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countFeatures(AbstractIntactParticipant participant) {
        return getIntactDao().getParticipantDao(participant.getClass()).countFeaturesForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countParameters(IntactParticipantEvidence participant) {
        return getIntactDao().getParticipantEvidenceDao().countParametersForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countConfidences(IntactParticipantEvidence participant) {
        return getIntactDao().getParticipantEvidenceDao().countConfidencesForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countIdentificationMethods(IntactParticipantEvidence participant) {
        return getIntactDao().getParticipantEvidenceDao().countIdentificationMethodsForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countExperimentalPreparations(IntactParticipantEvidence participant) {
        return getIntactDao().getParticipantEvidenceDao().countExperimentalPreparationsForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countCausalityStatements(AbstractIntactParticipant participant) {
        return getIntactDao().getParticipantDao(participant.getClass()).countCausalRelationshipsForParticipant(participant.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T initialiseParticipantAnnotations(T participant) {
        // reload feature without flushing changes
        T reloaded = (T)reattachIntactObjectIfTransient(participant, (uk.ac.ebi.intact.jami.dao.IntactBaseDao<AbstractIntactParticipant>) getIntactDao().getParticipantDao(participant.getClass()));
        Collection<Annotation> annotations = (Collection<Annotation>)reloaded.getAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T initialiseParticipantXrefs(T participant) {
        // reload feature without flushing changes
        T reloaded = (T)reattachIntactObjectIfTransient(participant, (uk.ac.ebi.intact.jami.dao.IntactBaseDao<AbstractIntactParticipant>) getIntactDao().getParticipantDao(participant.getClass()));
        Collection<Xref> xrefs = (Collection<Xref>)reloaded.getXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T initialiseParticipantAliases(T participant) {
        // reload feature without flushing changes
        T reloaded = (T)reattachIntactObjectIfTransient(participant, (uk.ac.ebi.intact.jami.dao.IntactBaseDao<AbstractIntactParticipant>) getIntactDao().getParticipantDao(participant.getClass()));
        Collection<Alias> aliases = (Collection<Alias>)reloaded.getAliases();
        initialiseAliases(aliases);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactParticipantEvidence initialiseParticipantParameters(IntactParticipantEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactParticipantEvidence reloaded = reattachIntactObjectIfTransient(participantEvidence, getIntactDao().getParticipantEvidenceDao());
        Collection<Parameter> parameters = reloaded.getParameters();
        initialiseParameters(parameters);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactParticipantEvidence initialiseParticipantIdentificationMethods(IntactParticipantEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactParticipantEvidence reloaded = reattachIntactObjectIfTransient(participantEvidence, getIntactDao().getParticipantEvidenceDao());
        Collection<CvTerm> dets = reloaded.getDbIdentificationMethods();
        for (CvTerm det : dets){
            initialiseCv(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactParticipantEvidence initialiseParticipantExperimentalPreparations(IntactParticipantEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactParticipantEvidence reloaded = reattachIntactObjectIfTransient(participantEvidence, getIntactDao().getParticipantEvidenceDao());
        Collection<CvTerm> dets = reloaded.getExperimentalPreparations();
        for (CvTerm det : dets){
            initialiseCv(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactParticipantEvidence initialiseParticipantConfidences(IntactParticipantEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactParticipantEvidence reloaded = reattachIntactObjectIfTransient(participantEvidence, getIntactDao().getParticipantEvidenceDao());
        Collection<Confidence> dets = reloaded.getConfidences();
        for (Confidence det : dets){
            initialiseConfidence(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T initialiseCausalRelationships(T participantEvidence) {
        // reload feature without flushing changes
        T reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
        Collection<CausalRelationship> dets = reloaded.getCausalRelationships();
        for (CausalRelationship det : dets){
            initialiseCausalRelationship(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T loadParticipantByAc(String ac, Class<T> participantClass) {
        T participant = getIntactDao().getEntityManager().find(participantClass, ac);

        if (participant != null){
            // initialise annotations because needs caution
            initialiseAnnotations(participant.getAnnotations());

            // load base types
            if (participant.getBiologicalRole() != null){
                initialiseCv(participant.getBiologicalRole());
            }
            if (participant instanceof IntactParticipantEvidence){
                initialiseCv(((IntactParticipantEvidence) participant).getExperimentalRole());
            }

            // load participant interactor
            initialiseInteractor((IntactInteractor)participant.getInteractor());

            // load features
            initialiseFeatures(participant.getFeatures());
        }

        return participant;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public AbstractIntactParticipant loadAnyParticipantByAc(String ac) {
        AbstractIntactParticipant participant = getIntactDao().getEntityManager().find(IntactParticipantEvidence.class, ac);
        if (participant == null){
            participant = getIntactDao().getEntityManager().find(IntactModelledParticipant.class, ac);
        }

        if (participant != null){
            // initialise annotations because needs caution
            initialiseAnnotations(participant.getAnnotations());

            // load base types
            if (participant.getBiologicalRole() != null){
                initialiseCv(participant.getBiologicalRole());
            }
            if (participant instanceof IntactParticipantEvidence){
                initialiseCv(((IntactParticipantEvidence) participant).getExperimentalRole());
            }

            // load participant interactor
            initialiseInteractor((IntactInteractor)participant.getInteractor());

            // load features
            initialiseFeatures(participant.getFeatures());
        }

        return participant;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T reloadFullyInitialisedParticipant(T participant) {
        T reloaded = (T)reattachIntactObjectIfTransient(participant, (uk.ac.ebi.intact.jami.dao.IntactBaseDao<AbstractIntactParticipant>) getIntactDao().getParticipantDao(participant.getClass()));

        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getAnnotations());

        // load base types
        if (reloaded.getBiologicalRole() != null){
            CvTerm bioRole = initialiseCv(reloaded.getBiologicalRole());
            if (bioRole != reloaded.getBiologicalRole()){
                reloaded.setBiologicalRole(bioRole);
            }
        }
        if (reloaded instanceof IntactParticipantEvidence){
            CvTerm expRole = initialiseCv(((IntactParticipantEvidence) reloaded).getExperimentalRole());
            if (expRole != ((IntactParticipantEvidence) reloaded).getExperimentalRole()){
                ((IntactParticipantEvidence) reloaded).setExperimentalRole(expRole);
            }
        }

        // load participant interactor
        Interactor reloadedInter = initialiseInteractor((IntactInteractor)reloaded.getInteractor());
        if (reloadedInter != reloaded.getInteractor()){
            reloaded.setInteractor(reloadedInter);
        }

        // load features
        initialiseFeatures(reloaded.getFeatures());

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T initialiseFeatures(T participant) {
        T reloaded = getIntactDao().getEntityManager().merge(participant);

        Collection dets = reloaded.getFeatures();
        initialiseFeatures(dets);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    private void initialiseFeatures(Collection dets) {
        for (Object det : dets){
            initialiseFeature((Feature) det);
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

    private void initialiseCausalRelationship(CausalRelationship det) {
        CvTerm rel = initialiseCv(det.getRelationType());
        if (rel != det.getRelationType()){
            ((AbstractIntactCausalRelationship)det).setRelationType(rel);
        }
        IntactInteractor inter = initialiseInteractor((IntactInteractor)det.getTarget().getInteractor());
        if (inter != det.getTarget().getInteractor()){
            det.getTarget().setInteractor(inter);
        }
    }

    private IntactInteractor initialiseInteractor(IntactInteractor participant) {
        if (participant.getAc() != null && !getIntactDao().getEntityManager().contains(participant)){
           participant = getIntactDao().getEntityManager().merge(participant);
        }
        if (participant instanceof IntactPolymer){
            // load sequence
            ((Polymer) participant).getSequence();
        }

        initialiseXrefs(participant.getDbXrefs());
        initialiseAnnotations(participant.getDbAnnotations());

        getIntactDao().getEntityManager().detach(participant);
        return participant;
    }
}
