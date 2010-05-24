package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.BlastReport;
import uk.ac.ebi.intact.curationTools.persistence.dao.BlastReportDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public List<BlastReport> getBlastReportsByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select ar from BlastReport as ar join ar.updateResult as res where res.id = :id" );
        query.setParameter( "id", id);

        return query.getResultList();
    }
}
