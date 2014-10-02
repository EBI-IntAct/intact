package uk.ac.ebi.intact.protein.mapping.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.protein.mapping.actions.CrossReferenceSearchProcess;
import uk.ac.ebi.intact.protein.mapping.actions.IdentificationAction;
import uk.ac.ebi.intact.protein.mapping.actions.PICRSearchProcessWithAccession;
import uk.ac.ebi.intact.protein.mapping.actions.SwissprotRemappingProcess;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.BlastContext;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.UniprotService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This strategy aims at identifying a protein using its identifier and organism. It can be also a complex IdentificationAction
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Mar-2010</pre>
 */

public class StrategyWithIdentifier extends IdentificationStrategyImpl implements IdentificationAction {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithIdentifier.class );

    /**
     * The list of reports of this action
     */
    private List<MappingReport> listOfReports = new ArrayList<MappingReport> ();

    /**
     * the list of special organisms entirely sequenced
     */
    private static ArrayList<String> organismWithSpecialCase = new ArrayList<String>();

    /**
     * List of database MI numbers that PICR can manage
     */
    public static Set<String> listOfDatabaseNamesManagedByPICR = new HashSet<String>();

    /**
     * List of database MI numbers that PICR can manage
     */
    public static Set<String> listOfMIDatabasesManagedByPICR = new HashSet<String>();

    /**
     * Create a new strategy with identifier
     */
    public StrategyWithIdentifier(){
        super();
        if (listOfMIDatabasesManagedByPICR.isEmpty()){
            initialiseListOfMIDatabasesManagedByPICR();
            initialiseListOfDatabaseNamesManagedByPICR();
        }
    }

    public StrategyWithIdentifier(UniprotService uniprotService) {
        super(uniprotService);
        if (listOfMIDatabasesManagedByPICR.isEmpty()){
            initialiseListOfMIDatabasesManagedByPICR();
            initialiseListOfDatabaseNamesManagedByPICR();
        }
    }

    private void initialiseListOfMIDatabasesManagedByPICR(){
        listOfMIDatabasesManagedByPICR.add(CvDatabase.UNIPROT_MI_REF);
        listOfMIDatabasesManagedByPICR.add(CvDatabase.FLYBASE_MI_REF);
        listOfMIDatabasesManagedByPICR.add(CvDatabase.DDBG_MI_REF);
        listOfMIDatabasesManagedByPICR.add(CvDatabase.REFSEQ_MI_REF);
        // genbank
        listOfMIDatabasesManagedByPICR.add("MI:0860");
        // genbank protein
        listOfMIDatabasesManagedByPICR.add("MI:0851");
        // genbank nucl
        listOfMIDatabasesManagedByPICR.add("MI:0852");
        // wormbase
        listOfMIDatabasesManagedByPICR.add("MI:0487");
        // ipi
        listOfMIDatabasesManagedByPICR.add("MI:0675");
        listOfMIDatabasesManagedByPICR.add(CvDatabase.ENSEMBL_MI_REF);
        listOfMIDatabasesManagedByPICR.add(CvDatabase.WWPDB_MI_REF);
        listOfMIDatabasesManagedByPICR.add(CvDatabase.RCSB_PDB_MI_REF);
        // het
        listOfMIDatabasesManagedByPICR.add("MI:2017");
        // pdbe
        listOfMIDatabasesManagedByPICR.add("MI:0472");
        // emdb
        listOfMIDatabasesManagedByPICR.add("MI:0936");
        // pdbj
        listOfMIDatabasesManagedByPICR.add("MI:0806");
        listOfMIDatabasesManagedByPICR.add(CvDatabase.SGD_MI_REF);
        listOfMIDatabasesManagedByPICR.add(CvDatabase.UNIPARC_MI_REF);
    }

    private void initialiseListOfDatabaseNamesManagedByPICR(){
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.UNIPROT);
        listOfDatabaseNamesManagedByPICR.add("SwissProt");
        listOfDatabaseNamesManagedByPICR.add("TrEMBL");
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.FLYBASE);
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.DDBG);
        listOfDatabaseNamesManagedByPICR.add("EMBL");
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.REFSEQ);
        listOfDatabaseNamesManagedByPICR.add("genbank indentifier");
        listOfDatabaseNamesManagedByPICR.add("genbank_protein_gi");
        listOfDatabaseNamesManagedByPICR.add("genbank_nucl_gi");
        listOfDatabaseNamesManagedByPICR.add("JPO");
        listOfDatabaseNamesManagedByPICR.add("PIR");
        listOfDatabaseNamesManagedByPICR.add("TAIR");
        listOfDatabaseNamesManagedByPICR.add("UniMES");
        listOfDatabaseNamesManagedByPICR.add("USPTO");
        listOfDatabaseNamesManagedByPICR.add("wormbase");
        listOfDatabaseNamesManagedByPICR.add("SEGUID");
        listOfDatabaseNamesManagedByPICR.add("ipi");
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.ENSEMBL);
        listOfDatabaseNamesManagedByPICR.add("EPO");
        listOfDatabaseNamesManagedByPICR.add("H Inv");
        listOfDatabaseNamesManagedByPICR.add("PDB");
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.WWPDB);
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.RCSB_PDB);
        listOfDatabaseNamesManagedByPICR.add("het");
        listOfDatabaseNamesManagedByPICR.add("pdbe");
        listOfDatabaseNamesManagedByPICR.add("eMDB");
        listOfDatabaseNamesManagedByPICR.add("pdbj");
        listOfDatabaseNamesManagedByPICR.add("PRF");
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.SGD);
        listOfDatabaseNamesManagedByPICR.add("TROME");
        listOfDatabaseNamesManagedByPICR.add(CvDatabase.UNIPARC);
        listOfDatabaseNamesManagedByPICR.add("VEGA");
        listOfDatabaseNamesManagedByPICR.add("KIPO");
    }

    /**
     * Initialise the set of actions for this strategy
     */
    protected void initialiseSetOfActions(){

        // first action = PICRSearchProcessWithAccession
        PICRSearchProcessWithAccession firstAction = new PICRSearchProcessWithAccession(getReportsFactory());
        this.listOfActions.add(firstAction);

        // second action = CrossReferenceSearchProcess (optional)
        CrossReferenceSearchProcess secondAction = new CrossReferenceSearchProcess(getReportsFactory(), uniprotService);
        this.listOfActions.add(secondAction);

        // third action = SwissprotRemappingProcess
        SwissprotRemappingProcess thirdAction = new SwissprotRemappingProcess(getReportsFactory());
        this.listOfActions.add(thirdAction);
    }

    /**
     * initialises the list of special organisms
     */
    public static final void initialiseOrganismWithSpecialCase(){
        organismWithSpecialCase.add("9606");
        organismWithSpecialCase.add("4932");
        organismWithSpecialCase.add("4896");
        organismWithSpecialCase.add("562");
        organismWithSpecialCase.add("1423");
    }

    /**
     *
     * @param taxId : the taxId to check
     * @return  true if it is a special organism entirely sequenced
     */
    private boolean isASpecialOrganism(String taxId){

        if (organismWithSpecialCase.isEmpty()){
            initialiseOrganismWithSpecialCase();
        }

        if (organismWithSpecialCase.contains(taxId)){
            return true;
        }
        return false;
    }

    private boolean isADatabaseManagedByPICR(String database, String databaseName){

        if (database != null){
            if (listOfMIDatabasesManagedByPICR.contains(database)){
                return true;
            }
            else {
                for (String name : listOfDatabaseNamesManagedByPICR){
                    if (name.equalsIgnoreCase(database)){
                        return true;
                    }
                }
            }
        }
        else if (databaseName != null){
            for (String name : listOfDatabaseNamesManagedByPICR){
                if (name.equalsIgnoreCase(database)){
                    return true;
                }
            }
        }
        return false;
    }

    private void runCrossReferenceProcess(IdentificationContext context, IdentificationResults result) throws ActionProcessingException {
        String uniprotResult = this.listOfActions.get(1).runAction(context);
        // get the reports of the second action
        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

        MappingReport lastReport = result.getLastAction();

        processIsoforms(uniprotResult, result);
    }

    private String runCrossReferenceProcess(IdentificationContext context) throws ActionProcessingException {
        String uniprot = this.listOfActions.get(1).runAction(context);
        // get the reports of the second action
        this.listOfReports.addAll(this.listOfActions.get(1).getListOfActionReports());
        // process the isoforms and set the uniprot id of the result
        uniprot = processIsoforms(uniprot);

        return uniprot;
    }

    /**
     * This strategy is using PICR and/or uniprot cross reference search to map the identifier to an unique uniprot AC. If an unique Trembl is found,
     * the strategy will use the SwissprotRemappingProcess to remap the trembl entry to a Swissprot entry.
     * @param context : the context of the protein to identify
     * @return the results
     * @throws uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException
     */
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {
        // new result
        IdentificationResults result = getResultsFactory().getIdentificationResults();

        // the strategy is based on the identifier
        if (context.getIdentifier() == null){
            throw new StrategyException("The identifier of the protein must be not null.");
        }
        else{

            try {
                String uniprot = null;

                if (context.getDatabaseForIdentifier() == null && context.getDatabaseName() == null){
                    log.warn("The identifier " + context.getIdentifier() + " is not associated to a database name or MI, so we will not use PICR" +
                            " to map this identifier to an uniprot entry.");
                }

                if (isADatabaseManagedByPICR(context.getDatabaseForIdentifier(), context.getDatabaseName())){
                    // result of PICR
                    uniprot = this.listOfActions.get(0).runAction(context);
                    // get the reports of the first action
                    result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());

                    if (uniprot == null && result.getLastAction().getPossibleAccessions().isEmpty()){
                        String taxId = null;
                        if (context.getOrganism() != null){
                            taxId = context.getOrganism().getTaxId();
                        }

                        if (taxId != null){
                            if (!taxId.startsWith("-")){
                                runCrossReferenceProcess(context, result);
                            }
                        }
                        else {
                            runCrossReferenceProcess(context, result);
                        }
                    }

                    // process the isoforms and set the uniprot id of the result
                    processIsoforms(uniprot, result);
                }
                else {
                    runCrossReferenceProcess(context, result);
                }

                // PICR and uniprot could map the identifier to a Swissprot accession
                if (result.getFinalUniprotId() != null){
                    MappingReport report = result.getLastAction();

                    // PICR or uniprot could map to a Trembl entry
                    if (!report.isASwissprotEntry()){

                        // get the uniprot protein
                        UniprotProtein tremblEntry = getUniprotProteinFor(result.getFinalUniprotId());
                        String sequence = tremblEntry.getSequence();

                        // create a blast context for the swissprotRemapping process
                        BlastContext blastContext = new BlastContext(context);
                        blastContext.setSequence(sequence);

                        // the Trembl entry should not be null
                        if (tremblEntry != null){

                            // we extract its ensembl gene accession
                            String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                            blastContext.setEnsemblGene(ensemblGene);
                        }
                        else {
                            throw new StrategyException("We couldn't find any Uniprot entries which match this accession number " + result.getFinalUniprotId());
                        }

                        // run the swissprotRemappingProcess
                        uniprot = this.listOfActions.get(2).runAction(blastContext);
                        // add the reports of the second action
                        result.getListOfActions().addAll(this.listOfActions.get(2).getListOfActionReports());
                        // process the isoforms and set the uniprot id of the result
                        processIsoforms(uniprot, result);

                        List<BlastReport> listOfSwissprotRemappingReports = getSwissprotRemappingReports(result.getListOfActions());

                        for (BlastReport<BlastResults> sr : listOfSwissprotRemappingReports){
                            for (BlastResults r : sr.getBlastMatchingProteins()){
                                r.setTremblAccession(tremblEntry.getPrimaryAc());
                            }
                        }
                    }
                }

            } catch (ActionProcessingException e) {

                throw  new StrategyException("An error occured while trying to identify the protein using the identifier " + context.getIdentifier(), e);

            }
            return result;
        }
    }

    /**
     * This action is using PICR and/or uniprot cross reference search to map the identifier to an unique uniprot AC. If an unique Trembl is found,
     * the strategy will use the SwissprotRemappingProcess to remap the trembl entry to a Swissprot entry.
     * @param context  : the context of the protein
     * @return an unique uniprot Accession if possible, null otherwise
     * @throws ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // Always clear the previous reports
        this.listOfReports.clear();
        String uniprot = null;

        if (context.getDatabaseForIdentifier() == null && context.getDatabaseName() == null){
            log.warn("The identifier " + context.getIdentifier() + " is not associated to a database name or MI, so we will not use PICR" +
                    " to map this identifier to an uniprot entry.");
        }

        if (isADatabaseManagedByPICR(context.getDatabaseForIdentifier(), context.getDatabaseName())){
            // result of PICR
            uniprot = this.listOfActions.get(0).runAction(context);
            // get the reports of the first action
            this.listOfReports.addAll(this.listOfActions.get(0).getListOfActionReports());

            MappingReport lastReport =  this.listOfReports.get(this.listOfReports.size() - 1);

            if (uniprot == null && lastReport.getPossibleAccessions().isEmpty()){
                String taxId = null;
                if (context.getOrganism() != null){
                    taxId = context.getOrganism().getTaxId();
                }

                if (taxId != null){
                    if (!taxId.startsWith("-")){
                        uniprot = runCrossReferenceProcess(context);
                    }
                }
                else {
                    uniprot = runCrossReferenceProcess(context);
                }
            }

            // process the isoforms
            uniprot = processIsoforms(uniprot);
        }
        else {
            uniprot = runCrossReferenceProcess(context);
        }

        // Get the last report
        MappingReport report = this.listOfReports.get(this.listOfReports.size() - 1);

        // If PICR and Uniprot could mapp the identifier to an unique Uniprot accession
        if (uniprot != null){

            // if the accession is a Trembl accession
            if (!report.isASwissprotEntry()){

                // Get the Uniprot protein for this Trembl entry
                UniprotProtein tremblEntry = getUniprotProteinFor(uniprot);

                // Create a new blast context containing the ensembl gane of the Trembl entry
                BlastContext blastContext = new BlastContext(context);
                if (tremblEntry != null){
                    String sequence = tremblEntry.getSequence();

                    blastContext.setSequence(sequence);

                    // extract the ensembl gene of the Trembl entry
                    String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                    blastContext.setEnsemblGene(ensemblGene);
                }
                else {
                    throw new ActionProcessingException("We couldn't find any Uniprot entries which match this accession number " + uniprot);
                }

                // Try to do a Swissprot-remapping process
                String uniprot2 = this.listOfActions.get(2).runAction(blastContext);
                // process the isoforms
                uniprot2 = processIsoforms(uniprot2);

                // If the swissprot remapping process is successful, this action return the swissprot accession  instead of the Trembl accession
                if (uniprot2 != null){
                    uniprot = uniprot2;
                }
                // add the reports
                this.listOfReports.addAll(this.listOfActions.get(2).getListOfActionReports());

                List<BlastReport> listOfSwissprotRemappingReports = getSwissprotRemappingReports(this.listOfReports);

                for (BlastReport<BlastResults> sr : listOfSwissprotRemappingReports){
                    for (BlastResults r : sr.getBlastMatchingProteins()){
                        r.setTremblAccession(tremblEntry.getPrimaryAc());
                    }
                }
            }
        }
        return uniprot;
    }

    /**
     *
     * @return the list of reports of this object
     */
    public List<MappingReport> getListOfActionReports() {
        return this.listOfReports;
    }
}
