package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionReport;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.model.results.PICRCrossReferences;
import uk.ac.ebi.intact.curationTools.persistence.dao.PICRReportDao;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21-May-2010</pre>
 */
@Repository
@Transactional(readOnly = true)
public class PICRReportDaoImpl extends ActionReportDaoImpl<PICRReport> implements PICRReportDao{

    public PICRReportDaoImpl() {
        super(PICRReport.class, null);
    }

    public PICRReportDaoImpl(EntityManager entityManager) {
        super(PICRReport.class, entityManager);
    }

    public List<ActionReport> getAllActionReportsWithPICRCrossReferences() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<PICRCrossReferences> getCrossReferencesByActionId(long actionId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
