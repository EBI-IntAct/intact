package uk.ac.ebi.intact.curationTools.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.commons.util.Crc64;
import uk.ac.ebi.intact.core.context.DataContext;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.curationTools.actions.exception.ActionProcessingException;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.IntactCrc64Report;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.Status;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;
import uk.ac.ebi.intact.curationTools.model.contexts.IdentificationContext;
import uk.ac.ebi.intact.model.ProteinImpl;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08-Apr-2010</pre>
 */

public class IntactCrc64SearchProcess extends IdentificationActionImpl {

    private IntactContext intactContext;
    
    /**
     * Sets up a logger for that class.
     */
    public static final Log log = LogFactory.getLog( IntactCrc64SearchProcess.class);

    public IntactCrc64SearchProcess(){
        super();
        intactContext = null;
    }

    public IntactCrc64SearchProcess(IntactContext context){
        super();
        intactContext = context;
    }

    public IntactContext getIntactContext() {
        return intactContext;
    }

    public void setIntactContext(IntactContext intactContext) {
        this.intactContext = intactContext;
    }

    public String runAction(IdentificationContext context) throws ActionProcessingException {
        this.listOfReports.clear();

        if (this.intactContext == null){
            throw new ActionProcessingException("To be able to search if the Intact database contains a protein with a matching CRC64, we need a non null IntactContext instance. Please, set the IntactContext instance.");
        }

        IntactCrc64Report report = new IntactCrc64Report(ActionName.SEARCH_intact_crc64);
        this.listOfReports.add(report);
        
        report.setQuerySequence(context.getSequence());
        final DataContext dataContext = this.intactContext.getDataContext();
        final DaoFactory daoFactory = dataContext.getDaoFactory();

        String CRC64 = Crc64.getCrc64(context.getSequence());

        List<ProteinImpl> proteins;
        String taxId = null;
        if (context.getOrganism() == null){
            report.addWarning("The organism is null for the sequence " + context.getSequence() + ". We will not filter the Intact entries with the organism.");

            proteins = daoFactory.getProteinDao().getByCrc(CRC64);
        }
        else {
            taxId = context.getOrganism().getTaxId();
            if (taxId == null){
                report.addWarning("The taxId of the organism is null for the sequence " + context.getSequence() + ". We will not filter the Intact entries with the organism.");

                proteins = daoFactory.getProteinDao().getByCrc(CRC64);
            }
            else {
                proteins = daoFactory.getProteinDao().getByCrcAndTaxId(CRC64, context.getOrganism().getTaxId());
            }
        }

        if (proteins.isEmpty()){
            Status status = new Status(StatusLabel.FAILED, "No proteins in IntAct could match the CRC64 ("+CRC64+") of the sequence " + context.getSequence() + (taxId != null ? " with organism " + taxId : ""));
            report.setStatus(status);
        }
        else if (proteins.size() == 1){
            if (proteins.get(0) != null){
                report.setIntactid(proteins.get(0).getAc());

                Status status = new Status(StatusLabel.COMPLETED, "The Crc64 search on Intact successfully returned the IntAct entry " + report.getIntactid()  + (taxId != null ? " with organism " + taxId : ""));
                report.setStatus(status);
            }
        }
        else {
            for (ProteinImpl p : proteins){
                report.addIntactMatchingProtein(p.getAc());
            }
            Status status = new Status(StatusLabel.TO_BE_REVIEWED, "The Crc64 search on IntAct returned " + proteins.size() + " matching IntAct entries."  + (taxId != null ? " with organism " + taxId : ""));
            report.setStatus(status);

        }
        return null;
    }
}
