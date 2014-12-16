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
package uk.ac.ebi.intact.editor.controller.curate.interaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.primefaces.event.TabChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.editor.controller.UserSessionController;
import uk.ac.ebi.intact.editor.controller.curate.AnnotatedObjectController;
import uk.ac.ebi.intact.editor.controller.curate.ChangesController;
import uk.ac.ebi.intact.editor.controller.curate.UnsavedChange;
import uk.ac.ebi.intact.editor.controller.curate.cloner.EditorCloner;
import uk.ac.ebi.intact.editor.controller.curate.cloner.InteractionEvidenceCloner;
import uk.ac.ebi.intact.editor.controller.curate.cloner.ParticipantEvidenceCloner;
import uk.ac.ebi.intact.editor.controller.curate.experiment.ExperimentController;
import uk.ac.ebi.intact.editor.controller.curate.publication.PublicationController;
import uk.ac.ebi.intact.editor.controller.curate.util.ParticipantWrapperExperimentalRoleComparator;
import uk.ac.ebi.intact.editor.services.curate.interaction.InteractionEditorService;
import uk.ac.ebi.intact.editor.services.summary.ExperimentSummary;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.util.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "general" )
public class InteractionController extends AnnotatedObjectController {

    private static final Log log = LogFactory.getLog( InteractionController.class );

    private static final String FIG_LEGEND = "MI:0599";

    private IntactInteractionEvidence interaction;
    private String ac;

    private List<SelectItem> experimentSelectItems;

    private LinkedList<ParticipantWrapper> participantWrappers;

    private IntactExperiment experiment;

    private String experimentToCopyTo;
    private String experimentToMoveTo;

    @Autowired
    private PublicationController publicationController;

    @Autowired
    private ExperimentController experimentController;

    @Autowired
    private UserSessionController userSessionController;

    @Autowired
    private ChangesController changesController;

    @Resource(name = "interactionEditorService")
    private transient InteractionEditorService interactionEditorService;

    private boolean isParticipantDisabled;
    private boolean isParameterDisabled;
    private boolean isConfidenceDisabled;
    private boolean isVariableParametersDisabled;

    private String imexId;
    private String figureLegend = null;

    private List<ImportExperimentalCondition> conditionsToImport;

    public InteractionController() {
    }

    @Override
    public IntactPrimaryObject getAnnotatedObject() {
        return getInteraction();
    }

    @Override
    public void setAnnotatedObject(IntactPrimaryObject annotatedObject) {
        setInteraction((IntactInteractionEvidence)annotatedObject);
    }

    @Override
    protected AnnotatedObjectController getParentController() {
        return experimentController;
    }

    @Override
    protected String getPageContext() {
        return "interaction";
    }

    @Override
    protected void loadCautionMessages() {
        if (this.interaction != null){
            if (!interaction.areAnnotationsInitialized()){
                setInteraction(getInteractionEditorService().initialiseInteractionAnnotations(this.interaction));
            }
            if (!interaction.areXrefsInitialized()){
                setInteraction(getInteractionEditorService().initialiseInteractionXrefs(this.interaction));
            }

            Annotation caution = AnnotationUtils.collectFirstAnnotationWithTopic(this.interaction.getDbAnnotations(), psidev.psi.mi.jami.model.Annotation.CAUTION_MI, psidev.psi.mi.jami.model.Annotation.CAUTION);
            setCautionMessage(caution != null ? caution.getValue() : null);
            Annotation internal = AnnotationUtils.collectFirstAnnotationWithTopic(this.interaction.getDbAnnotations(), null, "remark-internal");
            setInternalRemark(internal != null ? internal.getValue() : null);
            this.imexId = this.interaction.getImexId();
            Annotation legend = AnnotationUtils.collectFirstAnnotationWithTopic(this.interaction.getDbAnnotations(), Annotation.FIGURE_LEGEND_MI, Annotation.FIGURE_LEGEND);
            this.figureLegend = legend != null ? legend.getValue() : null;
        }
    }

    @Override
    public void refreshTabs(){
        super.refreshTabs();

        isParticipantDisabled = false;
        isParameterDisabled = true;
        isConfidenceDisabled = true;
        isVariableParametersDisabled = true;
    }

    public void loadData( ComponentSystemEvent event ) {
        if (!FacesContext.getCurrentInstance().isPostback()) {

            if ( ac != null ) {
                if ( interaction == null || !ac.equals( interaction.getAc() ) ) {
                    setInteraction(getInteractionEditorService().loadInteractionByAc(ac));
                }
            } else {
                if ( interaction != null ) ac = interaction.getAc();
            }

            if (interaction == null) {
                addErrorMessage("No interaction with this AC", ac);
                return;
            }

            refreshParentControllers();
            refreshTabs();
        }

        generalLoadChecks();
    }

    @Override
    protected void generalLoadChecks() {
        super.generalLoadChecks();
        generalPublicationLoadChecks();
    }

    private void refreshParentControllers() {
        // different loaded experiment
        if (experimentController.getExperiment() != interaction.getExperiment()){
            // different participant to load
            if (experimentController.getAc() == null ||
                    (interaction.getExperiment() instanceof IntactExperiment
                            && !experimentController.getAc().equals(((IntactExperiment)interaction.getExperiment()).getAc()))){
                IntactExperiment intactExperiment = (IntactExperiment)interaction.getExperiment();
                experimentController.setExperiment(intactExperiment);

                if ( intactExperiment.getPublication() instanceof IntactPublication ) {
                    IntactPublication publication = (IntactPublication)intactExperiment.getPublication();
                    publicationController.setPublication( publication );
                }
                else{
                    publicationController.setPublication(null);
                }
            }
            // replace old experiment instance
            else{
                interaction.setExperiment(experimentController.getExperiment());
                experimentController.reloadSingleInteractionEvidence(this.interaction);
            }
        }
    }

    public void refreshExperimentLists() {
        this.experimentSelectItems = new ArrayList<SelectItem>();

        SelectItem selectItem = new SelectItem(null, "Select experiment");
        selectItem.setNoSelectionOption(true);

        experimentSelectItems.add(selectItem);

        if (interaction.getExperiment() != null) {
            experiment = (IntactExperiment)interaction.getExperiment();
        }

        if (publicationController.getPublication() != null) {
            List<ExperimentSummary> experiments = publicationController.collectExperiments();

            // publication does have experiments so we can propose them
            if (!experiments.isEmpty()){
                for ( ExperimentSummary e : experiments ) {
                    String description = completeExperimentLabel((IntactExperiment)e.getExperiment());
                    experimentSelectItems.add(new SelectItem(e, description, publicationController.getTitle()));
                }
            }
        }
    }

    public String completeExperimentLabel(IntactExperiment e) {
        return e.getShortLabel()+" | "+
                e.getInteractionDetectionMethod().getShortName()+
                (e.getParticipantIdentificationMethod() != null? e.getParticipantIdentificationMethod().getShortName()+", " : "")+
                (e.getHostOrganism() != null? e.getHostOrganism().getCommonName() : "");
    }

    @Override
    public void doPostSave(){
        // the interaction was just created, add it to the list of interactions of the experiment
        if (interaction.getExperiment() != null){
            experimentController.reloadSingleInteractionEvidence(interaction);
        }
    }

    @Override
    public String doDelete() {
        experimentController.removeInteractionEvidence(interaction);
        return super.doDelete();
    }

    @Override
    protected void postProcessDeletedEvent(UnsavedChange unsaved) {
        super.postProcessDeletedEvent(unsaved);
        if (unsaved.getUnsavedObject() instanceof IntactParticipantEvidence){
            removeParticipant((IntactParticipantEvidence) unsaved.getUnsavedObject());
        }
    }

    @Override
    public void doPreSave() {
        // create master proteins from the unsaved manager
        final List<UnsavedChange> transcriptCreated = super.getChangesController().getAllUnsavedProteinTranscripts();
        String currentAc = interaction != null ? interaction.getAc() : null;

        for (UnsavedChange unsaved : transcriptCreated) {
            IntactInteractor transcript = (IntactInteractor)unsaved.getUnsavedObject();

            // the object to save is different from the current object. Checks that the scope of this object to save is the ac of the current object being saved
            // if the scope is null or different, the object should not be saved at this stage because we only save the current object and changes associated with it
            // if current ac is null, no unsaved event should be associated with it as this object has not been saved yet
            if (unsaved.getScope() != null && unsaved.getScope().equals(currentAc)){
                try {
                    getEditorService().doSaveMasterProteins(transcript);
                } catch (BridgeFailedException e) {
                    addErrorMessage("Cannot save master protein " + transcript.toString(), e.getCause() + ": " + e.getMessage());
                } catch (SynchronizerException e) {
                    addErrorMessage("Cannot save master protein " + transcript.toString(), e.getCause() + ": " + e.getMessage());
                } catch (FinderException e) {
                    addErrorMessage("Cannot save master protein " + transcript.toString(), e.getCause() + ": " + e.getMessage());
                } catch (PersisterException e) {
                    addErrorMessage("Cannot save master protein " + transcript.toString(), e.getCause() + ": " + e.getMessage());
                }

                getChangesController().removeFromHiddenChanges(unsaved);

            }
            else if (unsaved.getScope() == null && currentAc == null){
                try {
                    getEditorService().doSaveMasterProteins(transcript);
                } catch (BridgeFailedException e) {
                    addErrorMessage("Cannot save master protein " + transcript.toString(), e.getCause() + ": " + e.getMessage());
                } catch (SynchronizerException e) {
                    addErrorMessage("Cannot save master protein " + transcript.toString(), e.getCause() + ": " + e.getMessage());
                } catch (FinderException e) {
                    addErrorMessage("Cannot save master protein " + transcript.toString(), e.getCause() + ": " + e.getMessage());
                } catch (PersisterException e) {
                    addErrorMessage("Cannot save master protein " + transcript.toString(), e.getCause() + ": " + e.getMessage());
                }
                getChangesController().removeFromHiddenChanges(unsaved);
            }
        }
    }

    public void markParticipantToDelete(IntactParticipantEvidence component) {
        if (component == null) return;

        if (component.getAc() == null) {
            interaction.removeParticipant(component);
            refreshParticipants();
        } else {
            Collection<String> parents = collectParentAcsOfCurrentAnnotatedObject();
            if (this.interaction.getAc() != null){
                parents.add(this.interaction.getAc());
            }
            getChangesController().markToDelete(component, this.interaction, getEditorService().getIntactDao().getSynchronizerContext().getParticipantEvidenceSynchronizer(),
                    "participant "+component.getAc(), parents);
        }
    }

    public void experimentChanged(AjaxBehaviorEvent evt) {
        interaction.setExperiment(experiment);

        refreshParentControllers();

        changed();
    }

    public String newInteraction(IntactPublication publication, IntactExperiment exp) {
        IntactInteractionEvidence interaction = new IntactInteractionEvidence();
        interaction.setExperiment(exp != null ? exp : new IntactExperiment(publication));

        setInteraction(interaction);

        changed();

        return navigateToObject(interaction);
    }

    @Override
    protected EditorCloner<InteractionEvidence, IntactInteractionEvidence> newClonerInstance() {
        return new InteractionEvidenceCloner();
    }

    @Override
    public void newXref(ActionEvent evt) {

        this.interaction.getDbXrefs().add(new InteractionXref(IntactUtils.createMIDatabase("to set", null), "to set"));
        setUnsavedChanges(true);
    }

    @Override
    public InteractionXref newXref(String db, String dbMI, String id, String secondaryId, String qualifier, String qualifierMI) {
        return new InteractionXref(getCvService().findCvObject(IntactUtils.DATABASE_OBJCLASS, dbMI != null ? dbMI : db),
                id, secondaryId, getCvService().findCvObject(IntactUtils.QUALIFIER_OBJCLASS, qualifierMI != null ? qualifierMI : qualifier));
    }

    public void refreshParticipants() {
        participantWrappers = new LinkedList<ParticipantWrapper>();

        final Collection<ParticipantEvidence> components = interaction.getParticipants();

        for ( ParticipantEvidence component : components ) {
            participantWrappers.add( new ParticipantWrapper( (IntactParticipantEvidence)component) );
        }

        if (participantWrappers.size() > 0) {
            //We sort the participants for avoiding confusion with the place that a new participant should be appeared.
            Collections.sort(participantWrappers, new ParticipantWrapperExperimentalRoleComparator());
        }
    }

    public void addParticipant(IntactParticipantEvidence component) {
        interaction.addParticipant(component);

        participantWrappers.add(new ParticipantWrapper(component));

        if (participantWrappers.size() > 0) {
            try {
                updateShortLabel();
            } catch (Exception e) {
                addErrorMessage("Problem updating shortLabel", e.getMessage());
            }
            Collections.sort(participantWrappers, new ParticipantWrapperExperimentalRoleComparator());

        }

        setUnsavedChanges(true);
    }

    @Override
    public Collection<String> collectParentAcsOfCurrentAnnotatedObject(){
        Collection<String> parentAcs = new ArrayList<String>();

        addParentAcsTo(parentAcs, (IntactExperiment)interaction.getExperiment());

        return parentAcs;
    }

    @Override
    public Class<? extends IntactPrimaryObject> getAnnotatedObjectClass() {
        return IntactInteractionEvidence.class;
    }

    @Override
    public boolean isAliasNotEditable(Alias alias) {
        return false;
    }

    @Override
    public boolean isAnnotationNotEditable(psidev.psi.mi.jami.model.Annotation annot) {
        return AnnotationUtils.doesAnnotationHaveTopic(annot, Annotation.FIGURE_LEGEND_MI, Annotation.FIGURE_LEGEND);
    }

    @Override
    public boolean isXrefNotEditable(Xref ref) {
        return false;
    }

    public String copyToExperiment() {
        IntactInteractionEvidence newInteraction = null;

        if (experimentToCopyTo != null && !experimentToCopyTo.isEmpty()) {
            Experiment experiment = getInteractionEditorService().loadExperimentByAcOrLabel(experimentToCopyTo);

            if (experiment == null) {
                addErrorMessage("Cannot copy", "No experiment found with this AC or short label: "+experimentToCopyTo);
                return null;
            }

            newInteraction = cloneAnnotatedObject(interaction, new InteractionEvidenceCloner());
            newInteraction.setExperiment(experiment);
        } else {
            return null;
        }

        setInteraction(newInteraction);
        setUnsavedChanges(true);

        addInfoMessage("Copied interaction", "To experiment: "+experimentToCopyTo);

        return getCurateController().edit(newInteraction);
    }

    public String moveToExperiment() {
        if (experimentToMoveTo != null && !experimentToMoveTo.isEmpty()) {
            Experiment experiment = getInteractionEditorService().loadExperimentByAcOrLabel(experimentToCopyTo);

            if (experiment == null) {
                addErrorMessage("Cannot move", "No experiment found with this AC or short label: "+experimentToMoveTo);
                return null;
            }

            // set experiment
            interaction.setExperiment(experiment);

        } else {
            return null;
        }

        setInteraction(interaction);

        setUnsavedChanges(true);

        addInfoMessage("Moved interaction", "To experiment: "+experimentToMoveTo);

        return null;
    }

    public String getAc() {
        return ac;
    }

    @Override
    public int getXrefsSize() {
        if (interaction == null){
            return 0;
        }
        else if (interaction.areXrefsInitialized()){
            return interaction.getDbXrefs().size();
        }
        else{
            return getInteractionEditorService().countXrefs(this.interaction);
        }
    }

    @Override
    public int getAliasesSize() {
        return 0;
    }

    @Override
    public int getAnnotationsSize() {
        if (interaction == null){
            return 0;
        }
        else {
            return interaction.getDbAnnotations().size();
        }
    }

    public int getParticipantsSize() {
        if (interaction == null){
            return 0;
        }
        else {
            return interaction.getParticipants().size();
        }
    }

    public int getConfidencesSize() {
        if (interaction == null){
            return 0;
        }
        else if (interaction.areConfidencesInitialized()){
            return interaction.getConfidences().size();
        }
        else{
            return getInteractionEditorService().countConfidences(this.interaction);
        }
    }

    public int getParametersSize() {
        if (interaction == null){
            return 0;
        }
        else if (interaction.areParametersInitialized()){
            return interaction.getParameters().size();
        }
        else{
            return getInteractionEditorService().countParameters(this.interaction);
        }
    }

    public int getVariableParameterValuesSize() {
        if (interaction == null){
            return 0;
        }
        else if (interaction.areVariableParameterValuesInitialized()){
            return interaction.getVariableParameterValues().size();
        }
        else{
            return getInteractionEditorService().countVariableParameterValues(this.interaction);
        }
    }

    public int countParticipantsByInteraction( IntactInteractionEvidence interaction) {
        return getInteractionEditorService().countParticipants(interaction);
    }

    public void updateShortLabel() {
        updateShortLabel(this.interaction);
    }

    private void updateShortLabel(IntactInteractionEvidence interaction) {

        if (interaction.getParticipants().isEmpty()){
            return;
        }

        String oldLabel = interaction.getShortName();
        String shortLabel = IntactUtils.generateAutomaticInteractionEvidenceShortlabelFor(interaction, IntactUtils.MAX_SHORT_LABEL_LEN);
        interaction.setShortName(shortLabel);
        if (oldLabel == null || !oldLabel.equals(shortLabel)){
            Collection<String> parentsCs = new ArrayList<String>();
            if (interaction.getExperiment() != null && ((IntactExperiment)interaction.getExperiment()).getAc() != null){
                parentsCs.add(((IntactExperiment)interaction.getExperiment()).getAc());
            }
            super.addParentAcsTo(parentsCs, (IntactExperiment)interaction.getExperiment());
            changesController.markAsUnsaved(interaction, getEditorService().getIntactDao().getSynchronizerContext().getInteractionSynchronizer(),
                    "Interaction "+oldLabel,
                    parentsCs);
        }
    }

    public void cloneParticipant(ParticipantWrapper participantWrapper) {

        ParticipantEvidenceCloner cloner = new ParticipantEvidenceCloner();

        IntactParticipantEvidence clone = getEditorService().cloneAnnotatedObject((IntactParticipantEvidence)participantWrapper.getParticipant(), cloner);
        addParticipant(clone);
    }

    public void linkSelectedFeatures(ActionEvent evt) {
        List<AbstractIntactFeature> selected = new ArrayList<AbstractIntactFeature>();

        for (ParticipantWrapper pw : participantWrappers) {
            for (FeatureWrapper fw : pw.getFeatures()) {
                if (fw.isSelected()) {
                    selected.add(fw.getFeature());
                }
            }
        }

        Iterator<AbstractIntactFeature> fIterator1 = selected.iterator();
        while (fIterator1.hasNext()){
            AbstractIntactFeature f1 = fIterator1.next();

            for (AbstractIntactFeature f2 : selected){
                if (f1.getAc() == null && f1 != f2){
                    f1.getLinkedFeatures().add(f2);
                    f2.getLinkedFeatures().add(f1);
                }
                else if (f1.getAc() != null && !f1.getAc().equals(f2.getAc())){
                    f1.getLinkedFeatures().add(f2);
                    f2.getLinkedFeatures().add(f1);
                }
            }
            fIterator1.remove();
        }


        addInfoMessage("Features linked", "Size of linked features : "+selected.size());
        setUnsavedChanges(true);
        refreshParticipants();
    }

    public void unlinkFeature(FeatureWrapper wrapper) {
        AbstractIntactFeature feature1 = wrapper.getFeature();
        AbstractIntactFeature feature2 = wrapper.getSelectedLinkedFeature();
        if (feature2 != null){
            Iterator<Feature> featureIterator = feature1.getLinkedFeatures().iterator();
            Iterator<Feature> feature2Iterator = feature2.getLinkedFeatures().iterator();
            while (featureIterator.hasNext()){
                AbstractIntactFeature f1 = (AbstractIntactFeature)featureIterator.next();
                if (f1.getAc() == null && f1 == feature2){
                    featureIterator.remove();
                }
                else if (f1.getAc() != null && f1.getAc().equals(feature2.getAc())){
                    featureIterator.remove();
                }
            }
            while (feature2Iterator.hasNext()){
                AbstractIntactFeature f2 = (AbstractIntactFeature)feature2Iterator.next();
                if (f2.getAc() == null && f2 == feature1){
                    feature2Iterator.remove();
                }
                else if (f2.getAc() != null && f2.getAc().equals(feature1.getAc())){
                    featureIterator.remove();
                }
            }

            addInfoMessage("Feature unlinked", feature2.toString());
            setUnsavedChanges(true);
            refreshParticipants();
        }
    }

    public void selectLinkedFeature(FeatureWrapper wrapper, IntactFeatureEvidence linked){
        wrapper.setSelectedLinkedFeature(linked);
    }

    public String getImexId() {
        return imexId;
    }

    public void setAc( String ac ) {
        this.ac = ac;
    }

    public List<SelectItem> getExperimentSelectItems() {
        return experimentSelectItems;
    }

    public IntactInteractionEvidence getInteraction() {
        return interaction;
    }

    public IntactExperiment getExperiment() {
        return experiment;
    }

    public void setExperiment(IntactExperiment experiment) {
        this.experiment = experiment;
    }

    public void setInteraction( IntactInteractionEvidence interaction ) {
        this.interaction = interaction;

        if (interaction != null) {
            this.ac = interaction.getAc();

            initialiseDefaultProperties(this.interaction);
        }
        else{
            this.ac = null;
        }
    }

    public Collection<ParticipantWrapper> getParticipants() {
        return participantWrappers;
    }

    public String getExperimentToMoveTo() {
        return experimentToMoveTo;
    }

    public void setExperimentToMoveTo(String experimentToMoveTo) {
        this.experimentToMoveTo = experimentToMoveTo;
    }

    public String getExperimentToCopyTo() {
        return experimentToCopyTo;
    }

    public void setExperimentToCopyTo(String experimentToCopyTo) {
        this.experimentToCopyTo = experimentToCopyTo;
    }

    public String getFigureLegend() {
        return this.figureLegend;
    }

    public void setFigureLegend(String figureLegend) {
        this.figureLegend = figureLegend;
    }

    public void onFigureLegendChanged(ValueChangeEvent evt) {
        setUnsavedChanges(true);
        if (this.figureLegend != null && figureLegend.length() > 0){
            updateAnnotation(Annotation.FIGURE_LEGEND, Annotation.FIGURE_LEGEND_MI, figureLegend, interaction.getDbAnnotations());
        }
        else{
            removeAnnotation(Annotation.FIGURE_LEGEND, Annotation.FIGURE_LEGEND_MI, interaction.getDbAnnotations());
        }
    }

    //////////////////////////////////
    // Participant related methods

    public String getInteractorIdentity(IntactInteractor interactor) {
        if (interactor == null) return null;

        if (!interactor.areXrefsInitialized()){
            interactor = getInteractionEditorService().reloadFullyInitialisedInteractor(interactor);
        }

        final Collection<Xref> identities = interactor.getIdentifiers();
        StringBuilder sb = new StringBuilder(64);
        for ( Iterator<Xref> iterator = identities.iterator(); iterator.hasNext(); ) {
            Xref xref = iterator.next();
            sb.append( xref.getId() );
            if( iterator.hasNext() ) {
                sb.append( "|" );
            }
        }
        return sb.toString();
    }

    // Confidence
    ///////////////////////////////////////////////

    public void newConfidence() {
        if (!interaction.areConfidencesInitialized()){
            setInteraction(getInteractionEditorService().initialiseInteractionConfidences(interaction));
        }
        InteractionEvidenceConfidence confidence = new InteractionEvidenceConfidence(IntactUtils.createMIConfidenceType("to set", null), "to set");
        interaction.getConfidences().add(confidence);
        setUnsavedChanges(true);
    }

    public List<Confidence> collectConfidences() {
        if (!interaction.areConfidencesInitialized()){
            setInteraction(getInteractionEditorService().initialiseInteractionConfidences(interaction));
        }
        final List<Confidence> confidences = new ArrayList<Confidence>( interaction.getConfidences() );
        Collections.sort( confidences, new AuditableComparator() );
        return confidences;
    }

    public List<Parameter> collectParameters() {
        if (!interaction.areParametersInitialized()){
            setInteraction(getInteractionEditorService().initialiseInteractionParameters(interaction));
        }
        final List<Parameter> parameters = new ArrayList<Parameter>( interaction.getParameters() );
        Collections.sort( parameters, new AuditableComparator() );
        return parameters;
    }

    public boolean isParticipantDisabled() {
        return isParticipantDisabled;
    }

    public void setParticipantDisabled(boolean participantDisabled) {
        isParticipantDisabled = participantDisabled;
    }

    public boolean isParameterDisabled() {
        return isParameterDisabled;
    }

    public void setParameterDisabled(boolean parameterDisabled) {
        isParameterDisabled = parameterDisabled;
    }

    public boolean isConfidenceDisabled() {
        return isConfidenceDisabled;
    }

    public void setConfidenceDisabled(boolean confidenceDisabled) {
        isConfidenceDisabled = confidenceDisabled;
    }

    public boolean isVariableParametersDisabled() {
        return isVariableParametersDisabled;
    }

    public void setVariableParametersDisabled(boolean isVariableParametersDisabled) {
        this.isVariableParametersDisabled = isVariableParametersDisabled;
    }

    public boolean isVariableParametersTableEnabled(){
        List<VariableParameter> variableParameters = experimentController.collectVariableParameters();
        return !variableParameters.isEmpty();
    }

    public void onTabChanged(TabChangeEvent e) {

        // the xref tab is active
        super.onTabChanged(e);

        // all the tabs selectOneMenu are disabled, we can process the tabs specific to interaction
        if (isAliasDisabled() && isXrefDisabled() && isAnnotationTopicDisabled()){
            if (e.getTab().getId().equals("participantsTab")){
                isParticipantDisabled = false;
                isParameterDisabled = true;
                isConfidenceDisabled = true;
                isVariableParametersDisabled = true;
            }
            else if (e.getTab().getId().equals("parametersTab")){
                isParticipantDisabled = true;
                isParameterDisabled = false;
                isConfidenceDisabled = true;
                isVariableParametersDisabled = true;
            }
            else if (e.getTab().getId().equals("confidencesTab")){
                isParticipantDisabled = true;
                isParameterDisabled = true;
                isConfidenceDisabled = false;
                isVariableParametersDisabled = true;
            }
            else if (e.getTab().getId().equals("vparametersTab")){
                loadConditionsToImport();
                isParticipantDisabled = true;
                isParameterDisabled = true;
                isConfidenceDisabled = true;
                isVariableParametersDisabled = false;
            }
            else {
                isParticipantDisabled = true;
                isParameterDisabled = true;
                isConfidenceDisabled = true;
                isVariableParametersDisabled = true;
            }
        }
        else {
            isParticipantDisabled = true;
            isParameterDisabled = true;
            isConfidenceDisabled = true;
            isVariableParametersDisabled = true;
        }
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getEditorService().getIntactDao().getSynchronizerContext().getInteractionSynchronizer();
    }

    @Override
    public String getObjectName() {
        return interaction != null ? interaction.getShortName():null;
    }

    @Override
    protected boolean areXrefsInitialised() {
        return interaction.areXrefsInitialized();
    }

    @Override
    protected void initialiseDefaultProperties(IntactPrimaryObject annotatedObject) {
        IntactInteractionEvidence interaction = (IntactInteractionEvidence)annotatedObject;
        if (!interaction.areAnnotationsInitialized()
                || !interaction.areXrefsInitialized()
                || !isCvInitialised(interaction.getInteractionType())
                || !areParticipantsInitialised(interaction)
                || !isExperimentInitialised((IntactExperiment)interaction.getExperiment())){
            this.interaction = getInteractionEditorService().reloadFullyInitialisedInteraction(interaction);
        }

        refreshExperimentLists();
        refreshParticipants();
    }

    private boolean areParticipantsInitialised(IntactInteractionEvidence interaction) {
        if (!interaction.areParticipantsInitialized()){
            return false;
        }

        for (ParticipantEvidence part : interaction.getParticipants()){
            IntactInteractor interactor = (IntactInteractor)part.getInteractor();
            if (!interactor.areXrefsInitialized() || !interactor.areAnnotationsInitialized()){
                return false;
            }

            if (!isCvInitialised(part.getExperimentalRole())){
                return false;
            }
            if (!isCvInitialised(part.getBiologicalRole())){
                return false;
            }
            if (!((IntactParticipantEvidence)part).areFeaturesInitialized()){
                return false;
            }
            for (FeatureEvidence f : part.getFeatures()){
                if (!((IntactFeatureEvidence)f).areRangesInitialized()){
                    return false;
                }
                if (!((IntactFeatureEvidence)f).areLinkedFeaturesInitialized()){
                    return false;
                }
                for (Range obj : f.getRanges()){
                    if (!isCvInitialised(obj.getStart().getStatus())
                            || !isCvInitialised(obj.getEnd().getStatus())){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isCvInitialised(CvTerm cv) {
        if (cv instanceof IntactCvTerm){
            IntactCvTerm intactCv = (IntactCvTerm)cv;
            return intactCv.areXrefsInitialized() && intactCv.areAnnotationsInitialized();
        }
        return true;
    }

    private boolean isExperimentInitialised(IntactExperiment exp) {
        return isCvInitialised(exp.getParticipantIdentificationMethod());
    }

    public List<Annotation> collectAnnotations() {
        List<Annotation> annotations = new ArrayList<Annotation>(interaction.getDbAnnotations());
        Collections.sort(annotations, new AuditableComparator());
        // annotations are always initialised
        return annotations;
    }

    @Override
    public void newAlias(ActionEvent evt) {
        // nothing to do
    }

    @Override
    public <T extends AbstractIntactAlias> T newAlias(String alias, String aliasMI, String name) {
        return null;
    }

    @Override
    public void removeAlias(Alias alias) {
        // nothing to do
    }

    public List<Alias> collectAliases() {
        return Collections.EMPTY_LIST;
    }

    public List<Xref> collectXrefs() {
        List<Xref> xrefs = new ArrayList<Xref>(this.interaction.getDbXrefs());
        Collections.sort(xrefs, new AuditableComparator());
        return xrefs;
    }

    @Override
    public void removeXref(Xref xref) {
        if (XrefUtils.isXrefFromDatabase(xref, Xref.IMEX_MI, Xref.IMEX)
                && XrefUtils.doesXrefHaveQualifier(xref, Xref.IMEX_PRIMARY_MI, Xref.IMEX_PRIMARY)
                && xref.getId().equals(imexId)){
            imexId = null;
            interaction.getXrefs().remove(xref);
        }
        this.interaction.getDbXrefs().remove(xref);
    }

    @Override
    public void newAnnotation(ActionEvent evt) {
        interaction.getDbAnnotations().add(new InteractionAnnotation(IntactUtils.createMITopic("to set", null)));
        setUnsavedChanges(true);
    }

    @Override
    public InteractionAnnotation newAnnotation(String topic, String topicMI, String text) {
        return new InteractionAnnotation(getCvService().findCvObject(IntactUtils.TOPIC_OBJCLASS, topicMI != null ? topicMI: topic), text);
    }

    @Override
    public void removeAnnotation(psidev.psi.mi.jami.model.Annotation annotation) {
        interaction.getDbAnnotations().remove(annotation);
    }

    public void reloadSingleParticipant(IntactParticipantEvidence f){
        if (!this.interaction.areParticipantsInitialized()){
            setInteraction(getInteractionEditorService().initialiseParticipants(this.interaction));
        }
        Iterator<ParticipantEvidence> evIterator = interaction.getParticipants().iterator();
        boolean add = true;
        while (evIterator.hasNext()){
            IntactParticipantEvidence intactEv = (IntactParticipantEvidence)evIterator.next();
            if (intactEv.getAc() == null && f == intactEv){
                add = false;
            }
            else if (intactEv.getAc() != null && !intactEv.getAc().equals(f.getAc())){
                evIterator.remove();
            }
        }

        if (add){
            interaction.getParticipants().add(f);
        }

        refreshParticipants();
    }

    public void removeParticipant(IntactParticipantEvidence f){
        if (!this.interaction.areParticipantsInitialized()){
            setInteraction(getInteractionEditorService().initialiseParticipants(this.interaction));
        }
        Iterator<ParticipantEvidence> evIterator = interaction.getParticipants().iterator();
        while (evIterator.hasNext()){
            IntactParticipantEvidence intactEv = (IntactParticipantEvidence)evIterator.next();
            if (intactEv.getAc() == null && f == intactEv){
                evIterator.remove();
            }
            else if (intactEv.getAc() != null && !intactEv.getAc().equals(f.getAc())){
                evIterator.remove();
            }
        }

        refreshParticipants();
    }

    public InteractionEditorService getInteractionEditorService() {
        if (this.interactionEditorService == null){
            this.interactionEditorService = ApplicationContextProvider.getBean("interactionEditorService");
        }
        return interactionEditorService;
    }

    public void loadConditionsToImport(){
        List<VariableParameter> params = experimentController.getExperiment() != null ? experimentController.collectVariableParameters() : Collections.EMPTY_LIST;

        this.conditionsToImport = new ArrayList<ImportExperimentalCondition>();
        for (VariableParameter param : params){
             this.conditionsToImport.add(new ImportExperimentalCondition(param));
        }
    }

    public List<ImportExperimentalCondition> getConditionsToImport() {
        return conditionsToImport;
    }

    public void importExperimentalConditions(ActionEvent evt){
        if (this.conditionsToImport == null || this.conditionsToImport.isEmpty()){
            return;
        }
        VariableParameterValueSet newSet = new IntactVariableParameterValueSet();

        for (ImportExperimentalCondition condition : this.conditionsToImport){
           if (condition.getSelectedValue() != null){
               newSet.add(condition.getSelectedValue());
           }
        }

        if (!newSet.isEmpty()){
            if (!this.interaction.areVariableParameterValuesInitialized()){
                setInteraction(getInteractionEditorService().initialiseInteractionVariableParameterValues(this.interaction));
            }
            this.interaction.getVariableParameterValues().add(newSet);
            setUnsavedChanges(true);
        }
        else{
            addErrorMessage("No experimental conditions were selected so we could not import a new set of experimental conditions","No experimental conditions selected");
        }
    }

    public List<VariableParameterValueSet> collectVariableParameterValues(){
        if (!this.interaction.areVariableParameterValuesInitialized()){
            setInteraction(getInteractionEditorService().initialiseInteractionVariableParameterValues(this.interaction));
        }
        List<VariableParameterValueSet> conditions = new ArrayList<VariableParameterValueSet>(this.interaction.getVariableParameterValues());
        Collections.sort(conditions, new AuditableComparator());
        return conditions;
    }

    public void removeVariableParameterValuesSet(VariableParameterValueSet toRemove){
        if (!this.interaction.areVariableParameterValuesInitialized()){
            setInteraction(getInteractionEditorService().initialiseInteractionVariableParameterValues(this.interaction));
        }
        this.interaction.getVariableParameterValues().remove(toRemove);
    }
}
