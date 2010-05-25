package uk.ac.ebi.intact.curationTools.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.curationTools.actions.FeatureRangeCheckingProcess;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.FeatureRangeCheckingContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.model.contexts.UpdateContext;
import uk.ac.ebi.intact.curationTools.model.results.BlastResults;
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.model.results.UpdateResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The strategy to update proteins (add an uniprot cross reference with qualifier set to 'identity' and remove the uniprot-no-update)
 * It is using both strategyWithIdentifier and StrategyWithSequence
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Apr-2010</pre>
 */

public class StrategyForProteinUpdate extends IdentificationStrategyImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( StrategyForProteinUpdate.class );

    /**
     * boolean value to know if we want to process a blast when the results on PICR are unsuccessful and the sequence is not null.
     */
    private boolean isBasicBlastProcessRequired = false;

    /**
     * Create a Strategy for protein update
     */
    public StrategyForProteinUpdate(){
        super();
    }

    /**
     *
     * @return  the isBasicBlastProcessRequired boolean
     */
    public boolean isBasicBlastProcessRequired() {
        return isBasicBlastProcessRequired;
    }

    /**
     * set the isBasicBlastProcessRequired boolean value of this object and the one of the strategyWithSequence instance that contains
     * this object
     * @param basicBlastProcessRequired
     */
    public void setBasicBlastProcessRequired(boolean basicBlastProcessRequired) {
        isBasicBlastProcessRequired = basicBlastProcessRequired;
        // the first action of this object is a StrategyWithSequence
        StrategyWithSequence firstAction = (StrategyWithSequence) this.listOfActions.get(0);
        firstAction.setBasicBlastRequired(this.isBasicBlastProcessRequired);
    }

    /**
     * Enable the isoforms
     * @param enableIsoformId : the boolean value
     */
    @Override
    public void enableIsoforms(boolean enableIsoformId){
        super.enableIsoforms(enableIsoformId);
        ((StrategyWithSequence) this.listOfActions.get(0)).enableIsoforms(enableIsoformId);
        ((StrategyWithIdentifier) this.listOfActions.get(1)).enableIsoforms(enableIsoformId);
    }

    /**
     * Check that we don't have any conflict between the uniprot accession returned by the strategy using the identifier
     * and the uniprot accession returned by the strategy using the sequence
     * @param result : the result
     * @param context : the current context
     * @param updateReport : the curent update report
     * @return true if the uniprot Id returned by the StrategyWithIdentifier and the uniprot Id returned by the StrategyWithSequence are the same
     * @throws ActionProcessingException
     * @throws StrategyException
     */
    private boolean checkIdentifierResults(IdentificationResults result, UpdateContext context, ActionReport updateReport) throws ActionProcessingException, StrategyException {
        // The strategy using the sequence found a unique Uniprot accession
        if (result.getFinalUniprotId() != null){
            // Get the uniprot accession using the strategy with identifier
            String otherResultFromIdentifier = this.listOfActions.get(1).runAction(context);
            // process the isoforms
            otherResultFromIdentifier = processIsoforms(otherResultFromIdentifier);
            // add the reports to the list of reports of the result
            result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

            // the uniprot accession returned by the strategy with the sequence
            String uniprot1 = result.getFinalUniprotId();

            // the uniprot accession returned by the strategy using the identifier is successful
            if (otherResultFromIdentifier != null){
                // The result is not matching the previous one, there is a conflict
                if (!uniprot1.equals(otherResultFromIdentifier)){
                    updateReport.addPossibleAccession(otherResultFromIdentifier);
                }
                return uniprot1.equals(otherResultFromIdentifier);
            }
            // the uniprot accession returned by the strategy using the identifier is null, we have a conflict 
            else {
                updateReport.addWarning("We found a unique uniprot AC when we tried to identify the protein using the sequence but we didn't find any uniprot AC" +
                        " when we tried to identify this protein using its identifiers with qualifier set to 'identity'.");
                return false;
            }
        }
        // The strategy using the sequence couldn't find a unique Uniprot accession
        else {
            // Get the uniprot accession using the strategy with identifier
            String otherResultFromIdentifier = this.listOfActions.get(1).runAction(context);
            // process the isoforms
            otherResultFromIdentifier = processIsoforms(otherResultFromIdentifier);
            // add the reports to the list of reports of the result
            result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

            // the uniprot accession returned by the strategy using the identifier is not null, we can have a conflict, need to check if the result null from
            // the previous strategy is not because of the featureRangeCheckingProcess
            if (otherResultFromIdentifier != null){
                updateReport.addWarning("We didn't find a unique uniprot AC when we tried to identify the protein using the sequence but we found a uniprot AC" +
                        " when we tried to identify this protein using its identifiers with qualifier set to 'identity'.");

                // In case of blast, we can check if the previous strategy returned null because of the featureRangeCheckingProcess
                if (result.getLastAction() instanceof BlastReport){
                    Set<BlastResults> blastProteins = ((BlastReport) result.getLastAction()).getBlastMatchingProteins();

                    if (!blastProteins.isEmpty()){
                        for (BlastProtein p : blastProteins){
                            // one result is matching the result of the second strategy, we will process the featureRangeChecking
                            if (p.getAccession().equals(otherResultFromIdentifier)){
                                // set the uniprot id of the result
                                result.setFinalUniprotId(otherResultFromIdentifier);
                                // run the feture range checking process
                                runThirdAction((UpdateContext) context, result);

                                // the uniprot accession of the result is now null, we didn't have any conflict with the previous strategy
                                if (result.getFinalUniprotId() == null){
                                    return true;
                                }
                                // the uniprot accession of the result is still not null, we definitely have a conflict with the previous strategy
                                else {
                                    result.setFinalUniprotId(null);
                                }
                            }
                        }
                    }
                }
                // add the result of the last strategy in the report
                updateReport.addPossibleAccession(otherResultFromIdentifier);
            }
            // both uniprot accessions are null, we don't have any conflict
            else {
                updateReport.addWarning("We didn't find a unique uniprot AC neither when we tried to identify the protein using the sequence nor" +
                        " when we tried to identify this protein using its identifiers with qualifier set to 'identity'.");
                return true;
            }
        }
        return false;
    }

    /**
     * Set the intact context for featureRangeChecking process
     * @param context : the intact context
     */
    public void setIntactContextForFeatureRangeChecking(IntactContext context){
        FeatureRangeCheckingProcess process = (FeatureRangeCheckingProcess) this.listOfActions.get(2);
        process.setIntactContext(context);
    }

    /**
     *
     * @param listOfReports : the list of SWissprotRemappingReports
     * @return the last SwissprotRemappingReport with a status COMPLETED, null otherwise
     */
    private BlastReport getTheSuccessfulSwissprotRemappingReport(List<BlastReport> listOfReports){
        BlastReport finalSR = null;

        for (BlastReport sr : listOfReports){
            if (sr.getStatus() != null){
                if (sr.getStatus().getLabel() != null){
                    if (sr.getStatus().getLabel().equals(StatusLabel.COMPLETED)){
                        finalSR = sr;
                    }
                }
            }
        }
        return finalSR;
    }

    /**
     * Process the feature range checking process. If we have any conflict between the sequence of the Swissprot entry
     * and the feature ranges of the protein in Intact, we keep the trembl entry
     * @param context : the identification context
     * @param results : the result
     * @throws StrategyException
     * @throws ActionProcessingException
     */
    private void runThirdAction(UpdateContext context, IdentificationResults results) throws StrategyException, ActionProcessingException {
        // get the feature range checking process
        FeatureRangeCheckingProcess process = (FeatureRangeCheckingProcess) this.listOfActions.get(2);
        ActionReport lastReport = results.getLastAction();

        // We have an intact context so we can process the feature range checking process
        if (process.getIntactContext() != null){
            // the intact accession of the protein to update is null, we can't check the feature ranges
            if (context.getIntactAccession() == null){
                lastReport.addWarning("We can't check the feature ranges of the protein as the Intact accession is null in the context.");
                results.setFinalUniprotId(null);
            }
            else {
                // Get the list of SwissprotRemapping actions from the result
                List<BlastReport> listOfSwissprotRemappingProcess = getSwissprotRemappingReports(results.getListOfActions());
                // extract the successful Swissprot remapping report
                BlastReport sr = getTheSuccessfulSwissprotRemappingReport(listOfSwissprotRemappingProcess);

                // If we processed a Swissprot remapping and could successfully replace the trembl entry with the swissprot entry, we need to check the
                // possible conflicts with existing feature ranges
                if (sr != null && results.getFinalUniprotId() != null){

                    context.setSequence(sr.getQuerySequence());

                    // Create a specific context from the previous one
                    FeatureRangeCheckingContext featureCheckingContext = new FeatureRangeCheckingContext(context);

                    // add the Trembl accession and the results of the swissprot remapping process in the new context
                    featureCheckingContext.setResultsOfSwissprotRemapping(sr.getBlastMatchingProteins());

                    // run the featureRangeChecking process
                    String accession = process.runAction(featureCheckingContext);
                    // add the report to the result
                    results.getListOfActions().addAll(process.getListOfActionReports());
                    // process the isoforms and set the uniprot accession of the result with the one returned by the feature
                    // range checking process
                    processIsoforms(accession, results);
                }
            }
        }
        // We don't have any intact context, we can't process the feature range checking process
        else {
            lastReport.addWarning("As the IntactContext is null, we can't check if there are some conflicts between the sequence of the Swissprot entry and the range of some features attached to this protein.");
            results.setFinalUniprotId(null);
        }
    }

    /**
     * - If the sequence and the identifier(s) are null, the update can't be done and we and an update report with a status FAILED
     * to the results
     *
     * - If the sequence is not null, we use the StrategyWithSequence.
     *  -> If the last action was a swissprot remapping process, we will check that the feature ranges of the protein in intact are not in conflict with
     * the results of the last process
     *  -> If the list of identifiers is not null, we use the StrategyWithIdentifier and check that the results are consistent with the previous one
     * - If the sequence is null but the identifier(s) is(are) not null, we will use the StrategyWithIdentifier
     *  -> If the last action was a swissprot remapping process, we will check that the feature ranges of the protein in intact are not in conflict with
     * the results of the last process
     * @param context : the context of the protein to identify
     * @return the results
     * @throws StrategyException
     */
    @Override
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {

        // The context for this object must be an UpdateContext
        if (! (context instanceof UpdateContext)){
            throw new StrategyException("The context of a StrategyForProteinUpdate should be an instance of UpdateContext and not " + context.getClass().getSimpleName());
        }
        UpdateContext updateContext = (UpdateContext) context;

        String sequence = context.getSequence();
        Map<String, String> identifiers = ((UpdateContext) context).getIdentifiers();

        // create a new result instance
        UpdateResults result = new UpdateResults();
        // set the intact accession of the result
        result.setIntactAccession(((UpdateContext) context).getIntactAccession());

        try {
            // we don't have neither a sequence nor an identifier for this protein
            if (context.getSequence() == null && context.getIdentifier() == null){
                // create a new report which will be added to the results
                ActionReport report = new ActionReport(ActionName.update_Checking);
                Status status = new Status(StatusLabel.FAILED, "The sequence of the protein is null and there are no cross references with qualifier 'identity'.");
                report.setStatus(status);
                result.addActionReport(report);
            }
            // the protein has a sequence
            else if (sequence != null) {

                String uniprot = null;

                // We run the strategy with sequence
                uniprot = this.listOfActions.get(0).runAction(context);
                // add the reports to the result
                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                // process the isoforms and set the uniprot id of the result
                processIsoforms(uniprot, result);

                // Run the feature range checking process if necessary
                runThirdAction((UpdateContext) context, result);

                // The protein also has identifiers
                if (!identifiers.isEmpty()){
                    // we create a new update report which will be added to the results
                    ActionReport report = new ActionReport(ActionName.update_Checking);
                    report.addPossibleAccession(result.getFinalUniprotId());

                    // boolean value to know if there is a conflict with the previous results
                    boolean isMatchingIdentifierResults = false;

                    for (Map.Entry<String, String> entry : identifiers.entrySet()){
                        // set the identifier
                        updateContext.setIdentifier(entry.getValue());
                        // set the database
                        updateContext.setDatabaseForIdentifier(entry.getKey());
                        // check the possible conflicts with the previous results
                        isMatchingIdentifierResults = checkIdentifierResults(result, updateContext, report);
                    }

                    // We don't have any conflicts with the previous results
                    if (isMatchingIdentifierResults){
                        Status status = new Status(StatusLabel.COMPLETED, "There is no result conflicts when we tried to identify the protein using the sequence then using the identifiers " + identifiers);
                        report.setStatus(status);
                        result.addActionReport(report);
                    }
                    // We have a conflict with the previous results, we set the uniprot id of the result to null and ask a curator to review this entry
                    else {

                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "There is a conflict in the results when we tried to identify the protein using the sequence then using the identifiers " + identifiers);
                        report.setStatus(status);
                        if (result.getFinalUniprotId() != null){
                            report.addPossibleAccession(result.getFinalUniprotId());
                        }
                        result.addActionReport(report);
                        result.setFinalUniprotId(null);
                    }
                }
            }
            // we don't have a sequence but the protein has identifier(s)
            else{
                // we create a new update report which will be added to the results
                ActionReport report = new ActionReport(ActionName.update_Checking);
                report.addPossibleAccession(result.getFinalUniprotId());

                // boolean value to know if there is a conflict with the previous results
                boolean isMatchingIdentifierResults = true;

                Set<String> uniprots = new HashSet<String>();

                for (Map.Entry<String, String> entry : identifiers.entrySet()){
                    // set the identifier
                    updateContext.setIdentifier(entry.getValue());
                    // set the database
                    updateContext.setDatabaseForIdentifier(entry.getKey());

                    // we run the strategy with identifier
                    String newUniprot = this.listOfActions.get(1).runAction(updateContext);
                    // we add teh reports to the result
                    result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());
                    // we process the isoforms
                    newUniprot = processIsoforms(newUniprot);

                    if (newUniprot == null){
                        if (!uniprots.isEmpty()){
                            isMatchingIdentifierResults = false;
                        }
                    }
                    else {
                        uniprots.add(newUniprot);
                    }
                }

                // We don't have any conflicts with the previous results
                if (uniprots.isEmpty() || (uniprots.size() == 1 && isMatchingIdentifierResults)){
                    Status status = new Status(StatusLabel.COMPLETED, "There is no conflicts in the results when we tried to identify the protein using the identifiers " + identifiers);
                    report.setStatus(status);
                    result.addActionReport(report);
                    result.setFinalUniprotId(uniprots.iterator().next());
                }
                // We have a conflict with the previous results, we set the uniprot id of the result to null and ask a curator to review this entry
                else {
                    Status status = new Status(StatusLabel.TO_BE_REVIEWED, "There is a conflict in the results when we tried to identify using the identifiers one by one " + identifiers);
                    report.setStatus(status);
                    for (String uniprot : uniprots){
                        report.addPossibleAccession(uniprot);
                    }
                    result.addActionReport(report);
                }

                // we run the feature range checking process
                runThirdAction((UpdateContext) context, result);
            }
        } catch (ActionProcessingException e) {
            throw  new StrategyException("An error occured while trying to update the protein using the sequence " + context.getSequence(), e);
        }
        return result;
    }

    /**
     * Initialise the set of actions for this object
     */
    @Override
    protected void initialiseSetOfActions() {

        // the first action is a StrategyWithSequence
        StrategyWithSequence firstAction = new StrategyWithSequence();
        firstAction.enableIsoforms(this.isIsoformEnabled());
        this.listOfActions.add(firstAction);

        // the second action is a StrategyWithIdentifier
        StrategyWithIdentifier secondAction = new StrategyWithIdentifier();
        secondAction.enableIsoforms(this.isIsoformEnabled());
        this.listOfActions.add(secondAction);

        // The last action is a feature range checking process
        FeatureRangeCheckingProcess thirdAction = new FeatureRangeCheckingProcess();
        this.listOfActions.add(thirdAction);
    }
}
