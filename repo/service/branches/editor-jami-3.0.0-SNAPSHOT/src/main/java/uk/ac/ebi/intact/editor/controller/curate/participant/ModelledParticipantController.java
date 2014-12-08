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
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.orchestra.conversation.annotations.ConversationName;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.SelectableDataModelWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.editor.controller.curate.AnnotatedObjectController;
import uk.ac.ebi.intact.editor.controller.curate.ChangesController;
import uk.ac.ebi.intact.editor.controller.curate.cloner.EditorCloner;
import uk.ac.ebi.intact.editor.controller.curate.cloner.ModelledParticipantCloner;
import uk.ac.ebi.intact.editor.controller.curate.interaction.*;
import uk.ac.ebi.intact.editor.util.SelectableCollectionDataModel;
import uk.ac.ebi.intact.jami.context.UserContext;
import uk.ac.ebi.intact.jami.dao.CvTermDao;
import uk.ac.ebi.intact.jami.dao.IntactDao;
import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactUtils;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.CvTopic;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Modelled Participant controller.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "general" )
public class ModelledParticipantController extends AbstractParticipantController<IntactModelledParticipant> {

    private static final Log log = LogFactory.getLog( ModelledParticipantController.class );

    @Autowired
    private ComplexController interactionController;

    public ModelledParticipantController() {
        super(IntactModelledParticipant.class);
    }

    @Override
    protected void generalLoadChecks() {
        super.generalLoadChecks();
        generalComplexLoadChecks();
    }

    @Override
    protected void refreshParentControllers() {
        // different loaded interaction
        if (interactionController.getComplex() != getParticipant().getInteraction()){
            // different participant to load
            if (interactionController.getAc() == null ||
                    (getParticipant().getInteraction() instanceof IntactComplex
                            && !interactionController.getAc().equals(((IntactComplex)getParticipant().getInteraction()).getAc()))){
                IntactComplex intactInteraction = (IntactComplex)getParticipant().getInteraction();
                interactionController.setComplex(intactInteraction);
            }
            // replace old feature instance with new one in feature tables of participant
            else{
                getParticipant().setInteraction(interactionController.getComplex());
                interactionController.reloadSingleParticipant(getParticipant());
            }
        }

        super.refreshParentControllers();
    }

    @Override
    protected void initialiseParticipantTargets() {
        getParticipantTargets().clear();
        getParticipantTargets().add(new SelectItem(null, "select participant", "select participant", false, false, true));
        for (ParticipantWrapper wrapper : interactionController.getParticipants()){
            if (getAc() == null && getParticipant() != wrapper.getParticipant()){
                getParticipantTargets().add(new SelectItem( wrapper.getParticipant(),
                        wrapper.getParticipant().getInteractor().getShortName()+", "+wrapper.getParticipant().getAc(),
                        wrapper.getParticipant().getInteractor().getFullName()));
            }
            else if (getAc() != null && !getAc().equals(wrapper.getParticipant().getAc())){
                getParticipantTargets().add(new SelectItem( wrapper.getParticipant(),
                        wrapper.getParticipant().getInteractor().getShortName()+", "+wrapper.getParticipant().getAc(),
                        wrapper.getParticipant().getInteractor().getFullName()));
            }
        }
    }

    @Override
    public Collection<String> collectParentAcsOfCurrentAnnotatedObject(){
        Collection<String> parentAcs = new ArrayList<String>();

        if (getParticipant().getInteraction() != null){
            addParentAcsTo(parentAcs, (IntactComplex)getParticipant().getInteraction());
        }

        return parentAcs;
    }

    /**
     * Add all the parent acs of this interaction
     * @param parentAcs
     * @param inter
     */
    protected void addParentAcsTo(Collection<String> parentAcs, IntactComplex inter) {
        if (inter.getAc() != null){
            parentAcs.add(inter.getAc());
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
    public void addInteractorToParticipant(ActionEvent evt) {
        super.addInteractorToParticipant(evt);
        interactionController.reloadSingleParticipant(getParticipant());

    }

    @Override
    protected AnnotatedObjectController getParentController() {
        return interactionController;
    }

    @Override
    protected String getPageContext() {
        return "cparticipant";
    }

    @Override
    public void newXref(ActionEvent evt) {
        // xrefs are not always initialised
        if (!getParticipant().areXrefsInitialized()){
            setParticipant(getParticipantEditorService().initialiseParticipantXrefs(getParticipant()));
        }

        getParticipant().getXrefs().add(new ModelledParticipantXref(IntactUtils.createMIDatabase("to set", null), "to set"));
        setUnsavedChanges(true);
    }

    @Override
    public ModelledParticipantXref newXref(String db, String dbMI, String id, String secondaryId, String qualifier, String qualifierMI) {
        return new ModelledParticipantXref(getCvService().findCvObject(IntactUtils.DATABASE_OBJCLASS, dbMI != null ? dbMI : db),
                id,
                secondaryId,
                getCvService().findCvObject(IntactUtils.QUALIFIER_OBJCLASS, qualifierMI != null ? qualifierMI : qualifier));
    }

    @Override
    public void newAnnotation(ActionEvent evt) {
        getParticipant().getAnnotations().add(new ModelledParticipantAnnotation(IntactUtils.createMIAliasType("to set", null)));
        setUnsavedChanges(true);
    }

    @Override
    public ModelledParticipantAnnotation newAnnotation(String topic, String topicMI, String text) {
        return new ModelledParticipantAnnotation(getCvService().findCvObject(IntactUtils.TOPIC_OBJCLASS, topicMI != null ? topicMI: topic), text);
    }

    @Override
    public void newAlias(ActionEvent evt) {
        getParticipant().getAliases().add(new ModelledParticipantAlias(IntactUtils.createMIAliasType("to set", null), "to set"));
        setUnsavedChanges(true);
    }

    @Override
    public ModelledParticipantAlias newAlias(String alias, String aliasMI, String name) {
        return new ModelledParticipantAlias(getCvService().findCvObject(IntactUtils.ALIAS_TYPE_OBJCLASS, aliasMI != null ? aliasMI : alias),
                name);
    }

    @Override
    protected CausalRelationship createCausalStatement(CvTerm statementToAdd, Participant targetToAdd) {
        return new ModelledCausalRelationship(statementToAdd, targetToAdd);
    }

    @Override
    public IntactDbSynchronizer getDbSynchronizer() {
        return getEditorService().getIntactDao().getSynchronizerContext().getModelledParticipantSynchronizer();
    }

    @Override
    public String getObjectName() {
        return "Complex participant";
    }

    @Override
    protected boolean isInitialisedOtherProperties(IntactModelledParticipant part) {
        return true;
    }

    @Override
    protected EditorCloner<Participant, IntactModelledParticipant> newClonerInstance() {
        return new ModelledParticipantCloner();
    }

    @Override
    public void newCausalRelationship(ActionEvent evt) {
        super.newCausalRelationship(evt);
        interactionController.reloadSingleParticipant(getParticipant());
    }

    public void reloadSingleFeature(IntactModelledFeature f){
        // only update if not lazy loaded
        if (getParticipant().areFeaturesInitialized()){
            Iterator<ModelledFeature> evIterator = getParticipant().getFeatures().iterator();
            boolean add = true;
            while (evIterator.hasNext()){
                IntactModelledFeature intactEv = (IntactModelledFeature)evIterator.next();
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

    public void removeFeature(IntactModelledFeature f){
        // only update if not lazy loaded
        if (getParticipant().areFeaturesInitialized()){
            Iterator<ModelledFeature> evIterator = getParticipant().getFeatures().iterator();
            while (evIterator.hasNext()){
                IntactModelledFeature intactEv = (IntactModelledFeature)evIterator.next();
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
}