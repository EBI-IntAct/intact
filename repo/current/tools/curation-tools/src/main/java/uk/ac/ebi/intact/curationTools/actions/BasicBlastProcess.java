package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.BlastResultFilter;
import uk.ac.ebi.intact.bridges.ncbiblast.BlastServiceException;
import uk.ac.ebi.intact.bridges.ncbiblast.ProteinNCBIBlastService;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * This class is doing a Blast on Uniprot to collect matching proteins with a minimum identity percent. It can filter the results on the identity
 * and also the organism if an organism is given.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class BasicBlastProcess extends IdentificationActionImpl{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( BasicBlastProcess.class );

    /**
     * The NCBI blast service
     */
    private ProteinNCBIBlastService blastService;
    /**
     * The BLAST filter
     */
    private BlastResultFilter blastFilter;
    /**
     * The maximum number of BlastProtein instances we allow to keep in memory
     */
    private static final int maxNumberOfBlastProteins = 10;
    /**
     * The minimum identity : below this identity percent, we don't look at the BLAST results
     */
    private static final float minimumIdentityThreshold = (float) 90;

    /**
     * Create the process
     */
    public BasicBlastProcess(){
        try {
            this.blastService = new ProteinNCBIBlastService("marine@ebi.ac.uk");
            this.blastFilter = new BlastResultFilter();

        } catch (BlastServiceException e) {
            log.error("Problem instantiating the blast client.",e);
        }
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        BlastReport report = new BlastReport(ActionName.BLAST_uniprot);
        this.listOfReports.add(report);

        InputStream uniprotBlast = this.blastService.getResultsOfBlastOnUniprot(context.getSequence());
        this.blastFilter.setResults(uniprotBlast);

        if (context.getOrganism() != null){
            this.blastFilter.filterResultsWithIdentityAndOrganism(minimumIdentityThreshold, context.getOrganism().getTaxId());
        }
        else{
            report.addWarning("No organism has been given for the sequence " + context.getSequence() + ". We will process the blast on uniprot without filtering with the organism.");
            this.blastFilter.filterResultsWithIdentity(minimumIdentityThreshold);
        }

        ArrayList<BlastProtein> blastProteins = this.blastFilter.getMatchingEntries();

        if (blastProteins.isEmpty()){
            Status status2 = new Status(StatusLabel.FAILED, "A blast has been done on Uniprot and we didn't find any hits with more than "+minimumIdentityThreshold+"% identity.");
            report.setStatus(status2);
        }
        else {
            
            for (BlastProtein b : blastProteins){

                if (blastProteins.indexOf(b) > maxNumberOfBlastProteins){
                    break;
                }
                else {
                    report.addBlastMatchingProtein(b);
                }
            }

            Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "A blast has been done on Uniprot and we found " + blastProteins.size() + " possible proteins with an identity superior or equal to " + minimumIdentityThreshold + "%.");
            report.setStatus(status2);
        }

        return null;
    }
}
