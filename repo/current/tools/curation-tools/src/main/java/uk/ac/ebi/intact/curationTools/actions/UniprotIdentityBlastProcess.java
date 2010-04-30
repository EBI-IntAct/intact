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
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Apr-2010</pre>
 */

public class UniprotIdentityBlastProcess extends IdentificationActionImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotIdentityBlastProcess.class );

    private ProteinNCBIBlastService blastService;
    private BlastResultFilter blastFilter;
    private static final int maxNumberOfBlastProteins = 10;
    private static final float identityThreshold = 90;

    public UniprotIdentityBlastProcess(){
        try {
            this.blastService = new ProteinNCBIBlastService("marine@ebi.ac.uk");
            this.blastFilter = new BlastResultFilter();

        } catch (BlastServiceException e) {
            log.error("Problem instantiating the blast client.",e);
        }
    }

    private ArrayList<BlastProtein> getEntriesWithDatabase(ArrayList<BlastProtein> proteins, String database){

        ArrayList<BlastProtein> entriesWithDatabase = new ArrayList<BlastProtein>();

        for (BlastProtein p : proteins){
            if (p.getDatabase() != null){
                if (p.getDatabase().equals(database)){
                    entriesWithDatabase.add(p);
                }
            }
        }

        return entriesWithDatabase;
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        BlastReport report = new BlastReport(ActionName.BLAST_Uniprot_Total_Identity);
        this.listOfReports.add(report);

        InputStream uniprotBlast = this.blastService.getResultsOfBlastOnUniprot(context.getSequence());
        this.blastFilter.setResults(uniprotBlast);

        if (context.getOrganism() != null){
            this.blastFilter.filterResultsWithIdentityAndOrganism(identityThreshold, context.getOrganism().getTaxId());
        }
        else{
            report.addWarning("No organism has been given for the sequence " + context.getSequence() + ". We will process the blast on uniprot without filtering with the organism.");
            this.blastFilter.filterMappingEntriesWithIdentity(identityThreshold);
        }
        ArrayList<BlastProtein> blastProteinsWith100Identity = this.blastFilter.filterMappingEntriesWithIdentity((float) 100);
        ArrayList<BlastProtein> blastProteinsGlobalAlignment = BlastResultFilter.collectMappingEntriesWithTotalAlignment(blastProteinsWith100Identity, context.getSequence().length());

        if (blastProteinsGlobalAlignment.size() == 0){

            Status status = new Status(StatusLabel.FAILED, "The blast on Uniprot couldn't return any proteins matching with 100% identity the all sequence.");
            report.setStatus(status);

            BlastReport report2 = new BlastReport(ActionName.BLAST_uniprot);
            this.listOfReports.add(report2);

            ArrayList<BlastProtein> globalResults = this.blastFilter.getMatchingEntries();

            if (globalResults.isEmpty()){
                Status status2 = new Status(StatusLabel.FAILED, "The blast on Uniprot couldn't return any proteins matching the sequence with an identity superior or equal to "+identityThreshold+"%.");
                report2.setStatus(status2);
            }
            else {
                Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "The blast on Uniprot returned " + globalResults.size() + " hit(s) with an identity inferior to 100% but superior to " + identityThreshold);
                report2.setStatus(status2);

                for (BlastProtein p : globalResults){
                    if (globalResults.indexOf(p) > maxNumberOfBlastProteins){
                        break;
                    }
                    else {
                        report2.addBlastMatchingProtein(p);
                    }
                }
            }
        }
        else if (blastProteinsGlobalAlignment.size() == 1){
            Status status = new Status(StatusLabel.COMPLETED, "The blast on Uniprot successfully returned an unique uniprot Id " + blastProteinsGlobalAlignment.get(0).getAccession() + "(100% identity on the all sequence)");
            report.setStatus(status);
            report.addBlastMatchingProtein(blastProteinsGlobalAlignment.get(0));

            return blastProteinsGlobalAlignment.get(0).getAccession();
        }
        else {
            Status status = new Status(StatusLabel.FAILED, "The blast on Uniprot returned "+ blastProteinsGlobalAlignment.size() +" matching proteins. (100% identity on the all sequence)");
            report.setStatus(status);
            for (BlastProtein p : blastProteinsGlobalAlignment){
                if (blastProteinsGlobalAlignment.indexOf(p) > maxNumberOfBlastProteins){
                    break;
                }
                else {
                    report.addBlastMatchingProtein(p);
                }
            }

            BlastReport report2 = new BlastReport(ActionName.BLAST_Swissprot_Total_Identity);
            this.listOfReports.add(report2);

            ArrayList<BlastProtein> swissprotProteins = getEntriesWithDatabase(blastProteinsGlobalAlignment, "SP");

            if (swissprotProteins.size() == 0){
                Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "The blast on Uniprot returned "+ blastProteinsGlobalAlignment.size() +" matching proteins from Trembl. (100% identity on the all sequence)");
                report2.setStatus(status2);

                for (BlastProtein p : blastProteinsGlobalAlignment){
                    if (blastProteinsGlobalAlignment.indexOf(p) > maxNumberOfBlastProteins){
                        break;
                    }
                    else {
                        report2.addBlastMatchingProtein(p);
                    }
                }
            }
            else if (swissprotProteins.size() == 1){
                Status status2 = new Status(StatusLabel.COMPLETED, "The blast on Uniprot successfully returned an unique swissprot entry" + blastProteinsGlobalAlignment.get(0).getAccession() + " (100% identity on the all sequence)");
                report2.setStatus(status2);
                report2.addBlastMatchingProtein(blastProteinsGlobalAlignment.get(0));

                return blastProteinsGlobalAlignment.get(0).getAccession();
            }
            else {
                Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "The blast on Uniprot returned "+ swissprotProteins.size() +" matching proteins from Swissprot. (100% identity on the all sequence)");
                report2.setStatus(status2);

                for (BlastProtein p : swissprotProteins){
                    if (swissprotProteins.indexOf(p) > maxNumberOfBlastProteins){
                        break;
                    }
                    else {
                        report2.addBlastMatchingProtein(p);
                    }
                }
            }
        }

        return null;
    }
}
