/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.dbupdate.prot.listener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.bridges.taxonomy.TaxonomyService;
import uk.ac.ebi.intact.core.IntactTransactionException;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.CvObjectDao;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.core.persistence.dao.ProteinDao;
import uk.ac.ebi.intact.core.persistence.dao.XrefDao;
import uk.ac.ebi.intact.dbupdate.prot.*;
import uk.ac.ebi.intact.dbupdate.prot.event.*;
import uk.ac.ebi.intact.dbupdate.prot.rangefix.InvalidRange;
import uk.ac.ebi.intact.dbupdate.prot.rangefix.RangeChecker;
import uk.ac.ebi.intact.dbupdate.prot.referencefilter.IntactCrossReferenceFilter;
import uk.ac.ebi.intact.dbupdate.prot.util.ProteinTools;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.ProteinUtils;
import uk.ac.ebi.intact.uniprot.model.Organism;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotProteinTranscript;
import uk.ac.ebi.intact.util.Crc64;
import uk.ac.ebi.intact.util.biosource.BioSourceService;
import uk.ac.ebi.intact.util.biosource.BioSourceServiceException;
import uk.ac.ebi.intact.util.biosource.BioSourceServiceFactory;
import uk.ac.ebi.intact.util.protein.CvHelper;
import uk.ac.ebi.intact.util.protein.ProteinServiceException;
import uk.ac.ebi.intact.util.protein.utils.*;

import java.util.*;

/**
 * Updates the current protein in the database, using information from uniprot.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UniprotProteinUpdater extends AbstractProteinUpdateProcessorListener {

    private static final Log log = LogFactory.getLog( UniprotProteinUpdater.class );
    private static final String FEATURE_CHAIN_UNKNOWN_POSITION = "?";

    private final int MAX_RETRY_ATTEMPTS = 100;
    private int retryAttempt = 0;

    /**
     * Mapping allowing to specify which database shortlabel correspond to which MI reference.
     */
    private Map<String, String> databaseName2mi = new HashMap<String, String>();
    /**
     * The results
     */
    protected UniprotServiceResult uniprotServiceResult;

    private ProteinUpdateProcessor processor;

    /**
     * BioSource service allowing to create new BioSource in the database.
     */
    private BioSourceService bioSourceService;

    public UniprotProteinUpdater(TaxonomyService taxonomyService) {
        setBioSourceService(BioSourceServiceFactory.getInstance().buildBioSourceService(taxonomyService));
        IntactCrossReferenceFilter intactCrossReferenceFilter = new IntactCrossReferenceFilter();
        databaseName2mi = intactCrossReferenceFilter.getDb2Mi();
    }

    public UniprotProteinUpdater() {
        // Build default taxonomy service
        setBioSourceService(BioSourceServiceFactory.getInstance().buildBioSourceService());
        IntactCrossReferenceFilter intactCrossReferenceFilter = new IntactCrossReferenceFilter();
        databaseName2mi = intactCrossReferenceFilter.getDb2Mi();
    }

    /*public void onPreProcess(ProteinEvent evt) throws ProcessorException {
        Protein protein = evt.getProtein();

        if (log.isTraceEnabled()) log.trace("Checking if the protein can be updated using UniProt information: "+ protein.getShortLabel()+" ("+protein.getAc()+")");

        if (!ProteinUtils.isFromUniprot(protein)) {

            if (evt.getSource() instanceof ProteinUpdateProcessor) {
                final ProteinUpdateProcessor updateProcessor = (ProteinUpdateProcessor) evt.getSource();
                updateProcessor.fireNonUniprotProteinFound(evt);
            }

            if (log.isTraceEnabled()) log.debug("Request finalization, as this protein cannot be updated using UniProt");
            ((ProteinProcessor)evt.getSource()).finalizeAfterCurrentPhase();
        }
    }*/

    public void onProcess(ProteinEvent evt) throws ProcessorException {
        this.processor = (ProteinUpdateProcessor) evt.getSource();

        String uniprotAc = evt.getUniprotIdentity();
        UniprotProtein uniprotProtein = evt.getUniprotProtein();

        // we have a single uniprot identity
        if (uniprotAc != null){
            if (uniprotProtein != null){
                this.uniprotServiceResult = new UniprotServiceResult(uniprotAc);

                try {
                    this.uniprotServiceResult.addAllToProteins(createOrUpdate(uniprotProtein));
                } catch (ProteinServiceException e) {
                    this.uniprotServiceResult.addException(e);
                }
            }
        }
    }

    /**
     * Create or update a protein.
     *
     * @param uniprotProtein the UniProt protein we want to create in IntAct.
     *
     * @return an up-to-date IntAct protein.
     *
     * @throws ProteinServiceException
     */
    protected Collection<Protein> createOrUpdate( UniprotProtein uniprotProtein) throws ProteinServiceException {
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDaoFactory().getProteinDao();

        final String uniprotAc = uniprotProtein.getPrimaryAc();

        if (log.isDebugEnabled()) log.debug("Searching IntAct for Uniprot protein: "+ uniprotAc + ", "
                + uniprotProtein.getOrganism().getName() +" ("+uniprotProtein.getOrganism().getTaxid()+")");

        // we will assign the proteins to two collections - primary / secondary
        Collection<ProteinImpl> primaryProteins = proteinDao.getByUniprotId(uniprotAc);
        Collection<ProteinImpl> secondaryProteins = new ArrayList<ProteinImpl>();

        for (String secondaryAc : uniprotProtein.getSecondaryAcs()) {
            secondaryProteins.addAll(proteinDao.getByUniprotId(secondaryAc));
        }

        // filter and remove non-uniprot prots from the list, and assign to the primary or secondary collections
        ProteinTools.filterNonUniprotAndMultipleUniprot(primaryProteins);
        ProteinTools.filterNonUniprotAndMultipleUniprot(secondaryProteins);

        int countPrimary = primaryProteins.size();
        int countSecondary = secondaryProteins.size();

        if (log.isTraceEnabled()) log.trace("Found "+countPrimary+" primary and "+countSecondary+" secondary for "+uniprotAc);

        // TODO returned proteins are not used here
        processCase(uniprotProtein, primaryProteins, secondaryProteins);

        return uniprotServiceResult.getProteins();
    }

    protected Collection<Protein> processCase(UniprotProtein uniprotProtein, Collection<ProteinImpl> primaryProteins, Collection<ProteinImpl> secondaryProteins) throws ProteinServiceException {
        UpdateCaseEvent event = new UpdateCaseEvent(processor, IntactContext.getCurrentInstance().getDataContext(),
                uniprotProtein, primaryProteins, secondaryProteins);

        Collection<Protein> updatedProts = processProteinCase(uniprotProtein, primaryProteins, secondaryProteins);
        event.setUniprotServiceResult(getUniprotServiceResult());

        processor.fireOnUpdateCase(event);

        return updatedProts;
    }

    protected Collection<Protein> processProteinCase(UniprotProtein uniprotProtein, Collection<ProteinImpl> primaryProteins, Collection<ProteinImpl> secondaryProteins) throws ProteinServiceException {
        Collection<Protein> proteins = new ArrayList<Protein>();
        int countPrimary = primaryProteins.size();
        int countSecondary = secondaryProteins.size();

        if ( countPrimary == 0 && countSecondary == 0 ) {
            if (log.isDebugEnabled()) log.debug( "Could not find IntAct protein by UniProt primary or secondary AC." );
            Protein protein = createMinimalisticProtein( uniprotProtein );
            proteins.add( protein );
            updateProtein( protein, uniprotProtein);

            proteinCreated(protein);

        } else {
            if (log.isDebugEnabled())
                log.debug("Found in IntAct"+countPrimary+" protein(s) with primaryAc and "+countSecondary+" protein(s) on with secondaryAc.");

            boolean isPossibleToUpdate = true;

            if (countPrimary + countSecondary > 1){
                if (countPrimary >= 1 && countSecondary >= 1){
                    processor.fireonProcessErrorFound(new UpdateErrorEvent(processor, IntactContext.getCurrentInstance().getDataContext(), "Unexpected number of proteins found in IntAct for UniprotEntry("+ uniprotProtein.getPrimaryAc() + ") " + countPrimary + " primary proteins, " + countSecondary + " secondary proteins, " +
                            "Please fix this problem manually.", UpdateError.unexpected_number_of_duplicates));
                    if (log.isTraceEnabled()) log.debug("Request finalization, as this protein cannot be updated (several duplicated proteins not resolved)");
                    processor.finalizeAfterCurrentPhase();

                    if (countPrimary > 1){
                        isPossibleToUpdate = false;
                    }
                }
                else if (countSecondary > 1 && countPrimary == 0){
                    processor.fireonProcessErrorFound(new UpdateErrorEvent(processor, IntactContext.getCurrentInstance().getDataContext(), "Unresolved duplication of uniprot protein " + uniprotProtein.getPrimaryAc() + "("+countSecondary+" possible duplicated proteins)", UpdateError.unexpected_number_of_duplicates));
                    if (log.isTraceEnabled()) log.debug("Request finalization, as this protein cannot be updated (several duplicated proteins not resolved)");
                    processor.finalizeAfterCurrentPhase();

                    isPossibleToUpdate = false;
                }
                else if (countPrimary > 1 && countSecondary == 0){
                    processor.fireonProcessErrorFound(new UpdateErrorEvent(processor, IntactContext.getCurrentInstance().getDataContext(), "Unresolved duplication of the protein " + uniprotProtein.getPrimaryAc() + "("+countPrimary+" duplicated proteins)", UpdateError.unexpected_number_of_duplicates));
                    if (log.isTraceEnabled()) log.debug("Request finalization, as this protein cannot be updated (several duplicated proteins not resolved)");
                    processor.finalizeAfterCurrentPhase();

                    isPossibleToUpdate = false;
                }
            }

            for (Protein protein : primaryProteins){
                proteins.add(protein);

                if (isPossibleToUpdate){
                    updateProtein( protein, uniprotProtein);
                }
            }
            for (Protein protein : secondaryProteins){
                proteins.add(protein);
            }
        }

        uniprotServiceResult.addAllToProteins(proteins);

        return proteins;
    }

    /**
     * Update an existing intact protein's annotations.
     * <p/>
     * That includes, all Xrefs, Aliases, splice variants.
     *
     * @param protein        the intact protein to update.
     * @param uniprotProtein the uniprot protein used for data input.
     */
    private void updateProtein( Protein protein, UniprotProtein uniprotProtein) throws ProteinServiceException {
        List<Protein> proteins = new ArrayList<Protein>();

        // check that both protein carry the same organism information
        if (!UpdateBioSource(protein, uniprotProtein.getOrganism())){
            return;
        }

        // Fullname
        String fullname = uniprotProtein.getDescription();
        if ( fullname != null && fullname.length() > 250 ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Truncating fullname to the first 250 first chars." );
            }
            fullname = fullname.substring( 0, 250 );
        }
        protein.setFullName( fullname );

        // Shortlabel
        protein.setShortLabel( generateProteinShortlabel( uniprotProtein ) );

        // Xrefs -- but UniProt's as they are supposed to be up-to-date at this stage.
        XrefUpdaterReport report = XrefUpdaterUtils.updateAllXrefs( protein, uniprotProtein, databaseName2mi );

        if (report.isUpdated()) {
            uniprotServiceResult.addXrefUpdaterReport(report);
        }

        // Aliases
        AliasUpdaterUtils.updateAllAliases( protein, uniprotProtein );

        // Sequence
        updateProteinSequence(protein, uniprotProtein.getSequence(), uniprotProtein.getCrc64());

        // Persist changes
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        ProteinDao pdao = daoFactory.getProteinDao();
        pdao.update( ( ProteinImpl ) protein );

        ///////////////////////////////
        // Update Splice Variants and feature chains


        // search intact
        // splice variants with no 'no-uniprot-update'
        Collection<ProteinImpl> spliceVariantsAndChains = pdao.getSpliceVariants( protein );

        // feature chains
        spliceVariantsAndChains.addAll(pdao.getProteinChains( protein ));

        // We create a copy of the collection that hold the protein transcripts as the findMatches remove the protein transcripts
        // from the collection when a match is found. Therefore the first time it runs, it finds the match, protein transcripts
        //  are correctly created, the protein transcripts are deleted from the collection so that the second
        // you run it, the splice variant are not linked anymore to the uniprotProtein and therefore they are not correctly
        // updated.
        Collection<UniprotProteinTranscript> variantsClone = new ArrayList<UniprotProteinTranscript>();

        variantsClone.addAll(uniprotProtein.getSpliceVariants());
        variantsClone.addAll(uniprotProtein.getFeatureChains());

        for (UniprotProteinTranscript transcript : variantsClone){
            proteins.addAll(createOrUpdateProteinTranscript(transcript, uniprotProtein, protein));
        }

        if (!proteins.containsAll(spliceVariantsAndChains)){

            if ( proteins.size() < spliceVariantsAndChains.size()){
                for (Object protNotUpdated : CollectionUtils.subtract(spliceVariantsAndChains, proteins)){
                    Protein prot = (Protein) protNotUpdated;

                    if(prot.getActiveInstances().size() == 0){
                        deleteProtein(prot);

                        uniprotServiceResult.addMessage("The protein " + getProteinDescription(prot) +
                                " is a protein transcript of " + getProteinDescription(protein) + " in IntAct but not in Uniprot." +
                                " As it is not part of any interactions in IntAct we have deleted it."  );

                    }else if (ProteinUtils.isFromUniprot(prot)){
                        uniprotServiceResult.addError(UniprotServiceResult.SPLICE_VARIANT_IN_INTACT_BUT_NOT_IN_UNIPROT,
                                "In Intact the protein "+ getProteinDescription(prot) +
                                        " is a protein transcript of protein "+ getProteinDescription(protein)+
                                        " but in Uniprot it is not the case. As it is part of interactions in IntAct we couldn't " +
                                        "delete it.");
                    }
                }
            }
            else {
                Collection<Protein> spliceVariantsNotUpdated = new ArrayList<Protein>(spliceVariantsAndChains);
                spliceVariantsNotUpdated.removeAll(CollectionUtils.intersection(spliceVariantsAndChains, proteins));

                for (Protein protNotUpdated : spliceVariantsNotUpdated){

                    if(protNotUpdated.getActiveInstances().size() == 0){
                        deleteProtein(protNotUpdated);

                        uniprotServiceResult.addMessage("The protein " + getProteinDescription(protNotUpdated) +
                                " is a protein transcript of " + getProteinDescription(protein) + " in IntAct but not in Uniprot." +
                                " As it is not part of any interactions in IntAct we have deleted it."  );

                    }else if (ProteinUtils.isFromUniprot(protNotUpdated)){
                        uniprotServiceResult.addError(UniprotServiceResult.SPLICE_VARIANT_IN_INTACT_BUT_NOT_IN_UNIPROT,
                                "In Intact the protein "+ getProteinDescription(protNotUpdated) +
                                        " is a protein transcript of protein "+ getProteinDescription(protein)+
                                        " but in Uniprot it is not the case. As it is part of interactions in IntAct we couldn't " +
                                        "delete it.");
                    }
                }
            }
        }
    }

    private boolean UpdateBioSource(Protein protein, Organism organism) {
        // check that both protein carry the same organism information
        BioSource organism1 = protein.getBioSource();

        int t2 = organism.getTaxid();

        if (organism1 == null) {
            if (log.isWarnEnabled()) log.warn("Protein protein does not contain biosource. It will be assigned the Biosource from uniprot: "+organism.getName()+" ("+organism.getTaxid()+")");
            organism1 = new BioSource(protein.getOwner(), organism.getName(), String.valueOf(t2));
            protein.setBioSource(organism1);

            IntactContext.getCurrentInstance().getCorePersister().saveOrUpdate(organism1);
        }

        if ( organism1 != null && !String.valueOf( t2 ).equals( organism1.getTaxId() ) ) {
            processor.fireonProcessErrorFound(new UpdateErrorEvent(processor, IntactContext.getCurrentInstance().getDataContext(), "UpdateProteins is trying to modify" +
                    " the BioSource(" + organism1.getTaxId() + "," + organism1.getShortLabel() +  ") of the following protein protein " +
                    getProteinDescription(protein) + " by BioSource( " + t2 + "," +
                    organism.getName() + " ). Changing the organism of an existing protein is a forbidden operation.", UpdateError.organism_conflict_with_uniprot_protein));
            processor.finalizeAfterCurrentPhase();

            return false;
        }
        return true;
    }

    private void updateProteinSequence(Protein protein, String uniprotSequence, String uniprotCrC64) {
        boolean sequenceToBeUpdated = false;
        String oldSequence = protein.getSequence();
        String sequence = uniprotSequence;
        if ( (oldSequence == null && sequence != null)) {
            if ( log.isDebugEnabled() ) {
                log.debug( "Sequence requires update." );
            }
            sequenceToBeUpdated = true;
        }
        else if (oldSequence != null && sequence == null){
            processor.fireonProcessErrorFound(new UpdateErrorEvent(processor, IntactContext.getCurrentInstance().getDataContext(), "The sequence of the protein " + protein.getAc() +
                    " is not null but the uniprot entry has a sequence null.", UpdateError.uniprot_sequence_null));
            processor.finalizeAfterCurrentPhase();
        }
        else if (oldSequence != null && sequence != null){
            if (!sequence.equals( oldSequence ) ){
                if ( log.isDebugEnabled() ) {
                    log.debug( "Sequence requires update." );
                }
                sequenceToBeUpdated = true;
            }
        }


        if ( sequenceToBeUpdated) {
            RangeChecker checker = new RangeChecker();

            Set<String> interactionAcsWithBadFeatures = new HashSet<String>();

            Collection<Component> components = protein.getActiveInstances();

            for (Component component : components){
                Interaction interaction = component.getInteraction();

                Collection<Feature> features = component.getBindingDomains();
                for (Feature feature : features){
                    Collection<InvalidRange> invalidRanges = checker.collectRangesImpossibleToShift(feature, oldSequence, sequence);

                    if (!invalidRanges.isEmpty()){
                        interactionAcsWithBadFeatures.add(interaction.getAc());

                        for (InvalidRange invalid : invalidRanges){
                            // range is bad from the beginning, not after the range shifting
                            if (oldSequence.equalsIgnoreCase(invalid.getSequence())){
                                invalidRangeFound(invalid);
                            }
                        }
                    }
                }
            }

            if (!interactionAcsWithBadFeatures.isEmpty()){
                Collection<Component> componentsToFix = new ArrayList<Component>();
                for (Component c : components){
                    if (interactionAcsWithBadFeatures.contains(c.getInteractionAc())){
                        componentsToFix.add(c);
                    }
                }
                badParticipantFound(componentsToFix, protein);
            }
            protein.setSequence( sequence );

            // CRC64
            String crc64 = uniprotCrC64;
            if ( protein.getCrc64() == null || !protein.getCrc64().equals( crc64 ) ) {
                log.debug( "CRC64 requires update." );
                protein.setCrc64( crc64 );
            }

            sequenceChanged(protein, sequence, oldSequence, uniprotCrC64);
        }
    }

    /**
     * create or update a protein transcript
     * @param uniprotProteinTranscript : the uniprot protein transcript
     * @param uniprotProtein : the uniprot protein
     * @param masterProtein : the IntAct master protein
     * @return the list of protein transcripts created or updated
     * @throws ProteinServiceException
     */
    protected Collection<Protein> createOrUpdateProteinTranscript( UniprotProteinTranscript uniprotProteinTranscript, UniprotProtein uniprotProtein, Protein masterProtein ) throws ProteinServiceException {
        ProteinDao proteinDao = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getProteinDao();

        // getThe primary ac of the transcript
        final String uniprotAc = uniprotProteinTranscript.getPrimaryAc();
        // get the taxId of the uniprot transcript
        String taxid = String.valueOf( uniprotProteinTranscript.getOrganism().getTaxid() );

        if (log.isDebugEnabled()) log.debug("Searching IntAct for Uniprot protein: "+ uniprotAc + ", "
                + uniprotProteinTranscript.getOrganism().getName() +" ("+uniprotProteinTranscript.getOrganism().getTaxid()+")");

        // we will assign the proteins to two collections - primary / secondary
        Collection<ProteinImpl> primaryProteins = proteinDao.getByUniprotId(uniprotAc);
        Collection<ProteinImpl> secondaryProteins = new ArrayList<ProteinImpl>();

        // get the secondary accessions of the protein transcripts
        for (String secondaryAc : uniprotProteinTranscript.getSecondaryAcs()) {
            secondaryProteins.addAll(proteinDao.getByUniprotId(secondaryAc));
        }

        // filter and remove non-uniprot prots from the list, and assign to the primary or secondary collections
        ProteinTools.filterNonUniprotAndMultipleUniprot(primaryProteins);
        ProteinTools.filterNonUniprotAndMultipleUniprot(secondaryProteins);

        int countPrimary = primaryProteins.size();
        int countSecondary = secondaryProteins.size();

        if (log.isTraceEnabled()) log.trace("Found "+countPrimary+" primary and "+countSecondary+" secondary for "+uniprotAc);
        // TODO returned proteins are not used here

        return processProteinTranscript(uniprotProteinTranscript, uniprotProtein, masterProtein, primaryProteins, secondaryProteins);
    }

    /**
     * Process the protein transcript and update it
     * @param uniprotProteinTranscript
     * @param uniprotProtein
     * @param masterProtein
     * @param primaryProteins
     * @param secondaryProteins
     * @return
     * @throws ProteinServiceException
     */
    protected Collection<Protein> processProteinTranscript(UniprotProteinTranscript uniprotProteinTranscript, UniprotProtein uniprotProtein, Protein masterProtein, Collection<ProteinImpl> primaryProteins, Collection<ProteinImpl> secondaryProteins) throws ProteinServiceException {
        Collection<Protein> proteins = new ArrayList<Protein>();
        int countPrimary = primaryProteins.size();
        int countSecondary = secondaryProteins.size();

        boolean isPossibleToUpdate = true;

        if (countPrimary + countSecondary > 1){
            if (countPrimary >= 1 && countSecondary >= 1){
                processor.fireonProcessErrorFound(new UpdateErrorEvent(processor, IntactContext.getCurrentInstance().getDataContext(), "Unexpected number of proteins found in IntAct for UniprotEntry("+ uniprotProteinTranscript.getPrimaryAc() + ") " + countPrimary + " primary proteins, " + countSecondary + " secondary proteins, " +
                        "Please fix this problem manually.", UpdateError.unexpected_number_of_duplicates));
                if (log.isTraceEnabled()) log.debug("Request finalization, as this protein cannot be updated (several duplicated proteins not resolved)");
                processor.finalizeAfterCurrentPhase();

                if (countPrimary > 1){
                    isPossibleToUpdate = false;
                }
            }
            else if (countSecondary > 1 && countPrimary == 0){
                processor.fireonProcessErrorFound(new UpdateErrorEvent(processor, IntactContext.getCurrentInstance().getDataContext(), "Unresolved duplication of uniprot protein " + uniprotProteinTranscript.getPrimaryAc() + "("+countSecondary+" possible duplicated proteins)", UpdateError.unexpected_number_of_duplicates));
                if (log.isTraceEnabled()) log.debug("Request finalization, as this protein cannot be updated (several duplicated proteins not resolved)");
                processor.finalizeAfterCurrentPhase();

                isPossibleToUpdate = false;
            }
            else if (countPrimary > 1 && countSecondary == 0){
                processor.fireonProcessErrorFound(new UpdateErrorEvent(processor, IntactContext.getCurrentInstance().getDataContext(), "Unresolved duplication of the protein " + uniprotProteinTranscript.getPrimaryAc() + "("+countPrimary+" duplicated proteins)", UpdateError.unexpected_number_of_duplicates));
                if (log.isTraceEnabled()) log.debug("Request finalization, as this protein cannot be updated (several duplicated proteins not resolved)");
                processor.finalizeAfterCurrentPhase();

                isPossibleToUpdate = false;
            }
        }

        for (Protein protein : primaryProteins){
            proteins.add(protein);

            if (isPossibleToUpdate){
                updateProteinTranscript( protein, masterProtein, uniprotProteinTranscript, uniprotProtein );
            }
        }

        for (Protein protein : secondaryProteins){
            proteins.add(protein);
        }

        uniprotServiceResult.addAllToProteins(proteins);

        return proteins;
    }

    /**
     * Update an existing splice variant.
     *
     * @param transcript
     * @param uniprotTranscript
     */
    private boolean updateProteinTranscript( Protein transcript, Protein master,
                                             UniprotProteinTranscript uniprotTranscript,
                                             UniprotProtein uniprotProtein ) throws ProteinServiceException {

        if (!UpdateBioSource(transcript, uniprotTranscript.getOrganism())){
            return false;
        }

        transcript.setShortLabel( uniprotTranscript.getPrimaryAc().toLowerCase() );

        // we have a feature chain
        if (uniprotTranscript.getDescription() != null){
            transcript.setFullName(uniprotTranscript.getDescription());
        }
        // we have a splice variant
        else {
            transcript.setFullName( master.getFullName() );
        }

        // update UniProt Xrefs
        XrefUpdaterUtils.updateProteinTranscriptUniprotXrefs( transcript, uniprotTranscript, uniprotProtein );

        // Update Aliases from the uniprot protein aliases
        AliasUpdaterUtils.updateAllAliases( transcript, uniprotTranscript, uniprotProtein );

        // Sequence
        updateProteinSequence(transcript, uniprotTranscript.getSequence(), Crc64.getCrc64(uniprotTranscript.getSequence()));
        // Add IntAct Xref

        // Update Note
        String note = uniprotTranscript.getNote();
        if ( ( note != null ) && ( !note.trim().equals( "" ) ) ) {
            Institution owner = IntactContext.getCurrentInstance().getInstitution();
            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            CvObjectDao<CvTopic> cvDao = daoFactory.getCvObjectDao( CvTopic.class );
            CvTopic comment = cvDao.getByShortLabel( CvTopic.ISOFORM_COMMENT );

            if (comment == null) {
                comment = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvTopic.class, CvTopic.COMMENT_MI_REF, CvTopic.COMMENT);
                IntactContext.getCurrentInstance().getCorePersister().saveOrUpdate(comment);
            }

            Annotation annotation = new Annotation( owner, comment );
            annotation.setAnnotationText( note );
            AnnotationUpdaterUtils.addNewAnnotation( transcript, annotation );
        }

        // in case the protin transcript is a feature chain, we need to add two annotations containing the end and start positions of the feature chain
        if (CvXrefQualifier.CHAIN_PARENT_MI_REF.equalsIgnoreCase(uniprotTranscript.getParentXRefQualifier())){
            boolean hasStartPosition = false;
            boolean hasEndPosition = false;
            String startToString = Integer.toString(uniprotTranscript.getStart());
            String endToString = Integer.toString(uniprotTranscript.getEnd());

            DaoFactory factory = IntactContext.getCurrentInstance().getDaoFactory();

            // check if the annotated object already contains a start and or end position
            for (Annotation annot : transcript.getAnnotations()) {
                if (CvTopic.CHAIN_SEQ_START.equals(annot.getCvTopic().getShortLabel())) {
                    hasStartPosition = true;
                    if (uniprotTranscript.getStart() == -1){
                        annot.setAnnotationText(FEATURE_CHAIN_UNKNOWN_POSITION);
                    }
                    else {
                        annot.setAnnotationText(startToString);
                    }
                    factory.getAnnotationDao().update(annot);
                }
                else if (CvTopic.CHAIN_SEQ_END.equals(annot.getCvTopic().getShortLabel())) {
                    hasEndPosition = true;
                    if (uniprotTranscript.getEnd() == -1){
                        annot.setAnnotationText(FEATURE_CHAIN_UNKNOWN_POSITION);
                    }
                    else {
                        annot.setAnnotationText(endToString);
                    }
                    factory.getAnnotationDao().update(annot);
                }
            }

            CvObjectDao<CvTopic> cvTopicDao = factory.getCvObjectDao(CvTopic.class);

            if (!hasStartPosition){
                CvTopic startPosition = cvTopicDao.getByShortLabel(CvTopic.CHAIN_SEQ_START);

                if (startPosition == null){
                    startPosition = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvTopic.class, null, CvTopic.CHAIN_SEQ_START);
                    IntactContext.getCurrentInstance().getCorePersister().saveOrUpdate(startPosition);
                }
                Annotation start = new Annotation(startPosition, startToString);
                factory.getAnnotationDao().persist(start);

                transcript.addAnnotation(start);
            }
            if (!hasEndPosition){
                CvTopic endPosition = cvTopicDao.getByShortLabel(CvTopic.CHAIN_SEQ_END);

                if (endPosition == null){
                    endPosition = CvObjectUtils.createCvObject(IntactContext.getCurrentInstance().getInstitution(), CvTopic.class, null, CvTopic.CHAIN_SEQ_END);
                    IntactContext.getCurrentInstance().getCorePersister().saveOrUpdate(endPosition);
                }
                Annotation end = new Annotation(endPosition, endToString);
                factory.getAnnotationDao().persist(end);

                transcript.addAnnotation(end);
            }
        }

        // Persist changes
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        ProteinDao pdao = daoFactory.getProteinDao();
        pdao.update( ( ProteinImpl ) transcript );

        return true;
    }

    protected void deleteProtein(Protein protein) {
        processor.fireOnDelete(new ProteinEvent(processor, IntactContext.getCurrentInstance().getDataContext(), protein));
    }

    protected void sequenceChanged(Protein protein, String newSequence, String oldSequence, String crc64) {
        processor.fireOnProteinSequenceChanged(new ProteinSequenceChangeEvent(processor, IntactContext.getCurrentInstance().getDataContext(), protein, oldSequence, newSequence, crc64));
    }

    protected void invalidRangeFound(InvalidRange invalidRange){
        processor.fireOnInvalidRange(new InvalidRangeEvent(IntactContext.getCurrentInstance().getDataContext(), invalidRange));
    }

    protected void proteinCreated(Protein protein) {
        processor.fireOnProteinCreated(new ProteinEvent(processor, IntactContext.getCurrentInstance().getDataContext(), protein));
    }

    protected void badParticipantFound(Collection<Component> componentToFix, Protein protein){
        processor.fireOnOutOfDateParticipantFound(new OutOfDateParticipantFoundEvent(processor, IntactContext.getCurrentInstance().getDataContext(), protein, componentToFix));
    }

    /*@Override
    protected Protein processDuplication(UniprotProtein uniprotProtein, Collection<ProteinImpl> primaryProteins, Collection<ProteinImpl> secondaryProteins) throws ProteinServiceException {
        List<Protein> proteins = new ArrayList<Protein>(primaryProteins.size()+secondaryProteins.size());
        proteins.addAll(primaryProteins);
        proteins.addAll(secondaryProteins);
        DuplicatesFoundEvent event = new DuplicatesFoundEvent(processor, IntactContext.getCurrentInstance().getDataContext(), proteins, uniprotProtein.getSequence(), uniprotProtein.getCrc64());
        processor.fireOnProteinDuplicationFound(event);
        return event.getReferenceProtein();
    }

    @Override
    protected Protein processTranscriptDuplication(UniprotProteinTranscript uniprotProteinTranscript, UniprotProtein uniprot, Protein masterProtein, Collection<ProteinImpl> primaryProteins, Collection<ProteinImpl> secondaryProteins) throws ProteinServiceException {
        List<Protein> proteins = new ArrayList<Protein>(primaryProteins.size()+secondaryProteins.size());
        proteins.addAll(primaryProteins);
        proteins.addAll(secondaryProteins);
        DuplicatesFoundEvent event = new DuplicatesFoundEvent(processor, IntactContext.getCurrentInstance().getDataContext(), proteins, uniprot.getSequence(), uniprot.getCrc64());
        processor.fireOnProteinDuplicationFound(event);

        return event.getReferenceProtein();
    }*/

    public BioSourceService getBioSourceService() {
        return bioSourceService;
    }

    public void setBioSourceService(BioSourceService bioSourceService) {
        if ( bioSourceService == null ) {
            throw new IllegalArgumentException( "bioSourceService must not be null." );
        }
        this.bioSourceService = bioSourceService;
    }

    public UniprotServiceResult getUniprotServiceResult() {
        return uniprotServiceResult;
    }

    /**
     * Create a simple protein in view of updating it.
     * <p/>
     * It should contain the following elements: Shortlabel, Biosource and UniProt Xrefs.
     *
     * @param uniprotProtein the Uniprot protein we are going to build the intact on from.
     *
     * @return a non null, persisted intact protein.
     */
    private Protein createMinimalisticProtein( UniprotProtein uniprotProtein ) throws ProteinServiceException {
        try{
            if (uniprotProtein == null) {
                throw new NullPointerException("Passed a null UniprotProtein");
            }

            if (bioSourceService == null) {
                throw new IllegalStateException("BioSourceService should not be null");
            }

            DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
            ProteinDao pdao = daoFactory.getProteinDao();

            if (uniprotProtein.getOrganism() == null) {
                throw new IllegalStateException("Uniprot protein without organism: "+uniprotProtein);
            }

            BioSource biosource = null;
            try {
                biosource = bioSourceService.getBiosourceByTaxid( String.valueOf( uniprotProtein.getOrganism().getTaxid() ) );
            } catch ( BioSourceServiceException e ) {
                throw new ProteinServiceException(e);
            }

            TransactionStatus transactionStatus = IntactContext.getCurrentInstance().getDataContext().beginTransaction();

            Protein protein = new ProteinImpl( CvHelper.getInstitution(),
                    biosource,
                    generateProteinShortlabel( uniprotProtein ),
                    CvHelper.getProteinType() );
            protein.setSequence(uniprotProtein.getSequence());
            protein.setCrc64(uniprotProtein.getCrc64());

            pdao.persist( ( ProteinImpl ) protein );

            // Create UniProt Xrefs
            XrefUpdaterUtils.updateUniprotXrefs( protein, uniprotProtein );

            pdao.update( ( ProteinImpl ) protein );
            IntactContext.getCurrentInstance().getDataContext().commitTransaction(transactionStatus);
            return protein;

        }catch( IntactTransactionException e){
            throw new ProteinServiceException(e);
        }

    }

    private String generateProteinShortlabel( UniprotProtein uniprotProtein ) {

        String name = null;

        if ( uniprotProtein == null ) {
            throw new NullPointerException( "uniprotProtein must not be null." );
        }

        name = uniprotProtein.getId();

        return name.toLowerCase();
    }

    /**
     * Create a simple splice variant or feature chain in view of updating it.
     * <p/>
     * It should contain the following elements: Shorltabel, Biosource and UniProt Xrefs.
     *
     * @param uniprotProteinTranscript the Uniprot transcript we are going to build the intact on from.
     *
     * @return a non null, persisted intact protein.
     */
    private Protein createMinimalisticProteinTranscript( UniprotProteinTranscript uniprotProteinTranscript,
                                                         String masterAc,
                                                         BioSource masterBiosource,
                                                         UniprotProtein uniprotProtein
    ) {

        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        ProteinDao pdao = daoFactory.getProteinDao();

        Protein variant = new ProteinImpl( CvHelper.getInstitution(),
                masterBiosource,
                uniprotProteinTranscript.getPrimaryAc().toLowerCase(),
                CvHelper.getProteinType() );

        if (uniprotProteinTranscript.getSequence() != null) {
            variant.setSequence(uniprotProteinTranscript.getSequence());
            variant.setCrc64(Crc64.getCrc64(variant.getSequence()));
        } else if (!uniprotProteinTranscript.isNullSequenceAllowed()){
            log.warn("Uniprot splice variant without sequence: "+variant);
        }

        pdao.persist( ( ProteinImpl ) variant );

        // Create isoform-parent or chain-parent Xref
        CvXrefQualifier isoformParent = CvHelper.getQualifierByMi( uniprotProteinTranscript.getParentXRefQualifier() );
        CvDatabase intact = CvHelper.getDatabaseByMi( CvDatabase.INTACT_MI_REF );
        InteractorXref xref = new InteractorXref( CvHelper.getInstitution(), intact, masterAc, isoformParent );
        variant.addXref( xref );
        XrefDao xdao = daoFactory.getXrefDao();
        xdao.persist( xref );

        // Create UniProt Xrefs
        XrefUpdaterUtils.updateProteinTranscriptUniprotXrefs( variant, uniprotProteinTranscript, uniprotProtein );

        pdao.update( ( ProteinImpl ) variant );

        return variant;
    }

    private String getProteinDescription(Protein protein){
        InteractorXref uniprotXref = ProteinUtils.getUniprotXref(protein);
        return "[" + protein.getAc() + ","+ protein.getShortLabel() + "," + uniprotXref.getPrimaryId() + "]";
    }
}
