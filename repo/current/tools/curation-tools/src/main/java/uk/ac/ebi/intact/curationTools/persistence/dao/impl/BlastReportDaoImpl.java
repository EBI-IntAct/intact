package uk.ac.ebi.intact.curationTools.persistence.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.curationTools.model.actionReport.ActionName;
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

    public List<BlastReport> getActionReportsWithBlastResultsByProteinAc(String protAc) {
        final Query query = getEntityManager().createQuery( "select a from BlastReport as a join a.updateResult as res where res.intactAccession = :protAc" );
        query.setParameter( "protAc", protAc);

        return query.getResultList();
    }

    public List<BlastReport> getActionReportsWithSwissprotRemappingResultsByProteinAc(String protAc) {
        final Query query = getEntityManager().createQuery( "select a from BlastReport as a join a.updateResult as res where res.intactAccession = :protAc and a.name = :name" );
        query.setParameter( "protAc", protAc);
        query.setParameter("name", ActionName.BLAST_Swissprot_Remapping);

        return query.getResultList();
    }

    public List<BlastReport> getActionReportsWithBlastResultsByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select a from BlastReport as a join a.updateResult as res where res.id = :id" );
        query.setParameter( "id", id);

        return query.getResultList();
    }

    public List<BlastReport> getActionReportsWithSwissprotRemappingResultsByResultsId(long id) {
        final Query query = getEntityManager().createQuery( "select a from BlastReport as a join a.updateResult as res where res.id = :id and a.name = :name" );
        query.setParameter( "id", id);
        query.setParameter("name", ActionName.BLAST_Swissprot_Remapping);

        return query.getResultList();
    }
}
