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
import uk.ac.ebi.intact.protein.mapping.results.BlastResults;
import uk.ac.ebi.intact.protein.mapping.results.impl.DefaultBlastResults;
import uk.ac.ebi.intact.protein.mapping.strategies.IdentificationStrategyImpl;
import uk.ac.ebi.intact.protein.mapping.strategies.exceptions.StrategyException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This class is doing a BLAST on Swissprot to remap a Trembl entry to a Swissprot entry with an identity percent superior or equal to 'maximumIdentityThreshold'
 * and with the same Ensembl gene as the one of the Trembl entry
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public class SwissprotRemappingProcess extends ActionNeedingBlastService {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( SwissprotRemappingProcess.class );

    private final static int coverage_percent = 99;

    /**
     * The blast context.
     */
    private BlastContext context;

    /**
     * Minimum identity threshold for what we can keep the matching Swissprot entry
     */
    private static final float maximumIdentityThreshold = (float) 99;

    /**
     * Create a new SwissprotRemappingProcess
     */
    public SwissprotRemappingProcess(ReportsFactory factory){
        super(factory);
    }

    /**
     *
     * @param protein : the protein to check
     * @return true if the alignment covers at least the coverage_percent threshold  of the total query and match sequence
     */
    private boolean checkSequenceCoverageOfAlignment(BlastProtein protein){
        float queryCoveragePercent = getQuerySequenceCoveragePercentFor(protein);
        float matchCoveragePercent = getMatchSequenceCoveragePercentFor(protein);

        if (queryCoveragePercent >= coverage_percent && matchCoveragePercent >= coverage_percent){
            return true;
        }
        return false;
    }

    /**
     *
     * @param proteins : the list of blast proteins
     * @return true if at least one of the blast proteins representing an isoform of the same protein has a sequence coverage
     * superior or equal to coveragePercent
     */
    private boolean checkAllSequenceCoverageForIsoformsFromBlastReport(Collection<BlastResults> proteins){

        for (BlastResults p : proteins){
            float queryCoveragePercent = getQuerySequenceCoveragePercentFor(p);
            float matchCoveragePercent = getMatchSequenceCoveragePercentFor(p);

            if (queryCoveragePercent >= coverage_percent && matchCoveragePercent >= coverage_percent){
                return true;
            }
        }
        return false;
    }

        /**
     *
     * @param protein : the protein to check
     * @return true if the alignment covers at least the coverage_percent threshold  of the total query and match sequence
     */
    private boolean checkSequenceCoverageOfAlignment(BlastResults protein){
        float queryCoveragePercent = getQuerySequenceCoveragePercentFor(protein);
        float matchCoveragePercent = getMatchSequenceCoveragePercentFor(protein);

        if (queryCoveragePercent >= coverage_percent && matchCoveragePercent >= coverage_percent){
            return true;
        }
        return false;
    }

    /**
     *
     * @param proteins : the list of blast proteins
     * @return true if at least one of the blast proteins representing an isoform of the same protein has a sequence coverage
     * superior or equal to coveragePercent
     */
    private boolean checkAllSequenceCoverageForIsoforms(Collection<BlastProtein> proteins){

        for (BlastProtein p : proteins){
            float queryCoveragePercent = getQuerySequenceCoveragePercentFor(p);
            float matchCoveragePercent = getMatchSequenceCoveragePercentFor(p);

            if (queryCoveragePercent >= coverage_percent && matchCoveragePercent >= coverage_percent){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param protein : the blast protein
     * @return the sequence coverage of the alignment for the query sequence
     */
    private float getQuerySequenceCoveragePercentFor(BlastProtein protein){
        if (protein == null){
            return 0;
        }
        return ((float) (protein.getEndQuery() - protein.getStartQuery() + 1)) / (float) this.context.getSequence().length() * 100;
    }

    /**
     *
     * @param protein : the blast protein
     * @return the sequence coverage of the alignment for the match sequence
     */
    private float getMatchSequenceCoveragePercentFor(BlastProtein protein){
        if (protein == null){
            return 0;
        }
        return ((float) (protein.getEndMatch() - protein.getStartMatch() + 1)) / (float) protein.getSequence().length() * 100;
    }

        /**
     *
     * @param protein : the blast protein
     * @return the sequence coverage of the alignment for the query sequence
     */
    private float getQuerySequenceCoveragePercentFor(BlastResults protein){
        if (protein == null){
            return 0;
        }
        return ((float) (protein.getEndQuery() - protein.getStartQuery() + 1)) / (float) this.context.getSequence().length() * 100;
    }

    /**
     *
     * @param protein : the blast protein
     * @return the sequence coverage of the alignment for the match sequence
     */
    private float getMatchSequenceCoveragePercentFor(BlastResults protein){
        if (protein == null){
            return 0;
        }
        return ((float) (protein.getEndMatch() - protein.getStartMatch() + 1)) / (float) protein.getSequence().length() * 100;
    }

    /**
     * Check if a BlastProtein has the same ensembl gene as the one of the Trembl entry
     * @param protein : the protein to check
     * @param ensemblGeneFromContext : the ensemblGene of the Trembl entry
     * @return true if the BlastProtein has the same ensembl gene as the one of the Trembl entry
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    private boolean checkEnsemblGene(BlastProtein protein, String ensemblGeneFromContext) throws ActionProcessingException {
        try{
            String ensemblGene = null;

            // Check first if we didn't store the UniprotProtein instance in the BlastProtein.
            if (protein.getUniprotProtein() == null){
                // We have to first retrieve the UniprotProtein and then extract the Ensembl gene of the UniprotProtein
                ensemblGene = IdentificationStrategyImpl.extractENSEMBLGeneAccessionFrom(protein.getAccession());
            }
            else {
                // We can directly extract the ensembl gene from the UniprotProtein instance stored in the BlastProtein
                ensemblGene = IdentificationStrategyImpl.extractENSEMBLGeneAccessionFrom(protein.getUniprotProtein());
            }

            // If the Ensembl genes are not null
            if (ensemblGene != null && ensemblGeneFromContext != null){
                if (this.context.getEnsemblGene().equals(ensemblGene)){
                    return true;
                }
            }
            // If the both ensembl genes are null, there is no conflict in the ensembl genes
            else if (ensemblGene == null && ensemblGeneFromContext == null){
                return true;
            }
            return false;
        }catch (StrategyException e) {
            throw new ActionProcessingException("We cannot get the ensembl gene accession of the uniprot entry " + protein.getAccession(),e);
        }
    }

    /**
     * Check if a protein has the same ensembl gene as the one of the Trembl entry
     * @param protein : the accession of the protein to check
     * @param ensemblGeneFromContext : the ensemblGene of the Trembl entry
     * @return true if the protein has the same ensembl gene as the one of the Trembl entry
     * @throws ActionProcessingException
     */
    private boolean checkEnsemblGene(String protein, String ensemblGeneFromContext) throws ActionProcessingException {
        try{
            String ensemblGene = ensemblGene = IdentificationStrategyImpl.extractENSEMBLGeneAccessionFrom(protein);
            // If the Ensembl genes are not null
            if (ensemblGene != null && ensemblGeneFromContext != null){
                if (this.context.getEnsemblGene().equals(ensemblGene)){
                    return true;
                }
            }
            // If the both ensembl genes are null, there is no conflict in the ensembl genes
            else if (ensemblGene == null && ensemblGeneFromContext == null){
                return true;
            }
            return false;
        }catch (StrategyException e) {
            throw new ActionProcessingException("We cannot get the ensembl gene accession of the uniprot entry " + protein,e);
        }
    }

    /**
     * Process the Blast results :
     * - if we have only one Swissprot entry in the BLAST results : check the ensembl gene
     * - if we have several Swissprot entries : merge the isoforms and and check the ensembl genes of the Swissprot results
     * @param blastProteins : the results of the blast
     * @param report : the current report
     * @param keepBlastResult : boolean value to know if we want to replace the Trembl entry with the Swissprot entry
     * @return The unique Swissprot AC if we want to keep the swissprot entry which matches the ensembl gene of the Trembl entry
     * @throws ActionProcessingException
     */
    private String processBlast(List<BlastProtein> blastProteins, BlastReport<BlastResults> report, boolean keepBlastResult) throws ActionProcessingException{
        try {
            // Only one Swissprot entry in the BLAST results
            if (blastProteins.size() == 1){
                String ac = blastProteins.get(0).getAccession();

                // The Trembl entry has an ensembl gene so we can process the Swissprot remapping process
                if (context.getEnsemblGene() != null){
                    if (checkEnsemblGene(blastProteins.get(0), context.getEnsemblGene())){
                        if (checkSequenceCoverageOfAlignment(blastProteins.get(0))){
                            if (keepBlastResult){
                                Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + ac + " : Trembl sequence matches the swissprot sequence with " + blastProteins.get(0).getIdentity() + " % identity and matches the Ensembl gene " + context.getEnsemblGene());
                                report.setIsASwissprotEntry(true);
                                report.setStatus(status);
                                report.addBlastMatchingProtein(new DefaultBlastResults(blastProteins.get(0)));
                                return ac;
                            }
                            else {
                                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with this Swissprot entry " + ac + "? The Swissprot entry has been found with " + blastProteins.get(0).getIdentity() + " % identity and matches the Ensembl gene " + context.getEnsemblGene());

                                report.setStatus(status);
                                report.addBlastMatchingProtein(new DefaultBlastResults(blastProteins.get(0)));
                            }
                        }
                        else {
                            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The Swissprot entry has been found with " + blastProteins.get(0).getIdentity() + " % identity and matches the Ensembl gene " + context.getEnsemblGene() + " but the sequence coverage of the alignment is " + getQuerySequenceCoveragePercentFor(blastProteins.get(0)) + "% for the query sequence and "+ getMatchSequenceCoveragePercentFor(blastProteins.get(0))+"% for the match sequence.");

                            report.setStatus(status);
                            report.addBlastMatchingProtein(new DefaultBlastResults(blastProteins.get(0)));
                        }
                    }
                    else {
                        Status status = new Status(StatusLabel.FAILED, "The matching Swissprot entries are not matching the Ensembl gene " + context.getEnsemblGene());

                        report.setStatus(status);
                    }
                }
                // the trembl entry doesn't have any ensembl gene so we can't change the Trembl entry with a mapping Swissprot entry without the curator
                else {
                    report.addWarning("The ensembl gene of the Trembl entry doesn't have an Ensembl gene.");

                    Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with this Swissprot entry " + ac + "? The Swissprot entry has been found with " + blastProteins.get(0).getIdentity() + " % identity.");

                    report.setStatus(status);
                    report.addBlastMatchingProtein(new DefaultBlastResults(blastProteins.get(0)));
                }
            }
            // We have several Swissprot entries in the BLAST results
            else if (blastProteins.size() > 1) {

                // Add the BLAST results
                for (BlastProtein b : blastProteins){

                    if (blastProteins.indexOf(b) > maxNumberOfBlastProteins){
                        break;
                    }
                    else {
                        report.addBlastMatchingProtein(new DefaultBlastResults(b));
                    }
                }

                // Merge the possible isoforms of the same protein
                Set<String> accessions = mergeIsoformsFromBlastResults(blastProteins);

                // If the several BLASTProteins was isoforms of the same protein, we can do the Swissprot remapping process
                if (accessions.size() == 1){
                    String ac = accessions.iterator().next();

                    // If the Trembl entry has an ensembl gene, we can complete the Swissprot remapping process
                    if (context.getEnsemblGene() != null){
                        if (checkEnsemblGene(ac, context.getEnsemblGene())){
                            if (checkAllSequenceCoverageForIsoforms(blastProteins)){
                                if (keepBlastResult){
                                    Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + ac + " : the Trembl sequence matches several swissprot splice variant sequences of the same protein which matches the Ensembl gene " + context.getEnsemblGene());
                                    report.setIsASwissprotEntry(true);
                                    report.setStatus(status);
                                    return ac;
                                }
                                else {
                                    Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with this Swissprot entry " + ac + "? The Trembl sequence matches several swissprot splice variant sequences of this same protein which matches the Ensembl gene " + context.getEnsemblGene());

                                    report.setStatus(status);
                                }
                            }
                            else {
                                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Several isoforms of the same protein match the Ensembl gene " + context.getEnsemblGene() + " but there are not any isoforms wich have a sequence coverage percent of at least "+coverage_percent+"%.");

                                report.setStatus(status);
                            }
                        }
                        else {
                            Status status = new Status(StatusLabel.FAILED, "The matching Swissprot entries are not matching the Ensembl gene " + context.getEnsemblGene());
                            report.getBlastMatchingProteins().remove(blastProteins.get(0));
                            report.setStatus(status);
                        }
                    }
                    // If the Trembl entry doesn't have an ensembl gene, we need a curator to complete the Swissprot remapping process
                    else {
                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with the Swissprot entry " + ac + " : The Trembl sequence matches several swissprot splice variant sequences of this same protein.");

                        report.setStatus(status);
                    }
                }
                // The several blastProteins are different proteins
                else if (accessions.size() > 1){

                    // Without any ensembl gene from the Trembl entry, we can't decide which Swissprot entry can replace the Trembl
                    if (this.context.getEnsemblGene() == null){
                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The Trembl entry doesn't have any ensembl gene accession and we can't decide which Swissprot entry can replace the Trembl entry : we found " + accessions.size() + " possible choices.");

                        report.setStatus(status);
                    }
                    else {

                        String newUniprotId = null;

                        // The list of Swissprot entries with the matching ensembl gene
                        ArrayList<String> matchingAcs = new ArrayList<String>();

                        for (String s : accessions){
                            if (checkEnsemblGene(s, this.context.getEnsemblGene())){
                                matchingAcs.add(s);
                            }
                            else {
                                // remove the results with another ensembl gene
                                BlastResults proteinToRemove = null;
                                for (BlastResults p : report.getBlastMatchingProteins()){
                                    if (p.getAccession().startsWith(s)){
                                        proteinToRemove = p;
                                        break;
                                    }
                                }
                                if (proteinToRemove != null){
                                    report.getBlastMatchingProteins().remove(proteinToRemove);
                                }
                            }
                        }

                        // If the list of Swissprot entries with the matching ensembl gene is empty, the swissprot remapping is not possible
                        if (matchingAcs.isEmpty()){
                            Status status = new Status(StatusLabel.FAILED, "The blast returned several Swissprot entries but no one has an ensembl gene accession which matches "+ this.context.getEnsemblGene() +".");

                            report.setStatus(status);
                        }
                        // If there is an unique Swissprot entries which are matching the ensembl gene, we keep it if possible
                        else if (matchingAcs.size() == 1){
                            newUniprotId = matchingAcs.get(0);

                            if (checkAllSequenceCoverageForIsoformsFromBlastReport(report.getBlastMatchingProteins())){
                                if (keepBlastResult){
                                    Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + newUniprotId + " : the Trembl sequence matches several swissprot splice variant sequences of this protein and has the same ensembl gene accession : " + this.context.getEnsemblGene());
                                    report.setIsASwissprotEntry(true);
                                    report.setStatus(status);
                                    return newUniprotId;
                                }
                                else {
                                    Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with the Swissprot entry " + newUniprotId + " : the blast returned several splice variants of this same protein with the same ensembl gene accession "+ this.context.getEnsemblGene() +".");

                                    report.setStatus(status);
                                }
                            }
                            else {
                                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The protein "+newUniprotId+" matches the Ensembl gene " + context.getEnsemblGene() + " but the sequence coverage percent is inferior to "+coverage_percent+"%.");

                                report.setStatus(status);
                            }
                        }
                        // Several swissprot entries are matching the ensembl gene, we need a curator to choose the good one
                        else {
                            Status status = new Status(StatusLabel.TO_BE_REVIEWED, matchingAcs.size() + " Swissprot entries are matching the same ensembl gene accession "+ this.context.getEnsemblGene() +".");

                            report.setStatus(status);
                        }
                    }
                }
                else {
                    log.error("The blast on Swissprot didn't return valid results for the protein with the identifier "+ this.context.getIdentifier() +". Check the sequence " + context.getSequence());
                }
            }
        }catch (ActionProcessingException e) {
            throw new ActionProcessingException("We cannot process the swissprot remapping properly.",e);
        }
        return null;
    }

    /**
     * Run a BLAST on Swissprot and try to remap an unique Swissprot entry with the same ensembl gene as the Trembl entry and an
     * identity percent superior or equal to the maximum identity threshold.
     * All the Blast results we want to keep are strored in different Blast reports added to the list of reports
     * @param context  : the context of the protein
     * @return an unique swissprot entry if the remapping was successful, null otherwise
     * @throws ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // Always clear the previous reports
        this.listOfReports.clear();

        // We need a BlastContext with the ensembl gene of the Trembl entry
        if (!(context instanceof BlastContext)){
            log.error("The SwissprotRemappingProcess needs a BlastContext instance and the current context is a " + context.getClass().getSimpleName());
        }
        else{
            this.context = (BlastContext) context;

            // Run the blast on swissprot and keep the result in the Blast filter
            InputStream blastResults = this.blastService.getResultsOfBlastOnSwissprot(this.context.getSequence());
            try{
                this.blastFilter.setResults(blastResults);
            }
            finally {
                try {
                    blastResults.close();
                } catch (IOException e) {
                    log.error("Impossible to close BLAST results", e);
                }
            }

            if (this.context.getOrganism() != null){
                // Filter the Blast results on the minimum threshold identity and the organism
                this.blastFilter.filterResultsWithIdentityAndOrganism(minimumIdentityThreshold, this.context.getOrganism().getTaxId());
            }
            else{
                // Filter the Blast results on the minimum threshold identity
                this.blastFilter.filterResultsWithIdentity(minimumIdentityThreshold);
            }

            // Starts with identity = 100%
            float i = (float) 100;

            List<BlastProtein> blastProteins = new ArrayList<BlastProtein>();

            // While the maximum threshold identity is not reached, we can replace the Trembl entry with a matching swissprot entry, otherwise, we need a curator to decide
            while (blastProteins.size() == 0 && i >= maximumIdentityThreshold){

                // Create a blast report each time we decrease the identity percent
                BlastReport report = getReportsFactory().getBlastReport(ActionName.BLAST_Swissprot_Remapping);
                this.listOfReports.add(report);
                report.setQuerySequence(this.context.getSequence());

                // Filter the Blast proteins stored on the filter on the identity i
                blastProteins = this.blastFilter.filterMappingEntriesWithIdentity(i);

                // If we don't have any results, we decrease the identity percent
                if (blastProteins.isEmpty()){
                    Status status = new Status(StatusLabel.FAILED, "The blast on Swissprot didn't return any proteins matching the sequence with " + i + "% identity.");
                    report.setStatus(status);
                    i--;
                }
                else {

                    // we process the blast results
                    String accession = processBlast(blastProteins, report, true);

                    if (accession != null){
                        return accession;
                    }
                }
            }

            // We get the last DefaultBlastReport in case the status has not been added yet
            BlastReport lastReport = (BlastReport) this.listOfReports.get(this.listOfReports.size() - 1);

            // if we don't have any results with the filter on the maximum identity threshold
            if (lastReport.getBlastMatchingProteins().size() == 0){

                // If the last report doesn't have a status, we had one with label = FAILED because it means that the Blast proces couldn't find any Swissprot proteins
                if (lastReport.getStatus() == null){
                    Status status = new Status(StatusLabel.FAILED, "The blast on Swissprot didn't return any proteins matching the sequence with " + i + "% identity.");
                    lastReport.setStatus(status);
                }

                // Create a new Blast report where we can stores the Blast results not filtered with the maximum identity threshold
                BlastReport report = getReportsFactory().getBlastReport(ActionName.BLAST_Swissprot_Remapping);
                this.listOfReports.add(report);
                report.setQuerySequence(this.context.getSequence());

                blastProteins = this.blastFilter.getMatchingEntries();

                // We process the results but this time don't allow the Swissprot entry(ies) to replace the Trembl entry as we need a curator to decide
                processBlast(blastProteins, report, false);

                // If the results not filtered are empty, we had a status FAILED to the report
                if (report.getBlastMatchingProteins().isEmpty() && report.getStatus() == null){
                    Status status = new Status(StatusLabel.FAILED, "The blast on Swissprot didn't return any proteins matching the sequence with more than " + minimumIdentityThreshold + "% identity.");
                    report.setStatus(status);
                }
            }
        }
        return null;
    }
}
