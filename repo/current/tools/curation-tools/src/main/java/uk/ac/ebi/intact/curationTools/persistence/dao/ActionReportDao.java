package uk.ac.ebi.intact.curationTools.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.status.StatusLabel;

import java.io.Serializable;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>19-May-2010</pre>
 */
@Mockable
public interface ActionReportDao<T extends ActionReport> extends UpdateBaseDao<T>, Serializable{

    public ActionReport getByReportId(long id);

    public List<ActionReport> getByActionName(ActionName name);

    public List<ActionReport> getByReportStatus(StatusLabel status);

    public List<ActionReport> getAllReportsWithWarnings();

    public List<ActionReport> getAllReportsWithSeveralPossibleUniprot();

    public List<PICRReport> getAllPICRReports();

    public List<BlastReport> getAllBlastReports();

    public List<BlastReport> getAllSwissprotRemappingReports();

    public List<ActionReport> getReportsWithWarningsByResultsId(long id);

    public List<ActionReport> getAllReportsByResultsId(long id);

    public List<ActionReport> getReportsWithSeveralPossibleUniprotByResultId(long id);

}
