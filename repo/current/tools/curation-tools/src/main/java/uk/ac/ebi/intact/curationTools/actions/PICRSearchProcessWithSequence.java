package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.bridges.picr.PicrClient;
import uk.ac.ebi.intact.bridges.picr.PicrClientException;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.picr.model.CrossReference;
import uk.ac.ebi.picr.model.UPEntry;

import java.util.ArrayList;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31-Mar-2010</pre>
 */

public class PICRSearchProcessWithSequence extends IdentificationActionImpl {

    private PicrClient picrClient;

    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( PICRSearchProcessWithSequence.class );

    public PICRSearchProcessWithSequence(){
        this.picrClient = new PicrClient();
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        String sequence = context.getSequence();
        String taxId = null;
        if (context.getOrganism() != null){
            taxId = context.getOrganism().getTaxId();
        }

        PICRReport report = new PICRReport(ActionName.PICR_sequence_Swissprot);
        this.listOfReports.add(report);

        if (taxId == null){

            report.addWarning("No organism was given for the sequence " + sequence + ". We will process the identification without looking at the organism. We will keep only the swissprot results if there are both Swissprot and Trembl results.");
        }

        try {
            ArrayList<String> swissprotIds = this.picrClient.getSwissprotIdsForSequence(sequence, taxId);

            if (swissprotIds.size() == 1){
                if (swissprotIds.get(0) != null){
                    Status status = new Status(StatusLabel.COMPLETED, "PICR successfully returned an unique Swissprot accession " + swissprotIds.get(0));
                    report.setStatus(status);

                    report.setIsASwissprotEntry(true);
                    return swissprotIds.get(0);
                }
                else {
                    log.error("PICR returned an empty Swissprot accession for the sequence " + sequence);
                }
            }
            else if (swissprotIds.size() > 1){
                Status status = new Status(StatusLabel.TO_BE_REVIEWED, "PICR returned " + swissprotIds.size() + " Swissprot accessions which are matching the sequence.");
                report.setStatus(status);

                report.addWarning("Several SwissprotIds have been returned as no organism was given to filter the results. We will not process the sequence mapping in Trembl.");

                for (String ac : swissprotIds){
                    report.addPossibleAccession(ac);
                }

            }
            else {
                Status status = new Status(StatusLabel.FAILED, "PICR couldn't match the sequence to any Swissprot entries.");
                report.setStatus(status);

                PICRReport report2 = new PICRReport(ActionName.PICR_sequence_Trembl);
                this.listOfReports.add(report2);
                report2.getWarnings().addAll(report.getWarnings());
                
                ArrayList<String> tremblIds = this.picrClient.getTremblIdsForSequence(sequence, taxId);

                if (tremblIds.size() == 1){
                    Status status2 = new Status(StatusLabel.COMPLETED, "PICR successfully returned an unique Trembl accession " + tremblIds.get(0));
                    report2.setStatus(status2);

                    report.setIsASwissprotEntry(false);
                    return tremblIds.get(0);
                }
                else if (tremblIds.size() > 1){
                    Status status2 = new Status(StatusLabel.TO_BE_REVIEWED, "PICR returned " + tremblIds.size() + " Trembl accessions which are matching the sequence.");
                    report2.setStatus(status2);

                    for (String ac : tremblIds){
                        report2.addPossibleAccession(ac);
                    }
                }
                else {
                    Status status2 = new Status(StatusLabel.FAILED, "PICR couldn't match any Uniprot entry to the sequence " + sequence);
                    report2.setStatus(status2);

                    UPEntry entry = this.picrClient.getUPEntriesForSequence(sequence, taxId);

                    if (entry != null){
                        for (CrossReference ref : entry.getIdenticalCrossReferences()){
                            report.addCrossReference(ref.getDatabaseName(), ref.getAccession());
                        }
                    }
                }
            }

        } catch (PicrClientException e) {
            throw  new ActionProcessingException("PICR couldn't match the sequence " + sequence + " to any Uniprot accession. Check your identifier and/or organism.", e);
        }
        return null;
    }
}
