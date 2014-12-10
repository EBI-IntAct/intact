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
package uk.ac.ebi.intact.editor.controller.curate.participant;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.hibernate.Hibernate;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.SelectableDataModelWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AliasUtils;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.editor.controller.curate.AnnotatedObjectController;
import uk.ac.ebi.intact.editor.controller.curate.ChangesController;
import uk.ac.ebi.intact.editor.controller.curate.UnsavedChange;
import uk.ac.ebi.intact.editor.controller.curate.cloner.EditorCloner;
import uk.ac.ebi.intact.editor.controller.curate.cloner.ParticipantEvidenceCloner;
import uk.ac.ebi.intact.editor.controller.curate.interaction.*;
import uk.ac.ebi.intact.editor.services.curate.cvobject.CvObjectService;
import uk.ac.ebi.intact.editor.controller.curate.experiment.ExperimentController;
import uk.ac.ebi.intact.editor.controller.curate.publication.PublicationController;
import uk.ac.ebi.intact.editor.controller.curate.util.IntactObjectComparator;
import uk.ac.ebi.intact.editor.services.curate.organism.BioSourceService;
import uk.ac.ebi.intact.editor.util.SelectableCollectionDataModel;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Participant controller.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "general" )
public class ParticipantController extends AbstractParticipantController<IntactParticipantEvidence> {

    private static final Log log = LogFactory.getLog( ParticipantController.class );

    private IntactCvTerm preparationToAdd;
    private IntactCvTerm identificationToAdd;

    @Autowired
    private PublicationController publicationController;

    @Autowired
    private ExperimentController experimentController;
    @Autowired
    private InteractionController interactionController;

    private boolean isParameterDisabled;
    private boolean isConfidenceDisabled;
    private boolean isExperimentalPreparationDisabled;
    private boolean isIdentificationMethodDisabled;
    private String authorAssignedName=null;

    @Resource(name = "bioSourceService")
    private transient BioSourceService bioSourceService;

    public ParticipantController() {
        super(IntactParticipantEvidence.class);
    }

    @Override
    protected void loadCautionMessages() {
        super.loadCautionMessages();
        if (getParticipant() != null){
            if (!getParticipant().areAliasesInitialized()){
                setParticipant(getParticipantEditorService().initialiseParticipantAliases(getParticipant()));
            }

            Alias author = AliasUtils.collectFirstAliasWithType(getParticipant().getAliases(), Alias.AUTHOR_ASSIGNED_NAME_MI, Alias.AUTHOR_ASSIGNED_NAME);
            setAuthorGivenName(author != null ? author.getName() : null);
        }
    }

    @Override
    protected boolean isInitialisedOtherProperties(IntactParticipantEvidence part) {
        return isCvInitialised(part.getExperimentalRole());
    }

    @Override
    public Collection<String> collectParentAcsOfCurrentAnnotatedObject(){
        Collection<String> parentAcs = new ArrayList<String>();

        if (getParticipant().getInteraction() != null){
            addParentAcsTo(parentAcs, (IntactInteractionEvidence)getParticipant().getInteraction());
        }

        return parentAcs;
    }

    /**
     * Add all the parent acs of this interaction
     * @param parentAcs
     * @param inter
     */
    protected void addParentAcsTo(Collection<String> parentAcs, IntactInteractionEvidence inter) {
        if (inter.getAc() != null){
            parentAcs.add(inter.getAc());
        }

        if (inter.getExperiment() instanceof IntactExperiment){
            addParentAcsTo(parentAcs, (IntactExperiment)inter.getExperiment());
        }
    }

    public void removeExperimentalPreparation(CvTerm prep){
        if (prep != null){
            if (!getParticipant().areExperimentalPreparationsInitialized()){
                setParticipant(getParticipantEditorService().initialiseParticipantExperimentalPreparations(getParticipant()));
            }
            if (getParticipant().getExperimentalPreparations().remove(prep)){
                changed();
            }
            else{
                addWarningMessage("The experimental preparation " + prep.getFullName()+" has not been removed.","The experimental preparation " + prep.getFullName()+" has not been removed because was not attached to this participant.");
            }
        }
    }

    public void addExperimentalPreparation(){
        if (this.preparationToAdd != null){
            if (!getParticipant().areExperimentalPreparationsInitialized()){
                setParticipant(getParticipantEditorService().initialiseParticipantExperimentalPreparations(getParticipant()));
            }
            if (!getParticipant().getExperimentalPreparations().contains(this.preparationToAdd)){
                getParticipant().getExperimentalPreparations().add(this.preparationToAdd);
                changed();
            }
            else{
                addWarningMessage("The experimental preparation " + preparationToAdd.getFullName()+" was already attached to this participant.","The experimental preparation " + preparationToAdd.getFullName()+" was already attached to this participant.");
            }
        }
    }

    public void removeIdentificationMethod(CvTerm prep){
        if (prep != null){
            if (!getParticipant().areIdentificationMethodsInitialized()){
                setParticipant(getParticipantEditorService().initialiseParticipantIdentificationMethods(getParticipant()));
            }
            if (getParticipant().getDbIdentificationMethods().remove(prep)){
                changed();
            }
            else{
                addWarningMessage("The identification method " + prep.getFullName() + " has not been removed.", "The identification method " + prep.getFullName() + " has not been removed because was not attached to this participant.");
            }
        }
    }

    public void addIdentificationMethod(){
        if (this.identificationToAdd != null){
            if (!getParticipant().areIdentificationMethodsInitialized()){
                setParticipant(getParticipantEditorService().initialiseParticipantIdentificationMethods(getParticipant()));
            }
            if (!getParticipant().getDbIdentificationMethods().contains(this.identificationToAdd)){
                getParticipant().getDbIdentificationMethods().add(this.identificationToAdd);
                changed();
            }
            else{
                addWarningMessage("The identification method " + identificationToAdd.getFullName()+" was already attached to this participant.","The identification method " + identificationToAdd.getFullName()+" was already attached to this participant.");
            }
        }
    }

    @Override
    public void doPostSave(){
        // the participant was just created, add it to the list of participant of the interaction
        if (getParticipant().getInteraction() != null){
            interactionController.reloadSingleParticipant(getParticipant());
        }
    }

    @Override
    protected EditorCloner<ParticipantEvidence, IntactParticipantEvidence> newClonerInstance() {
        return new ParticipantEvidenceCloner();
    }

    public String newParticipant(InteractionEvidence interaction) throws InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException {
        String value = super.newParticipant(interaction);

        CvTerm defaultExperimentalRole = getCvService().getDefaultExperimentalRole();
        getParticipant().setExperimentalRole(defaultExperimentalRole);

        return value;
    }

    @Override
    public String doDelete(){
        interactionController.removeParticipant(getParticipant());
        return super.doDelete();
    }

    @Override
    public void newXref(ActionEvent evt) {
        // xrefs are not always initialised
        if (!getParticipant().areXrefsInitialized()){
            setParticipant(getParticipantEditorService().initialiseParticipantXrefs(getParticipant()));
        }

        getParticipant().getXrefs().add(new ParticipantEvidenceXref(IntactUtils.createMIDatabase("to set", null), "to set"));
        setUnsavedChanges(true);
    }

    @Override
    public ParticipantEvidenceXref newXref(String db, String dbMI, String id, String secondaryId, String qualifier, String qualifierMI) {
        return new ParticipantEvidenceXref(getCvService().findCvObject(IntactUtils.DATABASE_OBJCLASS, dbMI != null ? dbMI : db),
                id,
                secondaryId,
                getCvService().findCvObject(IntactUtils.QUALIFIER_OBJCLASS, qualifierMI != null ? qualifierMI : qualifier));
    }

    @Override
    public void refreshTabs(){
        super.refreshTabsAndFocusXref();
        this.isConfidenceDisabled = true;
        this.isParameterDisabled = true;
        this.isExperimentalPreparationDisabled = true;
        this.isIdentificationMethodDisabled = true;
    }

    @Override
    protected AnnotatedObjectController getParentController() {
        return interactionController;
    }

    @Override
    protected String getPageContext() {
        return "participant";
    }

    @Override
    protected void refreshParentControllers() {
        // different loaded interaction
        if (interactionController.getInteraction() != getParticipant().getInteraction()){
            // different participant to load
            if (interactionController.getAc() == null ||
                    (getParticipant().getInteraction() instanceof IntactInteractionEvidence
                            && !interactionController.getAc().equals(((IntactInteractionEvidence)getParticipant().getInteraction()).getAc()))){
                IntactInteractionEvidence intactInteraction = (IntactInteractionEvidence)getParticipant().getInteraction();
                interactionController.setInteraction(intactInteraction);

                // reload other parents
                if (intactInteraction.getExperiment() instanceof IntactExperiment){
                    IntactExperiment exp = (IntactExperiment)intactInteraction.getExperiment();
                    experimentController.setExperiment( exp );

                    if ( exp.getPublication() instanceof IntactPublication ) {
                        IntactPublication publication = (IntactPublication)exp.getPublication();
                        publicationController.setPublication( publication );
                    }
                    else{
                        publicationController.setPublication(null);
                    }
                }
                else{
                    experimentController.setExperiment(null);
                }
            }
            // replace old feature instance with new one in feature tables of participant
            else{
                getParticipant().setInteraction(interactionController.getInteraction());
                interactionController.reloadSingleParticipant(getParticipant());
            }
        }
    }

    @Override
    protected void initialiseDefaultProperties(IntactPrimaryObject annotatedObject) {
        IntactParticipantEvidence part = (IntactParticipantEvidence) annotatedObject;
        if (!isCvInitialised(part.getExperimentalRole())){
            setParticipant(getParticipantEditorService().reloadFullyInitialisedParticipant(part));
        }
        super.initialiseDefaultProperties(annotatedObject);
    }

    public String getAuthorGivenName() {
        return this.authorAssignedName;
    }

    public void setAuthorGivenName( String name ) {
        this.authorAssignedName = name;
    }

    public void onAuthorAssignedNameChanged(ValueChangeEvent evt) {
        setUnsavedChanges(true);
        String newValue = (String) evt.getNewValue();
        if (newValue != null && newValue.length() > 0){
            replaceOrCreateAlias(Alias.AUTHOR_ASSIGNED_NAME, Alias.AUTHOR_ASSIGNED_NAME_MI, newValue, getParticipant().getAliases());
        }
        else{
            removeAlias(Alias.AUTHOR_ASSIGNED_NAME, Alias.AUTHOR_ASSIGNED_NAME_MI, null, getParticipant().getAliases());
        }
        this.authorAssignedName = newValue;
    }

    public void removeAuthorAssignedName(ActionEvent evt){
        addInfoMessage("Removed author-assigned name", authorAssignedName);
        // aliases are always loaded
        removeAlias(Alias.AUTHOR_ASSIGNED_NAME, Alias.AUTHOR_ASSIGNED_NAME_MI, getParticipant().getAliases());
    }

    // Confidence
    ///////////////////////////////////////////////

    public void newConfidence() {
        if (!getParticipant().areConfidencesInitialized()){
           setParticipant(getParticipantEditorService().initialiseParticipantConfidences(getParticipant()));
        }
        ParticipantEvidenceConfidence confidence = new ParticipantEvidenceConfidence(IntactUtils.createMIConfidenceType("to set", null), "to set");
        getParticipant().getConfidences().add(confidence);
        setUnsavedChanges(true);
    }

    public List<Confidence> collectConfidences() {
        if (!getParticipant().areConfidencesInitialized()){
            setParticipant(getParticipantEditorService().initialiseParticipantConfidences(getParticipant()));
        }

        final List<Confidence> confidences = new ArrayList<Confidence>( getParticipant().getConfidences() );
        Collections.sort( confidences, new AuditableComparator() );
        return confidences;
    }

    public List<Parameter> collectParameters() {
        if (!getParticipant().areParametersInitialized()){
            setParticipant(getParticipantEditorService().initialiseParticipantParameters(getParticipant()));
        }

        final List<Parameter> parameters = new ArrayList<Parameter>( getParticipant().getParameters() );
        Collections.sort( parameters, new AuditableComparator() );
        return parameters;
    }

    public List<CvTerm> collectExperimentalPreparations() {
        if (!getParticipant().areExperimentalPreparationsInitialized()){
            setParticipant(getParticipantEditorService().initialiseParticipantExperimentalPreparations(getParticipant()));
        }

        final List<CvTerm> parameters = new ArrayList<CvTerm>( getParticipant().getExperimentalPreparations() );
        Collections.sort( parameters, new AuditableComparator() );
        return parameters;
    }

    public List<CvTerm> collectIdentificationMethods() {
        if (!getParticipant().areIdentificationMethodsInitialized()){
            setParticipant(getParticipantEditorService().initialiseParticipantIdentificationMethods(getParticipant()));
        }

        final List<CvTerm> parameters = new ArrayList<CvTerm>( getParticipant().getDbIdentificationMethods() );
        Collections.sort( parameters, new AuditableComparator() );
        return parameters;
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

    public boolean isIdentificationMethodDisabled() {
        return isIdentificationMethodDisabled;
    }

    public boolean isExperimentalPreparationDisabled() {
        return isExperimentalPreparationDisabled;
    }

    public void onTabChanged(TabChangeEvent e) {

        // the xref tab is active
        super.onTabChanged(e);

        // all the tabs selectOneMenu are disabled, we can process the tabs specific to interaction
        if (isAliasDisabled() && isXrefDisabled() && isAnnotationTopicDisabled()){
            if (e.getTab().getId().equals("parametersTab")){
                isParameterDisabled = false;
                isConfidenceDisabled = true;
                isExperimentalPreparationDisabled = true;
                isIdentificationMethodDisabled = true;
            }
            else if (e.getTab().getId().equals("confidencesTab")){
                isParameterDisabled = true;
                isConfidenceDisabled = false;
                isExperimentalPreparationDisabled = true;
                isIdentificationMethodDisabled = true;
            }
            else if (e.getTab().getId().equals("experimentalPreparationsTab")){
                isParameterDisabled = true;
                isConfidenceDisabled = true;
                isExperimentalPreparationDisabled = false;
                isIdentificationMethodDisabled = true;
            }
            else if (e.getTab().getId().equals("identificationMethodsTab")){
                isParameterDisabled = true;
                isConfidenceDisabled = true;
                isExperimentalPreparationDisabled = true;
                isIdentificationMethodDisabled = false;
            }
            else {
                isParameterDisabled = true;
                isConfidenceDisabled = true;
                isExperimentalPreparationDisabled = true;
                isIdentificationMethodDisabled = true;
            }
        }
        else {
            isParameterDisabled = true;
            isConfidenceDisabled = true;
            isExperimentalPreparationDisabled = true;
            isIdentificationMethodDisabled = true;
        }
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getEditorService().getIntactDao().getSynchronizerContext().getParticipantEvidenceSynchronizer();
    }

    public IntactCvTerm getPreparationToAdd() {
        return preparationToAdd;
    }

    public void setPreparationToAdd(IntactCvTerm preparationToAdd) {
        this.preparationToAdd = preparationToAdd;
    }

    public IntactCvTerm getIdentificationToAdd() {
        return identificationToAdd;
    }

    public void setIdentificationToAdd(IntactCvTerm identificationToAdd) {
        this.identificationToAdd = identificationToAdd;
    }

    @Override
    protected void generalLoadChecks() {
        super.generalLoadChecks();
        generalPublicationLoadChecks();

        if (!getBioSourceService().isInitialised()){
            getBioSourceService().loadData();
        }
    }

    public int getExperimentalPreparationsSize() {
        if (getParticipant() == null){
            return 0;
        }
        else if (getParticipant().areExperimentalPreparationsInitialized()){
            return getParticipant().getExperimentalPreparations().size();
        }
        else{
            return getParticipantEditorService().countExperimentalPreparations(getParticipant());
        }
    }

    public int getIdentificationMethodsSize() {
        if (getParticipant() == null){
            return 0;
        }
        else if (getParticipant().areIdentificationMethodsInitialized()){
            return getParticipant().getDbIdentificationMethods().size();
        }
        else{
            return getParticipantEditorService().countIdentificationMethods(getParticipant());
        }
    }

    @Override
    public void newAlias(ActionEvent evt) {
         getParticipant().getAliases().add(new ParticipantEvidenceAlias(IntactUtils.createMIAliasType("to set", null), "to set"));
        setUnsavedChanges(true);
    }

    @Override
    public ParticipantEvidenceAlias newAlias(String alias, String aliasMI, String name) {

        return new ParticipantEvidenceAlias(getCvService().findCvObject(IntactUtils.ALIAS_TYPE_OBJCLASS, aliasMI != null ? aliasMI : alias),
                name);
    }

    @Override
    public void newAnnotation(ActionEvent evt) {
        getParticipant().getAnnotations().add(new ParticipantEvidenceAnnotation(IntactUtils.createMIAliasType("to set", null)));
        setUnsavedChanges(true);
    }

    @Override
    public boolean isAliasNotEditable(Alias alias) {
        return AliasUtils.doesAliasHaveType(alias, Alias.AUTHOR_ASSIGNED_NAME_MI, Alias.AUTHOR_ASSIGNED_NAME);
    }

    @Override
    public ParticipantEvidenceAnnotation newAnnotation(String topic, String topicMI, String text) {
        return new ParticipantEvidenceAnnotation(getCvService().findCvObject(IntactUtils.TOPIC_OBJCLASS, topicMI != null ? topicMI: topic), text);
    }

    public void reloadSingleFeature(IntactFeatureEvidence f){
        // only update if not lazy loaded
        if (getParticipant().areFeaturesInitialized()){
            Iterator<FeatureEvidence> evIterator = getParticipant().getFeatures().iterator();
            boolean add = true;
            while (evIterator.hasNext()){
                IntactFeatureEvidence intactEv = (IntactFeatureEvidence)evIterator.next();
                if (intactEv.getAc() == null && f == intactEv){
                    add = false;
                }
                else if (intactEv.getAc() != null && !intactEv.getAc().equals(f.getAc())){
                    evIterator.remove();
                }
            }
            if (add){
                getParticipant().getFeatures().add(f);
            }
        }

        refreshFeatures();

        interactionController.reloadSingleParticipant(getParticipant());
    }

    public void removeFeature(IntactFeatureEvidence f){
        // only update if not lazy loaded
        if (getParticipant().areFeaturesInitialized()){
            Iterator<FeatureEvidence> evIterator = getParticipant().getFeatures().iterator();
            while (evIterator.hasNext()){
                IntactFeatureEvidence intactEv = (IntactFeatureEvidence)evIterator.next();
                if (intactEv.getAc() == null && f == intactEv){
                    evIterator.remove();
                }
                else if (intactEv.getAc() != null && !intactEv.getAc().equals(f.getAc())){
                    evIterator.remove();
                }
            }
        }

        refreshFeatures();

        interactionController.reloadSingleParticipant(getParticipant());
    }

    @Override
    public void addInteractorToParticipant(ActionEvent evt) {
        super.addInteractorToParticipant(evt);
        interactionController.reloadSingleParticipant(getParticipant());
    }

    public BioSourceService getBioSourceService() {
        if (this.bioSourceService == null){
            this.bioSourceService = ApplicationContextProvider.getBean("bioSourceService");
        }
        return bioSourceService;
    }

    public void removeConfidence(Confidence conf) {
        if (!getParticipant().areConfidencesInitialized()){
            setParticipant(getParticipantEditorService().initialiseParticipantConfidences(getParticipant()));
        }

        getParticipant().getConfidences().remove(conf);
    }

    public void removeParameter(Confidence conf) {
        if (!getParticipant().areConfidencesInitialized()){
            setParticipant(getParticipantEditorService().initialiseParticipantConfidences(getParticipant()));
        }

        getParticipant().getConfidences().remove(conf);
    }

    @Override
    protected void postProcessDeletedEvent(UnsavedChange unsaved) {
        super.postProcessDeletedEvent(unsaved);
        if (unsaved.getUnsavedObject() instanceof IntactFeatureEvidence){
            removeFeature((IntactFeatureEvidence)unsaved.getUnsavedObject());
        }
        interactionController.reloadSingleParticipant(getParticipant());
    }

    public int getParametersSize() {
        if (getParticipant() == null){
            return 0;
        }
        else if (getParticipant().areParametersInitialized()){
            return getParticipant().getParameters().size();
        }
        else{
            return getParticipantEditorService().countParameters(getParticipant());
        }
    }

    public int getConfidencesSize() {
        if (getParticipant() == null){
            return 0;
        }
        else if (getParticipant().areConfidencesInitialized()){
            return getParticipant().getConfidences().size();
        }
        else{
            return getParticipantEditorService().countConfidences(getParticipant());
        }
    }

    @Override
    public void unlinkFeature(FeatureWrapper wrapper) {
        super.unlinkFeature(wrapper);
        interactionController.reloadSingleParticipant(getParticipant());
    }
}