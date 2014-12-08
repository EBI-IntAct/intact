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

import org.hibernate.Hibernate;
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
        T reloaded = getIntactDao().getEntityManager().merge(participant);
        Collection<Annotation> annotations = (Collection<Annotation>)reloaded.getAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T initialiseParticipantXrefs(T participant) {
        // reload feature without flushing changes
        T reloaded = getIntactDao().getEntityManager().merge(participant);
        Collection<Xref> xrefs = (Collection<Xref>)reloaded.getXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T initialiseParticipantAliases(T participant) {
        // reload feature without flushing changes
        T reloaded = getIntactDao().getEntityManager().merge(participant);
        Collection<Alias> aliases = (Collection<Alias>)reloaded.getAliases();
        initialiseAliases(aliases);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactParticipantEvidence initialiseParticipantParameters(IntactParticipantEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactParticipantEvidence reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
        Collection<Parameter> parameters = reloaded.getParameters();
        initialiseParameters(parameters);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactParticipantEvidence initialiseParticipantIdentificationMethods(IntactParticipantEvidence participantEvidence) {
        // reload feature without flushing changes
        IntactParticipantEvidence reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
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
        IntactParticipantEvidence reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
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
        IntactParticipantEvidence reloaded = getIntactDao().getEntityManager().merge(participantEvidence);
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

        return participant;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T reloadFullyInitialisedParticipant(T participant) {
        T reloaded = getIntactDao().getEntityManager().merge(participant);

        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getAnnotations());

        // load base types
        if (reloaded.getBiologicalRole() != null){
            initialiseCv(reloaded.getBiologicalRole());
        }
        if (reloaded instanceof IntactParticipantEvidence){
            initialiseCv(((IntactParticipantEvidence) reloaded).getExperimentalRole());
        }

        // load participant interactor
        initialiseInteractor((IntactInteractor)reloaded.getInteractor());


        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactParticipant> T initialiseFeatures(T participant) {
        T reloaded = getIntactDao().getEntityManager().merge(participant);

        Collection dets = reloaded.getFeatures();
        for (Object det : dets){
            initialiseFeature((Feature) det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
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

    private void initialiseCausalRelationship(CausalRelationship det) {
        initialiseCv(det.getRelationType());
        initialiseInteractor((IntactInteractor)det.getTarget().getInteractor());
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

    private void initialiseInteractor(IntactInteractor participant) {
        if (participant instanceof IntactPolymer){
            // load sequence
            ((Polymer) participant).getSequence();
        }

        initialiseXrefs(participant.getDbXrefs());
        initialiseAnnotations(participant.getDbAnnotations());
    }
}
