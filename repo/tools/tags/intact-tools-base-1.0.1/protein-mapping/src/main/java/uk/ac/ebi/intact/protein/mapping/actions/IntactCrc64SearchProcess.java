package uk.ac.ebi.intact.protein.mapping.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.commons.util.Crc64;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.model.ProteinImpl;
import uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.protein.mapping.actions.status.Status;
import uk.ac.ebi.intact.protein.mapping.actions.status.StatusLabel;
import uk.ac.ebi.intact.protein.mapping.factories.ReportsFactory;
import uk.ac.ebi.intact.protein.mapping.model.actionReport.IntactCrc64Report;
import uk.ac.ebi.intact.protein.mapping.model.contexts.IdentificationContext;

import java.util.List;

/**
 * This class is looking into the IntAct database for proteins with a same CRC64 that the one of the protein to identify
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Apr-2010</pre>
 */

public class IntactCrc64SearchProcess extends IdentificationActionImpl {
    
    /**
     * Sets up a logger for this class.
     */
    public static final Log log = LogFactory.getLog( IntactCrc64SearchProcess.class);

    /**
     * Create an IntactCrc64SearchProcess
     */
    public IntactCrc64SearchProcess(ReportsFactory factory){
        super(factory);
    }

    /**
     * Look into the IntAct database if the CRC64 of the sequence to identify is matching an Intact entry. If the organism of the protein
     * to identify is given, add a filter on the organism to the search
     * @param context  : the context of the protein
     * @return Always null as the process doesn't aimed at finding an unique uniprot entry but aimed at finding an unique IntAct entry. It Will add the results of the process
     * (Intact accession, possible intact entries, etc.) on an DefaultIntactCrc64Report which will be added to the list of reports of this object.
     * @throws uk.ac.ebi.intact.protein.mapping.actions.exception.ActionProcessingException
     */
    public String runAction(IdentificationContext context) throws ActionProcessingException {
        // Always clear the previous action reports from the list
        this.listOfReports.clear();

        IntactContext intactContext = IntactContext.getCurrentInstance();

        // Create an DefaultIntactCrc64Report
        IntactCrc64Report report = getReportsFactory().getIntactCrc64Report(ActionName.SEARCH_intact_crc64);
        this.listOfReports.add(report);

        // Add the sequence to the report
        report.setQuerySequence(context.getSequence());

        // Get the Intact datacontext
        final DataContext dataContext = intactContext.getDataContext();
        final DaoFactory daoFactory = dataContext.getDaoFactory();

        // get the CRC64 of the sequence
        String CRC64 = Crc64.getCrc64(context.getSequence());

        List<ProteinImpl> proteins;
        String taxId = null;

        if (context.getOrganism() == null){
            report.addWarning("The organism is null for the sequence " + context.getSequence() + ". We will not filter the Intact entries with the organism.");

            // Organism is null, we only look for the CRC64 in the Intact database
            proteins = daoFactory.getProteinDao().getByCrc(CRC64);
        }
        else {
            taxId = context.getOrganism().getTaxId();
            if (taxId == null){
                report.addWarning("The taxId of the organism is null for the sequence " + context.getSequence() + ". We will not filter the Intact entries with the organism.");

                // Organism TaxId is null, we only look for the CRC64 in the Intact database
                proteins = daoFactory.getProteinDao().getByCrc(CRC64);
            }
            else {
                // TaxId not null, we look for the CRC64 in the Intact database with the specific organism
                proteins = daoFactory.getProteinDao().getByCrcAndTaxId(CRC64, context.getOrganism().getTaxId());
            }
        }

        if (proteins.isEmpty()){
            Status status = new Status(StatusLabel.FAILED, "No proteins in IntAct could match the CRC64 ("+CRC64+") of the sequence " + context.getSequence() + (taxId != null ? " with organism " + taxId : ""));
            report.setStatus(status);
        }
        else if (proteins.size() == 1){
            if (proteins.get(0) != null){
                report.setIntactAc(proteins.get(0).getAc());

                Status status = new Status(StatusLabel.COMPLETED, "The Crc64 search on Intact successfully returned the IntAct entry " + report.getIntactAc()  + (taxId != null ? " with organism " + taxId : ""));
                report.setStatus(status);
            }
        }
        else {
            for (ProteinImpl p : proteins){
                report.addPossibleIntactAc(p.getAc());
            }
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The Crc64 search on IntAct returned " + proteins.size() + " matching IntAct entries."  + (taxId != null ? " with organism " + taxId : ""));
            report.setStatus(status);

        }
        return null;
    }
}
