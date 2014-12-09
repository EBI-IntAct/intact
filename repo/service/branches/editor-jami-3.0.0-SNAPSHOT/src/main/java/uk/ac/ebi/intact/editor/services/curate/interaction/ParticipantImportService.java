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
package uk.ac.ebi.intact.editor.services.curate.interaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import psidev.psi.mi.jami.bridges.chebi.ChebiFetcher;
import psidev.psi.mi.jami.bridges.exception.BridgeFailedException;
import psidev.psi.mi.jami.bridges.uniprot.UniprotGeneFetcher;
import psidev.psi.mi.jami.bridges.uniprot.UniprotProteinFetcher;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.clone.InteractorCloner;
import uk.ac.ebi.intact.editor.controller.curate.interaction.CandidateType;
import uk.ac.ebi.intact.editor.controller.curate.interaction.ImportCandidate;
import uk.ac.ebi.intact.editor.controller.curate.util.CheckIdentifier;
import uk.ac.ebi.intact.editor.services.AbstractEditorService;
import uk.ac.ebi.intact.jami.context.IntactConfiguration;
import uk.ac.ebi.intact.jami.dao.InteractorDao;
import uk.ac.ebi.intact.jami.model.extension.*;

import javax.annotation.Resource;
import java.util.*;


@Service
public class ParticipantImportService extends AbstractEditorService {

    private static final Log log = LogFactory.getLog(ParticipantImportService.class);

    @Resource(name = "proteinFetcher")
    private UniprotProteinFetcher uniprotRemoteService;

    @Resource(name = "bioactiveEntityFetcher")
    private ChebiFetcher chebiFetcher;

    @Resource(name = "geneFetcher")
    private UniprotGeneFetcher uniprotGeneFetcher;

    @Resource(name = "intactJamiConfiguration")
    private IntactConfiguration intactConfig;

    private final static String FEATURE_CHAIN = "PRO_";

    @Transactional(value = "jamiTransactionManager", propagation = Propagation.REQUIRED, readOnly = true)
    public Set<ImportCandidate> importParticipant(String participantToImport) throws BridgeFailedException {
        log.debug("Importing participant: " + participantToImport);

        Set<ImportCandidate> candidates = importFromIntAct(participantToImport.toUpperCase());

        if (candidates.isEmpty()) {     //It is not an IntAct one

            CandidateType candidateType = detectCandidate(participantToImport.toUpperCase());

            switch (candidateType) {
                case BIO_ACTIVE_ENTITY:
                    candidates = importFromChebi(participantToImport.toUpperCase());
                    break;
                case GENE:
                    candidates = importFromSwissProtWithEnsemblId(participantToImport.toUpperCase());
                    break;
                case PROTEIN:
                    candidates = importFromUniprot(participantToImport.toUpperCase());
                    break;
            }

            // only pre-select those that match the query
            for (ImportCandidate candidate : candidates) {
                candidate.setSelected(false);

                for (String primaryAc : candidate.getPrimaryAcs()) {
                    if (candidate.getQuery().equalsIgnoreCase(primaryAc)) {
                        candidate.setSelected(true);
                        break;
                    }
                    // for feature chains, in IntAct, we add the parent uniprot ac before the chain id so feature chains are never pre-selected
                    else if (candidate.isChain() && primaryAc.toUpperCase().contains(FEATURE_CHAIN)) {
                        int indexOfChain = primaryAc.indexOf(FEATURE_CHAIN);

                        String chain_ac = primaryAc.substring(indexOfChain);

                        if (candidate.getQuery().equalsIgnoreCase(chain_ac)) {
                            candidate.setSelected(true);
                            break;
                        }
                    }
                }
            }

            candidates.addAll(candidates);

        }

        return candidates;
    }

    private CandidateType detectCandidate(String candidateId) {
        if (CheckIdentifier.checkChebiId(candidateId)) {
            return CandidateType.BIO_ACTIVE_ENTITY;
        } else if (CheckIdentifier.checkEnsembleId(candidateId)) {
            return CandidateType.GENE;
        } else { //If the identifier is not one of the previous we suppose that is a UniprotKB
            return CandidateType.PROTEIN;
        }
    }

    private Set<ImportCandidate> importFromIntAct(String participantToImport) {
        Set<ImportCandidate> candidates = new HashSet<ImportCandidate>();

        final InteractorDao<IntactInteractor> interactorDao = getIntactDao().getInteractorDao(IntactInteractor.class);

        // id
        if (participantToImport.startsWith(intactConfig.getAcPrefix())) {
            IntactInteractor interactor = interactorDao.getByAc(participantToImport);

            if (interactor != null) {
                candidates.add(toImportCandidate(participantToImport, interactor));
            } else {
                Participant component = getIntactDao().getParticipantEvidenceDao().getByAc(participantToImport);

                if (component != null) {
                    candidates.add(toImportCandidate(participantToImport, (IntactInteractor)component.getInteractor()));
                }

                component = getIntactDao().getModelledParticipantDao().getByAc(participantToImport);

                if (component != null) {
                    candidates.add(toImportCandidate(participantToImport, (IntactInteractor)component.getInteractor()));
                }
            }
        } else {
            // identity xref
            Collection<IntactInteractor> interactorsByXref = interactorDao.getByXrefQualifier(Xref.IDENTITY, Xref.IDENTITY_MI, participantToImport);

            for (IntactInteractor interactorByXref : interactorsByXref) {
                candidates.add(toImportCandidate(participantToImport, interactorByXref));
            }

            if (candidates.isEmpty()) {
                // shortLabel
                final Collection<IntactInteractor> interactorsByLabel = interactorDao.getByShortNameLike(participantToImport);

                for (IntactInteractor interactor : interactorsByLabel) {
                    candidates.add(toImportCandidate(participantToImport, interactor));
                }
            }
        }

        return candidates;
    }


    private Set<ImportCandidate> importFromUniprot(String participantToImport) throws BridgeFailedException {
        Set<ImportCandidate> candidates = new HashSet<ImportCandidate>();

        final Collection<Protein> uniprotProteins = uniprotRemoteService.fetchByIdentifier(participantToImport);

        for (Protein uniprotProtein : uniprotProteins) {
            ImportCandidate candidate = new ImportCandidate(participantToImport, uniprotProtein);
            candidate.setSource("uniprotkb");
            candidate.setInteractor(toProtein(candidate));
            candidates.add(candidate);
        }

        return candidates;
    }


    private Set<ImportCandidate> importFromChebi(String participantToImport) throws BridgeFailedException {
        Set<ImportCandidate> candidates = new HashSet<ImportCandidate>();

        final Collection<BioactiveEntity> smallMolecules = chebiFetcher.fetchByIdentifier(participantToImport);
        for (BioactiveEntity entity : smallMolecules){
            ImportCandidate candidate = toImportCandidate(participantToImport, toBioactiveEntity(entity));
            candidate.setSource("chebi");
            candidates.add(candidate);
        }

        return candidates;
    }

    private Set<ImportCandidate> importFromSwissProtWithEnsemblId(String participantToImport) throws BridgeFailedException {
        Set<ImportCandidate> candidates = new HashSet<ImportCandidate>();

        final Collection<Gene> genes = uniprotGeneFetcher.fetchByIdentifier(participantToImport);

        for (Gene gene : genes) {
            ImportCandidate candidate = toImportCandidate(participantToImport, toGene(gene));
            candidate.setSource("ensembl");
            candidates.add(candidate);
        }
        return candidates;
    }


    private ImportCandidate toImportCandidate(String participantToImport, IntactInteractor interactor) {
        ImportCandidate candidate = new ImportCandidate(participantToImport, interactor);
        candidate.setSource(intactConfig.getDefaultInstitution().getShortName());

        // initialise some properties
        initialiseXrefs(interactor.getDbXrefs());
        initialiseAnnotations(interactor.getDbAnnotations());
        initialiseCv(interactor.getInteractorType());

        final Collection<Xref> identityXrefs = interactor.getIdentifiers();

        if (!identityXrefs.isEmpty()) {
            List<String> ids = new ArrayList<String>(identityXrefs.size());

            for (Xref xref : identityXrefs) {
                ids.add(xref.getId());
            }

            candidate.setPrimaryAcs(ids);
        }

        List<String> secondaryAcs = new ArrayList<String>();

        for (Xref xref : interactor.getXrefs()) {
            if (XrefUtils.doesXrefHaveQualifier(xref, Xref.SECONDARY_MI, Xref.SECONDARY) || XrefUtils.doesXrefHaveQualifier(xref, null, "intact-secondary")) {
                secondaryAcs.add(xref.getId());
            }
        }

        candidate.setSecondaryAcs(secondaryAcs);

        return candidate;
    }

    private void initialiseXrefs(Collection<Xref> xrefs) {
        for (Xref ref : xrefs){
            Hibernate.initialize(((IntactCvTerm) ref.getDatabase()).getDbAnnotations());
            Hibernate.initialize(((IntactCvTerm)ref.getDatabase()).getDbXrefs());
            if (ref.getQualifier() != null){
                Hibernate.initialize(((IntactCvTerm)ref.getQualifier()).getDbXrefs());
            }
        }
    }

    private void initialiseAnnotations(Collection<Annotation> annotations) {
        for (Annotation annot : annotations){
            Hibernate.initialize(((IntactCvTerm)annot.getTopic()).getDbAnnotations());
            Hibernate.initialize(((IntactCvTerm)annot.getTopic()).getDbXrefs());
        }
    }

    private void initialiseCv(CvTerm term) {
        initialiseAnnotations(((IntactCvTerm)term).getDbAnnotations());
        initialiseXrefs(((IntactCvTerm)term).getDbXrefs());
    }

    private IntactProtein toProtein(ImportCandidate candidate) {
        IntactProtein protein=null;

        // use the protein service to create proteins (not persist!)
        if (candidate.getUniprotProtein() != null) {
            protein = new IntactProtein(candidate.getUniprotProtein().getShortName());
            InteractorCloner.copyAndOverrideBasicPolymerProperties(candidate.getUniprotProtein(), protein);
        }

        return protein;
    }

    private IntactBioactiveEntity toBioactiveEntity(BioactiveEntity candidate) {
        IntactBioactiveEntity entity=null;

        if (candidate != null) {
            entity = new IntactBioactiveEntity(candidate.getShortName());
            InteractorCloner.copyAndOverrideBasicInteractorProperties(candidate, entity);
        }

        return entity;
    }

    private IntactGene toGene(Gene candidate) {
        IntactGene entity=null;

        if (candidate != null) {
            entity = new IntactGene(candidate.getShortName());
            InteractorCloner.copyAndOverrideBasicInteractorProperties(candidate, entity);
        }

        return entity;
    }
}
