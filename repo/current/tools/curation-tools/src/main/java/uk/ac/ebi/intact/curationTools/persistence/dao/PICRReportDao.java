package uk.ac.ebi.intact.curationTools.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@Mockable
public interface PICRReportDao extends ActionReportDao<PICRReport>{

    public List<PICRReport> getPICRReportsByResultsId(long actionId);

    public List<PICRReport> getActionReportsWithPICRCrossReferencesByProteinAc(String protAc);

    public List<PICRReport> getActionReportsWithPICRCrossReferencesByResultsId(long id);
}
