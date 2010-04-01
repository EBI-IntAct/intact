package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.blast.BlastResultFilter;
import uk.ac.ebi.intact.bridges.blast.BlastServiceException;
import uk.ac.ebi.intact.bridges.blast.ProteinWsWuBlastService;
import uk.ac.ebi.intact.bridges.blast.model.BlastProtein;
import uk.ac.ebi.intact.curationTools.actions.exception.SwissprotRemappingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.strategies.ProteinIdentification;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.service.IdentifierChecker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public class SwissprotRemappingProcess implements IdentificationAction{

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( SwissprotRemappingProcess.class );
    private ProteinWsWuBlastService blastService;
    private BlastResultFilter blastFilter = new BlastResultFilter();
    private BlastContext context;
    private static final float identityThreshold = (float) 99;
    private static final int maxNumberOfBlastProteins = 10;
    private List<ActionReport> listOfReports = new ArrayList<ActionReport>();    

    public SwissprotRemappingProcess(){
        try {
            this.blastService = new ProteinWsWuBlastService("marine@ebi.ac.uk");
            this.context = null;
        } catch (BlastServiceException e) {
            log.error("Problem instantiating the blast client while trying to find an equivalent swissprot entry to the Trembl entry which was found using PICR.",e);
        }
    }

    private Set<String> mergeIsoforms(ArrayList<BlastProtein> blastProteins){
        HashSet<String> isoformMerged = new HashSet<String>();
        for (BlastProtein b : blastProteins){
            if (b != null){

                if (b.getAccession() != null){
                    String primaryId;
                    if (IdentifierChecker.isSpliceVariantId(b.getAccession())){
                        primaryId = b.getAccession().substring(0, b.getAccession().indexOf("-"));
                    }
                    else {
                        primaryId = b.getAccession();
                    }
                    isoformMerged.add(primaryId);
                }
            }
        }

        return isoformMerged;
    }

    private String processBlast(ArrayList<BlastProtein> blastProteins, BlastReport report, boolean keepBlastResult) throws SwissprotRemappingException{
        if (blastProteins.size() == 1){
            if (blastProteins.get(0) != null){
                report.addBlastMatchingProtein(blastProteins.get(0));

                if (keepBlastResult){
                    Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + blastProteins.get(0).getAccession() + " : Trembl sequence matches the swissprot sequence with " + blastProteins.get(0).getIdentity() + " % identity.");

                    report.setStatus(status);
                    return blastProteins.get(0).getAccession();
                }
                else {
                    Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with this Swissprot entry " + blastProteins.get(0).getAccession() + "? The Swissprot entry has been found with " + blastProteins.get(0).getIdentity() + " % identity.");

                    report.setStatus(status);
                }
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

            Set<String> accessions = mergeIsoforms(blastProteins);

            if (accessions.size() == 1){
                String newUniprotId = accessions.iterator().next();

                if (keepBlastResult){
                    Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + newUniprotId + " : Trembl sequence matches several swissprot splice variant sequences of the same protein.");

                    report.setStatus(status);
                    return newUniprotId;
                }
                else {
                    Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with the Swissprot entry " + newUniprotId + " : Trembl sequence matches several swissprot splice variant sequences of this same protein.");

                    report.setStatus(status);
                }

            }
            else if (accessions.size() > 1){
                if (this.context.getEnsemblGene() == null){
                    Status status = new Status(StatusLabel.FAILED, "The Trembl entry doesn't have any ensembl gene accession and we can't decide which Swissprot entry can replace the Trembl entry : we found " + accessions.size() + " possible choices.");

                    report.setStatus(status);
                }
                else {

                    String newUniprotId = null;
                    for (String ac : accessions){
                        try {
                            String ensemblGene = ProteinIdentification.extractENSEMBLGeneAccessionFrom(ac);
                            if (ensemblGene != null){
                                if (this.context.getEnsemblGene().equals(ensemblGene)){
                                    newUniprotId = ac;
                                }
                            }
                        } catch (StrategyException e) {
                            throw new SwissprotRemappingException("We cannot get the ensembl gene accession of the uniprot entry " + ac,e);
                        }
                    }

                    if (newUniprotId == null){
                        Status status = new Status(StatusLabel.FAILED, "The blast returned several Swissprot entries but no one has an ensembl gene accession which matches "+ this.context.getEnsemblGene() +".");

                        report.setStatus(status);
                    }
                    else {
                        if (keepBlastResult){
                            Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + newUniprotId + " : the Trembl sequence matches several swissprot splice variant sequences of this protein and has the same ensembl gene accession : " + this.context.getEnsemblGene());

                            report.setStatus(status);
                            return newUniprotId;
                        }
                        else {
                            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with the Swissprot entry " + newUniprotId + " : the blast returned several splice variants of this same protein with the same ensembl gene accession "+ this.context.getEnsemblGene() +".");

                            report.setStatus(status);
                        }
                    }
                }
            }
            else {
                log.error("The blast on Swissprot didn't return valid results for the protein with the identifier "+ this.context.getIdentifier() +". Check the sequence " + context.getSequence());
            }
        }
        return null;
    }

    public String runAction(IdentificationContext context) throws SwissprotRemappingException {
        if (!(context instanceof BlastContext)){
            log.error("The SwissprotRemappingProcess needs a BlastContext instance and the current context is a " + context.getClass().getSimpleName());
        }
        else{
            this.context = (BlastContext) context;

            InputStream blastResults = this.blastService.getResultsOfBlastOnSwissprot(this.context.getSequence());
            this.blastFilter.setResults(blastResults);

            if (this.context.getDeducedScientificOrganismName() != null){
                this.blastFilter.filterResultsWithOrganism(this.context.getDeducedScientificOrganismName());
            }
            else{
                this.blastFilter.readResultsWithoutFiltering();
            }

            float i = (float) 100;

            BlastReport report = new BlastReport(ActionName.BLAST_swissprot);
            this.listOfReports.add(report);
            report.setQuerySequence(this.context.getSequence());

            while (report.getBlastMatchingProteins().size() == 0 && i >= identityThreshold){
                ArrayList<BlastProtein> blastProteins = this.blastFilter.filterMappingEntriesWithIdentity(i);

                if (blastProteins.isEmpty()){
                    i--;
                }
                else {
                    String accession = processBlast(blastProteins, report, true);

                    if (accession != null){
                        return accession;
                    }
                }
            }

            if (report.getBlastMatchingProteins().size() == 0){
                ArrayList<BlastProtein> blastProteins = this.blastFilter.getMatchingEntries();

                String accession = processBlast(blastProteins, report, false);
                if (accession != null){
                    return accession;
                }
            }
        }
        return null;
    }

    public List<ActionReport> getListOfActionReports() {
        return this.listOfReports;
    }
}
