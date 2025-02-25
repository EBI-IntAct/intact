/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.clone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.core.persister.IntactCore;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.user.Preference;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;
import uk.ac.ebi.intact.model.util.AnnotatedObjectUtils;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * IntAct Object cloner.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public abstract class AbstractIntactCloner {

    private static final Log log = LogFactory.getLog(AbstractIntactCloner.class);

    private boolean excludeACs;
    private boolean cloneCvObjectTree;

    protected class ClonerManager<T extends IntactObject> {

        Map<T, T> register = new HashMap<T, T>();

        public ClonerManager() {
        }

        public T getClonedObject(T io) {
            return register.get(io);
        }

        public boolean isAlreadyCloned(T io) {
            return register.containsKey(io);
        }

        public void addClone(T io, T clone) {
            register.put(io, clone);
        }
    }

    protected ClonerManager clonerManager;

    public AbstractIntactCloner() {
        clonerManager = new ClonerManager();
    }

    public AbstractIntactCloner(boolean excludeACs) {
        this.excludeACs = excludeACs;
        clonerManager = new ClonerManager();
    }

    public <T extends IntactObject> T clone(T intactObject) throws IntactClonerException {

        if (intactObject == null) return null;

        if (log.isDebugEnabled()) {
            log.debug("Cloning " + intactObject.getClass().getSimpleName() + ": " +
                    (intactObject instanceof AnnotatedObject ?
                            ((AnnotatedObject) intactObject).getShortLabel() : intactObject.getAc()));
        }

        T clone = null;

        if (clonerManager.isAlreadyCloned(intactObject)) {
            log.debug("(!) we already have it in cache");
            return (T) clonerManager.getClonedObject(intactObject);
        }

        try {
            if (intactObject instanceof AnnotatedObject) {
                clone = (T) cloneAnnotatedObject((AnnotatedObject<Xref, Alias>) intactObject);
            } else if (intactObject instanceof Annotation) {
                clone = (T) cloneAnnotation((Annotation) intactObject);
            } else if (intactObject instanceof Alias) {
                clone = (T) cloneAlias((Alias) intactObject);
            } else if (intactObject instanceof Xref) {
                clone = (T) cloneXref((Xref) intactObject);
            } else if (intactObject instanceof Range) {
                clone = (T) cloneRange((Range) intactObject);
            } else if (intactObject instanceof Confidence) {
                clone = (T) cloneConfidence((Confidence) intactObject);
            } else if (intactObject instanceof InteractionParameter) {
                clone = (T) cloneInteractionParameter((InteractionParameter) intactObject);
            } else if (intactObject instanceof ComponentParameter) {
                clone = (T) cloneComponentParameter((ComponentParameter) intactObject);
            } else if (intactObject instanceof ComponentConfidence) {
                clone = (T) cloneComponentConfidence((ComponentConfidence) intactObject);
            } else if (intactObject instanceof User) {
                clone = (T) cloneUser((User) intactObject);
            } else if (intactObject instanceof Role) {
                clone = (T) cloneRole((Role) intactObject);
            } else if (intactObject instanceof Preference) {
                clone = (T) clonePreference((Preference) intactObject);
            } else if (intactObject instanceof LifecycleEvent) {
                clone = (T) cloneLifecycleEvent((LifecycleEvent) intactObject);
            } else {
                throw new IllegalArgumentException("Cannot clone objects of type: " + intactObject.getClass().getName());
            }

            cloneIntactObjectCommon(intactObject, clone);

        } catch (Throwable e) {
            throw new IntactClonerException("Problem cloning " + intactObject.getClass().getSimpleName() + ": " + intactObject, e);
        }

        return clone;
    }


    protected AnnotatedObject cloneAnnotatedObject(AnnotatedObject<?, ?> annotatedObject) throws IntactClonerException {

        if (annotatedObject == null) return null;

        AnnotatedObject clone = null;
        if (annotatedObject instanceof Interaction) {
            clone = cloneInteraction((Interaction) annotatedObject);
        } else if (annotatedObject instanceof Interactor) {
            clone = cloneInteractor((Interactor) annotatedObject);
        } else if (annotatedObject instanceof CvObject) {
            clone = cloneCvObject((CvObject) annotatedObject);
        } else if (annotatedObject instanceof Experiment) {
            clone = cloneExperiment((Experiment) annotatedObject);
        } else if (annotatedObject instanceof Component) {
            clone = cloneComponent((Component) annotatedObject);
        } else if (annotatedObject instanceof BioSource) {
            clone = cloneBioSource((BioSource) annotatedObject);
        } else if (annotatedObject instanceof Feature) {
            clone = cloneFeature((Feature) annotatedObject);
        } else if (annotatedObject instanceof Publication) {
            clone = clonePublication((Publication) annotatedObject);
        } else if (annotatedObject instanceof Institution) {
            clone = cloneInstitution((Institution) annotatedObject);
        } else {
            throw new IllegalArgumentException("Cannot process annotated object of type: " + annotatedObject.getClass().getName());
        }

        cloneAnnotatedObjectCommon(annotatedObject, clone);

        return clone;
    }

    ///////////////////////////////////////
    // IntactObject cloners

    protected Annotation cloneAnnotation(Annotation annotation) throws IntactClonerException {
        if (annotation == null) return null;
        Annotation clone = new Annotation();

        clonerManager.addClone(annotation, clone);

        clone.setCvTopic(clone(annotation.getCvTopic()));
        clone.setAnnotationText(annotation.getAnnotationText());
        return clone;
    }

    protected Alias cloneAlias(Alias alias) throws IntactClonerException {
        if (alias == null) return null;

        Class clazz = CgLibUtil.removeCglibEnhanced(alias.getClass());
        Alias clone = null;
        try {
            final Constructor constructor = clazz.getConstructor();
            clone = (Alias) constructor.newInstance();

            clonerManager.addClone(alias, clone);

            clone.setCvAliasType(clone(alias.getCvAliasType()));
            clone.setName(alias.getName());
        } catch (Exception e) {
            throw new IntactClonerException("An error occured upon building a " + clazz.getSimpleName(), e);
        }
        return clone;
    }

    /**
     * Note: this does not clone the parent.
     *
     * @param xref
     * @return
     * @throws uk.ac.ebi.intact.model.clone.IntactClonerException
     *
     */
    protected Xref cloneXref(Xref xref) throws IntactClonerException {
        if (xref == null) return null;

        Class clazz = CgLibUtil.removeCglibEnhanced(xref.getClass());
        Xref clone = null;

        try {
            final Constructor constructor = clazz.getConstructor();
            clone = (Xref) constructor.newInstance();

            clonerManager.addClone(xref, clone);

            clone.setPrimaryId(xref.getPrimaryId());
            clone.setSecondaryId(xref.getSecondaryId());
            clone.setDbRelease(xref.getDbRelease());
            clone.setCvDatabase(clone(xref.getCvDatabase()));
            clone.setCvXrefQualifier(clone(xref.getCvXrefQualifier()));

        } catch (Exception e) {
            throw new IntactClonerException("An error occured upon building a " + clazz.getSimpleName(), e);
        }

        clone.setParent(clone(xref.getParent()));

        return clone;
    }

    protected Range cloneRange(Range range) throws IntactClonerException {
        if (range == null) {
            throw new IllegalArgumentException("You must give a non null range");
        }

        Range clone = new Range();

        clonerManager.addClone(range, clone);

        clone.setFromIntervalStart(range.getFromIntervalStart());
        clone.setFromIntervalEnd(range.getFromIntervalEnd());
        clone.setToIntervalStart(range.getToIntervalStart());
        clone.setToIntervalEnd(range.getToIntervalEnd());

        clone.setSequence(range.getSequence());
        clone.setFullSequence(range.getFullSequence());
        clone.setUpStreamSequence(range.getUpStreamSequence());
        clone.setDownStreamSequence(range.getDownStreamSequence());

        clone.setFromCvFuzzyType(clone(range.getFromCvFuzzyType()));
        clone.setToCvFuzzyType(clone(range.getToCvFuzzyType()));

        clone.setFeature(clone(range.getFeature()));

        return clone;
    }

    protected Confidence cloneConfidence(Confidence confidence) throws IntactClonerException {
        if (confidence == null) {
            throw new IllegalArgumentException("You must give a non null confidence");
        }

        Confidence clone = new Confidence();

        clonerManager.addClone(confidence, clone);

        clone.setValue(confidence.getValue());
        clone.setInteraction(clone(confidence.getInteraction()));
        clone.setCvConfidenceType(clone(confidence.getCvConfidenceType()));

        return clone;
    }

    protected InteractionParameter cloneInteractionParameter(InteractionParameter interactionParameter) throws IntactClonerException {
        if (interactionParameter == null) {
            throw new IllegalArgumentException("You must give a non null interaction parameter");
        }

        InteractionParameter clone = new InteractionParameter();

        clonerManager.addClone(interactionParameter, clone);

        clone.setBase(interactionParameter.getBase());
        clone.setExponent(interactionParameter.getExponent());
        clone.setFactor(interactionParameter.getFactor());
        clone.setUncertainty(interactionParameter.getUncertainty());
        clone.setCvParameterType(clone(interactionParameter.getCvParameterType()));
        clone.setCvParameterUnit(clone(interactionParameter.getCvParameterUnit()));
        clone.setInteraction(interactionParameter.getInteraction());

        return clone;
    }

    protected ComponentParameter cloneComponentParameter(ComponentParameter componentParameter) throws IntactClonerException {
        if (componentParameter == null) {
            throw new IllegalArgumentException("You must give a non null component parameter");
        }

        ComponentParameter clone = new ComponentParameter();

        clonerManager.addClone(componentParameter, clone);

        clone.setBase(componentParameter.getBase());
        clone.setExponent(componentParameter.getExponent());
        clone.setFactor(componentParameter.getFactor());
        clone.setUncertainty(componentParameter.getUncertainty());
        clone.setCvParameterType(clone(componentParameter.getCvParameterType()));
        clone.setCvParameterUnit(clone(componentParameter.getCvParameterUnit()));
        clone.setComponent(componentParameter.getComponent());

        return clone;
    }

    protected ComponentConfidence cloneComponentConfidence(ComponentConfidence componentConfidence) throws IntactClonerException {
        if (componentConfidence == null) {
            throw new IllegalArgumentException("You must give a non null component confidence");
        }

        ComponentConfidence clone = new ComponentConfidence();

        clonerManager.addClone(componentConfidence, clone);

        clone.setValue(componentConfidence.getValue());
        clone.setComponent(clone(componentConfidence.getComponent()));
        clone.setCvConfidenceType(clone(componentConfidence.getCvConfidenceType()));

        return clone;
    }

    ///////////////////////////////////////
    // AnnotatedObject cloners

    public Experiment cloneExperiment(Experiment experiment) throws IntactClonerException {
        if (experiment == null) return null;
        Experiment clone = new Experiment();

        clonerManager.addClone(experiment, clone);

        clone.setCvIdentification(clone(experiment.getCvIdentification()));
        clone.setCvInteraction(clone(experiment.getCvInteraction()));
        clone.setBioSource(clone(experiment.getBioSource()));
        clone.setPublication(clone(experiment.getPublication()));

        if (isCollectionClonable(experiment.getInteractions())) {
            Collection<Interaction> interactions = IntactCore.ensureInitializedInteractions(experiment);

            for (Interaction i : interactions) {
                clone.addInteraction(clone(i));
            }
        }

        return clone;
    }

    public Feature cloneFeature(Feature feature) throws IntactClonerException {
        if (feature == null) return null;
        Feature clone = new Feature();

        clonerManager.addClone(feature, clone);

        clone.setOwner(clone(feature.getOwner()));
        clone.setShortLabel(feature.getShortLabel());
        clone.setCvFeatureType(clone(feature.getCvFeatureType()));
        clone.setCvFeatureIdentification(clone(feature.getCvFeatureIdentification()));

        if (isCollectionClonable(feature.getRanges())) {
            Collection<Range> ranges = IntactCore.ensureInitializedRanges(feature);

            for (Range range : ranges) {
                clone.addRange(clone(range));
            }
        }

        clone.setComponent(clone(feature.getComponent()));

        return clone;
    }

    public Institution cloneInstitution(Institution institution) throws IntactClonerException {
        if (institution == null) return null;

        if (clonerManager.isAlreadyCloned(institution)) {
            return (Institution) clonerManager.getClonedObject(institution);
        }

        Institution clone = new Institution();

        clonerManager.addClone(institution, clone);

        clone.setUrl(institution.getUrl());
        clone.setPostalAddress(institution.getPostalAddress());

        return clone;
    }

    public Interaction cloneInteraction(Interaction interaction) throws IntactClonerException {
        if (interaction == null) return null;
        Interaction clone = new InteractionImpl();

        clonerManager.addClone(interaction, clone);

        if (isCollectionClonable(interaction.getComponents())) {
            Collection<Component> components = IntactCore.ensureInitializedParticipants(interaction);

            for (Component component : components) {

                final Component clonedComp = clone(component);
                clonedComp.setInteraction(clone);
                clone.getComponents().add(clonedComp);
            }
        }

        clone.setCvInteractionType(clone(interaction.getCvInteractionType()));
        clone.setCvInteractorType(clone(interaction.getCvInteractorType()));

        if (isCollectionClonable(interaction.getExperiments())) {
            Collection<Experiment> experiments = IntactCore.ensureInitializedExperiments(interaction);

            for (Experiment experiment : experiments) {
                clone.addExperiment(clone(experiment));
            }
        }

        clone.setKD(interaction.getKD());
        clone.setCrc(interaction.getCrc());

        if (isCollectionClonable(interaction.getConfidences())) {
            Collection<Confidence> confidences = IntactCore.ensureInitializedConfidences(interaction);

            for (Confidence confidence : confidences) {
                clone.addConfidence(clone(confidence));
            }
        }

        if (isCollectionClonable(interaction.getParameters())) {
            Collection<InteractionParameter> parameters = IntactCore.ensureInitializedInteractionParameters(interaction);

            for (InteractionParameter interactionParameter : parameters) {
                clone.addParameter(clone(interactionParameter));
            }
        }


        return clone;
    }

    public Interactor cloneInteractor(Interactor interactor) throws IntactClonerException {
        if (interactor == null) return null;

        Interactor clone = null;

        final Class clazz = CgLibUtil.removeCglibEnhanced(interactor.getClass());

        try {
            final Constructor constructor = clazz.getConstructor();
            clone = (Interactor) constructor.newInstance();

            clonerManager.addClone(interactor, clone);

        } catch (Exception e) {
            throw new IntactClonerException("An error occured upon cloning a " + clazz.getSimpleName(), e);
        }

        if (interactor instanceof Polymer) {
            Polymer p = (Polymer) clone;
            p.setSequence(((Polymer) interactor).getSequence());
            p.setCrc64(((Polymer) interactor).getCrc64());
        }

        clone.setBioSource(clone(interactor.getBioSource()));
        clone.setCvInteractorType(clone(interactor.getCvInteractorType()));
        clone.setObjClass(interactor.getObjClass());

        // This is commented, because it is unusuable in production environments, where
        // one interactions may be present in many components; these components have more interactions
        // and that can only end in a memory problem
//        for ( Component component : interactor.getActiveInstances() ) {
//            clone.addActiveInstance(clone( component ));
//        }

        return clone;
    }

    public BioSource cloneBioSource(BioSource bioSource) throws IntactClonerException {
        if (bioSource == null) return null;

        BioSource clone = new BioSource();

        clonerManager.addClone(bioSource, clone);

        clone.setTaxId(bioSource.getTaxId());
        clone.setCvCellType(clone(bioSource.getCvCellType()));
        clone.setCvTissue(clone(bioSource.getCvTissue()));

        return clone;
    }

    public Publication clonePublication(Publication publication) throws IntactClonerException {
        if (publication == null) return null;

        Publication clone = new Publication();

        clonerManager.addClone(publication, clone);

        if (isCollectionClonable(publication.getExperiments())) {
            Collection<Experiment> experiments = IntactCore.ensureInitializedExperiments(publication);

            for (Experiment e : experiments) {
                clone.addExperiment(clone(e));
            }
        }

        if (isCollectionClonable(publication.getLifecycleEvents())) {
            Collection<LifecycleEvent> events = IntactCore.ensureInitializedLifecycleEvents(publication);

            for (LifecycleEvent event : events) {
                clone.addLifecycleEvent(clone(event));
            }
        }

        clone.setStatus(clone(publication.getStatus()));
        clone.setCurrentOwner(clone(publication.getCurrentOwner()));
        clone.setCurrentReviewer(clone(publication.getCurrentReviewer()));

        return clone;
    }

    public Component cloneComponent(Component component) throws IntactClonerException {
        if (component == null) return null;

        Component clone = new Component();

        clonerManager.addClone(component, clone);

        clone.setInteractor(clone(component.getInteractor()));
        clone.setInteraction(clone(component.getInteraction()));
        clone.setCvBiologicalRole(clone(component.getCvBiologicalRole()));

        clone.setStoichiometry(component.getStoichiometry());
        clone.setExpressedIn(clone(component.getExpressedIn()));

        if (isCollectionClonable(component.getParameters())) {
            Collection<ComponentParameter> parameters = IntactCore.ensureInitializedComponentParameters(component);

            for (ComponentParameter componentParameter : parameters) {
                clone.addParameter(clone(componentParameter));
            }
        }

        if (isCollectionClonable(component.getFeatures())) {
            Collection<Feature> features = IntactCore.ensureInitializedFeatures(component);

            for (Feature feature : features) {
                clone.addFeature(clone(feature));
            }
        }

        if (isCollectionClonable(component.getExperimentalPreparations())) {
            Collection<CvExperimentalPreparation> expPreps = IntactCore.ensureInitializedExperimentalPreparations(component);

            for (CvExperimentalPreparation preparation : expPreps) {
                clone.getExperimentalPreparations().add(clone(preparation));
            }
        }

        if (isCollectionClonable(component.getParticipantDetectionMethods())) {
            Collection<CvIdentification> partDet = IntactCore.ensureInitializedParticipantIdentificationMethods(component);

            for (CvIdentification method : partDet) {
                clone.getParticipantDetectionMethods().add(clone(method));
            }
        }

        if (isCollectionClonable(component.getExperimentalRoles())) {
            Collection<CvExperimentalRole> roles = IntactCore.ensureInitializedExperimentalRoles(component);

            for (CvExperimentalRole expRole : roles) {
                clone.getExperimentalRoles().add(clone(expRole));
            }
        }

        if (isCollectionClonable(component.getConfidences())) {
            Collection<ComponentConfidence> confidences = IntactCore.ensureInitializedComponentConfidences(component);

            for (ComponentConfidence conf : confidences) {
                clone.addConfidence(clone(conf));
            }
        }

        return clone;
    }

    public CvObject cloneCvObject(CvObject cvObject) throws IntactClonerException {
        if (cvObject == null) return null;

        Class clazz = CgLibUtil.removeCglibEnhanced(cvObject.getClass());
        CvObject clone = null;
        try {
            final Constructor constructor = clazz.getConstructor();
            clone = (CvObject) constructor.newInstance();

            clonerManager.addClone(cvObject, clone);

            clone.setIdentifier(cvObject.getIdentifier());
            clone.setObjClass(cvObject.getObjClass());

        } catch (Exception e) {
            throw new IntactClonerException("An error occured upon cloning a " + clazz.getSimpleName() + ": " + cvObject.getShortLabel(), e);
        }

        if (cloneCvObjectTree) {
            if (cvObject instanceof CvDagObject) {
                CvDagObject cvDag = (CvDagObject) cvObject;
                CvDagObject cloneDag = (CvDagObject) clone;

                if (isCollectionClonable(cvDag.getParents())) {
                    for (CvDagObject parent : cvDag.getParents()) {
                        cloneDag.getParents().add(clone(parent));
                    }
                }

                if (isCollectionClonable(cvDag.getChildren())) {
                    for (CvDagObject child : cvDag.getChildren()) {
                        cloneDag.getChildren().add(clone(child));
                    }
                }
            }
        }

        return clone;
    }

    public User cloneUser(User user) throws IntactClonerException {
        if (user == null) {
            throw new IllegalArgumentException("You must give a non null user");
        }

        User clone = new User();
        clone.setEmail(user.getEmail());
        clone.setFirstName(user.getFirstName());
        clone.setLastName(user.getLastName());
        clone.setDisabled(user.isDisabled());
        clone.setLastLogin(user.getLastLogin());
        clone.setLogin(user.getLogin());
        clone.setOpenIdUrl(user.getOpenIdUrl());
        clone.setPassword(user.getPassword());

        if (isCollectionClonable(user.getPreferences())) {
             Collection<Preference> preferences = IntactCore.ensureInitializedPreferences(user);

            for (Preference preference : preferences) {
                clone.addPreference(clone(preference));
            }
        }

        if (isCollectionClonable(user.getRoles())) {
            Collection<Role> roles = IntactCore.ensureInitializedRoles(user);

            for (Role role : roles) {
                clone.addRole(clone(role));
            }
        }

        return clone;
    }

    public Role cloneRole(Role role) throws IntactClonerException {
        if (role == null) {
            throw new IllegalArgumentException("You must give a non null role");
        }

        Role clone = new Role(role.getName());

        return clone;
    }

    public Preference clonePreference(Preference preference) throws IntactClonerException {
        if (preference == null) {
            throw new IllegalArgumentException("You must give a non null preference");
        }

        Preference clone = new Preference(preference.getUser(), preference.getKey(), preference.getValue());
        return clone;

    }

    public LifecycleEvent cloneLifecycleEvent(LifecycleEvent lifecycleEvent) throws IntactClonerException {
        if (lifecycleEvent == null) {
            throw new IllegalArgumentException("You must give a non null lifecycleEvent");
        }

        LifecycleEvent clone = new LifecycleEvent();
        clone.setEvent(clone(lifecycleEvent.getEvent()));
        clone.setNote(lifecycleEvent.getNote());
        clone.setPublication(clone(lifecycleEvent.getPublication()));
        clone.setWho(clone(lifecycleEvent.getWho()));
        clone.setWhen(lifecycleEvent.getWhen());

        return clone;
    }



    protected AnnotatedObject cloneAnnotatedObjectCommon(AnnotatedObject<?, ?> ao, AnnotatedObject clone) throws IntactClonerException {

        if (ao == clone) {
            throw new IllegalStateException(ao.getClass().getSimpleName() + " are the same instance!!");
        }

        if (!(ao instanceof Institution)) {
            clone.setOwner(clone(ao.getOwner()));
        }

        clone.setShortLabel(ao.getShortLabel());
        clone.setFullName(ao.getFullName());

        TransactionStatus transactionStatus = null;

        // as annotations, alias and xrefs could potentially be dettached, we should check if these
        // collections are accessible
        if (!AnnotatedObjectUtils.isNewOrManaged(ao)) {
            Class<? extends AnnotatedObject> clazz = ao.getClass();
            String ac = ao.getAc();

            transactionStatus = IntactContext.getCurrentInstance().getDataContext().beginTransaction("Core - Cloner");

            ao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getAnnotatedObjectDao(clazz).getByAc(ac);

            if (ao == null) {
                throw new IllegalStateException("Annotated object was expected to be found: " + clazz.getSimpleName() + " " + ac);
            }
        }

        if (isCollectionClonable(ao.getAnnotations())) {
            Collection<Annotation> annotations = IntactCore.ensureInitializedAnnotations(ao);

            for (Annotation annotation : annotations) {
                clone.addAnnotation(clone(annotation));
            }
        }

        if (isCollectionClonable(ao.getAliases())) {
            Collection<? extends Alias> aliases = IntactCore.ensureInitializedAliases(ao);

            for (Alias alias : aliases) {
                clone.addAlias(clone(alias));
            }
        }

        if (isCollectionClonable(ao.getXrefs())) {
            Collection<? extends Xref> refs = IntactCore.ensureInitializedXrefs(ao);

            for (Xref xref : refs) {
                clone.addXref(clone(xref));
            }
        }

        if (transactionStatus != null) {
            IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus);
        }

        return clone;
    }

    protected IntactObject cloneIntactObjectCommon(IntactObject ao, IntactObject clone) throws IntactClonerException {
        if (!excludeACs) {
            clone.setAc(ao.getAc());
        }
        if (ao.getCreated() != null) {
            clone.setCreated(new Date(ao.getCreated().getTime()));
        }
        if (ao.getUpdated() != null) {
            clone.setUpdated(new Date(ao.getUpdated().getTime()));
        }
        clone.setCreator(ao.getCreator());
        clone.setUpdator(ao.getUpdator());


        if (!(ao instanceof Institution)) {
            if (ao instanceof OwnedObject) {
                final OwnedObject boc = (OwnedObject) clone;
                final OwnedObject bo = (OwnedObject) ao;
                boc.setOwner(clone(bo.getOwner()));
            }
        }

        return clone;
    }

    public boolean isExcludeACs() {
        return excludeACs;
    }

    public void setExcludeACs(boolean excludeACs) {
        this.excludeACs = excludeACs;
    }

    public boolean isCloneCvObjectTree() {
        return cloneCvObjectTree;
    }

    public void setCloneCvObjectTree(boolean cloneCvObjectTree) {
        this.cloneCvObjectTree = cloneCvObjectTree;
    }

    protected abstract boolean isCollectionClonable(Collection col);
}
