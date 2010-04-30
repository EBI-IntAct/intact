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
import uk.ac.ebi.intact.curationTools.model.contexts.BlastContext;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.curationTools.strategies.IdentificationStrategyImpl;
import uk.ac.ebi.intact.curationTools.strategies.exceptions.StrategyException;
import uk.ac.ebi.intact.uniprot.service.IdentifierChecker;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Mar-2010</pre>
 */

public class SwissprotRemappingProcess extends IdentificationActionImpl {

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( SwissprotRemappingProcess.class );
    private ProteinNCBIBlastService blastService;
    private BlastResultFilter blastFilter = new BlastResultFilter();
    private BlastContext context;
    private static final float maximumIdentityThreshold = (float) 99;
    private static final float minimumIdentityThreshold = (float) 90;
    private static final int maxNumberOfBlastProteins = 10;

    public SwissprotRemappingProcess(){
        try {
            this.blastService = new ProteinNCBIBlastService("marine@ebi.ac.uk");
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

    private boolean checkEnsemblGene(BlastProtein protein, String ensemblGeneFromContext) throws ActionProcessingException {
        try{
            String ensemblGene = null;
            if (protein.getUniprotProtein() == null){
                ensemblGene = IdentificationStrategyImpl.extractENSEMBLGeneAccessionFrom(protein.getAccession());
            }
            else {
                ensemblGene = IdentificationStrategyImpl.extractENSEMBLGeneAccessionFrom(protein.getUniprotProtein());
            }

            if (ensemblGene != null && ensemblGeneFromContext != null){
                if (this.context.getEnsemblGene().equals(ensemblGene)){
                    return true;
                }
            }
            else if (ensemblGene == null && ensemblGeneFromContext == null){
                return true;
            }
            return false;
        }catch (StrategyException e) {
            throw new ActionProcessingException("We cannot get the ensembl gene accession of the uniprot entry " + protein.getAccession(),e);
        }
    }

    private boolean checkEnsemblGene(String protein, String ensemblGeneFromContext) throws ActionProcessingException {
        try{
            String ensemblGene = ensemblGene = IdentificationStrategyImpl.extractENSEMBLGeneAccessionFrom(protein);
            if (ensemblGene != null && ensemblGeneFromContext != null){
                if (this.context.getEnsemblGene().equals(ensemblGene)){
                    return true;
                }
            }
            else if (ensemblGene == null && ensemblGeneFromContext == null){
                return true;
            }
            return false;
        }catch (StrategyException e) {
            throw new ActionProcessingException("We cannot get the ensembl gene accession of the uniprot entry " + protein,e);
        }
    }

    private String processBlast(ArrayList<BlastProtein> blastProteins, BlastReport report, boolean keepBlastResult) throws ActionProcessingException{
        try {
            if (blastProteins.size() == 1){
                String ac = blastProteins.get(0).getAccession();

                if (context.getEnsemblGene() != null){
                    if (checkEnsemblGene(blastProteins.get(0), context.getEnsemblGene())){
                        if (keepBlastResult){
                            Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + ac + " : Trembl sequence matches the swissprot sequence with " + blastProteins.get(0).getIdentity() + " % identity and matches the Ensembl gene " + context.getEnsemblGene());

                            report.setStatus(status);
                            report.addBlastMatchingProtein(blastProteins.get(0));
                            return ac;
                        }
                        else {
                            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with this Swissprot entry " + ac + "? The Swissprot entry has been found with " + blastProteins.get(0).getIdentity() + " % identity and matches the Ensembl gene " + context.getEnsemblGene());

                            report.setStatus(status);
                            report.addBlastMatchingProtein(blastProteins.get(0));
                        }
                    }
                    else {
                        Status status = new Status(StatusLabel.FAILED, "The matching Swissprot entries are not matching the Ensembl gene " + context.getEnsemblGene());

                        report.setStatus(status);
                    }
                }
                else {
                    if (keepBlastResult){
                        Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + ac + " : Trembl sequence matches the swissprot sequence with " + blastProteins.get(0).getIdentity() + " % identity.");

                        report.setStatus(status);
                        report.addBlastMatchingProtein(blastProteins.get(0));
                        return ac;
                    }
                    else {
                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with this Swissprot entry " + ac + "? The Swissprot entry has been found with " + blastProteins.get(0).getIdentity() + " % identity.");

                        report.setStatus(status);
                        report.addBlastMatchingProtein(blastProteins.get(0));
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
                    String ac = accessions.iterator().next();

                    if (context.getEnsemblGene() != null){
                        if (checkEnsemblGene(blastProteins.get(0), context.getEnsemblGene())){
                            if (keepBlastResult){
                                Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + ac + " : the Trembl sequence matches several swissprot splice variant sequences of the same protein which matches the Ensembl gene " + context.getEnsemblGene());

                                report.setStatus(status);
                                return blastProteins.get(0).getAccession();
                            }
                            else {
                                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with this Swissprot entry " + ac + "? The Trembl sequence matches several swissprot splice variant sequences of this same protein which matches the Ensembl gene " + context.getEnsemblGene());

                                report.setStatus(status);
                            }
                        }
                        else {
                            Status status = new Status(StatusLabel.FAILED, "The matching Swissprot entries are not matching the Ensembl gene " + context.getEnsemblGene());

                            report.setStatus(status);
                        }
                    }
                    else {
                        if (keepBlastResult){
                            Status status = new Status(StatusLabel.COMPLETED, "We replaced the Trembl entry with the Swissprot entry " + ac + " : the Trembl sequence matches several swissprot splice variant sequences of the same protein.");

                            report.setStatus(status);
                            return ac;
                        }
                        else {
                            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "Could we replace the Trembl entry with the Swissprot entry " + ac + " : The Trembl sequence matches several swissprot splice variant sequences of this same protein.");

                            report.setStatus(status);
                        }
                    }
                }
                else if (accessions.size() > 1){

                    if (this.context.getEnsemblGene() == null){
                        Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The Trembl entry doesn't have any ensembl gene accession and we can't decide which Swissprot entry can replace the Trembl entry : we found " + accessions.size() + " possible choices.");

                        report.setStatus(status);
                    }
                    else {

                        String newUniprotId = null;
                        for (String s : accessions){
                            if (checkEnsemblGene(s, this.context.getEnsemblGene())){
                                newUniprotId = s;
                                break;
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
        }catch (ActionProcessingException e) {
            throw new ActionProcessingException("We cannot process the swissprot remapping properly.",e);
        }
        return null;
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        if (!(context instanceof BlastContext)){
            log.error("The SwissprotRemappingProcess needs a BlastContext instance and the current context is a " + context.getClass().getSimpleName());
        }
        else{
            this.context = (BlastContext) context;

            InputStream blastResults = this.blastService.getResultsOfBlastOnSwissprot(this.context.getSequence());
            this.blastFilter.setResults(blastResults);

            if (this.context.getOrganism() != null){
                this.blastFilter.filterResultsWithIdentityAndOrganism(minimumIdentityThreshold, this.context.getOrganism().getTaxId());
            }
            else{
                this.blastFilter.filterResultsWithIdentity(minimumIdentityThreshold);
            }

            float i = (float) 100;

            ArrayList<BlastProtein> blastProteins = new ArrayList<BlastProtein>();

            while (blastProteins.size() == 0 && i >= maximumIdentityThreshold){
                BlastReport report = new BlastReport(ActionName.BLAST_Swissprot_Remapping);
                this.listOfReports.add(report);
                report.setQuerySequence(this.context.getSequence());

                blastProteins = this.blastFilter.filterMappingEntriesWithIdentity(i);

                if (blastProteins.isEmpty()){
                    Status status = new Status(StatusLabel.FAILED, "The blast on Swissprot didn't return any proteins matching the sequence with " + i + "% identity.");
                    report.setStatus(status);
                    i--;
                }
                else {
                    String accession = processBlast(blastProteins, report, true);

                    if (accession != null){
                        return accession;
                    }
                }
            }

            BlastReport lastReport = (BlastReport) this.listOfReports.get(this.listOfReports.size() - 1);
            if (lastReport.getBlastMatchingProteins().size() == 0){
                Status status = new Status(StatusLabel.FAILED, "The blast on Swissprot didn't return any proteins matching the sequence with " + i + "% identity.");
                lastReport.setStatus(status);

                BlastReport report = new BlastReport(ActionName.BLAST_swissprot);
                this.listOfReports.add(report);
                report.setQuerySequence(this.context.getSequence());

                blastProteins = this.blastFilter.getMatchingEntries();

                processBlast(blastProteins, report, false);
            }
        }
        return null;
    }
}
