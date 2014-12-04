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
import org.primefaces.event.TabChangeEvent;
import psidev.psi.mi.jami.exception.IllegalRangeException;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.RangeUtils;
import uk.ac.ebi.intact.editor.controller.curate.AnnotatedObjectController;
import uk.ac.ebi.intact.editor.services.curate.feature.FeatureEditorService;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private List<SelectItem> participantSelectItems;
    private boolean isComplexFeature=false;

    @Resource(name = "featureEditorService")
    private transient FeatureEditorService featureEditorService;

    private Class<T> featureClass;
    private Class<? extends AbstractIntactResultingSequence> resultingSequenceClass;

    public AbstractFeatureController(Class<T> featureClass, Class<? extends AbstractIntactResultingSequence> resultingSeqClass) {
        if (featureClass == null){
             throw new IllegalArgumentException("the feature class cannot be null");
        }
        this.featureClass = featureClass;
        if (resultingSeqClass == null){
            throw new IllegalArgumentException("the resulting sequence class cannot be null");
        }
        this.resultingSequenceClass = resultingSeqClass;
    }

    @Override
    public IntactPrimaryObject getAnnotatedObject() {
        return getFeature();
    }

    @Override
    public void setAnnotatedObject(IntactPrimaryObject annotatedObject) {
         setFeature((T)feature);
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
        String sequence = getSequence();

        if (feature.areRangesInitialized()){
             this.rangeWrappers = new ArrayList<RangeWrapper>(feature.getRanges().size());
            for (Object r : this.feature.getRanges()){
                this.rangeWrappers.add(new RangeWrapper((AbstractIntactRange)r, sequence, getCvService(), resultingSequenceClass));
            }
        }
        else{
            this.rangeWrappers = getFeatureEditorService().loadRangeWrappers(feature, sequence, resultingSequenceClass);
        }

        containsInvalidRanges = false;

        for (RangeWrapper range : this.rangeWrappers) {

            if (!containsInvalidRanges && !range.isValidRange()) {
                containsInvalidRanges = true;
            }
        }
    }

    protected void refreshParticipantSelectItems() {
        participantSelectItems = new ArrayList<SelectItem>();
        participantSelectItems.add(new SelectItem(null, "select participant", "select participant", false, false, true));

        if (this.feature.getParticipant() != null){
            Entity participant = this.feature.getParticipant();
            if (isComplexFeature){
                loadParticipants((Complex)participant.getInteractor(), this.participantSelectItems);
            }
        }
    }

    private void loadParticipants(Complex parent, List<SelectItem> selectItems){
        for (ModelledParticipant child : parent.getParticipants()){
            IntactModelledParticipant part = (IntactModelledParticipant)child;
            if (part.getInteractor() instanceof Complex){
                loadParticipants((Complex)part.getInteractor(), selectItems);
            }
            else{
                SelectItem item = new SelectItem( part, part.getInteractor().getShortName()+", "+part.getAc(), part.getInteractor().getFullName());
                selectItems.add(item);
            }
        }
    }
    public boolean isComplexFeature(){
        return this.isComplexFeature;
    }

    public String newFeature(Participant participant) throws InstantiationException, IllegalAccessException{
        T feature = this.featureClass.newInstance();
        feature.setShortName(null);
        feature.setType(null);
        feature.setParticipant(participant);

        setFeature(feature);
        changed();
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
        try{
            Range newRange = RangeUtils.createRangeFromString(newRangeValue, false);

            IntactPosition start = new IntactPosition(getCvService().findCvObject(IntactUtils.RANGE_STATUS_OBJCLASS, newRange.getStart().getStatus().getMIIdentifier()),
                    newRange.getStart().getStart(), newRange.getStart().getEnd());
            IntactPosition end = new IntactPosition(getCvService().findCvObject(IntactUtils.RANGE_STATUS_OBJCLASS, newRange.getEnd().getStatus().getMIIdentifier()),
                    newRange.getEnd().getStart(), newRange.getEnd().getEnd());

            AbstractIntactRange intactRange = instantiateRange(start, end);
            intactRange.setResultingSequence(instantiateResultingSequence(RangeUtils.extractRangeSequence(intactRange, sequence), null));

            feature.getRanges().add(intactRange);
            this.rangeWrappers.add(new RangeWrapper(intactRange, sequence, getCvService(), this.resultingSequenceClass));
            newRangeValue = null;
            setUnsavedChanges(true);
        }
        catch (IllegalRangeException e){
            String problemMsg =e.getMessage();
            addErrorMessage("Range is not valid: "+newRangeValue, problemMsg);
            return;
        }
    }

    protected abstract <I extends AbstractIntactRange> I instantiateRange(Position start, Position end);

    protected abstract <I extends AbstractIntactResultingSequence> I instantiateResultingSequence(String original, String newSequence);

    public void validateFeature(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (rangeWrappers.isEmpty()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Feature without ranges", "One range is mandatory");
            throw new ValidatorException(message);
        }
    }

    private String getSequence() {
        Interactor interactor = feature.getParticipant() != null ? feature.getParticipant().getInteractor():null;

        String sequence = null;

        if (interactor instanceof Polymer) {
            Polymer polymer = (Polymer) interactor;
            sequence = polymer.getSequence();
        }
        return sequence;
    }

    public void markRangeToDelete(RangeWrapper range) {
        if (range == null) return;

        if (range.getRange().getAc() == null) {
            feature.getRanges().remove(range.getRange());
            rangeWrappers.remove(range);
        } else {
            Collection<String> parents = collectParentAcsOfCurrentAnnotatedObject();
            parents.add(feature.getAc());
            getChangesController().markToDelete(range.getRange(), this.feature, getRangeSynchronzer(), "Range: "+RangeUtils.convertRangeToString(range.getRange()), parents);
        }
    }

    public List<RangeWrapper> getWrappedRanges() {
        return rangeWrappers;
    }

    public String getAc() {
        return ac;
    }

    @Override
    public int getXrefsSize() {
        if (feature == null){
            return 0;
        }
        else if (feature.areXrefsInitialized()){
            return feature.getDbXrefs().size();
        }
        else{
            return getFeatureEditorService().countXrefs(this.feature);
        }
    }

    @Override
    public int getAliasesSize() {
        if (feature == null){
            return 0;
        }
        else if (feature.areAliasesInitialized()){
            return feature.getAliases().size();
        }
        else{
            return getFeatureEditorService().countAliases(this.feature);
        }
    }

    @Override
    public int getAnnotationsSize() {
        if (feature == null){
            return 0;
        }
        else {
            return feature.getAnnotations().size();
        }
    }

    public void setAc( String ac ) {
        this.ac = ac;
    }

    public T getFeature() {
        return feature;
    }

    @Override
    public void refreshTabs(){
        super.refreshTabs();

        this.isRangeDisabled = false;
    }

    public void setFeature( T feature ) {
        this.feature = feature;

        if (feature != null){
            this.ac = feature.getAc();

            initialiseDefaultProperties(this.feature);
        }
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
    public Class<? extends IntactPrimaryObject> getAnnotatedObjectClass() {
        return featureClass;
    }

    @Override
    public boolean isAliasNotEditable(Alias alias) {
        return false;
    }

    @Override
    public boolean isAnnotationNotEditable(Annotation annot) {
        return false;
    }

    public boolean isRangeDisabled() {
        return isRangeDisabled;
    }

    public void setRangeDisabled(boolean rangeDisabled) {
        isRangeDisabled = rangeDisabled;
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
    protected void initialiseDefaultProperties(IntactPrimaryObject annotatedObject) {
        T feature = (T) annotatedObject;
        if (!feature.areAnnotationsInitialized()
                || !isCvInitialised(feature.getType())
                || !isCvInitialised(feature.getRole())
                || !isInitialisedParticipant(feature.getParticipant())) {
            this.feature = getFeatureEditorService().reloadFullyInitialisedFeature(feature);
        }

        if (feature.getParticipant() != null && feature.getParticipant().getInteractor() instanceof Complex){
            isComplexFeature = true;
            refreshParticipantSelectItems();
        }
        else{
            isComplexFeature = false;
            this.participantSelectItems=Collections.EMPTY_LIST;
        }

        refreshRangeWrappers();
    }

    private boolean isCvInitialised(CvTerm cv) {
        if (cv instanceof IntactCvTerm){
            IntactCvTerm intactCv = (IntactCvTerm)cv;
            return intactCv.areXrefsInitialized() && intactCv.areAnnotationsInitialized();
        }
        return true;
    }


    private boolean isInitialisedParticipant(Entity entity) {
        if (entity != null && entity.getInteractor() instanceof IntactComplex){
            IntactComplex intactComplex = (IntactComplex) entity.getInteractor();
            if (!intactComplex.areParticipantsInitialized()){
                return false;
            }
            else{
                for (ModelledParticipant p : intactComplex.getParticipants()){
                     if (!isInitialisedParticipant(p)){
                          return false;
                     }
                }
            }
        }
        return true;
    }

    public int getFeatureRangeSize() {
        if (feature == null){
            return 0;
        }
        else if (feature.areRangesInitialized()){
            return feature.getRanges().size();
        }
        else{
            return getFeatureEditorService().countRanges(this.feature);
        }
    }

    public List<Annotation> collectAnnotations() {
        List<Annotation> annotations = new ArrayList<Annotation>(feature.getAnnotations());
        Collections.sort(annotations, new AuditableComparator());
        // annotations are always initialised
        return annotations;
    }

    @Override
    public void removeAlias(Alias alias) {
        // aliases are not always initialised
        if (!feature.areAliasesInitialized()){
            setFeature(getFeatureEditorService().initialiseFeatureAliases(this.feature));
        }

        this.feature.getAliases().remove(alias);
    }

    public List<Alias> collectAliases() {
        // aliases are not always initialised
        if (!feature.areAliasesInitialized()){
            setFeature(getFeatureEditorService().initialiseFeatureAliases(this.feature));
        }

        List<Alias> aliases = new ArrayList<Alias>(this.feature.getAliases());
        Collections.sort(aliases, new AuditableComparator());
        return aliases;
    }

    public List<Xref> collectXrefs() {
        // aliases are not always initialised
        if (!feature.areXrefsInitialized()){
            setFeature(getFeatureEditorService().initialiseFeatureXrefs(this.feature));
        }

        List<Xref> xrefs = new ArrayList<Xref>(this.feature.getDbXrefs());
        Collections.sort(xrefs, new AuditableComparator());
        return xrefs;
    }

    @Override
    public void removeXref(Xref xref) {
        // xrefs are not always initialised
        if (!feature.areXrefsInitialized()){
            setFeature(getFeatureEditorService().initialiseFeatureXrefs(this.feature));
        }

        this.feature.getDbXrefs().remove(xref);
    }

    @Override
    public void removeAnnotation(Annotation annotation) {
        feature.getAnnotations().remove(annotation);
    }

    public FeatureEditorService getFeatureEditorService() {
        if (this.featureEditorService == null){
            this.featureEditorService = ApplicationContextProvider.getBean("featureEditorService");
        }
        return featureEditorService;
    }

    public List<SelectItem> getParticipantSelectItems() {
        return participantSelectItems;
    }

    protected abstract IntactDbSynchronizer getRangeSynchronzer();
}