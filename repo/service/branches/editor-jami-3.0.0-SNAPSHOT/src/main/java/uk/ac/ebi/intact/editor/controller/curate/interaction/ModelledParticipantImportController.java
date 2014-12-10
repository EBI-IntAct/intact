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
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.model.CvTerm;
import uk.ac.ebi.intact.editor.controller.BaseController;
import uk.ac.ebi.intact.editor.controller.curate.ChangesController;
import uk.ac.ebi.intact.editor.services.curate.cvobject.CvObjectService;
import uk.ac.ebi.intact.editor.services.curate.interaction.ParticipantImportService;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractor;
import uk.ac.ebi.intact.jami.model.extension.IntactModelledParticipant;
import uk.ac.ebi.intact.jami.model.extension.IntactStoichiometry;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;

import javax.annotation.Resource;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope("conversation.access")
@ConversationName("general")
public class ModelledParticipantImportController extends BaseController {

    private static final Log log = LogFactory.getLog(ModelledParticipantImportController.class);

    @Resource(name = "participantImportService")
    private transient ParticipantImportService participantImportService;

    @Resource(name = "cvObjectService")
    private transient CvObjectService cvService;

    @Autowired
    private ChangesController changesController;

    @Autowired
    private ComplexController interactionController;

    private List<ImportCandidate> importCandidates;
    private List<String> queriesNoResults;
    private String[] participantsToImport = new String[0];

    private transient CvTerm cvBiologicalRole;
    private int minStoichiometry;
    private int maxStoichiometry;

    private final static String FEATURE_CHAIN = "PRO_";

    public void importParticipants(ActionEvent evt) {
        getParticipantImportService().getIntactDao().getUserContext().setUser(getCurrentUser());

        this.minStoichiometry = getEditorConfig().getDefaultStoichiometry();
        this.maxStoichiometry = getEditorConfig().getDefaultStoichiometry();

        this.minStoichiometry = getEditorConfig().getDefaultStoichiometry();
        this.maxStoichiometry = getEditorConfig().getDefaultStoichiometry();

        CvObjectService cvObjectService = getCvService();

        if (!cvObjectService.isInitialised()){
            cvObjectService.loadData();
        }

        cvBiologicalRole = cvObjectService.getDefaultBiologicalRole();


        importCandidates = new ArrayList<ImportCandidate>();
        queriesNoResults = new ArrayList<String>();

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
        // set current user
        getParticipantImportService().getIntactDao().getUserContext().setUser(getCurrentUser());

        this.minStoichiometry = getEditorConfig().getDefaultStoichiometry();
        this.maxStoichiometry = getEditorConfig().getDefaultStoichiometry();

        CvObjectService cvObjectService = getCvService();

        if (!cvObjectService.isInitialised()){
            cvObjectService.loadData();
        }

        cvBiologicalRole = cvObjectService.getDefaultBiologicalRole();

        for (ImportCandidate candidate : importCandidates) {
            if (candidate.isSelected()) {
                final IntactComplex interaction = interactionController.getComplex();
                IntactModelledParticipant participant = toParticipant(candidate, interaction);
                interactionController.addParticipant(participant);

                interactionController.setUnsavedChanges(true);
            }
        }
    }

    protected IntactModelledParticipant toParticipant(ImportCandidate candidate, IntactComplex interaction) {
        IntactInteractor interactor = candidate.getInteractor();

        if (cvBiologicalRole == null) {
            CvObjectService cvObjectService = getCvService();

            if (!cvObjectService.isInitialised()){
                cvObjectService.loadData();
            }

            if (cvBiologicalRole == null) {
                cvBiologicalRole = cvObjectService.getDefaultBiologicalRole();
            }
        }

        IntactModelledParticipant component = new IntactModelledParticipant(interactor);
        component.setInteraction(interaction);
        component.setBiologicalRole(cvBiologicalRole);
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

        return component;
    }

    /**
     * Add all the parent acs of this interaction
     *
     * @param parentAcs
     * @param inter
     */
    protected void addParentAcsTo(Collection<String> parentAcs, IntactComplex inter) {
        if (inter.getAc() != null) {
            parentAcs.add(inter.getAc());
        }
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

    public CvTerm getBiologicalRole() {
        return cvBiologicalRole;
    }

    public void setBiologicalRole(CvTerm cvBiologicalRole) {
        this.cvBiologicalRole = cvBiologicalRole;
    }

    public List<String> getQueriesNoResults() {
        return queriesNoResults;
    }

    public void setQueriesNoResults(List<String> queriesNoResults) {
        this.queriesNoResults = queriesNoResults;
    }

    public ComplexController getInteractionController() {
        return interactionController;
    }

    public void setInteractionController(ComplexController interactionController) {
        this.interactionController = interactionController;
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

    public ParticipantImportService getParticipantImportService() {
        if (this.participantImportService == null){
            this.participantImportService = ApplicationContextProvider.getBean("participantImportService");
        }
        return participantImportService;
    }

    public CvObjectService getCvService() {
        if (this.cvService == null){
            this.cvService = ApplicationContextProvider.getBean("cvObjectService");
        }
        return cvService;
    }
}
