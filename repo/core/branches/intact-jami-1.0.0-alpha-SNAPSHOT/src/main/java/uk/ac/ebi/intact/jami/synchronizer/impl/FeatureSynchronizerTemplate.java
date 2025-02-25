package uk.ac.ebi.intact.jami.synchronizer.impl;

import org.apache.commons.collections.map.IdentityMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.clone.FeatureCloner;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.merger.FeatureMergerEnrichOnly;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactFeature;
import uk.ac.ebi.intact.jami.synchronizer.AbstractIntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default finder/synchronizer for features
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class FeatureSynchronizerTemplate<F extends Feature, I extends AbstractIntactFeature> extends AbstractIntactDbSynchronizer<F,I>{
    private Map<F, I> persistedObjects;

    private static final Log log = LogFactory.getLog(FeatureSynchronizerTemplate.class);

    public FeatureSynchronizerTemplate(SynchronizerContext context, Class<? extends I> featureClass){
        super(context, featureClass);

        this.persistedObjects = new IdentityMap();
    }

    public I find(F feature) throws FinderException {
        if (this.persistedObjects.containsKey(feature)){
            return this.persistedObjects.get(feature);
        }
        // only retrieve an object in the cache, otherwise return null
        else {
            return null;
        }
    }

    public void synchronizeProperties(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        // then check shortlabel/synchronize
        prepareAndSynchronizeShortLabel(intactFeature);
        // then check full name
        prepareFullName(intactFeature);
        prepareType(intactFeature);
        // then check def
        prepareInteractionEffectAndDependencies(intactFeature);
        // then check aliases
        prepareAliases(intactFeature);
        // then check annotations
        prepareAnnotations(intactFeature);
        // then check xrefs
        prepareXrefs(intactFeature);
        // then check ranges
        prepareRanges(intactFeature);
        // then check linkedFeatures
        prepareLinkedFeatures(intactFeature);
    }

    protected void prepareType(I intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.getType() != null){
            intactFeature.setType(getContext().getFeatureTypeSynchronizer().synchronize(intactFeature.getType(), true));
        }
    }

    public void clearCache() {
        this.persistedObjects.clear();
    }

    protected void prepareLinkedFeatures(I intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areLinkedFeaturesInitialized()){
            List<I> featureToSynchronize = new ArrayList<I>(intactFeature.getLinkedFeatures());
            for (I feature : featureToSynchronize){
                if (intactFeature != feature){
                    // do not persist or merge features because of cascades
                    I linkedFeature = synchronize((F) feature, false);
                    // we have a different instance because needed to be synchronized
                    if (linkedFeature != feature){
                        intactFeature.getLinkedFeatures().remove(feature);
                        intactFeature.getLinkedFeatures().add(linkedFeature);
                    }
                }
            }
        }
    }

    protected void prepareRanges(I intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.areRangesInitialized()){
            List<Range> rangesToPersist = new ArrayList<Range>(intactFeature.getRanges());
            for (Range range : rangesToPersist){
                // do not persist or merge ranges because of cascades
                Range featureRange = getContext().getRangeSynchronizer().synchronize(range, false);
                // we have a different instance because needed to be synchronized
                if (featureRange != range){
                    intactFeature.getRanges().remove(range);
                    intactFeature.getRanges().add(featureRange);
                }
            }
        }
    }

    protected void prepareInteractionEffectAndDependencies(I intactFeature) throws PersisterException, FinderException, SynchronizerException {
        if (intactFeature.getInteractionDependency() != null){
            intactFeature.setInteractionDependency(getContext().getTopicSynchronizer().synchronize(intactFeature.getInteractionDependency(), true));
        }

        if (intactFeature.getInteractionEffect() != null){
            intactFeature.setInteractionEffect(getContext().getTopicSynchronizer().synchronize(intactFeature.getInteractionEffect(), true));
        }
    }

    protected void prepareXrefs(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areXrefsInitialized()){
            List<Xref> xrefsToPersist = new ArrayList<Xref>(intactFeature.getPersistentXrefs());
            for (Xref xref : xrefsToPersist){
                // do not persist or merge xrefs because of cascades
                Xref featureXref = getContext().getFeatureXrefSynchronizer().synchronize(xref, false);
                // we have a different instance because needed to be synchronized
                if (featureXref != xref){
                    intactFeature.getPersistentXrefs().remove(xref);
                    intactFeature.getPersistentXrefs().add(featureXref);
                }
            }
        }
    }

    protected void prepareAnnotations(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAnnotationsInitialized()){
            List<Annotation> annotationsToPersist = new ArrayList<Annotation>(intactFeature.getAnnotations());
            for (Annotation annotation : annotationsToPersist){
                // do not persist or merge annotations because of cascades
                Annotation featureAnnotation = getContext().getFeatureAnnotationSynchronizer().synchronize(annotation, false);
                // we have a different instance because needed to be synchronized
                if (featureAnnotation != annotation){
                    intactFeature.getAnnotations().remove(annotation);
                    intactFeature.getAnnotations().add(featureAnnotation);
                }
            }
        }
    }

    protected void prepareAliases(I intactFeature) throws FinderException, PersisterException, SynchronizerException {
        if (intactFeature.areAliasesInitialized()){
            List<Alias> aliasesToPersist = new ArrayList<Alias>(intactFeature.getAliases());
            for (Alias alias : aliasesToPersist){
                // do not persist or merge alias because of cascades
                Alias featureAlias = getContext().getFeatureAliasSynchronizer().synchronize(alias, false);
                // we have a different instance because needed to be synchronized
                if (featureAlias != alias){
                    intactFeature.getAliases().remove(alias);
                    intactFeature.getAliases().add(featureAlias);
                }
            }
        }
    }

    protected void prepareFullName(I intactFeature) {
        // truncate if necessary
        if (intactFeature.getFullName() != null && IntactUtils.MAX_FULL_NAME_LEN < intactFeature.getFullName().length()){
            log.warn("Feature fullName too long: "+intactFeature.getFullName()+", will be truncated to "+ IntactUtils.MAX_FULL_NAME_LEN+" characters.");
            intactFeature.setFullName(intactFeature.getFullName().substring(0, IntactUtils.MAX_FULL_NAME_LEN));
        }
    }

    protected void prepareAndSynchronizeShortLabel(I intactFeature) {
        // truncate if necessary
        if (intactFeature.getShortName() == null){
            intactFeature.setShortName("N/A");
        }
        else if (IntactUtils.MAX_SHORT_LABEL_LEN < intactFeature.getShortName().length()){
            log.warn("Feature shortLabel too long: "+intactFeature.getShortName()+", will be truncated to "+ IntactUtils.MAX_SHORT_LABEL_LEN+" characters.");
            intactFeature.setShortName(intactFeature.getShortName().substring(0, IntactUtils.MAX_SHORT_LABEL_LEN));
        }
    }

    @Override
    protected I instantiateNewPersistentInstance(F object, Class<? extends I> intactClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        I newFeature = intactClass.newInstance();
        FeatureCloner.copyAndOverrideBasicFeaturesProperties(object, newFeature);
        newFeature.setParticipant(object.getParticipant());
        return newFeature;
    }

    @Override
    protected void storeInCache(F originalObject, I persistentObject, I existingInstance) {
        if (existingInstance != null){
            this.persistedObjects.put(originalObject, existingInstance);
        }
        else{
            this.persistedObjects.put(originalObject, persistentObject);
        }
    }

    @Override
    protected I fetchObjectFromCache(F object) {
        return this.persistedObjects.get(object);
    }

    @Override
    protected boolean isObjectStoredInCache(F object) {
        return this.persistedObjects.containsKey(object);
    }

    @Override
    protected Object extractIdentifier(I object) {
        return object.getAc();
    }

    @Override
    protected void initialiseDefaultMerger() {
        super.setIntactMerger(new FeatureMergerEnrichOnly<F, I>());
    }
}
