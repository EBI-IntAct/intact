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
package uk.ac.ebi.intact.editor.services.curate.publication;

import org.hibernate.Hibernate;
import org.primefaces.model.LazyDataModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.clone.PublicationCloner;
import uk.ac.ebi.intact.editor.controller.curate.publication.PublicationController;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.editor.util.LazyDataModelFactory;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactExperiment;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.PublicationLifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;

import javax.persistence.Query;
import java.util.Collection;

/**
 */
@Service
public class PublicationEditorService extends AbstractEditorService {

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAnnotations(IntactPublication publication) {
        return getIntactDao().getPublicationDao().countAnnotationsForPublication(publication.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countXrefs(IntactPublication publication) {
        return getIntactDao().getPublicationDao().countXrefsForPublication(publication.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countInteractions(IntactPublication publication) {
        return getIntactDao().getPublicationDao().countInteractionsForPublication(publication.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countExperiments(IntactPublication publication) {
        return getIntactDao().getPublicationDao().countExperimentsForPublication(publication.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPublication initialisePublicationAnnotations(IntactPublication publication) {
        // reload publication without flushing changes
        IntactPublication reloaded = getIntactDao().getEntityManager().merge(publication);
        Collection<Annotation> annotations = reloaded.getDbAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPublication initialisePublicationXrefs(IntactPublication publication) {
        // reload publication without flushing changes
        IntactPublication reloaded = getIntactDao().getEntityManager().merge(publication);
        Collection<Xref> xrefs = reloaded.getDbXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPublication initialiseExperiments(IntactPublication publication) {
        // reload participant without flushing changes
        IntactPublication reloaded = getIntactDao().getEntityManager().merge(publication);
        Collection<Experiment> evidences = reloaded.getExperiments();
        initialiseEvidences(evidences);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPublication initialiseLifeCycleEvents(IntactPublication publication) {
        // reload participant without flushing changes
        IntactPublication reloaded = getIntactDao().getEntityManager().merge(publication);
        Collection<LifeCycleEvent> evidences = reloaded.getLifecycleEvents();
        initialiseEvents(evidences);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPublication loadPublicationByAc(String ac) {
        IntactPublication publication = getIntactDao().getEntityManager().find(IntactPublication.class, ac);

        if (publication != null){
            if (publication.getPubmedId() != null && (
                    "14681455".equals(publication.getPubmedId()) ||
                            "unassigned638".equals(publication.getPubmedId()) ||
                            "24288376".equals(publication.getPubmedId()) ||
                            "24214965".equals(publication.getPubmedId())
            )){
                return null;
            }
        }

        // initialise annotations because needs caution
        initialiseAnnotations(publication.getDbAnnotations());
        // initialise xrefs because needs pubmed
        initialiseXrefs(publication.getDbXrefs());
        // initialise status
        initialiseCv(publication.getCvStatus());
        // initialise experiments
        initialiseEvidences(publication.getExperiments());
        // initialise lifecycle events
        initialiseLifeCycleEvents(publication);

        return publication;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPublication reloadFullyInitialisedPublication(IntactPublication exp) {
        IntactPublication reloaded = getIntactDao().getEntityManager().merge(exp);

        if (reloaded != null){
            if (reloaded.getPubmedId() != null && (
                    "14681455".equals(reloaded.getPubmedId()) ||
                            "unassigned638".equals(reloaded.getPubmedId()) ||
                            "24288376".equals(reloaded.getPubmedId()) ||
                            "24214965".equals(reloaded.getPubmedId())
            )){
                return null;
            }
        }

        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getDbAnnotations());
        // initialise xrefs because needs pubmed
        initialiseXrefs(reloaded.getDbXrefs());
        // initialise status
        initialiseCv(reloaded.getCvStatus());
        // initialise experiments
        initialiseEvidences(reloaded.getExperiments());
        // initialise lifecycle events
        initialiseLifeCycleEvents(reloaded);
        getIntactDao().getEntityManager().detach(reloaded);

        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPublication loadPublicationByAcOrPubmedId(String id) {

        IntactPublication pub = getIntactDao().getEntityManager().find(IntactPublication.class, id);
        if (pub == null){
            pub = getIntactDao().getPublicationDao().getByPubmedId(id);
        }

        if (pub != null){
            if (pub.getPubmedId() != null && (
                    "14681455".equals(pub.getPubmedId()) ||
                            "unassigned638".equals(pub.getPubmedId()) ||
                            "24288376".equals(pub.getPubmedId()) ||
                            "24214965".equals(pub.getPubmedId())
            )){
                return null;
            }

            // initialise annotations because needs caution
            initialiseAnnotations(pub.getDbAnnotations());
            // initialise xrefs because needs pubmed
            initialiseXrefs(pub.getDbXrefs());
            // initialise status
            initialiseCv(pub.getCvStatus());
            // initialise experiments
            initialiseEvidences(pub.getExperiments());
            // initialise lifecycle events
            initialiseLifeCycleEvents(pub);
        }

        return pub;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public LazyDataModel<IntactInteractionEvidence> refreshDataModels(IntactPublication publication) {
        return LazyDataModelFactory.createLazyDataModel(getIntactDao().getEntityManager(),
                "select i from IntactInteractionEvidence i join fetch i.dbExperiments as exp " +
                        "where exp.publication.ac = '" + publication.getAc() + "' order by exp.shortLabel asc",
                "select count(i) from IntactInteractionEvidence i join i.dbExperiments as exp " +
                        "where exp.publication.ac = '" + publication.getAc() + "'"
        );
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public boolean doesDatasetAlreadyExist(String datasetName) {
        String sql = "select distinct a.value from PublicationAnnotation a where a.topic.shortName = :dataset and lower(a.value) like :name";
        Query query = getIntactDao().getEntityManager().createQuery(sql);
        query.setParameter("dataset", PublicationController.DATASET);
        query.setParameter("name", datasetName.toLowerCase() + " -%");

        if (!query.getResultList().isEmpty()) {
            return true;
        }

        return false;
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

    private void initialiseEvidences(Collection<Experiment> evidences) {

        for (Experiment exp : evidences){
            initialiseAnnotations(exp.getAnnotations());
            initialiseXrefs(exp.getXrefs());
        }
    }

    private void initialiseCv(CvTerm term) {
        initialiseAnnotations(((IntactCvTerm)term).getDbAnnotations());
        initialiseXrefs(((IntactCvTerm)term).getDbXrefs());
    }

    private void initialiseEvents(Collection<LifeCycleEvent> evidences) {
        for (LifeCycleEvent evt : evidences){
            initialiseCv(((PublicationLifeCycleEvent)evt).getCvEvent());
        }
    }
}
