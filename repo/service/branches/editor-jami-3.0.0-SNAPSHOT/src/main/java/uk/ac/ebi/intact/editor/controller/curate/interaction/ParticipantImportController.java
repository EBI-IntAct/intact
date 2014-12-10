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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Organism;
import uk.ac.ebi.intact.editor.config.EditorConfig;
import uk.ac.ebi.intact.editor.controller.BaseController;
import uk.ac.ebi.intact.editor.controller.curate.ChangesController;
import uk.ac.ebi.intact.editor.services.curate.cvobject.CvObjectService;
import uk.ac.ebi.intact.editor.controller.curate.util.CheckIdentifier;
import uk.ac.ebi.intact.editor.services.curate.interaction.ParticipantImportService;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.model.extension.IntactParticipantEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactStoichiometry;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope("conversation.access")
@ConversationName("general")
public class ParticipantImportController extends BaseController {

    private static final Log log = LogFactory.getLog(ParticipantImportController.class);

    @Resource(name = "participantImportService")
    private transient ParticipantImportService participantImportService;

    @Resource(name = "cvObjectService")
    private transient CvObjectService cvService;

    @Autowired
    private InteractionController interactionController;

    @Autowired
    private ChangesController changesController;

    private List<ImportCandidate> importCandidates;
    private List<String> queriesNoResults;
    private String[] participantsToImport = new String[0];

    private CvTerm cvExperimentalRole;
    private CvTerm cvBiologicalRole;
    private Organism expressedIn;
    private Collection<CvTerm> cvExperimentalPreparations = new ArrayList<CvTerm>();
    private Collection<CvTerm> cvIdentifications=new ArrayList<CvTerm>();
    private CvTerm preparationToAdd;
    private CvTerm identificationToAdd;
    private int minStoichiometry;
    private int maxStoichiometry;

    @Resource(name = "editorConfig")
    private transient EditorConfig editorConfig;

    private final static String FEATURE_CHAIN = "PRO_";

    public void importParticipants(ActionEvent evt) {
        getParticipantImportService().getIntactDao().getUserContext().setUser(getCurrentUser());

        importCandidates = new ArrayList<ImportCandidate>();
        queriesNoResults = new ArrayList<String>();

        this.minStoichiometry = getEditorConfig().getDefaultStoichiometry();
        this.maxStoichiometry = getEditorConfig().getDefaultStoichiometry();

        CvObjectService cvObjectService = getCvService();

        if (!cvObjectService.isInitialised()){
            cvObjectService.loadData();
        }

        cvExperimentalRole = cvObjectService.getDefaultExperimentalRole();
        cvBiologicalRole = cvObjectService.getDefaultBiologicalRole();

        if (participantsToImport == null) {
            addErrorMessage("No participants to import", "Please add at least one identifier in the box");
            return;
        }

        for (String participantToImport : participantsToImport) {
            participantToImport = participantToImport.trim();

            if (participantToImport.isEmpty()) {
                continue;
            }

            // only import if the query has more than 4 chars (to avoid massive queries) {

            if (participantToImport.length() < 4) {
                queriesNoResults.add(participantToImport + " (short query - less than 4 chars.)");
            } else if (participantToImport.contains("*")) {
                queriesNoResults.add(participantToImport + " (wildcards not allowed)");
            } else {
                Set<ImportCandidate> candidates = null;
                try {
                    candidates = getParticipantImportService().importParticipant(participantToImport);
                    if (candidates.isEmpty()) {
                        queriesNoResults.add(participantToImport);
                    } else {
                        importCandidates.addAll(candidates);
                    }
                } catch (BridgeFailedException e) {
                    addErrorMessage("Cannot load interactor " + participantToImport, e.getCause() + ": " + e.getMessage());
                    queriesNoResults.add(participantToImport);
                } catch (FinderException e) {
                    addErrorMessage("Cannot load interactor " + participantToImport, e.getCause() + ": " + e.getMessage());
                    queriesNoResults.add(participantToImport);
                } catch (SynchronizerException e) {
                    addErrorMessage("Cannot load interactor " + participantToImport, e.getCause() + ": " + e.getMessage());
                    queriesNoResults.add(participantToImport);
                } catch (PersisterException e) {
                    addErrorMessage("Cannot load interactor " + participantToImport, e.getCause() + ": " + e.getMessage());
                    queriesNoResults.add(participantToImport);
                }
            }
        }

        participantsToImport = new String[0];
    }

    public void importSelected(ActionEvent evt) {
        getParticipantImportService().getIntactDao().getUserContext().setUser(getCurrentUser());

        this.minStoichiometry = getEditorConfig().getDefaultStoichiometry();
        this.maxStoichiometry = getEditorConfig().getDefaultStoichiometry();

        CvObjectService cvObjectService = getCvService();

        if (!cvObjectService.isInitialised()){
            cvObjectService.loadData();
        }

        cvExperimentalRole = cvObjectService.getDefaultExperimentalRole();
        cvBiologicalRole = cvObjectService.getDefaultBiologicalRole();

        for (ImportCandidate candidate : importCandidates) {
            if (candidate.isSelected()) {
                final IntactInteractionEvidence interaction = interactionController.getInteraction();
                IntactParticipantEvidence participant = toParticipant(candidate, interaction);
                interactionController.addParticipant(participant);

                interactionController.setUnsavedChanges(true);
            }
        }
    }

    protected IntactParticipantEvidence toParticipant(ImportCandidate candidate, IntactInteractionEvidence interaction) {
        IntactInteractor interactor = candidate.getInteractor();

        if (cvExperimentalRole == null || cvBiologicalRole == null) {
            CvObjectService cvObjectService = getCvService();

            if (!cvObjectService.isInitialised()){
                cvObjectService.loadData();
            }

            if (cvExperimentalRole == null) {
                cvExperimentalRole = cvObjectService.getDefaultExperimentalRole();
            }

            if (cvBiologicalRole == null) {
                cvBiologicalRole = cvObjectService.getDefaultBiologicalRole();
            }
        }

        IntactParticipantEvidence component = new IntactParticipantEvidence(interactor);
        component.setInteraction(interaction);
        component.setExperimentalRole(cvExperimentalRole);
        component.setBiologicalRole(cvBiologicalRole);
        component.setExpressedInOrganism(expressedIn);
        component.setStoichiometry(new IntactStoichiometry(minStoichiometry, maxStoichiometry));

        if (candidate.isChain() || candidate.isIsoform()) {
            Collection<String> parentAcs = new ArrayList<String>();

            if (interaction.getAc() != null) {
                parentAcs.add(interaction.getAc());

                addParentAcsTo(parentAcs, interaction);
            }

            changesController.markAsHiddenChange(interactor, interaction, parentAcs,
                    getParticipantImportService().getIntactDao().getSynchronizerContext().getInteractorSynchronizer(), "Interactor "+interactor.getShortName());
        }

        if (!cvExperimentalPreparations.isEmpty()) {
            component.getExperimentalPreparations().addAll(cvExperimentalPreparations);
        }
        if (!cvIdentifications.isEmpty()) {
            component.getIdentificationMethods().addAll(cvIdentifications);
        }

        return component;
    }

    /**
     * Add all the parent acs of this interaction
     *
     * @param parentAcs
     * @param inter
     */
    protected void addParentAcsTo(Collection<String> parentAcs, IntactInteractionEvidence inter) {
        if (inter.getAc() != null) {
            parentAcs.add(inter.getAc());
        }

        interactionController.addParentAcsTo(parentAcs, inter.getExperiment());
    }

    public String[] getParticipantsToImport() {
        return participantsToImport;
    }

    public void setParticipantsToImport(String[] participantsToImport) {
        this.participantsToImport = participantsToImport;
    }

    public List<ImportCandidate> getImportCandidates() {
        return importCandidates;
    }

    public void setImportCandidates(List<ImportCandidate> importCandidates) {
        this.importCandidates = importCandidates;
    }

    public CvTerm getCvExperimentalRole() {
        return cvExperimentalRole;
    }

    public void setCvExperimentalRole(CvTerm cvExperimentalRole) {
        this.cvExperimentalRole = cvExperimentalRole;
    }

    public CvTerm getCvBiologicalRole() {
        return cvBiologicalRole;
    }

    public void setCvBiologicalRole(CvTerm cvBiologicalRole) {
        this.cvBiologicalRole = cvBiologicalRole;
    }

    public List<String> getQueriesNoResults() {
        return queriesNoResults;
    }

    public void setQueriesNoResults(List<String> queriesNoResults) {
        this.queriesNoResults = queriesNoResults;
    }

    public InteractionController getInteractionController() {
        return interactionController;
    }

    public void setInteractionController(InteractionController interactionController) {
        this.interactionController = interactionController;
    }

    public Organism getExpressedIn() {
        return expressedIn;
    }

    public void setExpressedIn(Organism expressedIn) {
        this.expressedIn = expressedIn;
    }

    public Collection<CvTerm> getCvExperimentalPreparations() {
        return cvExperimentalPreparations;
    }

    public Collection<CvTerm> getCvIdentifications() {
        return cvIdentifications;
    }

    public CvTerm getPreparationToAdd() {
        return preparationToAdd;
    }

    public void setPreparationToAdd(CvTerm preparationToAdd) {
        this.preparationToAdd = preparationToAdd;
    }

    public CvTerm getIdentificationToAdd() {
        return identificationToAdd;
    }

    public void setIdentificationToAdd(CvTerm identificationToAdd) {
        this.identificationToAdd = identificationToAdd;
    }

    public void removeExperimentalPreparation(CvTerm prep){
        if (prep != null){
            cvExperimentalPreparations.remove(prep);
        }
    }

    public void addExperimentalPreparation(){
        if (this.preparationToAdd != null){
            if (!cvExperimentalPreparations.contains(this.preparationToAdd)){
                cvExperimentalPreparations.add(this.preparationToAdd);
            }
        }
    }

    public void removeIdentificationMethod(CvTerm prep){
        if (prep != null){
            cvIdentifications.remove(prep);
        }
    }

    public void addIdentificationMethod(){
        if (this.identificationToAdd != null){
            if (!cvIdentifications.contains(this.identificationToAdd)){
                cvIdentifications.add(this.identificationToAdd);
            }
        }
    }

    public ParticipantImportService getParticipantImportService() {
        if (this.participantImportService == null){
            this.participantImportService = ApplicationContextProvider.getBean("participantImportService");
        }
        return participantImportService;
    }

    public int getMinStoichiometry() {
        return minStoichiometry;
    }

    public void setMinStoichiometry(int stoichiometry) {
        this.minStoichiometry = stoichiometry;
        this.maxStoichiometry = Math.max(minStoichiometry, this.maxStoichiometry);
    }

    public int getMaxStoichiometry() {
        return maxStoichiometry;
    }

    public void setMaxStoichiometry(int stoichiometry) {
        this.maxStoichiometry = stoichiometry;
        this.minStoichiometry = Math.min(this.minStoichiometry, maxStoichiometry);
    }

    public EditorConfig getEditorConfig() {
        if (this.editorConfig == null){
            this.editorConfig = ApplicationContextProvider.getBean("editorConfig");
        }
        return editorConfig;
    }

    public CvObjectService getCvService() {
        if (this.cvService == null){
            this.cvService = ApplicationContextProvider.getBean("cvObjectService");
        }
        return cvService;
    }
}
