package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.InteractorFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import psidev.psi.mi.jami.utils.comparator.interactor.UnambiguousExactInteractorBaseComparator;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.InteractorBaseMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;
import uk.ac.ebi.intact.jami.utils.comparator.IntactComparator;
import uk.ac.ebi.intact.jami.utils.comparator.UnambiguousIntactInteractorBaseComparator;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Synchronizer for interactors
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>28/01/14</pre>
 */

public class InteractorSynchronizerTemplate<T extends Interactor, I extends IntactInteractor> extends AbstractIntactDbSynchronizer<T, I>
implements InteractorFetcher<T>, InteractorSynchronizer<T, I>{
    private Map<T, I> persistedObjects;
    private Map<T, I> convertedObjects;

    private IntactComparator interactorComparator;

    private static final Log log = LogFactory.getLog(InteractorSynchronizerTemplate.class);

    public InteractorSynchronizerTemplate(SynchronizerContext context, Class<I> intactClass){
        super(context, intactClass);
        // to keep track of persisted cvs
        initialisePersistedObjectMap();
    }

    public I find(T term) throws FinderException {
        Query query;
        if (term == null){
            return null;
        }
        else if (this.persistedObjects.containsKey(term)){
            return this.persistedObjects.get(term);
        }
        else{
            IntactCvTerm existingType = getContext().getInteractorTypeSynchronizer().find(term.getInteractorType());
            // could not retrieve the interactor type so this interactor does not exist in IntAct
            if (existingType == null || existingType.getAc() == null){
                return null;
            }
            IntactOrganism existingOrganism = null;
            if (term.getOrganism() != null){
                existingOrganism = getContext().getOrganismSynchronizer().find(term.getOrganism());
                // could not retrieve the organism so this interactor does not exist in IntAct
                if (existingOrganism == null || existingOrganism.getAc() == null){
                    return null;
                }
            }

            // try to fetch interactor using identifiers
            Collection<I> results = findByIdentifier(term, existingOrganism, existingType);
            if (results.isEmpty()){
                // fetch using other properties
                results = findByOtherProperties(term, existingType, existingOrganism);
                if (results.isEmpty()){
                    // fetch using shortname
                    query = findByName(term, existingType, existingOrganism);
                    results = query.getResultList();
                }
            }

            I retrievedInstance = postFilter(term, results);
            if (retrievedInstance != null){
                return retrievedInstance;
            }
            else if (results.size() > 1){
                throw new FinderException("The interactor "+term + " can match "+results.size()+" interactors in the database and we cannot determine which one is valid.");
            }
            return retrievedInstance;
        }
    }

    // nothing to do here
    protected I postFilter(T term, Collection<I> results) {
        if (results.size() == 1){
            return results.iterator().next();
        }
        return null;
    }

    // nothing to do here
    protected Collection<I> findByOtherProperties(T term, IntactCvTerm existingType, IntactOrganism existingOrganism) {
        return Collections.EMPTY_LIST;
    }

    protected Query findByName(T term, IntactCvTerm existingType, IntactOrganism existingOrganism) {
        Query query;
        if (existingOrganism == null){
            query = getEntityManager().createQuery("select i from "+getIntactClass().getSimpleName()+" i " +
                    "join i.interactorType as t " +
                    "where i.shortName = :name " +
                    "and i.organism is null " +
                    "and t.ac = :typeAc");
            query.setParameter("name", term.getShortName().trim().toLowerCase());
            query.setParameter("typeAc", existingType.getAc());
        }
        else{
            query = getEntityManager().createQuery("select i from "+getIntactClass().getSimpleName()+" i " +
                    "join i.interactorType as t " +
                    "join i.organism as o " +
                    "where i.shortName = :name " +
                    "and o.ac = :orgAc " +
                    "and t.ac = :typeAc");
            query.setParameter("name", term.getShortName().trim().toLowerCase());
            query.setParameter("orgAc", existingOrganism.getAc());
            query.setParameter("typeAc", existingType.getAc());
        }
        return query;
    }

    public void synchronizeProperties(I intactInteractor) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactInteractor);
        // then check full name
        prepareFullName(intactInteractor);
        // then check organism
        synchronizeOrganism(intactInteractor, true);
        // then check interactor type
        synchronizeInteractorType(intactInteractor, true);
        // then check aliases
        prepareAliases(intactInteractor, true);
        // then check annotations
        prepareAnnotations(intactInteractor, true);
        // then check xrefs
        prepareXrefs(intactInteractor, true);
    }

    protected void synchronizeInteractorType(I intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        intactInteractor.setInteractorType(enableSynchronization ?
                getContext().getInteractorTypeSynchronizer().synchronize(intactInteractor.getInteractorType(), true) :
                getContext().getInteractorTypeSynchronizer().convertToPersistentObject(intactInteractor.getInteractorType()));
    }

    protected void synchronizeOrganism(I intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.getOrganism() != null){
            intactInteractor.setOrganism(enableSynchronization ?
                    getContext().getOrganismSynchronizer().synchronize(intactInteractor.getOrganism(), true) :
                    getContext().getOrganismSynchronizer().convertToPersistentObject(intactInteractor.getOrganism()));
        }
    }

    public void clearCache() {
        this.persistedObjects.clear();
        this.convertedObjects.clear();
    }

    protected void initialisePersistedObjectMap() {
        this.interactorComparator = new UnambiguousIntactInteractorBaseComparator();
        this.persistedObjects = new TreeMap<T, I>(this.interactorComparator);
        this.convertedObjects = new IdentityMap();
    }

    protected void initialisePersistedObjectMap(IntactComparator comparator) {
        this.interactorComparator = comparator;
        this.persistedObjects = new TreeMap<T, I>(this.interactorComparator);
        this.convertedObjects = new IdentityMap();
    }

    protected void setPersistedObjects(Map<T, I> persistedObjects) {
        this.persistedObjects = persistedObjects;
    }

    protected void setConvertedObjects(Map<T, I> persistedObjects) {
        this.convertedObjects = persistedObjects;
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getAc();
    }

    @Override
    protected I instantiateNewPersistentInstance(T object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        I newInteractor = intactClass.getConstructor(String.class).newInstance(object.getShortName());
        InteractorCloner.copyAndOverrideBasicInteractorProperties(object, newInteractor);
        return newInteractor;
    }

    @Override
    protected void storeInCache(T originalObject, I persistentObject, I existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected I fetchObjectFromCache(T object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(T object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected boolean containsObjectInstance(T object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(T object) {
        this.convertedObjects.remove(object);
    }

    @Override
    protected I fetchMatchingObjectFromIdentityCache(T object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(I object) throws SynchronizerException, PersisterException, FinderException {
        // then check organism
        synchronizeOrganism(object, false);
        // then check interactor type
        synchronizeInteractorType(object, false);
        // then check aliases
        prepareAliases(object, false);
        // then check annotations
        prepareAnnotations(object, false);
        // then check xrefs
        prepareXrefs(object, false);
    }

    @Override
    protected void storeObjectInIdentityCache(T originalObject, I persistableObject) {
        this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectDirty(T originalObject) {
        return !this.interactorComparator.canCompare(originalObject);
    }

    protected void prepareXrefs(I intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactInteractor.getDbXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref cvXref = enableSynchronization ?
                        getContext().getInteractorXrefSynchronizer().synchronize(xref, false) :
                        getContext().getInteractorXrefSynchronizer().convertToPersistentObject(xref);
                // we have a different instance because needed to be synchronized
                if (cvXref != xref){
                    intactInteractor.getDbXrefs().remove(xref);
                    intactInteractor.getDbXrefs().add(cvXref);
                }
            }
        }
    }

    protected void prepareAnnotations(I intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactInteractor.getDbAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation cvAnnotation = enableSynchronization ?
                        getContext().getInteractorAnnotationSynchronizer().synchronize(annotation, false) :
                        getContext().getInteractorAnnotationSynchronizer().convertToPersistentObject(annotation);
                // we have a different instance because needed to be synchronized
                if (cvAnnotation != annotation){
                    intactInteractor.getDbAnnotations().remove(annotation);
                    intactInteractor.getDbAnnotations().add(cvAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(I intactInteractor, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactInteractor.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactInteractor.getDbAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias cvAlias = enableSynchronization ?
                        getContext().getInteractorAliasSynchronizer().synchronize(alias, false) :
                        getContext().getInteractorAliasSynchronizer().convertToPersistentObject(alias);
                // we have a different instance because needed to be synchronized
                if (cvAlias != alias){
                    intactInteractor.getDbAliases().remove(alias);
                    intactInteractor.getDbAliases().add(cvAlias);
                }
            }
        }
    }

    protected void prepareFullName(I intactInteractor) {
        // truncate if necessary
        if (intactInteractor.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactInteractor.getFullName().length()){
            intactInteractor.setFullName(intactInteractor.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeShortLabel(I intactInteractor) {
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactInteractor.getShortName().length()){
            log.warn("Interactor shortLabel too long: "+intactInteractor.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactInteractor.setShortName(intactInteractor.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        String name;
        List<String> existingInteractors;
        do{
            name = intactInteractor.getShortName().trim().toLowerCase();

            // check if short name already exist, if yes, synchronize with existing label
            Query query = getEntityManager().createQuery("select i.shortName from IntactInteractor i " +
                    "where (i.shortName = :name or i.shortName like :nameWithSuffix) "
                    + (intactInteractor.getAc() != null ? "and i.ac <> :interactorAc" : ""));
            query.setParameter("name", name);
            query.setParameter("nameWithSuffix", name+"-%");
            if (intactInteractor.getAc() != null){
                query.setParameter("interactorAc", intactInteractor.getAc());
            }
            existingInteractors = query.getResultList();
            if (!existingInteractors.isEmpty()){
                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingInteractors, IntactUtils.MAX_SHORT_LABEL_LEN, false);
                if (!nameInSync.equals(name)){
                    intactInteractor.setShortName(nameInSync);
                }
                else{
                    break;
                }
            }
            else{
                intactInteractor.setShortName(name);
            }
        }
        while(!existingInteractors.isEmpty());
    }

    protected Collection<I> findByIdentifier(T term, IntactOrganism existingOrganism, IntactCvTerm existingType) throws FinderException {
        if (term.getIdentifiers().isEmpty()){
             return Collections.EMPTY_LIST;
        }
        Query query=null;
        Collection<I> totalInteractors = new ArrayList<I>();
        // no organism for this interactor.
        if (existingOrganism == null){
            for (Xref ref : term.getIdentifiers()){
                query = getEntityManager().createQuery("select i from "+getIntactClass().getSimpleName()+" i " +
                        "join i.interactorType as t " +
                        "where i.ac = :id " +
                        "and i.organism is null " +
                        "and t.ac = :typeAc");
                query.setParameter("id", ref.getId());
                query.setParameter("typeAc", existingType.getAc());
                Collection<I> interactors = query.getResultList();
                if (!interactors.isEmpty()){
                    return interactors;
                }
                else{
                    query = getEntityManager().createQuery("select distinct i from "+getIntactClass().getSimpleName()+" i " +
                            "join i.dbXrefs as x " +
                            "join x.database as d " +
                            "join x.qualifier as q " +
                            "join i.interactorType as t " +
                            "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                            "and d.shortName = :db " +
                            "and x.id = :id " +
                            "and t.ac = :typeAc " +
                            "and i.organism is null");
                    query.setParameter("identity", Xref.IDENTITY);
                    query.setParameter("secondaryAc", Xref.SECONDARY);
                    query.setParameter("db", ref.getDatabase().getShortName());
                    query.setParameter("id", ref.getId());
                    query.setParameter("typeAc", existingType.getAc());

                    interactors = query.getResultList();
                    if (interactors.size() == 1){
                        return interactors;
                    }
                    else if (interactors.size() > 1){
                        totalInteractors.addAll(interactors);
                    }
                }
            }
        }
        // organism for this interactor
        else{
            for (Xref ref : term.getIdentifiers()){
                query = getEntityManager().createQuery("select i from "+getIntactClass().getSimpleName()+" i " +
                        "join i.organism as o " +
                        "join i.interactorType as t " +
                        "where i.ac = :id " +
                        "and t.ac = :typeAc " +
                        "and o.ac = :orgAc");
                query.setParameter("id", ref.getId());
                query.setParameter("typeAc", existingType.getAc());
                query.setParameter("orgAc", existingOrganism.getAc());
                Collection<I> interactors = query.getResultList();
                if (!interactors.isEmpty()){
                    return interactors;
                }
                else{
                    query = getEntityManager().createQuery("select distinct i from "+getIntactClass().getSimpleName()+" i " +
                            "join i.dbXrefs as x " +
                            "join x.database as d " +
                            "join x.qualifier as q " +
                            "join i.organism as o " +
                            "join i.interactorType as t " +
                            "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                            "and d.shortName = :db " +
                            "and x.id = :id " +
                            "and t.ac = :typeAc " +
                            "and o.ac = :orgAc");
                    query.setParameter("identity", Xref.IDENTITY);
                    query.setParameter("secondaryAc", Xref.SECONDARY);
                    query.setParameter("db", ref.getDatabase().getShortName());
                    query.setParameter("id", ref.getId());
                    query.setParameter("typeAc", existingType.getAc());
                    query.setParameter("orgAc", existingOrganism.getAc());

                    interactors = query.getResultList();
                    if (interactors.size() == 1){
                        return interactors;
                    }
                    else if (interactors.size() > 1){
                        totalInteractors.addAll(interactors);
                    }
                }
            }
        }

        return totalInteractors;
    }

    public Collection<T> fetchByIdentifier(String identifier) throws BridgeFailedException {
        if (identifier == null){
            throw new IllegalArgumentException("The identifier cannot be null.");
        }
        Query query = getEntityManager().createQuery("select i from "+getIntactClass().getSimpleName()+" i " +
                "where i.ac = :id");
        query.setParameter("id", identifier);
        Collection<T> interactors = query.getResultList();
        if (!interactors.isEmpty()){
            return interactors;
        }
        else{
            query = getEntityManager().createQuery("select distinct i from "+getIntactClass().getSimpleName()+" i " +
                    "join i.dbXrefs as x " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc)" +
                    "and x.id = :id");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("id", identifier);
            return query.getResultList();
        }
    }

    public Collection<T> fetchByIdentifiers(Collection<String> identifiers) throws BridgeFailedException {
        if (identifiers == null){
            throw new IllegalArgumentException("The identifiers cannot be null.");
        }

        Collection<T> results = new ArrayList<T>(identifiers.size());
        for (String id : identifiers){
            Collection<T> element = fetchByIdentifier(id);
            if (element != null){
                results.addAll(element);
            }
        }
        return results;
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new InteractorBaseMergerEnrichOnly<T,I>(this));
    }
}
