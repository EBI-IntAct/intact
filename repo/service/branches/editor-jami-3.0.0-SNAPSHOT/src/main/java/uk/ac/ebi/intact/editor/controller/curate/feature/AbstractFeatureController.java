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
package uk.ac.ebi.intact.editor.controller.curate.feature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.hibernate.Hibernate;
import org.primefaces.event.TabChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.editor.controller.curate.AnnotatedObjectController;
import uk.ac.ebi.intact.editor.controller.curate.ChangesController;
import uk.ac.ebi.intact.editor.controller.curate.experiment.ExperimentController;
import uk.ac.ebi.intact.editor.controller.curate.interaction.InteractionController;
import uk.ac.ebi.intact.editor.controller.curate.participant.ParticipantController;
import uk.ac.ebi.intact.editor.controller.curate.publication.PublicationController;
import uk.ac.ebi.intact.editor.services.curate.cvobject.CvObjectService;
import uk.ac.ebi.intact.editor.services.curate.feature.FeatureEditorService;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract Feature controller.
 */
public abstract class AbstractFeatureController<T extends AbstractIntactFeature> extends AnnotatedObjectController {

    private static final Log log = LogFactory.getLog( AbstractFeatureController.class );

    private T feature;
    private List<RangeWrapper> rangeWrappers;
    private boolean containsInvalidRanges;

    /**
     * The AC of the feature to be loaded.
     */
    private String ac;

    private String newRangeValue;

    private boolean isRangeDisabled;

    @Resource(name = "featureEditorService")
    private transient FeatureEditorService featureEditorService;

    private Class<T> featureClass;

    public AbstractFeatureController(Class<T> featureClass) {
        if (featureClass == null){
             throw new IllegalArgumentException("the feature class cannot be null");
        }
        this.featureClass = featureClass;
    }

    @Override
    public IntactPrimaryObject getAnnotatedObject() {
        return getFeature();
    }

    @Override
    public void setAnnotatedObject(IntactPrimaryObject annotatedObject) {
         setFeature((AbstractIntactFeature)feature);
    }

    @Override
    protected void loadCautionMessages() {
        if (this.feature != null){
            if (!feature.areAnnotationsInitialized()){
                setFeature(getFeatureEditorService().initialiseFeatureAnnotations(this.feature));
            }

            Annotation caution = AnnotationUtils.collectFirstAnnotationWithTopic(this.feature.getAnnotations(), Annotation.CAUTION_MI, Annotation.CAUTION);
            setCautionMessage(caution != null ? caution.getValue() : null);
            Annotation internal = AnnotationUtils.collectFirstAnnotationWithTopic(this.feature.getAnnotations(), null, "remark-internal");
            setInternalRemark(internal != null ? internal.getValue() : null);
        }
    }

    public void loadData( ComponentSystemEvent event ) {
        if (!FacesContext.getCurrentInstance().isPostback()) {

            if ( ac != null ) {
                if ( feature == null || !ac.equals( feature.getAc() ) ) {
                    setFeature(getFeatureEditorService().loadFeatureByAc(ac, this.featureClass));
                }
            } else {
                if ( feature != null ) ac = feature.getAc();
            }

            if (feature == null) {
                super.addErrorMessage("Feature does not exist", ac);
                return;
            }

            refreshParentControllers();
            refreshTabs();
        }

        if (containsInvalidRanges) {
            addWarningMessage("This feature contains invalid ranges", "Ranges must be fixed before being able to save");
        }

        generalLoadChecks();
    }

    protected abstract void refreshParentControllers();

    public void refreshRangeWrappers() {
        this.rangeWrappers = new ArrayList<RangeWrapper>(feature.getRanges().size());

        String sequence = getSequence();

        containsInvalidRanges = false;

        for (Range range : feature.getRanges()) {
            rangeWrappers.add(new RangeWrapper(range, sequence));

            if (!containsInvalidRanges && FeatureUtils.isABadRange(range, sequence)) {
                containsInvalidRanges = true;
            }
        }
    }

    @Override
    protected IntactCloner newClonerInstance() {
        return new FeatureIntactCloner();
    }

    public String newFeature(Component participant) {
        Feature feature = new Feature("feature", participant, new CvFeatureType());
        feature.setShortLabel(null);
        feature.setCvFeatureType(null);

        setFeature(feature);

        //participant.addBindingDomain(feature);

        refreshRangeWrappers();
        changed();
        //getUnsavedChangeManager().markAsUnsaved(feature);

        return navigateToObject(feature);
    }

    public void newRange(ActionEvent evt) {
        if (newRangeValue == null || newRangeValue.isEmpty()) {
            addErrorMessage("Range value field is empty", "Please provide a range value before clicking on the New Range button");
            return;
        }

        newRangeValue = newRangeValue.trim();

        if (!newRangeValue.contains("-")) {
            addErrorMessage("Illegal range value", "The range must contain a hyphen");
            return;
        }

        String sequence = getSequence();

        if (FeatureUtils.isABadRange(newRangeValue, sequence)) {
            String problemMsg = FeatureUtils.getBadRangeInfo(newRangeValue, sequence);
            addErrorMessage("Range is not valid", problemMsg);
            return;
        }

        Range newRange = FeatureUtils.createRangeFromString(newRangeValue, sequence);
        newRange.setLinked(false);

        // replace CVs by ones with ACs

        CvObjectService cvObjectService = (CvObjectService) getSpringContext().getBean("cvObjectService");
        CvFuzzyType fromFuzzyType = cvObjectService.findCvObjectByIdentifier(CvFuzzyType.class, newRange.getFromCvFuzzyType().getIdentifier());
        CvFuzzyType toFuzzyType = cvObjectService.findCvObjectByIdentifier(CvFuzzyType.class, newRange.getToCvFuzzyType().getIdentifier());

        newRange.setFromCvFuzzyType(fromFuzzyType);
        newRange.setToCvFuzzyType(toFuzzyType);

        feature.addRange(newRange);

        refreshRangeWrappers();

        newRangeValue = null;

        setUnsavedChanges(true);
    }

    public void validateFeature(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (feature.getRanges().isEmpty()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Feature without ranges", "One range is mandatory");
            throw new ValidatorException(message);
        }
    }

    private String getSequence() {
        Interactor interactor = feature.getComponent().getInteractor();

        String sequence = null;

        if (interactor instanceof Polymer) {
            Polymer polymer = (Polymer) interactor;
            sequence = polymer.getSequence();
        }
        return sequence;
    }

    public void markRangeToDelete(Range range) {
        if (range == null) return;

        if (range.getAc() == null) {
            feature.removeRange(range);
            refreshRangeWrappers();
        } else {
            getChangesController().markToDeleteRange(range, range.getFeature());
        }
    }

    public List<RangeWrapper> getWrappedRanges() {
        return rangeWrappers;
    }

    public String getAc() {
        if ( ac == null && feature != null ) {
            return feature.getAc();
        }
        return ac;
    }

    @Override
    public int getXrefsSize() {
        return 0;
    }

    @Override
    public int getAliasesSize() {
        return 0;
    }

    @Override
    public int getAnnotationsSize() {
        return 0;
    }

    public void setAc( String ac ) {
        this.ac = ac;
    }

    public Feature getFeature() {
        return feature;
    }

    @Override
    public void refreshTabsAndFocusXref(){
        refreshTabs();
    }

    @Override
    public void refreshTabs(){
        super.refreshTabs();

        this.isRangeDisabled = false;
    }

    public void setFeature( Feature feature ) {
        this.feature = feature;

        if (feature != null){
            this.ac = feature.getAc();
        }
    }

    @Override
    public void doPreSave() {
        // the feature was just created, add it to the list of features of the participant
        if (feature.getAc() == null){
            participantController.getParticipant().addFeature(feature);
        }
    }

    @Override
    public String doDelete(){
        if (feature.getBoundDomain() != null){
            Feature bound = feature.getBoundDomain();

            if (bound.getBoundDomain() != null && feature.getAc() != null && feature.getAc().equalsIgnoreCase(bound.getBoundDomain().getAc())){
                bound.setBoundDomain(null);
                getPersistenceController().doSave(bound);
            }
            else if (bound.getBoundDomain() != null && feature.getAc() == null && feature.equals(bound.getBoundDomain())){
                bound.setBoundDomain(null);
                getPersistenceController().doSave(bound);
            }

            feature.setBoundDomain(null);
        }

        return super.doDelete();
    }

    @Override
    public void newXref(ActionEvent evt) {

    }

    @Override
    public <T extends AbstractIntactXref> T newXref(String db, String dbMI, String id, String secondaryId, String qualifier, String qualifierMI) {
        return null;
    }

    public String getNewRangeValue() {
        return newRangeValue;
    }

    public void setNewRangeValue(String newRangeValue) {
        this.newRangeValue = newRangeValue;
    }

    public boolean isContainsInvalidRanges() {
        return containsInvalidRanges;
    }

    public void setContainsInvalidRanges(boolean containsInvalidRanges) {
        this.containsInvalidRanges = containsInvalidRanges;
    }

    @Override
    public Collection<String> collectParentAcsOfCurrentAnnotatedObject(){
        Collection<String> parentAcs = new ArrayList<String>();

        if (feature.getComponent() != null){
            Component comp = feature.getComponent();
            if (comp.getAc() != null){
                parentAcs.add(comp.getAc());
            }

            addParentAcsTo(parentAcs, comp);
        }

        return parentAcs;
    }

    @Override
    public Class<? extends IntactPrimaryObject> getAnnotatedObjectClass() {
        return null;
    }

    @Override
    public boolean isAliasNotEditable(Alias alias) {
        return false;
    }

    @Override
    public boolean isAnnotationNotEditable(Annotation annot) {
        return false;
    }

    @Override
    protected void refreshUnsavedChangesBeforeRevert(){
        Collection<String> parentAcs = new ArrayList<String>();

        if (feature.getComponent() != null){
            Component comp = feature.getComponent();
            if (comp.getAc() != null){
                parentAcs.add(comp.getAc());
            }

            addParentAcsTo(parentAcs, comp);
        }

        getChangesController().revertFeature(feature, parentAcs);
    }

    /**
     * Get the publication ac of this participant if it exists, the ac of the interaction if it exists and the component ac if it exists and add it to the list or parentAcs
     * @param parentAcs
     * @param comp
     */
    private void addParentAcsTo(Collection<String> parentAcs, Component comp) {
        if (comp.getInteraction() != null){
            Interaction inter = comp.getInteraction();
            addParentAcsTo(parentAcs, inter);
        }
    }

    /**
     * Add all the parent acs of this interaction
     * @param parentAcs
     * @param inter
     */
    protected void addParentAcsTo(Collection<String> parentAcs, Interaction inter) {
        if (inter.getAc() != null){
            parentAcs.add(inter.getAc());
        }

        if (IntactCore.isInitialized(inter.getExperiments()) && !inter.getExperiments().isEmpty()){
            for (Experiment exp : inter.getExperiments()){
                addParentAcsTo(parentAcs, exp);
            }
        }
        else if (interactionController.getExperiment() != null){
            Experiment exp = interactionController.getExperiment();
            addParentAcsTo(parentAcs, exp);
        }
        else if (!IntactCore.isInitialized(inter.getExperiments())){
            Collection<Experiment> experiments = IntactCore.ensureInitializedExperiments(inter);

            for (Experiment exp : experiments){
                addParentAcsTo(parentAcs, exp);
            }
        }
    }

    public boolean isRangeDisabled() {
        return isRangeDisabled;
    }

    public void setRangeDisabled(boolean rangeDisabled) {
        isRangeDisabled = rangeDisabled;
    }

    @Override
    public void modifyClone(AnnotatedObject clone) {
        refreshTabs();
    }

    public void onTabChanged(TabChangeEvent e) {

        // the xref tab is active
        super.onTabChanged(e);

        // all the tabs selectOneMenu are disabled, we can process the tabs specific to interaction
        if (isAliasDisabled() && isXrefDisabled() && isAnnotationTopicDisabled()){
            if (e.getTab().getId().equals("rangesTab")){
                isRangeDisabled = false;
            }
            else {
                isRangeDisabled = true;
            }
        }
        else {
            isRangeDisabled = true;
        }
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return null;
    }

    @Override
    public String getObjectName() {
        return null;
    }

    @Override
    public void doSave(boolean refreshCurrentView) {
        ChangesController changesController = (ChangesController) getSpringContext().getBean("changesController");
        PersistenceController persistenceController = getPersistenceController();

        doSaveIntact(refreshCurrentView, changesController, persistenceController);
    }

    @Override
    protected void initialiseDefaultProperties(IntactPrimaryObject annotatedObject) {
        if (!Hibernate.isInitialized(feature.getRanges())
                || !Hibernate.isInitialized(feature.getAnnotations())
                || !Hibernate.isInitialized(feature.getXrefs())
                || !Hibernate.isInitialized(feature.getAliases())){
            feature = loadByAc(getDaoFactory().getFeatureDao(), feature.getAc());
            // initialise ranges
            Hibernate.initialize(feature.getRanges());
            // initialise xrefs
            Hibernate.initialize(feature.getXrefs());
            // initialise aliases
            Hibernate.initialize(feature.getAliases());
            // initialise annotations
            Hibernate.initialize(feature.getAnnotations());
        }

        final Component participant = feature.getComponent();

        if (participantController.getParticipant() == null) {
            participantController.setParticipant(participant);
        }

        if( interactionController.getInteraction() == null ) {
            final Interaction interaction = participant.getInteraction();
            interactionController.setInteraction( interaction );
        }

        if ( publicationController.getPublication() == null ) {
            Publication publication = participant.getInteraction().getExperiments().iterator().next().getPublication();
            publicationController.setPublication( publication );
        }

        if ( experimentController.getExperiment() == null ) {
            experimentController.setExperiment( participant.getInteraction().getExperiments().iterator().next() );
        }

        refreshTabsAndFocusXref();

        refreshRangeWrappers();
    }

    @Override
    public String doSave() {
        return super.doSave();
    }

    @Override
    public void doSaveIfNecessary(ActionEvent evt) {
        super.doSaveIfNecessary(evt);
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public String getCautionMessage() {
        if (feature == null){
            return null;
        }
        if (!Hibernate.isInitialized(feature.getAnnotations())){
            return getAnnotatedObjectHelper().findAnnotationText(getDaoFactory().getFeatureDao().getByAc(feature.getAc()),
                    CvTopic.CAUTION_MI_REF, getDaoFactory());
        }
        return findAnnotationText(CvTopic.CAUTION_MI_REF);
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public String getInternalRemarkMessage() {
        if (feature == null){
            return null;
        }
        if (!Hibernate.isInitialized(feature.getAnnotations())){
            return getAnnotatedObjectHelper().findAnnotationText(getDaoFactory().getFeatureDao().getByAc(feature.getAc()),
                    CvTopic.INTERNAL_REMARK, getDaoFactory());
        }
        return findAnnotationText(CvTopic.INTERNAL_REMARK);
    }
    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public int getFeatureRangeSize() {
        if (feature != null && Hibernate.isInitialized(feature.getRanges())){
            return feature.getRanges().size();
        }
        else if (feature != null){
            return getDaoFactory().getRangeDao().getByFeatureAc(feature.getAc()).size();
        }
        else {
            return 0;
        }
    }


    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public List collectAnnotations() {
        return super.collectAnnotations();
    }

    @Override
    public void newAlias(ActionEvent evt) {

    }

    @Override
    public <T extends AbstractIntactAlias> T newAlias(String alias, String aliasMI, String name) {
        return null;
    }

    @Override
    public void removeAlias(Alias alias) {

    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public List collectAliases() {
        return super.collectAliases();
    }

    @Transactional(value = "transactionManager", readOnly = true, propagation = Propagation.REQUIRED)
    public List collectXrefs() {
        return super.collectXrefs();
    }

    @Override
    public void removeXref(Xref xref) {

    }

    @Override
    public void newAnnotation(ActionEvent evt) {

    }

    @Override
    public <T extends AbstractIntactAnnotation> T newAnnotation(String topic, String topicMI, String text) {
        return null;
    }

    @Override
    public void removeAnnotation(Annotation annotation) {

    }

    public FeatureEditorService getFeatureEditorService() {
        if (this.featureEditorService == null){
            this.featureEditorService = ApplicationContextProvider.getBean("featureEditorService");
        }
        return featureEditorService;
    }

    protected ExperimentController getExperimentController() {
        return experimentController;
    }

    protected PublicationController getPublicationController() {
        return publicationController;
    }

    protected InteractionController getInteractionController() {
        return interactionController;
    }

    protected ParticipantController getParticipantController() {
        return participantController;
    }
}