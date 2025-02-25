package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.fetcher.OrganismFetcher;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Organism;
import psidev.psi.mi.jami.utils.clone.OrganismCloner;
import psidev.psi.mi.jami.utils.comparator.organism.UnambiguousOrganismComparator;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.OrganismMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;
import uk.ac.ebi.intact.jami.utils.comparator.IntactComparator;
import uk.ac.ebi.intact.jami.utils.comparator.IntactOrganismComparator;

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Default finder/synchronizer for organisms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class OrganismSynchronizer extends AbstractIntactDbSynchronizer<Organism, IntactOrganism> implements OrganismFetcher{
    private Map<Organism, IntactOrganism> persistedObjects;
    private Map<Organism, IntactOrganism> convertedObjects;
    private IntactComparator<Organism> organismComparator;

    private static final Log log = LogFactory.getLog(CvTermSynchronizer.class);

    public OrganismSynchronizer(SynchronizerContext context){
        super(context, IntactOrganism.class);
        this.organismComparator = new IntactOrganismComparator();
        // to keep track of persisted cvs
        this.persistedObjects = new TreeMap<Organism, IntactOrganism>(this.organismComparator);
        this.convertedObjects = new IdentityMap();
    }

    public IntactOrganism find(Organism term) throws FinderException {
        Query query;
        if (term == null){
            return null;
        }
        else if (this.persistedObjects.containsKey(term)){
            return this.persistedObjects.get(term);
        }
        // we have a simple organism. Only check its taxid
        else if (term.getCellType() == null && term.getTissue() == null){
            query = getEntityManager().createQuery("select o from IntactOrganism o " +
                    "where o.cellType is null " +
                    "and o.tissue is null " +
                    "and o.dbTaxid = :taxid");
            query.setParameter("taxid", Integer.toString(term.getTaxId()));
        }
        // we have a celltype/tissue to find first
        else {
            if (term.getCellType() != null && term.getTissue() != null){
                IntactCvTerm existingCell = getContext().getCellTypeSynchronizer().find(term.getCellType());
                IntactCvTerm existingTissue = getContext().getTissueSynchronizer().find(term.getTissue());
                // cell line or tissue is not known so the biosource does not exist in IntAct
                if ((existingCell == null || existingCell.getAc() == null) || (existingTissue == null || existingTissue.getAc() == null)){
                    return null;
                }
                else {
                    query = getEntityManager().createQuery("select o from IntactOrganism o " +
                            "join o.cellType as cell " +
                            "join o.tissue as t " +
                            "where cell.ac = :cellAc " +
                            "and t.ac = :tissueAc " +
                            "and o.dbTaxid = :taxid");
                    query.setParameter("cellAc", ((IntactCvTerm)existingCell).getAc());
                    query.setParameter("tissueAc", ((IntactCvTerm)existingTissue).getAc());
                    query.setParameter("taxid", Integer.toString(term.getTaxId()));
                }
            }
            else if (term.getCellType() != null){
                IntactCvTerm existingCell = getContext().getCellTypeSynchronizer().find(term.getCellType());
                // cell line or tissue is not known so the biosource does not exist in IntAct
                if (existingCell == null || existingCell.getAc() == null){
                    return null;
                }
                else {
                    query = getEntityManager().createQuery("select o from IntactOrganism o " +
                            "join o.cellType as cell " +
                            "where cell.ac = :cellAc " +
                            "and o.dbTaxid = :taxid");
                    query.setParameter("cellAc", ((IntactCvTerm)existingCell).getAc());
                    query.setParameter("taxid", Integer.toString(term.getTaxId()));
                }
            }
            else{
                IntactCvTerm existingTissue = getContext().getTissueSynchronizer().find(term.getTissue());
                // cell line or tissue is not known so the biosource does not exist in IntAct
                if (existingTissue == null  || existingTissue.getAc() == null){
                    return null;
                }
                else {
                    query = getEntityManager().createQuery("select o from IntactOrganism o " +
                            "join o.tissue as t " +
                            "where t.ac = :tissueAc " +
                            "and o.dbTaxid = :taxid");
                    query.setParameter("tissueAc", ((IntactCvTerm)existingTissue).getAc());
                    query.setParameter("taxid", Integer.toString(term.getTaxId()));
                }
            }
        }
        Collection<IntactOrganism> organism = query.getResultList();
        if (organism.size() == 1){
            return organism.iterator().next();
        }
        else if (organism.size() > 1){
            throw new FinderException("The organism "+term + " can match "+organism.size()+" organisms in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    public void synchronizeProperties(IntactOrganism intactOrganism) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeCommonName(intactOrganism);
        // then check full name
        prepareFullName(intactOrganism);
        // then check aliases
        prepareAliases(intactOrganism, true);
        // then check annotations
        prepareCellTypeAndTissue(intactOrganism, true);
    }

    public void clearCache() {
        this.persistedObjects.clear();
        this.convertedObjects.clear();
    }

    public IntactOrganism fetchByTaxID(int taxID) throws BridgeFailedException {
        Query query = getEntityManager().createQuery("select o from IntactOrganism o " +
                "where o.cellType is null " +
                "and o.tissue is null " +
                "and o.dbTaxid = :taxid");
        query.setParameter("taxid", Integer.toString(taxID));
        Collection<IntactOrganism> organism = query.getResultList();
        if (organism.size() == 1){
            return organism.iterator().next();
        }
        else if (organism.size() > 1){
            throw new BridgeFailedException("The organism "+taxID + " can match "+organism.size()+" organisms in the database and we cannot determine which one is valid.");
        }
        return null;
    }

    public Collection<Organism> fetchByTaxIDs(Collection<Integer> taxIDs) throws BridgeFailedException {
        if (taxIDs == null){
            throw new IllegalArgumentException("The taxids cannot be null.");
        }

        Collection<Organism> results = new ArrayList<Organism>(taxIDs.size());
        for (int id : taxIDs){
            Organism element = fetchByTaxID(id);
            if (element != null){
                results.add(element);
            }
        }
        return results;
    }

    protected void prepareCellTypeAndTissue(IntactOrganism intactOrganism, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactOrganism.getCellType() != null){
            intactOrganism.setCellType(enableSynchronization ?
                    getContext().getCellTypeSynchronizer().synchronize(intactOrganism.getCellType(), true) :
                    getContext().getCellTypeSynchronizer().convertToPersistentObject(intactOrganism.getCellType()));
        }
        if (intactOrganism.getTissue() != null){
            intactOrganism.setTissue(enableSynchronization ?
                    getContext().getTissueSynchronizer().synchronize(intactOrganism.getTissue(), true) :
                    getContext().getTissueSynchronizer().convertToPersistentObject(intactOrganism.getTissue()));
        }
    }

    protected void prepareAliases(IntactOrganism intactOrganism, boolean enableSynchronization) throws FinderException, PersisterException, SynchronizerException {
        if (intactOrganism.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactOrganism.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias organismAlias = enableSynchronization ?
                        getContext().getOrganismAliasSynchronizer().synchronize(alias, false) :
                        getContext().getOrganismAliasSynchronizer().convertToPersistentObject(alias);
                // we have a different instance because needed to be synchronized
                if (organismAlias != alias){
                    intactOrganism.getAliases().remove(alias);
                    intactOrganism.getAliases().add(organismAlias);
                }
            }
        }
    }

    protected void prepareFullName(IntactOrganism intactOrganism) {
        // truncate if necessary
        if (intactOrganism.getScientificName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactOrganism.getScientificName().length()){
            log.warn("Organism scientific name too long: "+intactOrganism.getScientificName()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactOrganism.setScientificName(intactOrganism.getScientificName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeCommonName(IntactOrganism intactOrganism) {
        // set shortname if not done yet
        if (intactOrganism.getCommonName() == null){
            intactOrganism.setCommonName(intactOrganism.getScientificName() != null ? intactOrganism.getScientificName() : Integer.toString(intactOrganism.getTaxId()));
            if (intactOrganism.getCellType() != null){
                intactOrganism.setCommonName(intactOrganism.getCommonName()+"-"+intactOrganism.getCellType().getShortName());
            }
            else if (intactOrganism.getTissue() != null){
                intactOrganism.setCommonName(intactOrganism.getCommonName()+"-"+intactOrganism.getTissue().getShortName());
            }
            else if (intactOrganism.getTissue() != null){
                intactOrganism.setCommonName(intactOrganism.getCommonName()+"-"+intactOrganism.getCompartment().getShortName());
            }
        }
        // truncate if necessary
        if (IntactUtils.MAX_SHORT_LABEL_LEN < intactOrganism.getCommonName().length()){
            log.warn("Organism shortLabel too long: "+intactOrganism.getCommonName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactOrganism.setCommonName(intactOrganism.getCommonName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
        String name;
        List<String> existingOrganism;
        do{
            name = intactOrganism.getCommonName().trim().toLowerCase();

            // check if short name already exist, if yes, synchronize with existing label
            Query query = getEntityManager().createQuery("select o.commonName from IntactOrganism o " +
                    "where (o.commonName = :name or (o.commonName like :nameWithSuffix and o.commonName not like :nameWithCellTissue) ) "
                    + (intactOrganism.getAc() != null ? "and o.ac <> :organismAc" : ""));
            query.setParameter("name", name);
            query.setParameter("nameWithSuffix", name+"-%");
            query.setParameter("nameWithCellTissue", name+"-%-%");
            if (intactOrganism.getAc() != null){
                query.setParameter("organismAc", intactOrganism.getAc());
            }
            existingOrganism = query.getResultList();
            if (!existingOrganism.isEmpty()){
                String nameInSync = IntactUtils.synchronizeShortlabel(name, existingOrganism, IntactUtils.MAX_SHORT_LABEL_LEN, false);
                if (!nameInSync.equals(name)){
                    intactOrganism.setCommonName(nameInSync);
                }
                else{
                    break;
                }
            }
            else{
                intactOrganism.setCommonName(name);
            }
        }
        while(!existingOrganism.isEmpty());
    }

    @Override
    protected Object extractIdentifier(IntactOrganism object) {
        return object.getAc();
    }

    @Override
    protected IntactOrganism instantiateNewPersistentInstance(Organism object, Class<? extends IntactOrganism> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        IntactOrganism o = intactClass.getConstructor(Integer.TYPE).newInstance(object.getTaxId());
        OrganismCloner.copyAndOverrideOrganismProperties(object, o);
        return o;
    }

    @Override
    protected void storeInCache(Organism originalObject, IntactOrganism persistentObject, IntactOrganism existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected IntactOrganism fetchObjectFromCache(Organism object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(Organism object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected boolean containsObjectInstance(Organism object) {
        return this.convertedObjects.containsKey(object);
    }

    @Override
    protected void removeObjectInstanceFromIdentityCache(Organism object) {
        this.convertedObjects.remove(object);
    }

    @Override
    protected IntactOrganism fetchMatchingObjectFromIdentityCache(Organism object) {
        return this.convertedObjects.get(object);
    }

    @Override
    protected void convertPersistableProperties(IntactOrganism intactOrganism) throws SynchronizerException, PersisterException, FinderException {
        // then check aliases
        prepareAliases(intactOrganism, false);
        // then check annotations
        prepareCellTypeAndTissue(intactOrganism, false);
    }

    @Override
    protected void storeObjectInIdentityCache(Organism originalObject, IntactOrganism persistableObject) {
        this.convertedObjects.put(originalObject, persistableObject);
    }

    @Override
    protected boolean isObjectDirty(Organism originalObject) {
        return !this.organismComparator.canCompare(originalObject);
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new OrganismMergerEnrichOnly(this));
    }
}
