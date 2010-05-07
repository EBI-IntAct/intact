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
import uk.ac.ebi.intact.curationTools.model.results.IdentificationResults;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;

import java.util.Set;

/**
 * TODO comment this
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

    private boolean isBasicBlastProcessRequired = false;

    public StrategyForProteinUpdate(){
        super();
    }

    public boolean isBasicBlastProcessRequired() {
        return isBasicBlastProcessRequired;
    }

    public void setBasicBlastProcessRequired(boolean basicBlastProcessRequired) {
        isBasicBlastProcessRequired = basicBlastProcessRequired;
        StrategyWithSequence firstAction = (StrategyWithSequence) this.listOfActions.get(0);
        firstAction.setBasicBlastRequired(this.isBasicBlastProcessRequired);
    }

    @Override
    public void enableIsoforms(boolean enableIsoformId){
        super.enableIsoforms(enableIsoformId);
        ((StrategyWithSequence) this.listOfActions.get(0)).enableIsoforms(enableIsoformId);
        ((StrategyWithIdentifier) this.listOfActions.get(1)).enableIsoforms(enableIsoformId);
    }

    private boolean checkIdentifierResults(IdentificationResults result, IdentificationContext context, ActionReport updateReport) throws ActionProcessingException, StrategyException {
        if (result.getUniprotId() != null){
            String otherResultFromIdentifier = this.listOfActions.get(1).runAction(context);
            otherResultFromIdentifier = processIsoforms(otherResultFromIdentifier);
            result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());
            
            String uniprot1 = result.getUniprotId();

            if (otherResultFromIdentifier != null){
                if (!uniprot1.equals(otherResultFromIdentifier)){
                    updateReport.addPossibleAccession(otherResultFromIdentifier);
                }
                return uniprot1.equals(otherResultFromIdentifier);
            }
            else {
                updateReport.addWarning("We found a unique uniprot AC when we tried to identify the protein using the sequence but we didn't find any uniprot AC" +
                        " when we tried to identify this protein using its identifiers with qualifier set to 'identity'.");
                return true;
            }
        }
        else {
            String otherResultFromIdentifier = this.listOfActions.get(1).runAction(context);
            otherResultFromIdentifier = processIsoforms(otherResultFromIdentifier);
            result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());

            if (otherResultFromIdentifier != null){
                updateReport.addWarning("We didn't find a unique uniprot AC when we tried to identify the protein using the sequence but we found a uniprot AC" +
                        " when we tried to identify this protein using its identifiers with qualifier set to 'identity'. We kept this uniprot AC.");
                
                if (result.getLastAction() instanceof BlastReport){
                    Set<BlastProtein> blastProteins = ((BlastReport) result.getLastAction()).getBlastMatchingProteins();

                    if (blastProteins.isEmpty()){
                        result.setUniprotId(otherResultFromIdentifier);
                        return true;
                    }
                    else {
                        for (BlastProtein p : blastProteins){
                            if (p.getAccession().equals(otherResultFromIdentifier)){
                                result.setUniprotId(otherResultFromIdentifier);
                                runThirdAction((UpdateContext) context, result);
                                return true;
                            }
                        }
                        updateReport.addPossibleAccession(otherResultFromIdentifier);
                    }
                }
                else {
                    Set<String> possibleProteins = result.getLastAction().getPossibleAccessions();

                    if (possibleProteins.isEmpty()){
                        result.setUniprotId(otherResultFromIdentifier);
                        return true;
                    }
                    else {
                        for (String p : possibleProteins){
                            if (p.equals(otherResultFromIdentifier)){
                                result.setUniprotId(otherResultFromIdentifier);
                                runThirdAction((UpdateContext) context, result);
                                return true;
                            }
                        }
                        updateReport.addPossibleAccession(otherResultFromIdentifier);
                    }
                }
            }
            else {
                return true;
            }
        }
        return false;
    }

    public void setIntactContextForFeatureRangeChecking(IntactContext context){
        FeatureRangeCheckingProcess process = (FeatureRangeCheckingProcess) this.listOfActions.get(2);
        process.setIntactContext(context);
    }

    private void runThirdAction(UpdateContext context, IdentificationResults results) throws StrategyException, ActionProcessingException {
        FeatureRangeCheckingProcess process = (FeatureRangeCheckingProcess) this.listOfActions.get(2);
        ActionReport lastReport = results.getLastAction();

        if (process.getIntactContext() != null){
            if (context.getIntactAccession() == null){
                lastReport.addWarning("We can't check the feature ranges of the protein as the Intact accession is null in the context.");
            }
            else {
                if (lastReport instanceof BlastReport && results.getUniprotId() != null){
                    BlastReport report = (BlastReport) lastReport;
                    context.setSequence(report.getQuerySequence());

                    FeatureRangeCheckingContext featureCheckingContext = new FeatureRangeCheckingContext(context);

                    if (report.getPossibleAccessions().isEmpty()){
                        throw new StrategyException("We can't check the feature ranges of the protein if the Trembl accession used for the swissprot remapping Blast is null in the context.");
                    }
                    else {
                        if (report.getName().equals(ActionName.BLAST_Swissprot_Remapping) && report.getStatus().getLabel().equals(StatusLabel.COMPLETED)){
                            featureCheckingContext.setTremblAccession(report.getPossibleAccessions().iterator().next());
                            featureCheckingContext.setResultsOfSwissprotRemapping(report.getBlastMatchingProteins());

                            String accession = process.runAction(featureCheckingContext);
                            results.getListOfActions().addAll(process.getListOfActionReports());

                            if (accession == null){
                                results.setUniprotId(null);
                            }
                            else {
                                processIsoforms(accession, results);
                            }
                        }

                    }
                }
            }
        }
        else {
            lastReport.addWarning("As the IntactContext is null, we can't check if there are some conflicts between the sequence of the Swissprot entry and the range of some features attached to this protein.");
        }
    }

    @Override
    public IdentificationResults identifyProtein(IdentificationContext context) throws StrategyException {

        if (! (context instanceof UpdateContext)){
            throw new StrategyException("The context of a StrategyForProteinUpdate should be an instance of UpdateContext and not " + context.getClass().getSimpleName());
        }
        String sequence = context.getSequence();
        Set<String> identifiers = ((UpdateContext) context).getIdentifiers();

        IdentificationResults result = new IdentificationResults();

        try {
            if (context.getSequence() == null && context.getIdentifier() == null){
                ActionReport report = new ActionReport(ActionName.update_Checking);
                Status status = new Status(StatusLabel.FAILED, "The sequence of the protein is null and there are no cross references with qualifier 'identity'.");
                report.setStatus(status);
                result.addActionReport(report);
            }
            else if (sequence != null) {

                String uniprot = null;

                uniprot = this.listOfActions.get(0).runAction(context);
                result.getListOfActions().addAll(this.listOfActions.get(0).getListOfActionReports());
                processIsoforms(uniprot, result);

                runThirdAction((UpdateContext) context, result);

                if (!identifiers.isEmpty()){
                    ActionReport report = new ActionReport(ActionName.update_Checking);
                    report.addPossibleAccession(result.getUniprotId());

                    boolean isMatchingIdentifierResults = false;

                    for (String identifier : identifiers){
                        IdentificationContext c = new IdentificationContext(context);
                        c.setIdentifier(identifier);
                        isMatchingIdentifierResults = checkIdentifierResults(result, context, report);
                    }

                    if (isMatchingIdentifierResults){
                        Status status = new Status(StatusLabel.COMPLETED, "There is no result conflicts when we tried to identify the protein using the sequence then using the identifiers " + identifiers);
                        report.setStatus(status);
                        result.addActionReport(report);
                    }
                    else {
                        
                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "There is a conflict in the results when we tried to identify the protein using the sequence then using the identifiers " + identifiers);
                        report.setStatus(status);
                        if (result.getUniprotId() != null){
                            report.addPossibleAccession(result.getUniprotId());
                        }
                        result.addActionReport(report);
                        result.setUniprotId(null);
                    }
                }
            }
            else{
                String uniprot = this.listOfActions.get(1).runAction(context);
                result.getListOfActions().addAll(this.listOfActions.get(1).getListOfActionReports());
                processIsoforms(uniprot, result);

                runThirdAction((UpdateContext) context, result);
            }
        } catch (ActionProcessingException e) {
            throw  new StrategyException("An error occured while trying to update the protein using the sequence " + context.getSequence(), e);
        }
        return result;
    }

    @Override
    protected void initialiseSetOfActions() {
        StrategyWithSequence firstAction = new StrategyWithSequence();
        firstAction.enableIsoforms(this.isIsoformEnabled());
        this.listOfActions.add(firstAction);

        StrategyWithIdentifier secondAction = new StrategyWithIdentifier();
        secondAction.enableIsoforms(this.isIsoformEnabled());
        this.listOfActions.add(secondAction);

        FeatureRangeCheckingProcess thirdAction = new FeatureRangeCheckingProcess();
        this.listOfActions.add(thirdAction);
    }
}
