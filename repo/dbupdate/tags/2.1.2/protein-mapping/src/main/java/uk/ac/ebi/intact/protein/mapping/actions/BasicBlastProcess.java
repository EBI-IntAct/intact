package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.update.model.protein.mapping.actions.ActionName;
import uk.ac.ebi.intact.update.model.protein.mapping.actions.BlastReport;
import uk.ac.ebi.intact.update.model.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.update.model.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.update.model.protein.mapping.results.BlastResults;

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
     * Create the process
     */
    public BasicBlastProcess(){
        super();
    }

    /**
     * Run a Blast on uniprot and keep less than 'maxNumberOfBlastProteins' BlastProtein instances in memory with an identity percent superior or equal to 'minimumIdentityThreshold'
     * Generate several Blast reports where the Blast results are stored in.
     * @param context : the context of the protein
     * @return Always null as this action is not aimed at analyzing the BLAST results to identify the protein but is aimed at storing the BLAST results in an ActionReport added to its list of ActionReports
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {

        // always clear the list of reports from previous actions
        this.listOfReports.clear();

        // Create a BlastReport
        BlastReport report = new BlastReport(ActionName.BLAST_uniprot);
        this.listOfReports.add(report);

        // Run the blast on Uniprot and save the results in the BLAST filter
        InputStream uniprotBlast = this.blastService.getResultsOfBlastOnUniprot(context.getSequence());
        this.blastFilter.setResults(uniprotBlast);

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

                if (blastProteins.indexOf(b) > maxNumberOfBlastProteins){
                    break;
                }
                else {
                    report.addBlastMatchingProtein(new BlastResults(b));
                }
            }

            Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "A blast has been done on Uniprot and we found " + blastProteins.size() + " possible proteins with an identity superior or equal to " + minimumIdentityThreshold + "%.");
            report.setStatus(status2);
        }

        return null;
    }
}
