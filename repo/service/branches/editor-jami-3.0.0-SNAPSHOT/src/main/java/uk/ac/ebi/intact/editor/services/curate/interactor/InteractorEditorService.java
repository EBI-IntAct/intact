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
package uk.ac.ebi.intact.editor.services.curate.interactor;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractorPool;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;

import java.util.Collection;

/**
 */
@Service
public class InteractorEditorService extends AbstractEditorService {

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAnnotations(IntactInteractor interactor) {
        return getIntactDao().getInteractorDao(IntactInteractor.class).countAnnotationsForInteractor(interactor.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countXrefs(IntactInteractor interactor) {
        return getIntactDao().getInteractorDao(IntactInteractor.class).countXrefsForInteractor(interactor.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countAliases(IntactInteractor interactor) {
        return getIntactDao().getInteractorDao(IntactInteractor.class).countAliasesForInteractor(interactor.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int countPoolMembers(IntactInteractorPool pool) {
        return getIntactDao().getInteractorPoolDao().countMembersOfPool(pool.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractor initialiseInteractorAnnotations(IntactInteractor interactor) {
        // reload IntactInteractor without flushing changes
        IntactInteractor reloaded = reattachIntactObjectIfTransient(interactor, getIntactDao().getInteractorDao(IntactInteractor.class));
        Collection<Annotation> annotations = reloaded.getDbAnnotations();
        initialiseAnnotations(annotations);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractor initialiseInteractorXrefs(IntactInteractor interactor) {
        // reload IntactInteractor without flushing changes
        IntactInteractor reloaded = reattachIntactObjectIfTransient(interactor, getIntactDao().getInteractorDao(IntactInteractor.class));
        Collection<Xref> xrefs = reloaded.getDbXrefs();
        initialiseXrefs(xrefs);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractor initialiseInteractorAliases(IntactInteractor interactor) {
        // reload interactor without flushing changes
        IntactInteractor reloaded = reattachIntactObjectIfTransient(interactor, getIntactDao().getInteractorDao(IntactInteractor.class));
        Collection<Alias> aliases = reloaded.getDbAliases();
        initialiseAliases(aliases);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractorPool initialisePoolMembers(IntactInteractorPool pool) {
        // reload feature without flushing changes
        IntactInteractorPool reloaded = reattachIntactObjectIfTransient(pool, getIntactDao().getInteractorPoolDao());
        Collection<Interactor> interactors = reloaded;
        initialiseInteractorMembers(interactors);

        getIntactDao().getEntityManager().detach(reloaded);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractor loadInteractorByAc(String ac) {
        IntactInteractor interactor = getIntactDao().getEntityManager().find(IntactInteractor.class, ac);

        if (interactor != null){
            // initialise xrefs because needs id
            initialiseXrefs(interactor.getDbXrefs());
            // initialise annotations because needs caution
            initialiseAnnotations(interactor.getDbAnnotations());

            // load base types
            if (interactor.getInteractorType() != null){
                initialiseCv(interactor.getInteractorType());
            }

            // load set members
            if (interactor instanceof IntactInteractorPool){
                initialiseInteractorMembers((IntactInteractorPool) interactor);
            }
            // load sequence
            if (interactor instanceof IntactPolymer){
                initialiseSequence((IntactPolymer) interactor);
            }
        }

        return interactor;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactInteractor reloadFullyInitialisedInteractor(IntactInteractor interactor) {
        IntactInteractor reloaded = reattachIntactObjectIfTransient(interactor, getIntactDao().getInteractorDao(IntactInteractor.class));

        // initialise xrefs because needs id
        initialiseXrefs(reloaded.getDbXrefs());
        // initialise annotations because needs caution
        initialiseAnnotations(reloaded.getDbAnnotations());

        // load base types
        if (reloaded.getInteractorType() != null){
            initialiseCv(reloaded.getInteractorType());
        }

        // load set members
        if (reloaded instanceof IntactInteractorPool){
            initialiseInteractorMembers((IntactInteractorPool) reloaded);
        }
        // load sequence
        if (reloaded instanceof IntactPolymer){
            initialiseSequence((IntactPolymer) reloaded);
        }

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
}
