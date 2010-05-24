package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
import uk.ac.ebi.intact.curationTools.model.actionReport.PICRReport;
import uk.ac.ebi.intact.curationTools.persistence.dao.PICRReportDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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

    public List<PICRReport> getPICRReportsByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select ar from PICRReport as ar join ar.updateResult as res where res.id = :id" );
        query.setParameter( "id", id);

        return query.getResultList();
    }

    public List<PICRReport> getPICRReportsByActionName(ActionName name) {
        final Query query = getEntityManager().createQuery( "select ar from PICRReport as ar where ar.name = :name" );
        query.setParameter( "name", name);

        return query.getResultList();
    }
}
