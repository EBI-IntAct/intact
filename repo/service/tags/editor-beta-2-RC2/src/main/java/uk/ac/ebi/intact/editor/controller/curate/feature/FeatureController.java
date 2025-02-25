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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.editor.controller.curate.AnnotatedObjectController;
import uk.ac.ebi.intact.editor.controller.curate.cloner.FeatureIntactCloner;
import uk.ac.ebi.intact.editor.controller.curate.cvobject.CvObjectService;
import uk.ac.ebi.intact.editor.controller.curate.experiment.ExperimentController;
import uk.ac.ebi.intact.editor.controller.curate.interaction.InteractionController;
import uk.ac.ebi.intact.editor.controller.curate.participant.ParticipantController;
import uk.ac.ebi.intact.editor.controller.curate.publication.PublicationController;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.clone.IntactCloner;
import uk.ac.ebi.intact.model.util.FeatureUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.validator.ValidatorException;
import java.util.ArrayList;
import java.util.List;

/**
 * Feature controller.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id: ParticipantController.java 14281 2010-04-12 21:48:43Z samuel.kerrien $
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "general" )
public class FeatureController extends AnnotatedObjectController {

    private static final Log log = LogFactory.getLog( FeatureController.class );

    private Feature feature;
    private List<RangeWrapper> rangeWrappers;
    private boolean containsInvalidRanges;

    /**
     * The AC of the feature to be loaded.
     */
    private String ac;

    @Autowired
    private PublicationController publicationController;

    @Autowired
    private ExperimentController experimentController;

    @Autowired
    private InteractionController interactionController;

    @Autowired
    private ParticipantController participantController;

    private String newRangeValue;

    public FeatureController() {
    }

    @Override
    public AnnotatedObject getAnnotatedObject() {
        return getFeature();
    }

    @Override
    public void setAnnotatedObject(AnnotatedObject annotatedObject) {
        setFeature((Feature)annotatedObject);
    }

    @Override
    public String goToParent() {
        return "/curate/participant?faces-redirect=true&includeViewParams=true";
    }

    public void loadData( ComponentSystemEvent event ) {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if ( ac != null ) {
                if ( feature == null || !ac.equals( feature.getAc() ) ) {
                    feature = loadByAc(IntactContext.getCurrentInstance().getDaoFactory().getFeatureDao(), ac);
                }
            } else {
                if ( feature != null ) ac = feature.getAc();
            }

            if (feature == null) {
                super.addErrorMessage("Feature does not exist", ac);
                return;
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
        }

        refreshRangeWrappers();

        if (containsInvalidRanges) {
            addWarningMessage("This feature contains invalid ranges", "Ranges must be fixed before being able to save");
        }

        generalLoadChecks();
    }

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

        // replace CVs by ones with ACs

        CvObjectService cvObjectService = (CvObjectService) getSpringContext().getBean("cvObjectService");
        CvFuzzyType fromFuzzyType = cvObjectService.findCvObjectByIdentifier(CvFuzzyType.class, newRange.getFromCvFuzzyType().getIdentifier());
        CvFuzzyType toFuzzyType = cvObjectService.findCvObjectByIdentifier(CvFuzzyType.class, newRange.getToCvFuzzyType().getIdentifier());

        newRange.setFromCvFuzzyType(fromFuzzyType);
        newRange.setToCvFuzzyType(toFuzzyType);

        feature.addRange(newRange);

        refreshRangeWrappers();

        getChangesController().markAsUnsaved(feature);
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
        } else {
            getChangesController().markToDelete(range, range.getFeature());
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

    public void setAc( String ac ) {
        this.ac = ac;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature( Feature feature ) {
        this.feature = feature;

        if (feature != null){
            this.ac = feature.getAc();
        }
    }

    @Override
    public void doPreSave() {

        participantController.getParticipant().addBindingDomain(feature);
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
}