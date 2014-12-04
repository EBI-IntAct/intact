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
package uk.ac.ebi.intact.editor.services.curate.feature;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.editor.controller.curate.feature.RangeWrapper;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.editor.services.curate.cvobject.CvObjectService;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 */
@Service
public class FeatureEditorService extends AbstractEditorService {

    @Resource(name = "cvObjectService")
    private CvObjectService cvObjectService;

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAnnotations(AbstractIntactFeature feature) {
        return getIntactDao().getFeatureDao(feature.getClass()).countAnnotationsForFeature(feature.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countXrefs(AbstractIntactFeature feature) {
        return getIntactDao().getFeatureDao(feature.getClass()).countXrefsForFeature(feature.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAliases(AbstractIntactFeature feature) {
        return getIntactDao().getFeatureDao(feature.getClass()).countAliasesForFeature(feature.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countRanges(AbstractIntactFeature feature) {
        return getIntactDao().getFeatureDao(feature.getClass()).countRangesForFeature(feature.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countParameters(IntactFeatureEvidence feature) {
        return getIntactDao().getFeatureEvidenceDao().countParametersForFeature(feature.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countDetectionMethods(IntactFeatureEvidence feature) {
        int first = feature.getFeatureIdentification() != null ? 1 : 0;
        return first + getIntactDao().getFeatureEvidenceDao().countDetectionMethodsForFeature(feature.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactFeature> T initialiseFeatureAnnotations(T feature) {
        // reload feature without flushing changes
        T reloaded = getIntactDao().getEntityManager().merge(feature);
        Collection<psidev.psi.mi.jami.model.Annotation> annotations = (Collection<psidev.psi.mi.jami.model.Annotation>)reloaded.getAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactFeature> T initialiseFeatureXrefs(T feature) {
        // reload feature without flushing changes
        T reloaded = getIntactDao().getEntityManager().merge(feature);
        Collection<Xref> xrefs = (Collection<Xref>)reloaded.getXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactFeature> T initialiseFeatureAliases(T feature) {
        // reload feature without flushing changes
        T reloaded = getIntactDao().getEntityManager().merge(feature);
        Collection<Alias> aliases = (Collection<Alias>)reloaded.getAliases();
        initialiseAliases(aliases);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactFeatureEvidence initialiseFeatureParameters(IntactFeatureEvidence feature) {
        // reload feature without flushing changes
        IntactFeatureEvidence reloaded = getIntactDao().getEntityManager().merge(feature);
        Collection<Parameter> parameters = reloaded.getParameters();
        initialiseParameters(parameters);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactFeatureEvidence initialiseFeatureDetectionMethods(IntactFeatureEvidence feature) {
        // reload feature without flushing changes
        IntactFeatureEvidence reloaded = getIntactDao().getEntityManager().merge(feature);
        Collection<CvTerm> dets = reloaded.getDetectionMethods();
        for (CvTerm det : dets){
            initialiseCv(det);
        }

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactFeature> T loadFeatureByAc(String ac, Class<T> featureClass) {
        T feature = getIntactDao().getEntityManager().find(featureClass, ac);

        // initialise annotations because needs caution
        initialiseAnnotations(feature.getAnnotations());

        // load base types
        if (feature.getType() != null){
            initialiseCv(feature.getType());
        }
        if (feature.getRole() != null){
            initialiseCv(feature.getRole());
        }

        // load participant interactor
        if (feature.getParticipant() != null){
            initialiseParticipant(feature.getParticipant());
        }

        return feature;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends AbstractIntactFeature> T reloadFullyInitialisedFeature(T feature) {
        T reloaded = getIntactDao().getEntityManager().merge(feature);

        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getAnnotations());

        // load base types
        if (feature.getType() != null){
            initialiseCv(reloaded.getType());
        }
        if (feature.getRole() != null){
            initialiseCv(reloaded.getRole());
        }

        // load participant interactor
        if (feature.getParticipant() != null){
            initialiseParticipant(reloaded.getParticipant());
        }

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public List<RangeWrapper> loadRangeWrappers(AbstractIntactFeature feature, String sequence, Class<? extends AbstractIntactResultingSequence> resultingSeqClass) {
        AbstractIntactFeature reloaded = getIntactDao().getEntityManager().merge(feature);

        List<RangeWrapper> rangeWrappers = new ArrayList<RangeWrapper>(feature.getRanges());
        for (Object r : feature.getRanges()){
            AbstractIntactRange range = (AbstractIntactRange)r;

            initialisePosition(range.getStart());
            initialisePosition(range.getEnd());

            rangeWrappers.add(new RangeWrapper(range, sequence, cvObjectService, resultingSeqClass));
        }

        getIntactDao().getEntityManager().detach(reloaded);

        return rangeWrappers;
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

    private void initialiseAnnotations(Collection<psidev.psi.mi.jami.model.Annotation> annotations) {
        for (psidev.psi.mi.jami.model.Annotation annot : annotations){
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

    private void initialisePosition(Position pos) {
        Hibernate.initialize(((IntactCvTerm)pos.getStatus()).getDbXrefs());

    }

    private void initialiseParticipant(Entity participant) {
        if (participant.getInteractor() instanceof IntactPolymer){
            // load sequence
            ((Polymer) participant.getInteractor()).getSequence();
        }
        else if (participant.getInteractor() instanceof IntactComplex){
            IntactComplex complex = (IntactComplex)participant.getInteractor();

            for (ModelledParticipant p : complex.getParticipants()){
                 initialiseParticipant(p);
            }
        }
    }
}
