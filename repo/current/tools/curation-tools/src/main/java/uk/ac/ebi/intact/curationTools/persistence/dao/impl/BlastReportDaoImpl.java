package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.model.results.BlastResults;
import uk.ac.ebi.intact.curationTools.persistence.dao.BlastReportDao;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20-May-2010</pre>
 */
@Repository
@Transactional(readOnly = true)
public class BlastReportDaoImpl extends ActionReportDaoImpl<BlastReport> implements BlastReportDao{

    public BlastReportDaoImpl() {
        super(BlastReport.class, null);
    }

    public BlastReportDaoImpl(EntityManager entityManager) {
        super(BlastReport.class, entityManager);
    }

    public List<ActionReport> getAllActionReportsWithBlastResults() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ActionReport> getAllActionReportsWithSwissprotRemappingResults() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<BlastResults> getBlastResultsByActionId(long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<BlastResults> getBlastResultsByActionName(ActionName name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
