package uk.ac.ebi.intact.protein.mapping.strategies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.protein.mapping.actions.FeatureRangeCheckingProcess;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.MappingReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.FeatureRangeCheckingContext;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.model.contexts.UpdateContext;
import uk.ac.ebi.intact.protein.mapping.results.IdentificationResults;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.service.UniprotService;

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
     * boolean value to know if the update is enabled or not
     */
    private boolean updateEnabled;

    /**
     * Create a Strategy for protein update
     */
    public StrategyForProteinUpdate(){
        super();
        updateEnabled = true;
    }

    public StrategyForProteinUpdate(UniprotService uniprotService) {
        super(uniprotService);
        updateEnabled = true;
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
     * @param updateReport : the curent update report
     * @return true if the uniprot Id returned by the StrategyWithIdentifier and the uniprot Id returned by the StrategyWithSequence are the same
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     * @throws uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException
     */
    private boolean checkIdentifierResults(IdentificationResults result, MappingReport updateReport, String otherIdentifier) throws ActionProcessingException, StrategyException {
        // The strategy using the sequence found a unique Uniprot accession
        if (result.getFinalUniprotId() != null){

            // the uniprot accession returned by the strategy with the sequence
            String uniprot1 = result.getFinalUniprotId();

            // the uniprot accession returned by the strategy using the identifier is successful
            if (otherIdentifier != null){
                // The result is not matching the previous one, there is a conflict
                if (!uniprot1.equals(otherIdentifier)){
                    updateReport.addPossibleAccession(otherIdentifier);
                }
                return uniprot1.equals(otherIdentifier);
            }
            // the uniprot accession returned by the strategy using the identifier is null, we have a possible conflict but we still process the results later
            else {
                updateReport.addWarning("We found a unique uniprot AC when we tried to identify the protein using the sequence but we didn't find any uniprot AC" +
                        " when we tried to identify this protein using its identifiers with qualifier set to 'identity'.");
                return true;
            }
        }
        // The strategy using the sequence couldn't find a unique Uniprot accession
        else {

            // the uniprot accession returned by the strategy using the identifier is not null, we can have a conflict, need to check if the result null from
            // the previous strategy is not because of the featureRangeCheckingProcess
            if (otherIdentifier != null){
                updateReport.addWarning("We didn't find a unique uniprot AC when we tried to identify the protein using the sequence but we found a uniprot AC" +
                        " when we tried to identify this protein using its identifiers with qualifier set to 'identity'.");

                // add the result of the last strategy in the report
                updateReport.addPossibleAccession(otherIdentifier);
            }
            // both uniprot accessions are null, we don't have any conflict
            else {
                updateReport.addWarning("We didn't find a unique uniprot AC neither when we tried to identify the protein using the sequence nor" +
                        " when we tried to identify this protein using its identifiers with qualifier set to 'identity'.");
            }
            return true;
        }
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
        MappingReport lastReport = results.getLastAction();

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
        IdentificationResults result = getResultsFactory().getIdentificationResults();
        // set the intact accession of the result  is not necessary because the protein update takes care of that
        // result.setIntactAccession(((UpdateContext) context).getIntactAccession());

        try {
            // we don't have neither a sequence nor an identifier for this protein
            if (updateContext.getSequence() == null && updateContext.getIdentifiers().isEmpty()){
                // create a new report which will be added to the results
                MappingReport report = getReportsFactory().getMappingReport(ActionName.update_checking);
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

                // The protein also has identifiers
                if (!identifiers.isEmpty()){
                    // we create a new update report which will be added to the results
                    MappingReport report = getReportsFactory().getMappingReport(ActionName.update_checking);
                    report.addPossibleAccession(result.getFinalUniprotId());

                    // boolean value to know if there is a conflict with the previous results
                    boolean isMatchingIdentifierResults = false;

                    String otherIdentifier = null;
                    for (Map.Entry<String, String> entry : identifiers.entrySet()){
                        // set the identifier
                        updateContext.setIdentifier(entry.getValue());
                        // set the database
                        updateContext.setDatabaseForIdentifier(entry.getKey());
                        // Get the uniprot accession using the strategy with identifier
                        String otherResultFromIdentifier = this.listOfActions.get(1).runAction(context);
                        // process the isoforms
                        otherResultFromIdentifier = processIsoforms(otherResultFromIdentifier);

                        if (otherResultFromIdentifier != null){
                            if (otherIdentifier != null){
                                 if (!otherIdentifier.equalsIgnoreCase(otherResultFromIdentifier)){
                                       isMatchingIdentifierResults = false;
                                     break;
                                 }
                            }
                             otherIdentifier = otherResultFromIdentifier;
                        }

                        // add the reports to the list of reports of the result
                        result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());
                        // check the possible conflicts with the previous results
                        isMatchingIdentifierResults = checkIdentifierResults(result, report, otherResultFromIdentifier);

                        if (!isMatchingIdentifierResults){
                            break;
                        }
                    }

                    // We have a conflict with the previous results, we set the uniprot id of the result to null and ask a curator to review this entry
                    if(!isMatchingIdentifierResults) {

                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "There is a conflict in the results when we try to identify the protein using the sequence then using the identifiers " + identifiers);
                        report.setStatus(status);
                        if (result.getFinalUniprotId() != null){
                            report.addPossibleAccession(result.getFinalUniprotId());
                        }
                        result.addActionReport(report);
                        result.setFinalUniprotId(null);
                    }
                    else if (isMatchingIdentifierResults && ((otherIdentifier == null && result.getFinalUniprotId() != null) || (otherIdentifier != null && result.getFinalUniprotId() == null))){
                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "There is a conflict in the results when we try to identify the protein using the sequence then using the identifiers " + identifiers);
                        report.setStatus(status);
                        if (result.getFinalUniprotId() != null){
                            report.addPossibleAccession(result.getFinalUniprotId());
                        }
                        result.addActionReport(report);
                        result.setFinalUniprotId(null);
                    }
                    // We don't have any conflicts with the previous results
                    else if (isMatchingIdentifierResults && otherIdentifier == null && result.getFinalUniprotId() == null){
                        Status status = new Status(StatusLabel.COMPLETED, "There is no result conflicts when we try to identify the protein using the sequence then using the identifiers " + identifiers);
                        report.setStatus(status);
                        result.addActionReport(report);
                    }
                    else if (isMatchingIdentifierResults && otherIdentifier.equals(result.getFinalUniprotId())){
                        Status status = new Status(StatusLabel.COMPLETED, "There is no result conflicts when we try to identify the protein using the sequence then using the identifiers " + identifiers);
                        report.setStatus(status);
                        result.addActionReport(report);
                    }

                }

                // Run the feature range checking process if necessary
                runThirdAction((UpdateContext) context, result);
            }
            // we don't have a sequence but the protein has identifier(s)
            else{
                // we create a new update report which will be added to the results
                MappingReport report = getReportsFactory().getMappingReport(ActionName.update_checking);
                report.addPossibleAccession(result.getFinalUniprotId());

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

                    if (newUniprot != null){
                        uniprots.add(newUniprot);
                    }
                }

                // We don't have any conflicts with the previous results  : if one identifier returns nothing but another allows to identify, we
                // keep the one which could identify. However, if several different uniprot acs are found, we have a conflict
                if (uniprots.size() == 1){
                    Status status = new Status(StatusLabel.COMPLETED, "There is no conflicts in the results when we tried to identify the protein using the identifiers " + identifiers);
                    report.setStatus(status);
                    result.addActionReport(report);
                    result.setFinalUniprotId(uniprots.iterator().next());
                }
                else if (uniprots.isEmpty()){
                    Status status = new Status(StatusLabel.COMPLETED, "There is no conflicts in the results when we tried to identify the protein using the identifiers " + identifiers);
                    report.setStatus(status);
                    result.addActionReport(report);
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

            if (!updateEnabled){

                if (result.getFinalUniprotId() != null){
                    // we create a new update report which will be added to the results
                    MappingReport report = getReportsFactory().getMappingReport(ActionName.update_checking);

                    report.addPossibleAccession(result.getFinalUniprotId());
                    Status updateStatus = new Status(StatusLabel.PENDING, "The protein " + updateContext.getIntactAccession() + " could successfully be mapped to " + result.getFinalUniprotId() + " but was not updated because uniprot cross references already exist and a curator should check first that the protein can be updated.");
                    result.setFinalUniprotId(null);
                    report.setStatus(updateStatus);
                    result.addActionReport(report);
                }
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
        StrategyWithSequence firstAction = new StrategyWithSequence(uniprotService);
        firstAction.setReportsFactory(getReportsFactory());
        firstAction.setResultsFactory(getResultsFactory());
        firstAction.enableIsoforms(this.isIsoformEnabled());
        this.listOfActions.add(firstAction);

        // the second action is a StrategyWithIdentifier
        StrategyWithIdentifier secondAction = new StrategyWithIdentifier(uniprotService);
        secondAction.setReportsFactory(getReportsFactory());
        secondAction.setResultsFactory(getResultsFactory());
        secondAction.enableIsoforms(this.isIsoformEnabled());
        this.listOfActions.add(secondAction);

        // The last action is a feature range checking process
        FeatureRangeCheckingProcess thirdAction = new FeatureRangeCheckingProcess(getReportsFactory());
        this.listOfActions.add(thirdAction);
    }

    /**
     *
     * @return true if the update is enabled
     */
    public boolean isUpdateEnabled() {
        return updateEnabled;
    }

    /**
     * set the updateEnabled boolean
     * @param updateEnabled
     */
    public void setUpdateEnabled(boolean updateEnabled) {
        this.updateEnabled = updateEnabled;
    }
}
