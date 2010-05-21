package uk.ac.ebi.intact.curationTools.persistence.dao;

import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.results.PICRCrossReferences;

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

    List<ActionReport> getAllActionReportsWithPICRCrossReferences();

    List<PICRCrossReferences> getCrossReferencesByActionId(long actionId);
}
