package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.ncbiblast.BlastResultFilter;
import uk.ac.ebi.intact.bridges.ncbiblast.model.BlastProtein;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.BlastReport;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultBlastResults;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is doing a Blast on uniprot and process the blast results as following :
 * - look if swissprot entry(ies) with 100% on the all sequence
 * - look if trembl entry(ies) with 100% on the all sequence
 * - look at the blast results without filtering
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27-Apr-2010</pre>
 */

public class UniprotIdentityBlastProcess extends ActionNeedingBlastService {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( UniprotIdentityBlastProcess.class );

    /**
     * The swissprot database name in the results
     */
    private static final String swissprot = "SP";

    /**
     * The trembl database name in the results
     */
    private static final String trembl = "TR";

    /**
     * Create a new UniprotIdentityBlastProcess
     */
    public UniprotIdentityBlastProcess(ReportsFactory factory){
        super(factory);
    }

    /**
     * Select the BlastProteins which are from a certain database
     * @param proteins : proteins to filter
     * @param database : the database we want to look into
     * @return a list of BlastProteins which are from this database
     */
    private List<BlastProtein> getEntriesWithDatabase(List<BlastProtein> proteins, String database){

        List<BlastProtein> entriesWithDatabase = new ArrayList<BlastProtein>();

        for (BlastProtein p : proteins){
            if (p.getDatabase() != null){
                if (p.getDatabase().equals(database)){
                    entriesWithDatabase.add(p);
                }
            }
        }

        return entriesWithDatabase;
    }

    /**
     * do a Blast on uniprot and process the blast results as following :
     * - look if swissprot entry(ies) with 100% on the all sequence
     * - look if trembl entry(ies) with 100% on the all sequence
     * - look at the blast results without filtering
     * @param context  : the context of the protein
     * @return an unique Uniprto accession if we can have an unique Swissprot/Trembl match, null otherwise
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // Always clean the previous reports
        this.listOfReports.clear();

        // Create a new Blast report
        BlastReport report = getReportsFactory().getBlastReport(ActionName.BLAST_Uniprot_Total_Identity);
        this.listOfReports.add(report);

        // Run a blast on uniprot and store the results in the Blast filter
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
            // Filter the results on the 'minimumIdentityThreshold' and the organism
            this.blastFilter.filterResultsWithIdentityAndOrganism(minimumIdentityThreshold, context.getOrganism().getTaxId());
        }
        else{
            // Filter the results on the 'minimumIdentityThreshold'
            report.addWarning("No organism has been given for the sequence " + context.getSequence() + ". We will process the blast on uniprot without filtering with the organism.");
            this.blastFilter.filterMappingEntriesWithIdentity(minimumIdentityThreshold);
        }

        // Filter the results stored in the BlastFilter on 100% identity
        List<BlastProtein> blastProteinsWith100Identity = this.blastFilter.filterMappingEntriesWithIdentity((float) 100);

        // Filter the results stored in the BlastFilter on 100% identity on the all sequence
        List<BlastProtein> blastProteinsGlobalAlignment = BlastResultFilter.collectMappingEntriesWithTotalAlignment(blastProteinsWith100Identity, context.getSequence().length());

        // We don't have any proteins matching the all sequence with 100% identity
        if (blastProteinsGlobalAlignment.size() == 0){

            Status status = new Status(StatusLabel.FAILED, "The blast on Uniprot couldn't return any proteins matching with 100% identity the all sequence.");
            report.setStatus(status);

            // New Report to store the results without filtering on 100% identity on the all sequence
            BlastReport report2 = getReportsFactory().getBlastReport(ActionName.BLAST_uniprot);
            this.listOfReports.add(report2);

            List<BlastProtein> globalResults = this.blastFilter.getMatchingEntries();

            // We don't have any results
            if (globalResults.isEmpty()){
                Status status2 = new Status(StatusLabel.FAILED, "The blast on Uniprot couldn't return any proteins matching the sequence with an identity superior or equal to "+minimumIdentityThreshold+"%.");
                report2.setStatus(status2);
            }
            // We have several results we can had to the report so the curators can look at them afterwards
            else {
                Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "The blast on Uniprot returned " + globalResults.size() + " hit(s) with an identity inferior to 100% but superior to " + minimumIdentityThreshold);
                report2.setStatus(status2);

                for (BlastProtein p : globalResults){
                    if (globalResults.indexOf(p) > maxNumberOfBlastProteins){
                        break;
                    }
                    else {
                        report2.addBlastMatchingProtein(new DefaultBlastResults(p));
                    }
                }
            }
        }
        // One protein is matching the all sequence with 100% identity
        else if (blastProteinsGlobalAlignment.size() == 1){
            Status status = new Status(StatusLabel.COMPLETED, "The blast on Uniprot successfully returned an unique uniprot Id " + blastProteinsGlobalAlignment.get(0).getAccession() + "(100% identity on the all sequence)");
            report.setStatus(status);
            report.addBlastMatchingProtein(new DefaultBlastResults(blastProteinsGlobalAlignment.get(0)));

            if (blastProteinsGlobalAlignment.get(0).getDatabase().equalsIgnoreCase(swissprot) ){
                report.setIsASwissprotEntry(true);
            }

            return blastProteinsGlobalAlignment.get(0).getAccession();
        }
        // Several proteins are matching the all sequence with 100% identity
        else {
            Status status = new Status(StatusLabel.FAILED, "The blast on Uniprot returned "+ blastProteinsGlobalAlignment.size() +" matching proteins. (100% identity on the all sequence)");
            report.setStatus(status);

            // Add the results to the report
            for (BlastProtein p : blastProteinsGlobalAlignment){
                if (blastProteinsGlobalAlignment.indexOf(p) > maxNumberOfBlastProteins){
                    break;
                }
                else {
                    report.addBlastMatchingProtein(new DefaultBlastResults(p));
                }
            }

            // we will look filter on the database so a new Blast report is necessary
            BlastReport report2 = getReportsFactory().getBlastReport(ActionName.BLAST_Swissprot_Total_Identity);
            this.listOfReports.add(report2);

            // Get the results from Swissprot
            List<BlastProtein> swissprotProteins = getEntriesWithDatabase(blastProteinsGlobalAlignment, swissprot);

            // We don't have any Swissprot results and several Trembl entries
            if (swissprotProteins.size() == 0){
                Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "The blast on Uniprot returned "+ blastProteinsGlobalAlignment.size() +" matching proteins from Trembl. (100% identity on the all sequence)");
                report2.setStatus(status2);

                for (BlastProtein p : blastProteinsGlobalAlignment){
                    if (blastProteinsGlobalAlignment.indexOf(p) > maxNumberOfBlastProteins){
                        break;
                    }
                    else {
                        report2.addBlastMatchingProtein(new DefaultBlastResults(p));
                    }
                }
            }
            // We have one Swissprot entry, we keep it
            else if (swissprotProteins.size() == 1){
                Status status2 = new Status(StatusLabel.COMPLETED, "The blast on Uniprot successfully returned an unique swissprot entry" + blastProteinsGlobalAlignment.get(0).getAccession() + " (100% identity on the all sequence)");
                report2.setStatus(status2);
                report2.setIsASwissprotEntry(true);
                report2.addBlastMatchingProtein(new DefaultBlastResults(swissprotProteins.get(0)));

                return swissprotProteins.get(0).getAccession();
            }
            // we have sevral swisprot entries, we keep them
            else {
                Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "The blast on Uniprot returned "+ swissprotProteins.size() +" matching proteins from Swissprot. (100% identity on the all sequence)");
                report2.setStatus(status2);

                for (BlastProtein p : swissprotProteins){
                    if (swissprotProteins.indexOf(p) > maxNumberOfBlastProteins){
                        break;
                    }
                    else {
                        report2.addBlastMatchingProtein(new DefaultBlastResults(p));
                    }
                }
            }
        }

        return null;
    }
}
