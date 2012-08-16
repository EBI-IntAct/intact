package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.BlastContext;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultBlastResults;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This class is doing a Blast on Uniprot to collect matching proteins with a minimum identity percent. It can filter the results on the identity
 * and also the organism if an organism is given.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class BasicBlastProcess extends ActionNeedingBlastService{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( BasicBlastProcess.class );

    /**
     * The minimum sequence coverage for match sequence : below this identity percent, we don't look at the BLAST results
     */
    private float minimumMatchSequenceCoverage = (float) 95;

    /**
     * The minimum sequence coverage for match sequence : below this identity percent, we don't look at the BLAST results
     */
    private float minimumQuerySequenceCoverage = (float) 95;

    /**
     * Create the process
     */
    public BasicBlastProcess(ReportsFactory factory){
        super(factory);
    }

    /**
     * Run a Blast on uniprot and keep less than 'maxNumberOfBlastProteins' BlastProtein instances in memory with an identity percent superior or equal to 'minimumIdentityThreshold'
     * Generate several Blast reports where the Blast results are stored in.
     * @param context : the context of the protein
     * @return Always null as this action is not aimed at analyzing the BLAST results to identify the protein but is aimed at storing the BLAST results in an ActionReport added to its list of ActionReports
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        BlastContext blastContext = (BlastContext) context;

        // always clear the list of reports from previous actions
        this.listOfReports.clear();

        // Create a DefaultBlastReport
        BlastReport report = getReportsFactory().getBlastReport(ActionName.BLAST_uniprot);
        this.listOfReports.add(report);

        // Run the blast on Uniprot and save the results in the BLAST filter
        InputStream uniprotBlast = this.blastService.getResultsOfBlastOnUniprot(context.getSequence());

        try{
            this.blastFilter.setResults(uniprotBlast);
        }
        finally {
            try {
                uniprotBlast.close();
            } catch (IOException e) {
                log.error("Impossible to close BLAST results", e);
            }
        }

        if (context.getOrganism() != null){
            // Filter the results on the organism and the minimum identity threshold
            this.blastFilter.filterResultsWithIdentityAndOrganism(minimumIdentityThreshold, context.getOrganism().getTaxId());
        }
        else{
            // Filter only on the minimum identity threshold
            report.addWarning("No organism has been given for the sequence " + context.getSequence() + ". We will process the blast on uniprot without filtering with the organism.");
            this.blastFilter.filterResultsWithIdentity(minimumIdentityThreshold);
        }

        // Get the results of the Blast filter after we have filtered the results
        List<BlastProtein> blastProteins = this.blastFilter.getMatchingEntries();

        if (blastProteins.isEmpty()){
            Status status2 = new Status(StatusLabel.FAILED, "A blast has been done on Uniprot and we didn't find any hits with more than "+minimumIdentityThreshold+"% identity.");
            report.setStatus(status2);
        }
        else {
            // Add the results of the blast but not more than the maximum number of BlastProtein we want to keep in memory
            for (BlastProtein b : blastProteins){

                float queryCoverage = getQuerySequenceCoveragePercentFor(b, blastContext);
                float matchCoverage = getMatchSequenceCoveragePercentFor(b);

                if (report.getBlastMatchingProteins().size() > maxNumberOfBlastProteins){
                    break;
                }
                else if (queryCoverage >= minimumQuerySequenceCoverage && matchCoverage >= minimumMatchSequenceCoverage) {
                    report.addBlastMatchingProtein(new DefaultBlastResults(b));
                }
            }

            Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "A blast has been done on Uniprot and we found " + blastProteins.size() + " possible proteins with an identity superior or equal to " + minimumIdentityThreshold + "%.");
            report.setStatus(status2);
        }

        return null;
    }

    public float getMinimumMatchSequenceCoverage() {
        return minimumMatchSequenceCoverage;
    }

    public void setMinimumMatchSequenceCoverage(float minimumMatchSequenceCoverage) {
        this.minimumMatchSequenceCoverage = minimumMatchSequenceCoverage;
    }

    public float getMinimumQuerySequenceCoverage() {
        return minimumQuerySequenceCoverage;
    }

    public void setMinimumQuerySequenceCoverage(float minimumQuerySequenceCoverage) {
        this.minimumQuerySequenceCoverage = minimumQuerySequenceCoverage;
    }
}
