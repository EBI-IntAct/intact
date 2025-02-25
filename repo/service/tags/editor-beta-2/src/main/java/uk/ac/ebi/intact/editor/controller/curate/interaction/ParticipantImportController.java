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
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.ComponentDao;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.dao.InteractorDao;
import uk.ac.ebi.intact.dbupdate.prot.report.ReportWriter;
import uk.ac.ebi.intact.dbupdate.prot.report.ReportWriterImpl;
import uk.ac.ebi.intact.dbupdate.prot.report.UpdateReportHandler;
import uk.ac.ebi.intact.editor.config.EditorConfig;
import uk.ac.ebi.intact.editor.controller.BaseController;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.InstitutionUtils;
import uk.ac.ebi.intact.model.util.XrefUtils;
import uk.ac.ebi.intact.uniprot.model.UniprotProteinLike;
import uk.ac.ebi.intact.uniprot.model.UniprotProteinTranscript;
import uk.ac.ebi.intact.uniprot.service.UniprotRemoteService;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
@Controller
@Scope( "conversation.access" )
@ConversationName( "general" )
public class ParticipantImportController extends BaseController {

    private static final Log log = LogFactory.getLog( ParticipantImportController.class );

    @Autowired
    private UniprotRemoteService uniprotRemoteService;

    @Autowired
    private InteractionController interactionController;

    //@Autowired
    //private EnricherService enricherService;

    private List<ImportCandidate> importCandidates;
    private List<String> queriesNoResults;
    private String[] participantsToImport = new String[0];

    private CvExperimentalRole cvExperimentalRole;
    private CvBiologicalRole cvBiologicalRole;
    private BioSource expressedIn;
    private CvExperimentalPreparation cvExperimentalPreparation;
    private float stoichiometry;

    @PostConstruct
    public void init() {
        EditorConfig editorConfig = getEditorConfig();
        stoichiometry = editorConfig.getDefaultStoichiometry();
    }

    public void importParticipants( ActionEvent evt ) {
        importCandidates = new ArrayList<ImportCandidate>();
        queriesNoResults = new ArrayList<String>();

        final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDaoFactory();
        cvExperimentalRole = daoFactory.getCvObjectDao(CvExperimentalRole.class).getByPsiMiRef(CvExperimentalRole.UNSPECIFIED_PSI_REF);
        cvBiologicalRole = daoFactory.getCvObjectDao(CvBiologicalRole.class).getByPsiMiRef(CvBiologicalRole.UNSPECIFIED_PSI_REF);

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
                queriesNoResults.add(participantToImport+" (short query - less than 4 chars.)");
            } else if (participantToImport.contains("*")) {
                queriesNoResults.add(participantToImport+" (wildcards not allowed)");
            } else {
                Set<ImportCandidate> candidates = importParticipant(participantToImport);

                if (candidates.isEmpty()) {
                    queriesNoResults.add(participantToImport);
                } else {
                    importCandidates.addAll(candidates);
                }
            }
        }

        participantsToImport = new String[0];
    }

    public void importSelected(ActionEvent evt) {
        for (ImportCandidate candidate : importCandidates) {
            if (candidate.isSelected()) {
                final Interaction interaction = interactionController.getInteraction();
                Component participant = toParticipant(candidate, interaction);
                interactionController.addParticipant(participant);

                interactionController.setUnsavedChanges(true);
            }
        }
    }



    public Set<ImportCandidate> importParticipant(String participantToImport) {
        if (participantToImport == null) {
            addErrorMessage("No participant to import", "Provide one or more accessions");
            return Collections.EMPTY_SET;
        }
        log.debug( "Importing participant: "+ participantToImport );

        Set<ImportCandidate> candidates = importFromIntAct(participantToImport);

        if (candidates.isEmpty()) {
            Set<ImportCandidate> uniprotCandidates = importFromUniprot(participantToImport);

            // only pre-select those that match the query
            for (ImportCandidate candidate : uniprotCandidates) {
                candidate.setSelected(false);

                for (String primaryAc : candidate.getPrimaryAcs()) {
                    if (candidate.getQuery().equalsIgnoreCase(primaryAc)) {
                        candidate.setSelected(true);
                        break;
                    }
                }
            }

            candidates.addAll(uniprotCandidates);
        }

        return candidates;
    }

    private Set<ImportCandidate> importFromIntAct(String participantToImport) {
        Set<ImportCandidate> candidates = new HashSet<ImportCandidate>();

        final IntactContext context = IntactContext.getCurrentInstance();
        final ComponentDao componentDao = context.getDaoFactory().getComponentDao();
        final InteractorDao<InteractorImpl> interactorDao = context.getDaoFactory().getInteractorDao();

        // id
        if (participantToImport.startsWith(context.getConfig().getAcPrefix())) {
            Interactor interactor = interactorDao.getByAc(participantToImport);

            if (interactor != null) {
                candidates.add(toImportCandidate(participantToImport, interactor));
            } else {
                Component component = componentDao.getByAc(participantToImport);

                if (component != null) {
                    candidates.add(toImportCandidate(participantToImport, component.getInteractor()));
                }
            }
        } else {
            // identity xref
            Collection<InteractorImpl> interactorsByXref = interactorDao.getByIdentityXref(participantToImport);

            for (InteractorImpl interactorByXref : interactorsByXref) {
                candidates.add(toImportCandidate(participantToImport, interactorByXref));
            }

            if (candidates.isEmpty()) {
                // shortLabel
                final Collection<InteractorImpl> interactorsByLabel = interactorDao.getByShortLabelLike(participantToImport);

                for (Interactor interactor : interactorsByLabel) {
                    if (!(interactor instanceof Interaction)) {
                        candidates.add(toImportCandidate(participantToImport, interactor));
                    }
                }
            }
        }

        return candidates;
    }



    private Set<ImportCandidate> importFromUniprot(String participantToImport) {
        Set<ImportCandidate> candidates = new HashSet<ImportCandidate>();

        final Collection<UniprotProteinLike> uniprotProteins = uniprotRemoteService.retrieveAny(participantToImport);

        for (UniprotProteinLike uniprotProtein : uniprotProteins) {
            ImportCandidate candidate = new ImportCandidate(participantToImport, uniprotProtein);
            candidate.setSource("uniprotkb");
            candidate.setInteractor(toProtein(candidate));
            candidates.add(candidate);
        }

        return candidates;
    }

    private ImportCandidate toImportCandidate(String participantToImport, Interactor interactor) {
        ImportCandidate candidate = new ImportCandidate(participantToImport, interactor);
        candidate.setSource(IntactContext.getCurrentInstance().getInstitution().getShortLabel());

        final Collection<InteractorXref> identityXrefs = XrefUtils.getIdentityXrefs(interactor);

        if (!identityXrefs.isEmpty()) {
            List<String> ids = new ArrayList<String>(identityXrefs.size());

            for (InteractorXref xref : identityXrefs) {
                ids.add(xref.getPrimaryId());
            }

            candidate.setPrimaryAcs(ids);
        }

        List<String> secondaryAcs = new ArrayList<String>();

        for (Xref xref : interactor.getXrefs()) {
            if (xref.getCvXrefQualifier() != null && CvXrefQualifier.SECONDARY_AC_MI_REF.equals(xref.getCvXrefQualifier().getIdentifier())) {
                secondaryAcs.add(xref.getPrimaryId());
            }
        }

        candidate.setSecondaryAcs(secondaryAcs);

        return candidate;
    }

    private Component toParticipant(ImportCandidate candidate, Interaction interaction) {
        Interactor interactor = candidate.getInteractor();

//        if (candidate.getInteractor() != null) {
//            interactor = candidate.getInteractor();
//        } else {
//            interactor = toProtein(candidate);
//        }

        if (candidate.isChain() || candidate.isIsoform()){
            getInteractionController().getChangesController().markToCreatedTranscriptWithoutMaster(interactor);
        }

        Component component = new Component(IntactContext.getCurrentInstance().getInstitution(),
                interaction, interactor, cvExperimentalRole, cvBiologicalRole );
        component.setExpressedIn(expressedIn);
        component.setStoichiometry(stoichiometry);

        if (cvExperimentalPreparation != null) {
            component.setExperimentalPreparations(Collections.singleton(cvExperimentalPreparation));
        }

        return component;
    }

    private Interactor toProtein(ImportCandidate candidate) {
        final Institution owner = IntactContext.getCurrentInstance().getInstitution();
        final UniprotProteinLike uniprotProtein = candidate.getUniprotProtein();

        final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDaoFactory();
        CvInteractorType type = daoFactory.getCvObjectDao(CvInteractorType.class).getByPsiMiRef(CvInteractorType.PROTEIN_MI_REF);
        CvDatabase uniprotkb = daoFactory.getCvObjectDao(CvDatabase.class).getByPsiMiRef(CvDatabase.UNIPROT_MI_REF);
        CvXrefQualifier identity = daoFactory.getCvObjectDao(CvXrefQualifier.class).getByPsiMiRef(CvXrefQualifier.IDENTITY_MI_REF);

        BioSource organism = new BioSource(uniprotProtein.getOrganism().getName(), String.valueOf(uniprotProtein.getOrganism().getTaxid()));
        Protein protein = new ProteinImpl(owner, organism, uniprotProtein.getId().toLowerCase(), type);
        protein.setFullName(uniprotProtein.getDescription());

        InteractorXref xref = new InteractorXref(owner, uniprotkb, candidate.getPrimaryAcs().iterator().next(), identity);
        protein.addXref(xref);

        protein.setSequence(uniprotProtein.getSequence());

        // if it is an isoform or chain, we will create a stub annotation that will be completed when the protein is saved by
        // creating its corresponding protein master and updating the accession.
        if (candidate.isIsoform() || candidate.isChain()) {

            CvXrefQualifier tempParentRef;

            if (candidate.isIsoform()) {
                tempParentRef = daoFactory.getCvObjectDao(CvXrefQualifier.class).getByIdentifier(CvXrefQualifier.ISOFORM_PARENT_MI_REF);
            } else {
                tempParentRef = daoFactory.getCvObjectDao(CvXrefQualifier.class).getByIdentifier(CvXrefQualifier.CHAIN_PARENT_MI_REF);
            }

            CvDatabase ownDatabase = InstitutionUtils.retrieveCvDatabase(IntactContext.getCurrentInstance(), owner);

            UniprotProteinTranscript proteinTranscript = (UniprotProteinTranscript) uniprotProtein;

            // set shortlabel differently for protein transcripts
            protein.setShortLabel( proteinTranscript.getPrimaryAc().toLowerCase() );

            // temporary master accession instead of the IntAct AC, with this structure "?P12345"
            InteractorXref tempIsoformParent = new InteractorXref(owner, ownDatabase, "?"+proteinTranscript.getMasterProtein().getPrimaryAc(), tempParentRef);
            protein.addXref(tempIsoformParent);
        }

        return protein;
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

    public CvExperimentalRole getCvExperimentalRole() {
        return cvExperimentalRole;
    }

    public void setCvExperimentalRole(CvExperimentalRole cvExperimentalRole) {
        this.cvExperimentalRole = cvExperimentalRole;
    }

    public CvBiologicalRole getCvBiologicalRole() {
        return cvBiologicalRole;
    }

    public void setCvBiologicalRole(CvBiologicalRole cvBiologicalRole) {
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

    public BioSource getExpressedIn() {
        return expressedIn;
    }

    public void setExpressedIn(BioSource expressedIn) {
        this.expressedIn = expressedIn;
    }

    public CvExperimentalPreparation getCvExperimentalPreparation() {
        return cvExperimentalPreparation;
    }

    public void setCvExperimentalPreparation(CvExperimentalPreparation cvExperimentalPreparation) {
        this.cvExperimentalPreparation = cvExperimentalPreparation;
    }

    public float getStoichiometry() {
        return stoichiometry;
    }

    public void setStoichiometry(float stoichiometry) {
        this.stoichiometry = stoichiometry;
    }

    private class EditorReportHandler implements UpdateReportHandler {

        private Writer writer;
        private ReportWriter reportWriter;

        public EditorReportHandler(Writer writer) {
            try {
                this.reportWriter = new ReportWriterImpl(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        @Override
//        public ReportWriter getPreProcessedWriter() throws IOException {
//            return reportWriter;
//        }
//
//        @Override
//        public ReportWriter getProcessedWriter() throws IOException {
//            return reportWriter;
//        }


        @Override
        public ReportWriter getOutOfDateRangeWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getTranscriptWithSameSequenceWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getIntactParentWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getProteinMappingWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getSequenceChangedCautionWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getDeletedComponentWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getDuplicatedWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getDeletedWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getCreatedWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getNonUniprotProteinWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getUpdateCasesWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getSequenceChangedWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getRangeChangedWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getInvalidRangeWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getDeadProteinWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getOutOfDateParticipantWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getPreProcessErrorWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public ReportWriter getSecondaryProteinsWriter() throws IOException {
            return reportWriter;
        }

        @Override
        public void close() throws IOException {

        }
    }
}