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
package uk.ac.ebi.intact.editor.services.curate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.ProteinFetcher;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Protein;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.editor.controller.curate.UnsavedChange;
import uk.ac.ebi.intact.editor.controller.curate.cloner.EditorCloner;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * General Editor service to save objects in the database
 */

@Service
@Scope( BeanDefinition.SCOPE_PROTOTYPE )
public class EditorObjectService extends AbstractEditorService {

    private static final Log log = LogFactory.getLog( EditorObjectService.class );

    @Resource(name = "proteinFetcher")
    private ProteinFetcher proteinFetcher;

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public <T extends IntactPrimaryObject> T doSave( IntactPrimaryObject object,
                                                     IntactDbSynchronizer dbSynchronizer) throws SynchronizerException,
            FinderException, PersisterException {

        if ( object == null ) {
            log.error( "No annotated object to save");
            return null;
        }
        else{
            // attach dao to transaction manager to clear cache
            attachDaoToTransactionManager();

            return (T)synchronizeIntactObject(object, dbSynchronizer, true);
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public <T extends IntactPrimaryObject> T doRevert(IntactPrimaryObject intactObject) {
        if (intactObject.getAc() != null) {
            if (log.isDebugEnabled()) log.debug("Reverting: " + intactObject.getClass()+", Ac="+intactObject.getAc());

            if (intactObject.getAc() != null && getIntactDao().getEntityManager().contains(intactObject)) {
                getIntactDao().getEntityManager().detach(intactObject);
            }

            intactObject = getIntactDao().getEntityManager().find(intactObject.getClass(), intactObject.getAc());
        }

        return (T)intactObject;
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public boolean doDelete(IntactPrimaryObject intactObject, IntactDbSynchronizer dbSynchronizer) {
        if (intactObject.getAc() != null) {
            if (log.isDebugEnabled()) log.debug("Deleting " + intactObject.getClass()+", Ac="+intactObject.getAc());
            // attach dao to transaction manager to clear cache
            attachDaoToTransactionManager();

            return dbSynchronizer.delete(intactObject);
        }

        return false;
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void saveAll(Collection<UnsavedChange> changes, Collection<UnsavedChange> currentChangesForUser) throws SynchronizerException, FinderException, PersisterException {

        for (UnsavedChange unsaved : changes){
            IntactPrimaryObject object = unsaved.getUnsavedObject();

            // checks that the current unsaved change is not obsolete because of a previous change (when saving/deleting, some unsaved change became obsolete and have been removed from the unsaved changes)
            if (currentChangesForUser.contains(unsaved)){
                doSave(object, unsaved.getDbSynchronizer());

            }
        }
    }

    /**
     * Save a master protein and update the cross reference of a protein transcript which will be created later
     * @param intactObject
     */
    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void doSaveMasterProteins(IntactPrimaryObject intactObject) throws BridgeFailedException, SynchronizerException, FinderException, PersisterException {

        if (intactObject instanceof Protein){
            attachDaoToTransactionManager();

            Protein proteinTranscript = (Protein) intactObject;
            Collection<psidev.psi.mi.jami.model.Xref> xrefsToDelete = new ArrayList<psidev.psi.mi.jami.model.Xref>(proteinTranscript.getXrefs().size());

            for (Xref xref : proteinTranscript.getXrefs()) {
                CvTerm qualifier = xref.getQualifier();

                if (qualifier != null){
                    if (XrefUtils.doesXrefHaveQualifier(xref, Xref.CHAIN_PARENT_MI, Xref.CHAIN_PARENT) ||
                            XrefUtils.doesXrefHaveQualifier(xref, Xref.ISOFORM_PARENT_MI, Xref.ISOFORM_PARENT)) {
                        String primaryId = xref.getId().replaceAll("\\?", "");

                        Collection<Protein> proteins = proteinFetcher.fetchByIdentifier(primaryId);

                        if (proteins.size() > 0){
                            xrefsToDelete.add(xref);

                            for (Protein prot : proteins){
                                IntactInteractor intactprotein = synchronizeIntactObject(prot,
                                        getIntactDao().getSynchronizerContext().getProteinSynchronizer(),
                                        true);
                                ((Protein) intactObject).getXrefs().add(new InteractorXref(xref.getDatabase(), intactprotein.getAc(), xref.getQualifier()));
                            }
                        }
                    }
                }
            }

            proteinTranscript.getXrefs().removeAll(xrefsToDelete);

            updateIntactObject((IntactProtein)intactObject, getIntactDao().getProteinDao());
        }
    }

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED)
    public void revertAll(Collection<UnsavedChange> jamiChanges) {

        for (UnsavedChange unsaved : jamiChanges){
            doRevert(unsaved.getUnsavedObject());
        }
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public Collection<String> findObjectDuplicates(IntactPrimaryObject jamiObject, IntactDbSynchronizer dbSynchronizer){
        // if the annotated object does not have an ac, check if another one similar exists in the db
        if (jamiObject.getAc() == null) {
            return dbSynchronizer.findAllMatchingAcs(jamiObject);
        }
        return Collections.EMPTY_LIST;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends IntactPrimaryObject> T loadByAc(String ac) {

        ac = ac.trim();

        IntactPrimaryObject primary = getIntactDao().getEntityManager().find(IntactPublication.class, ac);
        if (primary == null){
            primary = getIntactDao().getEntityManager().find(IntactExperiment.class, ac);
            if (primary == null){
                primary = getIntactDao().getEntityManager().find(IntactInteractionEvidence.class, ac);

                if (primary == null){
                    primary = getIntactDao().getEntityManager().find(IntactComplex.class, ac);

                    if (primary == null){
                        primary = getIntactDao().getEntityManager().find(IntactParticipantEvidence.class, ac);

                        if (primary == null){
                            primary = getIntactDao().getEntityManager().find(IntactModelledParticipant.class, ac);

                            if (primary == null){
                                primary = getIntactDao().getEntityManager().find(IntactFeatureEvidence.class, ac);

                                if (primary == null){
                                    primary = getIntactDao().getEntityManager().find(IntactModelledFeature.class, ac);

                                    if (primary == null){
                                        primary = getIntactDao().getEntityManager().find(IntactInteractor.class, ac);

                                        if (primary == null){
                                            primary = getIntactDao().getEntityManager().find(IntactOrganism.class, ac);

                                            if (primary == null){
                                                primary = getIntactDao().getEntityManager().find(IntactCvTerm.class, ac);

                                                if (primary == null){
                                                    primary = getIntactDao().getEntityManager().find(IntactSource.class, ac);

                                                    return (T)primary;
                                                }
                                                else{
                                                    return (T)primary;
                                                }
                                            }
                                            else{
                                                return (T)primary;
                                            }
                                        }
                                        else{
                                            return (T)primary;
                                        }
                                    }
                                    else{
                                        return (T)primary;
                                    }
                                }
                                else{
                                    return (T)primary;
                                }
                            }
                            else{
                                return (T)primary;
                            }
                        }
                        else{
                            return (T)primary;
                        }
                    }
                    else{
                        return (T)primary;
                    }
                }
                else{
                    return (T)primary;
                }
            }
            else{
                return (T)primary;
            }
        }
        else{
            return (T)primary;
        }
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends IntactPrimaryObject> T loadByAc(String ac, Class<T> clazz) {

        ac = ac.trim();

        return getIntactDao().getEntityManager().find(clazz, ac);
    }

    public void setUser(User user){
        getIntactDao().getUserContext().setUser(user);
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public Releasable initialiseLifecycleEvents(Releasable releasable) {
        // reload complex without flushing changes
        Releasable reloaded = releasable;
        // merge current user because detached
        if (((IntactPrimaryObject)releasable).getAc() != null && !getIntactDao().getEntityManager().contains(releasable)){
            reloaded = getIntactDao().getEntityManager().merge(releasable);
        }

        Collection<LifeCycleEvent> events = reloaded.getLifecycleEvents();
        Hibernate.initialize(events);
        return reloaded;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public IntactPrimaryObject refresh(IntactPrimaryObject object) {
        return getIntactDao().getEntityManager().find(object.getClass(), object.getAc());
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public void synchronizeExperimentShortLabel(IntactExperiment object) {
        IntactUtils.synchronizeExperimentShortLabel(object, getIntactDao().getEntityManager(), Collections.EMPTY_SET);
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public void synchronizeInteractionShortLabel(IntactInteractionEvidence object) {
        IntactUtils.synchronizeInteractionEvidenceShortName(object, getIntactDao().getEntityManager(), Collections.EMPTY_SET);
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends IntactPrimaryObject> T cloneAnnotatedObject(T ao, EditorCloner cloner) {

        T reloaded = ao;
        // merge current user because detached
        if (ao.getAc() != null && !getIntactDao().getEntityManager().contains(ao)){
            reloaded = getIntactDao().getEntityManager().merge(ao);
        }
        // refresh
        else if (ao.getAc() != null){
            getIntactDao().getEntityManager().refresh(reloaded);
        }

        T clone = (T)cloner.clone(reloaded, getIntactDao());
        getIntactDao().getEntityManager().detach(reloaded);

        return clone;
    }

    @Transactional(value = "jamiTransactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public <T extends IntactPrimaryObject> void detachObject(T ao) {
        // detach current object if necessary
        if (ao.getAc() != null && getIntactDao().getEntityManager().contains(ao)){
            getIntactDao().getEntityManager().detach(ao);
        }
    }
}
