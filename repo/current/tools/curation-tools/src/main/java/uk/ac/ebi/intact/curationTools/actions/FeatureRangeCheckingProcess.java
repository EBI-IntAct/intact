package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.TransactionStatus;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.FeatureRangeCheckingContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.model.Component;
import uk.ac.ebi.intact.model.Feature;
import uk.ac.ebi.intact.model.Range;

import java.util.Collection;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05-May-2010</pre>
 */

public class FeatureRangeCheckingProcess extends IdentificationActionImpl{

    private IntactContext intactContext;

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( FeatureRangeCheckingProcess.class);

    public FeatureRangeCheckingProcess(){
        super();
        intactContext = null;
    }

    public FeatureRangeCheckingProcess(IntactContext context){
        super();
        intactContext = context;
    }

    public IntactContext getIntactContext() {
        return intactContext;
    }
    public void setIntactContext(IntactContext intactContext) {
        this.intactContext = intactContext;
    }

    private boolean checkIfBlastProteinIsTremblEntry(BlastProtein protein, String tremblAccession){
        if (protein.getAccession() != null && protein.getAccession().equals(tremblAccession)){
            return true;
        }
        return false;
    }

    private boolean checkRangeValidWithNewSequence(Range range, BlastProtein protein){
        int diffStart = protein.getStartQuery() - protein.getStartMatch();
        int diffEnd = protein.getEndQuery() - protein.getEndMatch();

        int startFrom = range.getFromIntervalStart() - diffStart;
        int startTo = range.getToIntervalStart() - diffStart;
        int endFrom = range.getFromIntervalEnd() - diffEnd;
        int endTo = range.getToIntervalEnd() - diffEnd;

        if (startFrom < protein.getStartMatch() || range.getFromIntervalStart() < protein.getStartQuery()){
            return false;
        }
        else if (startTo < protein.getStartMatch() || range.getToIntervalStart() < protein.getStartQuery()){
            return false;
        }
        else if (endFrom > protein.getEndMatch() || range.getFromIntervalEnd() > protein.getEndQuery()){
            return false;
        }
        else if (endTo > protein.getEndMatch() || range.getToIntervalEnd() > protein.getEndQuery()){
            return false;
        }
        else {

            if (startFrom > 0 && endFrom > 0){
                String rangeNewSequence = protein.getSequence().substring(startFrom - 1, endFrom);
                if (!range.getSequence().equals(rangeNewSequence)){
                    return false;
                }
            }
            if (startTo > 0 && endTo > 0){
                String rangeNewSequence = protein.getSequence().substring(startTo - 1, endTo);
                if (!range.getSequence().equals(rangeNewSequence)){
                    return false;
                }
            }
        }
        return true;
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        if (this.intactContext == null){
            throw new ActionProcessingException("We can't check if a feature is affected by the sequence changes if an IntactContext instance is not provided.");
        }

        if (! (context instanceof FeatureRangeCheckingContext)){
            throw new ActionProcessingException("We can't process a feature range checking if the context is a " + context.getClass().getSimpleName() + " and not a FeatureRangeCheckingContext instance.");
        }
        else {
            final DataContext dataContext = this.intactContext.getDataContext();
            TransactionStatus transactionStatus = dataContext.beginTransaction();
            FeatureRangeCheckingContext processContext = (FeatureRangeCheckingContext) context;
            int initialNumberOfBlastProtein = processContext.getResultsOfSwissprotRemapping().size();

            BlastReport report = new BlastReport(ActionName.feature_range_checking);
            this.listOfReports.add(report);

            if (processContext.getResultsOfSwissprotRemapping().isEmpty()){
                Status status = new Status(StatusLabel.FAILED, "We don't have any valid results from the Swissprot-remapping process, so we will keep the Trembl entry " + processContext.getTremblAccession());
                report.setStatus(status);
                return processContext.getTremblAccession();
            }

            DaoFactory factory = this.intactContext.getDaoFactory();
            List<Component> components = factory.getComponentDao().getByInteractorAc(processContext.getIntactAccession());

            if (components.isEmpty()){
                report.getBlastMatchingProteins().addAll(processContext.getResultsOfSwissprotRemapping());
            }
            else {
                boolean hasAtLeastOneFeature = false;
                boolean hasRangeConflict = false;
                for (Component component : components){
                    Collection<Feature> features = component.getBindingDomains();

                    if (!features.isEmpty()){
                        hasAtLeastOneFeature = true;

                        for (Feature feature : features){
                            Collection<Range> ranges = feature.getRanges();

                            for (Range range : ranges){
                                if (range.getToCvFuzzyType() != null && !range.getToCvFuzzyType().isCTerminal() && !range.getToCvFuzzyType().isNTerminal()
                                        && !range.getToCvFuzzyType().isUndetermined()) {
                                    for (BlastProtein protein : processContext.getResultsOfSwissprotRemapping()){
                                        if (!checkIfBlastProteinIsTremblEntry(protein, processContext.getTremblAccession())){
                                            if (!checkRangeValidWithNewSequence(range, protein)){
                                                hasRangeConflict = true;
                                                report.addWarning("One of the ranges of the feature "+feature.getAc()+" is in conflict with the sequence of the Swissprot entry "+protein.getAccession()+". We will remove this protein from the matching Swissprot entries.");
                                            }
                                            else{
                                                report.addBlastMatchingProtein(protein);
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }

                if (!hasAtLeastOneFeature){
                    report.getBlastMatchingProteins().addAll(processContext.getResultsOfSwissprotRemapping());
                }
                else {
                    if (!hasRangeConflict){
                        report.getBlastMatchingProteins().addAll(processContext.getResultsOfSwissprotRemapping());
                    }
                }
            }

            if (report.getBlastMatchingProteins().isEmpty()){
                Status status = new Status(StatusLabel.FAILED, "The swissprot remapping is not possible as there are some conflicts between the sequence of the Swissprot entry and some feature ranges of the protein " + processContext.getIntactAccession() + ". We will keep the Trembl entry " + processContext.getTremblAccession());
                report.setStatus(status);

                return processContext.getTremblAccession();
            }
            else if (report.getBlastMatchingProteins().size() < initialNumberOfBlastProtein){
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, processContext.getResultsOfSwissprotRemapping().size() + " Swissprot entries on the initial " + initialNumberOfBlastProtein + " matching Swissprot proteins have a conflict between their sequence and some feature ranges of the protein " + processContext.getIntactAccession());
                report.setStatus(status);

                return processContext.getTremblAccession();
            }
            else {
                if (report.getBlastMatchingProteins().size() == 1){
                    BlastProtein swissprot = report.getBlastMatchingProteins().iterator().next();
                    Status status = new Status(StatusLabel.COMPLETED, "We don't have any conflicts between the sequence of the Swissprot entry " + swissprot.getAccession() + " and the feature ranges of the protein " + processContext.getIntactAccession());
                    report.setStatus(status);
                    return swissprot.getAccession();
                }
                else {
                    Status status = new Status(StatusLabel.COMPLETED, "We don't have any conflicts between the sequence(s) of the " + report.getBlastMatchingProteins().size() + " possible Swissprot proteins and the feature ranges of the protein " + processContext.getIntactAccession());
                    report.setStatus(status);
                }
            }

            dataContext.commitTransaction(transactionStatus);
        }

        return null;
    }
}
