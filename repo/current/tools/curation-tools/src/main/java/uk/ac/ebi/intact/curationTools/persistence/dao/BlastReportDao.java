package uk.ac.ebi.intact.curationTools.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.results.BlastResults;

import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@Mockable
public interface BlastReportDao extends ActionReportDao<BlastReport>{

    List<ActionReport> getAllActionReportsWithBlastResults();

    List<ActionReport> getAllActionReportsWithSwissprotRemappingResults();

    public List<BlastResults> getBlastResultsByActionId(long id);

    public List<BlastResults> getBlastResultsByActionName(ActionName name);
}
