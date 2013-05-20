package uk.ac.ebi.intact.protein.mapping.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.protein.mapping.actions.*;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.IntactCrc64Report;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.PICRReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.BlastContext;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.model.UniprotProtein;
import uk.ac.ebi.intact.uniprot.service.UniprotService;

import java.util.ArrayList;
import java.util.List;

/**
 * This strategy aims at identifying a protein using its sequence and organism. It can be also a complex IdentificationAction
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class StrategyWithSequence extends IdentificationStrategyImpl implements IdentificationAction {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyWithSequence.class );

    /**
     * the intact context of this object if we want to look into Intact.
     */
    private boolean enableIntactSearch = false;

    /**
     * A boolean value to know if we want to run a Blast if PICR can't map the sequence to any uniprot entry
     */
    private boolean isBasicBlastRequired = false;

    /**
     * the list of reports of this object
     */
    private List<MappingReport> listOfReports = new ArrayList<MappingReport>();

    /**
     * Create a new StrategyWithSequence
     */
    public StrategyWithSequence(){
        super();
    }

    public StrategyWithSequence(UniprotService uniprotService) {
        super(uniprotService);
    }

    /**
     *
     * @return true if we want to process a blast on uniprot if PICR couldn't map any uniprot accession to the sequence
     */
    public boolean isBasicBlastRequired() {
        return isBasicBlastRequired;
    }

    /**
     * set the basicBlastRequired boolean
     * @param basicBlastRequired
     */
    public void setBasicBlastRequired(boolean basicBlastRequired) {
        isBasicBlastRequired = basicBlastRequired;
    }

    /**
     * Run a Blast on uniprot and keep the results in a report
     * @param context : the context of the protein
     * @param result : the result of the strategy
     * @return always null as we can't take a decision with only BLAST results. However, we have one report with the results of the BLAST on uniprot
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    private void processLastAction(IdentificationContext context, IdentificationResults result) throws ActionProcessingException {

        // create a blast context
        BlastContext blastContext = new BlastContext(context);
        blastContext.setSequence(context.getSequence());

        // run the blast. We don't expect any swissprot accession as we just want to have the blast results in a report
        this.listOfActions.get(3).runAction(blastContext);
        // add the reports
        if (result != null){
            result.getListOfActions().addAll(this.listOfActions.get(3).getListOfActionReports());
        }
        else {
            this.listOfReports.addAll(this.listOfActions.get(3).getListOfActionReports());
        }
    }

    /**
     * Query PICR with the sequence first. If PICR didn't return any results, we can run a CRC64 search on Intact if the intact context is not null and/or
     * we can run a BLAST on uniprot if the boolean value isBasicBlastRequired is set to true
     * @param context : the context of the protein to identify
     * @return the result instance containing the information and results of this strategy
     * @throws uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException
     */
    @Override
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {
        // create the result instance
        IdentificationResults result = getResultsFactory().getIdentificationResults();

        // this strategy needs a sequence of the protein to identify
        if (context.getSequence() == null){
            throw new StrategyException("The sequence of the protein must be not null.");
        }
        else{

            try {

                // Query PICR with the sequence and the organism
                String uniprot = this.listOfActions.get(0).runAction(context);
                // add the reports in  the result
                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                // process the isoforms and set the uniprot id of the result
                processIsoforms(uniprot, result);
                // get the PICR report
                PICRReport lastAction = (PICRReport) result.getLastAction();

                // If PICR didn't return any Uniprot accession
                if (!result.hasUniqueUniprotId() && lastAction.getPossibleAccessions().isEmpty()){
                    // we can run a CRC64 search on Intact if it is enabled
                    if (isEnableIntactSearch()){
                        // get the intact process and set the intact context
                        IntactCrc64SearchProcess intactProcess = (IntactCrc64SearchProcess) this.listOfActions.get(1);

                        // run the search on Intact. We don't expect any uniprot accession as we are looking for an intact accession
                        intactProcess.runAction(context);
                        // add the reports to the result
                        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

                        // get the last report
                        IntactCrc64Report lastReport = (IntactCrc64Report) result.getLastAction();
                        // if the Intact search failed and the BLAST process is enabled, we process the BLAST on uniprot
                        if (lastReport.getIntactAc() == null && lastReport.getPossibleIntactIds().isEmpty() && isBasicBlastRequired){
                            processLastAction(context, result);
                        }
                    }
                    // if we don't have an intact context but the BLAST is enabled, we process a BLAST on uniprot
                    else if (isBasicBlastRequired) {
                        processLastAction(context, result);
                    }
                }
                // PICR was successful
                else {
                    // PICR could map the sequence to a Trembl entry
                    if (result.hasUniqueUniprotId() && !lastAction.isASwissprotEntry()){
                        // get the uniprot protein for the Trembl entry
                        UniprotProtein tremblEntry = getUniprotProteinFor(result.getFinalUniprotId());
                        String sequence = tremblEntry.getSequence();

                        // create a blast context
                        BlastContext blastContext = new BlastContext(context);
                        blastContext.setSequence(sequence);

                        // extract the Ensembl gene accession
                        if (tremblEntry != null){
                            String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                            blastContext.setEnsemblGene(ensemblGene);
                        }
                        else {
                            throw new StrategyException("We couldn't find any Uniprot entries which match this accession number " + result.getFinalUniprotId());
                        }

                        // run a swissprot remapping process
                        uniprot = this.listOfActions.get(2).runAction(blastContext);
                        // add the report to the result
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

                throw  new StrategyException("An error occured while trying to identify the protein using the sequence " + context.getSequence(), e);

            }
            return result;
        }
    }

    /**
     * initialises the set of actions for this object
     */
    @Override
    protected void initialiseSetOfActions() {
        // the first action is a PICR query using the sequence
        PICRSearchProcessWithSequence firstAction = new PICRSearchProcessWithSequence(getReportsFactory());
        this.listOfActions.add(firstAction);

        // the second action is a CRC64 search on Intact (only if the intact context is not null)
        IntactCrc64SearchProcess secondAction = new IntactCrc64SearchProcess(getReportsFactory());
        this.listOfActions.add(secondAction);

        // the third action is a swissprot remapping process
        SwissprotRemappingProcess thirdAction = new SwissprotRemappingProcess(getReportsFactory());
        this.listOfActions.add(thirdAction);

        // the last action is a BLAST on uniprot (only if the Blast is enabled)
        BasicBlastProcess lastAction = new BasicBlastProcess(getReportsFactory());
        this.listOfActions.add(lastAction);
    }

    /**
     * Query PICR with the sequence first. If PICR didn't return any results, we can run a CRC64 search on Intact if the intact context is not null and/or
     * we can run a BLAST on uniprot if the boolean value isBasicBlastRequired is set to true
     * @param context  : the context of the protein
     * @return an unique uniprot accession if possible, null otherwise
     * @throws ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // always clear the previous reports
        this.listOfReports.clear();

        // query PICR with the sequence
        String uniprot = this.listOfActions.get(0).runAction(context);
        // process the isoforms
        uniprot = processIsoforms(uniprot);
        // collect the reports and add them to the list of reports
        this.listOfReports.addAll(this.listOfActions.get(0).getListOfActionReports());
        // get the PICR report
        PICRReport report = (PICRReport) this.listOfReports.get(this.listOfReports.size() - 1);

        // If PICR didn't return any Uniprot accession
        if (uniprot == null && report.getPossibleAccessions().isEmpty()){
            // we can run a CRC64 search on Intact if it is enabled
            if (isEnableIntactSearch()){
                // get the intact process and set the intact context
                IntactCrc64SearchProcess intactProcess = (IntactCrc64SearchProcess) this.listOfActions.get(1);

                // run the search on Intact. We don't expect any uniprot accession as we are looking for an intact accession                
                intactProcess.runAction(context);
                // add the reports to the result
                this.listOfReports.addAll(this.listOfActions.get(1).getListOfActionReports());
                // get the last report
                IntactCrc64Report report2 = (IntactCrc64Report) this.listOfReports.get(this.listOfReports.size() - 1);

                // if the Intact search failed and the BLAST process is enabled, we process the BLAST on uniprot
                if (report2.getIntactAc() == null && report2.getPossibleIntactIds().isEmpty() && isBasicBlastRequired){
                    processLastAction(context, null);
                }
            }
            // if we don't have an intact context but the BLAST is enabled, we process a BLAST on uniprot
            else if (isBasicBlastRequired) {
                processLastAction(context, null);
            }
        }
        // PICR was successful
        else {
            // PICR could map the sequence to a Trembl entry
            if (uniprot != null && !report.isASwissprotEntry()){
                // get the uniprot protein for the Trembl entry
                UniprotProtein tremblEntry = getUniprotProteinFor(uniprot);

                if (tremblEntry != null){
                    String sequence = tremblEntry.getSequence();

                    // create a Blast context
                    BlastContext blastContext = new BlastContext(context);
                    blastContext.setSequence(sequence);

                    // extract the Ensembl gene accession
                    String ensemblGene = extractENSEMBLGeneAccessionFrom(tremblEntry.getCrossReferences());
                    blastContext.setEnsemblGene(ensemblGene);

                    // run a swissprot remapping process
                    String uniprot2 = this.listOfActions.get(2).runAction(blastContext);
                    // process the isoforms
                    uniprot2 = processIsoforms(uniprot2);

                    // if the swissprot remapping process was successful, we replace the trembl accession with the swissprot one
                    if (uniprot2 != null){
                        uniprot = uniprot2;
                    }
                    // add the reports to the list of reports
                    this.listOfReports.addAll(this.listOfActions.get(2).getListOfActionReports());

                    List<BlastReport> listOfSwissprotRemappingReports = getSwissprotRemappingReports(this.listOfReports);

                    for (BlastReport<BlastResults> sr : listOfSwissprotRemappingReports){
                        for (BlastResults r : sr.getBlastMatchingProteins()){
                            r.setTremblAccession(tremblEntry.getPrimaryAc());
                        }
                    }

                }
                else {
                    throw new ActionProcessingException("We couldn't find any Uniprot entries which match this accession number " + uniprot);
                }
            }
        }
        return uniprot;

    }

    public List<MappingReport> getListOfActionReports() {
        return this.listOfReports;
    }

    public boolean isEnableIntactSearch() {
        return enableIntactSearch;
    }

    public void setEnableIntactSearch(boolean enableIntactSearch) {
        this.enableIntactSearch = enableIntactSearch;
    }
}
