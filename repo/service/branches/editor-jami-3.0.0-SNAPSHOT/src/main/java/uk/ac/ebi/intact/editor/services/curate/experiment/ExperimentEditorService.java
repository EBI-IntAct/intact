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
package uk.ac.ebi.intact.editor.services.curate.experiment;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.CvTermUtils;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;

import java.util.Collection;

/**
 */
@Service
public class ExperimentEditorService extends AbstractEditorService {

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAnnotations(IntactExperiment experiment) {
        return getIntactDao().getExperimentDao().countAnnotationsForExperiment(experiment.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countXrefs(IntactExperiment experiment) {
        return getIntactDao().getExperimentDao().countXrefsForExperiment(experiment.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countInteractions(IntactExperiment experiment) {
        return getIntactDao().getExperimentDao().countInteractionsForExperiment(experiment.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countVariableParameters(IntactExperiment experiment) {
        return getIntactDao().getExperimentDao().countVariableParametersForExperiment(experiment.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactExperiment initialiseExperimentAnnotations(IntactExperiment experiment) {
        // reload participant without flushing changes
        IntactExperiment reloaded = getIntactDao().getEntityManager().merge(experiment);
        Collection<psidev.psi.mi.jami.model.Annotation> annotations = reloaded.getAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactExperiment initialiseExperimentVariableParameters(IntactExperiment experiment) {
        // reload participant without flushing changes
        IntactExperiment reloaded = getIntactDao().getEntityManager().merge(experiment);
        Collection<VariableParameter> parameters = reloaded.getVariableParameters();
        initialiseVariableParameters(parameters);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactExperiment initialiseInteractionEvidences(IntactExperiment experiment) {
        // reload participant without flushing changes
        IntactExperiment reloaded = getIntactDao().getEntityManager().merge(experiment);
        Collection<InteractionEvidence> evidences = reloaded.getInteractionEvidences();
        initialiseEvidences(evidences);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactExperiment initialiseExperimentXrefs(IntactExperiment experiment) {
        // reload participant without flushing changes
        IntactExperiment reloaded = getIntactDao().getEntityManager().merge(experiment);
        Collection<Xref> xrefs = reloaded.getXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactExperiment loadExperimentByAc(String ac) {
        IntactExperiment experiment = getIntactDao().getEntityManager().find(IntactExperiment.class, ac);

        if (experiment != null){
            if (CvTermUtils.isCvTerm(experiment.getInteractionDetectionMethod(), Experiment.INFERRED_BY_CURATOR_MI,
                    Experiment.INFERRED_BY_CURATOR)){
                return null;
            }

            // initialise annotations because needs caution
            initialiseAnnotations(experiment.getAnnotations());

            // initialise publication annotations and xrefs
            if (experiment.getPublication() != null){
                initialiseXrefs(((IntactPublication)experiment.getPublication()).getDbXrefs());
                initialiseAnnotations(((IntactPublication) experiment.getPublication()).getDbAnnotations());
            }

            initialiseCv(experiment.getInteractionDetectionMethod());
            initialiseCv(experiment.getParticipantIdentificationMethod());
        }

        return experiment;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAcceptedExperiments(String pubAc) {
        IntactPublication reloaded = getIntactDao().getEntityManager().find(IntactPublication.class, pubAc);

        Collection<Experiment> experiments = reloaded.getExperiments();
        int expAccepted = 0;

        for (Experiment exp : experiments) {
            if (AnnotationUtils.collectFirstAnnotationWithTopic(exp.getAnnotations(), null, Releasable.ACCEPTED) != null) {
                expAccepted++;
            }
        }

        return expAccepted;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countRejectedExperiments(String pubAc) {
        IntactPublication reloaded = getIntactDao().getEntityManager().find(IntactPublication.class, pubAc);

        Collection<Experiment> experiments = reloaded.getExperiments();
        int rejected = 0;

        for (Experiment exp : experiments) {
            if (AnnotationUtils.collectFirstAnnotationWithTopic(exp.getAnnotations(), null, Releasable.TO_BE_REVIEWED) != null) {
                rejected++;
            }
        }

        return rejected;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactExperiment reloadFullyInitialisedExperiment(IntactExperiment exp) {
        IntactExperiment reloaded = getIntactDao().getEntityManager().merge(exp);

        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getAnnotations());

        // initialise publication annotations and xrefs
        if (reloaded.getPublication() != null){
            initialiseXrefs(((IntactPublication)reloaded.getPublication()).getDbXrefs());
            initialiseAnnotations(((IntactPublication)reloaded.getPublication()).getDbAnnotations());
        }

        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }


    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPublication loadPublicationByAcOrPubmedId(String id) {

        IntactPublication pub = getIntactDao().getEntityManager().find(IntactPublication.class, id);
        if (pub == null){
            pub = getIntactDao().getPublicationDao().getByPubmedId(id);
        }

        // initialise pub
        if (pub != null){
            initialiseXrefs(pub.getDbXrefs());
            initialiseAnnotations(pub.getDbAnnotations());
        }

        return pub;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public boolean isAccepted(IntactExperiment exp) {
        IntactExperiment reloaded = getIntactDao().getEntityManager().merge(exp);

        boolean accepted = AnnotationUtils.collectAllAnnotationsHavingTopic(reloaded.getAnnotations(), null, Releasable.ACCEPTED)!=null;

        getIntactDao().getEntityManager().detach(reloaded);

        return accepted;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public boolean isRejected(IntactExperiment exp) {
        IntactExperiment reloaded = getIntactDao().getEntityManager().merge(exp);

        boolean accepted = AnnotationUtils.collectAllAnnotationsHavingTopic(reloaded.getAnnotations(), null, Releasable.TO_BE_REVIEWED)!=null;

        getIntactDao().getEntityManager().detach(reloaded);

        return accepted;
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

    private void initialiseEvidences(Collection<InteractionEvidence> evidences) {

        for (InteractionEvidence ev : evidences){
            ev.getExperiment();
        }
    }

    private void initialiseCv(CvTerm term) {
        initialiseAnnotations(((IntactCvTerm)term).getDbAnnotations());
        initialiseXrefs(((IntactCvTerm)term).getDbXrefs());
    }

    private void initialiseVariableParameters(Collection<VariableParameter> parameters) {
        for (VariableParameter param : parameters){
            if (param.getUnit() != null){
                Hibernate.initialize(((IntactCvTerm)param.getUnit()).getDbXrefs());
            }

            Hibernate.initialize(param.getVariableValues());
        }
    }
}
