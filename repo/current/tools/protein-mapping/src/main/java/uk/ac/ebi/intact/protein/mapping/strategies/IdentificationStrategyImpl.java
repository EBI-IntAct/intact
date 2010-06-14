package uk.ac.ebi.intact.protein.mapping.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.protein.mapping.actions.IdentificationAction;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.ActionName;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.ActionReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.model.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.model.UniprotXref;
import uk.ac.ebi.intact.uniprot.service.IdentifierChecker;
import uk.ac.ebi.intact.uniprot.service.UniprotRemoteService;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The abstract implementation of an IdentificationStrategy
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public abstract class IdentificationStrategyImpl implements IdentificationStrategy{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( IdentificationStrategyImpl.class );

    /**
     * The uniprot service
     */
    private static UniprotRemoteService uniprotService = new UniprotRemoteService();

    /**
     * The list of actions used by the strategy to identify a protein
     */
    protected ArrayList<IdentificationAction> listOfActions = new ArrayList<IdentificationAction>();

    /**
     * The taxId pattern
     */
    private static final Pattern taxIdExpr = Pattern.compile("[0-9]+");

    /**
     * the ensembl gene pattern
     */
    private static Pattern ensemblGenePattern = Pattern.compile("ENS[A-Z]*G[0-9]+");

    /**
     * the boolean value to know if we keep the isoform accessions
     */
    private boolean enableIsoformId = false;

    /**
     * Create a new IdentificationStrategyImpl
     */
    public IdentificationStrategyImpl(){
        // initialise the set of actions for this strategy
        initialiseSetOfActions();
    }

    /**
     *
     * @param context : the context of the protein to identify
     * @return
     * @throws uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException
     */
    public abstract IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException;

    /**
     *
     * @param enableIsoformId : the boolean value
     */
    public void enableIsoforms(boolean enableIsoformId) {
        this.enableIsoformId = enableIsoformId;
    }

    /**
     *
     * @return the boolean enableIsoformId of this strategy
     */
    public boolean isIsoformEnabled(){
        return enableIsoformId;
    }

    /**
     * initialise the set of actions used by this strategy
     */
    protected abstract void initialiseSetOfActions();

    /**
     * Extract the ensembl gene accession (if any) among the list of uniprot cross references
     * @param crossReferences : the Uniprot cross references
     * @return the ensembl gene accession (if any) among the list of uniprot cross references, null otherwise
     */
    protected static String extractENSEMBLGeneAccessionFrom(Collection<UniprotXref> crossReferences){

        for (UniprotXref xRef : crossReferences){
            if (xRef.getDatabase() != null){
                if (DatabaseType.ENSEMBL.toString().equalsIgnoreCase(xRef.getDatabase())){
                    String accession = xRef.getAccession();

                    if (ensemblGenePattern.matcher(accession).matches()){
                        return accession;
                    }
                    return xRef.getAccession();
                }
            }
        }

        return null;
    }

    /**
     * Get the uniprot cross references of the uniprot entry matching this accession and extract the ensembl gene accession if any
     * @param uniprotAccession : the uniprot accession
     * @return the ensembl gene accession (if any) of this protein, null otherwise
     * @throws StrategyException
     */
    public static String extractENSEMBLGeneAccessionFrom(String uniprotAccession) throws StrategyException{
        UniprotProtein entry = getUniprotProteinFor(uniprotAccession);

        if (entry != null){
            String ensemblGene = extractENSEMBLGeneAccessionFrom(entry.getCrossReferences());
            return ensemblGene;
        }
        else {
            throw new StrategyException("We couldn't find an Uniprot entry which matches this accession number " + uniprotAccession);
        }
    }

    /**
     * Extract the ensembl gene accession from this uniprotProtein
     * @param protein : the protein
     * @return the ensembl gene accession (if any) of this protein, null otherwise
     */
    public static String extractENSEMBLGeneAccessionFrom(UniprotProtein protein){

        if (protein != null){
            String ensemblGene = extractENSEMBLGeneAccessionFrom(protein.getCrossReferences());
            return ensemblGene;
        }
        return null;
    }

    /**
     * process the isoforms accessions and set the uniprot id of the result to 'matchingId'
     * @param matchingId : the uniprot accession returned by one of the actions of this strategy
     * @param result : the result
     */
    protected void processIsoforms(String matchingId, IdentificationResults result) {
        if (matchingId != null){
            if (!this.enableIsoformId){
                if (IdentifierChecker.isSpliceVariantId(matchingId)){
                    if (result == null){
                        result = new IdentificationResults();
                    }

                    if (result.getLastAction() != null){
                        result.getLastAction().addWarning("The identified Uniprot Id is the isoform "+ matchingId +". However, the canonical sequence has been kept.");                        
                    }
                    else {
                        log.error("A uniprot id has been set : " + result.getFinalUniprotId() + ", but no action have been reported.");
                    }
                    matchingId = matchingId.substring(0, matchingId.indexOf("-"));
                    result.setFinalUniprotId(matchingId);
                }
                else {
                    result.setFinalUniprotId(matchingId);
                }
            }
            else {
                result.setFinalUniprotId(matchingId);
            }
        }

    }

    /**
     * process the isoforms accessions
     * @param matchingId
     * @return the matching id after we have remapped or not the isoform to its canonical sequence
     */
    protected String processIsoforms(String matchingId) {
        String id = matchingId;
        if (matchingId != null){
            if (!this.enableIsoformId){
                if (IdentifierChecker.isSpliceVariantId(matchingId)){
                    id = matchingId.substring(0, matchingId.indexOf("-"));
                }
            }
        }
       return id;
    }

    /**
     * Get the uniprotProtein with this accession in Uniprot
     * @param accession : the uniprot accession
     * @return the uniprotProtein with this accession in Uniprot
     */
    protected static UniprotProtein getUniprotProteinFor(String accession){
        if (accession == null){
            log.error("You must give a non null Uniprot accession");
        }
        else {
            Collection<UniprotProtein> entries = uniprotService.retrieve(accession);

            if (entries.isEmpty()){
                log.error("The uniprot accession " + accession + " is not valid and couldn't match any UniprotEntry.");
            }
            else if (entries.size() != 1){
                log.error("The uniprot accession " + accession + " is matching several UniprotEntry instances.");
            }
            else {

                return entries.iterator().next();
            }

        }
        return null;
    }

   /**
     *
     * @param listOfActions : the list of actions to look into
     * @return the list of swissprot remapping reports which have been done to identify the protein
     */
    protected List<BlastReport> getSwissprotRemappingReports(List<ActionReport> listOfActions){
        ArrayList<BlastReport> reports = new ArrayList<BlastReport>();

        for (ActionReport action : listOfActions){
            if (action instanceof BlastReport){
                BlastReport blastReport = (BlastReport) action;

                if (blastReport.getName().equals(ActionName.BLAST_Swissprot_Remapping)){
                    reports.add( blastReport);                     
                }
            }
        }
        return reports;
    }
}
