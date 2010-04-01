package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.blast.BlastResultFilter;
import uk.ac.ebi.intact.bridges.blast.BlastServiceException;
import uk.ac.ebi.intact.bridges.blast.ProteinWsWuBlastService;
import uk.ac.ebi.intact.bridges.blast.model.BlastProtein;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.actions.exception.SwissprotRemappingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.IntactBlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class IntactBlastProcess implements IdentificationAction{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( IntactBlastProcess.class );
    private ProteinWsWuBlastService blastService;
    private BlastResultFilter blastFilter = new BlastResultFilter();
    private BlastContext context;
    private static final int maxNumberOfBlastProteins = 10;
    private List<ActionReport> listOfReports = new ArrayList<ActionReport>();

    public IntactBlastProcess(){
        try {
            this.blastService = new ProteinWsWuBlastService("marine@ebi.ac.uk");
            this.context = null;
        } catch (BlastServiceException e) {
            log.error("Problem instantiating the blast client.",e);
        }
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        if (!(context instanceof BlastContext)){
            log.error("The IntactBlastProcess needs a BlastContext instance and the current context is a " + context.getClass().getSimpleName());
        }
        else{
            this.context = (BlastContext) context;

            IntactBlastReport report = new IntactBlastReport(ActionName.BLAST_intact);
            this.listOfReports.add(report);

            report.setQuerySequence(this.context.getSequence());

            InputStream intactBlast = this.blastService.getResultsOfBlastOnIntact(this.context.getSequence());
            this.blastFilter.setResults(intactBlast);


            if (this.context.getDeducedScientificOrganismName() != null){
                this.blastFilter.filterResultsWithIdentityAndOrganism((float) 100, this.context.getDeducedScientificOrganismName());
            }
            else{
                this.blastFilter.filterResultsWithIdentity((float) 100);
            }

            ArrayList<BlastProtein> blastProteins = this.blastFilter.getMatchingEntries();

            if (blastProteins.isEmpty()){
                Status status = new Status(StatusLabel.FAILED, "No proteins in IntAct could match with 100% identity the entire sequence " + this.context.getSequence());
                report.setStatus(status);

                BlastReport report2 = new BlastReport(ActionName.BLAST_uniprot);
                this.listOfReports.add(report2);

                InputStream uniprotBlast = this.blastService.getResultsOfBlastOnUniprot(this.context.getSequence());
                this.blastFilter.setResults(uniprotBlast);

                if (this.context.getDeducedScientificOrganismName() != null){
                    this.blastFilter.filterResultsWithOrganism(this.context.getDeducedScientificOrganismName());
                }
                else{
                    this.blastFilter.readResultsWithoutFiltering();
                }

                blastProteins = this.blastFilter.getMatchingEntries();

                for (BlastProtein b : blastProteins){

                    if (blastProteins.indexOf(b) > maxNumberOfBlastProteins){
                        break;
                    }
                    else {
                        report2.addBlastMatchingProtein(b);
                    }
                }

                Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "A blast has been done on Uniprot and we need to decide which result we want to keep.");
                report.setStatus(status2);

            }
            else {
                processBlastOnIntact(blastProteins, report);
            }
        }
        return null;
    }

    public List<ActionReport> getListOfActionReports() {
        return this.listOfReports;
    }

    private void processBlastOnIntact(ArrayList<BlastProtein> blastProteins, IntactBlastReport report) throws SwissprotRemappingException {
        if (blastProteins.size() == 1){
            if (blastProteins.get(0) != null){
                report.addBlastMatchingProtein(blastProteins.get(0));

                Status status = new Status(StatusLabel.COMPLETED, "The blast on Intact successfully returned the IntAct entry " + report.getIntactid() + " with 100% identity on the entire sequence.");
                report.setStatus(status);
                report.setIntactid(blastProteins.get(0).getAccession());
            }
        }
        else if (blastProteins.size() > 1) {
            for (BlastProtein b : blastProteins){

                if (blastProteins.indexOf(b) > maxNumberOfBlastProteins){
                    break;
                }
                else {
                    report.addBlastMatchingProtein(b);
                }
            }
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The blast on Uniprot returned " + blastProteins.size() + " matching IntAct entries with 100% identity on the entire sequence.");
            report.setStatus(status);

        }
    }
}
